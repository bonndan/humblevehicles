package com.github.bonndan.humblevehicles.rendering

import com.mojang.blaze3d.vertex.ByteBufferBuilder
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import com.github.bonndan.humblevehicles.rendering.RenderUtil.computeFixedDistance
import com.github.bonndan.humblevehicles.network.client.VehicleTrackerPacketHandler
import com.github.bonndan.humblevehicles.setup.ModEntityTypes
import com.github.bonndan.humblevehicles.setup.ModItems
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.neoforged.neoforge.client.event.RenderLevelStageEvent

class VehicleTrackerRenderer {

    private val entityItemMap: MutableMap<String, Item> = mutableMapOf()

    init {
        entityItemMap[ModEntityTypes.ENERGY_LOCOMOTIVE.get().toString()] = ModItems.ENERGY_LOCOMOTIVE.get()
        entityItemMap[ModEntityTypes.STEAM_LOCOMOTIVE.get().toString()] = ModItems.STEAM_LOCOMOTIVE.get()
        entityItemMap[ModEntityTypes.ENERGY_TUG.get().toString()] = ModItems.ENERGY_TUG.get()
        entityItemMap[ModEntityTypes.STEAM_TUG.get().toString()] = ModItems.STEAM_TUG.get()
    }

    fun render(event: RenderLevelStageEvent, player: Player) {
        val renderTypeBuffer: MultiBufferSource.BufferSource = MultiBufferSource.immediate(ByteBufferBuilder(1536))
        val camera = Minecraft.getInstance().entityRenderDispatcher.camera
        val camPos = camera.position

        for (position in VehicleTrackerPacketHandler.toRender) {
            val entity = player.level().getEntity(position.id)

            val entityPos =
                if (entity != null) entity.getPosition(event.partialTick.gameTimeDeltaTicks) else position.pos
            val iconRenderPos = computeFixedDistance(entityPos, camPos, 1.0)
            val textRenderPos = computeFixedDistance(entityPos, camPos, 0.9)
            val matrixStack = PoseStack()
            matrixStack.mulPose(event.projectionMatrix)

            matrixStack.pushPose()
            run {
                matrixStack.translate(
                    iconRenderPos.x - camPos.x,
                    iconRenderPos.y - camPos.y,
                    iconRenderPos.z - camPos.z
                )
                matrixStack.mulPose(Axis.YP.rotationDegrees(-camera.yRot))
                matrixStack.mulPose(Axis.XP.rotationDegrees(camera.xRot))
                Minecraft.getInstance().itemRenderer.renderStatic(
                    ItemStack(entityItemMap.getOrDefault(position.type, Items.MINECART)),
                    ItemDisplayContext.GROUND,
                    150,
                    OverlayTexture.NO_OVERLAY,
                    matrixStack,
                    renderTypeBuffer,
                    player.level(),
                    position.id
                )
            }
            matrixStack.popPose()
            matrixStack.pushPose()
            run {
                matrixStack.translate(
                    textRenderPos.x - camPos.x,
                    textRenderPos.y - camPos.y,
                    textRenderPos.z - camPos.z
                )
                matrixStack.mulPose(Axis.YP.rotationDegrees(-camera.yRot))
                matrixStack.mulPose(Axis.XP.rotationDegrees(camera.xRot))

                matrixStack.scale(-0.025f, -0.025f, -0.025f)

                val fontRenderer = Minecraft.getInstance().font
                val text = String.format("%.1fm", position.pos.distanceTo(player.position()))

                fontRenderer.drawInBatch(
                    text,
                    (-fontRenderer.width(text) / 2f), 0.0f,
                    -1, true,
                    matrixStack.last().pose(), renderTypeBuffer,
                    Font.DisplayMode.NORMAL,
                    0, 15728880
                )

                val name = entity?.customName
                if (entity != null && name!= null) {

                    matrixStack.translate(0f, -20f, 0f)
                    fontRenderer.drawInBatch(
                        name,
                        (-fontRenderer.width(name) / 2f),
                        0.0f,
                        -1,
                        true,
                        matrixStack.last().pose(),
                        renderTypeBuffer,
                        Font.DisplayMode.NORMAL,
                        0,
                        15728880
                    )
                }
            }
            matrixStack.popPose()
        }

        renderTypeBuffer.endBatch()
    }
}