package dev.murad.shipping.block.dock

import dev.murad.shipping.entity.custom.vessel.VesselEntity
import dev.murad.shipping.setup.ModTileEntitiesTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState

class BargeDockTileEntity(pos: BlockPos, state: BlockState) :
    AbstractTailDockTileEntity<VesselEntity>(ModTileEntitiesTypes.BARGE_DOCK.get(), pos, state) {

    override val targetBlockPos: List<BlockPos>
        get() = if (isExtract) {
            listOf(
                this.blockPos
                    .below()
                    .relative(blockState.getValue(DockingBlockStates.FACING))
            )
        } else listOf(this.blockPos.above())

    override fun checkBadDirCondition(direction: Direction): Boolean {
        return blockState.getValue(DockingBlockStates.FACING).opposite != direction
    }
}
