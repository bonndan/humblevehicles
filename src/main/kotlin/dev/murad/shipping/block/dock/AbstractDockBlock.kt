package dev.murad.shipping.block.dock

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

abstract class AbstractDockBlock(properties: Properties) : Block(properties), EntityBlock {
    @Deprecated("")
    public override fun neighborChanged(
        state: BlockState,
        world: Level,
        p_220069_3_: BlockPos,
        p_220069_4_: Block,
        p_220069_5_: BlockPos,
        p_220069_6_: Boolean
    ) {
        super.neighborChanged(state, world, p_220069_3_, p_220069_4_, p_220069_5_, p_220069_6_)
        DockingBlockStates.fixHopperPos(
            state,
            world,
            p_220069_3_,
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
