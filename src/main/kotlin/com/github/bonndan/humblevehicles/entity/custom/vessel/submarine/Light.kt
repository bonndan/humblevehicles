package com.github.bonndan.humblevehicles.entity.custom.vessel.submarine

import com.github.bonndan.humblevehicles.setup.ModBlocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks.WATER
import net.minecraft.world.level.block.state.BlockState

/**
 * Adds a light emitting water block to the submarine´s position to simulate lights.
 */
class Light(private val level: Level) {

    private var oldState: BlockState? = null
    private var oldPos: BlockPos? = null
    private var light: LightTileEntity? = null

    fun update(targetPosition: BlockPos, lit: Boolean) {

        if (!lit) {
            turnOff()
            return
        }

        val targetState = level.getBlockState(targetPosition)
        if (targetState.block != WATER) {
            return
        }

        val lightEntity = getLightEntity(targetPosition, targetState)

        if (positionHasChanged(oldPos, targetPosition)) {

            if (oldPos != null && oldState != null) {
                removePreviousLight(oldPos!!, oldState!!)
            }

            oldState = targetState
            oldPos = targetPosition
            level.setBlockAndUpdate(targetPosition, lightEntity.blockState)
        }
    }

    fun turnOff() {
        if (oldPos != null && oldState != null) {
            removePreviousLight(oldPos!!, oldState!!)
            oldState = null
            oldPos = null
        }
    }

    private fun positionHasChanged(old: BlockPos?, lightPos: BlockPos) =
        old == null || old.compareTo(lightPos) != 0

    private fun getLightEntity(lightPos: BlockPos, targetState: BlockState): LightTileEntity {

        if (light == null) {
            light = LightTileEntity(lightPos, ModBlocks.LIGHT_BLOCK.get().withPropertiesOf(targetState))
            level.setBlockEntity(light!!)
        }

        return light!!
    }

    private fun removePreviousLight(old: BlockPos, previousState: BlockState) {
        level.setBlockAndUpdate(old, previousState)
    }

    companion object {
        const val LIGHT_LEVEL = 8
    }
}