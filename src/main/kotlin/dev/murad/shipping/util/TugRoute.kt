package dev.murad.shipping.util

import dev.murad.shipping.item.ItemStackUtil
import dev.murad.shipping.item.TugRouteItem
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.world.item.ItemStack
import java.util.*

class TugRoute(
    name: String? = null,
    owner: String? = null,
    nodes: Set<TugRouteNode> = mutableSetOf(),
) : Route(name, owner, nodes) {


    companion object {

        fun fromNBT(tag: CompoundTag): TugRoute {
            var name: String? = null
            if (tag.contains(NAME_TAG)) {
                name = tag.getString(NAME_TAG)
            }

            // 10 == magic number of Compound Tag
            val nodesNBT = tag.getList(NODES_TAG, 10)
            val nodes = mutableSetOf<TugRouteNode>()
            for (i in nodesNBT.indices) {
                nodes.add(TugRouteNode.fromNBT(nodesNBT.getCompound(i)))
            }

            return TugRoute(name, null, nodes)
        }

        fun getRoute(itemStack: ItemStack): TugRoute {

            return ItemStackUtil.getCompoundTag(itemStack)
                ?.let { compoundTag ->
                    return if (compoundTag.contains(ROUTE_NBT, 10))
                        fromNBT(compoundTag.getCompound(ROUTE_NBT))
                    else TugRoute()
                } ?: TugRoute()
        }
    }
}
