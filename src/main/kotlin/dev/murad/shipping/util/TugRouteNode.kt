package dev.murad.shipping.util

import net.minecraft.nbt.CompoundTag
import java.util.*

class TugRouteNode(name: String?, x: Int, y: Int, z: Int) : RouteNode(name, x, y, z) {
    constructor(x: Int, y: Int, z: Int) : this(null, x, y, z)

    companion object {
        private const val NAME_TAG = "name"

        fun fromNBT(tag: CompoundTag): TugRouteNode {
            var name: String? = null
            if (tag.contains(NAME_TAG)) {
                name = tag.getString(NAME_TAG)
            }

            val coords = getCoordsFromNBT(tag)
            return TugRouteNode(name, coords.x, coords.y, coords.z)
        }

    }
}
