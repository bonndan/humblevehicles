package com.github.bonndan.humblevehicles.entity.custom

import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.FurnaceBlockEntity


class FueledEngine(saveStateCallback: SaveStateCallback) : Engine(saveStateCallback) {

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