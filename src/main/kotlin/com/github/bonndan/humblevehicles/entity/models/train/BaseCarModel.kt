package com.github.bonndan.humblevehicles.entity.models.train

import com.github.bonndan.humblevehicles.HumVeeMod
import net.minecraft.client.model.MinecartModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation

class BaseCarModel(root: ModelPart) : MinecartModel(root) {

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.tryBuild(HumVeeMod.MOD_ID, "base_car_model"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            partdefinition.addOrReplaceChild(
                "bb_main", CubeListBuilder.create() // Long sides

                    .texOffs(0, 0).addBox(-7.0f, -13.0f, -8.0f, 2.0f, 9.0f, 16.0f)
                    .texOffs(0, 0).addBox(5.0f, -13.0f, -8.0f, 2.0f, 9.0f, 16.0f) // Short sides

                    .texOffs(0, 28).addBox(-5.0f, -13.0f, -8.0f, 10.0f, 9.0f, 2.0f)
                    .texOffs(0, 28).addBox(-5.0f, -13.0f, 6.0f, 10.0f, 9.0f, 2.0f) // Wheels

                    .texOffs(0, 0).addBox(-6.0f, -2.0f, 4.0f, 1.0f, 2.0f, 2.0f)
                    .texOffs(0, 0).addBox(-6.0f, -2.0f, -6.0f, 1.0f, 2.0f, 2.0f)
                    .texOffs(0, 0).addBox(5.0f, -2.0f, 4.0f, 1.0f, 2.0f, 2.0f)
                    .texOffs(0, 0).addBox(5.0f, -2.0f, -6.0f, 1.0f, 2.0f, 2.0f),

                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}