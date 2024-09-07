package dev.murad.shipping.block.rapidhopper

import dev.murad.shipping.setup.ModTileEntitiesTypes
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.HopperBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.HopperBlockEntity
import net.minecraft.world.level.block.state.BlockState

class RapidHopperBlock(p_54039_: Properties) : HopperBlock(p_54039_) {
    override fun newBlockEntity(pPos: BlockPos, pState: BlockState): BlockEntity? {
        return RapidHopperTileEntity(pPos, pState)
    }

    override fun <T : BlockEntity?> getTicker(
        pLevel: Level,
        pState: BlockState,
        pBlockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (pLevel.isClientSide) null else createTickerHelper(
            pBlockEntityType, ModTileEntitiesTypes.RAPID_HOPPER.get()
        ) { pLevel: Level?, pPos: BlockPos?, pState: BlockState?, pBlockEntity: RapidHopperTileEntity? ->
            HopperBlockEntity.pushItemsTick(
                pLevel,
                pPos,
                pState,
                pBlockEntity
            )
        }
    }
}
