package dev.murad.shipping.network.client;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.ArrayList;
import java.util.List;

public class VehicleTrackerPacketHandler {


    public static List<EntityPosition> toRender = new ArrayList<>();
    public static String toRenderDimension = "";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {

        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playBidirectional(
                VehicleTrackerClientPacket.TYPE,
                VehicleTrackerClientPacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        VehicleTrackerPacketHandler::handleData,
                        VehicleTrackerPacketHandler::handleData
                )
        );

    }

    public static void handleData(VehicleTrackerClientPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            VehicleTrackerPacketHandler.toRender = packet.parse();
            VehicleTrackerPacketHandler.toRenderDimension = packet.dimension();
        });
    }

    public static void flush() {
        toRender.clear();
    }

    public static void sendToPlayer(VehicleTrackerClientPacket vehicleTrackerClientPacket, ServerPlayer serverPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer, vehicleTrackerClientPacket);
    }
}
