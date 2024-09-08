package dev.murad.shipping.util

import net.minecraft.world.phys.Vec2
import java.util.*
import java.util.stream.Collectors

object LegacyTugRouteUtil {
    fun convertLegacyRoute(legacyRoute: List<Vec2>): TugRoute {
        return TugRoute(
            legacyRoute.stream().map<TugRouteNode?> { node: Vec2 -> TugRouteNode.Companion.fromVector2f(node) }
                .collect(Collectors.toList<TugRouteNode?>()))
    }

    @JvmStatic
    fun parseLegacyRouteString(route: String): List<Vec2> {
        if (route == "") {
            return ArrayList()
        }

        return Arrays.stream(route.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
            .map { string: String -> string.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray() }
            .map { arr: Array<String> -> Vec2(arr[0].toFloat(), arr[1].toFloat()) }
            .collect(Collectors.toList())
    }
}
