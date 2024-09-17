package dev.murad.shipping.event

import com.mojang.blaze3d.vertex.ByteBufferBuilder
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.murad.shipping.ShippingConfig
import dev.murad.shipping.HumVeeMod
import dev.murad.shipping.network.client.VehicleTrackerPacketHandler
import dev.murad.shipping.setup.EntityItemMap.get
import dev.murad.shipping.setup.ModItems
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemDisplayContext
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

    private val bufferSource: MultiBufferSource.BufferSource = MultiBufferSource.immediate(ByteBufferBuilder(1536))

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
        if (mainStack.item == ModItems.CONDUCTORS_WRENCH.get() && player.level().dimension()
                .toString() == VehicleTrackerPacketHandler.toRenderDimension
        ) {
            val renderTypeBuffer = bufferSource
            val camera = Minecraft.getInstance().entityRenderDispatcher.camera
            val camPos = camera.position

            for (position in VehicleTrackerPacketHandler.toRender) {
                val entity = player.level().getEntity(position.id)

                val entityPos =
                    if (entity != null) entity.getPosition(event.partialTick.gameTimeDeltaTicks) else position.pos
                val iconRenderPos = computeFixedDistance(entityPos, camPos, 1.0)
                val textRenderPos = computeFixedDistance(entityPos, camPos, 0.9)
                val matrixStack = PoseStack()
                matrixStack.mulPose(event.projectionMatrix)

                matrixStack.pushPose()
                run {
                    matrixStack.translate(
                        iconRenderPos.x - camPos.x,
                        iconRenderPos.y - camPos.y,
                        iconRenderPos.z - camPos.z
                    )
                    matrixStack.mulPose(Axis.YP.rotationDegrees(-camera.yRot))
                    matrixStack.mulPose(Axis.XP.rotationDegrees(camera.xRot))
                    Minecraft.getInstance().itemRenderer.renderStatic(
                        ItemStack(get(position.type)),
                        ItemDisplayContext.GROUND,
                        150,
                        OverlayTexture.NO_OVERLAY,
                        matrixStack,
                        renderTypeBuffer,
                        player.level(),
                        position.id
                    )
                }
                matrixStack.popPose()
                matrixStack.pushPose()
                run {
                    matrixStack.translate(
                        textRenderPos.x - camPos.x,
                        textRenderPos.y - camPos.y,
                        textRenderPos.z - camPos.z
                    )
                    matrixStack.mulPose(Axis.YP.rotationDegrees(-camera.yRot))
                    matrixStack.mulPose(Axis.XP.rotationDegrees(camera.xRot))

                    matrixStack.scale(-0.025f, -0.025f, -0.025f)

                    val fontRenderer = Minecraft.getInstance().font
                    val text = String.format("%.1fm", position.pos.distanceTo(player.position()))

                    fontRenderer.drawInBatch(
                        text,
                        (-fontRenderer.width(text) / 2f), 0.0f,
                        -1, true,
                        matrixStack.last().pose(), renderTypeBuffer,
                        Font.DisplayMode.NORMAL,
                        0, 15728880
                    )
                    if (entity != null && entity.hasCustomName()) {
                        val name = entity.customName
                        matrixStack.translate(0f, -20f, 0f)
                        fontRenderer.drawInBatch(
                            name,
                            (-fontRenderer.width(name) / 2f),
                            0.0f,
                            -1,
                            true,
                            matrixStack.last().pose(),
                            renderTypeBuffer,
                            Font.DisplayMode.NORMAL,
                            0,
                            15728880
                        )
                    }
                }
                matrixStack.popPose()
            }

            renderTypeBuffer.endBatch()
        }
    }

    /**
     * Returns whether we rendered a route here. Empty route also returns true
     */
    private fun renderRouteOnStack(event: RenderLevelStageEvent, player: Player, stack: ItemStack) {

        if (stack.item == ModItems.LOCO_ROUTE.get()) {
            LocoRouteRenderer(bufferSource).renderLocoRoute(event, stack, player)
        } else if (stack.item == ModItems.TUG_ROUTE.get()) {

            if (ShippingConfig.Client.DISABLE_TUG_ROUTE_BEACONS.get()) {
                return
            }

            TugRouteRenderer(bufferSource).renderTugRoute(stack, event, player)
        } else {
            return
        }
        return
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
