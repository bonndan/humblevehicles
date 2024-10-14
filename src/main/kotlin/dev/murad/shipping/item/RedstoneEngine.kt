package dev.murad.shipping.item

import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.RailBlock
import net.minecraft.world.level.block.state.properties.RailShape

class RedstoneEngine(pProperties: Properties) : Item(pProperties) {

    private val wrenchInfo: Component = Component.translatable("item.humblevehicles.redstone_engine.description")

    override fun appendHoverText(
        pStack: ItemStack,
        pContext: TooltipContext,
        pTooltipComponents: MutableList<Component>,
        pTooltipFlag: TooltipFlag
    ) {
        pTooltipComponents.add(wrenchInfo)
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag)
    }
}
