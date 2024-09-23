package dev.murad.shipping.rendering

import com.mojang.blaze3d.vertex.ByteBufferBuilder
import dev.murad.shipping.rendering.RenderUtil.calculatePlayerDir
import dev.murad.shipping.rendering.RenderUtil.renderLabel
import dev.murad.shipping.util.Route
import dev.murad.shipping.util.RouteNode
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.joml.Vector2d

class TugRouteRenderer {

    fun renderTugRoute(stack: ItemStack, event: RenderLevelStageEvent, player: Player) {

        val camera = Minecraft.getInstance().entityRenderDispatcher.camera
        val camPos = camera.position

        val renderTypeBuffer = MultiBufferSource.immediate(ByteBufferBuilder(1536))
        val route = Route.getRoute(stack)
        var i = 0
        val routeSize = route.size
        val matrixStack = event.poseStack
        while (i < routeSize) {

            val node = route[i]
            val playerDir = calculatePlayerDir(node, camPos)

            matrixStack.pushPose()

            matrixStack.translate(node.x - camPos.x, 0.0, node.z - camPos.z)

            RenderUtil.renderBeam(matrixStack, renderTypeBuffer, event, player, DyeColor.ORANGE.textureDiffuseColor)

            matrixStack.popPose()
            matrixStack.pushPose()

            renderLabel(node, playerDir, camPos, matrixStack, camera, i, renderTypeBuffer)

            matrixStack.popPose()
            i++
        }
        renderTypeBuffer.endBatch()
    }




}