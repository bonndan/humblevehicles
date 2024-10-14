package com.github.bonndan.humblevehicles.entity.custom.vessel.tug

import com.github.bonndan.humblevehicles.block.dock.TugDockTileEntity
import com.github.bonndan.humblevehicles.block.guiderail.TugGuideRailBlock.Companion.getArrowsDirection
import com.github.bonndan.humblevehicles.capability.StallingCapability
import com.github.bonndan.humblevehicles.entity.accessor.HeadVehicleDataAccessor
import com.github.bonndan.humblevehicles.entity.custom.*
import com.github.bonndan.humblevehicles.entity.custom.vessel.VesselEntity
import com.github.bonndan.humblevehicles.entity.navigation.TugPathNavigator
import com.github.bonndan.humblevehicles.item.TugRouteItem
import com.github.bonndan.humblevehicles.setup.ModBlocks
import com.github.bonndan.humblevehicles.setup.ModItems
import com.github.bonndan.humblevehicles.setup.ModSounds
import com.github.bonndan.humblevehicles.util.*
import net.minecraft.client.player.Input
import net.minecraft.client.player.LocalPlayer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.*
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.MoverType
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.entity.animal.WaterAnimal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.Boat
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.entity.PartEntity
import net.neoforged.neoforge.items.ItemStackHandler
import java.util.*
import java.util.stream.IntStream
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.hypot

abstract class AbstractTugEntity :
    VesselEntity, LinkableEntityHead<VesselEntity>, Container, WorldlyContainer, HeadVehicle,
    ItemHandlerVanillaContainerWrapper, Stalling {

    protected val enrollmentHandler: ChunkManagerEnrollmentHandler
    protected val saveStateCallback = object : SaveStateCallback {
        override fun saveState(engineState: Boolean, remainingBurnTime: Int) {
            entityData[ENGINE_IS_ON] = engineState
            entityData[REMAINING_BURN_TIME] = remainingBurnTime
        }
    }
    private lateinit var engine: Engine
    protected fun setEngine(engine: Engine) {
        this.engine = engine
    }

    protected fun getEngine(): Engine {
        return engine
    }

    private lateinit var control: VehicleControl
    protected fun setControl(control: VehicleControl) {
        this.control = control
    }

    override fun getControl(): VehicleControl {
        return control
    }

    // CONTAINER STUFF
    private val routeItemHandler: ItemStackHandler = createRouteItemHandler()
    private var contentsChanged: Boolean = false
    private var isDocked: Boolean = false
    fun isDocked(): Boolean = isDocked
    private var remainingStallTime: Int = 0
    private var swimSpeedMult: Double = 1.0

    private var dockCheckCooldown: Int = 0
    protected var independentMotion: Boolean = false
    private var pathfindCooldown: Int = 0
    private val frontHitbox: VehicleFrontPart

    private var path: Route?
    private var nextStop: Int = 0

    constructor(type: EntityType<out WaterAnimal>, world: Level) : super(type, world)

    constructor(type: EntityType<out WaterAnimal>, worldIn: Level, x: Double, y: Double, z: Double) : this(
        type,
        worldIn
    ) {
        this.setPos(x, y, z)
        this.xo = x
        this.yo = y
        this.zo = z
    }

    override fun allowDockInterface(): Boolean {
        return isDocked
    }

    override fun getRouteIcon(): ResourceLocation {
        return ModItems.TUG_ROUTE_ICON
    }


    // CONTAINER STUFF
    override fun dropLeash(p_110160_1_: Boolean, p_110160_2_: Boolean) {
        navigation.recomputePath()
        super.dropLeash(p_110160_1_, p_110160_2_)
    }

    fun getDataAccessor(): HeadVehicleDataAccessor = HeadVehicleDataAccessor.Builder()
        .withBurnProgressPct { engine.getBurnProgressPct() }
        .withId(this.id)
        .withLit { engine.isLit() }
        .withVisitedSize { nextStop }
        .withOn { engine.isOn() }
        .withRouteSize { path?.size ?: 0 }
        .withCanMove { enrollmentHandler.mayMove() }
        .build()

    private fun createRouteItemHandler(): ItemStackHandler {
        return object : ItemStackHandler() {
            override fun getStackLimit(slot: Int, stack: ItemStack): Int {
                return 1
            }

            override fun onContentsChanged(slot: Int) {
                contentsChanged = true
            }

            override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
                return stack.getItem() is TugRouteItem
            }
        }
    }

    override fun owner(): String? {
        return entityData[OWNER]
    }

    override fun isPushedByFluid(): Boolean {
        return true
    }

    protected abstract fun createContainerProvider(): MenuProvider

    override fun readAdditionalSaveData(compound: CompoundTag) {
        if (compound.contains("inv")) {
            val old = ItemStackHandler()
            old.deserializeNBT(this.registryAccess(), compound.getCompound("inv"))
            routeItemHandler.setStackInSlot(0, old.getStackInSlot(0))
        } else {
            routeItemHandler.deserializeNBT(this.registryAccess(), compound.getCompound("routeHandler"))
        }
        nextStop = if (compound.contains("next_stop")) compound.getInt("next_stop") else 0
        engine.readAdditionalSaveData(compound, registryAccess())
        contentsChanged = true
        enrollmentHandler.load(compound)
        super.readAdditionalSaveData(compound)
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.putInt("next_stop", nextStop)
        engine.addAdditionalSaveData(compound, registryAccess())
        compound.put("routeHandler", routeItemHandler.serializeNBT(this.registryAccess()))
        enrollmentHandler.save(compound)
        super.addAdditionalSaveData(compound)
    }

    private fun tickRouteCheck() {
        if (contentsChanged) {
            val stack: ItemStack = routeItemHandler.getStackInSlot(0)
            this.setPath(Route.getRoute(stack))
            contentsChanged = false
        }

        // fix for currently borked worlds
        if (nextStop >= path!!.size) {
            this.nextStop = 0
        }
    }

    override fun setItem(slot: Int, stack: ItemStack) {
        if (!engine.isItemValid(slot, stack)) {
            return
        }
        engine.insertItem(slot, stack, false)
        if (!stack.isEmpty && stack.count > this.maxStackSize) {
            stack.count = this.maxStackSize
        }
    }

    override fun getRawHandler(): ItemStackHandler {
        return engine
    }

    protected fun onDock() {
        this.playSound(ModSounds.TUG_DOCKING.get(), 0.6f, 1.0f)
    }

    protected open fun onUndock() {
        this.playSound(ModSounds.TUG_UNDOCKING.get(), 0.6f, 1.5f)
    }

    private val sideDirections: List<Direction>
        // MOB STUFF
        get() {
            return if (this.getDirection() == Direction.NORTH || this.getDirection() == Direction.SOUTH)
                listOf(Direction.EAST, Direction.WEST)
            else
                listOf(Direction.NORTH, Direction.SOUTH)
        }


    private fun tickCheckDock() {
        Optional.ofNullable<StallingCapability>(getCapability(StallingCapability.STALLING_CAPABILITY))
            .ifPresent { cap: StallingCapability ->
                val x: Int = floor(this.x).toInt()
                val y: Int = floor(this.y).toInt()
                val z: Int = floor(this.z).toInt()

                val docked: Boolean = cap.isDocked()

                if (docked && dockCheckCooldown > 0) {
                    dockCheckCooldown--
                    this.setDeltaMovement(Vec3.ZERO)
                    this.moveTo(x + 0.5, getY(), z + 0.5)
                    return@ifPresent
                }

                // Check docks
                val shouldDock: Boolean = sideDirections
                    .stream()
                    .map { curr: Direction ->
                        Optional.ofNullable(
                            level().getBlockEntity(BlockPos(x + curr.stepX, y, z + curr.stepZ))
                        )
                            .filter { entity: BlockEntity? -> entity is TugDockTileEntity }
                            .map { entity: BlockEntity -> entity as TugDockTileEntity }
                            .map { dock: TugDockTileEntity -> dock.hold(this, curr) }
                            .orElse(false)
                    }
                    .reduce(false) { acc: Boolean, curr: Boolean -> acc || curr }

                val changedDock: Boolean = !docked && shouldDock
                val changedUndock: Boolean = docked && !shouldDock

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


    override fun createNavigation(p_175447_1_: Level): PathNavigation {
        return TugPathNavigator(this, p_175447_1_)
    }

    public override fun mobInteract(player: Player, hand: InteractionHand): InteractionResult {

        if (!level().isClientSide) {
            val color: DyeColor? = DyeColor.getColor(player.getItemInHand(hand))

            if (color != null) {
                getEntityData().set(COLOR_DATA, color.getId())
            } else {

                if (isVehicle) {

                    if (wantsToStopRiding(player) && isControlledByLocalInstance) {
                        player.stopRiding()
                    } else {
                        player.openMenu(createContainerProvider(), getDataAccessor()::write)
                    }

                } else {

                    if (!player.isSecondaryUseActive) {
                        if (!player.startRiding(this))
                            return InteractionResult.FAIL
                    } else {
                        player.openMenu(createContainerProvider(), getDataAccessor()::write)
                    }
                }

            }
        }

        return InteractionResult.sidedSuccess(level().isClientSide)
    }

    protected open fun wantsToStopRiding(player: Player): Boolean {
        return player.isShiftKeyDown
    }

    override fun getControllingPassenger(): LivingEntity? =
        if (this.firstPassenger is LivingEntity) {
            this.firstPassenger as LivingEntity
        } else {
            super.getControllingPassenger()
        }

    override fun enroll(uuid: UUID) {
        enrollmentHandler.enroll(uuid)
    }

    override fun onSyncedDataUpdated(key: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(key)

        if (level().isClientSide && entityData != null) {
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
    }

    override fun registerGoals() {
        goalSelector.addGoal(0, MovementGoal())
    }

    internal inner class MovementGoal : Goal() {
        override fun canUse(): Boolean {
            return this@AbstractTugEntity.path != null
        }

        override fun tick() {
            if (!level().isClientSide) {
                tickRouteCheck()
                tickCheckDock()

                followPath()
                followGuideRail()
            }
        }
    }

    override fun isMultipartEntity(): Boolean {
        return true
    }

    override fun getParts(): Array<PartEntity<*>> {
        return arrayOf(frontHitbox)
    }

    override fun aiStep() {
        super.aiStep()
        if (!isDeadOrDying() && !this.isNoAi()) {
            frontHitbox.updatePosition(this)
        }
    }

    override fun recreateFromPacket(packet: ClientboundAddEntityPacket) {
        super.recreateFromPacket(packet)
        frontHitbox.id = packet.id
    }

    override fun tick() {

        if (!level().isClientSide) {
            enrollmentHandler.tick()
            enrollmentHandler.playerName.ifPresent { name: String -> entityData.set(OWNER, name) }
            engine.tickFuel()
        }

        if (this.isControlledByLocalInstance) {

            if (level().isClientSide && this.isVehicle) {
                val passenger = controllingPassenger
                if (passenger is LocalPlayer) {

                    if (passenger.isShiftKeyDown() && this.isPassenger) {
                        passenger.input.shiftKeyDown = false
                    }

                    this.controlBoat(passenger.input, this.status)
                }
            }

            this.move(MoverType.SELF, this.deltaMovement)
        } else {
            this.deltaMovement = Vec3.ZERO
        }

        super.tick()
    }

    private fun controlBoat(input: Input, status: Boat.Status?) {

        if (!engine.isLit()) {
            return
        }

        val result = control.calculateResult(input, status)

        this.deltaRotation += result.deltaRotationModifier
        this.yRot += result.yRotationModifier
        this.deltaMovement = deltaMovement.add(result.calculateDeltaMovement(yRot))
    }

    private fun followGuideRail() {
        // do not follow guide rail if stalled
        val dockcap = Optional.ofNullable(getCapability(StallingCapability.STALLING_CAPABILITY))
        if (dockcap.isPresent()) {
            val stallingCapability = dockcap.get()
            if (stallingCapability.isDocked() || stallingCapability.isFrozen() || stallingCapability.isStalled()) return
        }

        val currentPos = onPos
        val belowList: List<BlockState> = listOf(
            level().getBlockState(currentPos.below()),
            level().getBlockState(currentPos.below().below())
        )
        val water: BlockState = level().getBlockState(currentPos)
        for (below: BlockState in belowList) {
            if (below.`is`(ModBlocks.GUIDE_RAIL_TUG.get()) && water.`is`(Blocks.WATER)) {
                val arrows: Direction = getArrowsDirection(below)
                this.setYRot(arrows.toYRot())
                val modifier = 0.03
                this.deltaMovement = deltaMovement.add(Vec3(arrows.stepX * modifier, 0.0, arrows.stepZ * modifier))
            }
        }
    }

    // todo: someone said you could prevent mobs from getting stuck on blocks by override this
    override fun customServerAiStep() {
        super.customServerAiStep()
    }

    private fun followPath() {
        pathfindCooldown--
        if (!path!!.isEmpty() && !this.isDocked && engine.isLit() && !shouldFreezeTrain()) {
            val stop = path!!.get(nextStop)
            if (navigation.path == null || navigation.path!!.isDone) {
                if (pathfindCooldown < 0 || navigation.path != null) {  //only go on cooldown when the path was not completed
                    navigation.moveTo(stop.x.toDouble(), this.getY(), stop.z.toDouble(), 0.3)
                    pathfindCooldown = 20
                } else {
                    return
                }
            }
            val distance: Double = abs(hypot(this.getX() - (stop.x + 0.5), this.getZ() - (stop.z + 0.5)))
            independentMotion = true
            entityData.set(INDEPENDENT_MOTION, true)

            if (distance < 0.9) {
                incrementStop()
            }
        } else {
            entityData.set(INDEPENDENT_MOTION, false)
            navigation.stop()
            if (remainingStallTime > 0) {
                remainingStallTime--
            }

            if (path!!.isEmpty()) {
                this.nextStop = 0
            }
        }
    }

    private fun shouldFreezeTrain(): Boolean {
        return !enrollmentHandler.mayMove() || (stalling.isStalled() && !isDocked) || getLinkingHandler().train!!.asList()
            .stream().anyMatch(VesselEntity::isFrozen)
    }

    override fun defineSynchedData(pBuilder: SynchedEntityData.Builder) {
        super.defineSynchedData(pBuilder)
        pBuilder.define(INDEPENDENT_MOTION, false)
        pBuilder.define(OWNER, "")
        pBuilder.define(ENGINE_IS_ON, false)
        pBuilder.define(REMAINING_BURN_TIME, 0)
    }

    private fun setPath(path: Route?) {
        if (!this.path!!.isEmpty() && !this.path!!.equals(path)) {
            this.nextStop = 0
        }
        this.path = path
    }

    private fun incrementStop() {
        if (path!!.size == 1) {
            nextStop = 0
        } else if (!path!!.isEmpty()) {
            nextStop = (nextStop + 1) % (path!!.size)
        }
    }

    override fun setDominated(entity: VesselEntity) {
        getLinkingHandler()?.follower = (Optional.of(entity))
    }

    override fun setDominant(entity: VesselEntity) {
        throw IllegalStateException("Tugs cannot have a dominant")
    }

    override fun removeDominated() {
        getLinkingHandler().follower = Optional.empty()
        getLinkingHandler().train!!.tail = this
    }

    override fun hasOwner(): Boolean {
        return enrollmentHandler.hasOwner()
    }

    override fun removeDominant() {
        throw IllegalStateException("Tugs cannot have a dominant")
    }

    override fun setTrain(train: Train<VesselEntity>) {
        getLinkingHandler().train = train
    }

    override fun getTrain(): Train<VesselEntity> {
        return getLinkingHandler().train!!
    }

    override fun remove(r: RemovalReason) {
        if (!level().isClientSide) {
            val stack = ItemStack(this.getDropItem())
            if (this.hasCustomName()) {
                //TODO stack.setHoverName(this.getCustomName());
            }
            this.spawnAtLocation(stack)
            Containers.dropContents(this.level(), this, this)
            this.spawnAtLocation(routeItemHandler.getStackInSlot(0))
        }
        super.remove(r)
    }

    override fun canPlaceItem(p_94041_1_: Int, p_94041_2_: ItemStack): Boolean {
        return true
    }

    override fun setChanged() {
        contentsChanged = true
    }

    override fun isValid(p_70300_1_: Player): Boolean {
        if (this.isRemoved()) {
            return false
        } else {
            return !(p_70300_1_.distanceToSqr(this) > 64.0)
        }
    }

    override fun stillValid(p_70300_1_: Player): Boolean {
        if (this.isRemoved()) {
            return false
        } else {
            return !(p_70300_1_.distanceToSqr(this) > 64.0)
        }
    }

    override fun canTakeItemThroughFace(p_180461_1_: Int, p_180461_2_: ItemStack, p_180461_3_: Direction): Boolean {
        return false
    }

    override fun getSlotsForFace(p_180463_1_: Direction): IntArray {
        return IntStream.range(0, getContainerSize()).toArray()
    }

    override fun canPlaceItemThroughFace(p_180462_1_: Int, p_180462_2_: ItemStack, p_180462_3_: Direction?): Boolean {
        return isDocked
    }

    override fun getContainerSize(): Int {
        return 1
    }

    override fun canBeLeashed(): Boolean {
        return true
    }

    override fun swimSpeed(): Double {
        if (level().isClientSide) {
            return super.swimSpeed()
        }

        if (this.tickCount % 10 == 0) {
            swimSpeedMult = computeSpeedMultiplier()
        }

        return swimSpeedMult * super.swimSpeed()
    }

    private fun computeSpeedMultiplier(): Double {

        var mult = 1.0
        var doBreak = false
        var i: Int = 0
        val directions = listOf(Direction.NORTH, Direction.EAST, Direction.WEST, Direction.SOUTH)

        while (i < 10 && !doBreak) {
            for (direction: Direction in directions) {
                val pos: BlockPos = onPos.relative(direction, i)
                if (!level().getFluidState(pos).isSource) {
                    doBreak = true
                    break
                }
            }
            if (i > 3) {
                mult = 1 + ((i / 10f) * 1.8)
            }
            i++
        }

        return if (mult < swimSpeedMult) {
            mult
        } else {
            (mult + swimSpeedMult * 20) / 21
        }
    }

    override fun setEngineOn(state: Boolean) {
        this.engine.setEngineOn(state)
    }

    override fun getRouteItemHandler(): ItemStackHandler {
        return routeItemHandler
    }

    override fun getStalling(): StallingCapability {
        return stalling
    }

    /*
     Stalling Capability
    */
    private val stalling: StallingCapability = object : StallingCapability {
        override fun isDocked(): Boolean {
            return isDocked
        }

        override fun dock(x: Double, y: Double, z: Double) {
            isDocked = true
            setDeltaMovement(Vec3.ZERO)
            moveTo(x, y, z)
        }

        override fun undock() {
            isDocked = false
        }

        override fun isStalled(): Boolean {
            return remainingStallTime > 0
        }

        override fun stall() {
            remainingStallTime = 20
        }

        override fun unstall() {
            remainingStallTime = 0
        }

        override fun isFrozen(): Boolean {
            return super@AbstractTugEntity.isFrozen
        }

        override fun freeze() {
            isFrozen = true
        }

        override fun unfreeze() {
            isFrozen = false
        }
    }

    init {
        this.blocksBuilding = true
        getLinkingHandler().train = Train(this)
        this.path = Route()
        frontHitbox = VehicleFrontPart(this)
        enrollmentHandler = ChunkManagerEnrollmentHandler(this)
    }

    companion object {

        private val INDEPENDENT_MOTION: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            AbstractTugEntity::class.java, EntityDataSerializers.BOOLEAN
        )

        private val OWNER: EntityDataAccessor<String> = SynchedEntityData.defineId(
            AbstractTugEntity::class.java, EntityDataSerializers.STRING
        )

        private val ENGINE_IS_ON: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            AbstractTugEntity::class.java, EntityDataSerializers.BOOLEAN
        )

        private val REMAINING_BURN_TIME: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            AbstractTugEntity::class.java, EntityDataSerializers.INT
        )

        fun setCustomAttributes(): AttributeSupplier.Builder {
            return VesselEntity.setCustomAttributes()
                .add(Attributes.FOLLOW_RANGE, 200.0)
        }
    }
}
