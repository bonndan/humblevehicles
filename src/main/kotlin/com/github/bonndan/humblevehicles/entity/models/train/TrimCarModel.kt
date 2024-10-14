package com.github.bonndan.humblevehicles.entity.models.train

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.github.bonndan.humblevehicles.HumVeeMod
import com.github.bonndan.humblevehicles.entity.custom.train.AbstractTrainCarEntity
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation

class TrimCarModel<T : AbstractTrainCarEntity>(root: ModelPart) : EntityModel<T>() {
    private val bb_main: ModelPart

    init {
        this.bb_main = root.getChild("bb_main")
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
        pPoseStack: PoseStack,
        pBuffer: VertexConsumer,
        pPackedLight: Int,
        pPackedOverlay: Int,
        pColor: Int
    ) {
        bb_main.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pColor)
    }

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "trim_car_model"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()

            meshdefinition.getRoot().addOrReplaceChild(
                "bb_main", CubeListBuilder.create() // Long sides trim
                    .texOffs(0, 18).addBox(-7.0f, -14.0f, -8.0f, 2.0f, 1f, 16.0f)
                    .texOffs(0, 18).addBox(5.0f, -14.0f, -8.0f, 2.0f, 1f, 16.0f) // Short sides trim

                    .texOffs(36, 18).addBox(-5.0f, -14.0f, -8.0f, 10.0f, 1f, 2.0f)
                    .texOffs(36, 18).addBox(-5.0f, -14.0f, 6.0f, 10.0f, 1f, 2.0f) // Base

                    .texOffs(0, 0).addBox(-7.0f, -4.0f, -8.0f, 14.0f, 2.0f, 16.0f),

                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}