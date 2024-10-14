package com.github.bonndan.humblevehicles.entity.render

import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.world.entity.Entity

fun interface ModelSupplier<T : Entity> {
    fun supply(root: ModelPart): EntityModel<T>
}