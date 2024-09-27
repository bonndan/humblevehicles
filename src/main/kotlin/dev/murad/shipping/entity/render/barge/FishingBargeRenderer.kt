package dev.murad.shipping.entity.render.barge

import com.mojang.blaze3d.vertex.PoseStack
import dev.murad.shipping.entity.custom.vessel.barge.FishingBargeEntity
import dev.murad.shipping.entity.render.ModelPack
import dev.murad.shipping.entity.render.ModelSupplier
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation

class FishingBargeRenderer<T : FishingBargeEntity> protected constructor(
    context: EntityRendererProvider.Context,
    baseModelPack: ModelPack<T>,
    stashedInsertModelPack: ModelPack<T>,
    transitionInsertModelPack: ModelPack<T>,
    deployedInsertModelPack: ModelPack<T>,
    trimModelPack: ModelPack<T>
) : MultipartVesselRenderer<T>(context, baseModelPack, stashedInsertModelPack, trimModelPack) {

    private val transitionInsertModel: EntityModel<T>
    private val deployedInsertModel: EntityModel<T>

    private val transitionInsertTextureLocation: ResourceLocation
    private val deployedInsertTextureLocation: ResourceLocation

    init {
        this.transitionInsertModel =
            transitionInsertModelPack.supplier.supply(context.bakeLayer(transitionInsertModelPack.location))
        this.transitionInsertTextureLocation = transitionInsertModelPack.texture

        this.deployedInsertModel =
            deployedInsertModelPack.supplier.supply(context.bakeLayer(deployedInsertModelPack.location))
        this.deployedInsertTextureLocation = deployedInsertModelPack.texture
    }

    override fun renderInsertModel(
        vesselEntity: T,
        matrixStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        overlay: Int
    ) {
        val model: EntityModel<T> = when (vesselEntity.getStatus()!!) {
            FishingBargeEntity.Status.STASHED -> getInsertModel()
            FishingBargeEntity.Status.DEPLOYED -> deployedInsertModel
            FishingBargeEntity.Status.TRANSITION -> transitionInsertModel
        }

        val texture: ResourceLocation = when (vesselEntity.getStatus()!!) {
            FishingBargeEntity.Status.STASHED -> getInsertTextureLocation()
            FishingBargeEntity.Status.DEPLOYED -> deployedInsertTextureLocation
            FishingBargeEntity.Status.TRANSITION -> transitionInsertTextureLocation
        }

        model.renderToBuffer(matrixStack, buffer.getBuffer(model.renderType(texture)), packedLight, overlay, white)
    }

    class Builder<T : FishingBargeEntity>(context: EntityRendererProvider.Context) :
        MultipartVesselRenderer.Builder<T>(context) {

        private lateinit var transitionInsertModelPack: ModelPack<T>
        private lateinit var deployedInsertModelPack: ModelPack<T>

        fun transitionInsertModel(
            supplier: ModelSupplier<T>,
            location: ModelLayerLocation,
            texture: ResourceLocation
        ): Builder<T> {
            this.transitionInsertModelPack = ModelPack<T>(supplier, location, texture)
            return this
        }

        fun deployedInsertModel(
            supplier: ModelSupplier<T>,
            location: ModelLayerLocation,
            texture: ResourceLocation
        ): Builder<T> {
            this.deployedInsertModelPack = ModelPack<T>(supplier, location, texture)
            return this
        }

        override fun build(): FishingBargeRenderer<T> {
            return FishingBargeRenderer(
                context,
                baseModelPack,
                insertModelPack,
                transitionInsertModelPack,
                deployedInsertModelPack,
                trimModelPack
            )
        }
    }
}
