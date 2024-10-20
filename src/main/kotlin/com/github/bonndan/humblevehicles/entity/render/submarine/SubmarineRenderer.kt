package com.github.bonndan.humblevehicles.entity.render.submarine

import com.github.bonndan.humblevehicles.entity.custom.vessel.submarine.SubmarineEntity
import com.github.bonndan.humblevehicles.entity.render.ModelPack
import com.github.bonndan.humblevehicles.entity.render.barge.MultipartVesselRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.core.BlockPos

class SubmarineRenderer (
    context: EntityRendererProvider.Context,
    baseModelPack: ModelPack<SubmarineEntity>,
    insertModelPack: ModelPack<SubmarineEntity>,
    trimModelPack: ModelPack<SubmarineEntity>
) : MultipartVesselRenderer<SubmarineEntity>(context, baseModelPack, insertModelPack, trimModelPack) {

    override fun getBlockLightLevel(entity: SubmarineEntity, pos: BlockPos): Int {

        if (entity.getEngine().isLit()) {
            return 8
        }
        return super.getBlockLightLevel(entity, pos)
    }

    class Builder(context: EntityRendererProvider.Context) :
        MultipartVesselRenderer.Builder<SubmarineEntity>(context) {

        override fun build(): SubmarineRenderer {
            return SubmarineRenderer(
                context,
                baseModelPack,
                insertModelPack,
                trimModelPack
            )
        }
    }
}
