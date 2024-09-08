package dev.murad.shipping.event

import dev.murad.shipping.ShippingConfig
import dev.murad.shipping.ShippingMod
import dev.murad.shipping.entity.custom.vessel.tug.VehicleFrontPart
import dev.murad.shipping.global.PlayerTrainChunkManager
import dev.murad.shipping.global.TrainChunkManagerManager
import dev.murad.shipping.item.SpringItem
import dev.murad.shipping.util.LinkableEntity
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ShearsItem
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.ICancellableEvent
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import net.neoforged.neoforge.event.tick.LevelTickEvent
import java.util.function.Consumer

/**
 * Forge-wide event bus
 */
@EventBusSubscriber(modid = ShippingMod.MOD_ID)
object ForgeEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun entityInteract(event: PlayerInteractEvent.EntityInteract) {
        handleEvent(event, event.target)
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun entitySpecificInteract(event: PlayerInteractEvent.EntityInteractSpecific) {
        handleEvent(event, event.target)
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onWorldTick(event: LevelTickEvent) {
        if (event is LevelTickEvent.Pre) {
            return
        }
        // Don't do anything client side
        if (event.level is ServerLevel ) {
            val server = event.level.getServer()
            if (server != null)
            TrainChunkManagerManager.get(server).getManagers(event.level.dimension()).forEach{ obj -> obj.tick() }
        }
    }

    @SubscribeEvent
    fun onPlayerSignInEvent(event: PlayerEvent.PlayerLoggedInEvent) {

        if (event.entity.level().isClientSide() || ShippingConfig.Server.OFFLINE_LOADING.get()) {
            return
        }

        TrainChunkManagerManager.get(event.entity.level().server!!)
            .getManagers(event.entity.uuid)
            .forEach(Consumer { obj: PlayerTrainChunkManager -> obj.activate() })
    }

    @SubscribeEvent
    fun onPlayerSignInEvent(event: PlayerEvent.PlayerLoggedOutEvent) {
        if (event.entity.level().isClientSide || ShippingConfig.Server.OFFLINE_LOADING.get()) {
            return
        }

        TrainChunkManagerManager.get(event.entity.level().server!!)
            .getManagers(event.entity.uuid)
            .forEach(Consumer { obj: PlayerTrainChunkManager -> obj.deactivate() })
    }

    private fun handleEvent(event: PlayerInteractEvent, target: Entity) {
        if (!event.itemStack.isEmpty) {
            val item = event.itemStack.item
            if (item is SpringItem) {
                if (target is LinkableEntity<*> || target is VehicleFrontPart) {
                    item.onUsedOnEntity(event.itemStack, event.entity, event.level, target)

                    (event as ICancellableEvent).isCanceled = true
                    //event.setCancellationResult(InteractionResult.SUCCESS);
                }
            }

            if (item is ShearsItem) {
                if (target is LinkableEntity<*>) {
                    target.handleShearsCut()
                    (event as ICancellableEvent).isCanceled = true
                    //event.setCancellationResult(InteractionResult.SUCCESS);
                }
            }
        }
    }
}
