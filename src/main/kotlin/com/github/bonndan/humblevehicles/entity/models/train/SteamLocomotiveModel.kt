package com.github.bonndan.humblevehicles.entity.models.train

import com.github.bonndan.humblevehicles.HumVeeMod
import com.github.bonndan.humblevehicles.entity.models.PositionAdjusted
import net.minecraft.client.model.MinecartModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation

class SteamLocomotiveModel(root: ModelPart) : MinecartModel(root.getChild("bb_main")), PositionAdjusted {

    override fun getYOffset(): Float {
        return -1.0f
    }

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "steam_locomotive_model"),
            "main"
        )

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val bb_main = partdefinition.addOrReplaceChild(
                "bb_main",
                CubeListBuilder.create().texOffs(0, 46).addBox(-4.0f, -14.0f, -14.0f, 8.0f, 8.0f, 15.0f)
                    .texOffs(46, 0).addBox(-6.0f, -16.0f, 1.0f, 12.0f, 10.0f, 7.0f)
                    .texOffs(32, 23).addBox(-5.0f, -17.0f, 2.0f, 10.0f, 1.0f, 5.0f)
                    .texOffs(0, 0).addBox(-6.0f, -6.0f, -14.0f, 12.0f, 1.0f, 22.0f)
                    .texOffs(0, 23).addBox(-3.0f, -5.0f, -12.0f, 6.0f, 3.0f, 20.0f)
                    .texOffs(0, 0).addBox(-5.0f, -9.0f, -9.0f, 0.0f, 3.0f, 10.0f)
                    .texOffs(37, 31).addBox(-4.0f, -14.0f, -14.0f, 8.0f, 8.0f, 15.0f, CubeDeformation(0.25f))
                    .texOffs(0, 19).addBox(-1.0f, -15.0f, -15.0f, 2.0f, 2.0f, 1.0f)
                    .texOffs(0, 30).addBox(-1.5f, -18.0f, -12.0f, 3.0f, 4.0f, 3.0f)
                    .texOffs(0, 4).addBox(-1.0f, -16.0f, -7.0f, 2.0f, 2.0f, 2.0f)
                    .texOffs(0, 23).addBox(-1.5f, -18.0f, -12.0f, 3.0f, 4.0f, 3.0f, CubeDeformation(0.25f))
                    .texOffs(31, 50).addBox(-4.0f, -4.0f, -11.0f, 1.0f, 4.0f, 4.0f)
                    .texOffs(0, 46).addBox(-4.0f, -4.0f, -2.0f, 1.0f, 4.0f, 4.0f)
                    .texOffs(38, 33).addBox(-4.0f, -4.0f, 3.0f, 1.0f, 4.0f, 4.0f)
                    .texOffs(14, 16).addBox(-1.0f, -15.0f, -15.0f, 2.0f, 2.0f, 1.0f, CubeDeformation(0.25f))
                    .texOffs(8, 33).addBox(3.0f, -4.0f, 3.0f, 1.0f, 4.0f, 4.0f)
                    .texOffs(32, 29).addBox(3.0f, -4.0f, -2.0f, 1.0f, 4.0f, 4.0f)
                    .texOffs(12, 0).addBox(3.0f, -4.0f, -11.0f, 1.0f, 4.0f, 4.0f)
                    .texOffs(0, 54).addBox(3.0f, -5.0f, -6.0f, 2.0f, 4.0f, 3.0f)
                    .texOffs(7, 7).addBox(-1.0f, -5.0f, 8.0f, 2.0f, 2.0f, 1.0f)
                    .texOffs(0, 16).addBox(-3.0f, -7.0f, -15.0f, 6.0f, 2.0f, 1.0f)
                    .texOffs(0, 0).addBox(5.0f, -9.0f, -9.0f, 0.0f, 3.0f, 10.0f)
                    .texOffs(0, 54).mirror().addBox(-5.0f, -5.0f, -6.0f, 2.0f, 4.0f, 3.0f).mirror(false),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            val cube_r1 = bb_main.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, 0.0f, 0.0f, 8.0f, 4.0f, 0.0f),
                PartPose.offsetAndRotation(0.0f, -5.0f, -13.0f, -0.7854f, 0.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}