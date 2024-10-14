package com.github.bonndan.humblevehicles.entity.render.train

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import com.github.bonndan.humblevehicles.entity.custom.train.wagon.FluidTankCarEntity
import com.github.bonndan.humblevehicles.entity.custom.vessel.barge.FluidTankBargeEntity
import com.github.bonndan.humblevehicles.entity.render.ModelPack
import com.github.bonndan.humblevehicles.util.FluidRenderUtil.renderCubeUsingQuads
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider

class FluidTankCarRenderer<T : FluidTankCarEntity> protected constructor(
    context: EntityRendererProvider.Context,
    baseModelPack: ModelPack<T>,
    insertModelPack: ModelPack<T>,
    trimModelPack: ModelPack<T>
) : MultipartCarRenderer<T>(context, baseModelPack, insertModelPack, trimModelPack) {

    override fun renderInsertModel(
        entity: T?,
        matrixStack: PoseStack,
        buffer: MultiBufferSource,
        partialTicks: Float,
        packedLight: Int,
        overlay: Int
    ) {
        super.renderInsertModel(entity, matrixStack, buffer, partialTicks, packedLight, overlay)
        renderFluid(entity!!, partialTicks, matrixStack, buffer, packedLight)
    }

    protected fun renderFluid(
        entity: FluidTankCarEntity,
        partialTicks: Float,
        matrixStackIn: PoseStack,
        bufferIn: MultiBufferSource,
        pPackedLight: Int
    ) {
        val fluid = entity.fluidStack
        if (fluid == null) return

        val renderFluid = fluid.getFluid()
        if (renderFluid == null) return

        matrixStackIn.pushPose()
        matrixStackIn.mulPose(Axis.ZN.rotationDegrees(180f))
        matrixStackIn.translate(-0.22, -1.05, -0.11)
        matrixStackIn.scale(0.9f, 0.9f, 0.83f)
        renderCubeUsingQuads(
            FluidTankBargeEntity.Companion.CAPACITY,
            fluid,
            partialTicks,
            matrixStackIn,
            bufferIn,
            pPackedLight,
            pPackedLight
        )

        matrixStackIn.popPose()
    }


    class Builder<T : FluidTankCarEntity>(context: EntityRendererProvider.Context) :
        MultipartCarRenderer.Builder<T>(context) {
        override fun build(): FluidTankCarRenderer<T> {
            return FluidTankCarRenderer<T>(context, baseModelPack, insertModelPack, trimModelPack)
        }
    }
}
