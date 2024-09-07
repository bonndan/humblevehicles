package dev.murad.shipping.setup

import net.minecraft.world.item.Item
import net.minecraft.world.item.Items

object EntityItemMap {
    // leaving this public in case addon mods want to write here
    val ENTITY_CREATOR_ITEMS: MutableMap<String, Item?> = HashMap()

    private fun init() {
        ENTITY_CREATOR_ITEMS[ModEntityTypes.ENERGY_LOCOMOTIVE.get().toString()] = ModItems.ENERGY_LOCOMOTIVE.get()
        ENTITY_CREATOR_ITEMS[ModEntityTypes.STEAM_LOCOMOTIVE.get().toString()] = ModItems.STEAM_LOCOMOTIVE.get()
        ENTITY_CREATOR_ITEMS[ModEntityTypes.ENERGY_TUG.get().toString()] = ModItems.ENERGY_TUG.get()
        ENTITY_CREATOR_ITEMS[ModEntityTypes.STEAM_TUG.get().toString()] = ModItems.STEAM_TUG.get()
    }

    @JvmStatic
    fun get(entityType: String): Item? {
        if (ENTITY_CREATOR_ITEMS.isEmpty()) {
            init()
        }
        return ENTITY_CREATOR_ITEMS.getOrDefault(entityType, Items.MINECART)
    }
}
