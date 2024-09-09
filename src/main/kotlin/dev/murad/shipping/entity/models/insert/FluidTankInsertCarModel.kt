package dev.murad.shipping.entity.models.insert

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.murad.shipping.HumVeeMod
import dev.murad.shipping.entity.custom.train.AbstractTrainCarEntity
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation

// Made with Blockbench 4.1.1
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports


class FluidTankInsertCarModel<T : AbstractTrainCarEntity>(root: ModelPart) : EntityModel<T>() {
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
            ModelLayerLocation(ResourceLocation.tryBuild(HumVeeMod.MOD_ID, "fluid_tank_insert_car_model"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val bb_main = partdefinition.addOrReplaceChild(
                "bb_main", CubeListBuilder.create().texOffs(20, 0).addBox(-6.0f, -28.0f, -5.0f, 12.0f, 1.0f, 12.0f)
                    .texOffs(32, 40).addBox(-2.0f, -38.0f, -2.0f, 4.0f, 10.0f, 0.0f)
                    .texOffs(32, 40).addBox(-2.0f, -38.0f, 5.0f, 4.0f, 10.0f, 0.0f)
                    .texOffs(55, 30).addBox(-4.0f, -38.0f, -1.0f, 0.0f, 10.0f, 5.0f)
                    .texOffs(55, 30).addBox(4.0f, -38.0f, -1.0f, 0.0f, 10.0f, 5.0f)
                    .texOffs(20, 0).addBox(2.0f, -38.0f, -3.0f, 3.0f, 10.0f, 2.0f)
                    .texOffs(20, 0).addBox(-5.0f, -38.0f, -3.0f, 3.0f, 10.0f, 2.0f)
                    .texOffs(20, 0).addBox(-5.0f, -38.0f, 4.0f, 3.0f, 10.0f, 2.0f)
                    .texOffs(20, 0).addBox(2.0f, -38.0f, 4.0f, 3.0f, 10.0f, 2.0f)
                    .texOffs(20, 13).addBox(-5.0f, -39.0f, -3.0f, 10.0f, 1.0f, 2.0f), PartPose.offset(0.0f, 45.0f, 0.0f)
            )

            bb_main.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(24, 32).addBox(-4.0f, 2.0f, -5.0f, 1.0f, 1.0f, 7.0f)
                    .texOffs(54, 24).addBox(-5.0f, 3.0f, -4.0f, 1.0f, 1.0f, 5.0f)
                    .texOffs(7, 37).addBox(4.0f, 3.0f, -4.0f, 1.0f, 1.0f, 5.0f)
                    .texOffs(24, 32).addBox(3.0f, 2.0f, -5.0f, 1.0f, 1.0f, 7.0f)
                    .texOffs(20, 13).addBox(-5.0f, 3.0f, -6.0f, 10.0f, 1.0f, 2.0f),
                PartPose.offsetAndRotation(0.0f, -42.0f, 0.0f, 3.1416f, 0.0f, -3.1416f)
            )

            bb_main.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(33, 32).addBox(-2.0f, 2.0f, -3.0f, 1.0f, 1.0f, 6.0f)
                    .texOffs(33, 32).addBox(4.0f, 2.0f, -3.0f, 1.0f, 1.0f, 6.0f),
                PartPose.offsetAndRotation(0.0f, -42.0f, 0.0f, 0.0f, -1.5708f, 0.0f)
            )

            bb_main.addOrReplaceChild(
                "cube_r3",
                CubeListBuilder.create().texOffs(40, 39).addBox(-3.0f, -4.0f, -39.0f, 6.0f, 5.0f, 0.0f)
                    .texOffs(40, 39).addBox(-3.0f, -4.0f, -39.0f, 6.0f, 5.0f, 0.0f),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -1.5708f, 0.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}