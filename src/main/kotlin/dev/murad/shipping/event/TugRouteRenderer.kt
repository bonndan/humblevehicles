package dev.murad.shipping.event

import com.mojang.blaze3d.vertex.ByteBufferBuilder
import com.mojang.math.Axis
import dev.murad.shipping.event.ForgeClientEventHandler.BEAM_LOCATION
import dev.murad.shipping.event.ForgeClientEventHandler.computeFixedDistance
import dev.murad.shipping.item.TugRouteItem
import dev.murad.shipping.util.TugRoute
import dev.murad.shipping.util.TugRouteNode
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BeaconRenderer
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
        val route = TugRoute.getRoute(stack)
        var i = 0
        val routeSize = route.size
        val matrixStack = event.poseStack
        while (i < routeSize) {
            val node = route[i]

            // Direction from the beacon to the player
            val playerDir = Vector2d(node.x + 0.5, node.z + 0.5)
                .sub(Vector2d(camPos.x, camPos.z))
                .normalize(0.5)



            matrixStack.pushPose()

            matrixStack.translate(node.x - camPos.x, 0.0, node.z - camPos.z)
            BeaconRenderer.renderBeaconBeam(
                matrixStack, renderTypeBuffer, BEAM_LOCATION, event.partialTick.gameTimeDeltaTicks,
                1f, player.level().gameTime, player.level().minBuildHeight, 1024,
                DyeColor.ORANGE.textureDiffuseColor, 0.1f, 0.2f
            )

            matrixStack.popPose()
            matrixStack.pushPose()

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

            matrixStack.popPose()
            i++
        }
        renderTypeBuffer.endBatch()
    }
}