package dev.murad.shipping.entity.custom.vessel.submarine

import dev.murad.shipping.entity.custom.Engine
import dev.murad.shipping.entity.custom.vessel.VesselMovementBehaviour
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.vehicle.Boat
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids.WATER

const val ENGINE_OFF_AUTO_RAISE_SPEED = 0.1

class SubmarineMovementBehaviour(private val engine: Engine): VesselMovementBehaviour {

    /**
     * TODO see why unDrown is still necessary. This is for IN_WATER
     */
    override fun calculateBuoyancy(status: Boat.Status?, waterLevel: Double, y: Double, bbHeight: Double): Double {

        if (engine.isOn()) {
            return 0.0
        }

        var upForce = 0.0
        if (status == Boat.Status.IN_WATER) {
            upForce = (waterLevel - y) / bbHeight //same as boats
        } else if (status == Boat.Status.UNDER_WATER) {
            upForce = 0.5 //always rise (downforce is about 0.04)
        }

        return upForce * 0.10153846016296973
    }

    override fun calculateDownForce(isNoGravity: Boolean, status: Boat.Status?): Double {

        var downForce = if (isNoGravity) 0.0 else -0.04

        if (status == Boat.Status.UNDER_FLOWING_WATER || status == Boat.Status.UNDER_WATER) {
            downForce = -0.0
        }

        return downForce
    }

    /**
     * sink underwater when engine is on, raise when off (server side only)
     */
    override fun calculateUndrownForce(level: Level, status: Boat.Status?, onPos: BlockPos): Double {

        if (!engine.isOn() && (status == Boat.Status.UNDER_WATER || status == Boat.Status.UNDER_FLOWING_WATER)) {
            return ENGINE_OFF_AUTO_RAISE_SPEED
        }

        return 0.0
    }

    override fun isFallingIn(fluid: Fluid): Boolean {

        if (fluid == WATER) {
            return false
        }

        return super.isFallingIn(fluid)
    }
}