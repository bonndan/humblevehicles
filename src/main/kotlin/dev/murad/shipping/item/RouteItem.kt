package dev.murad.shipping.item

import dev.murad.shipping.entity.accessor.TugRouteScreenDataAccessor
import dev.murad.shipping.item.container.TugRouteContainer
import dev.murad.shipping.util.Route
import dev.murad.shipping.util.TugRouteNode
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import kotlin.math.floor

abstract class RouteItem(properties: Properties) : Item(properties) {

    fun getDataAccessor(entity: Player, hand: InteractionHand): TugRouteScreenDataAccessor {
        return TugRouteScreenDataAccessor.Builder(entity.id)
            .withOffHand(hand == InteractionHand.OFF_HAND)
            .build()
    }

    override fun use(world: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {

        val itemstack = player.getItemInHand(hand)

        if (player.level().isClientSide) {
            return InteractionResultHolder.pass(itemstack)
        }

        if (player.isShiftKeyDown) {
            player.openMenu(createContainerProvider(hand), getDataAccessor(player, hand)::write)
            return InteractionResultHolder.pass(itemstack)
        }

        val x = floor(player.x).toInt()
        val z = floor(player.z).toInt()
        if (!tryRemoveSpecific(itemstack, x, z)) {
            player.displayClientMessage(
                Component.translatable("item.humblevehicles.tug_route.added", x, z), false
            )
            pushRoute(itemstack, x, 0, z)
        } else {
            player.displayClientMessage(
                Component.translatable("item.humblevehicles.tug_route.removed", x, z), false
            )
        }

        return InteractionResultHolder.pass(itemstack)
    }

    private fun createContainerProvider(hand: InteractionHand): MenuProvider {

        return object : MenuProvider {
            override fun getDisplayName(): Component {
                return Component.translatable("screen.humblevehicles.tug_route")
            }

            override fun createMenu(i: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu {
                return TugRouteContainer(i, player.level(), getDataAccessor(player, hand), playerInventory, player)
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
        tooltip.add(Component.translatable("item.humblevehicles.tug_route.description"))
        tooltip.add(
            Component.translatable("item.humblevehicles.tug_route.num_nodes", getRoute(pStack).size).setStyle(
                Style.EMPTY.withColor(ChatFormatting.YELLOW)
            )
        )
    }

    abstract fun getRoute(itemStack: ItemStack): Route


    fun tryRemoveSpecific(itemStack: ItemStack, x: Int, z: Int): Boolean {
        val route = getRoute(itemStack)
        if (route.size == 0) {
            return false
        }
        val removed = route.removeIf { v -> v.x == x && v.z == z }
        route.save(itemStack)
        return removed
    }

    fun pushRoute(itemStack: ItemStack, x: Int, y: Int, z: Int) {
        val route = getRoute(itemStack)
        route.add(TugRouteNode(x, y, z))
        route.save(itemStack)
    }
}
