package com.github.bonndan.humblevehicles.entity.custom.vessel.submarine

import com.github.bonndan.humblevehicles.entity.custom.vessel.submarine.Light.Companion.LIGHT_LEVEL
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.material.Fluids

class LightBlock(properties: Properties) : LiquidBlock(Fluids.WATER, properties) {

    companion object {
        val behaviour: Properties = Blocks.WATER.properties().lightLevel { LIGHT_LEVEL }
    }

}