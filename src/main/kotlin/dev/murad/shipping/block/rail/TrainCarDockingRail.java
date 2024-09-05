package dev.murad.shipping.block.rail;

import com.mojang.serialization.MapCodec;
import dev.murad.shipping.block.dock.DockingBlockStates;
import dev.murad.shipping.setup.ModTileEntitiesTypes;
import dev.murad.shipping.util.InteractionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TrainCarDockingRail extends AbstractDockingRail{

    public static final MapCodec<TrainCarDockingRail> CODEC = simpleCodec(TrainCarDockingRail::new);

    public TrainCarDockingRail(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if (InteractionUtil.doConfigure(pPlayer, pHand)) {
            pLevel.setBlockAndUpdate(pPos, pState.setValue(DockingBlockStates.INVERTED, !pState.getValue(DockingBlockStates.INVERTED)));
            fixHopperPos(pState, pLevel, pPos);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(DockingBlockStates.INVERTED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModTileEntitiesTypes.CAR_DOCK.get().create(pPos, pState);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseRailBlock> codec() {
        return CODEC;
    }
}
