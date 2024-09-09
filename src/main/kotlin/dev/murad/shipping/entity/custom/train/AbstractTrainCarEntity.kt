package dev.murad.shipping.entity.custom.train

import com.google.common.collect.Maps
import com.mojang.datafixers.util.Pair
import dev.murad.shipping.ShippingConfig
import dev.murad.shipping.capability.StallingCapability
import dev.murad.shipping.entity.Colorable
import dev.murad.shipping.entity.custom.train.locomotive.AbstractLocomotiveEntity
import dev.murad.shipping.setup.ModItems
import dev.murad.shipping.util.LinkableEntity
import dev.murad.shipping.util.LinkingHandler
import dev.murad.shipping.util.RailHelper
import dev.murad.shipping.util.Train
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.tags.BlockTags
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.AbstractMinecart
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseRailBlock
import net.minecraft.world.level.block.state.properties.RailShape
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.common.extensions.IAbstractMinecartExtension
import java.util.*
import java.util.function.Consumer
import java.util.stream.Stream
import kotlin.math.*

abstract class AbstractTrainCarEntity : AbstractMinecart, IAbstractMinecartExtension,
    LinkableEntity<AbstractTrainCarEntity>, Colorable {

    protected val linkingHandler: LinkingHandler<AbstractTrainCarEntity> = LinkingHandler(
        this, AbstractTrainCarEntity::class.java, DOMINANT_ID, DOMINATED_ID
    )

    protected val railHelper: RailHelper

    private var frozen: Boolean = false

    fun isFrozen(): Boolean = frozen

    constructor(entityType: EntityType<*>, level: Level) : super(entityType, level) {
        linkingHandler.train = Train(this)
        railHelper = RailHelper(this)
        resetAttributes()
    }

    constructor(entityType: EntityType<*>, level: Level, x: Double, y: Double, z: Double) : super(
        entityType,
        level,
        x,
        y,
        z
    ) {
        val pos = BlockPos.containing(x, y, z)
        val state = level().getBlockState(pos)
        if (state.block is BaseRailBlock) {
            val railshape: RailShape = (state.block as BaseRailBlock).getRailDirection(state, this.level(), pos, this)
            val exit = RailHelper.EXITS[railshape]!!.first
            this.yRot =
                RailHelper.directionFromVelocity(Vec3(exit.x.toDouble(), exit.y.toDouble(), exit.z.toDouble())).toYRot()
        }
        linkingHandler.train = Train(this)
        railHelper = RailHelper(this)
        resetAttributes()
    }

    private fun resetAttributes() {
        isCustomNameVisible = true
    }

    protected val railShape: Optional<RailShape>
        get() {
            for (pos in Arrays.asList(onPos.above(), onPos)) {
                val state = level().getBlockState(pos)
                if (state.block is BaseRailBlock) {
                    return Optional.of(railHelper.getShape(pos))
                }
            }
            return Optional.empty()
        }


    override fun canBeCollidedWith(): Boolean {
        // future me: we don't want to change this, because then you can't push the cart
        return super.canBeCollidedWith()
    }

    public override fun getDropItem(): Item {
        return pickResult.item
    }

    override fun getColor(): Int? {
        val color = getEntityData().get(COLOR_DATA)
        return if (color == -1) null else color
    }

    override fun setColor(color: Int?) {
        var color = color
        if (color == null) color = -1
        getEntityData().set(COLOR_DATA, color)
    }

    override fun interact(player: Player, hand: InteractionHand): InteractionResult {
        val ret = super.interact(player, hand)
        if (ret.consumesAction()) return ret

        val color = DyeColor.getColor(player.getItemInHand(hand))

        if (color != null) {
            if (!level().isClientSide) {
                getEntityData().set(COLOR_DATA, color.id)
            }
            // don't interact *and* use current item
            return InteractionResult.sidedSuccess(level().isClientSide)
        }

        return InteractionResult.PASS
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)

        if (compound.contains("Color", Tag.TAG_INT.toInt())) {
            setColor(compound.getInt("Color"))
        }

        linkingHandler.readAdditionalSaveData(compound)
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)

        val color = getColor()
        if (color != null) {
            compound.putInt("Color", color)
        }

        linkingHandler.addAdditionalSaveData(compound)
    }

    override fun defineSynchedData(pBuilder: SynchedEntityData.Builder) {
        super.defineSynchedData(pBuilder)
        getEntityData().set(DOMINANT_ID, -1)
        getEntityData().set(DOMINATED_ID, -1)
        getEntityData().set(COLOR_DATA, -1)
    }


    override fun onSyncedDataUpdated(key: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(key)
        if (linkingHandler != null) {
            linkingHandler.onSyncedDataUpdated(key)
        }
    }


    override fun tick() {
        linkingHandler.tickLoad()
        tickYRot()
        val yrot = this.yRot
        tickVanilla()
        this.yRot = yrot
        if (!level().isClientSide) {
            doChainMath()
        }
    }

    override fun getMaxCartSpeedOnRail(): Float {
        return (ShippingConfig.Server.TRAIN_MAX_SPEED!!.get() * 1f).toFloat()
    }

    protected fun enforceMaxVelocity(maxSpeed: Double) {
        var vel = this.deltaMovement
        val normal = vel.normalize()
        if (abs(vel.x) > maxSpeed) {
            this.setDeltaMovement(normal.x * maxSpeed, vel.y, vel.z)
            vel = this.deltaMovement
        }
        if (abs(vel.z) > maxSpeed) {
            this.setDeltaMovement(vel.x, vel.y, normal.z * maxSpeed)
        }
    }

    override fun push(pEntity: Entity) {
        if (!level().isClientSide) {
            // not perfect, doesn't work when a mob stand in the way without moving, but works well enough underwater to keep this
            if (pEntity is LivingEntity && pEntity.getVehicle() == null) {
                Optional.ofNullable(this.getCapability(StallingCapability.STALLING_CAPABILITY))
                    .ifPresent { obj: StallingCapability -> obj.stall() }
            }
            if (!pEntity.noPhysics && !this.noPhysics) {
                // fix carts with passengers falling behind
                if (!this.hasPassenger(pEntity) || this.getLeader().isPresent()) {
                    var d0 = pEntity.x - this.x
                    var d1 = pEntity.z - this.z
                    var d2 = d0 * d0 + d1 * d1
                    if (d2 >= 1.0E-4) {
                        d2 = sqrt(d2)
                        d0 /= d2
                        d1 /= d2
                        var d3 = 1.0 / d2
                        if (d3 > 1.0) {
                            d3 = 1.0
                        }

                        d0 *= d3
                        d1 *= d3
                        d0 *= 0.1
                        d1 *= 0.1
                        d0 *= 0.5
                        d1 *= 0.5
                        if (pEntity is AbstractMinecart) {
                            val d4 = pEntity.getX() - this.x
                            val d5 = pEntity.getZ() - this.z
                            val vec3 = (Vec3(d4, 0.0, d5)).normalize()
                            val vec31 = (Vec3(
                                Mth.cos(this.yRot * (Math.PI.toFloat() / 180f)).toDouble(), 0.0, Mth.sin(
                                    this.yRot * (Math.PI.toFloat() / 180f)
                                ).toDouble()
                            )).normalize()
                            val d6 = abs(vec3.dot(vec31))
                            if (d6 < 0.8) {
                                return
                            }

                            val vec32 = this.deltaMovement
                            val vec33 = pEntity.getDeltaMovement()
                            if (pEntity.isPoweredCart && !this.isPoweredCart) {
                                this.deltaMovement = vec32.multiply(0.2, 1.0, 0.2)
                                this.push(vec33.x - d0, 0.0, vec33.z - d1)
                                pEntity.setDeltaMovement(vec33.multiply(0.95, 1.0, 0.95))
                            } else if (!pEntity.isPoweredCart && this.isPoweredCart) {
                                pEntity.setDeltaMovement(vec33.multiply(0.2, 1.0, 0.2))
                                pEntity.push(vec32.x + d0, 0.0, vec32.z + d1)
                                this.deltaMovement = vec32.multiply(0.95, 1.0, 0.95)
                            } else {
                                val d7 = (vec33.x + vec32.x) / 2.0
                                val d8 = (vec33.z + vec32.z) / 2.0
                                this.deltaMovement = vec32.multiply(0.2, 1.0, 0.2)
                                this.push(d7 - d0, 0.0, d8 - d1)
                                pEntity.setDeltaMovement(vec33.multiply(0.2, 1.0, 0.2))
                                pEntity.push(d7 + d0, 0.0, d8 + d1)
                            }
                        } else {
                            this.push(-d0, 0.0, -d1)
                            pEntity.push(d0 / 4.0, 0.0, d1 / 4.0)
                        }
                    }
                }
            }
        }
    }

    // avoid inheriting mixins
    override fun getOnPos(): BlockPos {
        val position = position()
        val i = Mth.floor(position.x)
        val j = Mth.floor(position.y - 0.2)
        val k = Mth.floor(position.z)
        val blockpos = BlockPos(i, j, k)
        if (level().isEmptyBlock(blockpos)) {
            val blockpos1 = blockpos.below()
            val blockstate = level().getBlockState(blockpos1)
            if (blockstate.collisionExtendsVertically(this.level(), blockpos1, this)) {
                return blockpos1
            }
        }

        return blockpos
    }

    protected fun tickYRot() {
        this.yRot = computeYaw()
    }

    fun computeYaw(): Float {
        val yrot = this.yRot
        // if the car is part of a train, enforce that direction instead
        val railShape = railShape
        if (linkingHandler.follower.isPresent && railShape.isPresent) {
            val r = railHelper.traverseBi(
                onPos.above(),
                RailHelper.samePositionPredicate(linkingHandler.follower.get()), 5, this
            )
            if (r.isPresent) {
                val yaw = yawHelper(r.get(), linkingHandler.follower.get())
                val directionOpt = RailHelper.getDirectionToOtherExit(yaw, railShape.get())
                if (directionOpt.isPresent) {
                    val direction = directionOpt.get()
                    return ((Mth.atan2(
                        direction.z.toDouble(),
                        direction.x.toDouble()
                    ) * 180.0 / Math.PI).toFloat() + 90)
                }
            }
        } else if (linkingHandler.leader.isPresent && railShape.isPresent) {
            val r = railHelper.traverseBi(
                onPos.above(),
                RailHelper.samePositionPredicate(linkingHandler.leader.get()), 5, this
            )
            if (r.isPresent) {
                val hordir = yawHelper(r.get(), linkingHandler.leader.get())
                val directionOpt = RailHelper.getDirectionToOtherExit(hordir, railShape.get())
                if (directionOpt.isPresent) {
                    val direction = directionOpt.get()
                    return ((Mth.atan2(
                        -direction.z.toDouble(),
                        -direction.x.toDouble()
                    ) * 180.0 / Math.PI).toFloat() + 90)
                }
            }
        } else {
            val d1 = this.xo - this.x
            val d3 = this.zo - this.z
            if (d1 * d1 + d3 * d3 > 0.001) {
                return ((Mth.atan2(d3, d1) * 180.0 / Math.PI).toFloat() + 90)
            }
        }

        return yrot
    }

    private fun yawHelper(directionIntPair: Pair<Direction, Int>, e: Entity): Direction {
        var hordir: Direction? = null
        if (directionIntPair.second == 0) {
            val dirvec = Vec3(e.xo - this.xo, 0.0, e.zo - this.zo)
            hordir = Direction.fromDelta(dirvec.normalize().x.toInt(), 0, dirvec.normalize().z.toInt()) // may fail
        }
        // if still null
        if (hordir == null) {
            return directionIntPair.first
        }
        return hordir
    }


    override fun isInvulnerableTo(pSource: DamageSource): Boolean {
        if (ShippingConfig.Server.TRAIN_EXEMPT_DAMAGE_SOURCES!!.get().contains(pSource.msgId)) {
            return true
        }
        return super.isInvulnerableTo(pSource)
    }

    /**
     * This method returns the specific position on the track at
     * pOffset blocks from the current position. This overridden
     * method takes into account of the minecart's yRot, which
     * the vanilla code does not (leading to lots of flipping)
     */
    override fun getPosOffs(pX: Double, pY: Double, pZ: Double, pOffset: Double): Vec3? {
        var pX = pX
        var pY = pY
        var pZ = pZ
        val i = Mth.floor(pX)
        var j = Mth.floor(pY)
        val k = Mth.floor(pZ)
        if (level().getBlockState(BlockPos(i, j - 1, k)).`is`(BlockTags.RAILS)) {
            --j
        }

        val blockstate = level().getBlockState(BlockPos(i, j, k))
        if (BaseRailBlock.isRail(blockstate)) {
            val railshape = (blockstate.block as BaseRailBlock).getRailDirection(
                blockstate,
                this.level(), BlockPos(i, j, k),
                this
            )
            pY = j.toDouble()
            if (railshape.isAscending) {
                pY = (j + 1).toDouble()
            }

            val pair = exits(railshape)
            var exit1 = pair.first
            var exit2 = pair.second

            // check if need to swap end points to make calculation correct
            val yawX = -sin(Math.toRadians(yRot.toDouble()))
            val yawZ = cos(Math.toRadians(yRot.toDouble()))
            if (Vec3(yawX, 0.0, yawZ).dot(
                    Vec3(
                        (exit2.x - exit1.x).toDouble(),
                        (exit2.y - exit1.y).toDouble(),
                        (exit2.z - exit1.z).toDouble()
                    )
                ) <= 0
            ) {
                val temp = exit1
                exit1 = exit2
                exit2 = temp
            }

            // get direction from e1 to e2
            var xDiff = (exit2.x - exit1.x).toDouble()
            var zDiff = (exit2.z - exit1.z).toDouble()
            // normalize x and z diff
            val dist = sqrt(xDiff * xDiff + zDiff * zDiff)
            xDiff /= dist
            zDiff /= dist
            pX += xDiff * pOffset
            pZ += zDiff * pOffset
            if (exit1.y != 0 && Mth.floor(pX) - i == exit1.x && Mth.floor(pZ) - k == exit1.z) {
                pY += exit1.y.toDouble()
            } else if (exit2.y != 0 && Mth.floor(pX) - i == exit2.x && Mth.floor(pZ) - k == exit2.z) {
                pY += exit2.y.toDouble()
            }

            return this.getPos(pX, pY, pZ)
        } else {
            return null
        }
    }

    // force render since we delegate rendering to the head of the train
    override fun shouldRender(pX: Double, pY: Double, pZ: Double): Boolean {
        return true
    }

    override fun getMotionDirection(): Direction {
        return Direction.fromYRot((this.yRot).toDouble())
    }

    override fun setYRot(pYRot: Float) {
        super.setYRot(pYRot)
    }

    protected fun tickVanilla() {
        super.tick()
    }

    override fun remove(r: RemovalReason) {
        handleLinkableKill()
        super.remove(r)
    }

    public override fun destroy(pSource: DamageSource) {
        val i = Stream.of(linkingHandler.leader, linkingHandler.follower)
            .filter { obj: Optional<AbstractTrainCarEntity> -> obj.isPresent }.count().toInt()
        this.remove(RemovalReason.KILLED)
        if (level().gameRules.getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            val stack = this.pickResult

            if (this.hasCustomName()) {
                //stack.setHoverName(this.getCustomName());
            }

            this.spawnAtLocation(stack)
            for (j in 0 until i) {
                spawnChain()
            }
        }
    }

    protected fun prevent180() {
        val dir =
            Vec3(this.direction.stepX.toDouble(), this.direction.stepY.toDouble(), this.direction.stepZ.toDouble())
        val vel = this.deltaMovement
        val mag = vel.multiply(dir)
        val fixer = Vec3(fixUtil(mag.x), 1.0, fixUtil(mag.z))
        this.deltaMovement = deltaMovement.multiply(fixer)
    }

    private fun fixUtil(mag: Double): Double {
        return if (mag < 0) 0.0 else 1.0
    }


    private fun doChainMath() {
        linkingHandler.leader.ifPresent { parent: AbstractTrainCarEntity ->
            val railDirDis =
                railHelper.traverseBi(this.onPos.above(), RailHelper.samePositionPredicate(parent), 5, this)
            // this is a fix to mitigate "bouncing" when trains start moving from a stopped position
            // todo: fix based on "docked" instead.
            val tug = linkingHandler.train?.tug
            val docked = tug?.isPresent == true && tug.get().deltaMovement == Vec3.ZERO
            val maxDist = if (docked) 1.0 else 1.2
            val minDist = 1.0

            val distance =
                railDirDis.map { obj: Pair<Direction, Int> -> obj.second }
                    .filter { a: Int -> a > 0 }.map { di: Int ->
                        val euclid = this.distanceTo(parent)
                        if (euclid < maxDist) di.toFloat() else euclid
                    }.orElse(this.distanceTo(parent))
            if (distance <= 6) {
                val euclideanDir = parent.position().subtract(position()).normalize()
                val parentDirection = railDirDis
                    .map { obj: Pair<Direction, Int> -> obj.first }
                    .map { obj: Direction -> obj.normal }
                    .map { pToCopy: Vec3i? -> Vec3.atLowerCornerOf(pToCopy) }
                    .orElse(euclideanDir)
                    .normalize()
                val parentVelocity = parent.deltaMovement

                if (distance > maxDist) {
                    if (parentVelocity.length() == 0.0) {
                        deltaMovement = parentDirection.scale(0.05)
                    } else {
                        deltaMovement = parentDirection.scale(parentVelocity.length())
                        if (distance > maxDist + 0.2) {
                            deltaMovement = deltaMovement.scale(distance * 0.8)
                        }
                    }
                } else if (parent.distanceTo(this) < minDist && parent.deltaMovement.length() < 0.01) {
                    this.moveTo(floor(x) + 0.5, y, floor(z) + 0.5)
                    deltaMovement = Vec3.ZERO
                } else {
                    deltaMovement = Vec3.ZERO
                }
            } else {
                linkingHandler.leader.ifPresent { obj: AbstractTrainCarEntity -> obj.removeDominated() }
                removeDominant()
            }
        }
    }

    override fun getMinecartType(): Type {
        // Why does this even exist
        return Type.CHEST
    }

    override fun getFollower(): Optional<AbstractTrainCarEntity> {
        return linkingHandler.follower
    }

    override fun getLeader(): Optional<AbstractTrainCarEntity> {
        return linkingHandler.leader
    }

    private fun spawnChain() {
        val stack = ItemStack(ModItems.SPRING.get())
        this.spawnAtLocation(stack)
    }

    override fun handleShearsCut() {
        if (!level().isClientSide && linkingHandler.leader.isPresent) {
            spawnChain()
        }
        linkingHandler.leader.ifPresent { obj: AbstractTrainCarEntity -> obj.removeDominated() }
        removeDominant()
    }

    override fun getBlockPos(): BlockPos {
        return this.onPos
    }

    override fun getTrain(): Train<AbstractTrainCarEntity> {
        return linkingHandler.train!!
    }

    override fun hasWaterOnSides(): Boolean {
        return false
    }

    private fun invertDoms() {
        val temp = linkingHandler.leader
        linkingHandler.leader = linkingHandler.follower
        linkingHandler.follower = temp
    }

    private fun distHelper(car1: AbstractTrainCarEntity, car2: AbstractTrainCarEntity): Optional<Int> {
        return railHelper.traverseBi(
            car1.onPos.above(),
            { l: Direction?, p: BlockPos ->
                RailHelper.getRail(car2.onPos.above(), car2.level())
                    .map { rp: BlockPos -> rp == p }.orElse(false)
            }, 5, car1
        ).map { obj -> obj.second }
    }

    private fun findClosestPair(
        train1: Train<AbstractTrainCarEntity>,
        train2: Train<AbstractTrainCarEntity>
    ): Optional<Pair<AbstractTrainCarEntity, AbstractTrainCarEntity>> {
        var mindistance = Int.MAX_VALUE
        var curr = Optional.empty<Pair<AbstractTrainCarEntity, AbstractTrainCarEntity>>()
        val pairs = Arrays.asList(
            Pair.of(train1.head, train2.tail),
            Pair.of(train1.tail, train2.head),
            Pair.of(train1.tail, train2.tail),
            Pair.of(train1.head, train2.head)
        )
        for (pair in pairs) {
            val d = distHelper(pair.first, pair.second)
            if (d.isPresent && d.get() < mindistance) {
                mindistance = d.get()
                curr = Optional.of(pair)
            }
        }

        return curr.filter { pair: Pair<AbstractTrainCarEntity, AbstractTrainCarEntity> ->
            (pair.first !is AbstractLocomotiveEntity || pair.first.getFollower().isEmpty())
                    && (pair.second !is AbstractLocomotiveEntity || pair.second.getFollower().isEmpty())
        }
    }

    private fun tryFindAndPrepareClosePair(
        train1: Train<AbstractTrainCarEntity>,
        train2: Train<AbstractTrainCarEntity>
    ): Optional<Pair<AbstractTrainCarEntity, AbstractTrainCarEntity>> {
        return findClosestPair(
            train1,
            train2
        ).flatMap { targetPair: Pair<AbstractTrainCarEntity, AbstractTrainCarEntity> ->
            if (targetPair.first == train1.head && targetPair.second == train2.head) {
                // if trying to attach to head loco then loco is solo
                if (train1.tug.isPresent) {
                    return@flatMap Optional.of<Pair<AbstractTrainCarEntity, AbstractTrainCarEntity>>(
                        targetPair
                    )
                } else {
                    invertTrain(train2)
                    return@flatMap Optional.of<Pair<AbstractTrainCarEntity, AbstractTrainCarEntity>>(
                        targetPair.swap()
                    )
                }
            } else if (targetPair.first == train1.head && targetPair.second == train2.tail) {
                return@flatMap Optional.of<Pair<AbstractTrainCarEntity, AbstractTrainCarEntity>>(
                    caseTailHead(train2, train1, targetPair.swap())
                )
            } else if (targetPair.first == train1.tail && targetPair.second == train2.head) {
                return@flatMap Optional.of<Pair<AbstractTrainCarEntity, AbstractTrainCarEntity>>(
                    caseTailHead(train1, train2, targetPair)
                )
            } else if (targetPair.first == train1.tail && targetPair.second == train2.tail) {
                if (train2.tug.isPresent) {
                    invertTrain(train1)
                    return@flatMap Optional.of<Pair<AbstractTrainCarEntity, AbstractTrainCarEntity>>(
                        targetPair.swap()
                    )
                } else {
                    invertTrain(train2)
                    return@flatMap Optional.of<Pair<AbstractTrainCarEntity, AbstractTrainCarEntity>>(
                        targetPair
                    )
                }
            }
            Optional.empty()
        }
    }

    override fun linkEntities(player: Player, target: Entity): Boolean {

        if (target !is AbstractTrainCarEntity) {
            player.displayClientMessage(Component.translatable("item.littlelogistics.spring.badTypes"), true)
            return false
        }

        val train1 = target.getTrain()
        val train2 = this.getTrain()

        if (train1 == null || train2 == null) {
            return false;
        }

        if (train2.tug.isPresent && train1.tug.isPresent) {
            player.displayClientMessage(Component.translatable("item.littlelogistics.spring.noTwoLoco"), true)
            return false
        } else if (train2 == train1) {
            player.displayClientMessage(Component.translatable("item.littlelogistics.spring.noLoops"), true)
            return false
        } else {
            tryFindAndPrepareClosePair(train1, train2).ifPresentOrElse(
                { pair -> createLinks(pair.first, pair.second) },
                {
                    player.displayClientMessage(Component.translatable("item.littlelogistics.spring.tooFar"), true)
                }
            )
        }

        return true
    }

    fun setFrozen(boolean: Boolean) {
        frozen = boolean
    }

    abstract override fun getPickResult(): ItemStack


    companion object {
        val COLOR_DATA: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            AbstractTrainCarEntity::class.java, EntityDataSerializers.INT
        )

        val DOMINANT_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            AbstractTrainCarEntity::class.java, EntityDataSerializers.INT
        )
        val DOMINATED_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            AbstractTrainCarEntity::class.java, EntityDataSerializers.INT
        )
        protected var TRAIN_SPEED: Double = ShippingConfig.Server.TRAIN_MAX_SPEED!!.get()
        private val EXITS: Map<RailShape?, Pair<Vec3i, Vec3i>> = Util.make(
            Maps.newEnumMap(
                RailShape::class.java
            )
        ) { enumMap: EnumMap<RailShape?, Pair<Vec3i, Vec3i>> ->
            val west = Direction.WEST.normal
            val east = Direction.EAST.normal
            val north = Direction.NORTH.normal
            val south = Direction.SOUTH.normal
            val westUnder = west.below()
            val eastUnder = east.below()
            val northUnder = north.below()
            val southUnder = south.below()
            enumMap[RailShape.NORTH_SOUTH] = Pair.of(north, south)
            enumMap[RailShape.EAST_WEST] = Pair.of(west, east)
            enumMap[RailShape.ASCENDING_EAST] = Pair.of(westUnder, east)
            enumMap[RailShape.ASCENDING_WEST] = Pair.of(west, eastUnder)
            enumMap[RailShape.ASCENDING_NORTH] = Pair.of(north, southUnder)
            enumMap[RailShape.ASCENDING_SOUTH] = Pair.of(northUnder, south)
            enumMap[RailShape.SOUTH_EAST] = Pair.of(south, east)
            enumMap[RailShape.SOUTH_WEST] = Pair.of(south, west)
            enumMap[RailShape.NORTH_WEST] = Pair.of(north, west)
            enumMap[RailShape.NORTH_EAST] = Pair.of(north, east)
        }

        private fun exits(pShape: RailShape): Pair<Vec3i, Vec3i> {
            return EXITS[pShape]!!
        }

        private fun caseTailHead(
            trainTail: Train<AbstractTrainCarEntity>,
            trainHead: Train<AbstractTrainCarEntity>,
            targetPair: Pair<AbstractTrainCarEntity, AbstractTrainCarEntity>
        ): Pair<AbstractTrainCarEntity, AbstractTrainCarEntity> {
            if (trainHead.tug.isPresent) {
                invertTrain(trainHead)
                invertTrain(trainTail)
                return targetPair.swap()
            } else {
                return targetPair
            }
        }

        private fun invertTrain(train: Train<AbstractTrainCarEntity>) {
            val head = train.head
            val tail = train.tail
            train.asList().forEach(Consumer { obj: AbstractTrainCarEntity -> obj.invertDoms() })
            train.head = tail
            train.tail = head
        }

        private fun createLinks(dominant: AbstractTrainCarEntity, dominated: AbstractTrainCarEntity) {
            dominated.setDominant(dominant)
            dominant.setDominated(dominated)
        }
    }
}
