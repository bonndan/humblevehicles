package dev.murad.shipping.entity.custom

import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.FurnaceBlockEntity


class FueledEngine(fuelMultiplier: Double = 1.0) : Engine(fuelMultiplier) {

    override fun calculateBurnTimeOfNextItem(stack: ItemStack): Int {

        if (stack.isEmpty) {
            return 0
        }

        return stack.item.getBurnTime(stack, null)
    }

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        return FurnaceBlockEntity.isFuel(stack)
    }
}