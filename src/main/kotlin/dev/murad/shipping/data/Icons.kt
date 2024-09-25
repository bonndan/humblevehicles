package dev.murad.shipping.data

import com.google.common.io.Files
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

const val MINECRAFT_PREFIX = "minecraft:"
const val MOD_PREFIX = "humblevehicles:"
const val TAG_PREFIX = "c:"

class Icons(
    private val minecraftJar: String = "../../.gradle/repositories/ng_dummy_ng/net/minecraft/client/1.21.1/client-1.21.1-client-extra.jar"
) {

    private val modBasePath = "../../src/main/resources/assets/humblevehicles/textures"
    private val texturesBasePath = "assets/minecraft/textures"
    private val targetBasePath = "../../recipes/"
    private val found = mutableListOf<String>()

    fun copyIcon(name: String) {

        if (found.contains(name)) return

        when {
            name.startsWith(MINECRAFT_PREFIX) -> copyMinecraftIcon(name)
            name.startsWith(MOD_PREFIX) -> copyModIcon(name)
            name.startsWith(TAG_PREFIX) -> copyTagIcon(name)
        }
    }

    private fun copyTagIcon(name: String) {

        //TODO this is blind guessing, use TagKey or else
        val singular = name.replace(TAG_PREFIX, "").trimEnd('s')

        readFromJar(singular)?.let {
            val destName = name.replace(TAG_PREFIX, "")
            val dest = File("$targetBasePath$destName.png")
            Files.write(it, dest)
            found.add(name)
        }
    }

    private fun copyModIcon(name: String) {

        val filename = name.replace(MOD_PREFIX, "")
        val source = findModIcon(filename)
        val dest = File("$targetBasePath$filename.png")

        try {
            source?.let { Files.copy(it, dest) }
            found.add(name)
        } catch (e: Exception) {
            println(e.message)
        }
    }

    private fun copyMinecraftIcon(
        name: String,
        filename: String = name.replace(MINECRAFT_PREFIX, "")
    ) {

        val dest = File("$targetBasePath$filename.png")
        try {
            readFromJar(filename)?.let {
                Files.write(it, dest)
                found.add(name)
            }
        } catch (e: Exception) {
            println(e.message)
        }

    }

    private fun readFromJar(filename: String): ByteArray? {

        val zip = ZipFile(minecraftJar)
        try {
            val entry = findEntry(zip, filename)
            if (entry != null) {
                return zip.getInputStream(entry).readAllBytes()
            }

        } catch (e: Exception) {
            println(e.message)
        } finally {
            zip.close()
        }

        return null
    }

    private fun findEntry(zip: ZipFile, filename: String): ZipEntry? {

        var entry = zip.getEntry("$texturesBasePath/item/$filename.png")
        if (entry != null) {
            return entry
        }

        entry = zip.getEntry("$texturesBasePath/block/$filename.png")
        if (entry != null) {
            return entry
        }

        entry = zip.getEntry("$texturesBasePath/block/${filename}_side.png")
        if (entry != null) {
            return entry
        }

        return null
    }

    private fun findModIcon(filename: String): File? {

        var source = File("$modBasePath/block/$filename.png")

        if (source.exists()) {
            return source
        }

        source = File("$modBasePath/item/$filename.png")

        if (source.exists()) {
            return source
        }

        return null
    }
}