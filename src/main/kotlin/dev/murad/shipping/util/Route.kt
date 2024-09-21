package dev.murad.shipping.util

import dev.murad.shipping.item.ItemStackUtil
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.world.item.ItemStack
import java.util.*
import kotlin.reflect.KClass

abstract class Route(
    private val name: String? = null,
    private val owner: String? = null,
    nodes: Set<out RouteNode> = HashSet()
) : ArrayList<RouteNode>(nodes) {
    constructor(nodes: Set<LocoRouteNode>) : this(null, null, nodes)

    override fun add(element: RouteNode): Boolean {
        if (contains(element)) {
            return false
        }
        return super.add(element)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        if (!super.equals(o)) return false
        val that = o as Route
        return name == that.name && owner == that.owner
    }

    override fun hashCode(): Int {
        return Objects.hash(super.hashCode(), name, owner)
    }

    fun toNBT(): CompoundTag {
        val tag = CompoundTag()

        val list = ListTag()
        for (node in this) {
            list.add(node.toNBT())
        }

        tag.put(NODES_TAG, list)
        if (name != null) {
            tag.putString(NAME_TAG, this.name)
        }

        if (owner != null) {
            tag.putString(OWNER_TAG, this.owner)
        }

        return tag
    }

    fun save(itemStack: ItemStack) {

        ItemStackUtil.getOrCreateTag(itemStack).put(ROUTE_NBT, toNBT())
    }

    companion object {

        const val NAME_TAG = "name"
        const val OWNER_TAG = "owner"
        const val NODES_TAG = "nodes"
        const val ROUTE_NBT = "route"
    }

}
