package com.github.bonndan.humblevehicles.entity.models.insert

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.github.bonndan.humblevehicles.HumVeeMod
import com.github.bonndan.humblevehicles.entity.Colorable
import com.github.bonndan.humblevehicles.entity.custom.vessel.barge.FishingBargeEntity
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity

class FishingInsertBargeModel<T>(root: ModelPart) : EntityModel<T>() where T : Entity, T : Colorable {
    private val armsLeft: ModelPart
    private val armsRight: ModelPart

    init {
        this.armsLeft = root.getChild("arms_left")
        this.armsRight = root.getChild("arms_right")
    }

    override fun setupAnim(
        entity: T?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
    }

    override fun renderToBuffer(
        pPoseStack: PoseStack?,
        pBuffer: VertexConsumer?,
        pPackedLight: Int,
        pPackedOverlay: Int,
        pColor: Int
    ) {
        armsLeft.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay)
        armsRight.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay)
    }

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val STASHED_LOCATION: ModelLayerLocation = ModelLayerLocation(
            ResourceLocation.tryBuild(HumVeeMod.MOD_ID, "fishing_insert_barge_model_stashed"),
            "main"
        )
        val TRANSITION_LOCATION: ModelLayerLocation = ModelLayerLocation(
            ResourceLocation.tryBuild(HumVeeMod.MOD_ID, "fishing_insert_barge_model_transition"),
            "main"
        )
        val DEPLOYED_LOCATION: ModelLayerLocation = ModelLayerLocation(
            ResourceLocation.tryBuild(HumVeeMod.MOD_ID, "fishing_insert_barge_model_deployed"),
            "main"
        )

        fun createBodyLayer(status: FishingBargeEntity.Status): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val armAngle = when (status) {
                FishingBargeEntity.Status.STASHED -> 0.0f
                FishingBargeEntity.Status.TRANSITION -> 0.6109f
                FishingBargeEntity.Status.DEPLOYED -> 1.5708f
            }

            val armY = when (status) {
                FishingBargeEntity.Status.STASHED -> -10.0f
                FishingBargeEntity.Status.TRANSITION -> -9.8192f
                FishingBargeEntity.Status.DEPLOYED -> -9.0f
            }

            val armZ = when (status) {
                FishingBargeEntity.Status.STASHED -> 0.0f
                FishingBargeEntity.Status.TRANSITION -> 0.5736f
                FishingBargeEntity.Status.DEPLOYED -> 1.0f
            }

            val netOffsetZ = when (status) {
                FishingBargeEntity.Status.STASHED, FishingBargeEntity.Status.TRANSITION -> 0.0f
                FishingBargeEntity.Status.DEPLOYED -> 1.0f
            }

            partdefinition.addOrReplaceChild(
                "arms_left", CubeListBuilder.create()
                    .texOffs(0, 0).addBox(-6.0f, armY, -1 + armZ, 1.0f, 9.0f, 2.0f)
                    .texOffs(0, 0).addBox(5.0f, armY, -1 + armZ, 1.0f, 9.0f, 2.0f),
                PartPose.offsetAndRotation(0.0f, -3.0f, -4.0f, armAngle, 0.0f, 0.0f)
            )
                .addOrReplaceChild(
                    "net_left", CubeListBuilder.create()
                        .texOffs(12, 11).addBox(-5.0f, -1.0f, -4.0f, 10.0f, 4.0f, 7.0f)
                        .texOffs(6, 0).addBox(-5.0f, -1.0f, -1.0f, 1.0f, 4.0f, 2.0f)
                        .texOffs(6, 0).addBox(4.0f, -1.0f, -1.0f, 1.0f, 4.0f, 2.0f),
                    PartPose.offsetAndRotation(0.0f, -7.0f, -netOffsetZ, -armAngle, 0.0f, 0.0f)
                )

            partdefinition.addOrReplaceChild(
                "arms_right", CubeListBuilder.create()
                    .texOffs(0, 0).addBox(-6.0f, armY, -1 - armZ, 1.0f, 9.0f, 2.0f)
                    .texOffs(0, 0).addBox(5.0f, armY, -1 - armZ, 1.0f, 9.0f, 2.0f),
                PartPose.offsetAndRotation(0.0f, -3.0f, 4.0f, -armAngle, 0.0f, 0.0f)
            )
                .addOrReplaceChild(
                    "net_right", CubeListBuilder.create()
                        .texOffs(12, 0).addBox(-5.0f, -1.0f, -3.0f, 10.0f, 4.0f, 7.0f)
                        .texOffs(6, 0).addBox(-5.0f, -1.0f, -1.0f, 1.0f, 4.0f, 2.0f)
                        .texOffs(6, 0).addBox(4.0f, -1.0f, -1.0f, 1.0f, 4.0f, 2.0f),
                    PartPose.offsetAndRotation(0.0f, -7.0f, netOffsetZ, armAngle, 0.0f, 0.0f)
                )

            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}