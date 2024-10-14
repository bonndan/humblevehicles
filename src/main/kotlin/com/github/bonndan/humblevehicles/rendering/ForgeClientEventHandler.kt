package com.github.bonndan.humblevehicles.rendering

import com.github.bonndan.humblevehicles.HumVeeMod
import com.github.bonndan.humblevehicles.ShippingConfig
import com.github.bonndan.humblevehicles.entity.custom.VehicleControl
import com.github.bonndan.humblevehicles.network.client.VehicleTrackerPacketHandler
import com.github.bonndan.humblevehicles.setup.ModItems
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import net.neoforged.neoforge.event.level.LevelEvent
import org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL

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
    fun onKeyInputEvent(event: InputEvent.Key?) {
        VehicleControl.handleKeyForVehicleControlDownForce(event, GLFW_KEY_LEFT_CONTROL, Minecraft.getInstance().player)
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
        //TODO stack tag nodes are not updated
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

}
