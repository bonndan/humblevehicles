package dev.murad.shipping.entity.custom.vessel.tug

import dev.murad.shipping.ShippingConfig
import dev.murad.shipping.block.dock.TugDockTileEntity
import dev.murad.shipping.block.guiderail.TugGuideRailBlock.Companion.getArrowsDirection
import dev.murad.shipping.capability.StallingCapability
import dev.murad.shipping.entity.accessor.DataAccessor
import dev.murad.shipping.entity.custom.HeadVehicle
import dev.murad.shipping.entity.custom.vessel.VesselEntity
import dev.murad.shipping.entity.navigation.TugPathNavigator
import dev.murad.shipping.item.TugRouteItem
import dev.murad.shipping.item.TugRouteItem.Companion.getRoute
import dev.murad.shipping.setup.ModBlocks
import dev.murad.shipping.setup.ModItems
import dev.murad.shipping.setup.ModSounds
import dev.murad.shipping.util.*
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.*
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.entity.animal.WaterAnimal
import net.minecraft.world.entity.player.Player
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
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier
import java.util.stream.IntStream
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.hypot

abstract class AbstractTugEntity(type: EntityType<out WaterAnimal>, world: Level) :
    VesselEntity(type, world), LinkableEntityHead<VesselEntity>, Container, WorldlyContainer, HeadVehicle {

    protected val enrollmentHandler: ChunkManagerEnrollmentHandler

    // CONTAINER STUFF
    private val routeItemHandler: ItemStackHandler = createRouteItemHandler()
    private var contentsChanged: Boolean = false
    private var isDocked: Boolean = false
    fun isDocked() : Boolean = isDocked
    private var remainingStallTime: Int = 0
    private var swimSpeedMult: Double = 1.0

    private var engineOn: Boolean = true

    private var dockCheckCooldown: Int = 0
    private var independentMotion: Boolean = false
    private var pathfindCooldown: Int = 0
    private val frontHitbox: VehicleFrontPart

    private var path: TugRoute?

    protected fun getPath(): TugRoute? = path

    private var nextStop: Int = 0

    protected fun getNextStop(): Int = nextStop

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


    abstract fun getDataAccessor(): DataAccessor

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
        return entityData.get(OWNER)
    }

    override fun isPushedByFluid(): Boolean {
        return true
    }

    protected abstract fun createContainerProvider(): MenuProvider

    override fun readAdditionalSaveData(compound: CompoundTag) {
        if (compound.contains("inv")) {
            val old: ItemStackHandler = ItemStackHandler()
            old.deserializeNBT(this.registryAccess(), compound.getCompound("inv"))
            routeItemHandler.setStackInSlot(0, old.getStackInSlot(0))
        } else {
            routeItemHandler.deserializeNBT(this.registryAccess(), compound.getCompound("routeHandler"))
        }
        nextStop = if (compound.contains("next_stop")) compound.getInt("next_stop") else 0
        engineOn = !compound.contains("engineOn") || compound.getBoolean("engineOn")
        contentsChanged = true
        enrollmentHandler.load(compound)
        super.readAdditionalSaveData(compound)
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.putInt("next_stop", nextStop)
        compound.putBoolean("engineOn", engineOn)
        compound.put("routeHandler", routeItemHandler.serializeNBT(this.registryAccess()))
        enrollmentHandler.save(compound)
        super.addAdditionalSaveData(compound)
    }

    private fun tickRouteCheck() {
        if (contentsChanged) {
            val stack: ItemStack = routeItemHandler.getStackInSlot(0)
            this.setPath(getRoute(stack))
            contentsChanged = false
        }

        // fix for currently borked worlds
        if (nextStop >= path!!.size) {
            this.nextStop = 0
        }
    }

    protected abstract fun tickFuel(): Boolean

    protected fun onDock() {
        this.playSound(ModSounds.TUG_DOCKING.get(), 0.6f, 1.0f)
    }

    protected open fun onUndock() {
        this.playSound(ModSounds.TUG_UNDOCKING.get(), 0.6f, 1.5f)
    }

    private val sideDirections: List<Direction>
        // MOB STUFF
        get() {
            return if (this.getDirection() == Direction.NORTH || this.getDirection() == Direction.SOUTH) Arrays.asList(
                Direction.EAST,
                Direction.WEST
            ) else Arrays.asList(
                Direction.NORTH,
                Direction.SOUTH
            )
        }


    private fun tickCheckDock() {
        Optional.ofNullable<StallingCapability>(getCapability(StallingCapability.STALLING_CAPABILITY))
            .ifPresent { cap: StallingCapability ->
                val x: Int = floor(this.getX()) as Int
                val y: Int = floor(this.getY()) as Int
                val z: Int = floor(this.getZ()) as Int

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
                            level().getBlockEntity(
                                BlockPos(
                                    x + curr.getStepX(),
                                    y,
                                    z + curr.getStepZ()
                                )
                            )
                        )
                            .filter(Predicate { entity: BlockEntity? -> entity is TugDockTileEntity })
                            .map(Function { entity: BlockEntity -> entity as TugDockTileEntity })
                            .map(Function { dock: TugDockTileEntity -> dock.hold(this, curr) })
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

    protected open fun makeSmoke() {
        val world: Level = this.level()
        if (world != null) {
            val blockpos: BlockPos = getOnPos().above().above()
            val random: RandomSource = world.random
            if (random.nextFloat() < ShippingConfig.Client.TUG_SMOKE_MODIFIER.get()) {
                for (i in 0 until random.nextInt(2) + 2) {
                    makeParticles(world, blockpos, this)
                }
            }
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
                player.openMenu(createContainerProvider())
            }
        }

        return InteractionResult.sidedSuccess(level().isClientSide)
    }

    override fun enroll(uuid: UUID) {
        enrollmentHandler.enroll(uuid)
    }

    override fun onSyncedDataUpdated(key: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(key)

        if (level().isClientSide) {
            if (INDEPENDENT_MOTION == key) {
                independentMotion = entityData.get(INDEPENDENT_MOTION)
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

    override fun recreateFromPacket(p_149572_: ClientboundAddEntityPacket) {
        super.recreateFromPacket(p_149572_)
        frontHitbox.setId(p_149572_.getId())
    }

    override fun tick() {
        if (level().isClientSide && independentMotion) {
            makeSmoke()
        }

        if (!level().isClientSide) {
            enrollmentHandler.tick()
            enrollmentHandler.playerName.ifPresent(Consumer { name: String -> entityData.set(OWNER, name) })
        }

        super.tick()
    }

    private fun followGuideRail() {
        // do not follow guide rail if stalled
        val dockcap: Optional<StallingCapability> =
            Optional.ofNullable(getCapability(StallingCapability.STALLING_CAPABILITY))
        if (dockcap.isPresent()) {
            val cap: StallingCapability = dockcap.get()
            if (cap.isDocked() || cap.isFrozen() || cap.isStalled()) return
        }

        val belowList: List<BlockState> = Arrays.asList(
            level().getBlockState(getOnPos().below()),
            level().getBlockState(getOnPos().below().below())
        )
        val water: BlockState = level().getBlockState(getOnPos())
        for (below: BlockState in belowList) {
            if (below.`is`(ModBlocks.GUIDE_RAIL_TUG.get()) && water.`is`(Blocks.WATER)) {
                val arrows: Direction = getArrowsDirection(below)
                this.setYRot(arrows.toYRot())
                val modifier: Double = 0.03
                this.setDeltaMovement(
                    getDeltaMovement().add(
                        Vec3(arrows.getStepX() * modifier, 0.0, arrows.getStepZ() * modifier)
                    )
                )
            }
        }
    }

    // todo: someone said you could prevent mobs from getting stuck on blocks by override this
    override fun customServerAiStep() {
        super.customServerAiStep()
    }

    private fun followPath() {
        pathfindCooldown--
        if (!path!!.isEmpty() && !this.isDocked && engineOn && !shouldFreezeTrain() && tickFuel()) {
            val stop: TugRouteNode = path!!.get(nextStop)
            if (navigation.getPath() == null || navigation.getPath()!!.isDone()
            ) {
                if (pathfindCooldown < 0 || navigation.getPath() != null) {  //only go on cooldown when the path was not completed
                    navigation.moveTo(stop.x, this.getY(), stop.z, 0.3)
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

    fun shouldFreezeTrain(): Boolean {
        return !enrollmentHandler.mayMove() || (stalling.isStalled() && !isDocked) || getLinkingHandler().train!!.asList()
            .stream().anyMatch(VesselEntity::isFrozen)
    }

    override fun defineSynchedData(pBuilder: SynchedEntityData.Builder) {
        super.defineSynchedData(pBuilder)
        entityData.set(INDEPENDENT_MOTION, false)
        entityData.set(OWNER, "")
    }


    fun setPath(path: TugRoute?) {
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
        getLinkingHandler().follower = (Optional.of(entity))
    }

    override fun setDominant(entity: VesselEntity) {
    }

    override fun removeDominated() {
        getLinkingHandler().follower = (Optional.empty())
        getLinkingHandler().train!!.tail = this
    }

    override fun hasOwner(): Boolean {
        return enrollmentHandler.hasOwner()
    }

    override fun removeDominant() {
    }

    override fun setTrain(train: Train<VesselEntity>) {
        getLinkingHandler().train = train
    }

    override fun getTrain(): Train<VesselEntity> {
        return getLinkingHandler().train!!
    }

    override fun remove(r: RemovalReason) {
        if (!level().isClientSide) {
            val stack: ItemStack = ItemStack(this.dropItem)
            if (this.hasCustomName()) {
                //TODO stack.setHoverName(this.getCustomName());
            }
            this.spawnAtLocation(stack)
            Containers.dropContents(this.level(), this, this)
            this.spawnAtLocation(routeItemHandler.getStackInSlot(0))
        }
        super.remove(r)
    }

    // Have to implement IInventory to work with hoppers
    override fun removeItem(p_70298_1_: Int, p_70298_2_: Int): ItemStack? {
        return null
    }

    override fun removeItemNoUpdate(p_70304_1_: Int): ItemStack? {
        return null
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

    override fun clearContent() {
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
            swimSpeedMult = computeSpeedMult()
        }

        return swimSpeedMult * super.swimSpeed()
    }

    private fun computeSpeedMult(): Double {
        var mult: Double = 1.0
        var doBreak: Boolean = false
        var i: Int = 0
        while (i < 10 && !doBreak) {
            for (direction: Direction in java.util.List.of(
                Direction.NORTH,
                Direction.EAST,
                Direction.WEST,
                Direction.SOUTH
            )) {
                val pos: BlockPos = getOnPos().relative(direction, i)
                if (!level().getFluidState(pos).isSource()) {
                    doBreak = true
                    break
                }
            }
            if (i > 3) {
                mult = 1 + ((i / 10f) * 1.8)
            }
            i++
        }
        if (mult < swimSpeedMult) return mult
        else return (mult + swimSpeedMult * 20) / 21
    }

    fun isEngineOn(): Boolean {
        return engineOn
    }

    override fun setEngineOn(engineOn: Boolean) {
        this.engineOn = engineOn
    }

    override fun getRouteItemHandler(): ItemStackHandler {
        return routeItemHandler
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
        getLinkingHandler().train = (Train(this))
        this.path = TugRoute()
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


        fun setCustomAttributes(): AttributeSupplier.Builder {
            return VesselEntity.setCustomAttributes()
                .add(Attributes.FOLLOW_RANGE, 200.0)
        }

        fun makeParticles(level: Level, pos: BlockPos, entity: Entity) {
            val random: RandomSource = level.getRandom()
            val h: Supplier<Boolean> = Supplier { random.nextDouble() < 0.5 }

            val dx: Double = (entity.getX() - entity.xOld) / 12.0
            val dy: Double = (entity.getY() - entity.yOld) / 12.0
            val dz: Double = (entity.getZ() - entity.zOld) / 12.0

            val xDrift: Double = (if (h.get()) 1 else -1) * random.nextDouble() * 2
            val zDrift: Double = (if (h.get()) 1 else -1) * random.nextDouble() * 2

            val particleType: SimpleParticleType =
                if (random.nextBoolean()) ParticleTypes.CAMPFIRE_SIGNAL_SMOKE else ParticleTypes.CAMPFIRE_COSY_SMOKE

            level.addAlwaysVisibleParticle(
                particleType,
                true,
                pos.getX()
                    .toDouble() + 0.5 + random.nextDouble() / 3.0 * (if (random.nextBoolean()) 1 else -1).toDouble(),
                pos.getY().toDouble() + random.nextDouble() + random.nextDouble(),
                pos.getZ()
                    .toDouble() + 0.5 + random.nextDouble() / 3.0 * (if (random.nextBoolean()) 1 else -1).toDouble(),
                0.007 * xDrift + dx, 0.05 + dy, 0.007 * zDrift + dz
            )
        }

    }
}
