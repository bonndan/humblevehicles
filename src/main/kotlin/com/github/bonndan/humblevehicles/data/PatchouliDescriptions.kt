package com.github.bonndan.humblevehicles.data

import com.google.gson.Gson
import java.io.File
import java.io.FileReader

object PatchouliDescriptions {

    private val modBasePath = "../../src/main/resources/assets/humblevehicles/patchouli_books/guide/en_us/entries/"
    private val linkRegex = "\\\$\\([^)]*\\)".toRegex()

    fun getDescriptions(name: String): List<String> {

        val file = File("$modBasePath$name.json")
        if (!file.exists()) {
            println("$file does not exist, cannot extract descriptions.")
            return listOf()
        }

        val patchouliRecipe = Gson().fromJson(FileReader(file), PatchouliRecipe::class.java)
        return patchouliRecipe.pages.map { removeLinks(it) }
    }

    private fun removeLinks(it: PatchouliRecipe.Page) =
        it.text.replace(linkRegex, "")

    class PatchouliRecipe {
        var name: String = ""
        var icon: String = ""
        var category: String = ""
        var pages: List<Page> = listOf()

        class Page {
            var type: Type? = null
            var entity: String? = null
            var recipe: String? = null
            var scale: Double = 0.0
            var offset: Double = 0.0
            var text: String = ""
        }

        enum class Type(s: String) {
            ENTITY("patchouli:entity"),
            CRAFTING("patchouli:crafting")
        }
    }
}