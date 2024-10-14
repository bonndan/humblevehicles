package com.github.bonndan.humblevehicles.block.rail

import com.mojang.serialization.MapCodec
import com.github.bonndan.humblevehicles.block.dock.DockingBlockStates
import com.github.bonndan.humblevehicles.setup.ModTileEntitiesTypes
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
        state: BlockState,
        world: Level,
        pos: BlockPos,
        p_220069_4_: Block,
        p_220069_5_: BlockPos,
        p_220069_6_: Boolean
    ) {
        super.neighborChanged(state, world, pos, p_220069_4_, p_220069_5_, p_220069_6_)
        if (!world.isClientSide) {
            val flag = state.getValue(DockingBlockStates.POWERED)
            if (flag != world.hasNeighborSignal(pos)) {
                world.setBlock(pos, state.cycle(DockingBlockStates.POWERED), 2)
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
