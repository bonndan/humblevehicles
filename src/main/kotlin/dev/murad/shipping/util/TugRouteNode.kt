package dev.murad.shipping.util

import net.minecraft.client.resources.language.I18n
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.phys.Vec2
import java.util.*

class TugRouteNode(var name: String?, @JvmField val x: Double, @JvmField val z: Double) {
    constructor(x: Double, y: Double) : this(null, x, y)

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

    val displayCoords: String
        get() = x.toString() + ", " + this.z

    fun hasCustomName(): Boolean {
        return this.name != null
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as TugRouteNode
        return java.lang.Double.compare(that.x, x) == 0 && java.lang.Double.compare(that.z, z) == 0 && name == that.name
    }

    override fun hashCode(): Int {
        return Objects.hash(name, x, z)
    }

    fun toNBT(): CompoundTag {
        val tag = CompoundTag()
        if (this.hasCustomName()) {
            tag.putString(NAME_TAG, this.name!!)
        }

        val coords = CompoundTag()
        coords.putDouble(X_TAG, x)
        coords.putDouble(Z_TAG, z)

        tag.put(COORDS_TAG, coords)
        return tag
    }

    companion object {
        private const val NAME_TAG = "name"
        private const val X_TAG = "x"
        private const val Z_TAG = "z"
        private const val COORDS_TAG = "coordinates"

        fun fromNBT(tag: CompoundTag): TugRouteNode {
            var name: String? = null
            if (tag.contains(NAME_TAG)) {
                name = tag.getString(NAME_TAG)
            }

            val coords = tag.getCompound(COORDS_TAG)
            val x = coords.getDouble(X_TAG)
            val z = coords.getDouble(Z_TAG)

            return TugRouteNode(name, x, z)
        }

        fun fromVector2f(node: Vec2): TugRouteNode {
            val x = node.x.toDouble()
            val z = node.y.toDouble()
            return TugRouteNode(null, x, z)
        }
    }
}
