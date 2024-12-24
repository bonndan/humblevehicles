package com.github.bonndan.humblevehicles.entity.render

import com.github.bonndan.humblevehicles.entity.custom.train.AbstractTrainCarEntity
import com.github.bonndan.humblevehicles.entity.custom.train.wagon.ChestCarEntity
import com.github.bonndan.humblevehicles.entity.custom.train.wagon.FluidTankCarEntity
import com.github.bonndan.humblevehicles.entity.models.PositionAdjusted
import com.github.bonndan.humblevehicles.entity.models.VesselRenderState
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.model.MinecartModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.block.BlockRenderDispatcher
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.state.MinecartRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.util.Mth
import net.minecraft.world.entity.vehicle.AbstractMinecart
import net.minecraft.world.entity.vehicle.NewMinecartBehavior
import net.minecraft.world.entity.vehicle.OldMinecartBehavior
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import java.util.*
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.max

/**
 * Copy of AbstractMinecartRenderer
 */
@OnlyIn(Dist.CLIENT)
class AbstractMinecartRendererCopy<T : AbstractTrainCarEntity>(
    context: EntityRendererProvider.Context,
    private val config: RendererConfig
) : EntityRenderer<T, VesselRenderState>(context), RenderLayerParent<VesselRenderState, MinecartModel> {

    private val model: MinecartModel
    private val blockRenderer: BlockRenderDispatcher
    private val chainRenderer = ChainRenderer(context = context)
    private val colorLayerRenderer: ColorLayerRenderer? =
        config.getColorLayer(this, context)
            ?.let { ColorLayerRenderer(it, config.trimModelYOffset, config.trimModelYRotation) }

    init {
        this.shadowRadius = 0.7f
        this.model = config.getModel(context)
        this.blockRenderer = context.blockRenderDispatcher
    }

    override fun render(
        renderState: VesselRenderState,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        super.render(renderState, poseStack, buffer, packedLight)

        poseStack.pushPose()
        val i = renderState.offsetSeed
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

        // render block inserted into minecart
        val blockstate = renderState.displayBlockState
        if (blockstate.renderShape != RenderShape.INVISIBLE) {
            poseStack.pushPose()
            poseStack.scale(0.75f, 0.75f, 0.75f)
            var yCorr = (renderState.displayOffset - 0).toFloat() / 16.0f + config.blockStateYOffset
            poseStack.translate(-0.5f, yCorr, 0.5f)
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0f))

            // contents
            this.renderMinecartContents(blockstate, poseStack, buffer, packedLight)
            poseStack.popPose()
        }

        poseStack.scale(-1.0f, -1.0f, 1.0f)

        //model corrections
        if (this.model is PositionAdjusted) {
            poseStack.translate(0f, this.model.getYOffset(), 0f)
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(config.modelYRotation))

        this.model.setupAnim(renderState)

        val vertexConsumer = buffer.getBuffer(this.model.renderType(config.textureLocation))
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY)

        //coloring
        colorLayerRenderer?.renderColorLayer(poseStack, buffer, packedLight, renderState)

        poseStack.popPose()

        //chain
        if (renderState.follower.isPresent) {

            renderState.backPos?.let { backPos ->
                chainRenderer.getAndRenderChain(
                    backPos,
                    renderState.follower.get().position(),
                    poseStack,
                    buffer,
                    packedLight
                )
            }
        }
    }


    override fun createRenderState(): VesselRenderState {
        return VesselRenderState()
    }

    override fun extractRenderState(entity: T, reusedState: VesselRenderState, partialTick: Float) {

        super.extractRenderState(entity, reusedState, partialTick)
        val behavior = entity.behavior
        if (behavior is NewMinecartBehavior) {
            newExtractState<T>(entity, behavior, reusedState, partialTick)
            reusedState.isNewRender = true
        } else if (behavior is OldMinecartBehavior) {
            oldExtractState<T>(entity, behavior, reusedState, partialTick)
            reusedState.isNewRender = false
        }

        val i = entity.id.toLong() * 493286711L
        reusedState.offsetSeed = i * i * 4392167121L + i * 98761L
        reusedState.hurtTime = entity.hurtTime.toFloat() - partialTick
        reusedState.hurtDir = entity.hurtDir
        reusedState.damageTime = max((entity.damage - partialTick).toDouble(), 0.0).toFloat()
        reusedState.displayOffset = entity.displayOffset
        reusedState.displayBlockState = entity.displayBlockState

        reusedState.setColor(entity.getColor())
        reusedState.follower = entity.getFollower()

        if (entity is ChestCarEntity) {
            reusedState.displayBlockState = Blocks.CHEST.defaultBlockState()
        }

        if (entity is FluidTankCarEntity) {
            reusedState.displayBlockState = Blocks.CAULDRON.defaultBlockState()
        }
    }

    private fun renderMinecartContents(
        state: BlockState,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int
    ) {
        this.blockRenderer.renderSingleBlock(
            state,
            poseStack,
            bufferSource,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            net.neoforged.neoforge.client.model.data.ModelData.EMPTY,
            null
        )
    }


    override fun getBoundingBoxForCulling(minecraft: T): AABB {
        val aabb = super.getBoundingBoxForCulling(minecraft)
        return if (minecraft.hasCustomDisplay()) {
            aabb.inflate(abs(minecraft.displayOffset.toDouble()) / 16.0)
        } else {
            aabb
        }
    }

    override fun getRenderOffset(renderState: VesselRenderState): Vec3 {
        val vec3 = super.getRenderOffset(renderState)
        return if (renderState.isNewRender && renderState.renderPos != null)
            vec3.add(
                renderState.renderPos!!.x - renderState.x,
                renderState.renderPos!!.y - renderState.y,
                renderState.renderPos!!.z - renderState.z
            )
        else
            vec3
    }

    override fun getModel(): MinecartModel {
        return model
    }

    companion object {


        private fun <S : MinecartRenderState?> newRender(renderState: S?, poseStack: PoseStack) {
            poseStack.mulPose(Axis.YP.rotationDegrees(renderState!!.yRot))
            poseStack.mulPose(Axis.ZP.rotationDegrees(-renderState.xRot))
            poseStack.translate(0.0f, 0.375f, 0.0f)
        }

        private fun <S : MinecartRenderState?> oldRender(renderState: S?, poseStack: PoseStack) {
            val d0 = renderState!!.x
            val d1 = renderState.y
            val d2 = renderState.z
            var f = renderState.xRot
            var f1 = renderState.yRot
            if (renderState.posOnRail != null && renderState.frontPos != null && renderState.backPos != null) {
                val vec3: Vec3 = renderState.frontPos!!
                val vec31: Vec3 = renderState.backPos!!
                poseStack.translate(
                    renderState.posOnRail!!.x - d0,
                    (vec3.y + vec31.y) / 2.0 - d1,
                    renderState.posOnRail!!.z - d2
                )
                var vec32 = vec31.add(-vec3.x, -vec3.y, -vec3.z)
                if (vec32.length() != 0.0) {
                    vec32 = vec32.normalize()
                    f1 = (atan2(vec32.z, vec32.x) * 180.0 / Math.PI).toFloat()
                    f = (atan(vec32.y) * 73.0).toFloat()
                }
            }

            poseStack.translate(0.0f, 0.375f, 0.0f)
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0f - f1))
            poseStack.mulPose(Axis.ZP.rotationDegrees(-f))
        }

        private fun <T : AbstractMinecart?> newExtractState(
            minecart: T?, behavior: NewMinecartBehavior, renderState: VesselRenderState, partialTick: Float
        ) {
            if (behavior.cartHasPosRotLerp()) {
                renderState.renderPos = behavior.getCartLerpPosition(partialTick)
                renderState.xRot = behavior.getCartLerpXRot(partialTick)
                renderState.yRot = behavior.getCartLerpYRot(partialTick)
            } else {
                renderState.renderPos = null
                renderState.xRot = minecart!!.xRot
                renderState.yRot = minecart.yRot
            }
        }

        private fun <T : AbstractMinecart?> oldExtractState(
            minecart: T?, behavior: OldMinecartBehavior, renderState: VesselRenderState, partialTick: Float
        ) {
            renderState.xRot = minecart!!.getXRot(partialTick)
            renderState.yRot = minecart.getYRot(partialTick)
            val d0 = renderState.x
            val d1 = renderState.y
            val d2 = renderState.z
            val vec3 = behavior.getPos(d0, d1, d2)
            if (vec3 != null) {
                renderState.posOnRail = vec3
                val vec31 = behavior.getPosOffs(d0, d1, d2, 0.3)
                val vec32 = behavior.getPosOffs(d0, d1, d2, -0.3)
                renderState.frontPos = Objects.requireNonNullElse<Vec3?>(vec31, vec3)
                renderState.backPos = Objects.requireNonNullElse<Vec3?>(vec32, vec3)
            } else {
                renderState.posOnRail = null
                renderState.frontPos = null
                renderState.backPos = null
            }
        }
    }
}
