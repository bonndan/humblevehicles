package dev.murad.shipping.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.FileInputStream

@OptIn(ExperimentalSerializationApi::class)
object Translations {

    private val values: Map<String, String>
    private const val itemPrefix = "item.humblevehicles."
    private const val blockPrefix = "block.humblevehicles."

    init {
        val stream = FileInputStream("../../src/main/resources/assets/humblevehicles/lang/en_us.json")
        values = Json.decodeFromStream<Map<String, String>>(stream)
    }

    fun getTranslationForId(path: String): Translation {

        if (values.containsKey(itemPrefix + path)) {
            return Translation("item", values[itemPrefix + path]!!)
        }

        if (values.containsKey(blockPrefix + path)) {
            return Translation("block", values[blockPrefix + path]!!)
        }

        return Translation(null, path)
    }

    data class Translation(val type: String?, val text: String) {
        override fun toString(): String = text
    }
}