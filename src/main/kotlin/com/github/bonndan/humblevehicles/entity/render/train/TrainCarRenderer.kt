package com.github.bonndan.humblevehicles.entity.render.train

import com.github.bonndan.humblevehicles.HumVeeMod
import com.github.bonndan.humblevehicles.entity.custom.train.AbstractTrainCarEntity
import com.github.bonndan.humblevehicles.entity.models.VesselRenderState
import com.github.bonndan.humblevehicles.entity.models.train.ChainModel
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.datafixers.util.Pair
import com.mojang.math.Axis
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3
import java.util.function.Function
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.ceil

class TrainCarRenderer(
    context: EntityRendererProvider.Context,
    baseModel: Function<ModelPart, EntityModel<VesselRenderState>>,
    layerLocation: ModelLayerLocation,
    baseTexture: ResourceLocation
) : EntityRenderer<AbstractTrainCarEntity, VesselRenderState>(context) {

    private val entityModel: EntityModel<VesselRenderState>
    private val texture: ResourceLocation?

    private val chainModel: ChainModel

    constructor(
        context: EntityRendererProvider.Context,
        baseModel: Function<ModelPart, EntityModel<VesselRenderState>>,
        layerLocation: ModelLayerLocation,
        baseTexture: String
    ) : this(context, baseModel, layerLocation, ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, baseTexture))

    init {
        chainModel = ChainModel(context.bakeLayer(ChainModel.Companion.LAYER_LOCATION))
        entityModel = baseModel.apply(context.bakeLayer(layerLocation))
        texture = baseTexture
    }

    override fun createRenderState(): VesselRenderState {
        return VesselRenderState()
    }

    override fun extractRenderState(
        entity: AbstractTrainCarEntity,
        renderState: VesselRenderState,
        partialTicks: Float
    ) {
        super.extractRenderState(entity, renderState, partialTicks)
        renderState.setColor(entity.getColor())
        renderState.follower = entity.getFollower()
    }


    override fun render(
        renderState: VesselRenderState,
        pose: PoseStack,
        buffer: MultiBufferSource,
        pPackedLight: Int
    ) {
        super.render(renderState, pose, buffer, pPackedLight)
        getAndRenderChain(renderState, pose, buffer, pPackedLight);
    }


    private fun getAndRenderChain(
        renderState: VesselRenderState,
        matrixStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        if (!renderState.follower.isPresent) {
            return
        }

        val from = renderState.renderPos!!
        val to = renderState.follower.get().position()

        matrixStack.pushPose()
        val vec = from.vectorTo(to)
        val dist = vec.length()
        val segments = ceil(dist * 4).toInt()

        // TODO: fix pitch
        matrixStack.mulPose(Axis.YP.rotation((-atan2(vec.z, vec.x)).toFloat()))
        matrixStack.mulPose(Axis.ZP.rotation((asin(vec.y / dist)).toFloat()))
        matrixStack.pushPose()
        val ivertexbuilderChain = buffer.getBuffer(chainModel.renderType(CHAIN_TEXTURE))
        for (i in 1 until segments) {
            matrixStack.pushPose()
            matrixStack.translate(i / 4.0, 0.0, 0.0)

            chainModel.renderToBuffer(matrixStack, ivertexbuilderChain, packedLight, OverlayTexture.NO_OVERLAY)
            matrixStack.popPose()
        }

        matrixStack.popPose()
        matrixStack.popPose()
    }

    // First - front anchor point
    // Second - back anchor point
    // Override this to change anchor points for larger or smaller cars
    fun getAttachmentPoints(chainCentre: Vec3, trackDirection: Vec3): Pair<Vec3, Vec3> {
        return Pair<Vec3, Vec3>(chainCentre.add(trackDirection.scale(.2)), chainCentre.add(trackDirection.scale(-.2)))
    }

    companion object {
        private val CHAIN_TEXTURE = ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "textures/entity/chain.png")
    }
}
