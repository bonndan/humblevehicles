package com.github.bonndan.humblevehicles.entity.render.train
//
//import com.mojang.blaze3d.vertex.PoseStack
//import com.mojang.datafixers.util.Pair
//import com.mojang.math.Axis
//import com.github.bonndan.humblevehicles.HumVeeMod.Companion.MOD_ID
import com.github.bonndan.humblevehicles.HumVeeMod.Companion.MOD_ID
import com.github.bonndan.humblevehicles.entity.custom.train.AbstractTrainCarEntity
//import com.github.bonndan.humblevehicles.entity.models.EmptyModel
import com.github.bonndan.humblevehicles.entity.models.VesselRenderState
import com.github.bonndan.humblevehicles.entity.render.ModelPack
import com.github.bonndan.humblevehicles.entity.render.ModelSupplier
import net.minecraft.client.model.geom.ModelLayerLocation
//import com.github.bonndan.humblevehicles.entity.models.train.ChainModel
//import com.github.bonndan.humblevehicles.entity.render.ModelPack
//import com.github.bonndan.humblevehicles.entity.render.ModelSupplier
//import net.minecraft.client.Minecraft
//import net.minecraft.client.model.geom.ModelLayerLocation
//import net.minecraft.client.renderer.MultiBufferSource
//import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import com.github.bonndan.humblevehicles.entity.models.EmptyModel
//import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation

//import net.minecraft.util.Mth
//import net.minecraft.world.item.DyeColor
//import net.minecraft.world.phys.Vec3
//import kotlin.math.asin
//import kotlin.math.atan
//import kotlin.math.atan2
//import kotlin.math.ceil
//
abstract class MultipartCarRenderer<T : AbstractTrainCarEntity> protected constructor(
    context: EntityRendererProvider.Context,
//    baseModelPack: ModelPack<T>,
//    insertModelPack: ModelPack<T>,
//    trimModelPack: ModelPack<T>
) : EntityRenderer<T, VesselRenderState>(context) {
    //
//    private val baseModel: EntityModel<VesselRenderState>
//    private val insertModel: EntityModel<VesselRenderState>
//    private val trimModel: EntityModel<VesselRenderState>
//
//    private val baseTextureLocation: ResourceLocation
//    private val insertTextureLocation: ResourceLocation
//    private val trimTextureLocation: ResourceLocation
//
//    private val chainModel: ChainModel
//
//
//    init {
//        this.baseModel = baseModelPack.supplier.supply(context.bakeLayer(baseModelPack.location))
//        this.baseTextureLocation = baseModelPack.texture
//
//        this.insertModel = insertModelPack.supplier.supply(context.bakeLayer(insertModelPack.location))
//        this.insertTextureLocation = insertModelPack.texture
//
//        this.trimModel = trimModelPack.supplier.supply(context.bakeLayer(trimModelPack.location))
//        this.trimTextureLocation = trimModelPack.texture
//
//        chainModel = ChainModel(context.bakeLayer(ChainModel.Companion.LAYER_LOCATION))
//    }
//
//    override fun createRenderState(): VesselRenderState {
//        return VesselRenderState()
//    }
//
    override fun extractRenderState(entity: T, renderState: VesselRenderState, partialTicks: Float) {
        super.extractRenderState(entity, renderState, partialTicks)
        renderState.setColorId(entity.getColorId())
        renderState.follower = entity.getLeader()
    }

    //
//    override fun render(
//        renderState: VesselRenderState,
//        pose: PoseStack,
//        buffer: MultiBufferSource,
//        pPackedLight: Int
//    ) {
//        val pPartialTicks = renderState.partialTick
//
//        if (renderState.follower.isPresent) return
//
//        pose.pushPose()
//
//        // render
//        var attachmentPoints = renderCarAndGetAttachmentPoints(renderState, pPartialTicks, pose, buffer, pPackedLight)
//
//
//        while (renderState.follower.isPresent) {
//            val nextT = renderState.follower.get()
//            val renderer = Minecraft.getInstance().entityRenderDispatcher.getRenderer(nextT)
//            if (renderer is RenderWithAttachmentPoints<*>) {
//                val attachmentRenderer = renderer as RenderWithAttachmentPoints<AbstractTrainCarEntity>
//
//                // translate to next train location
//                val nextTPos = nextT.getPosition(pPartialTicks)
//                val tPos = t.getPosition(pPartialTicks)
//                var offset = nextTPos.subtract(tPos)
//                pose.translate(offset.x, offset.y, offset.z)
//                val newAttachmentPoints = attachmentRenderer.renderCarAndGetAttachmentPoints(
//                    nextT,
//                    nextT.yRot,
//                    pPartialTicks,
//                    pose,
//                    buffer,
//                    pPackedLight
//                )
//                val from = newAttachmentPoints.getFirst()
//                val to = attachmentPoints.getSecond()
//
//                // translate to "from" position
//                pose.pushPose()
//                offset = from.subtract(nextTPos)
//                pose.translate(offset.x, offset.y, offset.z)
//                getAndRenderChain(from, to, pose, buffer, pPackedLight)
//                pose.popPose()
//
//                attachmentPoints = newAttachmentPoints
//            }
//
//            t = nextT
//        }
//
//        pose.popPose()
//    }
//
//    protected fun getAndRenderChain(
//        from: Vec3,
//        to: Vec3,
//        matrixStack: PoseStack,
//        buffer: MultiBufferSource,
//        p_225623_6_: Int
//    ) {
//        matrixStack.pushPose()
//        val vec = from.vectorTo(to)
//        val dist = vec.length()
//        val segments = ceil(dist * 4).toInt()
//
//        // TODO: fix pitch
//        matrixStack.mulPose(Axis.YP.rotation(-atan2(vec.z, vec.x).toFloat()))
//        matrixStack.mulPose(Axis.ZP.rotation((asin(vec.y / dist)).toFloat()))
//        matrixStack.pushPose()
//        val ivertexbuilderChain = buffer.getBuffer(chainModel.renderType(CHAIN_TEXTURE))
//        for (i in 1 until segments) {
//            matrixStack.pushPose()
//            matrixStack.translate(i / 4.0, 0.0, 0.0)
//            chainModel.renderToBuffer(matrixStack, ivertexbuilderChain, p_225623_6_, OverlayTexture.NO_OVERLAY)
//            matrixStack.popPose()
//        }
//
//        matrixStack.popPose()
//        matrixStack.popPose()
//    }
//
//    // First - front anchor point
//    // Second - back anchor point
//    // Override this to change anchor points for larger or smaller cars
//    fun getAttachmentPoints(chainCentre: Vec3, trackDirection: Vec3): Pair<Vec3, Vec3> {
//        return Pair<Vec3, Vec3>(chainCentre.add(trackDirection.scale(.2)), chainCentre.add(trackDirection.scale(-.2)))
//    }
//
//    override fun shouldRender(entity: T?, pCamera: Frustum?, pCamX: Double, pCamY: Double, pCamZ: Double): Boolean {
//        return true
//    }
//
//    override fun renderCarAndGetAttachmentPoints(
//        vesselRenderState: VesselRenderState,
//        partialTicks: Float,
//        pose: PoseStack,
//        buffer: MultiBufferSource,
//        packedLight: Int
//    ): Pair<Vec3, Vec3> {
//        var yaw = vesselRenderState.yRot
//        var attach = Pair<Vec3, Vec3>(
//            car.frontPos.add(0.0, .44, 0.0),
//            car.backPos.add(0.0, .44, 0.0)
//        )
//
//        pose.pushPose()
//        var i = car.id.toLong() * 493286711L
//        i = i * i * 4392167121L + i * 98761L
//        val f = (((i shr 16 and 7L).toFloat() + 0.5f) / 8.0f - 0.5f) * 0.004f
//        val f1 = (((i shr 20 and 7L).toFloat() + 0.5f) / 8.0f - 0.5f) * 0.004f
//        val f2 = (((i shr 24 and 7L).toFloat() + 0.5f) / 8.0f - 0.5f) * 0.004f
//        pose.translate(f.toDouble(), f1.toDouble(), f2.toDouble())
//        val d0 = Mth.lerp(partialTicks.toDouble(), car.x, car.x)
//        val d1 = Mth.lerp(partialTicks.toDouble(), car.yOld, car.y)
//        val d2 = Mth.lerp(partialTicks.toDouble(), car.zOld, car.z)
//        val pos = car.getPos(d0, d1, d2)
//        var pitch = Mth.lerp(partialTicks, car.xRotO, car.xRot)
//        if (pos != null) {
//            var forwardDir = car.getPosOffs(d0, d1, d2, 0.3)
//            var backDir = car.getPosOffs(d0, d1, d2, -0.3)
//            if (forwardDir == null) {
//                forwardDir = pos
//            }
//
//            if (backDir == null) {
//                backDir = pos
//            }
//
//            val centre = Vec3(pos.x, (forwardDir.y + backDir.y) / 2.0, pos.z)
//            val offset = centre.subtract(d0, d1, d2)
//
//            pose.translate(offset.x, offset.y, offset.z)
//            var trackDirection = forwardDir.subtract(backDir)
//            if (trackDirection.length() != 0.0) {
//                trackDirection = trackDirection.normalize()
//                yaw = (atan2(-trackDirection.z, -trackDirection.x) * 180.0 / Math.PI + 90).toFloat()
//                pitch = (atan(-trackDirection.y) * 73.0).toFloat()
//            }
//
//            val chainCentre = centre.add(0.0, .22, 0.0)
//            attach = getAttachmentPoints(chainCentre, trackDirection)
//        }
//
//        pose.translate(0.0, 0.375, 0.0)
//        pose.mulPose(Axis.YP.rotationDegrees(180.0f - yaw))
//        pose.mulPose(Axis.XN.rotationDegrees(pitch))
//        val f5 = car.hurtTime.toFloat() - partialTicks
//        var f6 = car.damage - partialTicks
//        if (f6 < 0.0f) {
//            f6 = 0.0f
//        }
//
//        if (f5 > 0.0f) {
//            pose.mulPose(Axis.XP.rotationDegrees(Mth.sin(f5) * f5 * f6 / 10.0f * car.hurtDir.toFloat()))
//        }
//
//        pose.translate(0.0, 1.1, 0.0)
//
//        pose.scale(-1.0f, -1.0f, 1.0f)
//
//        val overlay = OverlayTexture.NO_OVERLAY
//
//        renderBaseModel(car, pose, buffer, packedLight, overlay)
//        renderInsertModel(car, pose, buffer, partialTicks, packedLight, overlay)
//        renderTrimModel(car, pose, buffer, packedLight, overlay)
//
//        pose.popPose()
//
//        if (car.hasCustomName()) {
//            this.renderNameTag(car, car.customName, pose, buffer, packedLight, partialTicks)
//        }
//
//        return attach
//    }
//
//    protected fun renderBaseModel(
//        entity: T?,
//        matrixStack: PoseStack,
//        buffer: MultiBufferSource,
//        packedLight: Int,
//        overlay: Int
//    ) {
//        baseModel.renderToBuffer(
//            matrixStack,
//            buffer.getBuffer(baseModel.renderType(baseTextureLocation)),
//            packedLight,
//            overlay
//        )
//    }
//
//    protected open fun renderInsertModel(
//        entity: T?,
//        matrixStack: PoseStack,
//        buffer: MultiBufferSource,
//        partialTicks: Float,
//        packedLight: Int,
//        overlay: Int
//    ) {
//        insertModel.renderToBuffer(
//            matrixStack,
//            buffer.getBuffer(insertModel.renderType(insertTextureLocation)),
//            packedLight,
//            overlay
//        )
//    }
//
//    protected fun renderTrimModel(
//        entity: T?,
//        matrixStack: PoseStack,
//        buffer: MultiBufferSource,
//        packedLight: Int,
//        overlay: Int
//    ) {
//        val colorId = entity!!.getColor()
//        val color = (if (colorId == null) DyeColor.RED else DyeColor.byId(colorId)).textureDiffuseColor
//
//        trimModel.renderToBuffer(
//            matrixStack,
//            buffer.getBuffer(trimModel.renderType(trimTextureLocation)),
//            packedLight,
//            overlay,
//            color
//        )
//    }
//
//    // Do not use these directly
//    @Deprecated("")
//    override fun getTextureLocation(entity: T?): ResourceLocation {
//        return baseTextureLocation
//    }
//
    open class Builder<T : AbstractTrainCarEntity>(context: EntityRendererProvider.Context) {

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
            this.baseModelPack = ModelPack<T>(supplier, location, texture)
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
            insertModel(
                { root -> EmptyModel<AbstractTrainCarEntity>(root) },
                EmptyModel.LAYER_LOCATION,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("textures/entity/%s", "emptytexture.png"))
            )
            return this
        }

        fun trimModel(supplier: ModelSupplier<T>, location: ModelLayerLocation, texture: ResourceLocation): Builder<T> {
            this.trimModelPack = ModelPack(supplier, location, texture)
            return this
        }

        //open fun build(): MultipartCarRenderer<T> {
        //return MultipartCarRenderer(context, baseModelPack, insertModelPack, trimModelPack)
        //}
    }

    //
    companion object {
        private val CHAIN_TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/entity/chain.png")
    }
}
