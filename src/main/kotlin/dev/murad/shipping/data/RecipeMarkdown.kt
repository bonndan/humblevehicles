package dev.murad.shipping.data

import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Ingredient.TagValue
import net.minecraft.world.item.crafting.ShapedRecipePattern

class RecipeMarkdown {

    private val stringBuilder = StringBuilder()
    private val icons = Icons()

    fun write(values: Collection<RecipeGraph.Node>) {

        stringBuilder.append("# Humble Vehicles Recipes\n\n")
        stringBuilder.append(" __all original Minecraft icons (c) 2020 Microsoft Corporation__\n\n")
        stringBuilder.append(" __other icons (c) Little Logistics__\n\n")
        values.forEach { recipe -> appendRecipe(recipe) }
    }

    private fun appendRecipe(recipe: RecipeGraph.Node) {

        val translated = Translations.getTranslationForId(recipe.recipeId.path)
        val ingredients = extractIngredients(recipe)

        ingredients.forEach {
            icons.copyIcon(it)
        }

        stringBuilder.append("\n## [${translated}](#${stripPrefixes(recipe.recipeId.toString())})\n")
        stringBuilder.append("\nRequires: ${extractRequirements(recipe)}\n")
        stringBuilder.append("\nType: ${recipe.recipe.type}\n")
        stringBuilder.append("\nIngredients: \n${ingredients.joinToString("") { "* $it\n" }}\n")

        recipe.pattern?.let { stringBuilder.append("\nPattern: ${extractPattern(it)}\n") }
    }

    private fun extractRequirements(recipe: RecipeGraph.Node) =
        recipe.requirements
            .filter { it.value != recipe.recipeId }
            .map { asLink(it.value.toString()) }
            .joinToString(" and ")

    private fun extractIngredients(recipe: RecipeGraph.Node): Set<String> {
        return recipe.recipe.ingredients
            .map { ingredientAsString(it) }
            .filter { it.isNotBlank() }
            .toSet()
    }

    private fun asLink(ingredient: String): String {

        if (ingredient.startsWith(MINECRAFT_PREFIX)) {
            val name = stripPrefixes(ingredient)
            return "[$name](https://minecraft.wiki/w/$name)"
        }

        if (ingredient.startsWith(MOD_PREFIX)) {
            val name = stripPrefixes(ingredient)
            return "[$name](#$name)"
        }

        return ingredient
    }

    private fun extractPattern(pattern: ShapedRecipePattern): String {

        val stringBuilder = StringBuilder()
        createTableHead(stringBuilder, pattern)

        var index = 0
        for (row in 0 until pattern.height()) {
            stringBuilder.append("|")
            for (column in 0 until pattern.width()) {
                val ingredient = pattern.ingredients()[index]
                index++
                stringBuilder.append(" " + ingredientAsIcon(ingredient) + " ")
                stringBuilder.append("|")
            }
            stringBuilder.append("\n")
        }

        return stringBuilder.toString()
    }

    private fun ingredientAsIcon(ingredient: Ingredient): String {

        val name = ingredientAsString(ingredient, link = false)
        val iconFile = stripPrefixes(name) + ".png"

        return "![$name](./$iconFile)"
    }

    private fun stripPrefixes(name: String) = name
        .replace(MINECRAFT_PREFIX, "")
        .replace(MOD_PREFIX, "")
        .replace(TAG_PREFIX, "")

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

    private fun ingredientAsString(ingredient: Ingredient, link: Boolean = true): String {
        return ingredient.values
            .map { value ->
                when (value) {
                    is TagValue -> value.tag.location.toString()
                    else -> value.items.first()?.item.toString()
                }
            }
            .filter { it.isNotBlank() }
            .joinToString(" and ") { if (link) asLink(it) else it }
    }

    fun toMarkdown(): CharSequence {
        return stringBuilder.toString()
    }
}

