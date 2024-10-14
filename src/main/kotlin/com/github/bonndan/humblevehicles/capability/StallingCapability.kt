package com.github.bonndan.humblevehicles.capability

import com.github.bonndan.humblevehicles.HumVeeMod
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.capabilities.EntityCapability


interface StallingCapability {

    fun isDocked(): Boolean
    fun dock(x: Double, y: Double, z: Double)
    fun undock()

    fun isStalled(): Boolean
    fun stall()
    fun unstall()

    fun isFrozen(): Boolean
    fun freeze()
    fun unfreeze()

    companion object {

        val STALLING_CAPABILITY: EntityCapability<StallingCapability, Void?> = EntityCapability.createVoid(
            ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "stalling_capability"),
            StallingCapability::class.java
        )
    }
}
