package com.github.bonndan.humblevehicles.block.rail

import com.mojang.serialization.MapCodec
import com.github.bonndan.humblevehicles.block.dock.DockingBlockStates
import com.github.bonndan.humblevehicles.setup.ModTileEntitiesTypes
import com.github.bonndan.humblevehicles.util.InteractionUtil
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseRailBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.BlockHitResult

class TrainCarDockingRail(pProperties: Properties) : AbstractDockingRail(pProperties) {

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
            fixHopperPos(pState, pLevel, pPos)
            return ItemInteractionResult.SUCCESS
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
    }

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(pBuilder)
        pBuilder.add(DockingBlockStates.INVERTED)
    }

    override fun newBlockEntity(pPos: BlockPos, pState: BlockState): BlockEntity? {
        return ModTileEntitiesTypes.CAR_DOCK.get().create(pPos, pState)
    }

    override fun codec(): MapCodec<out BaseRailBlock> {
        return CODEC
    }

    companion object {
        val CODEC: MapCodec<TrainCarDockingRail> = simpleCodec { pProperties -> TrainCarDockingRail(pProperties) }
    }
}
