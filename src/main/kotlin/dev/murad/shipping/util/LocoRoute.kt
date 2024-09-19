package dev.murad.shipping.util

import dev.murad.shipping.item.ItemStackUtil
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.world.item.ItemStack
import java.util.*

class LocoRoute(
    name: String? = null,
    owner: String? = null,
    nodes: Set<LocoRouteNode> = HashSet()
) : Route(name, owner, nodes) {

    companion object {

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

        fun getRoute(itemStack: ItemStack): LocoRoute {

            return ItemStackUtil.getCompoundTag(itemStack)
                ?.let { compoundTag ->
                    return if (compoundTag.contains(ROUTE_NBT, 10))
                        fromNBT(compoundTag.getCompound(ROUTE_NBT))
                    else LocoRoute()
                } ?: LocoRoute()
        }
    }
}
