package dev.murad.shipping.entity.render.train

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.datafixers.util.Pair
import com.mojang.math.Axis
import dev.murad.shipping.HumVeeMod
import dev.murad.shipping.entity.custom.train.AbstractTrainCarEntity
import dev.murad.shipping.entity.models.train.ChainModel
import dev.murad.shipping.entity.render.RenderWithAttachmentPoints
import net.minecraft.client.Minecraft
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import java.util.function.Function
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.ceil

class TrainCarRenderer<T : AbstractTrainCarEntity>(
    context: EntityRendererProvider.Context,
    baseModel: Function<ModelPart?, EntityModel<T>>,
    layerLocation: ModelLayerLocation,
    baseTexture: ResourceLocation
) : EntityRenderer<T>(context), RenderWithAttachmentPoints<T> {

    private val entityModel: EntityModel<T>
    private val texture: ResourceLocation?

    private val chainModel: ChainModel

    constructor(
        context: EntityRendererProvider.Context,
        baseModel: Function<ModelPart?, EntityModel<T>>,
        layerLocation: ModelLayerLocation, baseTexture: String
    ) : this(context, baseModel, layerLocation, ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, baseTexture))

    init {
        chainModel = ChainModel(context.bakeLayer(ChainModel.Companion.LAYER_LOCATION))
        entityModel = baseModel.apply(context.bakeLayer(layerLocation))
        texture = baseTexture
    }

    override fun render(
        car: T?,
        yaw: Float,
        pPartialTicks: Float,
        pose: PoseStack?,
        buffer: MultiBufferSource?,
        pPackedLight: Int
    ) {
        //getAndRenderChain(car, pose, buffer, pPackedLight);
        if (car!!.getLeader().isPresent()) return

        pose!!.pushPose()

        // render
        var t: AbstractTrainCarEntity = car
        var attachmentPoints = renderCarAndGetAttachmentPoints(car, yaw, pPartialTicks, pose, buffer!!, pPackedLight)

        while (t.getFollower().isPresent()) {
            val nextT = t.getFollower().get()
            val renderer =
                Minecraft.getInstance().getEntityRenderDispatcher().getRenderer<AbstractTrainCarEntity?>(nextT)
            if (renderer is RenderWithAttachmentPoints<*>) {
                val attachmentRenderer = renderer as RenderWithAttachmentPoints<AbstractTrainCarEntity?>

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
        val segments = ceil(dist * 4) as Int

        // TODO: fix pitch
        matrixStack.mulPose(Axis.YP.rotation(-atan2(vec.z, vec.x) as Float))
        matrixStack.mulPose(Axis.ZP.rotation((asin(vec.y / dist)) as Float))
        matrixStack.pushPose()
        val ivertexbuilderChain = buffer.getBuffer(chainModel.renderType(TrainCarRenderer.Companion.CHAIN_TEXTURE))
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
        this.entityModel.setupAnim(car, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
        val vertexconsumer = buffer.getBuffer(this.entityModel.renderType(this.getTextureLocation(car)))
        this.entityModel.renderToBuffer(pose, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY)
        renderAdditional(car, yaw, partialTicks, pose, buffer, packedLight)
        pose.popPose()

        if (car.hasCustomName()) {
            this.renderNameTag(car, car.getCustomName(), pose, buffer, packedLight, partialTicks)
        }

        return attach
    }

    protected fun renderAdditional(
        pEntity: T?,
        pEntityYaw: Float,
        pPartialTicks: Float,
        pMatrixStack: PoseStack?,
        pBuffer: MultiBufferSource?,
        pPackedLight: Int
    ) {
    }

    override fun getTextureLocation(entity: T?): ResourceLocation? {
        return texture
    }

    companion object {
        private val CHAIN_TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "textures/entity/chain.png")
    }
}
