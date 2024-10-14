package com.github.bonndan.humblevehicles.util

import com.github.bonndan.humblevehicles.item.ItemStackUtil
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.world.item.ItemStack
import java.util.*

class Route(
    private val name: String? = null,
    private val owner: String? = null,
    nodes: Set<RouteNode> = HashSet()
) : ArrayList<RouteNode>(nodes) {
    constructor(nodes: Set<RouteNode>) : this(null, null, nodes)

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

        fun getRoute(itemStack: ItemStack): Route {

            return ItemStackUtil.getCompoundTag(itemStack)
                ?.let { compoundTag ->
                    return if (compoundTag.contains(ROUTE_NBT, 10))
                        fromNBT(compoundTag.getCompound(ROUTE_NBT))
                    else Route()
                } ?: Route()
        }

         fun fromNBT(tag: CompoundTag): Route {

            var name: String? = null
            var owner: String? = null
            if (tag.contains(NAME_TAG)) {
                name = tag.getString(NAME_TAG)
            }

            if (tag.contains(OWNER_TAG)) {
                owner = tag.getString(OWNER_TAG)
            }

            // 10 == magic number of Compound Tag
            val nodesNBT = tag.getList(NODES_TAG, 10)
            val nodes = HashSet<RouteNode>()
            for (i in nodesNBT.indices) {
                nodes.add(RouteNode.fromNBT(nodesNBT.getCompound(i)))
            }

            return Route(name, owner, nodes)
        }
    }

}
