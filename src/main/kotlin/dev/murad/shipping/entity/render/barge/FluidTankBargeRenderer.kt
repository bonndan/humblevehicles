package dev.murad.shipping.entity.render.barge

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.murad.shipping.entity.custom.vessel.barge.FluidTankBargeEntity
import dev.murad.shipping.entity.render.ModelPack
import dev.murad.shipping.util.FluidRenderUtil.renderCubeUsingQuads
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider

class FluidTankBargeRenderer<T : FluidTankBargeEntity> protected constructor(
    context: EntityRendererProvider.Context,
    baseModelPack: ModelPack<T>,
    insertModelPack: ModelPack<T>,
    trimModelPack: ModelPack<T>
) : MultipartVesselRenderer<T>(context, baseModelPack, insertModelPack, trimModelPack) {

    override fun render(
        entity: T,
        yaw: Float,
        partialTick: Float,
        matrixStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        super.render(entity, yaw, partialTick, matrixStack, buffer, packedLight)
        renderFluid(entity, yaw, partialTick, matrixStack, buffer, 0, packedLight)
    }

    fun renderFluid(
        entity: FluidTankBargeEntity,
        yaw: Float,
        partialTicks: Float,
        matrixStackIn: PoseStack,
        bufferIn: MultiBufferSource,
        combinedLightIn: Int,
        combinedOverlayIn: Int
    ) {
        val fluid = entity.getFluidStack()
        if (fluid == null) return

        val renderFluid = fluid.getFluid()
        if (renderFluid == null) return

        matrixStackIn.pushPose()
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0f - yaw))
        matrixStackIn.translate(-0.3, 0.4, -0.25)
        matrixStackIn.scale(1f, 1.2f, 1f)
        renderCubeUsingQuads(
            FluidTankBargeEntity.Companion.CAPACITY,
            fluid,
            partialTicks,
            matrixStackIn,
            bufferIn,
            combinedLightIn,
            combinedOverlayIn
        )

        matrixStackIn.popPose()
    }

    class Builder<T : FluidTankBargeEntity>(context: EntityRendererProvider.Context) :
        MultipartVesselRenderer.Builder<T>(context) {
        override fun build(): FluidTankBargeRenderer<T> {
            return FluidTankBargeRenderer(context, baseModelPack, insertModelPack, trimModelPack)
        }
    }
}
