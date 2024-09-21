package dev.murad.shipping.event

import com.mojang.blaze3d.vertex.*
import com.mojang.math.Axis
import dev.murad.shipping.event.ForgeClientEventHandler.BEAM_LOCATION
import dev.murad.shipping.util.LocoRoute
import dev.murad.shipping.util.RailHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BeaconRenderer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.properties.RailShape
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import java.util.*

class LocoRouteRenderer {

    fun renderLocoRoute(event: RenderLevelStageEvent, stack: ItemStack, player: Player) {

        val buffer = MultiBufferSource.immediate(ByteBufferBuilder(1536))
        val matrixStack = event.poseStack

        val cameraOff = Minecraft.getInstance().gameRenderer.mainCamera.position

        // Render Beacon Beams
        for (node in LocoRoute.getRoute(stack)) {
            val block = node.toBlockPos()

            matrixStack.pushPose()

            matrixStack.translate((block.x - cameraOff.x).toFloat(), (1 - cameraOff.y).toFloat(), (block.z - cameraOff.z).toFloat())
            BeaconRenderer.renderBeaconBeam(
                matrixStack, buffer, BEAM_LOCATION, event.partialTick.gameTimeDeltaTicks,
                1f, player.level().gameTime, player.level().minBuildHeight + 1, 1024,
                DyeColor.YELLOW.textureDiffuseColor, 0.1f, 0.2f
            )
            matrixStack.popPose()
            matrixStack.pushPose()

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

            matrixStack.translate(
                block.x + baseX - cameraOff.x,
                block.y + baseY - cameraOff.y,
                block.z + baseZ - cameraOff.z
            )
            matrixStack.mulPose(rotation)

            val a = AABB(0.0, 0.0, 0.0, 1.0, 0.2, 1.0)
            LevelRenderer.renderLineBox(matrixStack, buffer.getBuffer(ModRenderType.LINES), a, 1.0f, 1.0f, 0.3f, 0.5f)

            matrixStack.popPose()
        }

        buffer.endBatch()
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