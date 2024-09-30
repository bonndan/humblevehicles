package dev.murad.shipping.entity.custom

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.neoforged.neoforge.items.ItemStackHandler

class EnergyItemStackHandler : ItemStackHandler(1) {

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        return stack.item == Items.REDSTONE
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {

        if (!isItemValid(slot, stack)) {
            return stack
        }

        return super.insertItem(slot, stack, simulate)
    }

    override fun serializeNBT(lookup: HolderLookup.Provider): CompoundTag {
        val tag = super.serializeNBT(lookup)
        tag.remove("Size")
        return tag
    }

    override fun deserializeNBT(lookup: HolderLookup.Provider, nbt: CompoundTag) {
        nbt.remove("Size")
        super.deserializeNBT(lookup, nbt)
    }
}