package dev.murad.shipping;

import dev.murad.shipping.entity.container.EnergyHeadVehicleScreen;
import dev.murad.shipping.entity.container.FishingBargeScreen;
import dev.murad.shipping.entity.container.SteamHeadVehicleScreen;
import dev.murad.shipping.entity.custom.train.locomotive.EnergyLocomotiveEntity;
import dev.murad.shipping.entity.custom.train.locomotive.SteamLocomotiveEntity;
import dev.murad.shipping.entity.custom.vessel.tug.EnergyTugEntity;
import dev.murad.shipping.entity.custom.vessel.tug.SteamTugEntity;
import dev.murad.shipping.item.container.TugRouteScreen;
import dev.murad.shipping.setup.ModItemModelProperties;
import dev.murad.shipping.setup.ModMenuTypes;
import dev.murad.shipping.setup.Registration;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;

import static net.neoforged.neoforge.common.NeoForge.EVENT_BUS;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(ShippingMod.MOD_ID)
public class ShippingMod {

    public static final String MOD_ID = "assets/littlelogistics";
    // Directly reference a log4j logger.

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public ShippingMod() {
        Registration.register(NeoForge.EVENT_BUS);

        // Register the doClientStuff method for modloading
        NeoForge.EVENT_BUS.addListener(this::doClientStuff);

        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, ShippingConfig.Common.SPEC, "littlelogistics-common.toml");
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.CLIENT, ShippingConfig.Client.SPEC, "littlelogistics-client.toml");
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.SERVER, ShippingConfig.Server.SPEC, "littlelogistics-server.toml");

        // Register ourselves for server and other game events we are interested in
        EVENT_BUS.register(this);


    }

    private void doClientStuff(final FMLClientSetupEvent event) {

        EVENT_BUS.addListener(RegisterMenuScreensEvent.class, this::registerScreens);
        event.enqueueWork(ModItemModelProperties::register);
    }

    private void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.TUG_CONTAINER.get(), SteamHeadVehicleScreen<SteamTugEntity>::new);
        event.register(ModMenuTypes.STEAM_LOCOMOTIVE_CONTAINER.get(), SteamHeadVehicleScreen<SteamLocomotiveEntity>::new);
        event.register(ModMenuTypes.ENERGY_TUG_CONTAINER.get(), EnergyHeadVehicleScreen<EnergyTugEntity>::new);
        event.register(ModMenuTypes.ENERGY_LOCOMOTIVE_CONTAINER.get(), EnergyHeadVehicleScreen<EnergyLocomotiveEntity>::new);
        event.register(ModMenuTypes.FISHING_BARGE_CONTAINER.get(), FishingBargeScreen::new);
        event.register(ModMenuTypes.TUG_ROUTE_CONTAINER.get(), TugRouteScreen::new);
    }

    public static ResourceLocation entityTexture(String suffix) {
        return ResourceLocation.tryBuild(ShippingMod.MOD_ID, String.format("textures/entity/%s", suffix));
    }
}
