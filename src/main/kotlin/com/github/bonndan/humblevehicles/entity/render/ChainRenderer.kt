package com.github.bonndan.humblevehicles.entity.render

import com.github.bonndan.humblevehicles.HumVeeMod.Companion.MOD_ID
import com.github.bonndan.humblevehicles.entity.models.train.ChainModel
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3
import kotlin.math.atan2
import kotlin.math.asin
import kotlin.math.ceil

private const val CHAIN_Y_OFFSET = 0.4

class ChainRenderer(context: EntityRendererProvider.Context) {

    private val chainModel: ChainModel = ChainModel(context.bakeLayer(ChainModel.Companion.LAYER_LOCATION))
    private val chainTexture = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/entity/chain.png")

    fun getAndRenderChain(
        pos: Vec3,
        followerPos: Vec3,
        matrixStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        val vec = pos.vectorTo(followerPos)
        val dist = vec.length()
        val segments = ceil(dist * 4).toInt()

        matrixStack.pushPose()
        matrixStack.mulPose(Axis.YP.rotation(-atan2(vec.z, vec.x).toFloat()))
        matrixStack.mulPose(Axis.ZP.rotation((asin(vec.y / dist)).toFloat()))
        matrixStack.translate(0.0, CHAIN_Y_OFFSET, 0.0)
        matrixStack.pushPose()

        val ivertexbuilderChain = buffer.getBuffer(chainModel.renderType(chainTexture))
        for (i in 1 until segments) {
            matrixStack.pushPose()
            matrixStack.translate(i / 4.0, 0.0, 0.0)
            chainModel.renderToBuffer(matrixStack, ivertexbuilderChain, packedLight, OverlayTexture.NO_OVERLAY)
            matrixStack.popPose()
        }

        matrixStack.popPose()
        matrixStack.popPose()
    }
}