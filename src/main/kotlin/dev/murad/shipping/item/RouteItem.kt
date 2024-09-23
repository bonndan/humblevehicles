package dev.murad.shipping.item

import dev.murad.shipping.entity.accessor.TugRouteScreenDataAccessor
import dev.murad.shipping.network.SetRouteTagPacket
import dev.murad.shipping.network.TugRoutePacketHandler
import dev.murad.shipping.util.Route
import dev.murad.shipping.util.RouteNode
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag

abstract class RouteItem(properties: Properties) : Item(properties) {

    fun getDataAccessor(entity: Player, hand: InteractionHand): TugRouteScreenDataAccessor {
        return TugRouteScreenDataAccessor.Builder(entity.id)
            .withOffHand(hand == InteractionHand.OFF_HAND)
            .build()
    }

    override fun appendHoverText(
        pStack: ItemStack,
        pContext: TooltipContext,
        tooltip: MutableList<Component>,
        pTooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(pStack, pContext, tooltip, pTooltipFlag)
        tooltip.add(Component.translatable("item.humblevehicles.tug_route.description"))
        tooltip.add(
            Component.translatable("item.humblevehicles.tug_route.num_nodes", getRoute(pStack).size).setStyle(
                Style.EMPTY.withColor(ChatFormatting.YELLOW)
            )
        )
    }

     fun getRoute(itemStack: ItemStack): Route {
        return Route.getRoute(itemStack)
     }


    fun tryRemoveSpecific(route: Route, x: Int, z: Int): Boolean {
        if (route.size == 0) {
            return false
        }
        val removed = route.removeIf { v -> v.x == x && v.z == z }
        return removed
    }

    fun pushRoute(route: Route, x: Int, y: Int, z: Int) {
        route.add(RouteNode(null, x, y, z))

    }

    fun updateOnClient(route: Route, hand: InteractionHand, player: ServerPlayer) {
        val packet = SetRouteTagPacket(route.hashCode(), hand == InteractionHand.OFF_HAND, route.toNBT())
        TugRoutePacketHandler.sendToClient(player, packet)
    }
}
