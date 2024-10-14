package com.github.bonndan.humblevehicles.block.dock

import com.github.bonndan.humblevehicles.block.IVesselLoader
import com.github.bonndan.humblevehicles.util.LinkableEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.Container
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.HopperBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.items.IItemHandler
import java.util.*

abstract class AbstractDockTileEntity<T>(blockEntityType: BlockEntityType<*>, pos: BlockPos, s: BlockState) :
    BlockEntity(blockEntityType, pos, s) where T : Entity, T : LinkableEntity<T> {

    abstract fun hold(vessel: T, direction: Direction): Boolean

    fun getHopperAt(p: BlockPos): Optional<HopperBlockEntity> {
        val mayBeHopper = level!!.getBlockEntity(p)
        return if (mayBeHopper is HopperBlockEntity) {
            Optional.of(mayBeHopper)
        } else Optional.empty()
    }

    fun getVesselLoader(p: BlockPos): Optional<IVesselLoader> {
        val mayBeHopper = level!!.getBlockEntity(p)
        return if (mayBeHopper is IVesselLoader) {
            Optional.of(mayBeHopper)
        } else Optional.empty()
    }

    protected abstract fun getTargetBlockPos(): List<BlockPos>

    protected fun mayMoveIntoInventory(target: Container, source: Container): Boolean {
        if (source.isEmpty) {
            return false
        }

        val map = HashMap<Item, MutableList<ItemStack>>()
        val airList: MutableList<Int> = ArrayList()
        for (i in 0 until target.containerSize) {
            val stack = target.getItem(i)
            if ((stack.isEmpty || stack.item == Items.AIR) && target.canPlaceItem(i, stack)) {
                airList.add(i)
            } else if (stack.maxStackSize != stack.count && target.canPlaceItem(i, stack)) {
                if (map.containsKey(stack.item)) {
                    map[stack.item]!!.add(stack)
                } else {
                    map[stack.item] = ArrayList(setOf(stack))
                }
            }
        }

        for (i in 0 until source.containerSize) {
            val stack = source.getItem(i)
            if (!stack.isEmpty && map.containsKey(stack.item)) {
                for (targetStack in map[stack.item]!!) {
                    if (canMergeItems(targetStack, stack)) return true
                }
            } else if (!airList.isEmpty() && target is Entity) {
                val e = target as Entity
                val validSlot = Optional.ofNullable(e.getCapability(Capabilities.ItemHandler.ENTITY))
                    .map { itemHandler: IItemHandler ->
                        airList.stream()
                            .map { j: Int? -> itemHandler.isItemValid(j!!, stack) }
                            .reduce(false) { a: Boolean, b: Boolean -> java.lang.Boolean.logicalOr(a, b) }
                    }.orElse(true)
                if (validSlot) {
                    return true
                }
            } else if (!airList.isEmpty()) {
                return true
            }
        }
        return false
    }

    private fun canMergeItems(stack1: ItemStack, stack2: ItemStack): Boolean =
        when {
            stack1.item !== stack2.item -> false
            stack1.damageValue != stack2.damageValue -> false
            stack1.count > stack1.maxStackSize -> false
            else -> ItemStack.isSameItemSameComponents(stack1, stack2)
        }
}
