package com.github.bonndan.humblevehicles.entity.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.datafixers.util.Pair
import com.github.bonndan.humblevehicles.entity.custom.train.AbstractTrainCarEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.phys.Vec3

interface RenderWithAttachmentPoints<T : AbstractTrainCarEntity> {

    fun renderCarAndGetAttachmentPoints(
        car: T?,
        yaw: Float,
        partialTicks: Float,
        pose: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ): Pair<Vec3, Vec3>
}
