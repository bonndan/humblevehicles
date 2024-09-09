package dev.murad.shipping.network;

import dev.murad.shipping.HumVeeMod;
import dev.murad.shipping.entity.custom.HeadVehicle;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.Optional;

public final class VehiclePacketHandler {

    public static final ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "locomotive_channel");


    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {

        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playBidirectional(
                SetEnginePacket.TYPE,
                SetEnginePacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        VehiclePacketHandler::handleSetEngine,
                        VehiclePacketHandler::handleSetEngine
                )
        );
        registrar.playBidirectional(
                EnrollVehiclePacket.TYPE,
                EnrollVehiclePacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        VehiclePacketHandler::handleEnrollVehicle,
                        VehiclePacketHandler::handleEnrollVehicle
                )
        );
    }

    public static void send(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }


    public static void handleSetEngine(SetEnginePacket operation, IPayloadContext ctx) {
        ctx.enqueueWork(
                () -> Optional.of(ctx).map(IPayloadContext::player).ifPresent(serverPlayer -> {
                    var loco = serverPlayer.level().getEntity(operation.locoId());
                    if (loco != null && loco.distanceTo(serverPlayer) < 6 && loco instanceof HeadVehicle l) {
                        l.setEngineOn(operation.state());
                    }
                })
        );

    }

    public static void handleEnrollVehicle(EnrollVehiclePacket operation, IPayloadContext ctx) {
        ctx.enqueueWork(
                () -> Optional.of(ctx).map(IPayloadContext::player).ifPresent(serverPlayer -> {
                    var loco = serverPlayer.level().getEntity(operation.locoId());
                    if (loco != null && loco.distanceTo(serverPlayer) < 6 && loco instanceof HeadVehicle l) {
                        l.enroll(serverPlayer.getUUID());
                    }
                })
        );

    }
}
