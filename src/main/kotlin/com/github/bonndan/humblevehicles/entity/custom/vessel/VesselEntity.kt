package com.github.bonndan.humblevehicles.entity.custom.vessel

import com.github.bonndan.humblevehicles.ShippingConfig
import com.github.bonndan.humblevehicles.entity.Colorable
import com.github.bonndan.humblevehicles.entity.custom.StatusDetector
import com.github.bonndan.humblevehicles.entity.custom.StatusDetector.hasWaterOnSides
import com.github.bonndan.humblevehicles.setup.ModItems
import com.github.bonndan.humblevehicles.util.LinkableEntity
import com.github.bonndan.humblevehicles.util.LinkingHandler
import com.github.bonndan.humblevehicles.util.SpringPhysicsUtil
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
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
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.common.NeoForgeMod
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import java.util.stream.Stream

abstract class VesselEntity(type: EntityType<out WaterAnimal>, world: Level) :
    WaterAnimal(type, world), LinkableEntity<VesselEntity>, Colorable {

    private val linkingHandler: LinkingHandler<VesselEntity> = LinkingHandler(
        this,
        VesselEntity::class.java,
        DOMINANT_ID,
        DOMINATED_ID
    )

    /**
     * The linking handler can be null if used in constructors.
     * IMPORTANT: leave the getter here before init right under the property!
     */
    protected fun getLinkingHandler(): LinkingHandler<VesselEntity> {
        return linkingHandler
    }

    var isFrozen: Boolean = false
    private var oldStatus: Boat.Status? = null
    private val stuckCounter = AtomicReference<Int>(0)
    private var waterLevel = 0.0
    private var groundFriction = 0f

    protected var status: Boat.Status? = null
    protected var deltaRotation = 0f
    protected var movementBehaviour: VesselMovementBehaviour = BoatMovementBehaviour

    init {
        resetAttributes(ShippingConfig.Server.TUG_BASE_SPEED!!.get())
    }

    override fun isPickable(): Boolean {
        return true
    }

    override fun hasWaterOnSides(): Boolean {
        return hasWaterOnSides(level(), onPos, direction)
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

        if (this.isAlive && this.tickCount % 10 == 0) {
            this.heal(1f)
        }

        this.oldStatus = this.status
        this.status = this.updateStatus()
        this.floatBoat()

        if (!level().isClientSide) {
            val undrownForce = movementBehaviour.calculateUndrownForce(level(), status, onPos)
            if (undrownForce != 0.0) {
                this.deltaMovement = deltaMovement.add(Vec3(0.0, undrownForce, 0.0))
            }
        }
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        linkingHandler.readAdditionalSaveData(compound)
        if (compound.contains("Color", Tag.TAG_INT.toInt())) {
            setColor(compound.getInt("Color"))
        }
        super.readAdditionalSaveData(compound)
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        linkingHandler.addAdditionalSaveData(compound)

        val color = getColor()
        if (color != null) {
            compound.putInt("Color", color)
        }

        super.addAdditionalSaveData(compound)
    }

    override fun defineSynchedData(pBuilder: SynchedEntityData.Builder) {
        super.defineSynchedData(pBuilder)
        pBuilder.define(COLOR_DATA, -1)
        pBuilder.define(DOMINANT_ID, -1)
        pBuilder.define(DOMINATED_ID, -1)
    }

    override fun onSyncedDataUpdated(key: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(key)
        getLinkingHandler()?.onSyncedDataUpdated(key) //keep the "?"
    }

    override fun getColor(): Int? {
        val color = getEntityData()[COLOR_DATA]
        return if (color == -1) null else color
    }

    override fun setColor(color: Int?) {
        var color = color
        if (color == null) {
            color = -1
        }
        getEntityData().set(COLOR_DATA, color)
    }

    // reset speed to 1
    private fun resetAttributes(newSpeed: Double) {

        getAttribute(Attributes.MOVEMENT_SPEED)!!.baseValue = 0.0

        val swimSpeed = NeoForgeMod.SWIM_SPEED.delegate
        getAttribute(swimSpeed)!!.baseValue = 0.0

        getAttribute(Attributes.MOVEMENT_SPEED)?.addTransientModifier(
            AttributeModifier(
                ResourceLocation.parse("movementspeed_mult"),
                newSpeed,
                AttributeModifier.Operation.ADD_VALUE
            )
        )
        getAttribute(swimSpeed)?.addTransientModifier(
            AttributeModifier(
                ResourceLocation.parse("swimspeed_mult"),
                newSpeed,
                AttributeModifier.Operation.ADD_VALUE
            )
        )

        getAttribute(NeoForgeMod.NAMETAG_DISTANCE.delegate)!!.baseValue = NAMETAG_RENDERING_DISTANCE
    }

    override fun handleAirSupply(airSupply: Int) {
        this.airSupply = 300
    }

    abstract fun getDropItem(): Item?


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
            player.displayClientMessage(Component.translatable("item.humblevehicles.spring.badTypes"), true)
            return false
        }
        val firstTrain = this.getTrain()
        val secondTrain = entity.getTrain()

        if (this.distanceTo(entity) > 15) {
            player.displayClientMessage(Component.translatable("item.humblevehicles.spring.tooFar"), true)
        } else if (firstTrain.tug.isPresent && secondTrain.tug.isPresent) {
            player.displayClientMessage(Component.translatable("item.humblevehicles.spring.noTwoTugs"), true)
        } else if (secondTrain == firstTrain) {
            player.displayClientMessage(Component.translatable("item.humblevehicles.spring.noLoops"), true)
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
        return ItemStack(getDropItem())
    }

    private fun floatBoat() {

        // MOB STUFF
        if (wasInAir()) {
            this.waterLevel = this.getY(1.0)
            this.setPos(
                this.x,
                (StatusDetector.calculateWaterLevelAbove(level(), this.boundingBox) - this.bbHeight).toDouble() + 0.101,
                this.z
            )
            this.deltaMovement = deltaMovement.multiply(1.0, 0.0, 1.0)
            this.status = Boat.Status.IN_WATER
            return
        }

        val friction =
            if (this.status == Boat.Status.ON_LAND) groundFriction
            else movementBehaviour.calculateFriction(status)

        val vector3d = this.deltaMovement
        val downForce = movementBehaviour.calculateDownForce(this.isNoGravity, this.status)
        this.setDeltaMovement(
            vector3d.x * friction.toDouble(),
            vector3d.y + downForce,
            vector3d.z * friction.toDouble()
        )
        //this.deltaRotation = this.deltaRotation * invFriction

        val upForce =
            movementBehaviour.calculateBuoyancy(this.status, this.waterLevel, this.y, this.bbHeight.toDouble())
        if (upForce > 0.0) {
            val deltaMoveCopy = this.deltaMovement
            this.setDeltaMovement(deltaMoveCopy.x, (deltaMoveCopy.y + upForce) * 0.75, deltaMoveCopy.z)
        }
    }

    private fun wasInAir() =
        this.oldStatus == Boat.Status.IN_AIR && this.status != Boat.Status.IN_AIR && this.status != Boat.Status.ON_LAND

    private fun updateStatus(): Boat.Status {

        val isUnderwaterStatus = StatusDetector.isUnderwater(boundingBox, level())
        if (isUnderwaterStatus != null) {
            this.waterLevel = this.boundingBox.maxY
            return isUnderwaterStatus
        }

        val checkInWater = StatusDetector.checkInWater(boundingBox, level())
        this.waterLevel = checkInWater.waterLevel
        if (checkInWater.flag) {
            return Boat.Status.IN_WATER
        }

        var groundFriction = movementBehaviour.calculateGroundFriction(this.boundingBox, level(), this)
        if (groundFriction > 0.0f) {
            if (this.controllingPassenger is Player) {
                groundFriction /= 2.0f
            }
            this.groundFriction = groundFriction
            return Boat.Status.ON_LAND
        } else {
            return Boat.Status.IN_AIR
        }
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

    override fun hurt(damageSource: DamageSource, amount: Float): Boolean {

        if (this.isInvulnerableTo(damageSource)) {
            return false
        }

        if (!level().isClientSide && !this.isRemoved && damageSource.entity is Player) {
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

        movementBehaviour.travel(
            relative = relative,
            gravityValue = this.getAttribute(Attributes.GRAVITY)!!.value,
            fluidState = level().getFluidState(blockPosition()),
            entity = this,
            level = level(),
            isAffectedByFluids = isAffectedByFluids,
            stuckCounter = stuckCounter,
            waterSlowDown = this.waterSlowDown
        )

        this.calculateEntityAnimation(false)
    }

    open fun swimSpeed(): Double {
        return getAttribute(NeoForgeMod.SWIM_SPEED.delegate)!!.value
    }

    fun setFlyingState(state: Boolean) {
        setSharedFlag(7, state)
    }

    override fun getFluidFallingAdjustedMovement(pGravity: Double, pIsFalling: Boolean, pDeltaMovement: Vec3): Vec3 {

        val fluid = level().getFluidState(blockPosition()).type
        val effectiveGravity = if (movementBehaviour.isFallingIn(fluid)) pGravity else 0.0
        return super.getFluidFallingAdjustedMovement(effectiveGravity, pIsFalling, pDeltaMovement)
    }

    companion object {

        val COLOR_DATA: EntityDataAccessor<Int> =
            SynchedEntityData.defineId(VesselEntity::class.java, EntityDataSerializers.INT)

        private const val NAMETAG_RENDERING_DISTANCE = 15.0

        val DOMINANT_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            VesselEntity::class.java, EntityDataSerializers.INT
        )
        val DOMINATED_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            VesselEntity::class.java, EntityDataSerializers.INT
        )

        fun setCustomAttributes(): AttributeSupplier.Builder {
            return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
            //TODO .add(ForgeMod.NAMETAG_DISTANCE, NAMETAG_RENDERING_DISTANCE)
            //TODO.add(ForgeMod.SWIM_SPEED.get(), 0.0D)
        }
    }
}
