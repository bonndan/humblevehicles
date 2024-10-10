package dev.murad.shipping.block.rail.blockentity

import dev.murad.shipping.block.dock.AbstractHeadDockTileEntity
import dev.murad.shipping.block.dock.DockingBlockStates
import dev.murad.shipping.entity.custom.train.AbstractTrainCarEntity
import dev.murad.shipping.setup.ModTileEntitiesTypes
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
