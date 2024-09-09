package dev.murad.shipping.entity.models.vessel

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.murad.shipping.HumVeeMod
import dev.murad.shipping.entity.Colorable
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

class EnergyTugModel<T>(root: ModelPart) : EntityModel<T>() where T : Entity, T : Colorable {
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
        bb_main.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pColor)
    }

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.tryBuild(HumVeeMod.MOD_ID, "energy_tug_model"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()

            meshdefinition.getRoot().addOrReplaceChild(
                "bb_main", CubeListBuilder.create().texOffs(0, 30).addBox(-7.0f, -9.0f, -19.0f, 14.0f, 6.0f, 24.0f)
                    .texOffs(58, 53).addBox(-9.0f, -11.0f, -19.0f, 2.0f, 5.0f, 18.0f)
                    .texOffs(52, 17).addBox(-7.0f, -11.0f, -21.0f, 14.0f, 5.0f, 2.0f)
                    .texOffs(52, 30).addBox(7.0f, -11.0f, -19.0f, 2.0f, 5.0f, 18.0f)
                    .texOffs(0, 60).addBox(-9.0f, -10.0f, -21.0f, 18.0f, 2.0f, 6.0f, CubeDeformation(0.25f))
                    .texOffs(52, 0).addBox(-6.0f, -14.0f, -17.0f, 12.0f, 5.0f, 12.0f)
                    .texOffs(0, 0).addBox(-3.0f, -20.0f, -14.0f, 6.0f, 6.0f, 6.0f)
                    .texOffs(40, 60).addBox(-4.0f, -21.0f, -15.0f, 8.0f, 1.0f, 8.0f)
                    .texOffs(0, 0).addBox(-7.0f, -9.0f, -19.0f, 14.0f, 6.0f, 24.0f, CubeDeformation(0.25f))
                    .texOffs(52, 42).addBox(-0.5f, -27.0f, -11.0f, 1.0f, 6.0f, 0.0f), PartPose.offset(0.0f, 6.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}