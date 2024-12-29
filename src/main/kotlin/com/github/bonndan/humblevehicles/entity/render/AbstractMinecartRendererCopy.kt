package com.github.bonndan.humblevehicles.entity.render

import com.github.bonndan.humblevehicles.entity.custom.train.AbstractTrainCarEntity
import com.github.bonndan.humblevehicles.entity.models.VesselRenderState
import com.github.bonndan.humblevehicles.entity.models.train.TrimCarModel
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.MinecartModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.BlockRenderDispatcher
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.state.MinecartRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.entity.vehicle.AbstractMinecart
import net.minecraft.world.entity.vehicle.NewMinecartBehavior
import net.minecraft.world.entity.vehicle.OldMinecartBehavior
import net.minecraft.world.item.DyeColor
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
    private val config: RendererConfig,
) : EntityRenderer<T, VesselRenderState>(context), RenderLayerParent<VesselRenderState, MinecartModel> {

    private val model: MinecartModel
    private val colorModel: TrimCarModel?
    private val additionalModel: EntityModel<VesselRenderState>?
    private val blockRenderer: BlockRenderDispatcher
    private val chainRenderer = ChainRenderer(context = context)

    init {
        this.shadowRadius = 0.7f
        this.model = config.getModel(context)
        this.colorModel = config.getColorModel(context)
        this.additionalModel = config.getAdditionalModel(context)
        this.blockRenderer = context.blockRenderDispatcher
    }

    override fun render(
        renderState: VesselRenderState,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
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

        poseStack.scale(-1.0f, -1.0f, 1.0f)

        this.model.setupAnim(renderState)

        val vertexConsumer = buffer.getBuffer(this.model.renderType(config.modelTextureLocation))
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY)

        // render block inserted into minecart
        val blockstate = renderState.displayBlockState
        if (blockstate.renderShape != RenderShape.INVISIBLE) {
            poseStack.pushPose()
            poseStack.scale(0.75f, 0.75f, 0.75f)
            var yCorr = (renderState.displayOffset - 0).toFloat() / 16.0f + config.modelBlockStateYOffset
            poseStack.translate(-0.5f, yCorr, 0.5f)
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0f))

            // contents
            this.renderMinecartContents(blockstate, poseStack, buffer, packedLight)
            poseStack.popPose()
        }

        if (!renderState.isInvisible) {
            if (config.colorTexture != null && this.colorModel != null) {
                renderColorModel(
                    poseStack,
                    this.colorModel,
                    buffer,
                    config.colorTexture,
                    packedLight,
                    renderState,
                    config.colorModelYOffset,
                    config.colorModelYRotation
                )
            }

            if (this.additionalModel != null) {
                renderAdditionalModel(
                    poseStack,
                    this.additionalModel,
                    buffer,
                    config.additionalTexture!!,
                    packedLight,
                    config.additionalModelYOffset,
                    config.additionalModelYRotation
                )
            }
        }


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

    private fun renderColorModel(
        poseStack: PoseStack,
        colorModel: TrimCarModel,
        buffer: MultiBufferSource,
        colorTexture: ResourceLocation,
        packedLight: Int,
        renderState: VesselRenderState,
        yOffset: Float,
        yRotation: Float,
    ) {
        poseStack.pushPose()
        poseStack.translate(0f, yOffset, 0f)
        poseStack.scale(1.0f, 0.95f, 1.0f)
        poseStack.mulPose(Axis.YP.rotationDegrees(yRotation))
        colorModel.renderToBuffer(
            poseStack,
            buffer.getBuffer(RenderType.entityCutoutNoCull(colorTexture)),
            packedLight,
            OverlayTexture.NO_OVERLAY,
            DyeColor.byId(renderState.getColorId()).textureDiffuseColor
        )
        poseStack.popPose()
    }

    private fun renderAdditionalModel(
        poseStack: PoseStack,
        colorModel: EntityModel<VesselRenderState>,
        buffer: MultiBufferSource,
        colorTexture: ResourceLocation,
        packedLight: Int,
        yOffset: Float,
        yRotation: Float,
    ) {
        poseStack.pushPose()
        poseStack.translate(0f, yOffset, 0f)
        poseStack.mulPose(Axis.YP.rotationDegrees(yRotation))
        colorModel.renderToBuffer(
            poseStack,
            buffer.getBuffer(RenderType.entityCutoutNoCull(colorTexture)),
            packedLight,
            OverlayTexture.NO_OVERLAY,
        )
        poseStack.popPose()
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
        reusedState.setColorId(entity.getColorId())
        reusedState.follower = entity.getFollower()
    }

    private fun renderMinecartContents(
        state: BlockState,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
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

        private fun <S : MinecartRenderState> newRender(renderState: S, poseStack: PoseStack) {
            poseStack.mulPose(Axis.YP.rotationDegrees(renderState.yRot))
            poseStack.mulPose(Axis.ZP.rotationDegrees(-renderState.xRot))
            poseStack.translate(0.0f, 0.375f, 0.0f)
        }

        private fun <S : VesselRenderState> oldRender(renderState: S, poseStack: PoseStack) {
            val d0 = renderState.x
            val d1 = renderState.y
            val d2 = renderState.z
            var f = renderState.xRot
            var yRot = renderState.yRot
            var inverted = false
            if (renderState.posOnRail != null && renderState.frontPos != null && renderState.backPos != null) {
                val front: Vec3 = renderState.frontPos!!
                val back: Vec3 = renderState.backPos!!
                poseStack.translate(
                    renderState.posOnRail!!.x - d0,
                    (front.y + back.y) / 2.0 - d1,
                    renderState.posOnRail!!.z - d2
                )

                var trackDirection = front.subtract(back)
                if (trackDirection.length() != 0.0) {
                    trackDirection = trackDirection.normalize()
                    yRot = (atan2(trackDirection.z, trackDirection.x) * 180.0 / Math.PI).toFloat()
                    f = (atan(trackDirection.y) * 73.0).toFloat()
                }
            }

            poseStack.translate(0.0f, 0.375f, 0.0f)
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0f - yRot))
            poseStack.mulPose(Axis.ZP.rotationDegrees(-f))
        }

        private fun <T : AbstractMinecart> newExtractState(
            minecart: T, behavior: NewMinecartBehavior, renderState: VesselRenderState, partialTick: Float,
        ) {
            if (behavior.cartHasPosRotLerp()) {
                renderState.renderPos = behavior.getCartLerpPosition(partialTick)
                renderState.xRot = behavior.getCartLerpXRot(partialTick)
                renderState.yRot = behavior.getCartLerpYRot(partialTick)
            } else {
                renderState.renderPos = null
                renderState.xRot = minecart.xRot
                renderState.yRot = minecart.yRot
            }
        }

        private fun <T : AbstractTrainCarEntity> oldExtractState(
            minecart: T, behavior: OldMinecartBehavior, renderState: VesselRenderState, partialTick: Float,
        ) {
            renderState.xRot = minecart.getXRot(partialTick)
            renderState.yRot = minecart.getYRot(partialTick)
            val d0 = renderState.x
            val d1 = renderState.y
            val d2 = renderState.z

            val pos = behavior.getPos(d0, d1, d2)


            if (pos != null) {
                renderState.posOnRail = pos

                /*
                 * This is a hack to prevent the sudden visual flips of the cars, but still flickers in some cases.
                 *
                 */
                val xDir = minecart.x - minecart.xOld
                val zDir = minecart.z - minecart.zOld
                val direction = Direction.getApproximateNearest(xDir, 0.0, zDir)
                var factor = 1.0
                if (direction == Direction.EAST) { //undo flip
                    factor = -1.0
                }
                if (direction == Direction.SOUTH) { //undo flip
                    factor = -1.0
                }
                if (direction == Direction.WEST && renderState.direction == Direction.EAST) { //prevent flicker
                    factor = -1.0
                }
                if (direction == Direction.NORTH && renderState.direction == Direction.SOUTH) {//prevent flicker
                    factor = -1.0
                }

                /* extrapolate the positions of front and back of the car*/
                val newFront = behavior.getPosOffs(d0, d1, d2, 0.3 * factor)
                val newBack = behavior.getPosOffs(d0, d1, d2, -0.3 * factor)
                renderState.frontPos = Objects.requireNonNullElse<Vec3?>(newFront, pos)
                renderState.backPos = Objects.requireNonNullElse<Vec3?>(newBack, pos)
                renderState.direction = direction
            } else {
                renderState.posOnRail = null
                renderState.frontPos = null
                renderState.backPos = null
            }
        }
    }
}
