package com.github.bonndan.humblevehicles.block.dock

import com.github.bonndan.humblevehicles.entity.custom.vessel.VesselEntity
import com.github.bonndan.humblevehicles.setup.ModTileEntitiesTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState

class TugDockTileEntity(pos: BlockPos, state: BlockState) :
    AbstractHeadDockTileEntity<VesselEntity>(ModTileEntitiesTypes.TUG_DOCK.get(), pos, state) {

    override fun getTargetBlockPos(): List<BlockPos> = listOf(this.blockPos.above())

    override fun checkBadDirCondition(tug: VesselEntity, direction: Direction): Boolean {
        return blockState.getValue(DockingBlockStates.FACING).opposite != direction
                || tug.direction == getRowDirection(blockState.getValue(DockingBlockStates.FACING)
        )
    }

    override fun getRowDirection(facing: Direction): Direction {
        return if (blockState.getValue(DockingBlockStates.INVERTED)) facing.clockWise else facing.counterClockWise
    }
}
