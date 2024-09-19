package dev.murad.shipping.util

import net.minecraft.nbt.CompoundTag

/**
 * TODO: Model using schemas to easier serialize/deserialize
 */
class LocoRouteNode(name: String?, x: Int, y: Int, z: Int) : RouteNode(name, x, y, z) {


    companion object {
        private const val NAME_TAG = "name"

        fun fromNBT(tag: CompoundTag): LocoRouteNode {
            var name: String? = null
            if (tag.contains(NAME_TAG)) {
                name = tag.getString(NAME_TAG)
            }

            val coords = getCoordsFromNBT(tag)
            return LocoRouteNode(name, coords.x, coords.y, coords.z)
        }

    }
}
