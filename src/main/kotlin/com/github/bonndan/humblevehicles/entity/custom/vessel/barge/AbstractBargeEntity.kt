package com.github.bonndan.humblevehicles.entity.custom.vessel.barge

import com.github.bonndan.humblevehicles.capability.StallingCapability
import com.github.bonndan.humblevehicles.entity.custom.Stalling
import com.github.bonndan.humblevehicles.entity.custom.vessel.VesselEntity
import com.github.bonndan.humblevehicles.entity.custom.vessel.tug.AbstractTugEntity
import com.github.bonndan.humblevehicles.util.Train
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.items.ItemStackHandler
import java.util.*

abstract class AbstractBargeEntity(type: EntityType<out AbstractBargeEntity>, world: Level) :
    VesselEntity(type, world), Stalling {

    constructor(
        type: EntityType<out AbstractBargeEntity>,
        worldIn: Level,
        x: Double,
        y: Double,
        z: Double
    ) : this(type, worldIn) {
        this.setPos(x, y, z)
        this.deltaMovement = Vec3.ZERO
        this.xo = x
        this.yo = y
        this.zo = z
    }

    init {
        this.blocksBuilding = true
        getLinkingHandler()?.train = Train(this)
    }

    override fun canAddPassenger(passenger: Entity): Boolean {
        return false
    }

    abstract override fun getDropItem(): Item?

    public override fun mobInteract(player: Player, hand: InteractionHand): InteractionResult {
        if (!level().isClientSide) {
            val color = DyeColor.getColor(player.getItemInHand(hand))

            if (color != null) {
                getEntityData().set(COLOR_DATA, color.id)
            } else {
                doInteract(player)
            }
        }

        // don't interact *and* use current item
        return InteractionResult.sidedSuccess(level().isClientSide)
    }

    protected abstract fun doInteract(player: Player?)

    override fun setDominated(entity: VesselEntity) {
        getLinkingHandler()?.follower = Optional.of(entity)
    }

    override fun setDominant(entity: VesselEntity) {
        this.setTrain(entity.getTrain())
        getLinkingHandler()?.leader = Optional.of(entity)
    }

    override fun removeDominated() {
        if (!this.isAlive) {
            return
        }
        getLinkingHandler()?.follower = Optional.empty()
        getLinkingHandler()?.train!!.tail = this
    }

    override fun removeDominant() {
        if (!this.isAlive) {
            return
        }
        getLinkingHandler()?.leader = Optional.empty()
        this.setTrain(Train(this))
    }

    override fun setTrain(train: Train<VesselEntity>) {
        getLinkingHandler()?.train = train
        train.tail = this
        getLinkingHandler()?.follower?.ifPresent { dominated: VesselEntity ->
            // avoid recursion loops
            if (dominated.getTrain() != train) {
                dominated.setTrain(train)
            }
        }
    }

    override fun getTrain(): Train<VesselEntity> {
        return getLinkingHandler()?.train!!
    }

    override fun remove(r: RemovalReason) {
        if (!level().isClientSide) {
            val stack = ItemStack(this.getDropItem())
            if (this.hasCustomName()) {
                //stack.setHoverName(this.getCustomName());
            }
            this.spawnAtLocation(stack)
        }
        super.remove(r)
    }

    val isDockable: Boolean
        // hack to disable hoppers
        get() = getLinkingHandler()?.leader?.map { dom: VesselEntity ->
            this.distanceToSqr(dom) < 1.1 }?.orElse(true) == true

    override fun allowDockInterface(): Boolean {
        return isDockable
    }

    override fun getStalling(): StallingCapability {
        return stalling
    }

    private val stalling: StallingCapability = object : StallingCapability {
        override fun isDocked(): Boolean {
            return delegate().map { obj: StallingCapability -> obj.isDocked() }.orElse(false)
        }

        override fun dock(x: Double, y: Double, z: Double) {
            delegate().ifPresent { s: StallingCapability -> s.dock(x, y, z) }
        }

        override fun undock() {
            delegate().ifPresent { obj: StallingCapability -> obj.undock() }
        }

        override fun isStalled(): Boolean {
            return delegate().map { obj: StallingCapability -> obj.isStalled() }.orElse(false)
        }

        override fun stall() {
            delegate().ifPresent { obj: StallingCapability -> obj.stall() }
        }

        override fun unstall() {
            delegate().ifPresent { obj: StallingCapability -> obj.unstall() }
        }

        override fun isFrozen(): Boolean {
            return super@AbstractBargeEntity.isFrozen
        }

        override fun freeze() {
            super@AbstractBargeEntity.isFrozen = true
        }

        override fun unfreeze() {
            super@AbstractBargeEntity.isFrozen = false
        }

        private fun delegate(): Optional<StallingCapability> {
            val head = getLinkingHandler()?.train!!.head
            if (head is AbstractTugEntity) {
                return Optional.ofNullable<StallingCapability>(head.getCapability(StallingCapability.STALLING_CAPABILITY))
            }
            return Optional.empty()
        }
    }

    fun moveItemStackIntoHandler(handler: ItemStackHandler, stack: ItemStack): ItemStack {
        var stack = stack
        val slots = handler.slots
        var i = 0
        while (i < slots && !stack.isEmpty) {
            stack = handler.insertItem(i, stack, false)
            i++
        }
        return stack
    }

}
