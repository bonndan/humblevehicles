package com.github.bonndan.humblevehicles.rendering

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import com.github.bonndan.humblevehicles.util.RouteNode
import net.minecraft.client.Camera
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BeaconRenderer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.joml.Vector2d
import kotlin.math.min

object RenderUtil {

    fun computeFixedDistance(target: Vec3, position: Vec3, scale: Double): Vec3 {
        var newTarget = target
        newTarget = newTarget.add(0.0, 2.0, 0.0)
        val delta = position.vectorTo(newTarget)

        // The distance from the player camera to render the element
        val dist = min(5.0, delta.length())
        return position.add(delta.normalize().scale(dist * scale))
    }

     fun renderBeam(
        matrixStack: PoseStack?,
        buffer: MultiBufferSource.BufferSource?,
        event: RenderLevelStageEvent,
        player: Player,
        color: Int
    ) {
        BeaconRenderer.renderBeaconBeam(
            matrixStack,
            buffer,
            ForgeClientEventHandler.BEAM_LOCATION,
            event.partialTick.gameTimeDeltaTicks,
            1f,
            player.level().gameTime,
            player.level().minBuildHeight + 1,
            1024,
            color,
            0.1f,
            0.2f
        )
    }

     fun renderLabel(
        node: RouteNode,
        playerDir: Vector2d,
        camPos: Vec3,
        matrixStack: PoseStack,
        camera: Camera,
        i: Int,
        renderTypeBuffer: MultiBufferSource.BufferSource
    ) {
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

    // Direction from the beacon to the player
     fun calculatePlayerDir(
        node: RouteNode,
        camPos: Vec3
    ): Vector2d {
        return Vector2d(node.x + 0.5, node.z + 0.5)
            .sub(Vector2d(camPos.x, camPos.z))
            .normalize(0.5)
    }
}