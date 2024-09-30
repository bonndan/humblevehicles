package dev.murad.shipping.entity.custom

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class EnergyEngine : Engine(1.0) {

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        return stack.item == Items.REDSTONE
    }

    override fun calculateBurnTimeOfNextItem(stack: ItemStack): Int {

        if (stack.isEmpty) {
            return 0
        }

        return REDSTONE_BURN_TIME
    }

    companion object {
        const val REDSTONE_BURN_TIME = 300
    }
}