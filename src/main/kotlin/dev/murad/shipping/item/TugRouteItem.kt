package dev.murad.shipping.item

import dev.murad.shipping.entity.accessor.TugRouteScreenDataAccessor
import dev.murad.shipping.item.container.TugRouteContainer
import dev.murad.shipping.util.LegacyTugRouteUtil.convertLegacyRoute
import dev.murad.shipping.util.LegacyTugRouteUtil.parseLegacyRouteString
import dev.murad.shipping.util.TugRoute
import dev.murad.shipping.util.TugRoute.Companion.fromNBT
import dev.murad.shipping.util.TugRouteNode
import net.minecraft.ChatFormatting
import net.minecraft.nbt.CompoundTag
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
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import kotlin.math.floor

class TugRouteItem(properties: Properties) : Item(properties) {
    protected fun createContainerProvider(hand: InteractionHand): MenuProvider {
        return object : MenuProvider {
            override fun getDisplayName(): Component {
                return Component.translatable("screen.humblevehicles.tug_route")
            }

            override fun createMenu(i: Int, playerInventory: Inventory, Player: Player): AbstractContainerMenu? {
                return TugRouteContainer(i, Player.level(), getDataAccessor(Player, hand), playerInventory, Player)
            }
        }
    }

    fun getDataAccessor(entity: Player, hand: InteractionHand): TugRouteScreenDataAccessor {
        return TugRouteScreenDataAccessor.Builder(entity.id)
            .withOffHand(hand == InteractionHand.OFF_HAND)
            .build()
    }

    override fun use(world: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        val itemstack = player.getItemInHand(hand)
        if (!player.level().isClientSide) {
            if (player.isShiftKeyDown) {
                player.openMenu(createContainerProvider(hand))
                //NetworkHooks.openScreen((ServerPlayer) player, createContainerProvider(hand), getDataAccessor(player, hand)::write);
            } else {
                val x = floor(player.x) as Int
                val z = floor(player.z) as Int
                if (!tryRemoveSpecific(itemstack, x, z)) {
                    player.displayClientMessage(
                        Component.translatable("item.humblevehicles.tug_route.added", x, z),
                        false
                    )
                    pushRoute(itemstack, x, z)
                } else {
                    player.displayClientMessage(
                        Component.translatable("item.humblevehicles.tug_route.removed", x, z),
                        false
                    )
                }
            }
        }

        return InteractionResultHolder.pass(itemstack)
    }

    override fun verifyComponentsAfterLoad(pStack: ItemStack) {
        super.verifyComponentsAfterLoad(pStack)

        // convert old nbt format of route: "" into compound format
        // Precond: nbt is non-null, and nbt.tag is nonnull type 10
        val outer = ItemStackUtil.getCompoundTag(pStack)
        outer.ifPresent { compoundTag: CompoundTag? ->
            val tag = compoundTag!!.getCompound("tag")
            if (tag.contains(ROUTE_NBT, 8)) {
                LOGGER.info("Found legacy tug route tag, replacing now")
                val routeString = tag.getString(ROUTE_NBT)
                val legacyRoute = parseLegacyRouteString(routeString)
                val route = convertLegacyRoute(legacyRoute)
                tag.put(ROUTE_NBT, route.toNBT())
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

    companion object {
        private val LOGGER: Logger = LogManager.getLogger(TugRouteItem::class.java)

        private const val ROUTE_NBT = "route"

        @JvmStatic
        fun getRoute(itemStack: ItemStack): TugRoute {
            return ItemStackUtil.getCompoundTag(itemStack)
                .filter { compoundTag: CompoundTag? -> compoundTag!!.contains(ROUTE_NBT, 10) }
                .map { compoundTag: CompoundTag? ->
                    fromNBT(
                        compoundTag!!.getCompound(ROUTE_NBT)
                    )
                }
                .orElse(TugRoute())
        }

        fun popRoute(itemStack: ItemStack): Boolean {
            val route = getRoute(itemStack)
            if (route.size == 0) {
                return false
            }
            route.removeAt(route.size - 1)
            saveRoute(route, itemStack)
            return true
        }

        fun tryRemoveSpecific(itemStack: ItemStack, x: Int, z: Int): Boolean {
            val route = getRoute(itemStack)
            if (route.size == 0) {
                return false
            }
            val removed = route.removeIf { v: TugRouteNode -> v.x == x.toDouble() && v.z == z.toDouble() }
            saveRoute(route, itemStack)
            return removed
        }

        fun pushRoute(itemStack: ItemStack, x: Int, y: Int) {
            val route = getRoute(itemStack)
            route.add(TugRouteNode(x.toDouble(), y.toDouble()))
            saveRoute(route, itemStack)
        }

        // should only be called server side
        @JvmStatic
        fun saveRoute(route: TugRoute, itemStack: ItemStack) {
            ItemStackUtil.getCompoundTag(itemStack).ifPresent { compoundTag: CompoundTag? ->
                compoundTag!!.put(
                    ROUTE_NBT,
                    route.toNBT()
                )
            }
        }
    }
}
