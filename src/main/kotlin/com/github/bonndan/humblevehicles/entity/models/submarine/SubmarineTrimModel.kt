package com.github.bonndan.humblevehicles.entity.models.submarine

import com.github.bonndan.humblevehicles.HumVeeMod
import com.github.bonndan.humblevehicles.entity.models.SubmarineRenderType
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.WaterPatchModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity

const val WATER_PATCH = "water_patch"

class SubmarineTrimModel<T : Entity>(private val root: ModelPart) : EntityModel<T>(
    SubmarineRenderType.get(
        ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "textures/entity/submarine_trim.png")
    )
), WaterPatchModel {

    override fun waterPatch(): ModelPart {
        return root.getChild(WATER_PATCH)
    }

    override fun setupAnim(
        entity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        val u_boot = root.getChild("u_boot")
        u_boot.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
    }

    companion object {

        const val WIDTH: Float = 0.7f
        const val HEIGHT: Float = 1.0f
        const val MODEL_Y_OFFSET = 1.7

        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "trim_submarine_model"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val u_boot =
                partdefinition.addOrReplaceChild("u_boot", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f))

            val untere_teil = u_boot.addOrReplaceChild(
                "untere_teil",
                CubeListBuilder.create(),
                PartPose.offset(0.2473f, -8.1047f, -0.3566f)
            )

            val cube_r1 = untere_teil.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(58, 0)
                    .addBox(-3.0f, -1.0f, -8.0f, 6.0f, 2.0f, 16.0f, CubeDeformation(0.01f)),
                PartPose.offsetAndRotation(0.0027f, -4.8953f, 16.1066f, 0.0f, -1.5708f, 1.5708f)
            )

            val cube_r2 = untere_teil.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(58, 0)
                    .addBox(-3.0f, -1.0f, -8.0f, 6.0f, 2.0f, 16.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0027f, -4.8953f, 16.1066f, 0.0f, -1.5708f, -3.1416f)
            )

            val cube_r3 = untere_teil.addOrReplaceChild(
                "cube_r3",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(66.2f, 22.2f, -3.15f, 16.0f, 1.0f, 13.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 0.0f, -1.5708f, 0.0f)
            )

            val cube_r4 = untere_teil.addOrReplaceChild(
                "cube_r4",
                CubeListBuilder.create().texOffs(0, 28)
                    .addBox(66.4f, 17.6416f, 3.1621f, 16.0f, 1.0f, 11.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 0.0f, -1.5708f, -0.7854f)
            )

            val cube_r5 = untere_teil.addOrReplaceChild(
                "cube_r5",
                CubeListBuilder.create().texOffs(12, 120)
                    .addBox(38.1783f, 72.615f, 9.2f, 4.0f, 1.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 1.5708f, -0.6109f, -3.1416f)
            )

            val cube_r6 = untere_teil.addOrReplaceChild(
                "cube_r6",
                CubeListBuilder.create().texOffs(68, 99)
                    .addBox(-7.6811f, 83.5186f, 9.2f, 10.0f, 1.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 1.5708f, 0.0f, 3.1416f)
            )

            val cube_r7 = untere_teil.addOrReplaceChild(
                "cube_r7",
                CubeListBuilder.create().texOffs(34, 93)
                    .addBox(28.205f, 79.5983f, -3.3249f, 4.0f, 1.0f, 13.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 1.5708f, -0.6109f, -1.5708f)
            )

            val cube_r8 = untere_teil.addOrReplaceChild(
                "cube_r8",
                CubeListBuilder.create().texOffs(98, 25)
                    .addBox(81.1738f, -23.6218f, 3.1621f, 1.0f, 5.0f, 11.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, -1.5708f, -1.1345f, 0.7854f)
            )

            val cube_r9 = untere_teil.addOrReplaceChild(
                "cube_r9",
                CubeListBuilder.create().texOffs(58, 18)
                    .addBox(66.2f, 9.95f, 9.2f, 16.0f, 1.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 0.0f, -1.5708f, -1.5708f)
            )

            val cube_r10 = untere_teil.addOrReplaceChild(
                "cube_r10",
                CubeListBuilder.create().texOffs(58, 18).mirror()
                    .addBox(-82.2f, 9.95f, 9.2f, 16.0f, 1.0f, 6.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-3.3473f, -14.0953f, -71.9934f, 0.0f, 1.5708f, 1.5708f)
            )

            val cube_r11 = untere_teil.addOrReplaceChild(
                "cube_r11",
                CubeListBuilder.create().texOffs(0, 28).mirror()
                    .addBox(-82.4f, 17.6416f, 3.1621f, 16.0f, 1.0f, 11.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-3.3473f, -14.0953f, -71.9934f, 0.0f, 1.5708f, 0.7854f)
            )

            val cube_r12 = untere_teil.addOrReplaceChild(
                "cube_r12",
                CubeListBuilder.create().texOffs(66, 79)
                    .addBox(-19.8562f, 83.5186f, -3.3249f, 5.0f, 1.0f, 13.0f, CubeDeformation(0.01f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 1.5708f, 0.0f, -1.5708f)
            )

            val cube_r13 = untere_teil.addOrReplaceChild(
                "cube_r13",
                CubeListBuilder.create().texOffs(12, 120)
                    .addBox(32.5261f, 76.5726f, -15.2f, 4.0f, 1.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 1.5708f, -0.6109f, 0.0f)
            )

            val cube_r14 = untere_teil.addOrReplaceChild(
                "cube_r14",
                CubeListBuilder.create().texOffs(68, 99)
                    .addBox(-14.5811f, 83.5186f, -15.2f, 10.0f, 1.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 1.5708f, 0.0f, 0.0f)
            )

            val cube_r15 = untere_teil.addOrReplaceChild(
                "cube_r15",
                CubeListBuilder.create().texOffs(98, 25)
                    .addBox(83.1461f, -19.3922f, -9.2123f, 1.0f, 5.0f, 11.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, -1.5708f, -1.1345f, 2.3562f)
            )

            val obere_teil = u_boot.addOrReplaceChild(
                "obere_teil",
                CubeListBuilder.create(),
                PartPose.offset(0.1881f, -22.3076f, 2.5222f)
            )

            val cube_r16 = obere_teil.addOrReplaceChild(
                "cube_r16",
                CubeListBuilder.create().texOffs(54, 46)
                    .addBox(-3.2458f, 13.3f, -9.7002f, 16.0f, 1.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 0.0f, -1.5708f, 1.5708f)
            )

            val cube_r17 = obere_teil.addOrReplaceChild(
                "cube_r17",
                CubeListBuilder.create().texOffs(102, 3)
                    .addBox(13.2301f, -5.0047f, 2.5684f, 1.0f, 5.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, -1.5708f, -1.1345f, -0.7854f)
            )

            val cube_r18 = obere_teil.addOrReplaceChild(
                "cube_r18",
                CubeListBuilder.create().texOffs(102, 3)
                    .addBox(13.4094f, -4.6202f, -12.7098f, 1.0f, 5.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, -1.5708f, -1.1345f, -2.3562f)
            )

            val cube_r19 = obere_teil.addOrReplaceChild(
                "cube_r19",
                CubeListBuilder.create().texOffs(100, 99)
                    .addBox(-11.2311f, 14.0728f, -10.7002f, 10.0f, 1.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 1.5708f, 0.0f, 0.0f)
            )

            val cube_r20 = obere_teil.addOrReplaceChild(
                "cube_r20",
                CubeListBuilder.create().texOffs(124, 114)
                    .addBox(-4.5622f, 17.7645f, -10.7002f, 4.0f, 1.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 1.5708f, -0.6109f, 0.0f)
            )

            val cube_r21 = obere_teil.addOrReplaceChild(
                "cube_r21",
                CubeListBuilder.create().texOffs(0, 40)
                    .addBox(-3.0458f, 5.9388f, -13.5612f, 16.0f, 1.0f, 11.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 0.0f, -1.5708f, 2.3562f)
            )

            val cube_r22 = obere_teil.addOrReplaceChild(
                "cube_r22",
                CubeListBuilder.create().texOffs(100, 99)
                    .addBox(-11.0311f, 14.0728f, 4.7002f, 10.0f, 1.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 1.5708f, 0.0f, 3.1416f)
            )

            val cube_r23 = obere_teil.addOrReplaceChild(
                "cube_r23",
                CubeListBuilder.create().texOffs(124, 114)
                    .addBox(-4.3984f, 17.6497f, 4.7002f, 4.0f, 1.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 1.5708f, -0.6109f, -3.1416f)
            )

            val cube_r24 = obere_teil.addOrReplaceChild(
                "cube_r24",
                CubeListBuilder.create().texOffs(66, 65)
                    .addBox(-0.1279f, 14.0728f, -6.797f, 5.0f, 1.0f, 13.0f, CubeDeformation(0.01f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 1.5708f, 0.0f, 1.5708f)
            )

            val cube_r25 = obere_teil.addOrReplaceChild(
                "cube_r25",
                CubeListBuilder.create().texOffs(0, 89)
                    .addBox(4.533f, 11.3959f, -6.797f, 4.0f, 1.0f, 13.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 1.5708f, -0.6109f, 1.5708f)
            )

            val cube_r26 = obere_teil.addOrReplaceChild(
                "cube_r26",
                CubeListBuilder.create().texOffs(0, 14)
                    .addBox(-3.2458f, 2.0998f, -6.7f, 16.0f, 1.0f, 13.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 0.0f, -1.5708f, -3.1416f)
            )

            val cube_r27 = obere_teil.addOrReplaceChild(
                "cube_r27",
                CubeListBuilder.create().texOffs(0, 40)
                    .addBox(-3.0458f, 5.7974f, 2.4198f, 16.0f, 1.0f, 11.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 0.0f, -1.5708f, -2.3562f)
            )

            val cube_r28 = obere_teil.addOrReplaceChild(
                "cube_r28",
                CubeListBuilder.create().texOffs(54, 46)
                    .addBox(-3.2458f, 13.3f, 4.7002f, 16.0f, 1.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 0.0f, -1.5708f, -1.5708f)
            )

            val innenaustattung = u_boot.addOrReplaceChild(
                "innenaustattung",
                CubeListBuilder.create(),
                PartPose.offset(0.25f, -8.2525f, -8.0123f)
            )

            val cube_r29 = innenaustattung.addOrReplaceChild(
                "cube_r29",
                CubeListBuilder.create().texOffs(12, 103)
                    .addBox(51.8794f, -4.15f, 24.2395f, 8.0f, 15.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.35f, -13.9475f, -64.3377f, 0.0f, -0.8727f, 1.5708f)
            )

            val cube_r30 = innenaustattung.addOrReplaceChild(
                "cube_r30",
                CubeListBuilder.create().texOffs(108, 140)
                    .addBox(54.354f, -0.15f, 31.985f, 3.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(84, 135).addBox(55.354f, 0.35f, 27.985f, 1.0f, 1.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(140, 76).addBox(54.354f, 4.85f, 31.985f, 3.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(96, 135).addBox(55.354f, 5.35f, 27.985f, 1.0f, 1.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(140, 71).addBox(53.971f, 3.85f, 26.6637f, 4.0f, 4.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(140, 66).addBox(53.971f, -1.15f, 26.6637f, 4.0f, 4.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(60, 118).addBox(52.971f, -3.15f, 24.6637f, 6.0f, 13.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.35f, -13.9475f, -64.3377f, 0.0f, -0.8727f, 1.5708f)
            )

            val cube_r31 = innenaustattung.addOrReplaceChild(
                "cube_r31",
                CubeListBuilder.create().texOffs(0, 52)
                    .addBox(78.6128f, -6.8298f, -2.65f, 4.0f, 20.0f, 12.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.35f, -13.9475f, -64.3377f, -1.5708f, -1.4835f, 1.5708f)
            )

            val cube_r32 = innenaustattung.addOrReplaceChild(
                "cube_r32",
                CubeListBuilder.create().texOffs(32, 52)
                    .addBox(65.0297f, 19.184f, -2.65f, 13.0f, 1.0f, 12.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.35f, -13.9475f, -64.3377f, 0.0f, -1.5708f, 0.0f)
            )

            addWaterPatch(partdefinition)

            return LayerDefinition.create(meshdefinition, 256, 256)
        }

        private fun addWaterPatch(partDefinition: PartDefinition) {

            partDefinition.addOrReplaceChild(
                WATER_PATCH,
                CubeListBuilder.create().texOffs(0, 0).addBox(-14.0f, -9.0f, -3.0f, 15.0f, 3.0f, 25.0f),
                PartPose.offsetAndRotation(0.25f, 15f, -14.343f, 0.0f, 0.0f, 0.0f)
            )
        }
    }
}