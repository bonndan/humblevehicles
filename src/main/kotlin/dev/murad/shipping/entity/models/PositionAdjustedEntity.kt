package dev.murad.shipping.entity.models

import net.minecraft.world.phys.Vec3

/**
 * For entities which have unusual models.
 *
 * TODO this can probably be solved with off-the-shelf tools and should not be necessary.
 */
interface PositionAdjustedEntity {

    /**
     * Y-Axis adjustment for the positioning of the model in the world.
     */
    fun getModelYOffset(): Double

    /**
     * Rotates a point around the y-axis in a given degree (e.g. of the entity).
     */
    fun transformPoint(pPoint: Vec3, pYRot: Float): Vec3 {
        return pPoint.yRot(-pYRot * (Math.PI / 180.0).toFloat())
    }
}