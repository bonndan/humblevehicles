package dev.murad.shipping.entity.custom

import net.minecraft.client.player.Input
import net.minecraft.util.Mth
import net.minecraft.world.entity.vehicle.Boat
import net.minecraft.world.phys.Vec3

interface VehicleControl {

    fun calculateResult(input: Input, status: Boat.Status?): ControlResult
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
