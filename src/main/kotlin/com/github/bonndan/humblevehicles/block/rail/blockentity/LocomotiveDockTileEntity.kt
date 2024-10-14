package com.github.bonndan.humblevehicles.block.rail.blockentity

import com.github.bonndan.humblevehicles.block.dock.AbstractHeadDockTileEntity
import com.github.bonndan.humblevehicles.block.dock.DockingBlockStates
import com.github.bonndan.humblevehicles.entity.custom.train.AbstractTrainCarEntity
import com.github.bonndan.humblevehicles.setup.ModTileEntitiesTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState

class LocomotiveDockTileEntity(pos: BlockPos, state: BlockState) :
    AbstractHeadDockTileEntity<AbstractTrainCarEntity>(ModTileEntitiesTypes.LOCOMOTIVE_DOCK.get(), pos, state) {

    override fun getTargetBlockPos(): List<BlockPos> {

        val facing = blockState.getValue(DockingBlockStates.FACING)
        return listOf(blockPos.relative(facing.counterClockWise), blockPos.relative(facing.clockWise))
    }

    override fun checkBadDirCondition(tug: AbstractTrainCarEntity, direction: Direction): Boolean {
        return tug.direction != blockState.getValue(DockingBlockStates.FACING)
    }

    override fun getRowDirection(facing: Direction): Direction {
        return facing.opposite
    }
}
