package dev.murad.shipping.entity.custom.vessel

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.vehicle.Boat
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks

/**
 * This is the original code for tugs.
 */
object BoatMovementBehaviour : VesselMovementBehaviour {

    /**
     * Default for all regular boats.
     */
    override fun calculateBuoyancy(status: Boat.Status?, waterLevel: Double, y: Double, bbHeight: Double): Double {

        var upForce = 0.0

        if (status == Boat.Status.IN_WATER) {
            upForce = (waterLevel - y) / bbHeight
        } else if (status == Boat.Status.UNDER_WATER) {
            upForce = 0.01
        }

        return upForce * 0.10153846016296973
    }

    override fun calculateDownForce(isNoGravity: Boolean, status: Boat.Status?): Double {

        var downForce = if (isNoGravity) 0.0 else -0.04

        if (status == Boat.Status.UNDER_FLOWING_WATER) {
            downForce = -7.0E-4
        }

        return downForce
    }

    override fun calculateUndrownForce(level: Level, status: Boat.Status?, onPos: BlockPos): Double =
        if (level.getBlockState(onPos.above()).block == Blocks.WATER) {
            0.1
        } else
            0.0
}