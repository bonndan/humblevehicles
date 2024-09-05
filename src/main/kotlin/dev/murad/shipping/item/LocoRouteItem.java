package dev.murad.shipping.item;

import dev.murad.shipping.util.LocoRoute;
import dev.murad.shipping.util.LocoRouteNode;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.List;

public class LocoRouteItem extends Item {

    private static final String ROUTE_NBT = "route";

    public LocoRouteItem(Properties properties) {
        super(properties);
    }

    private boolean removeAndDisplay(@Nullable Player player, LocoRoute route, BlockPos pos) {
        boolean removed = route.removeIf(n -> n.isAt(pos));
        if (removed && player != null)
            player.displayClientMessage(Component.translatable("item.littlelogistics.locomotive_route.removed",
                    pos.getX(), pos.getY(), pos.getZ()), false);
        return removed;
    }

    private void addAndDisplay(@Nullable Player player, LocoRoute route, BlockPos pos, Level level) {
        if (level.getBlockState(pos).getBlock() instanceof BaseRailBlock) {
            // blockpos should be a railtype, either our custom rail or vanilla.
            // Though for pathfinding purposes, it is not guaranteed to be a rail, as the
            // world can change
            if (player != null)
                player.displayClientMessage(Component.translatable("item.littlelogistics.locomotive_route.added",
                        pos.getX(), pos.getY(), pos.getZ()), false);

            // add
            route.add(LocoRouteNode.fromBlocKPos(pos));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel().isClientSide) return InteractionResult.SUCCESS;

        // item used on block
        ItemStack stack = pContext.getItemInHand();
        if (stack.getItem() == this) {
            BlockPos target = pContext.getClickedPos();
            LocoRoute route = getRoute(stack);
            Player player = pContext.getPlayer();

            // target block
            Block targetBlock = pContext.getLevel().getBlockState(target).getBlock();
            boolean shouldCheckAboveOnRemove = !(targetBlock instanceof BaseRailBlock);

            if (!removeAndDisplay(player, route, target) && (!shouldCheckAboveOnRemove || !removeAndDisplay(player, route, target.above()))) {
                addAndDisplay(player, route, target, pContext.getLevel());
            }

            // save route
            saveRoute(stack, route);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    private void saveRoute(ItemStack stack, LocoRoute route) {

        var tag = ItemStackUtil.getCompoundTag(stack);

        if (route.isEmpty()) {
            tag.ifPresent(compoundTag -> compoundTag.remove(ROUTE_NBT));
            return;
        }

        tag.ifPresent(compoundTag -> compoundTag.put(ROUTE_NBT, route.toNBT()));
    }

    public static LocoRoute getRoute(ItemStack stack) {

        return ItemStackUtil.getCompoundTag(stack)
                .map(LocoRoute::fromNBT)
                .orElse(new LocoRoute());

    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> tooltip, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, tooltip, pTooltipFlag);
        tooltip.add(Component.translatable("item.littlelogistics.locomotive_route.description"));
        tooltip.add(
                Component.translatable("item.littlelogistics.locomotive_route.num_nodes", getRoute(pStack).size())
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
    }
}
