package com.github.bonndan.humblevehicles.recipe

import com.mojang.datafixers.util.Pair
import net.minecraft.core.HolderLookup
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.CustomRecipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.Level
import java.util.*

abstract class AbstractRouteCopyRecipe(cat: CraftingBookCategory, private val item: Item) : CustomRecipe(cat) {

    abstract fun stackHasNodes(stack: ItemStack): Boolean

    abstract override fun getSerializer(): RecipeSerializer<*>

    /**
     * If hasNodes is set, return if stack has nodes,
     * otherwise return if stack is empty.
     */
    private fun isRouteWithNodes(stack: ItemStack, hasNodes: Boolean): Boolean {
        if (stack.item === item) {
            return !stackHasNodes(stack) xor hasNodes
        }
        return false
    }

    // returns a pair of <Filled Tug Route, Unfilled Tug Route>
    private fun checkTugRoutes(input: CraftingInput): Optional<Pair<ItemStack, Int>> {
        var i = 0
        var filledRoute = ItemStack.EMPTY

        for (j in 0 until input.size()) {
            val stack = input.getItem(j)
            if (!stack.isEmpty) {
                if (isRouteWithNodes(stack, true)) {
                    if (!filledRoute.isEmpty) {
                        // can't have 2 filled routes
                        return Optional.empty()
                    }

                    filledRoute = stack
                } else {
                    if (!isRouteWithNodes(stack, false)) {
                        return Optional.empty()
                    }

                    ++i
                }
            }
        }

        // if we have a filled route
        if (!filledRoute.isEmpty && i <= filledRoute.maxStackSize - 1) {
            return Optional.of(Pair(filledRoute, i))
        }

        return Optional.empty()
    }

    override fun matches(pInput: CraftingInput, pLevel: Level): Boolean {
        return checkTugRoutes(pInput).isPresent
    }

    override fun assemble(pInput: CraftingInput, pRegistries: HolderLookup.Provider): ItemStack {
        val matchOpt = checkTugRoutes(pInput)
        if (matchOpt.isEmpty) return ItemStack.EMPTY

        val match = matchOpt.get()
        val filled = match.first
        val num = match.second

        if (num == 0) {
            // clear!
            return ItemStack(item, 1)
        } else {
            // copy
            val output = filled.copy()
            output.count = num + 1
            return output
        }
    }

    override fun canCraftInDimensions(x: Int, y: Int): Boolean {
        return x * y >= 2
    }
}
