package com.github.bonndan.humblevehicles.setup

import com.github.bonndan.humblevehicles.HumVeeMod
import com.github.bonndan.humblevehicles.item.SpringItem
import com.github.bonndan.humblevehicles.util.Route
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.resources.ResourceLocation

object ModItemModelProperties {
    fun register() {
        ItemProperties.register(
            ModItems.SPRING.get(),
            ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "springstate")
        ) { stack, world, entity, i: Int -> if (SpringItem.getState(stack) == SpringItem.State.READY) 0f else 1f }

        ItemProperties.register(
            ModItems.TUG_ROUTE.get(),
            ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "routestate")
        ) { stack, world, entity, i: Int -> if (Route.getRoute(stack).isEmpty()) 1f else 0f }

        ItemProperties.register(
            ModItems.LOCO_ROUTE.get(),
            ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "locoroutestate")
        ) { stack, world, entity, i -> if (Route.getRoute(stack).isEmpty()) 1f else 0f }
    }
}
