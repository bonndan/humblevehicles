package com.github.bonndan.humblevehicles.item

import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.RailBlock
import net.minecraft.world.level.block.state.properties.RailShape

class WrenchItem(pProperties: Properties) : Item(pProperties) {

    private val wrenchInfo: Component = Component.translatable("item.humblevehicles.conductors_wrench.description")

    override fun appendHoverText(
        pStack: ItemStack,
        pContext: TooltipContext,
        pTooltipComponents: MutableList<Component>,
        pTooltipFlag: TooltipFlag
    ) {
        pTooltipComponents.add(wrenchInfo)
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag)
    }


    override fun useOn(pContext: UseOnContext): InteractionResult {
        val state = pContext.level.getBlockState(pContext.clickedPos)
        if (state.`is`(Blocks.RAIL)) {
            val shape = state.getValue(RailBlock.SHAPE)
            if (shape.isAscending) {
                return InteractionResult.PASS
            }
            if (!pContext.level.isClientSide()) {
                pContext.level.setBlock(
                    pContext.clickedPos,
                    state.setValue(RailBlock.SHAPE, nextShapes.getOrDefault(shape, shape)), 2
                )
            }
            return InteractionResult.SUCCESS
        } else {
            return super.useOn(pContext)
        }
    }

    companion object {
        private val nextShapes: Map<RailShape, RailShape> = java.util.Map.ofEntries(
            java.util.Map.entry(RailShape.EAST_WEST, RailShape.NORTH_SOUTH),
            java.util.Map.entry(RailShape.NORTH_SOUTH, RailShape.NORTH_EAST),
            java.util.Map.entry(RailShape.NORTH_EAST, RailShape.NORTH_WEST),
            java.util.Map.entry(RailShape.NORTH_WEST, RailShape.SOUTH_WEST),
            java.util.Map.entry(RailShape.SOUTH_WEST, RailShape.SOUTH_EAST),
            java.util.Map.entry(RailShape.SOUTH_EAST, RailShape.EAST_WEST)
        )
    }
}
