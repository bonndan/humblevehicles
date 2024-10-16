package com.github.bonndan.humblevehicles.setup

import com.github.bonndan.humblevehicles.recipe.AbstractRouteCopyRecipe
import com.github.bonndan.humblevehicles.util.Route
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer
import java.util.function.Supplier

object ModRecipeSerializers {

    val TUG_ROUTE_COPY: Supplier<SimpleCraftingRecipeSerializer<AbstractRouteCopyRecipe>> =
        Registration.RECIPE_SERIALIZERS.register(
            "tug_route_copy",
            Supplier<SimpleCraftingRecipeSerializer<AbstractRouteCopyRecipe>> {
                SimpleCraftingRecipeSerializer { cat -> createTugRouteCopyRecipe(cat) }
            })

    private fun createTugRouteCopyRecipe(cat: CraftingBookCategory) =
        object : AbstractRouteCopyRecipe(cat, ModItems.TUG_ROUTE.get()) {

            override fun stackHasNodes(stack: ItemStack): Boolean {
                return !Route.getRoute(stack).isEmpty()
            }

            override fun getSerializer(): RecipeSerializer<*> {
                return TUG_ROUTE_COPY.get()
            }
        }

    val LOCO_ROUTE_COPY: Supplier<SimpleCraftingRecipeSerializer<AbstractRouteCopyRecipe>> =
        Registration.RECIPE_SERIALIZERS.register(
            "loco_route_copy",
            Supplier<SimpleCraftingRecipeSerializer<AbstractRouteCopyRecipe>> {
                SimpleCraftingRecipeSerializer<AbstractRouteCopyRecipe> { cat -> abstractLocoRouteCopyRecipe(cat) }
            })

    private fun abstractLocoRouteCopyRecipe(cat: CraftingBookCategory) =
        object : AbstractRouteCopyRecipe(cat, ModItems.LOCO_ROUTE.get()) {

            override fun stackHasNodes(stack: ItemStack): Boolean {
                return !Route.getRoute(stack).isEmpty()
            }

            override fun getSerializer(): RecipeSerializer<*> {
                return LOCO_ROUTE_COPY.get()
            }
        }

    fun register() {
    }
}
