package dev.murad.shipping.setup

import dev.murad.shipping.HumVeeMod
import dev.murad.shipping.item.SpringItem
import dev.murad.shipping.util.LocoRoute
import dev.murad.shipping.util.TugRoute
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
        ) { stack, world, entity, i: Int ->
            if (TugRoute.getRoute(stack).isEmpty()) 1f else 0f
        }

        ItemProperties.register(
            ModItems.LOCO_ROUTE.get(),
            ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "locoroutestate")
        ) { stack, world, entity, i -> if (LocoRoute.getRoute(stack).isEmpty()) 1f else 0f }
    }
}
