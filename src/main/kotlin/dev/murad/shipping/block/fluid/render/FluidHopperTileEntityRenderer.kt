package dev.murad.shipping.block.fluid.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.murad.shipping.block.fluid.FluidHopperBlock
import dev.murad.shipping.block.fluid.FluidHopperTileEntity
import dev.murad.shipping.util.FluidRenderUtil
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction

class FluidHopperTileEntityRenderer(context: BlockEntityRendererProvider.Context?) :
    BlockEntityRenderer<FluidHopperTileEntity> {
    override fun render(
        fluidHopperTileEntity: FluidHopperTileEntity,
        p_225616_2_: Float,
        matrixStack: PoseStack,
        buffer: MultiBufferSource,
        p_225616_5_: Int,
        p_225616_6_: Int
    ) {
        matrixStack.pushPose()
        val direction = fluidHopperTileEntity.blockState.getValue(FluidHopperBlock.FACING)
        matrixStack.translate(0.5f, 0f, 0.5f)
        when (direction) {
            Direction.NORTH -> matrixStack.mulPose(Axis.YP.rotationDegrees(Direction.SOUTH.toYRot()))
            Direction.SOUTH -> matrixStack.mulPose(Axis.YP.rotationDegrees(Direction.NORTH.toYRot()))
            else -> matrixStack.mulPose(Axis.YP.rotationDegrees(direction.toYRot()))
        }
        matrixStack.scale(1.45f, 1f, 1.2f)
        matrixStack.translate(-0.25f, 0f, -0.15f)

        FluidRenderUtil.renderCubeUsingQuads(
            FluidHopperTileEntity.CAPACITY,
            fluidHopperTileEntity.tank.fluid,
            p_225616_2_, matrixStack, buffer, p_225616_5_, p_225616_6_
        )

        matrixStack.popPose()
    }
}
