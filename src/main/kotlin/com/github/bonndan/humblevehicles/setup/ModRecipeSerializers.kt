package com.github.bonndan.humblevehicles.setup

import com.github.bonndan.humblevehicles.recipe.AbstractRouteCopyRecipe
import com.github.bonndan.humblevehicles.util.Route
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.CustomRecipe
import net.minecraft.world.item.crafting.RecipeSerializer
import java.util.function.Supplier

object ModRecipeSerializers {

    val LOCO_ROUTE_COPY: Supplier<RecipeSerializer<AbstractRouteCopyRecipe>> =
        Registration.RECIPE_SERIALIZERS.register(
            "loco_route_copy",
            Supplier<RecipeSerializer<AbstractRouteCopyRecipe>> {
                CustomRecipe.Serializer<AbstractRouteCopyRecipe>{ abstractLocoRouteCopyRecipe(it)}
            })

    private fun abstractLocoRouteCopyRecipe(cat: CraftingBookCategory) =
        object : AbstractRouteCopyRecipe(cat, ModItems.LOCO_ROUTE.get()) {

            override fun stackHasNodes(stack: ItemStack): Boolean {
                return !Route.getRoute(stack).isEmpty()
            }

            override fun getSerializer(): RecipeSerializer<AbstractRouteCopyRecipe> {
                return LOCO_ROUTE_COPY.get()
            }
        }

    fun register() {
    }
}
