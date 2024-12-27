package com.github.bonndan.humblevehicles.entity.models.insert

import com.github.bonndan.humblevehicles.HumVeeMod
import com.github.bonndan.humblevehicles.entity.models.VesselRenderState
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation


class SteamEngineModel(root: ModelPart) : EntityModel<VesselRenderState>(root.getChild("bb_main")) {

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "steam_engine"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val bb_main = partdefinition.addOrReplaceChild(
                "bb_main",
                CubeListBuilder.create().texOffs(6, 52)
                    .addBox(-4.0f, -14.0f, -14.0f, 8.0f, 8.0f, 9.0f, CubeDeformation(0.0f))
                    .texOffs(44, 38).addBox(-4.0f, -14.0f, -14.0f, 8.0f, 8.0f, 8.0f, CubeDeformation(0.25f))
                    .texOffs(0, 19).addBox(-1.0f, -15.0f, -15.0f, 2.0f, 2.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(0, 30).addBox(-1.5f, -19.0f, -12.0f, 3.0f, 5.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(0, 4).addBox(-1.0f, -16.0f, -7.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(0, 23).addBox(-1.5f, -21.0f, -12.0f, 3.0f, 7.0f, 3.0f, CubeDeformation(0.25f))
                    .texOffs(14, 16).addBox(-1.0f, -15.0f, -15.0f, 2.0f, 2.0f, 1.0f, CubeDeformation(0.25f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}