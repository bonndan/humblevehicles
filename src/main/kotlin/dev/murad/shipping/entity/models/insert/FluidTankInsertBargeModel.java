package dev.murad.shipping.entity.models.insert;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.murad.shipping.HumVeeMod;
import dev.murad.shipping.entity.Colorable;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class FluidTankInsertBargeModel<T extends Entity & Colorable> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.tryBuild(HumVeeMod.MOD_ID, "fluid_tank_insert_barge_model"), "main");
	private final ModelPart bb_main;

	public FluidTankInsertBargeModel(ModelPart root) {
		this.bb_main = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		 partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create()
				.texOffs(16, 49).addBox(-6.0F, -28.0F, -5.0F, 12.0F, 1.0F, 12.0F)
				.texOffs(0, 0).addBox(-6.0F, -38.0F, -6.0F, 4.0F, 10.0F, 2.0F)
				.texOffs(0, 0).addBox(2.0F, -38.0F, -6.0F, 4.0F, 10.0F, 2.0F)
				.texOffs(38, 0).addBox(-2.0F, -38.0F, -4.0F, 4.0F, 10.0F, 0.0F)
				.texOffs(38, 0).addBox(-2.0F, -38.0F, 5.0F, 4.0F, 10.0F, 0.0F)
				.texOffs(0, 0).addBox(2.0F, -38.0F, 4.0F, 4.0F, 10.0F, 2.0F)
				.texOffs(0, 0).addBox(4.0F, -38.0F, 2.0F, 2.0F, 10.0F, 2.0F)
				.texOffs(38, -4).addBox(5.0F, -38.0F, -2.0F, 0.0F, 10.0F, 4.0F)
				.texOffs(38, -4).addBox(-5.0F, -38.0F, -2.0F, 0.0F, 10.0F, 4.0F)
				.texOffs(0, 0).addBox(4.0F, -38.0F, -4.0F, 2.0F, 10.0F, 2.0F)
				.texOffs(0, 0).addBox(-6.0F, -38.0F, -4.0F, 2.0F, 10.0F, 2.0F)
				.texOffs(0, 0).addBox(-6.0F, -38.0F, 2.0F, 2.0F, 10.0F, 2.0F)
				.texOffs(0, 0).addBox(-6.0F, -38.0F, 4.0F, 4.0F, 10.0F, 2.0F)
				.texOffs(16, 49).addBox(-6.0F, -39.0F, -6.0F, 12.0F, 1.0F, 12.0F)
				.texOffs(72, 0).addBox(-4.0F, -40.0F, -4.0F, 8.0F, 1.0F, 8.0F),
					PartPose.offset(0.0F, 23.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(@NotNull PoseStack pPoseStack, @NotNull VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, int pColor) {
		bb_main.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
	}
}