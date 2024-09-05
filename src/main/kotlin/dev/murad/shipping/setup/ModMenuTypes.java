package dev.murad.shipping.setup;

import dev.murad.shipping.entity.accessor.EnergyHeadVehicleDataAccessor;
import dev.murad.shipping.entity.accessor.SteamHeadVehicleDataAccessor;
import dev.murad.shipping.entity.accessor.TugRouteScreenDataAccessor;
import dev.murad.shipping.entity.container.EnergyHeadVehicleContainer;
import dev.murad.shipping.entity.container.FishingBargeContainer;
import dev.murad.shipping.entity.container.SteamHeadVehicleContainer;
import dev.murad.shipping.entity.custom.train.locomotive.EnergyLocomotiveEntity;
import dev.murad.shipping.entity.custom.train.locomotive.SteamLocomotiveEntity;
import dev.murad.shipping.entity.custom.vessel.tug.EnergyTugEntity;
import dev.murad.shipping.entity.custom.vessel.tug.SteamTugEntity;
import dev.murad.shipping.item.container.TugRouteContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

import java.util.function.Supplier;


public class ModMenuTypes {

    private static SimpleContainerData makeIntArray(FriendlyByteBuf buffer) {
        int size = (buffer.readableBytes() + 1) / 4;
        SimpleContainerData arr = new SimpleContainerData(size);
        for (int i = 0; i < size; i++) {
            arr.set(i, buffer.readInt());
        }
        return arr;
    }

    public static final Supplier<MenuType<SteamHeadVehicleContainer<SteamTugEntity>>> TUG_CONTAINER =
            Registration.CONTAINERS.register("tug_container",
                    () -> IMenuTypeExtension.create(
                            (windowId, inv, data) ->
                                    new SteamHeadVehicleContainer<>(windowId, inv.player.level(), new SteamHeadVehicleDataAccessor(makeIntArray(data)), inv, inv.player)));

    public static final Supplier<MenuType<EnergyHeadVehicleContainer<EnergyTugEntity>>> ENERGY_TUG_CONTAINER =
            Registration.CONTAINERS.register("energy_tug_container",
                    () -> IMenuTypeExtension.create(
                            (windowId, inv, data) ->
                                    new EnergyHeadVehicleContainer<>(windowId, inv.player.level(), new EnergyHeadVehicleDataAccessor(makeIntArray(data)), inv, inv.player)));

    public static final Supplier<MenuType<SteamHeadVehicleContainer<SteamLocomotiveEntity>>> STEAM_LOCOMOTIVE_CONTAINER =
            Registration.CONTAINERS.register("steam_locomotive_container",
                    () -> IMenuTypeExtension.create(
                            (windowId, inv, data) ->
                                    new SteamHeadVehicleContainer<>(windowId, inv.player.level(), new SteamHeadVehicleDataAccessor(makeIntArray(data)), inv, inv.player)));

    public static final Supplier<MenuType<EnergyHeadVehicleContainer<EnergyLocomotiveEntity>>> ENERGY_LOCOMOTIVE_CONTAINER =
            Registration.CONTAINERS.register("energy_locomotive_container",
                    () -> IMenuTypeExtension.create(
                            (windowId, inv, data) ->
                                    new EnergyHeadVehicleContainer<>(windowId, inv.player.level(), new EnergyHeadVehicleDataAccessor(makeIntArray(data)), inv, inv.player)));

    public static final Supplier<MenuType<FishingBargeContainer>> FISHING_BARGE_CONTAINER =
            Registration.CONTAINERS.register("fishing_barge_container",
                    () -> IMenuTypeExtension.create(
                            (windowId, inv, data) ->
                                    new FishingBargeContainer(windowId, inv.player.level(), data.readInt(), inv, inv.player)));

    public static final Supplier<MenuType<TugRouteContainer>> TUG_ROUTE_CONTAINER =
            Registration.CONTAINERS.register("tug_route_container",
                    () -> IMenuTypeExtension.create(
                            (windowId, inv, data) ->
                                    new TugRouteContainer(windowId, inv.player.level(), new TugRouteScreenDataAccessor(makeIntArray(data)), inv, inv.player)));




    public static void register () {}
}
