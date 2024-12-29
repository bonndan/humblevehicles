package com.github.bonndan.humblevehicles.entity.custom.train.locomotive

import com.github.bonndan.humblevehicles.ShippingConfig
import com.github.bonndan.humblevehicles.block.rail.MultiShapeRail
import com.github.bonndan.humblevehicles.block.rail.blockentity.LocomotiveDockTileEntity
import com.github.bonndan.humblevehicles.capability.StallingCapability
import com.github.bonndan.humblevehicles.entity.accessor.HeadVehicleDataAccessor
import com.github.bonndan.humblevehicles.entity.custom.HeadVehicle
import com.github.bonndan.humblevehicles.entity.custom.Stalling
import com.github.bonndan.humblevehicles.entity.custom.engine.Engine
import com.github.bonndan.humblevehicles.entity.custom.engine.SaveStateCallback
import com.github.bonndan.humblevehicles.entity.custom.train.AbstractTrainCarEntity
import com.github.bonndan.humblevehicles.item.LocoRouteItem
import com.github.bonndan.humblevehicles.setup.ModBlocks.LOCOMOTIVE_DOCK_RAIL
import com.github.bonndan.humblevehicles.setup.ModItems
import com.github.bonndan.humblevehicles.setup.ModSounds
import com.github.bonndan.humblevehicles.util.*
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.AbstractMinecart
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.PoweredRailBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.properties.RailShape
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.items.ItemStackHandler
import java.util.*
import java.util.function.Function
import java.util.function.Predicate
import kotlin.math.abs
import kotlin.math.floor


abstract class AbstractLocomotiveEntity : AbstractTrainCarEntity, LinkableEntityHead<AbstractTrainCarEntity>,
    ItemHandlerVanillaContainerWrapper, HeadVehicle, Stalling, WorldlyContainer {

    protected val enrollmentHandler: ChunkManagerEnrollmentHandler
    protected val saveStateCallback = object : SaveStateCallback {
        override fun saveState(engineState: Boolean, remainingBurnTime: Int) {
            entityData[ENGINE_IS_ON] = engineState
            entityData[REMAINING_BURN_TIME] = remainingBurnTime
        }
    }
    protected lateinit var engine: Engine

    private var independentMotion = false
    var isDocked: Boolean = false
        private set
    private var speedRecomputeCooldown = 0
    private var speedLimit = -1.0
    private var collisionCheckCooldown = 0
    private var remainingStallTime = 0
    private var forceStallCheck = false

    private var currentHorizontalBlockPos: BlockPos? = null
    var oldHorizontalBlockPos: BlockPos? = null
        private set

    private var routeItemHandler: ItemStackHandler = createLocoRouteItemHandler()

    protected var navigator: LocomotiveNavigator = LocomotiveNavigator(this)

    private var dockCheckCooldown = 0


    constructor(type: EntityType<*>, world: Level) : super(type, world) {
        enrollmentHandler = ChunkManagerEnrollmentHandler(this)
    }

    override fun enroll(uuid: UUID) {
        enrollmentHandler.enroll(uuid)
    }

    override fun allowDockInterface(): Boolean {
        return isDocked
    }

    override fun getRouteIcon(): ResourceLocation {
        return ModItems.LOCO_ROUTE_ICON
    }

    override fun remove(r: RemovalReason) {
        if (!level().isClientSide) {
            this.spawnAtLocation(level() as ServerLevel, routeItemHandler.getStackInSlot(0))
        }
        super.remove(r)
    }

    override fun interact(player: Player, hand: InteractionHand): InteractionResult {

        val ret = super.interact(player, hand)
        if (ret.consumesAction()) {
            return ret
        }

        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS
        }

        if (level().isClientSide) {
            return InteractionResult.CONSUME
        }

        // right click works for riding player, otherwise would dismount
        if (this.isVehicle) {
            return showInventoryMenu(player)
        }

        //shift: open menu
        if (player.isSecondaryUseActive) {
            return showInventoryMenu(player)
        }

        //default: start riding
        return if (player.startRiding(this)) InteractionResult.CONSUME else InteractionResult.PASS

    }

    private fun showInventoryMenu(pPlayer: Player): InteractionResult {
        pPlayer.openMenu(createContainerProvider(), getDataAccessor()::write)
        return InteractionResult.CONSUME
    }

    private fun createLocoRouteItemHandler(): ItemStackHandler {
        return object : ItemStackHandler() {
            override fun getStackLimit(slot: Int, stack: ItemStack): Int {
                return 1
            }

            override fun onContentsChanged(slot: Int) {
                updateNavigatorFromItem()
            }

            override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
                return stack.item is LocoRouteItem
            }
        }
    }

    protected abstract fun createContainerProvider(): MenuProvider

    fun getDataAccessor(): HeadVehicleDataAccessor =
        HeadVehicleDataAccessor.Builder()
            .withBurnProgressPct { engine.getBurnProgressPct() }
            .withId(this.id)
            .withOn { engine.isOn() }
            .withRouteSize { navigator.routeSize }
            .withVisitedSize { navigator.visitedSize }
            .withLit { engine.isLit() }
            .withCanMove { enrollmentHandler.mayMove() }
            .build()

    private fun tickFuel(): Boolean {
        return engine.tickFuel() > 0
    }

    override fun onSyncedDataUpdated(key: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(key)

        if (!level().isClientSide) return

        if (INDEPENDENT_MOTION == key) {
            independentMotion = entityData[INDEPENDENT_MOTION]
        }

        if (ENGINE_IS_ON == key) {
            setEngineOn(entityData[ENGINE_IS_ON])
        }

        if (REMAINING_BURN_TIME == key) {
            engine.setRemainingBurnTime(entityData[REMAINING_BURN_TIME])
        }
    }

    override fun owner(): String {
        return entityData[OWNER]
    }

    override fun hasOwner(): Boolean {
        return enrollmentHandler.hasOwner()
    }


    override fun tick() {
        linkingHandler.tickLoad()

        if (!level().isClientSide) {
            tickOldBlockPos()
            if (remainingStallTime <= 0) {
                navigator.serverTick()
            }
            enrollmentHandler.tick()
            enrollmentHandler.playerName.ifPresent { name -> entityData.set(OWNER, name) }
        }

        super.tick()
        //TODO check: this prevents loco getting stuck in slopes
        if (linkingHandler.follower.isEmpty && deltaMovement.length() > 0.05) {
            //this.yRot = RailHelper.directionFromVelocity(deltaMovement).toYRot()
        }
        if (!level().isClientSide) {
            tickDockCheck()
            tickMovement()
        }

        if (level().isClientSide && independentMotion) {
            doMovementEffect()
        }

    }

    private fun tickOldBlockPos() {
        if (oldHorizontalBlockPos == null || currentHorizontalBlockPos == null) {
            oldHorizontalBlockPos = getBlockPos()
            currentHorizontalBlockPos = getBlockPos()
        } else {
            if (currentHorizontalBlockPos!!.x != this.blockX ||
                currentHorizontalBlockPos!!.z != this.blockZ
            ) {
                oldHorizontalBlockPos = currentHorizontalBlockPos
                currentHorizontalBlockPos = getBlockPos()
            }
        }
    }

    protected open fun doMovementEffect() {
    }

    override fun defineSynchedData(pBuilder: SynchedEntityData.Builder) {
        super.defineSynchedData(pBuilder)
        pBuilder.define(INDEPENDENT_MOTION, false)
        pBuilder.define(OWNER, "")
        pBuilder.define(ENGINE_IS_ON, false)
        pBuilder.define(REMAINING_BURN_TIME, 0)
    }

    private fun tickMovement() {
        if (remainingStallTime > 0) {
            remainingStallTime--
            if (remainingStallTime == 0) forceStallCheck = true
        } else {
            if (collisionCheckCooldown <= 0 || forceStallCheck) {
                val result = railHelper.traverse(
                    onPos.above(), this.level(), this.direction,
                    { dir: Direction, pos: BlockPos -> checkCollision(pos) || checkStopSign(pos, dir) },
                    4
                )
                if (result.isPresent) {
                    remainingStallTime = 40
                }
                collisionCheckCooldown = 4
                forceStallCheck = false
            } else {
                collisionCheckCooldown--
            }
        }
        if (!isDocked && engine.isOn() && remainingStallTime <= 0 && !forceStallCheck && !shouldFreezeTrain() && tickFuel()) {
            tickSpeedLimit()
            entityData[INDEPENDENT_MOTION] = true
            accelerate()
        } else {
            if (RailHelper.getRail(this.onPos.above(), this.level())
                    .map { pos -> railHelper.getShape(pos) }
                    .map { obj -> obj.name }
                    .map { s -> s.contains("ASCENDING") }
                    .orElse(true) && tickFuel()
            ) {
                this.deltaMovement = Vec3.ZERO
                entityData[INDEPENDENT_MOTION] = true
                this.setPos(xOld, yOld, zOld)
            } else {
                entityData[INDEPENDENT_MOTION] = false
            }
        }

        if (shouldFreezeTrain()) {
            linkingHandler.train?.asList()
                ?.forEach { t: AbstractTrainCarEntity -> t.setDeltaMovement(0.0, 0.0, 0.0) }
        }
    }

    private fun checkStopSign(pos: BlockPos, prevExitTaken: Direction): Boolean {

        return RailHelper.getRail(pos, this.level())
            .flatMap { block: BlockPos ->

                if (level().getBlockState(block).block is MultiShapeRail) {

                    val r = level().getBlockState(block).block as MultiShapeRail
                    if (level().getEntitiesOfClass<Entity>(Entity::class.java, AABB(pos)) { e: Entity -> e == this }
                            .isNotEmpty()) {
                        return@flatMap Optional.empty<Boolean>()
                    }

                    return@flatMap r.getPriorityDirectionsToCheck(level().getBlockState(block), prevExitTaken.opposite)
                        .stream()
                        .map<Optional<Int>> { direction ->
                            railHelper.traverse(
                                pos.relative(direction),
                                this.level(),
                                direction,
                                { _, f: BlockPos -> checkLocoCollision(f) },
                                2
                            )
                        }
                        .map { obj -> obj.isPresent }
                        .reduce { a, b -> java.lang.Boolean.logicalOr(a, b) }
                } else return@flatMap Optional.of<Boolean>(false)
            }.orElse(false)
    }

    private fun checkCollision(pos: BlockPos): Boolean {
        val aabb = AABB(pos)
        return level().getEntitiesOfClass(Entity::class.java, aabb) { entity ->
            when (entity) {

                is AbstractTrainCarEntity -> {
                    entity.getTrain().tug
                        .map { tug -> tug.uuid != this.getUUID() }
                        .orElse(true)
                }

                is AbstractMinecart -> {
                    true
                }

                else -> false
            }
        }.isNotEmpty()
    }

    // to avoid deadlock for stopsign, you only care about incoming "heads"
    private fun checkLocoCollision(pos: BlockPos): Boolean {
        val aabb = AABB(pos)
        return level().getEntitiesOfClass(Entity::class.java, aabb)
        { e: Entity ->
            when (e) {
                //TODO check this kotlin conversion
                is AbstractLocomotiveEntity -> {
                    e.getTrain().tug.map { f -> f.uuid != this.getUUID() }
                        ?.orElse(true) ?: true
                }

                else -> false
            }
        }.isNotEmpty()
    }

    override fun isPoweredCart(): Boolean {
        return true
    }

    protected fun onDock() {
        this.playSound(ModSounds.DOCKING.get(), 0.6f, 1.0f)
    }

    protected open fun onUndock() {
        this.playSound(ModSounds.UNDOCKING.get(), 0.6f, 1.5f)
    }

    private fun tickDockCheck() {
        Optional.ofNullable(getCapability(StallingCapability.STALLING_CAPABILITY))
            .ifPresent { cap: StallingCapability ->
                val x = floor(this.x).toInt()
                val y = floor(this.y).toInt()
                val z = floor(this.z).toInt()

                val docked = cap.isDocked()

                if (docked && dockCheckCooldown > 0) {
                    dockCheckCooldown--
                    this.deltaMovement = Vec3.ZERO
                    this.moveTo(x + 0.5, getY(), z + 0.5)
                    return@ifPresent
                }

                val prepCord =
                    Function { d: Double -> abs(d - d.toInt()) }
                val aroundCentre =
                    Predicate { i: Double ->
                        prepCord.apply(i) < 0.8 && prepCord.apply(
                            i
                        ) > 0.2
                    }

                if (!aroundCentre.test(this.x) || !aroundCentre.test(this.z)) {
                    return@ifPresent
                }


                // Check docks
                val shouldDock = Optional.ofNullable(level().getBlockEntity(onPos.above()))
                    .filter { entity: BlockEntity? -> entity is LocomotiveDockTileEntity }
                    .map { entity: BlockEntity -> entity as LocomotiveDockTileEntity }
                    .map { dock: LocomotiveDockTileEntity ->
                        dock.hold(
                            this,
                            direction
                        )
                    }
                    .orElse(false)

                val changedDock = !docked && shouldDock
                val changedUndock = docked && !shouldDock

                if (shouldDock) {
                    dockCheckCooldown = 20 // todo: magic number
                    cap.dock(x + 0.5, getY(), z + 0.5)
                } else {
                    dockCheckCooldown = 0
                    cap.undock()
                }

                if (changedDock) onDock()
                if (changedUndock) onUndock()
            }
    }

    /**
     * adjust speed based on slope etc.
     */
    private val speedModifier: Double
        get() {
            val state = level().getBlockState(this.onPos.above())
            if (state.`is`(Blocks.POWERED_RAIL)) {
                return if (!state.getValue(PoweredRailBlock.POWERED)) {
                    0.0
                } else {
                    0.005
                }
            }
            return railShape.map { shape: RailShape? ->
                when (shape) {
                    RailShape.NORTH_SOUTH, RailShape.EAST_WEST -> 0.07
                    RailShape.SOUTH_WEST, RailShape.NORTH_WEST, RailShape.SOUTH_EAST, RailShape.NORTH_EAST -> 0.03
                    else -> 0.07
                }
            }.orElse(0.0)
        }

    private fun tickSpeedLimit() {
        if (speedRecomputeCooldown < 0 || speedLimit < 0) {
            val dist = RailHelper.getRail(onPos.above(), this.level()).flatMap { pos ->
                railHelper.traverse(
                    pos,
                    this.level(),
                    this.direction,
                    { _, blockPos ->
                        val railoc = RailHelper.getRail(blockPos, this.level())
                        if (railoc.isEmpty) {
                            return@traverse true
                        }
                        val shape = railHelper.getShape(railoc.get())
                        val block = level().getBlockState(railoc.get())
                        !(shape == RailShape.EAST_WEST || shape == RailShape.NORTH_SOUTH)
                                || block.`is`(LOCOMOTIVE_DOCK_RAIL.get())
                                || block.block is MultiShapeRail
                    },
                    12
                )
            }
                .orElse(12)
            val minimum = ShippingConfig.Server.LOCO_BASE_SPEED!!.get() * 0.2
            val modifier = dist / 12.0
            speedLimit = minimum + (ShippingConfig.Server.LOCO_BASE_SPEED!!.get() * 0.8 * modifier)
            speedRecomputeCooldown = 10
        } else {
            speedRecomputeCooldown--
        }
    }

    fun shouldFreezeTrain(): Boolean = !enrollmentHandler.mayMove()
            || (stalling.isStalled() && !isDocked)
            || linkingHandler.train?.asList()?.any { trainCarEntity -> trainCarEntity.isFrozen() } ?: false

    private fun accelerate() {

        val dir = this.direction
        if (abs(deltaMovement.x) < speedLimit && abs(deltaMovement.z) < speedLimit) {
            val mod = this.speedModifier
            this.push(dir.stepX * mod, 0.0, dir.stepZ * mod)
        }
    }

    override fun setDominated(entity: AbstractTrainCarEntity) {
        linkingHandler.follower = Optional.of(entity)
    }

    override fun setDominant(entity: AbstractTrainCarEntity) {
        throw IllegalStateException("Locomotive cannot have a dominant")
    }


    override fun removeDominated() {
        linkingHandler.follower = Optional.empty()
        linkingHandler.train?.tail = this
    }

    override fun removeDominant() {
        throw IllegalStateException("Locomotive cannot have a dominant")
    }

    override fun setTrain(train: Train<AbstractTrainCarEntity>) {
        linkingHandler.train = train
    }

    override fun getStalling(): StallingCapability {
        return stalling
    }

    private val stalling: StallingCapability = object : StallingCapability {

        override fun isDocked(): Boolean = isDocked

        override fun dock(x: Double, y: Double, z: Double) {
            isDocked = true
            deltaMovement = Vec3.ZERO
            moveTo(x, y, z)
        }

        override fun undock() {
            isDocked = false
        }

        override fun isStalled(): Boolean = remainingStallTime > 0

        override fun stall() {
            remainingStallTime = 20
        }

        override fun unstall() {
            remainingStallTime = 0
        }

        override fun isFrozen(): Boolean = super@AbstractLocomotiveEntity.isFrozen()

        override fun freeze() {
            setFrozen(true)
        }

        override fun unfreeze() {
            setFrozen(false)
        }
    }

    private fun updateNavigatorFromItem() {
        val stack = routeItemHandler.getStackInSlot(0)
        if (stack.item is LocoRouteItem) {
            navigator.updateWithLocoRouteItem(Route.getRoute(stack))
        } else {
            navigator.updateWithLocoRouteItem(Route())
        }
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        engine.readAdditionalSaveData(compound, registryAccess())
        routeItemHandler.deserializeNBT(this.registryAccess(), compound.getCompound(LOCO_ROUTE_INV_TAG))
        navigator.loadFromNbt(compound.getCompound(NAVIGATOR_TAG))
        enrollmentHandler.load(compound)
        updateNavigatorFromItem()
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        engine.addAdditionalSaveData(compound, registryAccess())
        compound.put(LOCO_ROUTE_INV_TAG, routeItemHandler.serializeNBT(this.registryAccess()))
        compound.put(NAVIGATOR_TAG, navigator.saveToNbt())
        enrollmentHandler.save(compound)
    }

    override fun getRawHandler(): ItemStackHandler {
        return engine
    }


    // duplicate due to linking issues
    override fun isValid(pPlayer: Player): Boolean =
        if (this.isRemoved) {
            false
        } else {
            this.distanceToSqr(pPlayer) <= 64.0
        }

    override fun stillValid(pPlayer: Player): Boolean =
        if (this.isRemoved) {
            false
        } else {
            this.distanceToSqr(pPlayer) <= 64.0
        }

    override fun setEngineOn(state: Boolean) {
        this.engine.setEngineOn(state)
    }

    override fun getRouteItemHandler(): ItemStackHandler {
        return routeItemHandler
    }


    /**
     * Called every tick the minecart is on an activator rail.
     */
    override fun activateMinecart(pX: Int, pY: Int, pZ: Int, pReceivingPower: Boolean) {
        if (pReceivingPower) {
            if (this.isVehicle) {
                this.ejectPassengers()
            }

            if (this.hurtTime == 0) {
                this.hurtDir = -this.hurtDir
                this.hurtTime = 10
                this.damage = 50.0f
                this.markHurt()
            }
        }
    }

    override fun canTakeItemThroughFace(index: Int, itemStack: ItemStack, dir: Direction): Boolean {
        return false
    }

    override fun getSlotsForFace(dir: Direction): IntArray {
        return intArrayOf(0)
    }

    override fun canPlaceItemThroughFace(index: Int, itemStack: ItemStack, dir: Direction?): Boolean {
        return getStalling().isDocked()
    }

    override fun getPassengerAttachmentPoint(entity: Entity, dimensions: EntityDimensions, partialTick: Float): Vec3 {
        return super.getPassengerAttachmentPoint(entity, dimensions, partialTick).subtract(PASSENGER_ATTACHMENT_OFFSET)
    }

    companion object {
        // item handler for loco routes
        private const val LOCO_ROUTE_INV_TAG = "locoRouteInv"
        private const val NAVIGATOR_TAG = "navigator"

        val PASSENGER_ATTACHMENT_OFFSET = Vec3(0.0, 1.0, 0.0)

        private val INDEPENDENT_MOTION: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            AbstractLocomotiveEntity::class.java, EntityDataSerializers.BOOLEAN
        )
        private val OWNER: EntityDataAccessor<String> = SynchedEntityData.defineId(
            AbstractLocomotiveEntity::class.java, EntityDataSerializers.STRING
        )
        private val ENGINE_IS_ON: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            AbstractLocomotiveEntity::class.java, EntityDataSerializers.BOOLEAN
        )
        private val REMAINING_BURN_TIME: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            AbstractLocomotiveEntity::class.java, EntityDataSerializers.INT
        )
    }
}
