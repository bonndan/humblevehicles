package dev.murad.shipping.entity.custom.vessel.submarine

import dev.murad.shipping.entity.custom.Engine
import dev.murad.shipping.entity.custom.vessel.BoatFloatBehaviour
import dev.murad.shipping.entity.custom.vessel.FloatBehaviour
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.vehicle.Boat
import net.minecraft.world.level.Level


private const val ENGINE_ON_SINK_SPEED = -0.02
private const val ENGINE_OFF_AUTO_RAISE_SPEED = 0.1

class SubmarineFloatBehaviour(private val engine: Engine): FloatBehaviour {

    /**
     * TODO see why unDrown is still necessary. This is for IN_WATER
     */
    override fun calculateBuoyancy(status: Boat.Status?, waterLevel: Double, y: Double, bbHeight: Double): Double {

        if (engine.isOn()) {
            return 0.0
        }

        var upForce = 0.0
        if (status == Boat.Status.IN_WATER) {
            upForce = (waterLevel - y) / bbHeight //same as above
        } else if (status == Boat.Status.UNDER_WATER) {
            upForce = 0.5 //always rise (downforce is about 0.04)
        }

        return upForce * 0.10153846016296973
    }

    override fun calculateDownForce(isNoGravity: Boolean, status: Boat.Status?): Double {
        return BoatFloatBehaviour.calculateDownForce(isNoGravity, status)
    }

    /**
     * sink underwater when engine is on, raise when off (server side only)
     */
    override fun calculateUndrownForce(level: Level, status: Boat.Status?, onPos: BlockPos): Double {

        if (engine.isOn() && status == Boat.Status.IN_WATER) {
            return  ENGINE_ON_SINK_SPEED
        }

        if (!engine.isOn() && (status == Boat.Status.UNDER_WATER || status == Boat.Status.UNDER_FLOWING_WATER)) {
            return ENGINE_OFF_AUTO_RAISE_SPEED
        }

        return 0.0
    }

}