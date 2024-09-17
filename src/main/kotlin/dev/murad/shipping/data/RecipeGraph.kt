package dev.murad.shipping.data

import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.InventoryChangeTrigger
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.ShapedRecipe
import net.minecraft.world.item.crafting.ShapedRecipePattern
import net.neoforged.neoforge.common.conditions.ICondition
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import kotlin.io.path.Path

class RecipeGraph(private val modRecipeProvider: ModRecipeProvider) : RecipeOutput {

    private val recipes: MutableMap<ResourceLocation, Node> = mutableMapOf()

    fun build() {
        modRecipeProvider.build(this)

        val recipeMarkdown = RecipeMarkdown()
        recipeMarkdown.write(recipes.values)

        Files.writeString(
            Path("../../recipes.md"),
            recipeMarkdown.toMarkdown(),
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE,
            StandardOpenOption.TRUNCATE_EXISTING,
        )
    }

    override fun accept(
        id: ResourceLocation,
        recipe: Recipe<*>,
        advancement: AdvancementHolder?,
        vararg conditions: ICondition?
    ) {
        val pattern: ShapedRecipePattern? = when (recipe) {
            is ShapedRecipe -> recipe.pattern
            else -> return
        }
        recipes[id] = Node(id, recipe, asRequirements(advancement?.value?.criteria), pattern)
    }

    private fun asRequirements(criteria: Map<String, Criterion<*>>?): Map<String, ResourceLocation> {

        if (criteria == null) {
            return mapOf()
        }

        return criteria.map { (k, v) -> k to asResourceLocation(k, v) }
            .toMap()
            .filterValues { it != null } as Map<String, ResourceLocation>
    }

    private fun asResourceLocation(k: String, v: Criterion<*>): ResourceLocation? {
        if (k == "has_item") {
            if (v.triggerInstance is InventoryChangeTrigger.TriggerInstance) {
                val items = (v.triggerInstance as InventoryChangeTrigger.TriggerInstance).items
                return items.get(0).items.map { it[0].key?.location() }.get()
            }
        }

        if (k == "has_the_recipe") {
            if (v.triggerInstance is RecipeUnlockedTrigger.TriggerInstance) {
                return (v.triggerInstance as RecipeUnlockedTrigger.TriggerInstance).recipe
            }
        }

        return null
    }

    override fun advancement(): Advancement.Builder {
        return Advancement.Builder.recipeAdvancement()
    }

    data class Node(
        val recipeId: ResourceLocation,
        val recipe: Recipe<*>,
        val requirements: Map<String, ResourceLocation>,
        val pattern: ShapedRecipePattern?
    )

}