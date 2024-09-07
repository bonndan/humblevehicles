package dev.murad.shipping.block.rail.blockentity

import dev.murad.shipping.block.dock.AbstractTailDockTileEntity
import dev.murad.shipping.block.rail.AbstractDockingRail
import dev.murad.shipping.entity.custom.train.AbstractTrainCarEntity
import dev.murad.shipping.setup.ModTileEntitiesTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.RailShape

class TrainCarDockTileEntity(pos: BlockPos, state: BlockState) :
    AbstractTailDockTileEntity<AbstractTrainCarEntity>(ModTileEntitiesTypes.CAR_DOCK.get(), pos, state) {

    override val targetBlockPos: List<BlockPos>
        get() {
            if (this.isExtract) {
                return listOf(blockPos.below())
            }
            val facing =
                if (blockState.getValue(AbstractDockingRail.RAIL_SHAPE) == RailShape.EAST_WEST) Direction.EAST else Direction.NORTH
            return listOf(
                blockPos.relative(facing.counterClockWise),
                blockPos.relative(facing.clockWise)
            )
        }

    override fun checkBadDirCondition(direction: Direction): Boolean {
        return false
    }
}
