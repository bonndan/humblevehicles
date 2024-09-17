package dev.murad.shipping.network.client

import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.handling.IPayloadHandler
import java.util.ArrayList

object VehicleTrackerPacketHandler {

    var toRender: MutableList<EntityPosition> = ArrayList<EntityPosition>()
    var toRenderDimension: String = ""

    @SubscribeEvent
    fun register(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("1")
        registrar.playBidirectional(
            VehicleTrackerClientPacket.TYPE,
            VehicleTrackerClientPacket.STREAM_CODEC,
            DirectionalPayloadHandler(
                { obj, packet: IPayloadContext -> handleData(obj, packet) },
                { obj, packet: IPayloadContext -> handleData(obj, packet) }
            )
        )
    }

    private fun handleData(packet: VehicleTrackerClientPacket, ctx: IPayloadContext) {
        ctx.enqueueWork {
            toRender = packet.parse()
            toRenderDimension = packet.dimension
        }
    }

    fun flush() {
        toRender.clear()
    }

    fun sendToPlayer(vehicleTrackerClientPacket: VehicleTrackerClientPacket, serverPlayer: ServerPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer, vehicleTrackerClientPacket)
    }
}
