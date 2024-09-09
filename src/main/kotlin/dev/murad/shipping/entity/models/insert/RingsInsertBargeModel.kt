package dev.murad.shipping.entity.models.insert;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.murad.shipping.HumVeeMod;
import dev.murad.shipping.entity.Colorable;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class RingsInsertBargeModel<T extends Entity & Colorable> extends EntityModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.tryBuild(HumVeeMod.MOD_ID, "rings_insert_barge_model"), "main");
    private final ModelPart bb_main;

    public RingsInsertBargeModel(ModelPart root) {
        this.bb_main = root.getChild("bb_main");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition main = meshdefinition.getRoot()
                .addOrReplaceChild("bb_main",
                        CubeListBuilder.create()
                                .texOffs(0, 37).addBox(-2.0F, -33.0F, -2.0F, 4.0F, 4.0F, 4.0F),
                        PartPose.offset(0.0F, 23.0F, 0.0F));

        main.addOrReplaceChild("ring", CubeListBuilder.create().texOffs(38, 0).addBox(-5.0F, -29.0F, -5.0F, 10.0F, 2.0F, 2.0F)
                        .texOffs(36, 31).addBox(-5.0F, -29.0F, 3.0F, 10.0F, 2.0F, 2.0F)
                        .texOffs(10, 41).addBox(3.0F, -29.0F, -3.0F, 2.0F, 2.0F, 6.0F)
                        .texOffs(38, 4).addBox(-5.0F, -29.0F, -3.0F, 2.0F, 2.0F, 6.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        main.addOrReplaceChild("ring2", CubeListBuilder.create().texOffs(38, 0).addBox(-5.0F, -29.0F, -5.0F, 10.0F, 2.0F, 2.0F)
                        .texOffs(36, 31).addBox(-5.0F, -29.0F, 3.0F, 10.0F, 2.0F, 2.0F)
                        .texOffs(10, 41).addBox(3.0F, -29.0F, -3.0F, 2.0F, 2.0F, 6.0F)
                        .texOffs(38, 4).addBox(-5.0F, -29.0F, -3.0F, 2.0F, 2.0F, 6.0F),
                PartPose.offset(0.0F, -3.0F, 0.0F));

        main.addOrReplaceChild("ring3", CubeListBuilder.create().texOffs(38, 0).addBox(-5.0F, -29.0F, -5.0F, 10.0F, 2.0F, 2.0F)
                        .texOffs(36, 31).addBox(-5.0F, -29.0F, 3.0F, 10.0F, 2.0F, 2.0F)
                        .texOffs(10, 41).addBox(3.0F, -29.0F, -3.0F, 2.0F, 2.0F, 6.0F)
                        .texOffs(38, 4).addBox(-5.0F, -29.0F, -3.0F, 2.0F, 2.0F, 6.0F),
                PartPose.offset(0.0F, -7.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

	@Override
	public void renderToBuffer(@NotNull PoseStack pPoseStack, @NotNull VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, int pColor) {
		bb_main.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
	}
}