package com.github.bonndan.humblevehicles.entity.render

import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity

data class ModelPack<T : Entity>(
    val supplier: ModelSupplier<T>,
    val location: ModelLayerLocation,
    val texture: ResourceLocation
)