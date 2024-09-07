package dev.murad.shipping

import dev.murad.shipping.entity.container.EnergyHeadVehicleScreen
import dev.murad.shipping.entity.container.FishingBargeScreen
import dev.murad.shipping.entity.container.SteamHeadVehicleScreen
import dev.murad.shipping.item.container.TugRouteScreen
import dev.murad.shipping.setup.ModItemModelProperties
import dev.murad.shipping.setup.ModMenuTypes
import dev.murad.shipping.setup.Registration
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.registries.DeferredRegister
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Mod(ShippingMod.MOD_ID)
class ShippingMod(modBus: IEventBus, container: ModContainer) {

    init {
        LOGGER.info("Starting ShippingMod")
        Registration.register(modBus)

        // Register the doClientStuff method for modloading
        modBus.addListener(FMLClientSetupEvent::class.java) { event: FMLClientSetupEvent -> this.doClientStuff(event) }
        modBus.addListener(RegisterMenuScreensEvent::class.java) { event: RegisterMenuScreensEvent ->
            this.registerScreens(
                event
            )
        }


        container.registerConfig(ModConfig.Type.COMMON, ShippingConfig.Common.SPEC, "littlelogistics-common.toml")
        container.registerConfig(ModConfig.Type.CLIENT, ShippingConfig.Client.SPEC, "littlelogistics-client.toml")
        container.registerConfig(ModConfig.Type.SERVER, ShippingConfig.Server.SPEC, "littlelogistics-server.toml")

        // Register ourselves for server and other game events we are interested in
        modBus.register(this)
    }

    private fun doClientStuff(event: FMLClientSetupEvent) {
        event.enqueueWork { ModItemModelProperties.register() }
    }

    private fun registerScreens(event: RegisterMenuScreensEvent) {

        event.register(ModMenuTypes.TUG_CONTAINER.get(), ::SteamHeadVehicleScreen)
        event.register(ModMenuTypes.STEAM_LOCOMOTIVE_CONTAINER.get(), ::SteamHeadVehicleScreen)
        event.register(ModMenuTypes.ENERGY_TUG_CONTAINER.get(), ::EnergyHeadVehicleScreen)
        event.register(ModMenuTypes.ENERGY_LOCOMOTIVE_CONTAINER.get(), ::EnergyHeadVehicleScreen)
        event.register(ModMenuTypes.FISHING_BARGE_CONTAINER.get(), ::FishingBargeScreen)
        event.register(ModMenuTypes.TUG_ROUTE_CONTAINER.get(), ::TugRouteScreen)
    }

    companion object {
        // The value here should match an entry in the META-INF/mods.toml file
        const val MOD_ID: String = "littlelogistics_neo"

        val CREATIVE_MODE_TABS: DeferredRegister<CreativeModeTab> =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID)

        private val LOGGER: Logger = LoggerFactory.getLogger(ShippingMod::class.java)

        @JvmStatic
        fun entityTexture(suffix: String?): ResourceLocation? {
            return ResourceLocation.tryBuild(MOD_ID, String.format("textures/entity/%s", suffix))
        }
    }
}
