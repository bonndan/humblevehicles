package com.github.bonndan.humblevehicles.entity.models.insert

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.github.bonndan.humblevehicles.HumVeeMod
import com.github.bonndan.humblevehicles.entity.Colorable
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity

class RingsInsertBargeModel<T>(root: ModelPart) : EntityModel<T>() where T : Entity, T : Colorable {

    private val bb_main: ModelPart

    init {
        this.bb_main = root.getChild("bb_main")
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
        pPoseStack: PoseStack,
        pBuffer: VertexConsumer,
        pPackedLight: Int,
        pPackedOverlay: Int,
        pColor: Int
    ) {
        bb_main.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay)
    }

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.tryBuild(HumVeeMod.MOD_ID, "rings_insert_barge_model"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val main = meshdefinition.getRoot()
                .addOrReplaceChild(
                    "bb_main",
                    CubeListBuilder.create()
                        .texOffs(0, 37).addBox(-2.0f, -33.0f, -2.0f, 4.0f, 4.0f, 4.0f),
                    PartPose.offset(0.0f, 23.0f, 0.0f)
                )

            main.addOrReplaceChild(
                "ring", CubeListBuilder.create().texOffs(38, 0).addBox(-5.0f, -29.0f, -5.0f, 10.0f, 2.0f, 2.0f)
                    .texOffs(36, 31).addBox(-5.0f, -29.0f, 3.0f, 10.0f, 2.0f, 2.0f)
                    .texOffs(10, 41).addBox(3.0f, -29.0f, -3.0f, 2.0f, 2.0f, 6.0f)
                    .texOffs(38, 4).addBox(-5.0f, -29.0f, -3.0f, 2.0f, 2.0f, 6.0f),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            main.addOrReplaceChild(
                "ring2", CubeListBuilder.create().texOffs(38, 0).addBox(-5.0f, -29.0f, -5.0f, 10.0f, 2.0f, 2.0f)
                    .texOffs(36, 31).addBox(-5.0f, -29.0f, 3.0f, 10.0f, 2.0f, 2.0f)
                    .texOffs(10, 41).addBox(3.0f, -29.0f, -3.0f, 2.0f, 2.0f, 6.0f)
                    .texOffs(38, 4).addBox(-5.0f, -29.0f, -3.0f, 2.0f, 2.0f, 6.0f),
                PartPose.offset(0.0f, -3.0f, 0.0f)
            )

            main.addOrReplaceChild(
                "ring3", CubeListBuilder.create().texOffs(38, 0).addBox(-5.0f, -29.0f, -5.0f, 10.0f, 2.0f, 2.0f)
                    .texOffs(36, 31).addBox(-5.0f, -29.0f, 3.0f, 10.0f, 2.0f, 2.0f)
                    .texOffs(10, 41).addBox(3.0f, -29.0f, -3.0f, 2.0f, 2.0f, 6.0f)
                    .texOffs(38, 4).addBox(-5.0f, -29.0f, -3.0f, 2.0f, 2.0f, 6.0f),
                PartPose.offset(0.0f, -7.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}