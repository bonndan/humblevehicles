package com.github.bonndan.humblevehicles.entity.models.vessel.base

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

class BaseBargeModel<T>(root: ModelPart) : EntityModel<T>() where T : Entity, T : Colorable {

    private val bb_main: ModelPart = root.getChild("bb_main")

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
        bb_main.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay)
    }

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val CLOSED_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "base_barge_model_closed"), "main")
        val OPEN_FRONT_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "base_barge_model_open_front"), "main")
        val OPEN_SIDES_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "base_barge_model_open_sides"), "main")

        fun createBodyLayer(closedFront: Boolean, closedSides: Boolean): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val listBuilder = CubeListBuilder.create() // Main
                .texOffs(0, 0).addBox(-6.0f, -4f, -7.0f, 12.0f, 5.0f, 14.0f) // Back Side
                .texOffs(0, 19).addBox(-8.0f, -4f, -7.0f, 2.0f, 2.0f, 14.0f)

            if (closedFront) {
                // Front Side
                listBuilder.texOffs(0, 19).addBox(6.0f, -4f, -7.0f, 2.0f, 2.0f, 14.0f)
            }

            if (closedSides) {
                // Short Sides
                listBuilder.texOffs(19, 21).addBox(-6.0f, -4f, -9.0f, 12.0f, 2.0f, 2.0f)
                    .texOffs(19, 21).addBox(-6.0f, -4f, 7.0f, 12.0f, 2.0f, 2.0f)
            }

            partdefinition.addOrReplaceChild("bb_main", listBuilder, PartPose.ZERO)

            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}