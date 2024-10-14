package com.github.bonndan.humblevehicles.entity.render.barge

import com.mojang.blaze3d.vertex.PoseStack
import com.github.bonndan.humblevehicles.HumVeeMod.Companion.entityTexture
import com.github.bonndan.humblevehicles.entity.custom.vessel.VesselEntity
import com.github.bonndan.humblevehicles.entity.models.vessel.EmptyModel
import com.github.bonndan.humblevehicles.entity.render.ModelPack
import com.github.bonndan.humblevehicles.entity.render.ModelSupplier
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.DyeColor
import java.awt.Color

open class MultipartVesselRenderer<T : VesselEntity> protected constructor(
    context: EntityRendererProvider.Context,
    baseModelPack: ModelPack<T>,
    insertModelPack: ModelPack<T>,
    trimModelPack: ModelPack<T>
) : AbstractVesselRenderer<T>(context) {

    // TODO: de-uglify
    private var rotation = 90f

    private val baseModel: EntityModel<T> = baseModelPack.supplier.supply(
        context.bakeLayer(baseModelPack.location)
    )
    private val insertModel: EntityModel<T> = insertModelPack.supplier.supply(
        context.bakeLayer(insertModelPack.location)
    )

    /**
     * The trim model is used for coloring.
     */
    private val trimModel: EntityModel<T> = trimModelPack.supplier.supply(
        context.bakeLayer(trimModelPack.location)
    )

    private val baseTextureLocation: ResourceLocation = baseModelPack.texture
    private val insertTextureLocation: ResourceLocation = insertModelPack.texture
    private val trimTextureLocation: ResourceLocation = trimModelPack.texture

    /**
     * Don't directly use this method, use the multipart methods instead
     */
    @Deprecated("")
    override fun getModel(entity: T): EntityModel<T> {
        return baseModel
    }

    /**
     * Don't directly use this method, use the multipart methods instead
     */
    @Deprecated("")
    override fun getTextureLocation(pEntity: T): ResourceLocation {
        return baseTextureLocation
    }

    override fun renderModel(vesselEntity: T, matrixStack: PoseStack, buffer: MultiBufferSource, packedLight: Int) {
        val overlay = LivingEntityRenderer.getOverlayCoords(vesselEntity, 0f)
        renderBaseModel(matrixStack, buffer, packedLight, overlay)
        renderInsertModel(vesselEntity, matrixStack, buffer, packedLight, overlay)
        renderTrimModel(vesselEntity, matrixStack, buffer, packedLight, overlay)
    }

    protected fun renderBaseModel(
        matrixStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        overlay: Int
    ) {

        baseModel.renderToBuffer(
            matrixStack, buffer.getBuffer(baseModel.renderType(baseTextureLocation)), packedLight, overlay, white
        )
    }

    protected open fun renderInsertModel(
        vesselEntity: T,
        matrixStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        overlay: Int
    ) {
        insertModel.renderToBuffer(
            matrixStack, buffer.getBuffer(insertModel.renderType(insertTextureLocation)), packedLight, overlay, white
        )
    }

    private fun renderTrimModel(
        vesselEntity: T?,
        matrixStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        overlay: Int
    ) {
        val colorId = vesselEntity!!.getColor()
        val color = (if (colorId == null) DyeColor.RED else DyeColor.byId(colorId)).getTextureDiffuseColor()

        trimModel.renderToBuffer(
            matrixStack,
            buffer.getBuffer(trimModel.renderType(trimTextureLocation)),
            packedLight,
            overlay,
            color
        )
    }

    fun derotate(): MultipartVesselRenderer<T> {
        this.rotation = 0f
        return this
    }

    override fun getModelYrot(): Float {
        return rotation
    }

    fun getRotation(): Float {
        return rotation
    }

    fun getBaseModel(): EntityModel<T> {
        return baseModel
    }

    fun getInsertModel(): EntityModel<T> {
        return insertModel
    }

    fun getTrimModel(): EntityModel<T> {
        return trimModel
    }

    fun getBaseTextureLocation(): ResourceLocation {
        return baseTextureLocation
    }

    fun getInsertTextureLocation(): ResourceLocation {
        return insertTextureLocation
    }

    fun getTrimTextureLocation(): ResourceLocation {
        return trimTextureLocation
    }

    open class Builder<T : VesselEntity>(context: EntityRendererProvider.Context) {

        protected val context: EntityRendererProvider.Context

        protected lateinit var baseModelPack: ModelPack<T>
        protected lateinit var insertModelPack: ModelPack<T>
        protected lateinit var trimModelPack: ModelPack<T>


        init {
            this.context = context
        }

        fun baseModel(
            supplier: ModelSupplier<T>,
            location: ModelLayerLocation,
            texture: ResourceLocation
        ): Builder<T> {
            this.baseModelPack = ModelPack(supplier, location, texture)
            return this
        }

        fun insertModel(
            supplier: ModelSupplier<T>,
            location: ModelLayerLocation,
            texture: ResourceLocation
        ): Builder<T> {
            this.insertModelPack = ModelPack<T>(supplier, location, texture)
            return this
        }

        fun emptyInsert(): Builder<T> {
            insertModel({ root -> EmptyModel(root) }, EmptyModel.LAYER_LOCATION, entityTexture("emptytexture.png"))
            return this
        }

        /**
         * The trim model is used for coloring.
         */
        fun trimModel(supplier: ModelSupplier<T>, location: ModelLayerLocation, texture: ResourceLocation): Builder<T> {
            this.trimModelPack = ModelPack(supplier, location, texture)
            return this
        }

        open fun build(): MultipartVesselRenderer<T> {
            return MultipartVesselRenderer(context, baseModelPack, insertModelPack, trimModelPack)
        }
    }
}
