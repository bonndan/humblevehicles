package dev.murad.shipping.event;

import dev.murad.shipping.ShippingConfig;
import dev.murad.shipping.ShippingMod;
import dev.murad.shipping.entity.custom.vessel.tug.VehicleFrontPart;
import dev.murad.shipping.global.PlayerTrainChunkManager;
import dev.murad.shipping.global.TrainChunkManagerManager;
import dev.murad.shipping.item.SpringItem;
import dev.murad.shipping.util.LinkableEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShearsItem;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;


/**
 * Forge-wide event bus
 */
@EventBusSubscriber(modid = ShippingMod.MOD_ID)
public class ForgeEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void entityInteract(PlayerInteractEvent.EntityInteract event) {
        handleEvent(event, event.getTarget());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void entitySpecificInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        handleEvent(event, event.getTarget());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onWorldTick(LevelTickEvent event) {
        if (event instanceof LevelTickEvent.Pre) {
            return;
        }
        // Don't do anything client side
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            TrainChunkManagerManager.get(serverLevel.getServer()).getManagers(event.getLevel().dimension()).forEach(PlayerTrainChunkManager::tick);
        }
    }

    @SubscribeEvent
    public static void onPlayerSignInEvent(PlayerEvent.PlayerLoggedInEvent event){
        if (event.getEntity().level().isClientSide() || ShippingConfig.Server.OFFLINE_LOADING.get()) {
            return;
        }

        TrainChunkManagerManager.get(event.getEntity().level().getServer())
                .getManagers(event.getEntity().getUUID())
                .forEach(PlayerTrainChunkManager::activate);
    }

    @SubscribeEvent
    public static void onPlayerSignInEvent(PlayerEvent.PlayerLoggedOutEvent event){
        if (event.getEntity().level().isClientSide || ShippingConfig.Server.OFFLINE_LOADING.get()) {
            return;
        }

        TrainChunkManagerManager.get(event.getEntity().level().getServer())
                .getManagers(event.getEntity().getUUID())
                .forEach(PlayerTrainChunkManager::deactivate);
    }

    private static void handleEvent(PlayerInteractEvent event, Entity target) {
        if(!event.getItemStack().isEmpty()) {
            Item item = event.getItemStack().getItem();
            if(item instanceof SpringItem springItem) {
                if(target instanceof LinkableEntity || target instanceof VehicleFrontPart) {
                    springItem.onUsedOnEntity(event.getItemStack(), event.getEntity(), event.getLevel(), target);

                    ((ICancellableEvent)event).setCanceled(true);
                    //event.setCancellationResult(InteractionResult.SUCCESS);
                }
            }

            if(item instanceof ShearsItem) {
                if(target instanceof LinkableEntity v) {
                    v.handleShearsCut();
                    ((ICancellableEvent)event).setCanceled(true);
                    //event.setCancellationResult(InteractionResult.SUCCESS);
                }
            }
        }
    }
}
