package com.github.bonndan.humblevehicles.entity.render

import com.github.bonndan.humblevehicles.entity.models.train.TrimCarModel
import net.minecraft.client.model.MinecartModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import java.util.function.Function

data class RendererConfig(
    val layer: ModelLayerLocation = ModelLayers.MINECART,
    val textureLocation: ResourceLocation = ResourceLocation.withDefaultNamespace("textures/entity/minecart.png"),
    val modelSupplier: Function<ModelPart, MinecartModel> = Function { part: ModelPart -> MinecartModel(part) },
    val colorModelSupplier: Function<ModelPart, TrimCarModel> = Function { part: ModelPart -> TrimCarModel(part) },
    val modelYRotation: Float = 0f,
    val colorTexture: ResourceLocation? = null,
    val colorLayer: ModelLayerLocation? = null,
    val colorModelYOffset: Float = 0f,
    val colorModelYRotation: Float = 0f,
    val blockStateYOffset: Float = 0f,
) {
    fun getModel(context: EntityRendererProvider.Context): MinecartModel {
        return modelSupplier.apply(context.bakeLayer(layer))
    }

    fun getColorModel(context: EntityRendererProvider.Context): TrimCarModel? {
        if (colorLayer == null) {
            return null
        }
        return colorModelSupplier.apply(context.bakeLayer(colorLayer))
    }
}
