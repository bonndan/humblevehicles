package dev.murad.shipping.event

import dev.murad.shipping.HumVeeMod
import dev.murad.shipping.ShippingConfig
import dev.murad.shipping.network.client.VehicleTrackerPacketHandler
import dev.murad.shipping.setup.ModItems
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import net.neoforged.neoforge.event.level.LevelEvent
import kotlin.math.min

/**
 * Forge-wide event bus
 */
@EventBusSubscriber(modid = HumVeeMod.MOD_ID, value = [Dist.CLIENT])
object ForgeClientEventHandler {

    val BEAM_LOCATION: ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "textures/entity/beacon_beam.png")

    @SubscribeEvent
    fun onWorldUnload(event: LevelEvent.Unload?) {
        VehicleTrackerPacketHandler.flush()
    }

    @SubscribeEvent
    fun onRenderWorldLast(event: RenderLevelStageEvent) {
        if (event.stage != RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) {
            return
        }

        val player: Player? = Minecraft.getInstance().player

        val mainStack = player!!.getItemInHand(InteractionHand.MAIN_HAND)
        val offStack = player.getItemInHand(InteractionHand.OFF_HAND)

        // TODO: figure out if we want to disable offstack rendering when mainstack is rendered successfully.
        renderRouteOnStack(event, player, mainStack)
        renderRouteOnStack(event, player, offStack)

        // Only render registered vehicles when conductors wrench is on the mainhand
        if (wrenchIsInMainHand(mainStack, player)) {
            VehicleTrackerRenderer().render(event, player)
        }
    }

    private fun wrenchIsInMainHand(
        mainStack: ItemStack,
        player: Player
    ) = mainStack.item == ModItems.CONDUCTORS_WRENCH.get()
            && true //player.level().dimension().toString() == VehicleTrackerPacketHandler.toRenderDimension

    /**
     * Returns whether we rendered a route here. Empty route also returns true
     */
    private fun renderRouteOnStack(event: RenderLevelStageEvent, player: Player, stack: ItemStack) {

        if (stack.item == ModItems.LOCO_ROUTE.get()) {
            LocoRouteRenderer().renderLocoRoute(event, stack, player)
            return
        }

        if (stack.item == ModItems.TUG_ROUTE.get()) {

            if (ShippingConfig.Client.DISABLE_TUG_ROUTE_BEACONS.get()) {
                return
            }

            TugRouteRenderer().renderTugRoute(stack, event, player)
        }
    }

    fun computeFixedDistance(target: Vec3, position: Vec3, scale: Double): Vec3 {
        var newTarget = target
        newTarget = newTarget.add(0.0, 2.0, 0.0)
        val delta = position.vectorTo(newTarget)

        // The distance from the player camera to render the element
        val dist = min(5.0, delta.length())
        return position.add(delta.normalize().scale(dist * scale))
    }
}
