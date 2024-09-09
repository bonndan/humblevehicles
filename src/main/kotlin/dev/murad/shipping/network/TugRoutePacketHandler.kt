package dev.murad.shipping.network;

import dev.murad.shipping.item.TugRouteItem;
import dev.murad.shipping.setup.ModItems;
import dev.murad.shipping.util.TugRoute;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class TugRoutePacketHandler {

    private static final Logger LOGGER = LogManager.getLogger(TugRoutePacketHandler.class);


    @SubscribeEvent
    public static void on(final RegisterPayloadHandlersEvent event) {

        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playBidirectional(
                SetRouteTagPacket.TYPE,
                SetRouteTagPacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        TugRoutePacketHandler::handleSetTag,
                        TugRoutePacketHandler::handleSetTag
                )
        );
    }


    public static void handleSetTag(SetRouteTagPacket operation, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var player = ctx.player();
            if (player == null) {
                LOGGER.error("Received packet not from player, dropping packet");
                return;
            }

            ItemStack heldStack = player.getItemInHand(operation.isOffhand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
            LOGGER.info("Item in hand is {}", heldStack);
            if (heldStack.getItem() != ModItems.TUG_ROUTE.get()) {
                LOGGER.error("Item held in hand was not tug_route item, perhaps client has de-synced? Dropping packet");
                return;
            }

            CompoundTag routeTag = operation.tag;
            LOGGER.info(routeTag);
            TugRouteItem.saveRoute(TugRoute.fromNBT(routeTag), heldStack);
        });

    }

    public static void sendToServer(SetRouteTagPacket setRouteTagPacket) {
        PacketDistributor.sendToServer(setRouteTagPacket);
    }
}
