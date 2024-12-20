package com.github.bonndan.humblevehicles.entity.models.train

import com.github.bonndan.humblevehicles.HumVeeMod
import com.github.bonndan.humblevehicles.entity.models.VesselRenderState
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation

class ChunkLoaderCarModel(root: ModelPart) : EntityModel<VesselRenderState>(root) {

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "chunkloadercarmodel"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val bb_main = partdefinition.addOrReplaceChild(
                "bb_main",
                CubeListBuilder.create().texOffs(64, 37).addBox(-2.0f, -33.0f, -1.0f, 4.0f, 4.0f, 4.0f),
                PartPose.offset(0.0f, 43.0f, 0.0f)
            )

            bb_main.addOrReplaceChild(
                "ring", CubeListBuilder.create().texOffs(102, 0).addBox(-5.0f, -29.0f, -4.0f, 10.0f, 2.0f, 2.0f)
                    .texOffs(100, 31).addBox(-5.0f, -29.0f, 4.0f, 10.0f, 2.0f, 2.0f)
                    .texOffs(74, 41).addBox(3.0f, -29.0f, -2.0f, 2.0f, 2.0f, 6.0f)
                    .texOffs(102, 4).addBox(-5.0f, -29.0f, -2.0f, 2.0f, 2.0f, 6.0f), PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            bb_main.addOrReplaceChild(
                "ring2", CubeListBuilder.create().texOffs(102, 0).addBox(-5.0f, -29.0f, -4.0f, 10.0f, 2.0f, 2.0f)
                    .texOffs(100, 31).addBox(-5.0f, -29.0f, 4.0f, 10.0f, 2.0f, 2.0f)
                    .texOffs(74, 41).addBox(3.0f, -29.0f, -2.0f, 2.0f, 2.0f, 6.0f)
                    .texOffs(102, 4).addBox(-5.0f, -29.0f, -2.0f, 2.0f, 2.0f, 6.0f), PartPose.offset(0.0f, -3.0f, 0.0f)
            )

            bb_main.addOrReplaceChild(
                "ring3", CubeListBuilder.create().texOffs(102, 0).addBox(-5.0f, -29.0f, -4.0f, 10.0f, 2.0f, 2.0f)
                    .texOffs(100, 31).addBox(-5.0f, -29.0f, 4.0f, 10.0f, 2.0f, 2.0f)
                    .texOffs(74, 41).addBox(3.0f, -29.0f, -2.0f, 2.0f, 2.0f, 6.0f)
                    .texOffs(102, 4).addBox(-5.0f, -29.0f, -2.0f, 2.0f, 2.0f, 6.0f), PartPose.offset(0.0f, -7.0f, 0.0f)
            )

            partdefinition.addOrReplaceChild(
                "bb_main2", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0f, -14.0f, -8.0f, 2.0f, 12.0f, 16.0f)
                    .texOffs(0, 0).addBox(5.0f, -14.0f, -8.0f, 2.0f, 12.0f, 16.0f)
                    .texOffs(0, 28).addBox(-5.0f, -14.0f, -8.0f, 10.0f, 12.0f, 2.0f)
                    .texOffs(0, 28).addBox(-5.0f, -14.0f, 6.0f, 10.0f, 12.0f, 2.0f)
                    .texOffs(20, 0).addBox(-5.0f, -6.0f, -6.0f, 10.0f, 4.0f, 12.0f)
                    .texOffs(0, 0).addBox(-6.0f, -2.0f, 4.0f, 1.0f, 2.0f, 2.0f)
                    .texOffs(0, 0).addBox(-6.0f, -2.0f, -6.0f, 1.0f, 2.0f, 2.0f)
                    .texOffs(0, 0).addBox(5.0f, -2.0f, 4.0f, 1.0f, 2.0f, 2.0f)
                    .texOffs(0, 0).addBox(5.0f, -2.0f, -6.0f, 1.0f, 2.0f, 2.0f), PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 64)
        }
    }
}