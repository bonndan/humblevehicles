package dev.murad.shipping.block.rapidhopper

import dev.murad.shipping.setup.ModTileEntitiesTypes
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.HopperBlockEntity
import net.minecraft.world.level.block.state.BlockState

class RapidHopperTileEntity(pWorldPosition: BlockPos, pBlockState: BlockState) :
    HopperBlockEntity(pWorldPosition, pBlockState) {
    private val rapidCooldown = 0

    /*
    public static void pushItemsTick(Level pLevel, BlockPos pPos, BlockState pState, RapidHopperTileEntity pBlockEntity) {
        pBlockEntity.setCooldown(0);
        pBlockEntity.rapidCooldown--;
        if (pBlockEntity.rapidCooldown <= 0) {

            HopperBlockEntity.pushItemsTick(pLevel, pPos, pState, pBlockEntity);
            if (!tryMoveItems(pLevel, pPos, pState, pBlockEntity, () -> suckInItems(pLevel, pBlockEntity))) {
                pBlockEntity.rapidCooldown = SEARCH_COOLDOWN;
            } else {
                pBlockEntity.rapidCooldown = RAPID_COOLDOWN;
            }
        }
    }

     */
    override fun getType(): BlockEntityType<*> {
        return ModTileEntitiesTypes.RAPID_HOPPER.get()
    }

    companion object {
        private const val SEARCH_COOLDOWN = 8
        private const val RAPID_COOLDOWN = 1
    }
}
