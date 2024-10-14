package com.github.bonndan.humblevehicles.entity.custom

import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.core.Direction
import net.minecraft.tags.FluidTags
import net.minecraft.util.Mth
import net.minecraft.world.entity.vehicle.Boat
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.AABB
import kotlin.math.max

object StatusDetector {

    /**
     * Decides whether the boat is currently underwater.
     */
    fun isUnderwater(boundingBox: AABB, level: Level): Boat.Status? {

        val aabb = boundingBox
        val top = aabb.maxY + 0.001
        val i = Mth.floor(aabb.minX)
        val j = Mth.ceil(aabb.maxX)
        val k = Mth.floor(aabb.maxY)
        val l = Mth.ceil(top)
        val i1 = Mth.floor(aabb.minZ)
        val j1 = Mth.ceil(aabb.maxZ)
        val mutableBlockPos = MutableBlockPos()

        for (k1 in i until j) {
            for (l1 in k until l) {
                for (i2 in i1 until j1) {

                    mutableBlockPos.set(k1, l1, i2)

                    val fluidState = level.getFluidState(mutableBlockPos)
                    val fluidHeight = fluidState.getHeight(level, mutableBlockPos)
                    val boxSmallerThanFluidHeight = top < (mutableBlockPos.y + fluidHeight).toDouble()

                    if (fluidState.`is`(FluidTags.WATER) && boxSmallerThanFluidHeight) {
                        if (!fluidState.isSource) {
                            return Boat.Status.UNDER_FLOWING_WATER
                        }

                        return Boat.Status.UNDER_WATER
                    }
                }
            }
        }

        return null
    }

    fun checkInWater(boundingBox: AABB, level: Level): InWaterCheckResult {
        val aabb = boundingBox
        val i = Mth.floor(aabb.minX)
        val j = Mth.ceil(aabb.maxX)
        val k = Mth.floor(aabb.minY)
        val l = Mth.ceil(aabb.minY + 0.001)
        val i1 = Mth.floor(aabb.minZ)
        val j1 = Mth.ceil(aabb.maxZ)
        var flag = false
        var waterLevel = -Double.MAX_VALUE
        val mutableBlockPos = MutableBlockPos()

        for (k1 in i until j) {
            for (l1 in k until l) {
                for (i2 in i1 until j1) {
                    mutableBlockPos[k1, l1] = i2
                    val fluidstate = level.getFluidState(mutableBlockPos)
                    if (fluidstate.`is`(FluidTags.WATER)) {
                        val f = l1.toFloat() + fluidstate.getHeight(level, mutableBlockPos)
                        waterLevel = max(f.toDouble(), waterLevel)
                        flag = flag or (aabb.minY < f.toDouble())
                    }
                }
            }
        }

        return InWaterCheckResult(flag, waterLevel)
    }


    fun hasWaterOnSides(level: Level, onPos: BlockPos, direction: Direction): Boolean =
        level.getFluidState(onPos.relative(direction.clockWise)).`is`(Fluids.WATER) &&
                level.getFluidState(onPos.relative(direction.counterClockWise)).`is`(Fluids.WATER) &&
                level.getBlockState(onPos.above().relative(direction.clockWise)).block == Blocks.AIR &&
                level.getBlockState(onPos.above().relative(direction.counterClockWise)).block == Blocks.AIR

    fun calculateWaterLevelAbove(level: Level, boundingBox: AABB): Float {
        val aabb = boundingBox
        val i = Mth.floor(aabb.minX)
        val j = Mth.ceil(aabb.maxX)
        val k = Mth.floor(aabb.maxY)
        val top = Mth.ceil(aabb.maxY)
        val i1 = Mth.floor(aabb.minZ)
        val j1 = Mth.ceil(aabb.maxZ)
        val mutableBlockPos = MutableBlockPos()

        label39@ for (k1 in k until top) {
            var f = 0.0f

            for (l1 in i until j) {
                for (i2 in i1 until j1) {
                    mutableBlockPos[l1, k1] = i2
                    val fluidstate = level.getFluidState(mutableBlockPos)
                    if (fluidstate.`is`(FluidTags.WATER)) {
                        f = max(f.toDouble(), fluidstate.getHeight(level, mutableBlockPos).toDouble()).toFloat()
                    }

                    if (f >= 1.0f) {
                        continue@label39
                    }
                }
            }

            if (f < 1.0f) {
                return mutableBlockPos.y.toFloat() + f
            }
        }

        return (top + 1).toFloat()
    }
}