package com.github.bonndan.humblevehicles.entity.models

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.github.bonndan.humblevehicles.HumVeeMod
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity


// Made with Blockbench 4.11.1
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

const val RIDING_POSITION_Y_OFFSET = -0.6

class SubmarineModel<T : Entity>(root: ModelPart) : EntityModel<T>(
    SubmarineRenderType.get(
        ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "textures/entity/submarine.png")
    )
) {

    private val bone: ModelPart = root.getChild("bone")
    private val bone2: ModelPart = root.getChild("bone2")
    private val bone3: ModelPart = root.getChild("bone3")
    private val bone4: ModelPart = root.getChild("bone4")
    private val bb_main: ModelPart = root.getChild("bb_main")

    override fun setupAnim(
        entity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        //deprecated
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        bone2.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        bone3.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        bone4.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
    }

    companion object {

        const val WIDTH: Float = 0.7f
        const val HEIGHT: Float = 1.0f

        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "submarine_model"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val bone =
                partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f))

            val bone2 =
                partdefinition.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 11.0f))

            val bone3 =
                partdefinition.addOrReplaceChild("bone3", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 11.0f))

            val bone4 =
                partdefinition.addOrReplaceChild("bone4", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f))

            val bb_main = partdefinition.addOrReplaceChild(
                "bb_main",
                CubeListBuilder.create().texOffs(-5, -5)
                    .addBox(-6.0f, -27.0f, -12.0f, 17.0f, 24.0f, 25.0f, CubeDeformation(0.0f))
                    .texOffs(35, 26).addBox(-6.0f, -3.0f, -11.0f, 17.0f, 3.0f, 23.0f, CubeDeformation(0.0f))
                    .texOffs(0, 14).addBox(-4.0f, -25.0f, -14.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(9, 0).addBox(7.0f, -25.0f, -14.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.25f))
                    .texOffs(9, 0).addBox(-4.0f, -25.0f, -14.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.25f))
                    .texOffs(0, 14).addBox(7.0f, -25.0f, -14.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 3.0f, 0.0f)
            )

            val cube_r1 = bb_main.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(0, 30)
                    .addBox(0.0f, 0.0f, -9.5f, 0.0f, 1.0f, 19.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(6.0f, -4.0f, -1.5f, 0.0f, 0.0f, -0.0436f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}