package dev.murad.shipping.entity.custom.vessel

import dev.murad.shipping.entity.custom.ControlResult
import dev.murad.shipping.entity.custom.VehicleControl
import net.minecraft.client.player.Input
import net.minecraft.world.entity.vehicle.Boat

object TugControl : VehicleControl {

    override fun calculateResult(input: Input, status: Boat.Status?): ControlResult {

        val result = ControlResult()

        if (input.left) {
            result.deltaRotationModifier--
        }

        if (input.right) {
            result.deltaRotationModifier++
        }

        if (input.right != input.left && !input.up && !input.down) {
            result.force += 0.005f
        }

        if (input.up) {
            result.force += 0.05f
        }

        if (input.down) {
            result.force -= 0.005f
        }

        result.yRotationModifier += result.deltaRotationModifier

        return result
    }
}