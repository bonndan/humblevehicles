package com.github.bonndan.humblevehicles.entity.models.train

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.github.bonndan.humblevehicles.HumVeeMod
import com.github.bonndan.humblevehicles.entity.custom.vessel.barge.AbstractBargeEntity
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation


// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports
class ChainModel(root: ModelPart) : EntityModel<AbstractBargeEntity?>() {
    private val bb_main: ModelPart

    init {
        this.bb_main = root.getChild("bb_main")
    }

    override fun setupAnim(
        entity: AbstractBargeEntity?,
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
        bb_main.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay)
    }

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.tryBuild(HumVeeMod.MOD_ID, "chainmodel"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val bb_main = partdefinition.addOrReplaceChild(
                "bb_main",
                CubeListBuilder.create(),
                PartPose.offset(-13.0f, 24.0f, 0.0f)
            )

            val cube_r1 = bb_main.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, -1.5708f, 0.0f)
            )

            cube_r1.addOrReplaceChild(
                "bone2", CubeListBuilder.create().texOffs(0, 2).addBox(1.0f, -25.0f, -11.0f, 1.0f, 1.0f, 1.0f)
                    .texOffs(0, 0).addBox(0.0f, -25.0f, -13.0f, 1.0f, 1.0f, 1.0f)
                    .texOffs(3, 8).addBox(0.0f, -26.0f, -13.0f, 2.0f, 1.0f, 4.0f)
                    .texOffs(4, 4).addBox(0.0f, -24.0f, -13.0f, 2.0f, 1.0f, 4.0f), PartPose.offset(-1.0f, 0.0f, 1.0f)
            )

            return LayerDefinition.create(meshdefinition, 16, 16)
        }
    }
}