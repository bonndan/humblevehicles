package dev.murad.shipping.util

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.FurnaceBlockEntity
import net.neoforged.neoforge.items.ItemStackHandler

/**
 * Implementation of ItemStackHandler that doesn't change size when loaded from NBT.
 * Used in Steam and Locomotive tugs.
 */
class FuelItemStackHandler : ItemStackHandler(1) {
    /**
     * Consume an item of fuel
     * @return number of base ticks the fuel burns for. This will be multiplied by the fuel multiplier in config
     */
    fun tryConsumeFuel(): Int {
        val stack = getStackInSlot(0)
        val burnTime = stack.item.getBurnTime(stack, null)

        if (burnTime > 0) {
            // shrink the stack and replace with byproducts (if exists)
            val byproduct = stack.craftingRemainingItem
            stack.shrink(1)

            if (stack.isEmpty) {
                // replace stack with byproduct
                // if somehow a stackable item has a byproduct, then we should call the police
                setStackInSlot(0, byproduct)
            }
        }

        return burnTime
    }

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        return FurnaceBlockEntity.isFuel(stack)
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