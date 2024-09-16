package dev.murad.shipping.item

import dev.murad.shipping.setup.ModDataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack

object ItemStackUtil {

    fun getCompoundTag(stack: ItemStack): CompoundTag? {
        return stack.get(ModDataComponents.getCompoundTag())
    }

    fun getOrCreateTag(stack: ItemStack): CompoundTag {

        val componentTypeSupplier = ModDataComponents.getCompoundTag()
        if (!stack.has(componentTypeSupplier)) {
            val compoundTag = CompoundTag()
            stack.set(componentTypeSupplier, compoundTag)
            return compoundTag
        }
        return stack.get(componentTypeSupplier)!!
    }
}
