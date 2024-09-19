package dev.murad.shipping.network

import dev.murad.shipping.setup.ModItems
import dev.murad.shipping.util.TugRoute
import net.minecraft.world.InteractionHand
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object TugRoutePacketHandler {

    private val LOGGER: Logger = LogManager.getLogger(TugRoutePacketHandler::class.java)

    @SubscribeEvent
    fun on(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("1")
        registrar.playBidirectional<SetRouteTagPacket>(
            SetRouteTagPacket.TYPE,
            SetRouteTagPacket.STREAM_CODEC,
            DirectionalPayloadHandler<SetRouteTagPacket>(
                { obj, operation -> handleSetTag(obj, operation) },
                { obj, operation -> handleSetTag(obj, operation) }
            )
        )
    }


    fun handleSetTag(operation: SetRouteTagPacket, ctx: IPayloadContext) {
        ctx.enqueueWork(Runnable {
            val player = ctx.player()
            if (player == null) {
                LOGGER.error("Received packet not from player, dropping packet")
                return@Runnable
            }

            val heldStack =
                player.getItemInHand(if (operation.isOffhand()) InteractionHand.OFF_HAND else InteractionHand.MAIN_HAND)
            LOGGER.info("Item in hand is {}", heldStack)
            if (heldStack.getItem() !== ModItems.TUG_ROUTE.get()) {
                LOGGER.error("Item held in hand was not tug_route item, perhaps client has de-synced? Dropping packet")
                return@Runnable
            }

            val routeTag = operation.getTag()
            LOGGER.info(routeTag)
            val tugRoute = TugRoute.fromNBT(routeTag!!)
            tugRoute.save(heldStack)
        })
    }

    fun sendToServer(setRouteTagPacket: SetRouteTagPacket) {
        PacketDistributor.sendToServer(setRouteTagPacket)
    }
}
