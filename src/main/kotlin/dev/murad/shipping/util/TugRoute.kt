package dev.murad.shipping.util

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import java.util.*

class TugRoute constructor(private val name: String? = null, nodes: List<TugRouteNode> = ArrayList()) :
    ArrayList<TugRouteNode>(nodes) {

    constructor(nodes: List<TugRouteNode>) : this(null, nodes)

    fun hasCustomName(): Boolean {
        return this.name != null
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        if (!super.equals(o)) return false
        val that = o as TugRoute
        return name == that.name
    }

    override fun hashCode(): Int {
        return Objects.hash(super.hashCode(), name)
    }

    fun toNBT(): CompoundTag {
        val tag = CompoundTag()

        val list = ListTag()
        for (node in this) {
            list.add(node.toNBT())
        }

        tag.put(NODES_TAG, list)
        if (hasCustomName()) {
            tag.putString(NAME_TAG, this.name)
        }
        return tag
    }

    companion object {
        private const val NAME_TAG = "name"
        private const val NODES_TAG = "nodes"
        private const val HASH_TAG = "hash" // # :)

        fun fromNBT(tag: CompoundTag): TugRoute {
            var name: String? = null
            if (tag.contains(NAME_TAG)) {
                name = tag.getString(NAME_TAG)
            }

            // 10 == magic number of Compound Tag
            val nodesNBT = tag.getList(NODES_TAG, 10)
            val nodes = ArrayList<TugRouteNode>()
            for (i in nodesNBT.indices) {
                nodes.add(TugRouteNode.fromNBT(nodesNBT.getCompound(i)))
            }

            return TugRoute(name, nodes)
        }
    }
}
