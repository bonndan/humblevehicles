package com.github.bonndan.humblevehicles.rendering

import com.mojang.blaze3d.vertex.*
import com.mojang.math.Axis
import com.github.bonndan.humblevehicles.rendering.RenderUtil.calculatePlayerDir
import com.github.bonndan.humblevehicles.rendering.RenderUtil.renderBeam
import com.github.bonndan.humblevehicles.rendering.RenderUtil.renderLabel
import com.github.bonndan.humblevehicles.util.RailHelper
import com.github.bonndan.humblevehicles.util.Route
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.properties.RailShape
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.event.RenderLevelStageEvent

class LocoRouteRenderer {

    fun renderLocoRoute(event: RenderLevelStageEvent, stack: ItemStack, player: Player) {

        val buffer = MultiBufferSource.immediate(ByteBufferBuilder(1536))
        val matrixStack = event.poseStack
        val camera = Minecraft.getInstance().entityRenderDispatcher.camera
        val camPos = camera.position

        val cameraOff = Minecraft.getInstance().gameRenderer.mainCamera.position

        for ((i, node) in Route.getRoute(stack).withIndex()) {

            val block = node.toBlockPos()

            matrixStack.pushPose()

            //beam
            matrixStack.translate(
                (block.x - cameraOff.x).toFloat(),
                (1 - cameraOff.y).toFloat(),
                (block.z - cameraOff.z).toFloat()
            )
            renderBeam(matrixStack, buffer, event, player, DyeColor.YELLOW.textureDiffuseColor)

            matrixStack.popPose()
            matrixStack.pushPose()

            renderLabel(node, calculatePlayerDir(node, camPos), camPos, matrixStack, camera, i, buffer)

            matrixStack.popPose()
            matrixStack.pushPose()
            //lines
            renderLineBox(block, player, matrixStack, cameraOff, buffer)

            matrixStack.popPose()
        }

        buffer.endBatch()
    }

    private fun renderLineBox(
        block: BlockPos,
        player: Player,
        matrixStack: PoseStack,
        cameraOff: Vec3,
        buffer: MultiBufferSource.BufferSource
    ) {
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
    }
}