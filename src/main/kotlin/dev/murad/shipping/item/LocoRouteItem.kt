package dev.murad.shipping.item

import dev.murad.shipping.util.Route
import dev.murad.shipping.util.RouteNode
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseRailBlock

class LocoRouteItem(properties: Properties) : RouteItem(properties) {

    override fun useOn(pContext: UseOnContext): InteractionResult {

        if (pContext.level.isClientSide) {
            return InteractionResult.SUCCESS
        }

        // item used on block
        val stack = pContext.itemInHand
        if (stack.item === this) {
            val target = pContext.clickedPos
            val route = getRoute(stack)
            val player = pContext.player

            // target block
            val targetBlock = pContext.level.getBlockState(target).block
            val shouldCheckAboveOnRemove = targetBlock !is BaseRailBlock

            if (!removeAndDisplay(player, route, target) && (!shouldCheckAboveOnRemove || !removeAndDisplay(
                    player,
                    route,
                    target.above()
                ))
            ) {
                addAndDisplay(player, route, target, pContext.level)
            }

            // save route
            route.save(stack)
            return InteractionResult.SUCCESS
        }

        return InteractionResult.PASS
    }

    private fun removeAndDisplay(player: Player?, route: Route, pos: BlockPos): Boolean {
        val removed = route.removeIf { n -> n.isAt(pos) }
        if (removed && player != null) {
            player.displayClientMessage(
                Component.translatable("item.humblevehicles.locomotive_route.removed", pos.x, pos.y, pos.z),
                false
            )
        }
        return removed
    }

    private fun addAndDisplay(player: Player?, route: Route, pos: BlockPos, level: Level) {
        if (level.getBlockState(pos).block is BaseRailBlock) {
            // blockpos should be a railtype, either our custom rail or vanilla.
            // Though for pathfinding purposes, it is not guaranteed to be a rail, as the
            // world can change

            if (route.add(RouteNode(null, pos.x, pos.y, pos.z))) {
                player?.displayClientMessage(
                    Component.translatable("item.humblevehicles.locomotive_route.added", pos.x, pos.y, pos.z),
                    false
                )
            }
        }
    }

    override fun appendHoverText(
        pStack: ItemStack,
        pContext: TooltipContext,
        tooltip: MutableList<Component>,
        pTooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(pStack, pContext, tooltip, pTooltipFlag)
        tooltip.add(Component.translatable("item.humblevehicles.locomotive_route.description"))
        tooltip.add(
            Component.translatable("item.humblevehicles.locomotive_route.num_nodes", getRoute(pStack).size)
                .setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW))
        )
    }
}
