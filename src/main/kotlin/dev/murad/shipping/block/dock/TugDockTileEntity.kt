package dev.murad.shipping.block.dock

import dev.murad.shipping.entity.custom.vessel.VesselEntity
import dev.murad.shipping.setup.ModTileEntitiesTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState

class TugDockTileEntity(pos: BlockPos?, state: BlockState?) :
    AbstractHeadDockTileEntity<VesselEntity?>(ModTileEntitiesTypes.TUG_DOCK.get(), pos, state) {
    override val targetBlockPos: List<BlockPos>
        get() = java.util.List.of(this.blockPos.above())

    override fun checkBadDirCondition(tug: VesselEntity?, direction: Direction?): Boolean {
        return blockState.getValue(DockingBlockStates.FACING).opposite != direction
                || tug?.direction == getRowDirection(blockState.getValue(DockingBlockStates.FACING)
        )
    }

    override fun getRowDirection(facing: Direction?): Direction {
        return if (blockState.getValue(DockingBlockStates.INVERTED)) facing!!.clockWise else facing!!.counterClockWise
    }
}
