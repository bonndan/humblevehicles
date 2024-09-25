package dev.murad.shipping.data

import com.google.common.io.Files
import java.io.File
import java.util.zip.ZipFile

private const val MINECRAFT_PREFIX = "minecraft:"

object Icons {

    private val itemBasePath =
        "../../.gradle/repositories/ng_dummy_ng/net/minecraft/client/1.21.1/client-1.21.1-client-extra.jar!/assets/minecraft/textures/item/"

    private val jarPath = "/assets/minecraft/textures/item/"
    private val targetBasePath = "../../recipes/"

    fun copyIcon(name: String) {
        if (name.startsWith(MINECRAFT_PREFIX)) {
            copyMinecraftIcon(name)
        }
    }

    private fun copyMinecraftIcon(name: String) {

        val filename = name.replace(MINECRAFT_PREFIX, "")
        val zip =
            ZipFile("../../.gradle/repositories/ng_dummy_ng/net/minecraft/client/1.21.1/client-1.21.1-client-extra.jar")
        try {
            val entry = zip.getEntry("assets/minecraft/textures/item/$filename.png")
            if (entry != null) {
                val inputStream = zip.getInputStream(entry)
                val dest = File("$targetBasePath$filename.png")

                Files.write(inputStream.readAllBytes(), dest)
            }

        } catch (e: Exception) {
            println(e.message)
        }

    }
}