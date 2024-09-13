package dev.murad.shipping.setup

import dev.murad.shipping.HumVeeMod
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item

class ModTags {
    class Blocks

    object Items {
        
        val WRENCHES: TagKey<Item> = forge("tools/wrench")

        private fun forge(path: String): TagKey<Item> {
            return TagKey.create(Registries.ITEM, ResourceLocation.tryBuild("forge", path))
        }

        private fun mod(path: String): TagKey<Item> {
            return TagKey.create(Registries.ITEM, ResourceLocation.tryBuild(HumVeeMod.MOD_ID, path))
        }
    }
}
