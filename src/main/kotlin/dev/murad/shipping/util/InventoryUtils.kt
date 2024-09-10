package dev.murad.shipping.util

import dev.murad.shipping.setup.ModDataComponents
import net.minecraft.world.Container
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.energy.IEnergyStorage
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.ItemStackHandler
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.MutableList
import kotlin.collections.set
import kotlin.collections.setOf

object InventoryUtils {

    fun mayMoveIntoInventory(target: Container, source: Container): Boolean {
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

    fun isEmpty(itemHandler: ItemStackHandler): Boolean {
        for (i in 0 until itemHandler.slots) {
            if (!itemHandler.getStackInSlot(i).isEmpty && itemHandler.getStackInSlot(i).item != Items.AIR) {
                return false
            }
        }
        return true
    }

    fun moveItemStackIntoHandler(handler: ItemStackHandler, stack: ItemStack): ItemStack {
        var stack = stack
        val slots = handler.slots
        var i = 0
        while (i < slots && !stack.isEmpty) {
            stack = handler.insertItem(i, stack, false)
            i++
        }
        return stack
    }

    fun canMergeItems(stack1: ItemStack, stack2: ItemStack): Boolean {
        return if (stack1.item !== stack2.item) {
            false
        } else if (stack1.damageValue != stack2.damageValue) {
            false
        } else if (stack1.count > stack1.maxStackSize) {
            false
        } else {
            ItemStack.isSameItemSameComponents(stack1, stack2)
        }
    }

    fun getEnergyCapabilityInSlot(slot: Int, handler: ItemStackHandler): IEnergyStorage? {
        val stack = handler.getStackInSlot(slot)
        if (!stack.isEmpty) {
            val energyStorage = Optional.ofNullable<IEnergyStorage>(
                stack.components.get(ModDataComponents.getEnergyStorage().get())
            )
            if (energyStorage.isPresent) {
                return energyStorage.get()
            }
        }
        return null
    }
}
