package com.github.bonndan.humblevehicles.util

import net.minecraft.client.resources.language.I18n
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import java.util.*

 class RouteNode(var name: String?, val x: Int, val y: Int, val z: Int) {

    fun getDisplayName(index: Int): String {
        return if (!this.hasCustomName()) {
            I18n.get("item.humblevehicles.tug_route.node", index)
        } else {
            I18n.get(
                "item.humblevehicles.tug_route.node_named", index,
                name
            )
        }
    }

    fun toBlockPos(): BlockPos {
        return BlockPos(x, y, z)
    }

    fun isAt(pos: BlockPos): Boolean {
        return this.x == pos.x && this.y == pos.y && this.z == pos.z
    }

    fun getDisplayCoords(): String {
        return "$x,$y,$z"
    }

    fun hasCustomName(): Boolean {
        return this.name != null
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as RouteNode
        return that.x.compareTo(x) == 0
                && that.y.compareTo(y) == 0
                && that.z.compareTo(z) == 0
                && name == that.name
    }

    override fun hashCode(): Int {
        return Objects.hash(name, x, y, z)
    }

    fun toNBT(): CompoundTag {
        val tag = CompoundTag()
        if (this.hasCustomName()) {
            tag.putString(NAME_TAG, this.name!!)
        }

        val coords = CompoundTag()
        coords.putInt(X_TAG, x)
        coords.putInt(Y_TAG, y)
        coords.putInt(Z_TAG, z)

        tag.put(COORDS_TAG, coords)
        return tag
    }


    companion object {
        private const val NAME_TAG = "name"
        const val X_TAG = "x"
        const val Y_TAG = "y"
        const val Z_TAG = "z"
        private const val COORDS_TAG = "coordinates"

        fun fromNBT(tag: CompoundTag): RouteNode {
            var name: String? = null
            if (tag.contains(NAME_TAG)) {
                name = tag.getString(NAME_TAG)
            }

            val coords = getCoordsFromNBT(tag)
            return RouteNode(name, coords.x, coords.y, coords.z)
        }

        private fun getCoordsFromNBT(tag: CompoundTag): BlockPos =
            tag.getCompound(COORDS_TAG).let { coords ->
                BlockPos(coords.getInt(X_TAG), coords.getInt(Y_TAG), coords.getInt(Z_TAG))
            }

    }
}
