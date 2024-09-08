package dev.murad.shipping.entity.custom.train.locomotive

import dev.murad.shipping.ShippingConfig
import dev.murad.shipping.block.rail.MultiShapeRail
import dev.murad.shipping.block.rail.blockentity.LocomotiveDockTileEntity
import dev.murad.shipping.capability.StallingCapability
import dev.murad.shipping.entity.accessor.DataAccessor
import dev.murad.shipping.entity.custom.HeadVehicle
import dev.murad.shipping.entity.custom.train.AbstractTrainCarEntity
import dev.murad.shipping.entity.custom.vessel.tug.VehicleFrontPart
import dev.murad.shipping.entity.navigation.LocomotiveNavigator
import dev.murad.shipping.item.LocoRouteItem
import dev.murad.shipping.setup.ModBlocks
import dev.murad.shipping.setup.ModItems
import dev.murad.shipping.setup.ModSounds
import dev.murad.shipping.util.*
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.Entity
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
import net.neoforged.neoforge.entity.PartEntity
import net.neoforged.neoforge.items.ItemStackHandler
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import kotlin.math.abs
import kotlin.math.floor

abstract class AbstractLocomotiveEntity : AbstractTrainCarEntity, LinkableEntityHead<AbstractTrainCarEntity>,
    ItemHandlerVanillaContainerWrapper, HeadVehicle {

    private var engineOn: Boolean = false

    protected val enrollmentHandler: ChunkManagerEnrollmentHandler
    private val doflip = false
    private var independentMotion = false
    var isDocked: Boolean = false
        private set
    private val frontHitbox: VehicleFrontPart
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
        frontHitbox = VehicleFrontPart(this)
        enrollmentHandler = ChunkManagerEnrollmentHandler(this)
    }

    constructor(type: EntityType<*>, level: Level, x: Double, y: Double, z: Double) : super(type, level, x, y, z) {
        frontHitbox = VehicleFrontPart(this)
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
            this.spawnAtLocation(routeItemHandler.getStackInSlot(0))
        }
        super.remove(r)
    }

    override fun interact(pPlayer: Player, pHand: InteractionHand): InteractionResult {
        val ret = super.interact(pPlayer, pHand)
        if (ret.consumesAction()) return ret

        if (pHand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS
        }

        if (!level().isClientSide) {
            pPlayer.openMenu(createContainerProvider())
            //NetworkHooks.openScreen((ServerPlayer) pPlayer, , getDataAccessor()::write);
        }

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

    abstract val dataAccessor: DataAccessor

    protected abstract fun tickFuel(): Boolean

    override fun onSyncedDataUpdated(key: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(key)

        if (level().isClientSide) {
            if (INDEPENDENT_MOTION == key) {
                independentMotion = entityData.get(INDEPENDENT_MOTION)
            }
        }
    }

    override fun owner(): String {
        return entityData.get(OWNER)
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
            enrollmentHandler.playerName.ifPresent { name: String ->
                entityData.set(
                    OWNER,
                    name
                )
            }
        }

        tickYRot()
        val yrot = this.yRot
        tickVanilla()
        this.yRot = yrot
        if (linkingHandler.follower.isEmpty && deltaMovement.length() > 0.05) {
            this.yRot = RailHelper.directionFromVelocity(deltaMovement).toYRot()
        }
        if (!level().isClientSide) {
            tickDockCheck()
            tickMovement()
        }

        if (level().isClientSide
            && independentMotion
        ) {
            doMovementEffect()
        }


        frontHitbox.updatePosition(this)
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

    override fun getMaxCartSpeedOnRail(): Float {
        return (ShippingConfig.Server.TRAIN_MAX_SPEED!!.get() * 0.9).toFloat()
    }

    fun flip() {
        this.yRot = direction.opposite.toYRot()
    }

    protected open fun doMovementEffect() {
    }

    override fun defineSynchedData(pBuilder: SynchedEntityData.Builder) {
        super.defineSynchedData(pBuilder)
        entityData.set(INDEPENDENT_MOTION, false)
        entityData.set(OWNER, "")
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
        if (!isDocked && engineOn && remainingStallTime <= 0 && !forceStallCheck && !shouldFreezeTrain() && tickFuel()) {
            tickSpeedLimit()
            entityData.set(INDEPENDENT_MOTION, true)
            accelerate()
        } else {
            if (RailHelper.getRail(this.onPos.above(), this.level())
                    .map { pos: BlockPos? -> railHelper.getShape(pos) }
                    .map { obj: RailShape -> obj.name }
                    .map { s: String -> s.contains("ASCENDING") }
                    .orElse(true) && tickFuel()
            ) {
                this.deltaMovement = Vec3.ZERO
                entityData.set(INDEPENDENT_MOTION, true)
                this.setPos(xOld, yOld, zOld)
            } else {
                entityData.set(INDEPENDENT_MOTION, false)
            }
        }

        if (shouldFreezeTrain()) {
            linkingHandler.train?.asList()
                ?.forEach(Consumer { t: AbstractTrainCarEntity -> t.setDeltaMovement(0.0, 0.0, 0.0) })
        }
    }

    private fun checkStopSign(pos: BlockPos, prevExitTaken: Direction): Boolean {
        return RailHelper.getRail(pos, this.level()).flatMap { block: BlockPos? ->

            if (level().getBlockState(block).block is MultiShapeRail) {

                val r = level().getBlockState(block).block as MultiShapeRail
                if (level().getEntitiesOfClass<Entity>(
                        Entity::class.java, AABB(pos)
                    ) { e: Entity -> e == this || e == frontHitbox }.isNotEmpty()
                ) {
                    return@flatMap Optional.empty<Boolean>()
                }

                return@flatMap r.getPriorityDirectionsToCheck(level().getBlockState(block), prevExitTaken.opposite)
                    .stream()
                    .map<Optional<Int>> { p: Direction? ->
                        railHelper.traverse(
                            pos.relative(p), this.level(), p,
                            { dir: Direction?, f: BlockPos -> checkLocoCollision(f) }, 2
                        )
                    }
                    .map<Boolean> { obj -> obj.isPresent }
                    .reduce { a: Boolean, b: Boolean -> java.lang.Boolean.logicalOr(a, b) }
            } else return@flatMap Optional.of<Boolean>(false)
        }.orElse(false)
    }

    private fun checkCollision(pos: BlockPos): Boolean {
        val aabb = AABB(pos)
        return level().getEntitiesOfClass(
            Entity::class.java, aabb
        ) { entity ->
            when (entity) {
                //TODO check this kotlin conversion
                is AbstractTrainCarEntity -> {
                    entity.getTrain()?.tug?.map { f -> f?.uuid != this.getUUID() }
                        ?.orElse(true) ?: true
                }

                is AbstractMinecart -> {
                    true
                }

                is VehicleFrontPart -> {
                    !entity.`is`(this)
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
                    e.getTrain()?.tug?.map { f -> f?.uuid != this.getUUID() }
                        ?.orElse(true) ?: true
                }

                is VehicleFrontPart -> {
                    !e.`is`(this)
                }

                else -> false
            }
        }.isNotEmpty()
    }

    override fun getParts(): Array<PartEntity<*>> {
        return arrayOf(frontHitbox)
    }

    override fun isMultipartEntity(): Boolean {
        return true
    }

    override fun isPoweredCart(): Boolean {
        return true
    }

    override fun recreateFromPacket(p_149572_: ClientboundAddEntityPacket) {
        super.recreateFromPacket(p_149572_)
        frontHitbox.id = p_149572_.id
    }


    protected fun onDock() {
        this.playSound(ModSounds.TUG_DOCKING.get(), 0.6f, 1.0f)
    }

    protected open fun onUndock() {
        this.playSound(ModSounds.TUG_UNDOCKING.get(), 0.6f, 1.5f)
    }

    private fun tickDockCheck() {
        Optional.ofNullable(getCapability(StallingCapability.STALLING_CAPABILITY))
            .ifPresent { cap: StallingCapability ->
                val x = floor(this.x) as Int
                val y = floor(this.y) as Int
                val z = floor(this.z) as Int

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

    private val speedModifier: Double
        get() {
            // adjust speed based on slope etc.
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
            val dist = RailHelper.getRail(onPos.above(), this.level()).flatMap { pos: BlockPos? ->
                railHelper.traverse(
                    pos,
                    this.level(),
                    this.direction,
                    { direction: Direction?, p: BlockPos? ->
                        val railoc = RailHelper.getRail(p, this.level())
                        if (railoc.isEmpty) {
                            return@traverse true
                        }
                        val shape = railHelper.getShape(railoc.get())
                        val block = level().getBlockState(railoc.get())
                        !(shape == RailShape.EAST_WEST || shape == RailShape.NORTH_SOUTH)
                                || block.`is`(ModBlocks.LOCOMOTIVE_DOCK_RAIL.get())
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
    }


    override fun removeDominated() {
        linkingHandler.follower = Optional.empty()
        linkingHandler.train?.tail = this
    }

    override fun removeDominant() {
    }

    override fun setTrain(train: Train<AbstractTrainCarEntity>) {
        linkingHandler.train = train
    }

    protected val stalling: StallingCapability = object : StallingCapability {

        override fun isDocked(): Boolean
            = isDocked

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

        override fun isFrozen(): Boolean
            = super@AbstractLocomotiveEntity.isFrozen()

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
            navigator.updateWithLocoRouteItem(LocoRouteItem.getRoute(stack))
        } else {
            navigator.updateWithLocoRouteItem(LocoRoute())
        }
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        if (compound.contains("eo")) {
            engineOn = compound.getBoolean("eo")
        }
        routeItemHandler.deserializeNBT(this.registryAccess(), compound.getCompound(LOCO_ROUTE_INV_TAG))
        navigator.loadFromNbt(compound.getCompound(NAVIGATOR_TAG))
        enrollmentHandler.load(compound)
        updateNavigatorFromItem()
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        compound.putBoolean("eo", engineOn)
        compound.put(LOCO_ROUTE_INV_TAG, routeItemHandler.serializeNBT(this.registryAccess()))
        compound.put(NAVIGATOR_TAG, navigator.saveToNbt())
        enrollmentHandler.save(compound)
    }

    // duplicate due to linking issues
    override fun isValid(pPlayer: Player): Boolean {
        return if (this.isRemoved) {
            false
        } else {
            !(this.distanceToSqr(pPlayer) > 64.0)
        }
    }

    override fun stillValid(pPlayer: Player): Boolean {
        return if (this.isRemoved) {
            false
        } else {
            !(this.distanceToSqr(pPlayer) > 64.0)
        }
    }

    fun isEngineOn(): Boolean {
        return engineOn
    }

    override fun setEngineOn(state: Boolean) {
        this.engineOn = state
    }

    override fun getRouteItemHandler(): ItemStackHandler {
        return routeItemHandler
    }

    companion object {
        // item handler for loco routes
        private const val LOCO_ROUTE_INV_TAG = "locoRouteInv"


        private const val NAVIGATOR_TAG = "navigator"
        private val INDEPENDENT_MOTION: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            AbstractLocomotiveEntity::class.java, EntityDataSerializers.BOOLEAN
        )
        private val OWNER: EntityDataAccessor<String> = SynchedEntityData.defineId(
            AbstractLocomotiveEntity::class.java, EntityDataSerializers.STRING
        )
    }
}
