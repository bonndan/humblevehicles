package dev.murad.shipping.entity.custom

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.items.ItemStackHandler
import java.util.*

interface HeadVehicle {

    fun setEngineOn(state: Boolean)

    fun getRouteItemHandler(): ItemStackHandler

    fun isValid(pPlayer: Player): Boolean

    fun hasOwner(): Boolean

    fun getRouteIcon(): ResourceLocation

    fun enroll(uuid: UUID)

    fun owner(): String?
}
