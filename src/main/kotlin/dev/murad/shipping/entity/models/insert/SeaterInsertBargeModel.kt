package dev.murad.shipping.entity.models.insert

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.murad.shipping.HumVeeMod
import dev.murad.shipping.entity.Colorable
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity

class SeaterInsertBargeModel<T>(root: ModelPart) : EntityModel<T>() where T : Entity, T : Colorable {
    private val bb_main: ModelPart

    init {
        this.bb_main = root.getChild("bb_main")
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

    override fun setupAnim(
        pEntity: T?,
        pLimbSwing: Float,
        pLimbSwingAmount: Float,
        pAgeInTicks: Float,
        pNetHeadYaw: Float,
        pHeadPitch: Float
    ) {
    }

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.tryBuild(HumVeeMod.MOD_ID, "seater_insert_barge_model"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            partdefinition.addOrReplaceChild(
                "bb_main", CubeListBuilder.create()
                    .texOffs(0, 19).addBox(-5.0f, -29.0f, -5.0f, 9.0f, 1.0f, 10.0f)
                    .texOffs(9, 22).addBox(-5.0f, -35.0f, -5.0f, 1.0f, 6.0f, 10.0f)
                    .texOffs(11, 23).addBox(-4.0f, -31.0f, -5.0f, 8.0f, 2.0f, 1.0f)
                    .texOffs(11, 24).addBox(-4.0f, -31.0f, 4.0f, 8.0f, 2.0f, 1.0f)
                    .texOffs(0, 49).addBox(-4.0f, -32.0f, -6.0f, 8.0f, 1.0f, 2.0f)
                    .texOffs(0, 49).addBox(-4.0f, -32.0f, 4.0f, 8.0f, 1.0f, 2.0f),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}