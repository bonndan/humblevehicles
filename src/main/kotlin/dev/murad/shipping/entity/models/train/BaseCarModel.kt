package dev.murad.shipping.entity.models.train

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

class BaseCarModel<T : AbstractTrainCarEntity>(root: ModelPart) : EntityModel<T>() {

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
            ModelLayerLocation(ResourceLocation.tryBuild(HumVeeMod.MOD_ID, "base_car_model"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            partdefinition.addOrReplaceChild(
                "bb_main", CubeListBuilder.create() // Long sides

                    .texOffs(0, 0).addBox(-7.0f, -13.0f, -8.0f, 2.0f, 9.0f, 16.0f)
                    .texOffs(0, 0).addBox(5.0f, -13.0f, -8.0f, 2.0f, 9.0f, 16.0f) // Short sides

                    .texOffs(0, 28).addBox(-5.0f, -13.0f, -8.0f, 10.0f, 9.0f, 2.0f)
                    .texOffs(0, 28).addBox(-5.0f, -13.0f, 6.0f, 10.0f, 9.0f, 2.0f) // Wheels

                    .texOffs(0, 0).addBox(-6.0f, -2.0f, 4.0f, 1.0f, 2.0f, 2.0f)
                    .texOffs(0, 0).addBox(-6.0f, -2.0f, -6.0f, 1.0f, 2.0f, 2.0f)
                    .texOffs(0, 0).addBox(5.0f, -2.0f, 4.0f, 1.0f, 2.0f, 2.0f)
                    .texOffs(0, 0).addBox(5.0f, -2.0f, -6.0f, 1.0f, 2.0f, 2.0f),

                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}