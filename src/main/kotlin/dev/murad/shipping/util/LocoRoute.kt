package dev.murad.shipping.util

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import java.util.*

class LocoRoute @JvmOverloads constructor(
    private val name: String? = null,
    private val owner: String? = null,
    nodes: Set<LocoRouteNode> = HashSet()
) :
    HashSet<LocoRouteNode>(nodes) {
    constructor(nodes: Set<LocoRouteNode>) : this(null, null, nodes)

    fun hasCustomName(): Boolean {
        return this.name != null
    }

    fun hasOwner(): Boolean {
        return this.owner != null
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        if (!super.equals(o)) return false
        val that = o as LocoRoute
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
        if (hasCustomName()) {
            tag.putString(NAME_TAG, this.name)
        }

        if (hasOwner()) {
            tag.putString(OWNER_TAG, this.owner)
        }

        return tag
    }

    companion object {
        private const val NAME_TAG = "name"
        private const val OWNER_TAG = "owner"
        private const val NODES_TAG = "nodes"

        @JvmStatic
        fun fromNBT(tag: CompoundTag): LocoRoute {
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
            val nodes = HashSet<LocoRouteNode>()
            for (i in nodesNBT.indices) {
                nodes.add(LocoRouteNode.fromNBT(nodesNBT.getCompound(i)))
            }

            return LocoRoute(name, owner, nodes)
        }
    }
}
