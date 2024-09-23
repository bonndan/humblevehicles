package dev.murad.shipping.item

import dev.murad.shipping.item.container.RouteContainer
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import kotlin.math.floor

class TugRouteItem(properties: Properties) : RouteItem(properties) {

    override fun use(world: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {

        val itemstack = player.getItemInHand(hand)

        if (player.level().isClientSide) {
            return InteractionResultHolder.pass(itemstack)
        }

        if (player.isShiftKeyDown) {
            player.openMenu(createContainerProvider(hand), getDataAccessor(player, hand)::write)
            return InteractionResultHolder.pass(itemstack)
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
        return InteractionResultHolder.pass(itemstack)
    }

    private fun createContainerProvider(hand: InteractionHand): MenuProvider {

        return object : MenuProvider {
            override fun getDisplayName(): Component {
                return Component.translatable("screen.humblevehicles.tug_route")
            }

            override fun createMenu(i: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu {
                return RouteContainer(i, getDataAccessor(player, hand), player)
            }
        }
    }
}
