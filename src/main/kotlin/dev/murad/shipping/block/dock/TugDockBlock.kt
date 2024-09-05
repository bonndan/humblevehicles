package dev.murad.shipping.block.dock

import dev.murad.shipping.setup.ModBlocks
import dev.murad.shipping.setup.ModTileEntitiesTypes
import dev.murad.shipping.util.InteractionUtil
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.BlockHitResult

class TugDockBlock(properties: Properties?) : AbstractDockBlock(properties!!) {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return ModTileEntitiesTypes.TUG_DOCK.get().create(pos, state)
    }

    override fun useItemOn(
        pStack: ItemStack,
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pPlayer: Player,
        pHand: InteractionHand,
        pHitResult: BlockHitResult
    ): ItemInteractionResult {
        if (InteractionUtil.doConfigure(pPlayer, pHand)) {
            pLevel.setBlockAndUpdate(
                pPos,
                pState.setValue(DockingBlockStates.INVERTED, !pState.getValue(DockingBlockStates.INVERTED))
            )
            return ItemInteractionResult.SUCCESS
        }

        return super.useItemOn(pStack, pState, pLevel, pPos, pPlayer, pHand, pHitResult)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return super.getStateForPlacement(context)
            ?.setValue(DockingBlockStates.INVERTED, false)
            ?.setValue(DockingBlockStates.POWERED, context.level.hasNeighborSignal(context.clickedPos))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(DockingBlockStates.INVERTED, DockingBlockStates.POWERED)
    }

    @Suppress("deprecation")
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
            adjustInverted(state, world, pos)
        }
    }

    private fun adjustInverted(state: BlockState, level: Level, pos: BlockPos) {
        val facing = state.getValue(DockingBlockStates.FACING)
        val dockdir = if (state.getValue(DockingBlockStates.INVERTED)) facing.counterClockWise else facing.clockWise
        val tarpos = pos.relative(dockdir)
        if (level.getBlockState(tarpos).`is`(ModBlocks.BARGE_DOCK.get())) {
            level.setBlock(
                pos,
                state.setValue(DockingBlockStates.INVERTED, !state.getValue(DockingBlockStates.INVERTED)),
                2
            )
        }
    }


    override fun canConnectRedstone(state: BlockState, world: BlockGetter, pos: BlockPos, side: Direction?): Boolean {
        return true
    }
}
