package com.github.bonndan.humblevehicles.block.dock

import com.github.bonndan.humblevehicles.entity.custom.vessel.VesselEntity
import com.github.bonndan.humblevehicles.setup.ModTileEntitiesTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState

class BargeDockTileEntity(pos: BlockPos, state: BlockState) :
    AbstractTailDockTileEntity<VesselEntity>(ModTileEntitiesTypes.BARGE_DOCK.get(), pos, state) {

    override fun getTargetBlockPos(): List<BlockPos> =
        if (isExtracting) {
            listOf(this.blockPos.below().relative(blockState.getValue(DockingBlockStates.FACING)))
        } else {
            listOf(this.blockPos.above())
        }

    override fun checkBadDirCondition(direction: Direction): Boolean {
        return blockState.getValue(DockingBlockStates.FACING).opposite != direction
    }
}
