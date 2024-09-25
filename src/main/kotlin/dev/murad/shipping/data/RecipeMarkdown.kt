package dev.murad.shipping.data

import dev.murad.shipping.setup.ModItems
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.ShapedRecipePattern

class RecipeMarkdown {

    private val stringBuilder = StringBuilder()

    fun write(values: Collection<RecipeGraph.Node>) {

        stringBuilder.append("# Humble Vehicles Recipes\n\n")
        stringBuilder.append(" __ all original Minecraft icons (c) 2020 Microsoft Corporation __\n\n")
        stringBuilder.append(" __ other icons (c) Little Logistics __\n\n")
        values.forEach { recipe -> appendRecipe(recipe) }
    }

    private fun appendRecipe(recipe: RecipeGraph.Node) {

        ModItems.CHEST_CAR

        val translated = Translations.getTranslationForId(recipe.recipeId.path)
        val ingredients = extractIngredients(recipe)

        ingredients.forEach {
            Icons.copyIcon(it)
        }

        stringBuilder.append("\n## ${translated}\n")
        stringBuilder.append("\nRequires: ${extractRequirements(recipe)}\n")
        stringBuilder.append("\nType: ${recipe.recipe.type}\n")
        stringBuilder.append("\nIngredients: \n${ingredients.joinToString("") { "* $it\n" }}\n")

        recipe.pattern?.let { stringBuilder.append("\nPattern: ${extractPattern(it)}\n") }
    }

    private fun extractRequirements(recipe: RecipeGraph.Node) =
        recipe.requirements
            .filter { it.value != recipe.recipeId }
            .map { it.value }
            .joinToString(" and ")

    private fun extractIngredients(recipe: RecipeGraph.Node): Set<String> {
        return recipe.recipe.ingredients
            .map { ingredientAsString(it) }
            .toSet()
    }

    private fun extractPattern(pattern: ShapedRecipePattern): String {

        val stringBuilder = StringBuilder()
        createTableHead(stringBuilder, pattern)

        var index = 0
        for (row in 0 until pattern.height()) {
            stringBuilder.append("|")
            for (column in 0 until pattern.width()) {
                val ingredient = pattern.ingredients().get(index)
                index++
                stringBuilder.append(" " + ingredientAsIcon(ingredient) + " ")
                stringBuilder.append("|")
            }
            stringBuilder.append("\n")
        }

        return stringBuilder.toString()
    }

    private fun ingredientAsIcon(ingredient: Ingredient): String {
        val name = ingredientAsString(ingredient)
        val iconFile = name.replace("minecraft:", "") + ".png"
        return "![$name](./$iconFile)"
    }

    private fun createTableHead(stringBuilder: StringBuilder, pattern: ShapedRecipePattern) {
        stringBuilder.append("\n\n")
        for (row in 0 until 1) {
            stringBuilder.append("|")
            for (column in 0 until pattern.width()) {
                stringBuilder.append(" |")
            }
            stringBuilder.append("\n")
        }

        for (row in 0 until 1) {
            stringBuilder.append("|")
            for (column in 0 until pattern.width()) {
                stringBuilder.append(" --- |")
            }
            stringBuilder.append("\n")
        }
    }

    private fun ingredientAsString(ingredient: Ingredient): String {
        return ingredient.values
            .map { value -> value.items?.first()?.item.toString() }
            .filter { it.isNotBlank() }
            .joinToString(" and")
    }

    fun toMarkdown(): CharSequence {
        return stringBuilder.toString()
    }
}

