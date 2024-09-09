package dev.murad.shipping.event

import com.mojang.blaze3d.vertex.ByteBufferBuilder
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.math.Axis
import dev.murad.shipping.ShippingConfig
import dev.murad.shipping.HumVeeMod
import dev.murad.shipping.item.LocoRouteItem
import dev.murad.shipping.item.TugRouteItem
import dev.murad.shipping.network.client.VehicleTrackerPacketHandler
import dev.murad.shipping.setup.EntityItemMap.get
import dev.murad.shipping.setup.ModItems
import dev.murad.shipping.util.RailHelper
import dev.murad.shipping.util.TugRouteNode
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BeaconRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.properties.RailShape
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import net.neoforged.neoforge.event.level.LevelEvent
import org.joml.Vector2d
import java.util.*
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

    /**
     * Returns whether we rendered a route here. Empty route also returns true
     */
    private fun renderRouteOnStack(event: RenderLevelStageEvent, player: Player, stack: ItemStack): Boolean {
        if (stack.item == ModItems.LOCO_ROUTE.get()) {
            val buffer = bufferSource
            val pose = PoseStack()
            pose.mulPose(event.projectionMatrix)
            val cameraOff = Minecraft.getInstance().gameRenderer.mainCamera.position


            // Render Beacon Beams
            for (node in LocoRouteItem.getRoute(stack)) {
                val block = node?.toBlockPos()!!

                pose.pushPose()

                pose.translate(
                    (block.x - cameraOff.x).toFloat(),
                    (1 - cameraOff.y).toFloat(),
                    (block.z - cameraOff.z).toFloat()
                )
                pose.popPose()


                BeaconRenderer.renderBeaconBeam(
                    pose, buffer, BEAM_LOCATION, event.partialTick.gameTimeDeltaTicks,
                    1f, player.level().gameTime, player.level().minBuildHeight + 1, 1024,
                    DyeColor.YELLOW.textureDiffuseColor, 0.1f, 0.2f
                )

                pose.popPose()
                pose.pushPose()
                run {
                    // handling for removed blocks and blocks out of distance
                    val shape = RailHelper.getRail(block, player.level())
                        .map { pos -> RailHelper.getShape(pos, player.level()) }
                        .orElse(RailShape.EAST_WEST)
                    var baseY = (if (shape.isAscending) 0.1 else 0.0)
                    var baseX = 0.0
                    var baseZ = 0.0
                    var rotation = Axis.ZP.rotationDegrees(0f)
                    when (shape) {
                        RailShape.ASCENDING_EAST -> {
                            baseX = 0.2
                            rotation = Axis.ZP.rotationDegrees(45f)
                        }

                        RailShape.ASCENDING_WEST -> {
                            baseX = 0.1
                            baseY += 0.7
                            rotation = Axis.ZP.rotationDegrees(-45f)
                        }

                        RailShape.ASCENDING_NORTH -> {
                            baseZ = 0.1
                            baseY += 0.7
                            rotation = Axis.XP.rotationDegrees(45f)
                        }

                        RailShape.ASCENDING_SOUTH -> {
                            baseZ = 0.2
                            rotation = Axis.XP.rotationDegrees(-45f)
                        }

                        else -> {}
                    }

                    pose.translate(
                        block.x + baseX - cameraOff.x,
                        block.y + baseY - cameraOff.y,
                        block.z + baseZ - cameraOff.z
                    )
                    pose.mulPose(rotation)

                    val a = AABB(0.0, 0.0, 0.0, 1.0, 0.2, 1.0)
                    LevelRenderer.renderLineBox(pose, buffer.getBuffer(ModRenderType.LINES), a, 1.0f, 1.0f, 0.3f, 0.5f)
                }
                pose.popPose()
            }

            buffer.endBatch()
        } else if (stack.item == ModItems.TUG_ROUTE.get()) {
            if (ShippingConfig.Client.DISABLE_TUG_ROUTE_BEACONS.get()) {
                return false
            }

            val camera = Minecraft.getInstance().entityRenderDispatcher.camera
            val camPos = camera.position

            val renderTypeBuffer = bufferSource
            val route = TugRouteItem.getRoute(stack)
            var i = 0
            val routeSize = route.size
            while (i < routeSize) {
                val node: TugRouteNode = route[i]!!

                // Direction from the beacon to the player
                val playerDir = Vector2d(node.x + 0.5, node.z + 0.5)
                    .sub(Vector2d(camPos.x, camPos.z))
                    .normalize(0.5)

                val matrixStack = PoseStack()
                matrixStack.mulPose(event.projectionMatrix) //???
                matrixStack.pushPose()
                run {
                    matrixStack.translate(node.x - camPos.x, 0.0, node.z - camPos.z)
                    BeaconRenderer.renderBeaconBeam(
                        matrixStack, renderTypeBuffer, BEAM_LOCATION, event.partialTick.gameTimeDeltaTicks,
                        1f, player.level().gameTime, player.level().minBuildHeight, 1024,
                        DyeColor.ORANGE.textureDiffuseColor, 0.1f, 0.2f
                    )
                }
                matrixStack.popPose()
                matrixStack.pushPose()
                run {
                    val nodePos = Vec3(node.x + 0.5 - playerDir.x, camPos.y, node.z + 0.5 - playerDir.y)
                    val textRenderPos = computeFixedDistance(nodePos, camPos, 1.0)

                    matrixStack.translate(
                        textRenderPos.x - camPos.x,
                        textRenderPos.y - camPos.y,
                        textRenderPos.z - camPos.z
                    )
                    matrixStack.mulPose(Axis.YP.rotationDegrees(-camera.yRot))
                    matrixStack.mulPose(Axis.XP.rotationDegrees(camera.xRot))
                    matrixStack.scale(-0.025f, -0.025f, -0.025f)

                    val fontRenderer = Minecraft.getInstance().font
                    val text = node.getDisplayName(i)
                    val width = (-fontRenderer.width(text) / 2f)
                    fontRenderer.drawInBatch(
                        text,
                        width,
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
                matrixStack.popPose()
                i++
            }
            renderTypeBuffer.endBatch()
        } else {
            return false
        }
        return true
    }

    private val bufferSource: MultiBufferSource.BufferSource
        get() = MultiBufferSource.immediate(ByteBufferBuilder(1536))

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
                            (-fontRenderer.width(name) / 2f), 0.0f,
                            -1, true,
                            matrixStack.last().pose(), renderTypeBuffer,
                            Font.DisplayMode.NORMAL,
                            0, 15728880
                        )
                    }
                }
                matrixStack.popPose()
            }

            renderTypeBuffer.endBatch()
        }
    }

    private fun computeFixedDistance(target: Vec3, position: Vec3, scale: Double): Vec3 {
        var target = target
        target = target.add(0.0, 2.0, 0.0)
        val delta = position.vectorTo(target)

        // The distance from the player camera to render the element
        val dist = min(5.0, delta.length())
        return position.add(delta.normalize().scale(dist * scale))
    }

    class ModRenderType(
        pName: String,
        pFormat: VertexFormat,
        pMode: VertexFormat.Mode,
        pBufferSize: Int,
        pAffectsCrumbling: Boolean,
        pSortOnUpload: Boolean,
        pSetupState: Runnable,
        pClearState: Runnable
    ) :
        RenderType(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState) {
        companion object {
            val LINES: RenderType = create(
                "lines", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 256, false, false,
                CompositeState.builder()
                    .setShaderState(RENDERTYPE_LINES_SHADER)
                    .setLineState(LineStateShard(OptionalDouble.empty()))
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setOutputState(ITEM_ENTITY_TARGET)
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .setCullState(NO_CULL).createCompositeState(false)
            )
        }
    }
}
