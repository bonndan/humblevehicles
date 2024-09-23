package dev.murad.shipping.network

import dev.murad.shipping.setup.ModItems
import dev.murad.shipping.util.Route
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.Item
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object RoutePacketHandler {

    private val LOGGER: Logger = LogManager.getLogger(RoutePacketHandler::class.java)

    @SubscribeEvent
    fun on(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("1")
        registrar.playBidirectional(
            SetRouteTagPacket.TYPE,
            SetRouteTagPacket.STREAM_CODEC,
            DirectionalPayloadHandler(
                { obj, operation -> handleSetTag(obj, operation) },
                { obj, operation -> handleSetTag(obj, operation) }
            )
        )
    }


    private fun handleSetTag(operation: SetRouteTagPacket, ctx: IPayloadContext) {

        ctx.enqueueWork(Runnable {
            val player = ctx.player()
            if (player == null) {
                LOGGER.error("Received packet not from player, dropping packet")
                return@Runnable
            }

            val interactionHand = if (operation.isOffhand()) InteractionHand.OFF_HAND else InteractionHand.MAIN_HAND
            val heldStack = player.getItemInHand(interactionHand)
            if (!itemInHandIsRouteItem(heldStack.item)) {
                return@Runnable
            }

            val routeTag = operation.getTag()
            val tugRoute = Route.fromNBT(routeTag)
            tugRoute.save(heldStack)

        }).exceptionally { e ->
            // Handle exception
            ctx.disconnect(Component.translatable("my_mod.networking.failed", e.message));
            null
        }
    }

    private fun itemInHandIsRouteItem(item: Item): Boolean {

        LOGGER.info("Item in hand is {}", item)

        if (item !== ModItems.TUG_ROUTE.get() && item !== ModItems.LOCO_ROUTE.get()) {
            LOGGER.error("Item held in hand was not tug_route item, perhaps client has de-synced? Dropping packet")
            return false
        }

        return true
    }

    fun sendToServer(setRouteTagPacket: SetRouteTagPacket) {
        PacketDistributor.sendToServer(setRouteTagPacket)
    }

    fun sendToClient(player: ServerPlayer, setRouteTagPacket: SetRouteTagPacket) {
        PacketDistributor.sendToPlayer(player, setRouteTagPacket)
    }

}
