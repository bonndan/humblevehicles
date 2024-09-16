package dev.murad.shipping.setup

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item

class ModTags {

    object Items {
        
        val WRENCHES: TagKey<Item> = createTagKey("tools/wrench")

        private fun createTagKey(path: String): TagKey<Item> {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("forge", path))
        }
    }
}
