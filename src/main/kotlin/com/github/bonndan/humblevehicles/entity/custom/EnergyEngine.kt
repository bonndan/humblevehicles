package com.github.bonndan.humblevehicles.entity.custom

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class EnergyEngine(saveStateCallback: SaveStateCallback) : Engine(saveStateCallback) {

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        return stack.item == Items.REDSTONE || stack.item == Items.REDSTONE_BLOCK
    }

    override fun calculateBurnTimeOfNextItem(stack: ItemStack): Int {

        if (stack.isEmpty) {
            return 0
        }

        if (stack.item == Items.REDSTONE_BLOCK) {
            return REDSTONE_BLOCK_BURN_TIME_TICKS
        }

        return REDSTONE_BURN_TIME_TICKS
    }

    companion object {
        const val REDSTONE_BURN_TIME_TICKS = 2000
        const val REDSTONE_BLOCK_BURN_TIME_TICKS = 20000 //same as lava bucket
    }
}