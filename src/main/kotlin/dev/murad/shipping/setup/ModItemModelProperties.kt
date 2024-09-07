package dev.murad.shipping.setup

import dev.murad.shipping.ShippingMod
import dev.murad.shipping.item.LocoRouteItem
import dev.murad.shipping.item.SpringItem
import dev.murad.shipping.item.TugRouteItem
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

object ModItemModelProperties {
    fun register() {
        ItemProperties.register(
            ModItems.SPRING.get(),
            ResourceLocation.fromNamespaceAndPath(ShippingMod.MOD_ID, "springstate")
        ) { stack, world, entity, i: Int -> if (SpringItem.getState(stack) == SpringItem.State.READY) 0f else 1f }

        ItemProperties.register(
            ModItems.TUG_ROUTE.get(),
            ResourceLocation.fromNamespaceAndPath(ShippingMod.MOD_ID, "routestate")
        ) { stack: ItemStack?, world: ClientLevel?, entity: LivingEntity?, i: Int ->
            if (TugRouteItem.getRoute(stack).isEmpty()) 1f else 0f
        }

        ItemProperties.register(
            ModItems.LOCO_ROUTE.get(),
            ResourceLocation.fromNamespaceAndPath(ShippingMod.MOD_ID, "locoroutestate")
        ) { stack, world, entity, i -> if (LocoRouteItem.getRoute(stack).isEmpty()) 1f else 0f }
    }
}
