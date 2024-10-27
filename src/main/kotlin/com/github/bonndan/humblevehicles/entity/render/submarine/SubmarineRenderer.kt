package com.github.bonndan.humblevehicles.entity.render.submarine

import com.github.bonndan.humblevehicles.entity.custom.vessel.submarine.SubmarineEntity
import com.github.bonndan.humblevehicles.entity.render.ModelPack
import com.github.bonndan.humblevehicles.entity.render.barge.MultipartVesselRenderer
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.WaterPatchModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture

class SubmarineRenderer(
    context: EntityRendererProvider.Context,
    baseModelPack: ModelPack<SubmarineEntity>,
    insertModelPack: ModelPack<SubmarineEntity>,
    trimModelPack: ModelPack<SubmarineEntity>
) : MultipartVesselRenderer<SubmarineEntity>(context, baseModelPack, insertModelPack, trimModelPack) {

    override fun render(
        vesselEntity: SubmarineEntity,
        yaw: Float,
        partialTick: Float,
        matrixStack: PoseStack,
        buffer: MultiBufferSource,
        packetLight: Int
    ) {
        matrixStack.pushPose()
        matrixStack.translate(0.0, getModelYOffset(vesselEntity), 0.0)
        matrixStack.translate(0.0, 0.07, 0.0)
        matrixStack.mulPose(Axis.YP.rotationDegrees(180.0f - yaw))
        matrixStack.scale(-1.0f, -1.0f, 1.0f)
        matrixStack.mulPose(Axis.YP.rotationDegrees(getModelYrot()))
        renderModel(vesselEntity, matrixStack, buffer, packetLight)
        //renderWaterPatch(buffer, matrixStack, packetLight, getTrimModel())
        getAndRenderChain(vesselEntity, matrixStack, buffer, packetLight)
        matrixStack.popPose()
        getAndRenderLeash(vesselEntity, yaw, partialTick, matrixStack, buffer, packetLight)
    }

    /**
     * TODO Prevent water from being rendered inside the boat.
     *
     * This works in theory, but conflicts with the transparent pixels of the glass. As with regular boats, the water
     * patch should be surrounded by nontransparent pixels (the boat bottom) to prevent strange see-through effects.
     */
    private fun renderWaterPatch(
        buffer: MultiBufferSource,
        matrixStack: PoseStack,
        packetLight: Int,
        model: EntityModel<SubmarineEntity>
    ) {
        if (model is WaterPatchModel) {
            val consumer = buffer.getBuffer(RenderType.waterMask())
            model.waterPatch().render(matrixStack, consumer, packetLight, OverlayTexture.NO_OVERLAY)
        }
    }

    class Builder(context: EntityRendererProvider.Context) : MultipartVesselRenderer.Builder<SubmarineEntity>(context) {

        override fun build(): SubmarineRenderer {
            return SubmarineRenderer(context, baseModelPack, insertModelPack, trimModelPack)
        }
    }
}
