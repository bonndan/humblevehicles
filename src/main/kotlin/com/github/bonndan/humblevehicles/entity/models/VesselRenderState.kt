package com.github.bonndan.humblevehicles.entity.models

import com.github.bonndan.humblevehicles.entity.Colorable
import com.github.bonndan.humblevehicles.entity.custom.train.AbstractTrainCarEntity
import net.minecraft.client.renderer.entity.state.MinecartRenderState
import net.minecraft.core.Direction
import net.minecraft.world.item.DyeColor
import java.util.Optional

class VesselRenderState : MinecartRenderState(), Colorable {

    private var color: Int? = null
    var follower: Optional<AbstractTrainCarEntity> = Optional.empty()
    var direction: Direction? = null

    override fun getColorId(): Int {
        return color ?: DyeColor.RED.id
    }

    override fun setColorId(color: Int?) {
        this.color = color
    }
}