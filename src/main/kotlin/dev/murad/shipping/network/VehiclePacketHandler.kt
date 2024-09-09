package dev.murad.shipping.network

import dev.murad.shipping.HumVeeMod
import dev.murad.shipping.entity.custom.HeadVehicle
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.handling.IPayloadHandler
import java.util.Optional
import java.util.function.Consumer
import java.util.function.Function

object VehiclePacketHandler {



    @SubscribeEvent
    fun register(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("1")
        registrar.playBidirectional<SetEnginePacket>(
            SetEnginePacket.TYPE,
            SetEnginePacket.STREAM_CODEC,
            DirectionalPayloadHandler<SetEnginePacket>(
                IPayloadHandler { obj, operation -> handleSetEngine(obj, operation) },
                IPayloadHandler { obj, operation -> handleSetEngine(obj, operation) }
            )
        )
        registrar.playBidirectional<EnrollVehiclePacket>(
            EnrollVehiclePacket.TYPE,
            EnrollVehiclePacket.STREAM_CODEC,
            DirectionalPayloadHandler<EnrollVehiclePacket>(
                IPayloadHandler { obj, operation -> handleEnrollVehicle(obj, operation) },
                IPayloadHandler { obj, operation -> handleEnrollVehicle(obj, operation) }
            )
        )
    }

    fun send(payload: CustomPacketPayload) {
        PacketDistributor.sendToServer(payload)
    }

    fun handleSetEngine(operation: SetEnginePacket, ctx: IPayloadContext) {
        ctx.enqueueWork(
            Runnable {
                Optional.of<IPayloadContext>(ctx).map<Player?>(Function { obj: IPayloadContext? -> obj!!.player() })
                    .ifPresent(Consumer { serverPlayer: Player? ->
                        val loco = serverPlayer!!.level().getEntity(operation.locoId)
                        if (loco != null && loco.distanceTo(serverPlayer) < 6 && loco is HeadVehicle) {
                            loco.setEngineOn(operation.state)
                        }
                    })
            }
        )
    }

    fun handleEnrollVehicle(operation: EnrollVehiclePacket, ctx: IPayloadContext) {
        ctx.enqueueWork(
            Runnable {
                Optional.of<IPayloadContext?>(ctx).map<Player?>(Function { obj: IPayloadContext? -> obj!!.player() })
                    .ifPresent(Consumer { serverPlayer: Player? ->
                        val loco = serverPlayer!!.level().getEntity(operation.locoId)
                        if (loco != null && loco.distanceTo(serverPlayer) < 6 && loco is HeadVehicle) {
                            loco.enroll(serverPlayer.getUUID())
                        }
                    })
            }
        )
    }
}
