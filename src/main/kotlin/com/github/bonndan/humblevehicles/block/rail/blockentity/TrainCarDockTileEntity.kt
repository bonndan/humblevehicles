package com.github.bonndan.humblevehicles.block.rail.blockentity

import com.github.bonndan.humblevehicles.block.dock.AbstractTailDockTileEntity
import com.github.bonndan.humblevehicles.block.rail.AbstractDockingRail
import com.github.bonndan.humblevehicles.entity.custom.train.AbstractTrainCarEntity
import com.github.bonndan.humblevehicles.setup.ModTileEntitiesTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.RailShape

class TrainCarDockTileEntity(pos: BlockPos, state: BlockState) :
    AbstractTailDockTileEntity<AbstractTrainCarEntity>(ModTileEntitiesTypes.CAR_DOCK.get(), pos, state) {

    override fun getTargetBlockPos(): List<BlockPos> {

        if (this.isExtracting) {
            return listOf(blockPos.below())
        }
        val facing =
            if (blockState.getValue(AbstractDockingRail.RAIL_SHAPE) == RailShape.EAST_WEST) Direction.EAST else Direction.NORTH

        return listOf(blockPos.relative(facing.counterClockWise), blockPos.relative(facing.clockWise))
    }

    override fun checkBadDirCondition(direction: Direction): Boolean {
        return false
    }
}
