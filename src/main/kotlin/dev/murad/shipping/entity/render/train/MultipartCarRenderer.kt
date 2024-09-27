package dev.murad.shipping.entity.render.train

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.datafixers.util.Pair
import com.mojang.math.Axis
import dev.murad.shipping.HumVeeMod
import dev.murad.shipping.HumVeeMod.Companion.entityTexture
import dev.murad.shipping.entity.custom.train.AbstractTrainCarEntity
import dev.murad.shipping.entity.models.train.ChainModel
import dev.murad.shipping.entity.models.vessel.EmptyModel
import dev.murad.shipping.entity.render.ModelPack
import dev.murad.shipping.entity.render.ModelSupplier
import dev.murad.shipping.entity.render.RenderWithAttachmentPoints
import net.minecraft.client.Minecraft
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.item.DyeColor
import net.minecraft.world.phys.Vec3
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.ceil

open class MultipartCarRenderer<T : AbstractTrainCarEntity> protected constructor(
    context: EntityRendererProvider.Context,
    baseModelPack: ModelPack<T>,
    insertModelPack: ModelPack<T>,
    trimModelPack: ModelPack<T>
) : EntityRenderer<T>(context), RenderWithAttachmentPoints<T> {

    private val baseModel: EntityModel<T>
    private val insertModel: EntityModel<T>
    private val trimModel: EntityModel<T>

    private val baseTextureLocation: ResourceLocation
    private val insertTextureLocation: ResourceLocation
    private val trimTextureLocation: ResourceLocation

    private val chainModel: ChainModel


    init {
        this.baseModel = baseModelPack.supplier.supply(context.bakeLayer(baseModelPack.location))
        this.baseTextureLocation = baseModelPack.texture

        this.insertModel = insertModelPack.supplier.supply(context.bakeLayer(insertModelPack.location))
        this.insertTextureLocation = insertModelPack.texture

        this.trimModel = trimModelPack.supplier.supply(context.bakeLayer(trimModelPack.location))
        this.trimTextureLocation = trimModelPack.texture

        chainModel = ChainModel(context.bakeLayer(ChainModel.Companion.LAYER_LOCATION))
    }

    override fun render(
        car: T,
        yaw: Float,
        pPartialTicks: Float,
        pose: PoseStack,
        buffer: MultiBufferSource,
        pPackedLight: Int
    ) {
        if (car.getLeader().isPresent()) return

        pose.pushPose()

        // render
        var t: AbstractTrainCarEntity = car
        var attachmentPoints = renderCarAndGetAttachmentPoints(car, yaw, pPartialTicks, pose, buffer!!, pPackedLight)

        while (t.getFollower().isPresent()) {
            val nextT = t.getFollower().get()
            val renderer =
                Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(nextT)
            if (renderer is RenderWithAttachmentPoints<*>) {
                val attachmentRenderer = renderer as RenderWithAttachmentPoints<AbstractTrainCarEntity>

                // translate to next train location
                val nextTPos = nextT.getPosition(pPartialTicks)
                val tPos = t.getPosition(pPartialTicks)
                var offset = nextTPos.subtract(tPos)
                pose.translate(offset.x, offset.y, offset.z)
                val newAttachmentPoints = attachmentRenderer.renderCarAndGetAttachmentPoints(
                    nextT,
                    nextT.getYRot(),
                    pPartialTicks,
                    pose,
                    buffer,
                    pPackedLight
                )
                val from = newAttachmentPoints.getFirst()
                val to = attachmentPoints.getSecond()

                // translate to "from" position
                pose.pushPose()
                offset = from.subtract(nextTPos)
                pose.translate(offset.x, offset.y, offset.z)
                getAndRenderChain(nextT, from, to, pose, buffer, pPackedLight)
                pose.popPose()

                attachmentPoints = newAttachmentPoints
            }

            t = nextT
        }

        pose.popPose()
    }

    private fun getAndRenderChain(
        car: AbstractTrainCarEntity?,
        from: Vec3,
        to: Vec3,
        matrixStack: PoseStack,
        buffer: MultiBufferSource,
        p_225623_6_: Int
    ) {
        matrixStack.pushPose()
        val vec = from.vectorTo(to)
        val dist = vec.length()
        val segments = ceil(dist * 4).toInt()

        // TODO: fix pitch
        matrixStack.mulPose(Axis.YP.rotation(-atan2(vec.z, vec.x).toFloat()))
        matrixStack.mulPose(Axis.ZP.rotation((asin(vec.y / dist)).toFloat()))
        matrixStack.pushPose()
        val ivertexbuilderChain = buffer.getBuffer(chainModel.renderType(MultipartCarRenderer.Companion.CHAIN_TEXTURE))
        for (i in 1 until segments) {
            matrixStack.pushPose()
            matrixStack.translate(i / 4.0, 0.0, 0.0)
            chainModel.renderToBuffer(matrixStack, ivertexbuilderChain, p_225623_6_, OverlayTexture.NO_OVERLAY)
            matrixStack.popPose()
        }

        matrixStack.popPose()
        matrixStack.popPose()
    }

    // First - front anchor point
    // Second - back anchor point
    // Override this to change anchor points for larger or smaller cars
    fun getAttachmentPoints(chainCentre: Vec3, trackDirection: Vec3): Pair<Vec3, Vec3> {
        return Pair<Vec3, Vec3>(chainCentre.add(trackDirection.scale(.2)), chainCentre.add(trackDirection.scale(-.2)))
    }

    override fun shouldRender(entity: T?, pCamera: Frustum?, pCamX: Double, pCamY: Double, pCamZ: Double): Boolean {
        return true
    }

    override fun renderCarAndGetAttachmentPoints(
        car: T?,
        yaw: Float,
        partialTicks: Float,
        pose: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ): Pair<Vec3, Vec3> {
        var yaw = yaw
        var attach = Pair<Vec3, Vec3>(
            car!!.getPosition(partialTicks).add(0.0, .44, 0.0),
            car.getPosition(partialTicks).add(0.0, .44, 0.0)
        )

        pose.pushPose()
        var i = car.getId().toLong() * 493286711L
        i = i * i * 4392167121L + i * 98761L
        val f = (((i shr 16 and 7L).toFloat() + 0.5f) / 8.0f - 0.5f) * 0.004f
        val f1 = (((i shr 20 and 7L).toFloat() + 0.5f) / 8.0f - 0.5f) * 0.004f
        val f2 = (((i shr 24 and 7L).toFloat() + 0.5f) / 8.0f - 0.5f) * 0.004f
        pose.translate(f.toDouble(), f1.toDouble(), f2.toDouble())
        val d0 = Mth.lerp(partialTicks.toDouble(), car.xOld, car.getX())
        val d1 = Mth.lerp(partialTicks.toDouble(), car.yOld, car.getY())
        val d2 = Mth.lerp(partialTicks.toDouble(), car.zOld, car.getZ())
        val d3 = 0.3
        val pos = car.getPos(d0, d1, d2)
        var pitch = Mth.lerp(partialTicks, car.xRotO, car.getXRot())
        if (pos != null) {
            var forwardDir = car.getPosOffs(d0, d1, d2, 0.3)
            var backDir = car.getPosOffs(d0, d1, d2, -0.3)
            if (forwardDir == null) {
                forwardDir = pos
            }

            if (backDir == null) {
                backDir = pos
            }

            val centre = Vec3(pos.x, (forwardDir.y + backDir.y) / 2.0, pos.z)
            val offset = centre.subtract(d0, d1, d2)

            pose.translate(offset.x, offset.y, offset.z)
            var trackDirection = forwardDir.subtract(backDir)
            if (trackDirection.length() != 0.0) {
                trackDirection = trackDirection.normalize()
                yaw = (atan2(-trackDirection.z, -trackDirection.x) * 180.0 / Math.PI + 90).toFloat()
                pitch = (atan(-trackDirection.y) * 73.0).toFloat()
            }

            val chainCentre = centre.add(0.0, .22, 0.0)
            attach = getAttachmentPoints(chainCentre, trackDirection)
        }

        pose.translate(0.0, 0.375, 0.0)
        pose.mulPose(Axis.YP.rotationDegrees(180.0f - yaw))
        pose.mulPose(Axis.XN.rotationDegrees(pitch))
        val f5 = car.getHurtTime().toFloat() - partialTicks
        var f6 = car.getDamage() - partialTicks
        if (f6 < 0.0f) {
            f6 = 0.0f
        }

        if (f5 > 0.0f) {
            pose.mulPose(Axis.XP.rotationDegrees(Mth.sin(f5) * f5 * f6 / 10.0f * car.getHurtDir().toFloat()))
        }

        pose.translate(0.0, 1.1, 0.0)

        pose.scale(-1.0f, -1.0f, 1.0f)

        val overlay = OverlayTexture.NO_OVERLAY

        renderBaseModel(car, pose, buffer, packedLight, overlay)
        renderInsertModel(car, pose, buffer, partialTicks, packedLight, overlay)
        renderTrimModel(car, pose, buffer, packedLight, overlay)

        pose.popPose()

        if (car.hasCustomName()) {
            this.renderNameTag(car, car.getCustomName(), pose, buffer, packedLight, partialTicks)
        }

        return attach
    }

    protected fun renderBaseModel(
        entity: T?,
        matrixStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        overlay: Int
    ) {
        baseModel.renderToBuffer(
            matrixStack,
            buffer.getBuffer(baseModel.renderType(baseTextureLocation)),
            packedLight,
            overlay
        )
    }

    protected open fun renderInsertModel(
        entity: T?,
        matrixStack: PoseStack,
        buffer: MultiBufferSource,
        partialTicks: Float,
        packedLight: Int,
        overlay: Int
    ) {
        insertModel.renderToBuffer(
            matrixStack,
            buffer.getBuffer(insertModel.renderType(insertTextureLocation)),
            packedLight,
            overlay
        )
    }

    protected fun renderTrimModel(
        entity: T?,
        matrixStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        overlay: Int
    ) {
        val colorId = entity!!.getColor()
        val color = (if (colorId == null) DyeColor.RED else DyeColor.byId(colorId)).getTextureDiffuseColor()

        trimModel.renderToBuffer(
            matrixStack,
            buffer.getBuffer(trimModel.renderType(trimTextureLocation)),
            packedLight,
            overlay,
            color
        )
    }

    // Do not use these directly
    @Deprecated("")
    override fun getTextureLocation(entity: T?): ResourceLocation {
        return baseTextureLocation
    }

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
            insertModel({ root -> EmptyModel(root) }, EmptyModel.LAYER_LOCATION, entityTexture("emptytexture.png"))
            return this
        }

        fun trimModel(supplier: ModelSupplier<T>, location: ModelLayerLocation, texture: ResourceLocation): Builder<T> {
            this.trimModelPack = ModelPack(supplier, location, texture)
            return this
        }

        open fun build(): MultipartCarRenderer<T> {
            return MultipartCarRenderer(context, baseModelPack, insertModelPack, trimModelPack)
        }
    }

    companion object {
        private val CHAIN_TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "textures/entity/chain.png")
    }
}
