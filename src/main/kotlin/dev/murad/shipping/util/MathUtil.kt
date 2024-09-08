package dev.murad.shipping.util

import com.mojang.datafixers.util.Pair
import net.minecraft.util.Mth
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

object MathUtil {
    fun getEdges(bb: AABB): List<Pair<Vec3, Vec3>> {
        val edges: MutableList<Pair<Vec3, Vec3>> = ArrayList()
        val corners = getCorners(bb)

        // minY plane
        edges.add(Pair(corners[0], corners[1]))
        edges.add(Pair(corners[0], corners[2]))
        edges.add(Pair(corners[3], corners[1]))
        edges.add(Pair(corners[3], corners[2]))

        // maxY plane
        edges.add(Pair(corners[4], corners[5]))
        edges.add(Pair(corners[4], corners[6]))
        edges.add(Pair(corners[7], corners[5]))
        edges.add(Pair(corners[7], corners[6]))

        // vertical edges
        edges.add(Pair(corners[0], corners[4]))
        edges.add(Pair(corners[1], corners[5]))
        edges.add(Pair(corners[2], corners[6]))
        edges.add(Pair(corners[3], corners[7]))

        return edges
    }

    // returns a list of corners in a set order, but I can't be bothered to write
    // out the order here.
    fun getCorners(bb: AABB): List<Vec3> {
        val corners: MutableList<Vec3> = ArrayList()
        corners.add(Vec3(bb.minX, bb.minY, bb.minZ)) // 000
        corners.add(Vec3(bb.minX, bb.minY, bb.maxZ)) // 001
        corners.add(Vec3(bb.minX, bb.maxY, bb.minZ)) // 010
        corners.add(Vec3(bb.minX, bb.maxY, bb.maxZ)) // 011
        corners.add(Vec3(bb.maxX, bb.minY, bb.minZ)) // 100
        corners.add(Vec3(bb.maxX, bb.minY, bb.maxZ)) // 101
        corners.add(Vec3(bb.maxX, bb.maxY, bb.minZ)) // 110
        corners.add(Vec3(bb.maxX, bb.maxY, bb.maxZ)) // 111
        return corners
    }

    fun lerp(from: Vec3, to: Vec3, ratio: Double): Vec3 {
        return Vec3(Mth.lerp(ratio, from.x, to.x), Mth.lerp(ratio, from.y, to.y), Mth.lerp(ratio, from.z, to.z))
    }
}
