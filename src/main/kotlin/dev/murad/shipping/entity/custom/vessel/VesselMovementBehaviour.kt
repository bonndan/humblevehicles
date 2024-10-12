package dev.murad.shipping.entity.custom.vessel

import dev.murad.shipping.entity.custom.VesselTravelBehaviour
import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.vehicle.Boat
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.WaterlilyBlock
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.Shapes
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import java.util.concurrent.atomic.AtomicReference

/**
 * This is for vessels which are actually water animals.
 */
interface VesselMovementBehaviour {

    fun calculateBuoyancy(status: Boat.Status?, waterLevel: Double, y: Double, bbHeight: Double): Double

    fun calculateDownForce(isNoGravity: Boolean, status: Boat.Status?): Double

    /**
     * Decides how much the boat should be gliding on the land (based on any slippery blocks)
     */
     fun calculateGroundFriction(aabb: AABB, level: Level, entity: Entity): Float {

        val aabb1 = AABB(aabb.minX, aabb.minY - 0.001, aabb.minZ, aabb.maxX, aabb.minY, aabb.maxZ)
        val i = Mth.floor(aabb1.minX) - 1
        val j = Mth.ceil(aabb1.maxX) + 1
        val k = Mth.floor(aabb1.minY) - 1
        val l = Mth.ceil(aabb1.maxY) + 1
        val i1 = Mth.floor(aabb1.minZ) - 1
        val j1 = Mth.ceil(aabb1.maxZ) + 1
        val voxelshape = Shapes.create(aabb1)
        var f = 0.0f
        var k1 = 0
        val mutableBlockPos = MutableBlockPos()

        for (l1 in i until j) {
            for (i2 in i1 until j1) {
                val j2 = (if (l1 != i && l1 != j - 1) 0 else 1) + (if (i2 != i1 && i2 != j1 - 1) 0 else 1)
                if (j2 != 2) {
                    for (k2 in k until l) {
                        if (j2 <= 0 || k2 != k && k2 != l - 1) {
                            mutableBlockPos[l1, k2] = i2
                            val blockstate = level.getBlockState(mutableBlockPos)
                            if (blockstate.block !is WaterlilyBlock && Shapes.joinIsNotEmpty(
                                    blockstate.getCollisionShape(level, mutableBlockPos)
                                        .move(l1.toDouble(), k2.toDouble(), i2.toDouble()), voxelshape, BooleanOp.AND
                                )
                            ) {
                                f += blockstate.getFriction(level, mutableBlockPos, entity)
                                ++k1
                            }
                        }
                    }
                }
            }
        }

        return f / k1.toFloat()


    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    fun calculateUndrownForce(level: Level, status: Boat.Status?, onPos: BlockPos): Double

    fun calculateFriction(status: Boat.Status?): Float {

        if (status == Boat.Status.ON_LAND) {
            throw IllegalStateException("On land friction has already been calculated")
        }

        return when (status) {
            Boat.Status.IN_WATER -> { 0.9f }
            Boat.Status.UNDER_FLOWING_WATER -> { 0.9f }
            Boat.Status.UNDER_WATER -> { 0.45f }
            Boat.Status.IN_AIR -> { 0.9f }
            else -> 0.05f
        }
    }

    /**
     *
     */
    fun travel(
        relative: Vec3,
        gravityValue: Double,
        fluidState: FluidState,
        entity: VesselEntity,
        level: Level,
        isAffectedByFluids: Boolean,
        waterSlowDown: Float,
        stuckCounter: AtomicReference<Int>
    ) {
        return VesselTravelBehaviour.calculateTravel(relative,
            gravityValue,
            fluidState,
            entity,
            level,
            isAffectedByFluids,
            waterSlowDown,
            stuckCounter)
    }

    fun isFallingIn(fluid: Fluid): Boolean {
        return true
    }
}