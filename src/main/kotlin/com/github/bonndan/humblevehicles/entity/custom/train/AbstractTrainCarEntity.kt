package com.github.bonndan.humblevehicles.entity.custom.train

import com.github.bonndan.humblevehicles.capability.StallingCapability
import com.github.bonndan.humblevehicles.entity.Colorable
import com.github.bonndan.humblevehicles.entity.custom.train.locomotive.AbstractLocomotiveEntity
import com.github.bonndan.humblevehicles.setup.ModItems
import com.github.bonndan.humblevehicles.util.LinkableEntity
import com.github.bonndan.humblevehicles.util.LinkingHandler
import com.github.bonndan.humblevehicles.util.RailHelper
import com.github.bonndan.humblevehicles.util.Train
import com.mojang.datafixers.util.Pair
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.Minecart
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseRailBlock
import net.minecraft.world.level.block.state.properties.RailShape
import net.minecraft.world.phys.Vec3
import java.util.*
import java.util.function.Consumer
import java.util.stream.Stream
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sqrt

abstract class AbstractTrainCarEntity : Minecart, LinkableEntity<AbstractTrainCarEntity>, Colorable {

    protected val linkingHandler: LinkingHandler<AbstractTrainCarEntity> = LinkingHandler(
        this, AbstractTrainCarEntity::class.java, DOMINANT_ID, DOMINATED_ID
    )

    protected val railHelper: RailHelper

    private var frozen: Boolean = false

    fun isFrozen(): Boolean = frozen

    constructor(entityType: EntityType<*>, level: Level) : super(entityType, level) {
        linkingHandler.train = Train(this)
        railHelper = RailHelper(this)
    }

    val railShape: Optional<RailShape>
        get() {
            for (pos in mutableListOf(onPos.above(), onPos)) {
                val state = level().getBlockState(pos)
                if (state.block is BaseRailBlock) {
                    return Optional.of(railHelper.getShape(pos))
                }
            }
            return Optional.empty()
        }

    public override fun getDropItem(): Item {
        return pickResult.item
    }

    override fun getColorId(): Int? {
        val color = getEntityData().get(COLOR_DATA)
        return if (color == -1) null else color
    }

    override fun setColorId(color: Int?) {
        var color = color
        if (color == null) color = -1
        getEntityData()[COLOR_DATA] = color
    }

    override fun interact(player: Player, hand: InteractionHand): InteractionResult {
        val ret = super.interact(player, hand)
        if (ret.consumesAction()) return ret

        val color = DyeColor.getColor(player.getItemInHand(hand))

        if (color != null) {
            if (!level().isClientSide) {
                getEntityData()[COLOR_DATA] = color.id
            }
            // don't interact *and* use current item
            return InteractionResult.SUCCESS
        }

        return InteractionResult.PASS
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)

        if (compound.contains("Color", Tag.TAG_INT.toInt())) {
            setColorId(compound.getInt("Color"))
        }

        linkingHandler.readAdditionalSaveData(compound)
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)

        val color = getColorId()
        if (color != null) {
            compound.putInt("Color", color)
        }

        linkingHandler.addAdditionalSaveData(compound)
    }

    override fun defineSynchedData(pBuilder: SynchedEntityData.Builder) {
        super.defineSynchedData(pBuilder)
        pBuilder.define(DOMINANT_ID, -1)
        pBuilder.define(DOMINATED_ID, -1)
        pBuilder.define(COLOR_DATA, -1)
    }


    override fun onSyncedDataUpdated(key: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(key)
        linkingHandler.onSyncedDataUpdated(key)
        if (key == COLOR_DATA) {
            setColorId(entityData[COLOR_DATA])
        }
    }

    override fun tick() {
        linkingHandler.tickLoad()
        super.tick()
        if (!level().isClientSide) {
            doChainMath()
        }
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

    open fun isPoweredCart(): Boolean {
        return false
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
                        if (pEntity is AbstractTrainCarEntity) {
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
                            if (pEntity.isPoweredCart() && !this.isPoweredCart()) {
                                this.deltaMovement = vec32.multiply(0.2, 1.0, 0.2)
                                this.push(vec33.x - d0, 0.0, vec33.z - d1)
                                pEntity.setDeltaMovement(vec33.multiply(0.95, 1.0, 0.95))
                            } else if (!pEntity.isPoweredCart() && this.isPoweredCart()) {
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

    override fun remove(r: RemovalReason) {
        handleLinkableKill()
        super.remove(r)
    }

    public override fun destroy(level: ServerLevel, pSource: DamageSource) {
        val i = Stream.of(linkingHandler.leader, linkingHandler.follower)
            .filter { obj: Optional<AbstractTrainCarEntity> -> obj.isPresent }.count().toInt()
        this.remove(RemovalReason.KILLED)
        if (level.gameRules.getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            val stack = this.pickResult

            if (this.hasCustomName()) {
                stack[DataComponents.CUSTOM_NAME] = customName;
            }

            this.spawnAtLocation(level, stack)
            (0 until i).forEach { j ->
                spawnChain()
            }
        }
    }

    private fun doChainMath() {
        linkingHandler.leader.ifPresent { leader: AbstractTrainCarEntity ->
            val railDirDis =
                railHelper.traverseBi(this.onPos.above(), RailHelper.samePositionPredicate(leader), 5, this)
            // this is a fix to mitigate "bouncing" when trains start moving from a stopped position
            // todo: fix based on "docked" instead.
            val tug = linkingHandler.train?.tug
            val docked = tug?.isPresent == true && tug.get().deltaMovement == Vec3.ZERO
            val maxDist = if (docked) 1.2 else 1.4
            val minDist = 1.2

            val distance = railDirDis.map { obj: Pair<Direction, Int> -> obj.second }
                .filter { a: Int -> a > 0 }
                .map { di: Int ->
                    val euclid = this.distanceTo(leader)
                    if (euclid < maxDist) di.toFloat() else euclid
                }
                .orElse(this.distanceTo(leader))

            if (distance <= 6) {
                val euclideanDir = leader.position().subtract(position()).normalize()
                val parentDirection = railDirDis
                    .map { obj: Pair<Direction, Int> -> obj.first }
                    .map { obj: Direction -> obj.unitVec3i }
                    .map { pToCopy -> Vec3.atLowerCornerOf(pToCopy) }
                    .orElse(euclideanDir)
                    .normalize()
                val leaderVelocity = leader.deltaMovement

                if (distance > maxDist) {
                    if (leaderVelocity.length() == 0.0) {
                        deltaMovement = parentDirection.scale(0.05)
                    } else {
                        deltaMovement = parentDirection.scale(leaderVelocity.length())
                        if (distance > maxDist + 0.2) {
                            deltaMovement = deltaMovement.scale(distance * 0.8)
                        }
                    }
                } else if (leader.distanceTo(this) < minDist && leader.deltaMovement.length() < 0.01) {
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

    override fun getFollower(): Optional<AbstractTrainCarEntity> {
        return linkingHandler.follower
    }

    override fun getLeader(): Optional<AbstractTrainCarEntity> {
        return linkingHandler.leader
    }

    private fun spawnChain() {
        val stack = ItemStack(ModItems.SPRING.get())
        this.spawnAtLocation(level() as ServerLevel, stack)
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
            player.displayClientMessage(Component.translatable("item.humblevehicles.spring.badTypes"), true)
            return false
        }

        val train1 = target.getTrain()
        val train2 = this.getTrain()

        if (train2.tug.isPresent && train1.tug.isPresent) {
            player.displayClientMessage(Component.translatable("item.humblevehicles.spring.noTwoLoco"), true)
            return false
        } else if (train2 == train1) {
            player.displayClientMessage(Component.translatable("item.humblevehicles.spring.noLoops"), true)
            return false
        } else {
            tryFindAndPrepareClosePair(train1, train2).ifPresentOrElse(
                { pair -> createLinks(pair.first, pair.second) },
                {
                    player.displayClientMessage(Component.translatable("item.humblevehicles.spring.tooFar"), true)
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
