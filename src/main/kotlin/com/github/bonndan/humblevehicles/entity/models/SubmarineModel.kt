package com.github.bonndan.humblevehicles.entity.models

import com.github.bonndan.humblevehicles.HumVeeMod
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.LightTexture.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import java.awt.Color.white

const val RIDING_POSITION_Y_OFFSET = -0.6

class SubmarineModel<T : Entity>(root: ModelPart) : EntityModel<T>(
    SubmarineRenderType.get(
        ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "textures/entity/submarine.png")
    )
) {
    private val u_boot: ModelPart = root.getChild("u_boot")
    private val untere_teil: ModelPart = u_boot.getChild("untere_teil")
    private val obere_teil: ModelPart = u_boot.getChild("obere_teil")
    private val glass_unten: ModelPart = u_boot.getChild("glass_unten")
    private val glass_oben: ModelPart = u_boot.getChild("glass_oben")
    private val lampen: ModelPart = u_boot.getChild("lampen")
    private val innenaustattung: ModelPart = u_boot.getChild("innenaustattung")
    private val flosse: ModelPart = u_boot.getChild("flosse")

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
        u_boot.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        lampen.render(poseStack, vertexConsumer, FULL_BRIGHT, packedOverlay, white.rgb)
    }

    companion object {

        const val WIDTH: Float = 0.7f
        const val HEIGHT: Float = 1.0f
        const val MODEL_Y_OFFSET = 1.7

        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "submarine_model"), "main")

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
                CubeListBuilder.create().texOffs(102, 118)
                    .addBox(-138.6f, 5.2f, -0.65f, 0.0f, 8.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, 119.0066f, 0.0f, -1.5708f, 0.0f)
            )

            val cube_r2 = untere_teil.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(84, 118)
                    .addBox(85.1f, 6.2f, 0.35f, 3.0f, 6.0f, 6.0f, CubeDeformation(0.0f))
                    .texOffs(108, 135).addBox(74.4f, 24.2f, 2.35f, 4.0f, 3.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(100, 130).addBox(77.3f, 27.3f, 2.35f, 6.0f, 3.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(68, 93).addBox(66.4f, 21.2f, 1.85f, 16.0f, 3.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(0, 103).addBox(82.2f, -1.8f, 1.85f, 3.0f, 22.0f, 3.0f, CubeDeformation(-0.01f))
                    .texOffs(0, 136).addBox(51.7f, 6.8f, -2.45f, 3.0f, 5.0f, 2.0f, CubeDeformation(0.01f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 0.0f, -1.5708f, 0.0f)
            )

            val cube_r3 = untere_teil.addOrReplaceChild(
                "cube_r3",
                CubeListBuilder.create().texOffs(32, 65)
                    .addBox(88.1f, -9.35f, 3.2f, 5.0f, 12.0f, 12.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -72.0934f, 0.0f, -1.5708f, -1.5708f)
            )

            val cube_r4 = untere_teil.addOrReplaceChild(
                "cube_r4",
                CubeListBuilder.create().texOffs(140, 62).mirror()
                    .addBox(31.5587f, 75.4495f, -9.3742f, 5.0f, 3.0f, 1.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-3.3473f, -14.0953f, -71.9934f, 1.5708f, -0.6109f, -0.7854f)
            )

            val cube_r5 = untere_teil.addOrReplaceChild(
                "cube_r5",
                CubeListBuilder.create().texOffs(130, 18).mirror()
                    .addBox(37.7293f, -74.1035f, 7.7f, 5.0f, 3.0f, 3.0f, CubeDeformation(0.01f)).mirror(false),
                PartPose.offsetAndRotation(-3.3473f, -14.0953f, -71.9934f, -1.5708f, -0.6109f, 0.0f)
            )

            val cube_r6 = untere_teil.addOrReplaceChild(
                "cube_r6",
                CubeListBuilder.create().texOffs(82, 58).mirror()
                    .addBox(-82.4f, -11.65f, -10.7f, 16.0f, 3.0f, 3.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-3.3473f, -14.0953f, -71.9934f, 0.0f, 1.5708f, -1.5708f)
            )

            val cube_r7 = untere_teil.addOrReplaceChild(
                "cube_r7",
                CubeListBuilder.create().texOffs(58, 25).mirror()
                    .addBox(-82.4f, 17.1366f, 8.3742f, 16.0f, 2.0f, 1.0f, CubeDeformation(0.0f)).mirror(false)
                    .texOffs(30, 138).mirror().addBox(-54.7f, 1.7073f, 3.1449f, 3.0f, 5.0f, 2.0f, CubeDeformation(0.0f))
                    .mirror(false),
                PartPose.offsetAndRotation(-3.3473f, -14.0953f, -71.9934f, 0.0f, 1.5708f, 0.7854f)
            )

            val cube_r8 = untere_teil.addOrReplaceChild(
                "cube_r8",
                CubeListBuilder.create().texOffs(124, 15).mirror()
                    .addBox(-69.079f, -3.731f, 8.3742f, 10.0f, 2.0f, 1.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-3.3473f, -14.0953f, -71.9934f, -1.5708f, 1.2654f, -0.7854f)
            )

            val cube_r9 = untere_teil.addOrReplaceChild(
                "cube_r9",
                CubeListBuilder.create().texOffs(128, 46).mirror()
                    .addBox(-48.8257f, -35.1678f, 8.3742f, 8.0f, 2.0f, 1.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-3.3473f, -14.0953f, -71.9934f, -1.5708f, 0.6981f, -0.7854f)
            )

            val cube_r10 = untere_teil.addOrReplaceChild(
                "cube_r10",
                CubeListBuilder.create().texOffs(114, 70).mirror()
                    .addBox(-66.8277f, 8.8711f, -10.7f, 10.0f, 3.0f, 3.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-3.3473f, -14.0953f, -71.9934f, 1.5708f, 1.2654f, 0.0f)
            )

            val cube_r11 = untere_teil.addOrReplaceChild(
                "cube_r11",
                CubeListBuilder.create().texOffs(122, 30).mirror()
                    .addBox(35.2329f, -41.4006f, -8.975f, 8.0f, 3.0f, 3.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-3.3473f, -14.0953f, -71.9934f, -1.5272f, -0.6981f, 3.1416f)
            )

            val cube_r12 = untere_teil.addOrReplaceChild(
                "cube_r12",
                CubeListBuilder.create().texOffs(60, 139).mirror()
                    .addBox(-54.7f, 6.4449f, -10.0073f, 3.0f, 5.0f, 2.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-3.3473f, -14.0953f, -71.9934f, 0.0f, 1.5708f, -0.7854f)
            )

            val cube_r13 = untere_teil.addOrReplaceChild(
                "cube_r13",
                CubeListBuilder.create().texOffs(0, 136).mirror()
                    .addBox(-54.7f, 6.8f, -2.45f, 3.0f, 5.0f, 2.0f, CubeDeformation(0.01f)).mirror(false),
                PartPose.offsetAndRotation(-3.3473f, -14.0953f, -71.9934f, 0.0f, 1.5708f, 0.0f)
            )

            val cube_r14 = untere_teil.addOrReplaceChild(
                "cube_r14",
                CubeListBuilder.create().texOffs(60, 133)
                    .addBox(77.2f, 16.65f, 13.2f, 1.0f, 1.0f, 5.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(-17.1473f, -16.0953f, -56.9934f, 0.0f, -1.5708f, -1.5708f)
            )

            val cube_r15 = untere_teil.addOrReplaceChild(
                "cube_r15",
                CubeListBuilder.create().texOffs(84, 130)
                    .addBox(76.7055f, -29.0513f, 2.35f, 6.0f, 3.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(0, 84).addBox(69.7907f, -26.0489f, 2.35f, 10.0f, 3.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, -1.5708f, -0.9163f, 1.5708f)
            )

            val cube_r16 = untere_teil.addOrReplaceChild(
                "cube_r16",
                CubeListBuilder.create().texOffs(102, 64)
                    .addBox(82.2f, -7.65f, -10.7f, 3.0f, 22.0f, 3.0f, CubeDeformation(0.02f))
                    .texOffs(82, 58).addBox(66.4f, -11.65f, -10.7f, 16.0f, 3.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(10, 136).addBox(51.7f, 0.85f, -5.5f, 3.0f, 5.0f, 2.0f, CubeDeformation(0.01f))
                    .texOffs(20, 136).addBox(51.7f, 0.85f, -15.1f, 3.0f, 5.0f, 2.0f, CubeDeformation(0.01f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 0.0f, -1.5708f, 1.5708f)
            )

            val cube_r17 = untere_teil.addOrReplaceChild(
                "cube_r17",
                CubeListBuilder.create().texOffs(130, 18)
                    .addBox(-42.7293f, -74.1035f, 7.7f, 5.0f, 3.0f, 3.0f, CubeDeformation(0.01f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, -1.5708f, 0.6109f, 0.0f)
            )

            val cube_r18 = untere_teil.addOrReplaceChild(
                "cube_r18",
                CubeListBuilder.create().texOffs(114, 70)
                    .addBox(56.8277f, 8.8711f, -10.7f, 10.0f, 3.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 1.5708f, -1.2654f, 0.0f)
            )

            val cube_r19 = untere_teil.addOrReplaceChild(
                "cube_r19",
                CubeListBuilder.create().texOffs(122, 30)
                    .addBox(-43.2329f, -41.4006f, -8.975f, 8.0f, 3.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, -1.5272f, 0.6981f, -3.1416f)
            )

            val cube_r20 = untere_teil.addOrReplaceChild(
                "cube_r20",
                CubeListBuilder.create().texOffs(122, 24)
                    .addBox(44.7045f, -32.9131f, 1.85f, 8.0f, 3.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, -1.5708f, -0.6981f, 1.5708f)
            )

            val cube_r21 = untere_teil.addOrReplaceChild(
                "cube_r21",
                CubeListBuilder.create().texOffs(114, 76)
                    .addBox(60.6016f, 0.098f, 1.85f, 10.0f, 3.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, -1.5708f, -1.2654f, 1.5708f)
            )

            val cube_r22 = untere_teil.addOrReplaceChild(
                "cube_r22",
                CubeListBuilder.create().texOffs(58, 0)
                    .addBox(-3.0f, -1.0f, -8.0f, 6.0f, 2.0f, 16.0f, CubeDeformation(0.01f)),
                PartPose.offsetAndRotation(0.0027f, -4.8953f, 16.1066f, 0.0f, -1.5708f, 1.5708f)
            )

            val cube_r23 = untere_teil.addOrReplaceChild(
                "cube_r23",
                CubeListBuilder.create().texOffs(58, 0)
                    .addBox(-3.0f, -1.0f, -8.0f, 6.0f, 2.0f, 16.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0027f, -4.8953f, 16.1066f, 0.0f, -1.5708f, -3.1416f)
            )

            val cube_r24 = untere_teil.addOrReplaceChild(
                "cube_r24",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(66.2f, 22.2f, -3.15f, 16.0f, 1.0f, 13.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 0.0f, -1.5708f, 0.0f)
            )

            val cube_r25 = untere_teil.addOrReplaceChild(
                "cube_r25",
                CubeListBuilder.create().texOffs(0, 28)
                    .addBox(66.4f, 17.6416f, 3.1621f, 16.0f, 1.0f, 11.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 0.0f, -1.5708f, -0.7854f)
            )

            val cube_r26 = untere_teil.addOrReplaceChild(
                "cube_r26",
                CubeListBuilder.create().texOffs(12, 120)
                    .addBox(38.1783f, 72.615f, 9.2f, 4.0f, 1.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 1.5708f, -0.6109f, -3.1416f)
            )

            val cube_r27 = untere_teil.addOrReplaceChild(
                "cube_r27",
                CubeListBuilder.create().texOffs(68, 99)
                    .addBox(-7.6811f, 83.5186f, 9.2f, 10.0f, 1.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 1.5708f, 0.0f, 3.1416f)
            )

            val cube_r28 = untere_teil.addOrReplaceChild(
                "cube_r28",
                CubeListBuilder.create().texOffs(34, 93)
                    .addBox(28.205f, 79.5983f, -3.3249f, 4.0f, 1.0f, 13.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 1.5708f, -0.6109f, -1.5708f)
            )

            val cube_r29 = untere_teil.addOrReplaceChild(
                "cube_r29",
                CubeListBuilder.create().texOffs(98, 25)
                    .addBox(81.1738f, -23.6218f, 3.1621f, 1.0f, 5.0f, 11.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, -1.5708f, -1.1345f, 0.7854f)
            )

            val cube_r30 = untere_teil.addOrReplaceChild(
                "cube_r30",
                CubeListBuilder.create().texOffs(58, 18)
                    .addBox(66.2f, 9.95f, 9.2f, 16.0f, 1.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 0.0f, -1.5708f, -1.5708f)
            )

            val cube_r31 = untere_teil.addOrReplaceChild(
                "cube_r31",
                CubeListBuilder.create().texOffs(58, 18).mirror()
                    .addBox(-82.2f, 9.95f, 9.2f, 16.0f, 1.0f, 6.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-3.3473f, -14.0953f, -71.9934f, 0.0f, 1.5708f, 1.5708f)
            )

            val cube_r32 = untere_teil.addOrReplaceChild(
                "cube_r32",
                CubeListBuilder.create().texOffs(0, 28).mirror()
                    .addBox(-82.4f, 17.6416f, 3.1621f, 16.0f, 1.0f, 11.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-3.3473f, -14.0953f, -71.9934f, 0.0f, 1.5708f, 0.7854f)
            )

            val cube_r33 = untere_teil.addOrReplaceChild(
                "cube_r33",
                CubeListBuilder.create().texOffs(66, 79)
                    .addBox(-19.8562f, 83.5186f, -3.3249f, 5.0f, 1.0f, 13.0f, CubeDeformation(0.01f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 1.5708f, 0.0f, -1.5708f)
            )

            val cube_r34 = untere_teil.addOrReplaceChild(
                "cube_r34",
                CubeListBuilder.create().texOffs(12, 120)
                    .addBox(32.5261f, 76.5726f, -15.2f, 4.0f, 1.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 1.5708f, -0.6109f, 0.0f)
            )

            val cube_r35 = untere_teil.addOrReplaceChild(
                "cube_r35",
                CubeListBuilder.create().texOffs(68, 99)
                    .addBox(-14.5811f, 83.5186f, -15.2f, 10.0f, 1.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 1.5708f, 0.0f, 0.0f)
            )

            val cube_r36 = untere_teil.addOrReplaceChild(
                "cube_r36",
                CubeListBuilder.create().texOffs(98, 25)
                    .addBox(83.1461f, -19.3922f, -9.2123f, 1.0f, 5.0f, 11.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, -1.5708f, -1.1345f, 2.3562f)
            )

            val cube_r37 = untere_teil.addOrReplaceChild(
                "cube_r37",
                CubeListBuilder.create().texOffs(140, 62)
                    .addBox(-36.5587f, 75.4495f, -9.3742f, 5.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 1.5708f, 0.6109f, 0.7854f)
            )

            val cube_r38 = untere_teil.addOrReplaceChild(
                "cube_r38",
                CubeListBuilder.create().texOffs(76, 118)
                    .addBox(82.2f, -15.1366f, -9.3742f, 3.0f, 22.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 0.0f, -1.5708f, 2.3562f)
            )

            val cube_r39 = untere_teil.addOrReplaceChild(
                "cube_r39",
                CubeListBuilder.create().texOffs(116, 112)
                    .addBox(82.2f, -2.1258f, -4.6366f, 3.0f, 22.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 0.0f, -1.5708f, 0.7854f)
            )

            val cube_r40 = untere_teil.addOrReplaceChild(
                "cube_r40",
                CubeListBuilder.create().texOffs(30, 138)
                    .addBox(51.7f, 1.7073f, 3.1449f, 3.0f, 5.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(58, 25).addBox(66.4f, 17.1366f, 8.3742f, 16.0f, 2.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 0.0f, -1.5708f, -0.7854f)
            )

            val cube_r41 = untere_teil.addOrReplaceChild(
                "cube_r41",
                CubeListBuilder.create().texOffs(60, 139)
                    .addBox(51.7f, 6.4449f, -10.0073f, 3.0f, 5.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 0.0f, -1.5708f, 0.7854f)
            )

            val cube_r42 = untere_teil.addOrReplaceChild(
                "cube_r42",
                CubeListBuilder.create().texOffs(0, 130)
                    .addBox(-32.411f, 78.3538f, -4.85f, 5.0f, 3.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, 1.5708f, 0.6109f, 1.5708f)
            )

            val cube_r43 = untere_teil.addOrReplaceChild(
                "cube_r43",
                CubeListBuilder.create().texOffs(128, 46)
                    .addBox(40.8257f, -35.1678f, 8.3742f, 8.0f, 2.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, -1.5708f, -0.6981f, 0.7854f)
            )

            val cube_r44 = untere_teil.addOrReplaceChild(
                "cube_r44",
                CubeListBuilder.create().texOffs(124, 15)
                    .addBox(59.079f, -3.731f, 8.3742f, 10.0f, 2.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.3527f, -14.0953f, -71.9934f, -1.5708f, -1.2654f, 0.7854f)
            )

            val cube_r45 = untere_teil.addOrReplaceChild(
                "cube_r45",
                CubeListBuilder.create().texOffs(102, 118).mirror()
                    .addBox(138.6f, 5.2f, -0.65f, 0.0f, 8.0f, 4.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-3.3473f, -14.0953f, 119.0066f, 0.0f, 1.5708f, 0.0f)
            )

            val obere_teil = u_boot.addOrReplaceChild(
                "obere_teil",
                CubeListBuilder.create(),
                PartPose.offset(0.1881f, -22.3076f, 2.5222f)
            )

            val cube_r46 = obere_teil.addOrReplaceChild(
                "cube_r46",
                CubeListBuilder.create().texOffs(32, 130).mirror()
                    .addBox(4.7781f, -12.5084f, -8.0662f, 8.0f, 2.0f, 1.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 1.5708f, 0.6981f, 0.7854f)
            )

            val cube_r47 = obere_teil.addOrReplaceChild(
                "cube_r47",
                CubeListBuilder.create().texOffs(124, 121).mirror()
                    .addBox(0.6718f, -7.9906f, -8.0662f, 10.0f, 2.0f, 1.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 1.5708f, 1.2654f, 0.7854f)
            )

            val cube_r48 = obere_teil.addOrReplaceChild(
                "cube_r48",
                CubeListBuilder.create().texOffs(102, 0).mirror()
                    .addBox(-12.9542f, -7.4338f, -8.0662f, 16.0f, 2.0f, 1.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 0.0f, 1.5708f, -0.7854f)
            )

            val cube_r49 = obere_teil.addOrReplaceChild(
                "cube_r49",
                CubeListBuilder.create().texOffs(138, 82).mirror()
                    .addBox(1.3506f, -14.7985f, 7.0662f, 5.0f, 3.0f, 1.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, -1.5708f, -0.6109f, 0.7854f)
            )

            val cube_r50 = obere_teil.addOrReplaceChild(
                "cube_r50",
                CubeListBuilder.create().texOffs(54, 46)
                    .addBox(-3.2458f, 13.3f, -9.7002f, 16.0f, 1.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 0.0f, -1.5708f, 1.5708f)
            )

            val cube_r51 = obere_teil.addOrReplaceChild(
                "cube_r51",
                CubeListBuilder.create().texOffs(102, 3)
                    .addBox(13.2301f, -5.0047f, 2.5684f, 1.0f, 5.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, -1.5708f, -1.1345f, -0.7854f)
            )

            val cube_r52 = obere_teil.addOrReplaceChild(
                "cube_r52",
                CubeListBuilder.create().texOffs(102, 3)
                    .addBox(13.4094f, -4.6202f, -12.7098f, 1.0f, 5.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, -1.5708f, -1.1345f, -2.3562f)
            )

            val cube_r53 = obere_teil.addOrReplaceChild(
                "cube_r53",
                CubeListBuilder.create().texOffs(100, 99)
                    .addBox(-11.2311f, 14.0728f, -10.7002f, 10.0f, 1.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 1.5708f, 0.0f, 0.0f)
            )

            val cube_r54 = obere_teil.addOrReplaceChild(
                "cube_r54",
                CubeListBuilder.create().texOffs(124, 114)
                    .addBox(-4.5622f, 17.7645f, -10.7002f, 4.0f, 1.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 1.5708f, -0.6109f, 0.0f)
            )

            val cube_r55 = obere_teil.addOrReplaceChild(
                "cube_r55",
                CubeListBuilder.create().texOffs(0, 40)
                    .addBox(-3.0458f, 5.9388f, -13.5612f, 16.0f, 1.0f, 11.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 0.0f, -1.5708f, 2.3562f)
            )

            val cube_r56 = obere_teil.addOrReplaceChild(
                "cube_r56",
                CubeListBuilder.create().texOffs(100, 99)
                    .addBox(-11.0311f, 14.0728f, 4.7002f, 10.0f, 1.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 1.5708f, 0.0f, 3.1416f)
            )

            val cube_r57 = obere_teil.addOrReplaceChild(
                "cube_r57",
                CubeListBuilder.create().texOffs(124, 114)
                    .addBox(-4.3984f, 17.6497f, 4.7002f, 4.0f, 1.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 1.5708f, -0.6109f, -3.1416f)
            )

            val cube_r58 = obere_teil.addOrReplaceChild(
                "cube_r58",
                CubeListBuilder.create().texOffs(120, 135)
                    .addBox(4.9542f, -7.2998f, -1.0f, 4.0f, 3.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(124, 130).addBox(7.8542f, -10.3998f, -1.0f, 6.0f, 3.0f, 2.0f, CubeDeformation(0.02f))
                    .texOffs(82, 52).addBox(-3.0458f, -4.2998f, -1.5f, 16.0f, 3.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 0.0f, -1.5708f, 0.0f)
            )

            val cube_r59 = obere_teil.addOrReplaceChild(
                "cube_r59",
                CubeListBuilder.create().texOffs(132, 95)
                    .addBox(9.4959f, -0.4368f, -1.0f, 6.0f, 3.0f, 2.0f, CubeDeformation(-0.01f))
                    .texOffs(114, 82).addBox(2.5812f, -3.4391f, -1.0f, 10.0f, 3.0f, 2.0f, CubeDeformation(0.01f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 1.5708f, -0.9163f, -1.5708f)
            )

            val cube_r60 = obere_teil.addOrReplaceChild(
                "cube_r60",
                CubeListBuilder.create().texOffs(66, 65)
                    .addBox(-0.1279f, 14.0728f, -6.797f, 5.0f, 1.0f, 13.0f, CubeDeformation(0.01f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 1.5708f, 0.0f, 1.5708f)
            )

            val cube_r61 = obere_teil.addOrReplaceChild(
                "cube_r61",
                CubeListBuilder.create().texOffs(0, 89)
                    .addBox(4.533f, 11.3959f, -6.797f, 4.0f, 1.0f, 13.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 1.5708f, -0.6109f, 1.5708f)
            )

            val cube_r62 = obere_teil.addOrReplaceChild(
                "cube_r62",
                CubeListBuilder.create().texOffs(0, 14)
                    .addBox(-3.2458f, 2.0998f, -6.7f, 16.0f, 1.0f, 13.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 0.0f, -1.5708f, -3.1416f)
            )

            val cube_r63 = obere_teil.addOrReplaceChild(
                "cube_r63",
                CubeListBuilder.create().texOffs(0, 40)
                    .addBox(-3.0458f, 5.7974f, 2.4198f, 16.0f, 1.0f, 11.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 0.0f, -1.5708f, -2.3562f)
            )

            val cube_r64 = obere_teil.addOrReplaceChild(
                "cube_r64",
                CubeListBuilder.create().texOffs(54, 46)
                    .addBox(-3.2458f, 13.3f, 4.7002f, 16.0f, 1.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 0.0f, -1.5708f, -1.5708f)
            )

            val cube_r65 = obere_teil.addOrReplaceChild(
                "cube_r65",
                CubeListBuilder.create().texOffs(16, 130)
                    .addBox(-8.9178f, -13.0009f, -1.5f, 5.0f, 3.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, -1.5708f, 0.6109f, -1.5708f)
            )

            val cube_r66 = obere_teil.addOrReplaceChild(
                "cube_r66",
                CubeListBuilder.create().texOffs(114, 64)
                    .addBox(-11.6142f, -5.0016f, -1.5f, 10.0f, 3.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 1.5708f, -1.2654f, -1.5708f)
            )

            val cube_r67 = obere_teil.addOrReplaceChild(
                "cube_r67",
                CubeListBuilder.create().texOffs(120, 56)
                    .addBox(-15.1789f, -10.4939f, -1.5f, 8.0f, 3.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 1.5708f, -0.6981f, -1.5708f)
            )

            val cube_r68 = obere_teil.addOrReplaceChild(
                "cube_r68",
                CubeListBuilder.create().texOffs(32, 130)
                    .addBox(-12.7781f, -12.5084f, -8.0662f, 8.0f, 2.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 1.5708f, -0.6981f, -0.7854f)
            )

            val cube_r69 = obere_teil.addOrReplaceChild(
                "cube_r69",
                CubeListBuilder.create().texOffs(124, 121)
                    .addBox(-10.6718f, -7.9906f, -8.0662f, 10.0f, 2.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 1.5708f, -1.2654f, -0.7854f)
            )

            val cube_r70 = obere_teil.addOrReplaceChild(
                "cube_r70",
                CubeListBuilder.create().texOffs(102, 0)
                    .addBox(-3.0458f, -7.4338f, -8.0662f, 16.0f, 2.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, 0.0f, -1.5708f, 0.7854f)
            )

            val cube_r71 = obere_teil.addOrReplaceChild(
                "cube_r71",
                CubeListBuilder.create().texOffs(138, 82)
                    .addBox(-6.3506f, -14.7985f, 7.0662f, 5.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0619f, -1.3927f, -5.4264f, -1.5708f, 0.6109f, -0.7854f)
            )

            val glass_unten = u_boot.addOrReplaceChild(
                "glass_unten",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.25f, -5.928f, -14.343f, 0.0f, 0.0f, -3.1416f)
            )

            val cube_r72 = glass_unten.addOrReplaceChild(
                "cube_r72",
                CubeListBuilder.create().texOffs(106, 95)
                    .addBox(-35.2468f, -24.9525f, 79.6434f, 9.0f, 0.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-87.0f, -0.4399f, 0.7868f, 1.6754f, 0.7388f, 2.0876f)
            )

            val cube_r73 = glass_unten.addOrReplaceChild(
                "cube_r73",
                CubeListBuilder.create().texOffs(124, 106).mirror()
                    .addBox(-60.5855f, 53.341f, -15.5126f, 9.0f, 0.0f, 3.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-87.0f, -0.4399f, 0.7868f, 1.3107f, -0.7566f, -2.7164f)
            )

            val cube_r74 = glass_unten.addOrReplaceChild(
                "cube_r74",
                CubeListBuilder.create().texOffs(124, 110).mirror()
                    .addBox(-59.4611f, 48.8295f, -31.2875f, 9.0f, 0.0f, 3.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-87.0f, -0.4399f, 0.7868f, 1.5687f, -0.7791f, -2.7123f)
            )

            val cube_r75 = glass_unten.addOrReplaceChild(
                "cube_r75",
                CubeListBuilder.create().texOffs(12, 127).mirror()
                    .addBox(-48.6704f, 43.067f, -51.2867f, 8.0f, 0.0f, 2.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-87.0f, -0.4399f, 0.7868f, 1.5304f, -0.8029f, -2.4071f)
            )

            val cube_r76 = glass_unten.addOrReplaceChild(
                "cube_r76",
                CubeListBuilder.create().texOffs(124, 11).mirror()
                    .addBox(-37.8593f, 34.6843f, -67.7157f, 9.0f, 0.0f, 3.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-87.0f, -0.4399f, 0.7868f, 1.4151f, -0.716f, -2.0884f)
            )

            val cube_r77 = glass_unten.addOrReplaceChild(
                "cube_r77",
                CubeListBuilder.create().texOffs(106, 95).mirror()
                    .addBox(-37.3161f, 16.8361f, -76.8477f, 9.0f, 0.0f, 3.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-87.0f, -0.4399f, 0.7868f, 1.6754f, -0.7388f, -2.0876f)
            )

            val cube_r78 = glass_unten.addOrReplaceChild(
                "cube_r78",
                CubeListBuilder.create().texOffs(128, 43).mirror()
                    .addBox(-19.0276f, 3.2214f, -84.803f, 8.0f, 0.0f, 2.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-87.0f, -0.4399f, 0.7868f, 1.653f, -0.7871f, -1.7965f)
            )

            val cube_r79 = glass_unten.addOrReplaceChild(
                "cube_r79",
                CubeListBuilder.create().texOffs(32, 107).mirror()
                    .addBox(8.368f, 4.1308f, -85.5549f, 9.0f, 0.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-87.0f, -0.4399f, 0.7868f, -2.1724f, 1.1974f, 0.9355f)
            )

            val cube_r80 = glass_unten.addOrReplaceChild(
                "cube_r80",
                CubeListBuilder.create().texOffs(60, 112).mirror()
                    .addBox(7.8894f, -46.1937f, -66.4482f, 9.0f, 0.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-87.0f, -0.4399f, 0.7868f, -1.533f, 1.2208f, 0.9184f)
            )

            val cube_r81 = glass_unten.addOrReplaceChild(
                "cube_r81",
                CubeListBuilder.create().texOffs(88, 112).mirror()
                    .addBox(13.0213f, 56.0272f, 45.6266f, 9.0f, 0.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-87.0f, -0.4399f, 0.7868f, 1.533f, 1.2208f, 0.6524f)
            )

            val cube_r82 = glass_unten.addOrReplaceChild(
                "cube_r82",
                CubeListBuilder.create().texOffs(32, 113).mirror()
                    .addBox(14.5216f, 71.8393f, -2.6424f, 9.0f, 0.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-87.0f, -0.4399f, 0.7868f, 2.1724f, 1.1974f, 0.6353f)
            )

            val cube_r83 = glass_unten.addOrReplaceChild(
                "cube_r83",
                CubeListBuilder.create().texOffs(32, 113)
                    .addBox(27.561f, -94.1075f, -13.9671f, 9.0f, 0.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-87.0f, -0.4399f, 0.7868f, 2.1724f, -1.1974f, -0.6353f)
            )

            val cube_r84 = glass_unten.addOrReplaceChild(
                "cube_r84",
                CubeListBuilder.create().texOffs(88, 112)
                    .addBox(25.387f, -69.7775f, -64.8344f, 9.0f, 0.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-87.0f, -0.4399f, 0.7868f, 1.533f, -1.2208f, -0.6524f)
            )

            val cube_r85 = glass_unten.addOrReplaceChild(
                "cube_r85",
                CubeListBuilder.create().texOffs(124, 110)
                    .addBox(-62.109f, -62.4918f, 40.9036f, 9.0f, 0.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-87.0f, -0.4399f, 0.7868f, 1.5687f, 0.7791f, 2.7123f)
            )

            val cube_r86 = glass_unten.addOrReplaceChild(
                "cube_r86",
                CubeListBuilder.create().texOffs(12, 127)
                    .addBox(-49.0293f, -54.4632f, 61.4866f, 8.0f, 0.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-87.0f, -0.4399f, 0.7868f, 1.5304f, 0.8029f, 2.4071f)
            )

            val cube_r87 = glass_unten.addOrReplaceChild(
                "cube_r87",
                CubeListBuilder.create().texOffs(124, 106)
                    .addBox(-63.6802f, -70.2611f, 25.8679f, 9.0f, 0.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-87.0f, -0.4399f, 0.7868f, 1.3107f, 0.7566f, 2.7164f)
            )

            val cube_r88 = glass_unten.addOrReplaceChild(
                "cube_r88",
                CubeListBuilder.create().texOffs(124, 11)
                    .addBox(-36.0927f, -44.5954f, 72.8971f, 9.0f, 0.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-87.0f, -0.4399f, 0.7868f, 1.4151f, 0.716f, 2.0884f)
            )

            val cube_r89 = glass_unten.addOrReplaceChild(
                "cube_r89",
                CubeListBuilder.create().texOffs(128, 43)
                    .addBox(-16.4606f, -10.3454f, 86.4756f, 8.0f, 0.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-87.0f, -0.4399f, 0.7868f, 1.653f, 0.7871f, 1.7965f)
            )

            val cube_r90 = glass_unten.addOrReplaceChild(
                "cube_r90",
                CubeListBuilder.create().texOffs(60, 112)
                    .addBox(19.3278f, 58.1855f, 67.9738f, 9.0f, 0.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-87.0f, -0.4399f, 0.7868f, -1.533f, -1.2208f, -0.9184f)
            )

            val cube_r91 = glass_unten.addOrReplaceChild(
                "cube_r91",
                CubeListBuilder.create().texOffs(32, 107)
                    .addBox(20.2925f, 4.1308f, 84.3206f, 9.0f, 0.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-87.0f, -0.4399f, 0.7868f, -2.1724f, -1.1974f, -0.9355f)
            )

            val glass_oben = u_boot.addOrReplaceChild(
                "glass_oben",
                CubeListBuilder.create(),
                PartPose.offset(0.25f, -19.928f, -14.343f)
            )

            val cube_r92 = glass_oben.addOrReplaceChild(
                "cube_r92",
                CubeListBuilder.create().texOffs(106, 95)
                    .addBox(-3.4653f, -4.0582f, 1.3979f, 9.0f, 0.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -0.4399f, 0.7868f, 1.6754f, 0.7388f, 2.0876f)
            )

            val cube_r93 = glass_oben.addOrReplaceChild(
                "cube_r93",
                CubeListBuilder.create().texOffs(124, 106).mirror()
                    .addBox(-2.9526f, -8.4601f, 5.1776f, 9.0f, 0.0f, 3.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, -0.4399f, 0.7868f, 1.3107f, -0.7566f, -2.7164f)
            )

            val cube_r94 = glass_oben.addOrReplaceChild(
                "cube_r94",
                CubeListBuilder.create().texOffs(124, 110).mirror()
                    .addBox(-3.176f, -6.8311f, 4.8081f, 9.0f, 0.0f, 3.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, -0.4399f, 0.7868f, 1.5687f, -0.7791f, -2.7123f)
            )

            val cube_r95 = glass_oben.addOrReplaceChild(
                "cube_r95",
                CubeListBuilder.create().texOffs(12, 127).mirror()
                    .addBox(-3.8206f, -5.6981f, 5.0999f, 8.0f, 0.0f, 2.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, -0.4399f, 0.7868f, 1.5304f, -0.8029f, -2.4071f)
            )

            val cube_r96 = glass_oben.addOrReplaceChild(
                "cube_r96",
                CubeListBuilder.create().texOffs(124, 11).mirror()
                    .addBox(-5.3833f, -4.9555f, 2.5907f, 9.0f, 0.0f, 3.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, -0.4399f, 0.7868f, 1.4151f, -0.716f, -2.0884f)
            )

            val cube_r97 = glass_oben.addOrReplaceChild(
                "cube_r97",
                CubeListBuilder.create().texOffs(106, 95).mirror()
                    .addBox(-5.5347f, -4.0582f, 1.3979f, 9.0f, 0.0f, 3.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, -0.4399f, 0.7868f, 1.6754f, -0.7388f, -2.0876f)
            )

            val cube_r98 = glass_oben.addOrReplaceChild(
                "cube_r98",
                CubeListBuilder.create().texOffs(128, 43).mirror()
                    .addBox(-5.2835f, -3.562f, 0.8363f, 8.0f, 0.0f, 2.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, -0.4399f, 0.7868f, 1.653f, -0.7871f, -1.7965f)
            )

            val cube_r99 = glass_oben.addOrReplaceChild(
                "cube_r99",
                CubeListBuilder.create().texOffs(32, 107).mirror()
                    .addBox(-10.4623f, 4.1308f, -0.6171f, 9.0f, 0.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, -0.4399f, 0.7868f, -2.1724f, 1.1974f, 0.9355f)
            )

            val cube_r100 = glass_oben.addOrReplaceChild(
                "cube_r100",
                CubeListBuilder.create().texOffs(60, 112).mirror()
                    .addBox(-10.2192f, 5.9959f, 0.7628f, 9.0f, 0.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, -0.4399f, 0.7868f, -1.533f, 1.2208f, 0.9184f)
            )

            val cube_r101 = glass_oben.addOrReplaceChild(
                "cube_r101",
                CubeListBuilder.create().texOffs(88, 112).mirror()
                    .addBox(-10.6828f, -6.8751f, -9.6039f, 9.0f, 0.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, -0.4399f, 0.7868f, 1.533f, 1.2208f, 0.6524f)
            )

            val cube_r102 = glass_oben.addOrReplaceChild(
                "cube_r102",
                CubeListBuilder.create().texOffs(32, 113).mirror()
                    .addBox(-11.0197f, -11.1341f, -8.3047f, 9.0f, 0.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, -0.4399f, 0.7868f, 2.1724f, 1.1974f, 0.6353f)
            )

            val cube_r103 = glass_oben.addOrReplaceChild(
                "cube_r103",
                CubeListBuilder.create().texOffs(32, 113)
                    .addBox(2.0197f, -11.1341f, -8.3047f, 9.0f, 0.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -0.4399f, 0.7868f, 2.1724f, -1.1974f, -0.6353f)
            )

            val cube_r104 = glass_oben.addOrReplaceChild(
                "cube_r104",
                CubeListBuilder.create().texOffs(88, 112)
                    .addBox(1.6828f, -6.8751f, -9.6039f, 9.0f, 0.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -0.4399f, 0.7868f, 1.533f, -1.2208f, -0.6524f)
            )

            val cube_r105 = glass_oben.addOrReplaceChild(
                "cube_r105",
                CubeListBuilder.create().texOffs(124, 110)
                    .addBox(-5.824f, -6.8311f, 4.8081f, 9.0f, 0.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -0.4399f, 0.7868f, 1.5687f, 0.7791f, 2.7123f)
            )

            val cube_r106 = glass_oben.addOrReplaceChild(
                "cube_r106",
                CubeListBuilder.create().texOffs(12, 127)
                    .addBox(-4.1794f, -5.6981f, 5.0999f, 8.0f, 0.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -0.4399f, 0.7868f, 1.5304f, 0.8029f, 2.4071f)
            )

            val cube_r107 = glass_oben.addOrReplaceChild(
                "cube_r107",
                CubeListBuilder.create().texOffs(124, 106)
                    .addBox(-6.0474f, -8.4601f, 5.1776f, 9.0f, 0.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -0.4399f, 0.7868f, 1.3107f, 0.7566f, 2.7164f)
            )

            val cube_r108 = glass_oben.addOrReplaceChild(
                "cube_r108",
                CubeListBuilder.create().texOffs(124, 11)
                    .addBox(-3.6167f, -4.9555f, 2.5907f, 9.0f, 0.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -0.4399f, 0.7868f, 1.4151f, 0.716f, 2.0884f)
            )

            val cube_r109 = glass_oben.addOrReplaceChild(
                "cube_r109",
                CubeListBuilder.create().texOffs(128, 43)
                    .addBox(-2.7165f, -3.562f, 0.8363f, 8.0f, 0.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -0.4399f, 0.7868f, 1.653f, 0.7871f, 1.7965f)
            )

            val cube_r110 = glass_oben.addOrReplaceChild(
                "cube_r110",
                CubeListBuilder.create().texOffs(60, 112)
                    .addBox(1.2192f, 5.9959f, 0.7628f, 9.0f, 0.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -0.4399f, 0.7868f, -1.533f, -1.2208f, -0.9184f)
            )

            val cube_r111 = glass_oben.addOrReplaceChild(
                "cube_r111",
                CubeListBuilder.create().texOffs(32, 107)
                    .addBox(1.4623f, 4.1308f, -0.6171f, 9.0f, 0.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -0.4399f, 0.7868f, -2.1724f, -1.1974f, -0.9355f)
            )

            val lampen =
                u_boot.addOrReplaceChild("lampen", CubeListBuilder.create(), PartPose.offset(0.05f, -1.0f, -7.75f))

            val cube_r112 = lampen.addOrReplaceChild(
                "cube_r112",
                CubeListBuilder.create().texOffs(48, 133)
                    .addBox(-2.1f, -2.0f, -2.0f, 2.0f, 4.0f, 4.0f, CubeDeformation(0.2f))
                    .texOffs(32, 119).addBox(-2.4f, -2.0f, -2.0f, 7.0f, 4.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-11.5f, 0.0f, 0.0f, 0.0f, -1.5708f, -1.5708f)
            )

            val cube_r113 = lampen.addOrReplaceChild(
                "cube_r113",
                CubeListBuilder.create().texOffs(48, 133)
                    .addBox(-2.1f, -2.0f, -2.0f, 2.0f, 4.0f, 4.0f, CubeDeformation(0.2f))
                    .texOffs(32, 119).addBox(-2.4f, -2.0f, -2.0f, 7.0f, 4.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(11.5f, 0.0f, 0.0f, 0.0f, -1.5708f, -1.5708f)
            )

            val innenaustattung = u_boot.addOrReplaceChild(
                "innenaustattung",
                CubeListBuilder.create(),
                PartPose.offset(0.25f, -8.2525f, -8.0123f)
            )

            val cube_r114 = innenaustattung.addOrReplaceChild(
                "cube_r114",
                CubeListBuilder.create().texOffs(12, 103)
                    .addBox(51.8794f, -4.15f, 24.2395f, 8.0f, 15.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.35f, -13.9475f, -64.3377f, 0.0f, -0.8727f, 1.5708f)
            )

            val cube_r115 = innenaustattung.addOrReplaceChild(
                "cube_r115",
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

            val cube_r116 = innenaustattung.addOrReplaceChild(
                "cube_r116",
                CubeListBuilder.create().texOffs(0, 52)
                    .addBox(78.6128f, -6.8298f, -2.65f, 4.0f, 20.0f, 12.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.35f, -13.9475f, -64.3377f, -1.5708f, -1.4835f, 1.5708f)
            )

            val cube_r117 = innenaustattung.addOrReplaceChild(
                "cube_r117",
                CubeListBuilder.create().texOffs(32, 52)
                    .addBox(65.0297f, 19.184f, -2.65f, 13.0f, 1.0f, 12.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.35f, -13.9475f, -64.3377f, 0.0f, -1.5708f, 0.0f)
            )

            val flosse = u_boot.addOrReplaceChild(
                "flosse",
                CubeListBuilder.create(),
                PartPose.offset(0.25f, -14.0833f, 19.7441f)
            )

            val cube_r118 = flosse.addOrReplaceChild(
                "cube_r118",
                CubeListBuilder.create().texOffs(128, 49)
                    .addBox(-82.5716f, 16.65f, -4.2f, 4.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(102, 89).addBox(-82.3716f, 16.65f, -1.2f, 1.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(58, 91).addBox(-82.3716f, 16.65f, -2.2f, 2.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(134, 87).addBox(-83.1716f, 16.65f, -4.2f, 1.0f, 1.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(24, 86).addBox(-82.3716f, 16.65f, -3.2f, 3.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-17.15f, -10.1167f, -77.0941f, 0.0f, 1.5708f, -1.5708f)
            )

            val cube_r119 = flosse.addOrReplaceChild(
                "cube_r119",
                CubeListBuilder.create().texOffs(120, 62)
                    .addBox(-58.5987f, 16.65f, -58.5384f, 7.0f, 1.0f, 1.0f, CubeDeformation(0.01f)),
                PartPose.offsetAndRotation(-17.15f, -10.1167f, -77.0941f, 3.1416f, 0.7854f, 1.5708f)
            )

            val cube_r120 = flosse.addOrReplaceChild(
                "cube_r120",
                CubeListBuilder.create().texOffs(0, 128)
                    .addBox(77.8f, 16.65f, 13.2f, 4.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(98, 41).addBox(77.2f, 16.65f, 4.2f, 6.0f, 1.0f, 9.0f, CubeDeformation(0.0f))
                    .texOffs(92, 25).addBox(78.0f, 16.65f, 16.2f, 1.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(58, 89).addBox(78.0f, 16.65f, 15.2f, 2.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(24, 84).addBox(78.0f, 16.65f, 14.2f, 3.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-17.15f, -10.1167f, -77.0941f, 0.0f, -1.5708f, -1.5708f)
            )

            val cube_r121 = flosse.addOrReplaceChild(
                "cube_r121",
                CubeListBuilder.create().texOffs(114, 87)
                    .addBox(42.4975f, 16.65f, 67.1651f, 7.0f, 1.0f, 1.0f, CubeDeformation(0.001f)),
                PartPose.offsetAndRotation(-17.15f, -10.1167f, -77.0941f, 0.0f, -0.7854f, -1.5708f)
            )

            return LayerDefinition.create(meshdefinition, 256, 256)
        }
    }
}