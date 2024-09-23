package dev.murad.shipping.item.container

import dev.murad.shipping.entity.accessor.RouteScreenDataAccessor
import dev.murad.shipping.setup.ModMenuTypes
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class RouteContainer(
    windowId: Int,
    data: RouteScreenDataAccessor,
    player: Player
) :
    AbstractContainerMenu(ModMenuTypes.TUG_ROUTE_CONTAINER.get(), windowId) {

    val isOffHand: Boolean = data.isOffHand

    val itemStack: ItemStack = player.getItemInHand(if (isOffHand) InteractionHand.OFF_HAND else InteractionHand.MAIN_HAND)

    init {
        LOGGER.debug("Got item stack {} in {}hand", itemStack.toString(), if (isOffHand) "off" else "main")
    }

    /**
     * Tug route container has no inventory so this will never actually be called.
     */
    override fun quickMoveStack(player: Player, p_38942_: Int): ItemStack {
        throw NotImplementedError("Tug route container has no inventory ")
    }

    override fun stillValid(player: Player): Boolean {
        return true
    }

    companion object {
        private val LOGGER: Logger = LogManager.getLogger(
            RouteContainer::class.java
        )
    }
}
