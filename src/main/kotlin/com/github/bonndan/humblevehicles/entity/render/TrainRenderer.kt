package com.github.bonndan.humblevehicles.entity.render

import com.github.bonndan.humblevehicles.HumVeeMod.Companion.MOD_ID
import com.github.bonndan.humblevehicles.entity.custom.train.AbstractTrainCarEntity
import com.github.bonndan.humblevehicles.entity.models.VesselRenderState
import com.github.bonndan.humblevehicles.entity.models.train.ChainModel
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.AbstractMinecartRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.state.MinecartRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.entity.vehicle.AbstractMinecart
import net.minecraft.world.entity.vehicle.NewMinecartBehavior
import net.minecraft.world.entity.vehicle.OldMinecartBehavior
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.phys.Vec3
import java.util.Objects
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.ceil

class TrainRenderer(context: EntityRendererProvider.Context, layer: ModelLayerLocation) :
    AbstractMinecartRenderer<AbstractTrainCarEntity, VesselRenderState>(context, layer) {

    private val chainModel: ChainModel = ChainModel(context.bakeLayer(ChainModel.Companion.LAYER_LOCATION))
    private val MINECART_LOCATION: ResourceLocation =
        ResourceLocation.withDefaultNamespace("textures/entity/minecart.png");

    override fun render(
        renderState: VesselRenderState,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        super.render(renderState, poseStack, buffer, packedLight)

        poseStack.pushPose()
        val i = renderState!!.offsetSeed
        val f = (((i shr 16 and 7L).toFloat() + 0.5f) / 8.0f - 0.5f) * 0.004f
        val f1 = (((i shr 20 and 7L).toFloat() + 0.5f) / 8.0f - 0.5f) * 0.004f
        val f2 = (((i shr 24 and 7L).toFloat() + 0.5f) / 8.0f - 0.5f) * 0.004f
        poseStack.translate(f, f1, f2)
        if (renderState.isNewRender) {
            newRender(renderState, poseStack)
        } else {
            oldRender(renderState, poseStack)
        }

        val f3 = renderState.hurtTime
        if (f3 > 0.0f) {
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(f3) * f3 * renderState.damageTime / 10.0f * renderState.hurtDir.toFloat()))
        }

        val blockstate = renderState.displayBlockState
        if (blockstate.renderShape != RenderShape.INVISIBLE) {
            poseStack.pushPose()
            poseStack.scale(0.75f, 0.75f, 0.75f)
            poseStack.translate(-0.5f, (renderState.displayOffset - 8).toFloat() / 16.0f, 0.5f)
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0f))
            this.renderMinecartContents(renderState, blockstate, poseStack, buffer, packedLight)
            poseStack.popPose()
        }

        poseStack.scale(-1.0f, -1.0f, 1.0f)
        this.model.setupAnim(renderState)
        val vertexconsumer = buffer.getBuffer(this.model.renderType(MINECART_LOCATION))
        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY)
        poseStack.popPose()

        if (renderState.follower.isPresent) {

            renderState.renderPos?.let { pos ->
                val from = pos.add(0.0, .44, 0.0)
                val to = pos.add(0.0, .44, 0.0)
                getAndRenderChain(from, to, poseStack, buffer, packedLight)
            }
        }
    }

    override fun createRenderState(): VesselRenderState {
        return VesselRenderState()
    }

    override fun extractRenderState(car: AbstractTrainCarEntity, renderState: VesselRenderState, partialTicks: Float) {
        super.extractRenderState(car, renderState, partialTicks)

        renderState.setColor(car.getColor())
        renderState.follower = car.getFollower()
    }

    protected fun getAndRenderChain(
        from: Vec3,
        to: Vec3,
        matrixStack: PoseStack,
        buffer: MultiBufferSource,
        p_225623_6_: Int
    ) {
        matrixStack.pushPose()
        val vec = from.vectorTo(to)
        val dist = vec.length()
        val segments = ceil(dist * 4).toInt()

        // TODO: fix pitch
        matrixStack.mulPose(Axis.YP.rotation(-atan2(vec.z, vec.x).toFloat()))
        matrixStack.mulPose(Axis.ZP.rotation((asin(vec.y / dist)).toFloat()))
        matrixStack.pushPose()
        val ivertexbuilderChain = buffer.getBuffer(chainModel.renderType(CHAIN_TEXTURE))
        for (i in 1 until segments) {
            matrixStack.pushPose()
            matrixStack.translate(i / 4.0, 0.0, 0.0)
            chainModel.renderToBuffer(matrixStack, ivertexbuilderChain, p_225623_6_, OverlayTexture.NO_OVERLAY)
            matrixStack.popPose()
        }

        matrixStack.popPose()
        matrixStack.popPose()
    }

    companion object {

        private val CHAIN_TEXTURE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/entity/chain.png")

        private fun <S : MinecartRenderState?> newRender(p_360917_: S?, p_362046_: PoseStack) {
            p_362046_.mulPose(Axis.YP.rotationDegrees(p_360917_!!.yRot))
            p_362046_.mulPose(Axis.ZP.rotationDegrees(-p_360917_.xRot))
            p_362046_.translate(0.0f, 0.375f, 0.0f)
        }

        private fun <S : MinecartRenderState?> oldRender(p_364095_: S?, p_360278_: PoseStack) {
            val d0 = p_364095_!!.x
            val d1 = p_364095_.y
            val d2 = p_364095_.z
            var f = p_364095_.xRot
            var f1 = p_364095_.yRot
            if (p_364095_.posOnRail != null && p_364095_.frontPos != null && p_364095_.backPos != null) {
                val vec3: Vec3 = p_364095_.frontPos!!
                val vec31: Vec3 = p_364095_.backPos!!
                p_360278_.translate(
                    p_364095_.posOnRail!!.x - d0,
                    (vec3.y + vec31.y) / 2.0 - d1,
                    p_364095_.posOnRail!!.z - d2
                )
                var vec32 = vec31.add(-vec3.x, -vec3.y, -vec3.z)
                if (vec32.length() != 0.0) {
                    vec32 = vec32.normalize()
                    f1 = (atan2(vec32.z, vec32.x) * 180.0 / Math.PI).toFloat()
                    f = (atan(vec32.y) * 73.0).toFloat()
                }
            }

            p_360278_.translate(0.0f, 0.375f, 0.0f)
            p_360278_.mulPose(Axis.YP.rotationDegrees(180.0f - f1))
            p_360278_.mulPose(Axis.ZP.rotationDegrees(-f))
        }

        private fun <T : AbstractMinecart?, S : MinecartRenderState?> newExtractState(
            p_365349_: T?, p_365110_: NewMinecartBehavior, p_363052_: S?, p_364223_: Float
        ) {
            if (p_365110_.cartHasPosRotLerp()) {
                p_363052_!!.renderPos = p_365110_.getCartLerpPosition(p_364223_)
                p_363052_.xRot = p_365110_.getCartLerpXRot(p_364223_)
                p_363052_.yRot = p_365110_.getCartLerpYRot(p_364223_)
            } else {
                p_363052_!!.renderPos = null
                p_363052_.xRot = p_365349_!!.xRot
                p_363052_.yRot = p_365349_.yRot
            }
        }

        private fun <T : AbstractMinecart?, S : MinecartRenderState?> oldExtractState(
            p_363303_: T?, p_363748_: OldMinecartBehavior, p_360336_: S?, p_363476_: Float
        ) {
            p_360336_!!.xRot = p_363303_!!.getXRot(p_363476_)
            p_360336_.yRot = p_363303_.getYRot(p_363476_)
            val d0 = p_360336_.x
            val d1 = p_360336_.y
            val d2 = p_360336_.z
            val vec3 = p_363748_.getPos(d0, d1, d2)
            if (vec3 != null) {
                p_360336_.posOnRail = vec3
                val vec31 = p_363748_.getPosOffs(d0, d1, d2, 0.3)
                val vec32 = p_363748_.getPosOffs(d0, d1, d2, -0.3)
                p_360336_.frontPos = Objects.requireNonNullElse<Vec3?>(vec31, vec3)
                p_360336_.backPos = Objects.requireNonNullElse<Vec3?>(vec32, vec3)
            } else {
                p_360336_.posOnRail = null
                p_360336_.frontPos = null
                p_360336_.backPos = null
            }
        }
    }
}