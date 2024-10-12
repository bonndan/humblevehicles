package dev.murad.shipping.entity.custom.vessel.submarine

import dev.murad.shipping.entity.custom.ControlResult
import dev.murad.shipping.entity.custom.VehicleControl
import dev.murad.shipping.entity.custom.vessel.TugControl
import net.minecraft.client.player.Input
import net.minecraft.world.entity.vehicle.Boat

const val DOWN_THRUST = -0.1

/**
 * Adds up/down thrust to regular boat controls.
 */
class SubmarineControl : VehicleControl {

    private var downThrust: Double = 0.0

    override fun calculateResult(input: Input, status: Boat.Status?): ControlResult {

        //regular boat direction controls
        val result = TugControl.calculateResult(input, status)

        if (input.jumping && status == Boat.Status.UNDER_WATER) {
            result.yMovement = 0.08
        }

        if (downThrust < 0.0) {
            result.yMovement += downThrust
            downThrust = 0.0
        }
        return result
    }

    override fun addDownThrust() {
        this.downThrust = DOWN_THRUST
    }
}