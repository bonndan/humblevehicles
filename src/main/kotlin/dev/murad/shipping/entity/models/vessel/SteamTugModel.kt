package dev.murad.shipping.entity.models.vessel

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.murad.shipping.HumVeeMod
import dev.murad.shipping.entity.Colorable
import dev.murad.shipping.entity.models.PositionAdjustedEntity
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
class SteamTugModel<T>(root: ModelPart) : EntityModel<T>() where T : Entity, T : Colorable {

    private val bone: ModelPart = root.getChild("bone")

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
        pColor: Int
    ) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, pColor)
    }

    companion object {

        val LAYER_LOCATION = ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "steam_tug_model"),
            "main"
        )

        const val MODEL_Y_OFFSET = 1.7

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val bone = partdefinition.addOrReplaceChild(
                "bone",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-2.3024f, -6.0f, 5.2522f, 15.0f, 6.0f, 13.0f, CubeDeformation(0.0f))
                    .texOffs(0, 59).addBox(4.9416f, 2.4224f, 5.2522f, 7.0f, 1.0f, 13.0f, CubeDeformation(0.0f))
                    .texOffs(115, 73).addBox(-0.1024f, -5.9f, 3.3522f, 10.0f, 1.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(104, 97).addBox(-0.1024f, -5.9f, 17.8522f, 10.0f, 1.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(80, 25).addBox(4.4976f, -17.2f, 28.4522f, 1.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(88, 124).addBox(4.9976f, -18.2f, 27.4522f, 0.0f, 3.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(88, 129).addBox(4.9976f, -23.2f, 28.4522f, 0.0f, 5.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(116, 73).addBox(0.8976f, -5.9f, 1.3522f, 8.0f, 1.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(117, 73).addBox(1.8976f, -5.9f, -0.6478f, 6.0f, 1.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(117, 73).addBox(1.8976f, -5.9f, -2.6478f, 6.0f, 1.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(118, 73).addBox(2.8976f, -5.9f, -4.6478f, 4.0f, 1.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(119, 73).addBox(3.8976f, -5.9f, -6.6478f, 2.0f, 1.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(119, 73).mirror().addBox(4.0976f, -5.9f, -6.6478f, 2.0f, 1.0f, 2.0f, CubeDeformation(0.0f))
                    .mirror(false)
                    .texOffs(74, 135).mirror().addBox(2.8976f, -5.9f, 19.9522f, 4.0f, 1.0f, 2.0f, CubeDeformation(0.0f))
                    .mirror(false)
                    .texOffs(104, 97).mirror()
                    .addBox(0.0976f, -5.9f, 17.8522f, 10.0f, 1.0f, 2.0f, CubeDeformation(0.0f)).mirror(false)
                    .texOffs(0, 59).mirror()
                    .addBox(-1.9465f, 2.4224f, 5.2522f, 7.0f, 1.0f, 13.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offset(-4.9976f, 20.5f, -15.9522f)
            )

            val cube_r1 = bone.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(0, 102).mirror()
                    .addBox(0.0281f, 0.1f, -7.3934f, 3.0f, 2.0f, 13.0f, CubeDeformation(0.0f)).mirror(false)
                    .texOffs(1, 103).mirror().addBox(0.0281f, 2.1f, -6.3934f, 3.0f, 1.0f, 12.0f, CubeDeformation(0.0f))
                    .mirror(false),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.1122f, -0.4677f, -0.2449f)
            )

            val cube_r2 = bone.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(136, 120).mirror()
                    .addBox(-2.7874f, -0.57f, -4.3755f, 3.0f, 4.0f, 10.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(1.4103f, 0.07f, 21.4529f, -2.8892f, -0.8449f, 2.8095f)
            )

            val cube_r3 = bone.addOrReplaceChild(
                "cube_r3",
                CubeListBuilder.create().texOffs(0, 79).mirror()
                    .addBox(0.0f, -0.5f, -6.3f, 6.0f, 4.0f, 13.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-2.7024f, 0.0f, 11.5522f, 0.0f, 0.0f, -0.2182f)
            )

            val cube_r4 = bone.addOrReplaceChild(
                "cube_r4",
                CubeListBuilder.create().texOffs(128, 0).mirror()
                    .addBox(-0.5677f, -6.25f, 0.0322f, 2.0f, 8.0f, 9.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(4.9976f, -1.75f, 22.4791f, -3.1416f, -0.8727f, -3.1416f)
            )

            val cube_r5 = bone.addOrReplaceChild(
                "cube_r5",
                CubeListBuilder.create().texOffs(91, 101).mirror()
                    .addBox(-0.0361f, -1.0014f, -6.5141f, 6.0f, 1.0f, 12.0f, CubeDeformation(0.01f)).mirror(false),
                PartPose.offsetAndRotation(0.6259f, 3.4045f, 0.5747f, 0.0f, -0.48f, 0.0f)
            )

            val cube_r6 = bone.addOrReplaceChild(
                "cube_r6",
                CubeListBuilder.create().texOffs(74, 140).mirror()
                    .addBox(-5.5998f, -1.0236f, -5.3737f, 6.0f, 1.0f, 8.0f, CubeDeformation(0.03f)).mirror(false),
                PartPose.offsetAndRotation(0.5729f, 3.4399f, 19.7995f, 3.1416f, -0.8727f, -3.1416f)
            )

            val cube_r7 = bone.addOrReplaceChild(
                "cube_r7",
                CubeListBuilder.create().texOffs(150, 158).mirror()
                    .addBox(-0.67f, -7.501f, -1.0263f, 1.0f, 8.0f, 3.0f, CubeDeformation(0.1f)).mirror(false),
                PartPose.offsetAndRotation(-1.7924f, -1.045f, 5.5745f, -3.098f, -1.2654f, -3.1416f)
            )

            val cube_r8 = bone.addOrReplaceChild(
                "cube_r8",
                CubeListBuilder.create().texOffs(0, 168).mirror()
                    .addBox(-0.7f, 0.0063f, -0.9824f, 1.0f, 4.0f, 3.0f, CubeDeformation(0.1f)).mirror(false),
                PartPose.offsetAndRotation(-1.7924f, -1.045f, 5.5745f, 2.7925f, -1.2654f, -3.1416f)
            )

            val cube_r9 = bone.addOrReplaceChild(
                "cube_r9",
                CubeListBuilder.create().texOffs(51, 28).mirror()
                    .addBox(0.0f, -5.25f, -14.7f, 3.0f, 7.0f, 14.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-2.7024f, -1.75f, 5.2522f, 0.0f, -0.48f, 0.0f)
            )

            val cube_r10 = bone.addOrReplaceChild(
                "cube_r10",
                CubeListBuilder.create().texOffs(80, 0).mirror()
                    .addBox(-0.35f, -0.95f, -1.05f, 1.0f, 2.0f, 15.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(4.3676f, -7.91f, -7.4878f, -0.0873f, -0.5236f, 0.0f)
            )

            val cube_r11 = bone.addOrReplaceChild(
                "cube_r11",
                CubeListBuilder.create().texOffs(1, 103)
                    .addBox(-3.0281f, 2.1f, -6.3934f, 3.0f, 1.0f, 12.0f, CubeDeformation(0.0f))
                    .texOffs(0, 102).addBox(-3.0281f, 0.1f, -7.3934f, 3.0f, 2.0f, 13.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(9.9951f, 0.0f, 0.0f, 0.1122f, 0.4677f, 0.2449f)
            )

            val cube_r12 = bone.addOrReplaceChild(
                "cube_r12",
                CubeListBuilder.create().texOffs(162, 168).mirror()
                    .addBox(5.0708f, -3.1761f, 7.7342f, 0.0f, 3.0f, 4.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-0.1482f, -4.6239f, 10.2695f, -3.1416f, 0.4887f, -3.1416f)
            )

            val cube_r13 = bone.addOrReplaceChild(
                "cube_r13",
                CubeListBuilder.create().texOffs(154, 70).mirror()
                    .addBox(4.3402f, -0.0761f, 7.649f, 2.0f, 4.0f, 4.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0518f, -4.6239f, 10.1695f, -3.1416f, 0.48f, -3.1416f)
            )

            val cube_r14 = bone.addOrReplaceChild(
                "cube_r14",
                CubeListBuilder.create().texOffs(148, 38).mirror()
                    .addBox(8.0493f, -2.3808f, -8.9673f, 0.0f, 3.0f, 4.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-0.1482f, -4.6239f, 10.2695f, 2.9873f, -0.8625f, -2.9396f)
            )

            val cube_r15 = bone.addOrReplaceChild(
                "cube_r15",
                CubeListBuilder.create().texOffs(154, 70).mirror()
                    .addBox(7.5786f, -0.4761f, -8.9277f, 2.0f, 4.0f, 4.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-0.1482f, -4.6239f, 10.2695f, -3.1416f, -0.8727f, -3.1416f)
            )

            val cube_r16 = bone.addOrReplaceChild(
                "cube_r16",
                CubeListBuilder.create().texOffs(154, 70)
                    .addBox(-1.0f, -2.0f, -2.0f, 2.0f, 4.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(10.2247f, -2.7f, -0.8551f, -3.1416f, -0.48f, 3.1416f)
            )

            val cube_r17 = bone.addOrReplaceChild(
                "cube_r17",
                CubeListBuilder.create().texOffs(48, 126)
                    .addBox(-8.0f, -13.0f, -5.0f, 7.0f, 2.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(9.4976f, -9.5f, -10.5478f, -1.5708f, 0.0f, 0.0f)
            )

            val cube_r18 = bone.addOrReplaceChild(
                "cube_r18",
                CubeListBuilder.create().texOffs(7, 127)
                    .addBox(-7.0f, -7.0f, 0.0f, 3.0f, 7.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(6.9976f, -5.5f, 4.9522f, 0.0f, -1.5708f, 0.0f)
            )

            val cube_r19 = bone.addOrReplaceChild(
                "cube_r19",
                CubeListBuilder.create().texOffs(148, 24)
                    .addBox(-4.5f, 0.4f, -4.05f, 9.0f, 1.0f, 8.0f, CubeDeformation(0.0f))
                    .texOffs(0, 142).addBox(-2.85f, -11.6f, -3.15f, 6.0f, 17.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(4.9976f, -6.19f, 17.8522f, -0.2618f, 0.0f, 0.0f)
            )

            val cube_r20 = bone.addOrReplaceChild(
                "cube_r20",
                CubeListBuilder.create().texOffs(154, 70)
                    .addBox(-4.189f, -6.5f, 3.6174f, 2.0f, 4.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.9976f, 1.4f, 22.4621f, -3.1416f, 0.8727f, 3.1416f)
            )

            val cube_r21 = bone.addOrReplaceChild(
                "cube_r21",
                CubeListBuilder.create().texOffs(162, 168)
                    .addBox(0.0f, -5.0f, -2.0f, 0.0f, 3.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(10.0506f, -2.8f, -0.8058f, -3.1416f, -0.4887f, 3.1416f)
            )

            val cube_r22 = bone.addOrReplaceChild(
                "cube_r22",
                CubeListBuilder.create().texOffs(148, 38)
                    .addBox(-3.189f, -9.5f, 3.6174f, 0.0f, 3.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(4.1976f, 1.8f, 22.7621f, 2.9873f, 0.8625f, 2.9396f)
            )

            val cube_r23 = bone.addOrReplaceChild(
                "cube_r23",
                CubeListBuilder.create().texOffs(28, 142)
                    .addBox(-0.5f, -12.0f, -1.0f, 2.0f, 19.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(4.4976f, -12.5f, 23.9522f, -0.3927f, 0.0f, 0.0f)
            )

            val cube_r24 = bone.addOrReplaceChild(
                "cube_r24",
                CubeListBuilder.create().texOffs(80, 0)
                    .addBox(-0.65f, -0.95f, -1.05f, 1.0f, 2.0f, 15.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(5.6276f, -7.91f, -7.4878f, -0.0873f, 0.5236f, 0.0f)
            )

            val cube_r25 = bone.addOrReplaceChild(
                "cube_r25",
                CubeListBuilder.create().texOffs(51, 28)
                    .addBox(-3.0f, -5.25f, -14.7f, 3.0f, 7.0f, 14.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(12.6976f, -1.75f, 5.2522f, 0.0f, 0.48f, 0.0f)
            )

            val cube_r26 = bone.addOrReplaceChild(
                "cube_r26",
                CubeListBuilder.create().texOffs(0, 168)
                    .addBox(-0.3f, 0.0063f, -0.9824f, 1.0f, 4.0f, 3.0f, CubeDeformation(0.1f)),
                PartPose.offsetAndRotation(11.7876f, -1.045f, 5.5745f, 2.7925f, 1.2654f, 3.1416f)
            )

            val cube_r27 = bone.addOrReplaceChild(
                "cube_r27",
                CubeListBuilder.create().texOffs(150, 158)
                    .addBox(-0.33f, -7.501f, -1.0263f, 1.0f, 8.0f, 3.0f, CubeDeformation(0.1f)),
                PartPose.offsetAndRotation(11.7876f, -1.045f, 5.5745f, -3.098f, 1.2654f, 3.1416f)
            )

            val cube_r28 = bone.addOrReplaceChild(
                "cube_r28",
                CubeListBuilder.create().texOffs(138, 158).mirror()
                    .addBox(-0.485f, -6.7763f, -1.1183f, 1.0f, 8.0f, 3.0f, CubeDeformation(0.1f)).mirror(false),
                PartPose.offsetAndRotation(-2.0319f, -1.773f, 17.2489f, 0.0436f, -0.829f, 0.0f)
            )

            val cube_r29 = bone.addOrReplaceChild(
                "cube_r29",
                CubeListBuilder.create().texOffs(60, 159).mirror()
                    .addBox(-0.515f, 0.7111f, -0.7902f, 1.0f, 4.0f, 3.0f, CubeDeformation(0.1f)).mirror(false),
                PartPose.offsetAndRotation(-2.0319f, -1.773f, 17.2489f, -0.3491f, -0.829f, 0.0f)
            )

            val cube_r30 = bone.addOrReplaceChild(
                "cube_r30",
                CubeListBuilder.create().texOffs(60, 159)
                    .addBox(-0.3f, 0.0063f, -0.9824f, 1.0f, 4.0f, 3.0f, CubeDeformation(0.1f)),
                PartPose.offsetAndRotation(11.8576f, -1.045f, 17.3445f, -0.3491f, 0.829f, 0.0f)
            )

            val cube_r31 = bone.addOrReplaceChild(
                "cube_r31",
                CubeListBuilder.create().texOffs(138, 158)
                    .addBox(-0.33f, -7.501f, -1.0263f, 1.0f, 8.0f, 3.0f, CubeDeformation(0.1f)),
                PartPose.offsetAndRotation(11.8576f, -1.045f, 17.3445f, 0.0436f, 0.829f, 0.0f)
            )

            val cube_r32 = bone.addOrReplaceChild(
                "cube_r32",
                CubeListBuilder.create().texOffs(74, 154)
                    .addBox(-1.63f, -9.03f, 0.03f, 3.0f, 9.0f, 3.0f, CubeDeformation(0.1f)),
                PartPose.offsetAndRotation(4.9976f, 0.0f, -8.0478f, 0.1309f, 0.0f, 0.0f)
            )

            val cube_r33 = bone.addOrReplaceChild(
                "cube_r33",
                CubeListBuilder.create().texOffs(162, 158)
                    .addBox(-1.6f, -0.1315f, -0.8678f, 3.0f, 4.0f, 3.0f, CubeDeformation(0.1f)),
                PartPose.offsetAndRotation(4.9976f, -0.9547f, 23.1996f, -0.3491f, 0.0f, 0.0f)
            )

            val cube_r34 = bone.addOrReplaceChild(
                "cube_r34",
                CubeListBuilder.create().texOffs(122, 158)
                    .addBox(-1.63f, -7.3777f, -0.9467f, 3.0f, 8.0f, 3.0f, CubeDeformation(0.1f)),
                PartPose.offsetAndRotation(4.9976f, -1.1647f, 23.2696f, 0.0436f, 0.0f, 0.0f)
            )

            val cube_r35 = bone.addOrReplaceChild(
                "cube_r35",
                CubeListBuilder.create().texOffs(160, 10)
                    .addBox(-1.6f, -2.25f, -1.4f, 3.0f, 4.0f, 3.0f, CubeDeformation(0.1f)),
                PartPose.offsetAndRotation(4.9976f, 1.12f, -5.4578f, 0.6545f, 0.0f, 0.0f)
            )

            val cube_r36 = bone.addOrReplaceChild(
                "cube_r36",
                CubeListBuilder.create().texOffs(74, 140)
                    .addBox(-0.4002f, -1.0236f, -5.3737f, 6.0f, 1.0f, 8.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(9.4223f, 3.4399f, 19.7995f, 3.1416f, 0.8727f, 3.1416f)
            )

            val cube_r37 = bone.addOrReplaceChild(
                "cube_r37",
                CubeListBuilder.create().texOffs(91, 101)
                    .addBox(-5.9639f, -1.0014f, -6.5141f, 6.0f, 1.0f, 12.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(9.3692f, 3.4045f, 0.5747f, 0.0f, 0.48f, 0.0f)
            )

            val cube_r38 = bone.addOrReplaceChild(
                "cube_r38",
                CubeListBuilder.create().texOffs(128, 0)
                    .addBox(-1.4323f, -6.25f, 0.0322f, 2.0f, 8.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(4.9976f, -1.75f, 22.4791f, -3.1416f, 0.8727f, 3.1416f)
            )

            val cube_r39 = bone.addOrReplaceChild(
                "cube_r39",
                CubeListBuilder.create().texOffs(0, 79)
                    .addBox(-6.0f, -0.5f, -6.3f, 6.0f, 4.0f, 13.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(12.6976f, 0.0f, 11.5522f, 0.0f, 0.0f, 0.2182f)
            )

            val cube_r40 = bone.addOrReplaceChild(
                "cube_r40",
                CubeListBuilder.create().texOffs(136, 120)
                    .addBox(-0.2126f, -0.57f, -4.3755f, 3.0f, 4.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(8.5848f, 0.07f, 21.4529f, -2.8892f, 0.8449f, -2.8095f)
            )

            return LayerDefinition.create(meshdefinition, 256, 256)
        }
    }
}