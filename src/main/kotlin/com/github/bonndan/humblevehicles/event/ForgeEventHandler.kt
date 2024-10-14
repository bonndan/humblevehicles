package com.github.bonndan.humblevehicles.event

import com.github.bonndan.humblevehicles.ShippingConfig
import com.github.bonndan.humblevehicles.HumVeeMod
import com.github.bonndan.humblevehicles.entity.custom.vessel.tug.VehicleFrontPart
import com.github.bonndan.humblevehicles.global.PlayerTrainChunkManager
import com.github.bonndan.humblevehicles.global.TrainChunkManagerManager
import com.github.bonndan.humblevehicles.item.SpringItem
import com.github.bonndan.humblevehicles.util.LinkableEntity
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ShearsItem
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.ICancellableEvent
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific
import net.neoforged.neoforge.event.tick.LevelTickEvent
import java.util.function.Consumer

/**
 * Forge-wide event bus
 */
@EventBusSubscriber(modid = HumVeeMod.MOD_ID)
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
    fun onWorldTick(event: LevelTickEvent.Post) {

        // Don't do anything client side
        if (event.level is ServerLevel) {
            val server = event.level.getServer()
            if (server != null) {
                TrainChunkManagerManager.get(server)
                    .getManagers(event.level.dimension())
                    .forEach { obj -> obj.tick() }
            }
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

        if (event.itemStack.isEmpty) return

        val item = event.itemStack.item
        if (item is SpringItem) {
            if (target is LinkableEntity<*> || target is VehicleFrontPart) {
                item.onUsedOnEntity(event.itemStack, event.entity, event.level, target)

                (event as ICancellableEvent).isCanceled = true
                if (event is EntityInteractSpecific) {
                    event.cancellationResult = (InteractionResult.SUCCESS)
                }
            }
        }

        if (item is ShearsItem) {
            if (target is LinkableEntity<*>) {
                target.handleShearsCut()
                (event as ICancellableEvent).isCanceled = true
                if (event is EntityInteractSpecific) {
                    event.cancellationResult = (InteractionResult.SUCCESS)
                }
            }
        }
    }
}
