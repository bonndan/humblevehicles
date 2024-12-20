package com.github.bonndan.humblevehicles.block.dock

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.redstone.Orientation

abstract class AbstractDockBlock(properties: Properties) : Block(properties), EntityBlock {

    override fun neighborChanged(
        state: BlockState,
        world: Level,
        p_60511_: BlockPos,
        p_60512_: Block,
        p_365159_: Orientation?,
        p_60514_: Boolean
    ) {
        super.neighborChanged(state, world, p_60511_, p_60512_, p_365159_, p_60514_)
        DockingBlockStates.fixHopperPos(
            world,
            p_60511_,
            Direction.UP,
            state.getValue(DockingBlockStates.FACING)
        )
    }

    @Suppress("deprecation")
    public override fun rotate(state: BlockState, rot: Rotation): BlockState {
        return state.setValue(DockingBlockStates.FACING, rot.rotate(state.getValue(DockingBlockStates.FACING)))
    }

    @Suppress("deprecation")
    public override fun mirror(state: BlockState, mirrorIn: Mirror): BlockState {
        return state.rotate(mirrorIn.getRotation(state.getValue(DockingBlockStates.FACING)))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(DockingBlockStates.FACING)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return defaultBlockState()
            .setValue(DockingBlockStates.FACING, context.horizontalDirection.opposite)
    }
}
