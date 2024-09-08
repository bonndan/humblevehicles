package dev.murad.shipping.entity.custom.vessel

import dev.murad.shipping.ShippingConfig
import dev.murad.shipping.entity.Colorable
import dev.murad.shipping.entity.custom.TrainInventoryProvider
import dev.murad.shipping.setup.ModItems
import dev.murad.shipping.util.LinkableEntity
import dev.murad.shipping.util.LinkingHandler
import dev.murad.shipping.util.SpringPhysicsUtil
import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.FluidTags
import net.minecraft.tags.TagKey
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MoverType
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.animal.WaterAnimal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.Boat
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.WaterlilyBlock
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.Shapes
import net.neoforged.neoforge.common.NeoForgeMod
import java.util.*
import java.util.stream.Stream
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

abstract class VesselEntity protected constructor(type: EntityType<out WaterAnimal>, world: Level) :
    WaterAnimal(type, world), LinkableEntity<VesselEntity>, Colorable {

    var isFrozen: Boolean = false

    private val linkingHandler: LinkingHandler<VesselEntity> = LinkingHandler(
        this,
        VesselEntity::class.java,
        DOMINANT_ID,
        DOMINATED_ID
    )

    protected fun getLinkingHandler(): LinkingHandler<VesselEntity> {
        return linkingHandler
    }

    private var stuckCounter = 0
    private var waterLevel = 0.0
    private var landFriction = 0f
    private var status: Boat.Status? = null
    private var oldStatus: Boat.Status? = null
    private var lastYd = 0.0

    init {
        resetAttributes(ShippingConfig.Server.TUG_BASE_SPEED!!.get())
    }

    override fun isPickable(): Boolean {
        return true
    }

    override fun hasWaterOnSides(): Boolean {
        return level().getFluidState(this.onPos.relative(this.direction.clockWise)).`is`(Fluids.WATER) &&
                level().getFluidState(this.onPos.relative(this.direction.counterClockWise)).`is`(Fluids.WATER) &&
                level().getBlockState(this.onPos.above().relative(this.direction.clockWise)).block == Blocks.AIR &&
                level().getBlockState(this.onPos.above().relative(this.direction.counterClockWise)).block == Blocks.AIR
    }

    override fun getBlockPos(): BlockPos {
        return onPos
    }

    override fun tick() {
        super.tick()

        linkingHandler.tickLoad()
        if (!level().isClientSide) {
            doChainMath()
        }
        if (this.isAlive) {
            if (this.tickCount % 10 == 0) {
                this.heal(1f)
            }
        }

        if (!level().isClientSide) {
            this.oldStatus = this.status
            this.status = this.getStatus()

            this.floatBoat()
            this.unDrown()
        }
    }

    private fun unDrown() {
        if (level().getBlockState(onPos.above()).block == Blocks.WATER) {
            this.deltaMovement = deltaMovement.add(Vec3(0.0, 0.1, 0.0))
        }
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        linkingHandler.readAdditionalSaveData(compound)
        if (compound.contains("Color", Tag.TAG_INT.toInt())) {
            color = compound.getInt("Color")
        }
        super.readAdditionalSaveData(compound)
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        linkingHandler.addAdditionalSaveData(compound)

        val color = color
        if (color != null) {
            compound.putInt("Color", color)
        }

        super.addAdditionalSaveData(compound)
    }

    override fun defineSynchedData(pBuilder: SynchedEntityData.Builder) {
        super.defineSynchedData(pBuilder)
        getEntityData().set(COLOR_DATA, -1)
        LinkingHandler.defineSynchedData(this, DOMINANT_ID, DOMINATED_ID)
    }

    override fun onSyncedDataUpdated(key: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(key)
        linkingHandler.onSyncedDataUpdated(key)
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

    // reset speed to 1
    private fun resetAttributes(newSpeed: Double) {
        getAttribute(Attributes.MOVEMENT_SPEED)!!.baseValue =
            0.0

        val swimSpeed = NeoForgeMod.SWIM_SPEED.delegate
        getAttribute(swimSpeed)!!.baseValue = 0.0

        getAttribute(Attributes.MOVEMENT_SPEED)
            ?.addTransientModifier(
                AttributeModifier(
                    ResourceLocation.parse("movementspeed_mult"),
                    newSpeed,
                    AttributeModifier.Operation.ADD_VALUE
                )
            )
        getAttribute(swimSpeed)
            ?.addTransientModifier(
                AttributeModifier(
                    ResourceLocation.parse("swimspeed_mult"),
                    newSpeed,
                    AttributeModifier.Operation.ADD_VALUE
                )
            )

        isCustomNameVisible = true
        getAttribute(NeoForgeMod.NAMETAG_DISTANCE.delegate)!!.baseValue =
            NAMETAG_RENDERING_DISTANCE
    }

    override fun handleAirSupply(airSupply: Int) {
        this.airSupply = 300
    }

    abstract val dropItem: Item?


    override fun getFollower(): Optional<VesselEntity> {
        return linkingHandler.follower
    }

    override fun getLeader(): Optional<VesselEntity> {
        return linkingHandler.leader
    }


    override fun checkDespawn() {
    }

    override fun linkEntities(player: Player, entity: Entity): Boolean {
        if (entity !is VesselEntity) {
            player.displayClientMessage(Component.translatable("item.littlelogistics.spring.badTypes"), true)
            return false
        }
        val firstTrain = this.getTrain()
        val secondTrain = entity.getTrain()

        if (firstTrain == null || secondTrain == null) {
            return false;
        }

        if (this.distanceTo(entity) > 15) {
            player.displayClientMessage(Component.translatable("item.littlelogistics.spring.tooFar"), true)
        } else if (firstTrain.tug.isPresent && secondTrain.tug.isPresent) {
            player.displayClientMessage(Component.translatable("item.littlelogistics.spring.noTwoTugs"), true)
        } else if (secondTrain == firstTrain) {
            player.displayClientMessage(Component.translatable("item.littlelogistics.spring.noLoops"), true)
        } else if (firstTrain.tug.isPresent) {
            val tail = firstTrain.tail
            val head = secondTrain.head
            tail.setDominated(head)
            head.setDominant(tail)
            return true
        } else {
            val tail = secondTrain.tail
            val head = firstTrain.head
            tail.setDominated(head)
            head.setDominant(tail)
            return true
        }
        return false
    }

    fun doChainMath() {
        linkingHandler.leader.ifPresent { dominant: VesselEntity ->
            SpringPhysicsUtil.adjustSpringedEntities(dominant, this)
            checkInsideBlocks()
        }
    }

    override fun remove(r: RemovalReason) {
        handleLinkableKill()
        super.remove(r)
    }

    override fun handleShearsCut() {
        if (!level().isClientSide && linkingHandler.leader.isPresent) {
            spawnChain()
        }
        linkingHandler.leader.ifPresent { obj: VesselEntity -> obj.removeDominated() }
        removeDominant()
    }

    private fun spawnChain() {
        val stack = ItemStack(ModItems.SPRING.get())
        this.spawnAtLocation(stack)
    }

    override fun getPickResult(): ItemStack? {
        return ItemStack(dropItem)
    }

    private fun floatBoat() {
        val d0 = -0.04
        var d1 = if (this.isNoGravity) 0.0 else -0.04
        var d2 = 0.0
        // MOB STUFF
        var invFriction = 0.05f
        if (this.oldStatus == Boat.Status.IN_AIR && this.status != Boat.Status.IN_AIR && this.status != Boat.Status.ON_LAND) {
            this.waterLevel = this.getY(1.0)
            this.setPos(this.x, (this.waterLevelAbove - this.bbHeight).toDouble() + 0.101, this.z)
            this.deltaMovement = deltaMovement.multiply(1.0, 0.0, 1.0)
            this.lastYd = 0.0
            this.status = Boat.Status.IN_WATER
        } else {
            if (this.status == Boat.Status.IN_WATER) {
                d2 = (this.waterLevel - this.y) / this.bbHeight.toDouble()
                invFriction = 0.9f
            } else if (this.status == Boat.Status.UNDER_FLOWING_WATER) {
                d1 = -7.0E-4
                invFriction = 0.9f
            } else if (this.status == Boat.Status.UNDER_WATER) {
                d2 = 0.01
                invFriction = 0.45f
            } else if (this.status == Boat.Status.IN_AIR) {
                invFriction = 0.9f
            } else if (this.status == Boat.Status.ON_LAND) {
                invFriction = this.landFriction
                if (this.controllingPassenger is Player) {
                    this.landFriction /= 2.0f
                }
            }

            val vector3d = this.deltaMovement
            this.setDeltaMovement(
                vector3d.x * invFriction.toDouble(),
                vector3d.y + d1,
                vector3d.z * invFriction.toDouble()
            )
            if (d2 > 0.0) {
                val vector3d1 = this.deltaMovement
                this.setDeltaMovement(vector3d1.x, (vector3d1.y + d2 * 0.10153846016296973) * 0.75, vector3d1.z)
            }
        }
    }

    private fun getStatus(): Boat.Status {
        val `Boat$status` = this.isUnderwater
        if (`Boat$status` != null) {
            this.waterLevel = this.boundingBox.maxY
            return `Boat$status`
        } else if (this.checkInWater()) {
            return Boat.Status.IN_WATER
        } else {
            val f = this.groundFriction
            if (f > 0.0f) {
                this.landFriction = f
                return Boat.Status.ON_LAND
            } else {
                return Boat.Status.IN_AIR
            }
        }
    }

    val waterLevelAbove: Float
        get() {
            val aabb = this.boundingBox
            val i = Mth.floor(aabb.minX)
            val j = Mth.ceil(aabb.maxX)
            val k = Mth.floor(aabb.maxY)
            val l = Mth.ceil(aabb.maxY - this.lastYd)
            val i1 = Mth.floor(aabb.minZ)
            val j1 = Mth.ceil(aabb.maxZ)
            val `blockpos$mutableblockpos` = MutableBlockPos()

            label39@ for (k1 in k until l) {
                var f = 0.0f

                for (l1 in i until j) {
                    for (i2 in i1 until j1) {
                        `blockpos$mutableblockpos`[l1, k1] = i2
                        val fluidstate =
                            level().getFluidState(`blockpos$mutableblockpos`)
                        if (fluidstate.`is`(FluidTags.WATER)) {
                            f = max(
                                f.toDouble(),
                                fluidstate.getHeight(this.level(), `blockpos$mutableblockpos`).toDouble()
                            ).toFloat()
                        }

                        if (f >= 1.0f) {
                            continue@label39
                        }
                    }
                }

                if (f < 1.0f) {
                    return `blockpos$mutableblockpos`.y.toFloat() + f
                }
            }

            return (l + 1).toFloat()
        }

    val groundFriction: Float
        /**
         * Decides how much the boat should be gliding on the land (based on any slippery blocks)
         */
        get() {
            val aabb = this.boundingBox
            val aabb1 = AABB(aabb.minX, aabb.minY - 0.001, aabb.minZ, aabb.maxX, aabb.minY, aabb.maxZ)
            val i = Mth.floor(aabb1.minX) - 1
            val j = Mth.ceil(aabb1.maxX) + 1
            val k = Mth.floor(aabb1.minY) - 1
            val l = Mth.ceil(aabb1.maxY) + 1
            val i1 = Mth.floor(aabb1.minZ) - 1
            val j1 = Mth.ceil(aabb1.maxZ) + 1
            val voxelshape = Shapes.create(aabb1)
            var f = 0.0f
            var k1 = 0
            val `blockpos$mutableblockpos` = MutableBlockPos()

            for (l1 in i until j) {
                for (i2 in i1 until j1) {
                    val j2 = (if (l1 != i && l1 != j - 1) 0 else 1) + (if (i2 != i1 && i2 != j1 - 1) 0 else 1)
                    if (j2 != 2) {
                        for (k2 in k until l) {
                            if (j2 <= 0 || k2 != k && k2 != l - 1) {
                                `blockpos$mutableblockpos`[l1, k2] = i2
                                val blockstate =
                                    level().getBlockState(`blockpos$mutableblockpos`)
                                if (blockstate.block !is WaterlilyBlock && Shapes.joinIsNotEmpty(
                                        blockstate.getCollisionShape(
                                            this.level(),
                                            `blockpos$mutableblockpos`
                                        ).move(l1.toDouble(), k2.toDouble(), i2.toDouble()), voxelshape, BooleanOp.AND
                                    )
                                ) {
                                    f += blockstate.getFriction(
                                        this.level(),
                                        `blockpos$mutableblockpos`,
                                        this
                                    )
                                    ++k1
                                }
                            }
                        }
                    }
                }
            }

            return f / k1.toFloat()
        }


    private fun checkInWater(): Boolean {
        val aabb = this.boundingBox
        val i = Mth.floor(aabb.minX)
        val j = Mth.ceil(aabb.maxX)
        val k = Mth.floor(aabb.minY)
        val l = Mth.ceil(aabb.minY + 0.001)
        val i1 = Mth.floor(aabb.minZ)
        val j1 = Mth.ceil(aabb.maxZ)
        var flag = false
        this.waterLevel = -Double.MAX_VALUE
        val `blockpos$mutableblockpos` = MutableBlockPos()

        for (k1 in i until j) {
            for (l1 in k until l) {
                for (i2 in i1 until j1) {
                    `blockpos$mutableblockpos`[k1, l1] = i2
                    val fluidstate = level().getFluidState(`blockpos$mutableblockpos`)
                    if (fluidstate.`is`(FluidTags.WATER)) {
                        val f = l1.toFloat() + fluidstate.getHeight(this.level(), `blockpos$mutableblockpos`)
                        this.waterLevel = max(f.toDouble(), this.waterLevel)
                        flag = flag or (aabb.minY < f.toDouble())
                    }
                }
            }
        }

        return flag
    }

    private val isUnderwater: Boat.Status?
        /**
         * Decides whether the boat is currently underwater.
         */
        get() {
            val aabb = this.boundingBox
            val d0 = aabb.maxY + 0.001
            val i = Mth.floor(aabb.minX)
            val j = Mth.ceil(aabb.maxX)
            val k = Mth.floor(aabb.maxY)
            val l = Mth.ceil(d0)
            val i1 = Mth.floor(aabb.minZ)
            val j1 = Mth.ceil(aabb.maxZ)
            var flag = false
            val `blockpos$mutableblockpos` = MutableBlockPos()

            for (k1 in i until j) {
                for (l1 in k until l) {
                    for (i2 in i1 until j1) {
                        `blockpos$mutableblockpos`[k1, l1] = i2
                        val fluidstate =
                            level().getFluidState(`blockpos$mutableblockpos`)
                        if (fluidstate.`is`(FluidTags.WATER) && d0 < (`blockpos$mutableblockpos`.y
                                .toFloat() + fluidstate.getHeight(
                                this.level(),
                                `blockpos$mutableblockpos`
                            )).toDouble()
                        ) {
                            if (!fluidstate.isSource) {
                                return Boat.Status.UNDER_FLOWING_WATER
                            }

                            flag = true
                        }
                    }
                }
            }

            return if (flag) Boat.Status.UNDER_WATER else null
        }

    override fun jumpInLiquid(pFluidTag: TagKey<Fluid>) {
        if (getNavigation().canFloat()) {
            super.jumpInLiquid(pFluidTag)
        } else {
            this.deltaMovement = deltaMovement.add(0.0, 0.3, 0.0)
        }
    }

    override fun isInvulnerableTo(pSource: DamageSource): Boolean {
        if (ShippingConfig.Server.VESSEL_EXEMPT_DAMAGE_SOURCES!!.get().contains(pSource.msgId)) {
            return true
        }

        return pSource == level().damageSources().inWall() || super.isInvulnerableTo(pSource)
    }


    /* TODO Get rid of default armour/hands slots itemhandler from mobs
    @Nonnull
    @Override
    public <T> Lazy<T> getCapability(@Nonnull EntityCapability<T, Void> cap, @Nullable Direction side) {
        if (cap == Capabilities.ItemHandler.ENTITY) {
            return Lazy.empty();
        }

        return super.getCapability(cap, side);
    }

     */
    override fun hurt(damageSource: DamageSource, amount: Float): Boolean {
        if (this.isInvulnerableTo(damageSource)) {
            return false
        } else if (!level().isClientSide && !this.isRemoved && damageSource.entity is Player) {
            val i = Stream.of(linkingHandler.leader, linkingHandler.follower)
                .filter { obj: Optional<VesselEntity> -> obj.isPresent }.count().toInt()
            if (level().gameRules.getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                for (j in 0 until i) {
                    spawnChain()
                }
            }
            this.remove(RemovalReason.KILLED)
            return true
        } else {
            return super.hurt(damageSource, amount)
        }
    }

    // LivingEntity override, to avoid jumping out of water
    override fun travel(relative: Vec3) {
        if (this.isEffectiveAi || this.isControlledByLocalInstance) {
            val d0: Double
            val gravity = this.getAttribute(Attributes.GRAVITY)
            val flag = deltaMovement.y <= 0.0
            d0 = gravity!!.value

            val fluidstate = level().getFluidState(this.blockPosition())
            if (this.isInWater && this.isAffectedByFluids && !this.canStandOnFluid(fluidstate)) {
                val d8 = this.y
                var f5 = if (this.isSprinting) 0.9f else this.waterSlowDown
                var f6 = 0.02f
                var f7 = 0f
                if (f7 > 3.0f) {
                    f7 = 3.0f
                }

                if (!this.onGround()) {
                    f7 *= 0.5f
                }

                if (f7 > 0.0f) {
                    f5 += (0.54600006f - f5) * f7 / 3.0f
                    f6 += (this.speed - f6) * f7 / 3.0f
                }

                if (this.hasEffect(MobEffects.DOLPHINS_GRACE)) {
                    f5 = 0.96f
                }

                f6 *= swimSpeed().toFloat()
                this.moveRelative(f6, relative)
                this.move(MoverType.SELF, this.deltaMovement)
                var vector3d6 = this.deltaMovement
                if (this.horizontalCollision && this.onClimbable()) {
                    vector3d6 = Vec3(vector3d6.x, 0.2, vector3d6.z)
                }

                this.deltaMovement = vector3d6.multiply(f5.toDouble(), 0.8, f5.toDouble())
                val vector3d2 = this.getFluidFallingAdjustedMovement(d0, flag, this.deltaMovement)
                this.deltaMovement = vector3d2
                if (this.horizontalCollision) {
                    if (stuckCounter > 10) {
                        // destroy lilypads
                        val direction = direction
                        val front = onPos.relative(direction).above()
                        val left = front.relative(direction.clockWise)
                        val right = front.relative(direction.counterClockWise)
                        for (pos in Arrays.asList(front, left, right)) {
                            val state = level().getBlockState(pos)
                            if (state.`is`(Blocks.LILY_PAD)) {
                                level().destroyBlock(pos, true)
                            }
                        }
                        stuckCounter = 0
                    } else {
                        stuckCounter++
                    }
                } else {
//                    stuckCounter = 0;
                }
            } else if (this.isInLava && this.isAffectedByFluids && !this.canStandOnFluid(fluidstate)) {
                val d7 = this.y
                this.moveRelative(0.02f, relative)
                this.move(MoverType.SELF, this.deltaMovement)
                if (this.getFluidHeight(FluidTags.LAVA) <= this.fluidJumpThreshold) {
                    this.deltaMovement = deltaMovement.multiply(0.5, 0.8, 0.5)
                    val vector3d3 = this.getFluidFallingAdjustedMovement(d0, flag, this.deltaMovement)
                    this.deltaMovement = vector3d3
                } else {
                    this.deltaMovement = deltaMovement.scale(0.5)
                }

                if (!this.isNoGravity) {
                    this.deltaMovement = deltaMovement.add(0.0, -d0 / 4.0, 0.0)
                }

                val vector3d4 = this.deltaMovement
                if (this.horizontalCollision && this.isFree(
                        vector3d4.x,
                        vector3d4.y + 0.6 - this.y + d7,
                        vector3d4.z
                    )
                ) {
                    this.setDeltaMovement(vector3d4.x, 0.3, vector3d4.z)
                }
            } else if (this.isFallFlying) {
                var vector3d = this.deltaMovement
                if (vector3d.y > -0.5) {
                    this.fallDistance = 1.0f
                }

                val vector3d1 = this.lookAngle
                val f = this.xRot * (Math.PI.toFloat() / 180f)
                val d1 = sqrt(vector3d1.x * vector3d1.x + vector3d1.z * vector3d1.z)
                val d3 = deltaMovement.horizontalDistance()
                val d4 = vector3d1.length()
                var f1 = Mth.cos(f)
                f1 = (f1.toDouble() * f1.toDouble() * min(1.0, d4 / 0.4)).toFloat()
                vector3d = deltaMovement.add(0.0, d0 * (-1.0 + f1.toDouble() * 0.75), 0.0)
                if (vector3d.y < 0.0 && d1 > 0.0) {
                    val d5 = vector3d.y * -0.1 * f1.toDouble()
                    vector3d = vector3d.add(vector3d1.x * d5 / d1, d5, vector3d1.z * d5 / d1)
                }

                if (f < 0.0f && d1 > 0.0) {
                    val d9 = d3 * (-Mth.sin(f)).toDouble() * 0.04
                    vector3d = vector3d.add(-vector3d1.x * d9 / d1, d9 * 3.2, -vector3d1.z * d9 / d1)
                }

                if (d1 > 0.0) {
                    vector3d = vector3d.add(
                        (vector3d1.x / d1 * d3 - vector3d.x) * 0.1,
                        0.0,
                        (vector3d1.z / d1 * d3 - vector3d.z) * 0.1
                    )
                }

                this.deltaMovement = vector3d.multiply(0.99, 0.98, 0.99)
                this.move(MoverType.SELF, this.deltaMovement)
                if (this.horizontalCollision && !level().isClientSide) {
                    val d10 = deltaMovement.horizontalDistance()
                    val d6 = d3 - d10
                    val f2 = (d6 * 10.0 - 3.0).toFloat()
                    if (f2 > 0.0f) {
                        this.hurt(level().damageSources().flyIntoWall(), f2)
                    }
                }

                if (this.onGround() && !level().isClientSide) {
                    this.setSharedFlag(7, false)
                }
            } else {
                val blockpos = this.blockPosBelowThatAffectsMyMovement
                val f3 = level().getBlockState(this.blockPosBelowThatAffectsMyMovement).getFriction(
                    level(),
                    this.blockPosBelowThatAffectsMyMovement,
                    this
                )
                val f4 = if (this.onGround()) f3 * 0.91f else 0.91f
                val vector3d5 = this.handleRelativeFrictionAndCalculateMovement(relative, f3)
                var d2 = vector3d5.y
                if (this.hasEffect(MobEffects.LEVITATION)) {
                    d2 += (0.05 * (getEffect(MobEffects.LEVITATION)!!.amplifier + 1).toDouble() - vector3d5.y) * 0.2
                    this.fallDistance = 0.0f
                } else if (level().isClientSide && !level().hasChunkAt(blockpos)) {
                    d2 = if (this.y > 0.0) {
                        -0.1
                    } else {
                        0.0
                    }
                } else if (!this.isNoGravity) {
                    d2 -= d0
                }

                this.setDeltaMovement(vector3d5.x * f4.toDouble(), d2 * 0.98, vector3d5.z * f4.toDouble())
            }
        }

        this.calculateEntityAnimation(false)
    }

    protected open fun swimSpeed(): Double {
        return getAttribute(NeoForgeMod.SWIM_SPEED.delegate)!!.value
    }

    val connectedInventories: List<TrainInventoryProvider>
        /**
         * Grabs a list of connected vessels that provides inventory to this vessel
         * For Example:
         * Tug F F F C C C -- All F barges are linked to all C barges
         * Tug F C F C F C -- Each F barge is linked to 1 C barge
         */
        get() {
            val result: MutableList<TrainInventoryProvider> = ArrayList()

            var vessel: Optional<VesselEntity> = getFollower()
            while (vessel.isPresent) {
                // TODO generalize this to all inventory providers
                if (vessel.get() is TrainInventoryProvider) {
                    break
                }

                vessel = vessel.get().getFollower()
            }

            // vessel is either empty or is a chest barge
            while (vessel.isPresent) {
                if (vessel.get() is TrainInventoryProvider) {
                    result.add(vessel as TrainInventoryProvider)
                } else {
                    break
                }

                vessel = vessel.get().getFollower()
            }

            return result
        }


    companion object {
        @JvmField
        val COLOR_DATA: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            VesselEntity::class.java, EntityDataSerializers.INT
        )

        private const val NAMETAG_RENDERING_DISTANCE = 15.0

        val DOMINANT_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            VesselEntity::class.java, EntityDataSerializers.INT
        )
        val DOMINATED_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            VesselEntity::class.java, EntityDataSerializers.INT
        )

        @JvmStatic
        fun setCustomAttributes(): AttributeSupplier.Builder {
            return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
            //TODO .add(ForgeMod.NAMETAG_DISTANCE, NAMETAG_RENDERING_DISTANCE)
            //TODO.add(ForgeMod.SWIM_SPEED.get(), 0.0D)
        }
    }
}
