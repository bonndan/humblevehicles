package com.github.bonndan.humblevehicles.entity.models.insert

import com.github.bonndan.humblevehicles.HumVeeMod
import com.github.bonndan.humblevehicles.entity.Colorable
import com.github.bonndan.humblevehicles.entity.models.VesselRenderState
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity

class CubeInsertCarModel(root: ModelPart) : EntityModel<VesselRenderState>(root.getChild("bb_main"))  {

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.tryBuild(HumVeeMod.MOD_ID, "cube_insert_car_model"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val bb_main = meshdefinition.getRoot()
                .addOrReplaceChild(
                    "bb_main",
                    CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-5.0f, 8.0f, -4.0f, 10.0f, 10.0f, 10.0f),
                    PartPose.ZERO
                )

            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}