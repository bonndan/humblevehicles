package dev.murad.shipping.item

import dev.murad.shipping.entity.custom.vessel.tug.VehicleFrontPart
import dev.murad.shipping.util.LinkableEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

/*
MIT License

Copyright (c) 2018 Xavier "jglrxavpok" Niochaut

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/


class SpringItem(properties: Properties) : Item(properties) {
    private val springInfo: Component = Component.translatable("item.humblevehicles.spring.description")

    // because 'itemInteractionForEntity' is only for Living entities
    fun onUsedOnEntity(stack: ItemStack, player: Player, world: Level, target: Entity) {
        var target = target
        if (target is VehicleFrontPart) {
            target = target.parent!!
        }
        if (world.isClientSide) return
        when (getState(stack)) {
            State.WAITING_NEXT -> {
                createSpringHelper(stack, player, world, target)
            }

            else -> {
                setDominant(world, stack, target)
            }
        }
    }

    private fun createSpringHelper(stack: ItemStack, player: Player, world: Level, target: Entity) {
        val dominant = getDominant(world, stack) ?: return
        if (dominant === target) {
            player.displayClientMessage(Component.translatable("item.humblevehicles.spring.notToSelf"), true)
        } else if (dominant is LinkableEntity<*>) {
            if (dominant.linkEntities(player, target) && !player.isCreative) stack.shrink(1)
        }
        resetLinked(stack)
    }

    override fun appendHoverText(
        pStack: ItemStack,
        pContext: TooltipContext,
        pTooltipComponents: MutableList<Component>,
        pTooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag)
        pTooltipComponents.add(springInfo)
    }


    private fun setDominant(worldIn: Level, stack: ItemStack, entity: Entity) {
        ItemStackUtil.getCompoundTag(stack)
            .ifPresent { compoundTag: CompoundTag? -> compoundTag!!.putInt(LINKED, entity.id) }
    }

    private fun getDominant(worldIn: Level, stack: ItemStack): Entity? {
        if (ItemStackUtil.contains(stack, LINKED)) {
            val id = ItemStackUtil.getCompoundTag(stack).map { compoundTag: CompoundTag? ->
                compoundTag!!.getInt(
                    LINKED
                )
            }.orElseThrow()
            return worldIn.getEntity(id)
        }
        resetLinked(stack)
        return null
    }

    private fun resetLinked(itemstack: ItemStack) {
        ItemStackUtil.getCompoundTag(itemstack).ifPresent { compoundTag: CompoundTag? ->
            compoundTag!!.remove(
                LINKED
            )
        }
    }

    override fun use(worldIn: Level, playerIn: Player, handIn: InteractionHand): InteractionResultHolder<ItemStack> {
        resetLinked(playerIn.getItemInHand(handIn))
        return super.use(worldIn, playerIn, handIn)
    }

    enum class State {
        WAITING_NEXT,
        READY
    }

    companion object {
        const val LINKED: String = "linked"

        fun getState(stack: ItemStack): State {
            return if (ItemStackUtil.contains(stack, LINKED)) State.WAITING_NEXT else State.READY
        }
    }
}
