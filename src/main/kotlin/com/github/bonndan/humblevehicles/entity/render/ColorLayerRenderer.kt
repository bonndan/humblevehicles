package com.github.bonndan.humblevehicles.entity.render

import com.github.bonndan.humblevehicles.entity.models.VesselRenderState
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.model.MinecartModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.layers.RenderLayer

class ColorLayerRenderer(
    private val colorLayer: RenderLayer<VesselRenderState, MinecartModel>,
    private val yOffset: Float,
    private val yRotation: Float
) {

    fun renderColorLayer(
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        renderState: VesselRenderState
    ) {
        poseStack.pushPose()
        poseStack.translate(0f, yOffset, 0f)
        poseStack.mulPose(Axis.YP.rotationDegrees(yRotation))
        colorLayer.render(poseStack, buffer, packedLight, renderState, renderState.yRot, renderState.xRot)
        poseStack.popPose()
    }
}