package dev.murad.shipping.block.dock

import dev.murad.shipping.block.dock.DockingBlockStates.fixHopperPos
import dev.murad.shipping.setup.ModTileEntitiesTypes
import dev.murad.shipping.util.InteractionUtil
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.BlockHitResult

class BargeDockBlock(p_i48440_1_: Properties) : AbstractDockBlock(p_i48440_1_) {

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
            fixHopperPos(pLevel, pPos, Direction.UP, pState.getValue(DockingBlockStates.FACING))
            return ItemInteractionResult.SUCCESS
        }

        return super.useItemOn(pStack, pState, pLevel, pPos, pPlayer, pHand, pHitResult)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return ModTileEntitiesTypes.BARGE_DOCK.get().create(pos, state)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(DockingBlockStates.INVERTED)
    }
}
