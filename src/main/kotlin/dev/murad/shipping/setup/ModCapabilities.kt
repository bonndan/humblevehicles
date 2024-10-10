package dev.murad.shipping.setup

import dev.murad.shipping.capability.StallingCapability.Companion.STALLING_CAPABILITY
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.fluids.CauldronFluidContent

object ModCapabilities {

    fun register(eventBus: IEventBus) {
        eventBus.addListener { event: RegisterCapabilitiesEvent -> CauldronFluidContent.registerCapabilities(event) }

        eventBus.addListener { event: RegisterCapabilitiesEvent ->
            event.registerEntity(STALLING_CAPABILITY, ModEntityTypes.STEAM_LOCOMOTIVE.get())
            { entity, _ -> entity.getStalling() }
        }

        eventBus.addListener { event: RegisterCapabilitiesEvent ->
            event.registerEntity(STALLING_CAPABILITY, ModEntityTypes.ENERGY_LOCOMOTIVE.get())
            { entity, _ -> entity.getStalling() }
        }

        eventBus.addListener { event: RegisterCapabilitiesEvent ->
            event.registerEntity(STALLING_CAPABILITY, ModEntityTypes.STEAM_TUG.get())
            { entity, _ -> entity.getStalling() }
        }
        eventBus.addListener { event: RegisterCapabilitiesEvent ->
            event.registerEntity(STALLING_CAPABILITY, ModEntityTypes.ENERGY_TUG.get())
            { entity, _ -> entity.getStalling() }
        }
    }
}