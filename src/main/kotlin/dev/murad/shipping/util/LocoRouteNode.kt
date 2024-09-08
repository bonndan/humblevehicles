package dev.murad.shipping.util

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import java.util.*

/**
 * TODO: Model using schemas to easier serialize/deserialize
 */
class LocoRouteNode(private var name: String?, private val x: Int, private val y: Int, private val z: Int) {
    fun setName(name: String?) {
        this.name = name
    }

    fun hasCustomName(): Boolean {
        return this.name != null
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as LocoRouteNode
        return java.lang.Double.compare(
            that.x.toDouble(),
            x.toDouble()
        ) == 0 && java.lang.Double.compare(
            that.y.toDouble(),
            y.toDouble()
        ) == 0 && java.lang.Double.compare(that.z.toDouble(), z.toDouble()) == 0 && name == that.name
    }

    override fun hashCode(): Int {
        return Objects.hash(name, x, y, z)
    }

    fun toNBT(): CompoundTag {
        val tag = CompoundTag()
        if (this.hasCustomName()) {
            tag.putString(NAME_TAG, this.name)
        }

        val coords = CompoundTag()
        coords.putDouble(X_TAG, x.toDouble())
        coords.putDouble(Y_TAG, y.toDouble())
        coords.putDouble(Z_TAG, z.toDouble())

        tag.put(COORDS_TAG, coords)
        return tag
    }

    fun toBlockPos(): BlockPos {
        return BlockPos(x, y, z)
    }

    fun isAt(pos: BlockPos): Boolean {
        return this.x == pos.x && this.y == pos.y && this.z == pos.z
    }

    companion object {
        private const val NAME_TAG = "name"
        private const val X_TAG = "x"
        private const val Y_TAG = "y"
        private const val Z_TAG = "z"
        private const val COORDS_TAG = "coordinates"

        fun fromNBT(tag: CompoundTag): LocoRouteNode {
            var name: String? = null
            if (tag.contains(NAME_TAG)) {
                name = tag.getString(NAME_TAG)
            }

            val coords = tag.getCompound(COORDS_TAG)
            // Backwards incompatible. Used to be double, but now it is int (BlockPos recently change to int only)
            val x = coords.getInt(X_TAG)
            val y = coords.getInt(Y_TAG)
            val z = coords.getInt(Z_TAG)

            return LocoRouteNode(name, x, y, z)
        }

        @JvmStatic
        fun fromBlocKPos(pos: BlockPos): LocoRouteNode {
            return LocoRouteNode(null, pos.x, pos.y, pos.z)
        }
    }
}
