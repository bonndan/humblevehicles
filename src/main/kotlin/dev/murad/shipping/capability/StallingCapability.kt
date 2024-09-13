package dev.murad.shipping.capability

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
            ResourceLocation.parse("item_handler"),  //"create"
            StallingCapability::class.java
        )
    }
}
