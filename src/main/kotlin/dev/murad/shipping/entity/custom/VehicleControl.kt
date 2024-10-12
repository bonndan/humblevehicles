package dev.murad.shipping.entity.custom

import net.minecraft.client.player.Input
import net.minecraft.client.player.LocalPlayer
import net.minecraft.util.Mth
import net.minecraft.world.entity.vehicle.Boat
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.event.InputEvent

interface VehicleControl {

    fun calculateResult(input: Input, status: Boat.Status?): ControlResult

    /**
     * Adds a downward force to the input
     */
    fun addDownThrust() {
        //only applies to 3D controls
    }

    companion object {

        fun handleKeyForVehicleControlDownForce(event: InputEvent.Key?, triggerKey: Int, player: LocalPlayer?) {

            if (event?.key != triggerKey) return

            if (player is LocalPlayer && player.isPassenger) {
                val vehicle = player.vehicle
                if (vehicle is HeadVehicle) {
                    vehicle.getControl().addDownThrust()
                }
            }
        }

        /**
         * This is for vehicles not using VehicleControl.
         */
        val IGNORED = object : VehicleControl {
            override fun calculateResult(input: Input, status: Boat.Status?): ControlResult { return ControlResult() }
        }
    }
}

class ControlResult {

    var force: Float = 0f
    var deltaRotationModifier = 0
    var yRotationModifier = 0
    var yMovement = 0.0

    fun calculateDeltaMovement(yRot: Float): Vec3 {

        return Vec3(
            (Mth.sin(-yRot * (Math.PI / 180.0).toFloat()) * force).toDouble(),
            yMovement,
            (Mth.cos(yRot * (Math.PI / 180.0).toFloat()) * force).toDouble()
        )
    }
}
