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

class FluidTankInsertBargeModel<T>(root: ModelPart) : EntityModel<T>() where T : Entity, T : Colorable {
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
            ModelLayerLocation(ResourceLocation.tryBuild(HumVeeMod.MOD_ID, "fluid_tank_insert_barge_model"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            partdefinition.addOrReplaceChild(
                "bb_main", CubeListBuilder.create()
                    .texOffs(16, 49).addBox(-6.0f, -28.0f, -5.0f, 12.0f, 1.0f, 12.0f)
                    .texOffs(0, 0).addBox(-6.0f, -38.0f, -6.0f, 4.0f, 10.0f, 2.0f)
                    .texOffs(0, 0).addBox(2.0f, -38.0f, -6.0f, 4.0f, 10.0f, 2.0f)
                    .texOffs(38, 0).addBox(-2.0f, -38.0f, -4.0f, 4.0f, 10.0f, 0.0f)
                    .texOffs(38, 0).addBox(-2.0f, -38.0f, 5.0f, 4.0f, 10.0f, 0.0f)
                    .texOffs(0, 0).addBox(2.0f, -38.0f, 4.0f, 4.0f, 10.0f, 2.0f)
                    .texOffs(0, 0).addBox(4.0f, -38.0f, 2.0f, 2.0f, 10.0f, 2.0f)
                    .texOffs(38, -4).addBox(5.0f, -38.0f, -2.0f, 0.0f, 10.0f, 4.0f)
                    .texOffs(38, -4).addBox(-5.0f, -38.0f, -2.0f, 0.0f, 10.0f, 4.0f)
                    .texOffs(0, 0).addBox(4.0f, -38.0f, -4.0f, 2.0f, 10.0f, 2.0f)
                    .texOffs(0, 0).addBox(-6.0f, -38.0f, -4.0f, 2.0f, 10.0f, 2.0f)
                    .texOffs(0, 0).addBox(-6.0f, -38.0f, 2.0f, 2.0f, 10.0f, 2.0f)
                    .texOffs(0, 0).addBox(-6.0f, -38.0f, 4.0f, 4.0f, 10.0f, 2.0f)
                    .texOffs(16, 49).addBox(-6.0f, -39.0f, -6.0f, 12.0f, 1.0f, 12.0f)
                    .texOffs(72, 0).addBox(-4.0f, -40.0f, -4.0f, 8.0f, 1.0f, 8.0f),
                PartPose.offset(0.0f, 23.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}