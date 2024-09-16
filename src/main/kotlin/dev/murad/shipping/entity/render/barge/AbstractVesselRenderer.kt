package dev.murad.shipping.entity.render.barge

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import dev.murad.shipping.HumVeeMod
import dev.murad.shipping.entity.custom.vessel.VesselEntity
import dev.murad.shipping.entity.models.train.ChainModel
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.LightLayer
import org.joml.Matrix4f
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

abstract class AbstractVesselRenderer<T : VesselEntity>(context: EntityRendererProvider.Context) :
    EntityRenderer<T>(context) {

    private val chainModel: ChainModel

    init {
        chainModel = ChainModel(context.bakeLayer(ChainModel.Companion.LAYER_LOCATION))
    }

    override fun render(
        vesselEntity: T,
        yaw: Float,
        partialTick: Float,
        matrixStack: PoseStack,
        buffer: MultiBufferSource?,
        p_225623_6_: Int
    ) {
        matrixStack.pushPose()
        matrixStack.translate(0.0, getModelYoffset(), 0.0)
        matrixStack.translate(0.0, 0.07, 0.0)
        matrixStack.mulPose(Axis.YP.rotationDegrees(180.0f - yaw))
        matrixStack.scale(-1.0f, -1.0f, 1.0f)
        matrixStack.mulPose(Axis.YP.rotationDegrees(getModelYrot()))
        renderModel(vesselEntity, matrixStack, buffer!!, p_225623_6_)
        getAndRenderChain(vesselEntity, matrixStack, buffer, p_225623_6_)
        matrixStack.popPose()

        getAndRenderLeash(vesselEntity, yaw, partialTick, matrixStack, buffer, p_225623_6_)
    }

    protected open fun renderModel(
        vesselEntity: T,
        matrixStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        val ivertexbuilder =
            buffer.getBuffer(getModel(vesselEntity).renderType(this.getTextureLocation(vesselEntity)))
        val overlay = LivingEntityRenderer.getOverlayCoords(vesselEntity, 0f)

        //getModel(vesselEntity).renderToBuffer(matrixStack, buffer, packedLight, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        getModel(vesselEntity)!!.renderToBuffer(matrixStack, ivertexbuilder, packedLight, overlay, 1)
    }

    protected fun getModelYoffset(): Double {
        return 0.275
    }

    protected open fun getModelYrot(): Float {
        return 90.0f
    }

    private fun getAndRenderChain(
        bargeEntity: T?,
        matrixStack: PoseStack,
        buffer: MultiBufferSource,
        p_225623_6_: Int
    ) {
        if (bargeEntity!!.getLeader().isPresent()) {
            val dist = bargeEntity.getLeader().get().distanceTo(bargeEntity).toDouble()
            val ivertexbuilderChain =
                buffer.getBuffer(chainModel.renderType(CHAIN_TEXTURE))
            val segments = ceil(dist * 4).toInt()
            matrixStack.pushPose()
            for (i in 0 until segments) {
                matrixStack.pushPose()
                matrixStack.translate(i / 4.0, 0.0, 0.0)
                chainModel.renderToBuffer(matrixStack, ivertexbuilderChain, p_225623_6_, OverlayTexture.NO_OVERLAY)
                matrixStack.popPose()
            }
            matrixStack.popPose()
        }
    }

    private fun getAndRenderLeash(
        bargeEntity: T,
        p_225623_2_: Float,
        p_225623_3_: Float,
        matrixStack: PoseStack,
        buffer: MultiBufferSource,
        p_225623_6_: Int
    ) {
        matrixStack.pushPose()
        val entity = bargeEntity.getLeashHolder()
        super.render(bargeEntity, p_225623_2_, p_225623_3_, matrixStack, buffer, p_225623_6_)
        if (entity != null) {
            matrixStack.pushPose()
            this.renderLeash<Entity>(bargeEntity, p_225623_3_, matrixStack, buffer, entity)
            matrixStack.popPose()
        }
        matrixStack.popPose()
    }

    override fun shouldRender(
        p_225626_1_: T?,
        p_225626_2_: Frustum?,
        p_225626_3_: Double,
        p_225626_5_: Double,
        p_225626_7_: Double
    ): Boolean {
        if (p_225626_1_!!.getLeader().isPresent()) {
            if (p_225626_1_.getLeader().get().shouldRender(p_225626_3_, p_225626_5_, p_225626_7_)) {
                return true
            }
            if (p_225626_1_.getLeader().get().shouldRender(p_225626_3_, p_225626_5_, p_225626_7_)) {
                return true
            }
        }
        return super.shouldRender(p_225626_1_, p_225626_2_, p_225626_3_, p_225626_5_, p_225626_7_)
    }


    abstract fun getModel(entity: T): EntityModel<T>


    private fun <E : Entity> renderLeash(
        pEntityLiving: T,
        pPartialTicks: Float,
        pMatrixStack: PoseStack,
        pBuffer: MultiBufferSource,
        pLeashHolder: E?
    ) {
        pMatrixStack.pushPose()
        val vec3 = pLeashHolder!!.getRopeHoldPosition(pPartialTicks)
        val d0 = (Mth.lerp(
            pPartialTicks,
            pEntityLiving!!.yBodyRot,
            pEntityLiving.yBodyRotO
        ) * (Math.PI.toFloat() / 180f)).toDouble() + (Math.PI / 2.0)
        val vec31 = pEntityLiving.getLeashOffset(pPartialTicks)
        val d1 = cos(d0) * vec31.z + sin(d0) * vec31.x
        val d2 = sin(d0) * vec31.z - cos(d0) * vec31.x
        val d3 = Mth.lerp(pPartialTicks.toDouble(), pEntityLiving.xo, pEntityLiving.getX()) + d1
        val d4 = Mth.lerp(pPartialTicks.toDouble(), pEntityLiving.yo, pEntityLiving.getY()) + vec31.y
        val d5 = Mth.lerp(pPartialTicks.toDouble(), pEntityLiving.zo, pEntityLiving.getZ()) + d2
        pMatrixStack.translate(d1, vec31.y, d2)
        val f = (vec3.x - d3).toFloat()
        val f1 = (vec3.y - d4).toFloat()
        val f2 = (vec3.z - d5).toFloat()
        val f3 = 0.025f
        val vertexconsumer = pBuffer.getBuffer(RenderType.leash())
        val matrix4f = pMatrixStack.last().pose()
        val f4 = Mth.invSqrt(f * f + f2 * f2) * 0.025f / 2.0f
        val f5 = f2 * f4
        val f6 = f * f4
        val blockpos = BlockPos.containing(pEntityLiving.getEyePosition(pPartialTicks))
        val blockpos1 = BlockPos.containing(pLeashHolder.getEyePosition(pPartialTicks))
        val i = this.getBlockLightLevel(pEntityLiving, blockpos)
        val j = i
        val k = pEntityLiving.level().getBrightness(LightLayer.SKY, blockpos)
        val l = pEntityLiving.level().getBrightness(LightLayer.SKY, blockpos1)

        for (i1 in 0..24) {
            AbstractVesselRenderer.Companion.addVertexPair(
                vertexconsumer,
                matrix4f,
                f,
                f1,
                f2,
                i,
                j,
                k,
                l,
                0.025f,
                0.025f,
                f5,
                f6,
                i1,
                false
            )
        }

        for (j1 in 24 downTo 0) {
            AbstractVesselRenderer.Companion.addVertexPair(
                vertexconsumer,
                matrix4f,
                f,
                f1,
                f2,
                i,
                j,
                k,
                l,
                0.025f,
                0.0f,
                f5,
                f6,
                j1,
                true
            )
        }

        pMatrixStack.popPose()
    }

    companion object {
        private val CHAIN_TEXTURE = ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "textures/entity/chain.png")

        private fun addVertexPair(
            p_174308_: VertexConsumer,
            p_174309_: Matrix4f,
            p_174310_: Float,
            p_174311_: Float,
            p_174312_: Float,
            p_174313_: Int,
            p_174314_: Int,
            p_174315_: Int,
            p_174316_: Int,
            p_174317_: Float,
            p_174318_: Float,
            p_174319_: Float,
            p_174320_: Float,
            p_174321_: Int,
            p_174322_: Boolean
        ) {
            val f = p_174321_.toFloat() / 24.0f
            val i = Mth.lerp(f, p_174313_.toFloat(), p_174314_.toFloat()).toInt()
            val j = Mth.lerp(f, p_174315_.toFloat(), p_174316_.toFloat()).toInt()
            val k = LightTexture.pack(i, j)
            val f1 = if (p_174321_ % 2 == (if (p_174322_) 1 else 0)) 0.7f else 1.0f
            val f2 = 0.5f * f1
            val f3 = 0.4f * f1
            val f4 = 0.3f * f1
            val f5 = p_174310_ * f
            val f6 = if (p_174311_ > 0.0f) p_174311_ * f * f else p_174311_ - p_174311_ * (1.0f - f) * (1.0f - f)
            val f7 = p_174312_ * f

            // TODO uv2 field taken from FluidRenderUtil instead of k
            p_174308_.addVertex(p_174309_, f5 - p_174319_, f6 + p_174318_, f7 + p_174320_)
                .setColor(f2, f3, f4, 1.0f)
                .setUv2(0, 240)

            p_174308_.addVertex(p_174309_, f5 + p_174319_, f6 + p_174317_ - p_174318_, f7 - p_174320_)
                .setColor(f2, f3, f4, 1.0f)
                .setUv2(0, 240)
        }
    }
}
