package com.github.bonndan.humblevehicles.entity.models

import com.github.bonndan.humblevehicles.entity.Colorable
import com.github.bonndan.humblevehicles.entity.custom.train.AbstractTrainCarEntity
import net.minecraft.client.renderer.entity.state.MinecartRenderState
import java.util.Optional

class VesselRenderState : MinecartRenderState(), Colorable {

    private var color: Int? = null
    var follower: Optional<AbstractTrainCarEntity> = Optional.empty()

    override fun getColor(): Int? {
        return color
    }

    override fun setColor(color: Int?) {
        this.color = color
    }
}