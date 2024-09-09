package dev.murad.shipping.item

import dev.murad.shipping.setup.ModDataComponents.TAG_PROPERTIES
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import java.util.*

object ItemStackUtil {
    fun getCompoundTag(stack: ItemStack): Optional<CompoundTag> {
        return Optional.ofNullable<CompoundTag?>(stack.components.get<CompoundTag>(TAG_PROPERTIES.get()))
    }

    fun contains(stack: ItemStack, pKey: String): Boolean {
        return getCompoundTag(stack).map { compoundTag: CompoundTag? ->
            compoundTag!!.contains(
                pKey
            )
        }.orElse(false)
    }
}
