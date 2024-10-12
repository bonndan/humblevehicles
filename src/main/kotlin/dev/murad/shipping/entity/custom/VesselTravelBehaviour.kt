package dev.murad.shipping.entity.custom

import dev.murad.shipping.entity.custom.vessel.VesselEntity
import net.minecraft.util.Mth
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.MoverType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.common.NeoForgeMod
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.min
import kotlin.math.sqrt

/**
 * This is the cleaned up code from VesselEntity`s travel function.
 *
 * It prevents jumps out of water, since vessel are derived from water animals.
 */
object VesselTravelBehaviour {

    fun calculateTravel(
        relative: Vec3,
        gravityValue: Double,
        fluidState: FluidState,
        entity: VesselEntity,
        level: Level,
        isAffectedByFluids: Boolean,
        waterSlowDown: Float,
        stuckCounter: AtomicReference<Int>,
    ) {

        val isSinking = entity.deltaMovement.y <= 0.0
        val isInWaterAndAffected = entity.isInWater && isAffectedByFluids && !entity.canStandOnFluid(fluidState)
        val isInLavaAndAffected = entity.isInLava && isAffectedByFluids && !entity.canStandOnFluid(fluidState)

        if (isInWaterAndAffected) {
            calculateInWater(entity, waterSlowDown, relative, gravityValue, isSinking, stuckCounter, level)
            return
        }

        if (isInLavaAndAffected) {
            calculateInLava(entity, relative, gravityValue, isSinking)
            return
        }

        if (entity.isFallFlying) {
            calculateFallFlying(entity, gravityValue, level)
            return
        }

        calculateOtherState(entity, level, relative, gravityValue)
    }

    private fun calculateOtherState(entity: VesselEntity, level: Level, relative: Vec3, gravityValue: Double) {

        val blockpos = entity.blockPosBelowThatAffectsMyMovement
        val friction = level.getBlockState(blockpos).getFriction(level, blockpos, entity)

        val frictionModifier = (if (entity.onGround()) friction * 0.91f else 0.91f).toDouble()
        val vector3d5 = entity.handleRelativeFrictionAndCalculateMovement(relative, friction)
        var yMovement = vector3d5.y

        when {
            entity.hasEffect(MobEffects.LEVITATION) -> {
                yMovement += (0.05 * (entity.getEffect(MobEffects.LEVITATION)!!.amplifier + 1).toDouble() - vector3d5.y) * 0.2
                entity.fallDistance = 0.0f
            }

            level.isClientSide && !level.hasChunkAt(blockpos) -> {
                yMovement = if (entity.y > 0.0) {
                    -0.1
                } else {
                    0.0
                }
            }

            !entity.isNoGravity -> {
                yMovement -= gravityValue
            }
        }

        entity.setDeltaMovement(vector3d5.x * frictionModifier, yMovement * 0.98, vector3d5.z * frictionModifier)
    }

    private fun calculateInLava(
        entity: VesselEntity,
        relative: Vec3,
        gravityValue: Double,
        isSinking: Boolean
    ) {
        val d7 = entity.y
        entity.moveRelative(0.02f, relative)
        entity.move(MoverType.SELF, entity.deltaMovement)
        val height = entity.getFluidTypeHeight(NeoForgeMod.LAVA_TYPE.value())
        if (height <= entity.fluidJumpThreshold) {
            entity.deltaMovement = entity.deltaMovement.multiply(0.5, 0.8, 0.5)
            val vector3d3 = entity.getFluidFallingAdjustedMovement(gravityValue, isSinking, entity.deltaMovement)
            entity.deltaMovement = vector3d3
        } else {
            entity.deltaMovement = entity.deltaMovement.scale(0.5)
        }

        if (!entity.isNoGravity) {
            entity.deltaMovement = entity.deltaMovement.add(0.0, -gravityValue / 4.0, 0.0)
        }

        val vector3d4 = entity.deltaMovement
        if (entity.horizontalCollision && entity.isFree(
                vector3d4.x,
                vector3d4.y + 0.6 - entity.y + d7,
                vector3d4.z
            )
        ) {
            entity.setDeltaMovement(vector3d4.x, 0.3, vector3d4.z)
        }
    }

    private fun calculateInWater(
        entity: VesselEntity,
        waterSlowDown: Float,
        relative: Vec3,
        gravityValue: Double,
        isSinking: Boolean,
        stuckCounter: AtomicReference<Int>,
        level: Level
    ) {
        var f5 = if (entity.isSprinting) 0.9f else waterSlowDown
        var f6 = 0.02f
        var f7 = 0f
        if (f7 > 3.0f) {
            f7 = 3.0f
        }

        if (!entity.onGround()) {
            f7 *= 0.5f
        }

        if (f7 > 0.0f) {
            f5 += (0.54600006f - f5) * f7 / 3.0f
            f6 += (entity.speed - f6) * f7 / 3.0f
        }

        if (entity.hasEffect(MobEffects.DOLPHINS_GRACE)) {
            f5 = 0.96f
        }

        f6 *= entity.swimSpeed().toFloat()
        entity.moveRelative(f6, relative)
        entity.move(MoverType.SELF, entity.deltaMovement)
        var vector3d6 = entity.deltaMovement
        if (entity.horizontalCollision && entity.onClimbable()) {
            vector3d6 = Vec3(vector3d6.x, 0.2, vector3d6.z)
        }

        entity.deltaMovement = vector3d6.multiply(f5.toDouble(), 0.8, f5.toDouble())
        entity.deltaMovement = entity.getFluidFallingAdjustedMovement(gravityValue, isSinking, entity.deltaMovement)

        /**
         * TODO this could also work for ice to have an icebreaker tug.
         */
        if (entity.horizontalCollision) {
            if (stuckCounter.get() > 10) {
                // destroy lilypads
                val direction = entity.direction
                val front = entity.onPos.relative(direction).above()
                val left = front.relative(direction.clockWise)
                val right = front.relative(direction.counterClockWise)
                for (pos in Arrays.asList(front, left, right)) {
                    val state = level.getBlockState(pos)
                    if (state.`is`(Blocks.LILY_PAD)) {
                        level.destroyBlock(pos, true)
                    }
                }
                stuckCounter.set(0)
            } else {
                stuckCounter.getAndUpdate { it + 1 }
            }
        } else {
            //                    stuckCounter = 0;
        }
    }

    private fun calculateFallFlying(entity: VesselEntity, gravityValue: Double, level: Level) {

        var vector3d = entity.deltaMovement
        if (vector3d.y > -0.5) {
            entity.fallDistance = 1.0f
        }

        val vector3d1 = entity.lookAngle
        val f = entity.xRot * (Math.PI.toFloat() / 180f)
        val d1 = sqrt(vector3d1.x * vector3d1.x + vector3d1.z * vector3d1.z)
        val d3 = entity.deltaMovement.horizontalDistance()
        val d4 = vector3d1.length()
        var f1 = Mth.cos(f)
        f1 = (f1.toDouble() * f1.toDouble() * min(1.0, d4 / 0.4)).toFloat()
        vector3d = entity.deltaMovement.add(0.0, gravityValue * (-1.0 + f1.toDouble() * 0.75), 0.0)
        if (vector3d.y < 0.0 && d1 > 0.0) {
            val d5 = vector3d.y * -0.1 * f1.toDouble()
            vector3d = vector3d.add(vector3d1.x * d5 / d1, d5, vector3d1.z * d5 / d1)
        }

        if (f < 0.0f && d1 > 0.0) {
            val d9 = d3 * (-Mth.sin(f)).toDouble() * 0.04
            vector3d = vector3d.add(-vector3d1.x * d9 / d1, d9 * 3.2, -vector3d1.z * d9 / d1)
        }

        if (d1 > 0.0) {
            vector3d = vector3d.add(
                (vector3d1.x / d1 * d3 - vector3d.x) * 0.1,
                0.0,
                (vector3d1.z / d1 * d3 - vector3d.z) * 0.1
            )
        }

        entity.deltaMovement = vector3d.multiply(0.99, 0.98, 0.99)
        entity.move(MoverType.SELF, entity.deltaMovement)
        if (entity.horizontalCollision && !level.isClientSide) {
            val d10 = entity.deltaMovement.horizontalDistance()
            val d6 = d3 - d10
            val f2 = (d6 * 10.0 - 3.0).toFloat()
            if (f2 > 0.0f) {
                entity.hurt(level.damageSources().flyIntoWall(), f2)
            }
        }

        if (entity.onGround() && !level.isClientSide) {
            entity.setFlyingState(false)
        }
    }
}