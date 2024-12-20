package com.github.bonndan.humblevehicles

import com.github.bonndan.humblevehicles.entity.container.EnergyHeadVehicleScreen
import com.github.bonndan.humblevehicles.entity.container.SteamHeadVehicleScreen
import com.github.bonndan.humblevehicles.item.container.RouteScreen
import com.github.bonndan.humblevehicles.setup.ModItemModelProperties
import com.github.bonndan.humblevehicles.setup.ModMenuTypes
import com.github.bonndan.humblevehicles.setup.Registration
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent

@Mod(HumVeeMod.MOD_ID)
class HumVeeMod(modBus: IEventBus, container: ModContainer) {

    init {
        Registration.register(modBus)

        // Register the doClientStuff method for modloading
        modBus.addListener(FMLClientSetupEvent::class.java) { event: FMLClientSetupEvent -> this.doClientStuff(event) }
        modBus.addListener(RegisterMenuScreensEvent::class.java) { event: RegisterMenuScreensEvent ->
            this.registerScreens(
                event
            )
        }

        container.registerConfig(ModConfig.Type.COMMON, ShippingConfig.Common.SPEC, "humblevehicles-common.toml")
        container.registerConfig(ModConfig.Type.CLIENT, ShippingConfig.Client.SPEC, "humblevehicles-client.toml")
        container.registerConfig(ModConfig.Type.SERVER, ShippingConfig.Server.SPEC, "humblevehicles-server.toml")
    }

    private fun doClientStuff(event: FMLClientSetupEvent) {
        event.enqueueWork { ModItemModelProperties.register() }
    }

    private fun registerScreens(event: RegisterMenuScreensEvent) {

        event.register(ModMenuTypes.STEAM_LOCOMOTIVE_CONTAINER.get(), ::SteamHeadVehicleScreen)
        event.register(ModMenuTypes.ENERGY_HEAD_CONTAINER.get(), ::EnergyHeadVehicleScreen)
        event.register(ModMenuTypes.TUG_ROUTE_CONTAINER.get(), ::RouteScreen)
    }

    companion object {
        // The value here should match an entry in the META-INF/mods.toml file
        const val MOD_ID: String = "humblevehicles"
    }
}
