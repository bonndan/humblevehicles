package com.github.bonndan.humblevehicles.block.rail

import com.mojang.serialization.MapCodec
import com.github.bonndan.humblevehicles.block.dock.DockingBlockStates
import com.github.bonndan.humblevehicles.setup.ModBlocks
import com.github.bonndan.humblevehicles.setup.ModTileEntitiesTypes
import com.github.bonndan.humblevehicles.setup.Registration
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseRailBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.redstone.Orientation

class LocomotiveDockingRail(pProperties: Properties) : AbstractDockingRail(pProperties) {

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState? {
        return super.getStateForPlacement(pContext)
            ?.setValue(DockingBlockStates.POWERED, pContext.level.hasNeighborSignal(pContext.clickedPos))
            ?.setValue(DockingBlockStates.FACING, pContext.horizontalDirection)
    }

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(pBuilder)
        pBuilder.add(DockingBlockStates.POWERED, DockingBlockStates.FACING)
    }

    override fun codec(): MapCodec<out BaseRailBlock> {
        return CODEC
    }

    override fun neighborChanged(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pBlock: Block,
        pOrientation: Orientation?,
        pIsMoving: Boolean
    ) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pOrientation, pIsMoving)
        if (!pLevel.isClientSide) {
            val flag = pState.getValue(DockingBlockStates.POWERED)
            if (flag != pLevel.hasNeighborSignal(pPos)) {
                pLevel.setBlock(pPos, pState.cycle(DockingBlockStates.POWERED), 2)
            }
        }
    }

    override fun canConnectRedstone(state: BlockState, world: BlockGetter, pos: BlockPos, side: Direction?): Boolean {
        return true
    }

    override fun newBlockEntity(pPos: BlockPos, pState: BlockState): BlockEntity? {
        return ModTileEntitiesTypes.LOCOMOTIVE_DOCK.get().create(pPos, pState)
    }

    companion object {
        val CODEC: MapCodec<LocomotiveDockingRail> = simpleCodec { pProperties -> LocomotiveDockingRail(pProperties) }
    }
}
