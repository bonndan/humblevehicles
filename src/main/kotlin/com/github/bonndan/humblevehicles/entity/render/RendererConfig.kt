package com.github.bonndan.humblevehicles.entity.render

import com.github.bonndan.humblevehicles.entity.models.VesselRenderState
import net.minecraft.client.model.MinecartModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.resources.ResourceLocation
import java.util.function.Function

data class RendererConfig(
    val layer: ModelLayerLocation = ModelLayers.MINECART,
    val textureLocation: ResourceLocation = ResourceLocation.withDefaultNamespace("textures/entity/minecart.png"),
    val modelSupplier: Function<ModelPart, MinecartModel> = Function { part: ModelPart -> MinecartModel(part) },
    val modelYRotation: Float = 0f,
    val colorTexture: ResourceLocation? = null,
    val colorLayer: ModelLayerLocation? = null,
    val trimModelYOffset: Float = 0f,
    val trimModelYRotation: Float = 0f,
    val blockStateYOffset: Float = 0f,
) {
    fun getModel(context: EntityRendererProvider.Context): MinecartModel {
        return modelSupplier.apply(context.bakeLayer(layer))
    }

    fun getColorLayer(
        renderLayer: RenderLayerParent<VesselRenderState, MinecartModel>,
        context: EntityRendererProvider.Context
    ): TrainColorLayer? =
        if (colorTexture != null && colorLayer != null)
            TrainColorLayer(renderLayer, context.modelSet, colorTexture, colorLayer) else null
}
