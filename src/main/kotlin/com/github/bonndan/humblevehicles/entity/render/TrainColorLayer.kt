package com.github.bonndan.humblevehicles.entity.render

import com.github.bonndan.humblevehicles.entity.models.VesselRenderState
import com.github.bonndan.humblevehicles.entity.models.train.TrimCarModel
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.MinecartModel
import net.minecraft.client.model.geom.EntityModelSet
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.renderer.texture.OverlayTexture.pack
import net.minecraft.client.renderer.texture.OverlayTexture.u
import net.minecraft.client.renderer.texture.OverlayTexture.v
import net.minecraft.resources.ResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn

@OnlyIn(Dist.CLIENT)
class TrainColorLayer(
    renderer: RenderLayerParent<VesselRenderState, MinecartModel>,
    modelSet: EntityModelSet,
    val textureLocation: ResourceLocation,
    modelLayerLocation : ModelLayerLocation
) : RenderLayer<VesselRenderState, MinecartModel>(renderer) {

    private val model: EntityModel<VesselRenderState> = TrimCarModel(modelSet.bakeLayer(modelLayerLocation))

    override fun render(
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        renderState: VesselRenderState,
        yRot: Float,
        xRot: Float
    ) {
        model.setupAnim(renderState);

        val vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(textureLocation));
        model.renderToBuffer(
            poseStack,
            vertexConsumer,
            packedLight,
            pack(u(0.0F), v(false)),
            renderState.getColor()
        )
    }

}
