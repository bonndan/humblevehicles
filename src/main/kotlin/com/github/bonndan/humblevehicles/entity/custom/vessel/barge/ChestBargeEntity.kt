package com.github.bonndan.humblevehicles.entity.custom.vessel.barge

import com.github.bonndan.humblevehicles.entity.custom.TrainInventoryProvider
import com.github.bonndan.humblevehicles.setup.ModEntityTypes
import com.github.bonndan.humblevehicles.setup.ModItems
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.Container
import net.minecraft.world.Containers
import net.minecraft.world.MenuProvider
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.neoforged.neoforge.items.ItemStackHandler
import java.util.Optional
import java.util.stream.IntStream

class ChestBargeEntity : com.github.bonndan.humblevehicles.entity.custom.vessel.barge.AbstractBargeEntity, Container, MenuProvider, WorldlyContainer, TrainInventoryProvider {

    private val itemHandler: ItemStackHandler = ItemStackHandler(27)

    constructor(type: EntityType<out com.github.bonndan.humblevehicles.entity.custom.vessel.barge.ChestBargeEntity>, world: Level) : super(type, world)

    constructor(type: EntityType<out com.github.bonndan.humblevehicles.entity.custom.vessel.barge.ChestBargeEntity>, world: Level, x: Double, y: Double, z: Double) : super(
        type,
        world,
        x,
        y,
        z
    )

    override fun remove(r: RemovalReason) {
        if (!this.level().isClientSide) {
            Containers.dropContents(this.level(), this, this)
        }
        super.remove(r)
    }

    override fun getDropItem(): Item? {

        return if (this.type == ModEntityTypes.BARREL_BARGE.get()) {
            ModItems.BARREL_BARGE.get()
        } else {
            ModItems.CHEST_BARGE.get()
        }
    }

    override fun doInteract(player: Player?) {
        player?.openMenu(this)
    }

    override fun getContainerSize(): Int {
        return this.itemHandler.getSlots()
    }

    override fun isEmpty(): Boolean {
        return isEmpty(this.itemHandler)
    }

    override fun getItem(slot: Int): ItemStack {
        return this.itemHandler.getStackInSlot(slot)
    }

    override fun removeItem(slot: Int, count: Int): ItemStack {
        return itemHandler.extractItem(slot, count, false)
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        val itemstack = itemHandler.getStackInSlot(slot)
        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY
        } else {
            this.itemHandler.setStackInSlot(slot, ItemStack.EMPTY)
            return itemstack
        }
    }

    override fun setItem(slot: Int, stack: ItemStack) {
        itemHandler.setStackInSlot(slot, stack)
    }

    override fun setChanged() {
    }

    override fun stillValid(player: Player?): Boolean {
        if (this.isRemoved()) {
            return false
        } else {
            return !(player!!.distanceToSqr(this) > 64.0)
        }
    }

    override fun clearContent() {
    }

    override fun createMenu(pContainerId: Int, pInventory: Inventory?, pPlayer: Player): AbstractContainerMenu? {
        if (pPlayer.isSpectator()) {
            return null
        } else {
            return ChestMenu.threeRows(pContainerId, pInventory, this)
        }
    }

    override fun addAdditionalSaveData(tag: CompoundTag) {
        super.addAdditionalSaveData(tag)
        tag.put("Items", itemHandler.serializeNBT(registryAccess()))
    }

    override fun readAdditionalSaveData(tag: CompoundTag) {
        super.readAdditionalSaveData(tag)
        itemHandler.deserializeNBT(registryAccess(), tag.getCompound("Items"))
    }

    override fun getSlotsForFace(face: Direction): IntArray {
        return IntStream.range(0, getContainerSize()).toArray()
    }

    override fun canPlaceItemThroughFace(p_180462_1_: Int, item: ItemStack, p_180462_3_: Direction?): Boolean {
        return isDockable
    }

    override fun canTakeItemThroughFace(p_180461_1_: Int, item: ItemStack, p_180461_3_: Direction): Boolean {
        return isDockable
    }

    override fun getTrainInventoryHandler(): Optional<ItemStackHandler> {
        return Optional.of<ItemStackHandler>(itemHandler)
    }

    private fun isEmpty(itemHandler: ItemStackHandler): Boolean {
        for (i in 0 until itemHandler.slots) {
            if (!itemHandler.getStackInSlot(i).isEmpty && itemHandler.getStackInSlot(i).item != Items.AIR) {
                return false
            }
        }
        return true
    }
}
