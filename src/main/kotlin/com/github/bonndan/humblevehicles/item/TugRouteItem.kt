package com.github.bonndan.humblevehicles.item

import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import kotlin.math.floor

class TugRouteItem(properties: Properties) : RouteItem(properties) {

    override fun use(world: Level, player: Player, hand: InteractionHand): InteractionResult{

        val itemstack = player.getItemInHand(hand)

        if (player.level().isClientSide) {
            return InteractionResult.PASS
        }

        if (player.isShiftKeyDown) {
            player.openMenu(createContainerProvider(hand), getDataAccessor(player, hand)::write)
            return InteractionResult.PASS
        }

        val route = getRoute(itemstack)

        val x = floor(player.x).toInt()
        val z = floor(player.z).toInt()
        if (!tryRemoveSpecific(route, x, z)) {
            player.displayClientMessage(
                Component.translatable("item.humblevehicles.tug_route.added", x, z), false
            )
            pushRoute(route, x, 0, z)
        } else {
            player.displayClientMessage(
                Component.translatable("item.humblevehicles.tug_route.removed", x, z), false
            )
        }

        route.save(itemstack)
        updateOnClient(route, hand, player as ServerPlayer)
        return InteractionResult.PASS
    }
}
