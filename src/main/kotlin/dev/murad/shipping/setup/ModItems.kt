package dev.murad.shipping.setup;

import dev.murad.shipping.ShippingMod;
import dev.murad.shipping.entity.custom.train.locomotive.EnergyLocomotiveEntity;
import dev.murad.shipping.entity.custom.train.locomotive.SteamLocomotiveEntity;
import dev.murad.shipping.entity.custom.train.wagon.ChestCarEntity;
import dev.murad.shipping.entity.custom.train.wagon.FluidTankCarEntity;
import dev.murad.shipping.entity.custom.train.wagon.SeaterCarEntity;
import dev.murad.shipping.entity.custom.vessel.barge.*;
import dev.murad.shipping.entity.custom.vessel.tug.EnergyTugEntity;
import dev.murad.shipping.entity.custom.vessel.tug.SteamTugEntity;
import dev.murad.shipping.item.*;
import dev.murad.shipping.util.MultiMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModItems {

    private static final MultiMap<ResourceKey<CreativeModeTab>, Supplier<? extends Item>> PRIVATE_TAB_REGISTRY = new MultiMap<>();

    /**
     *  Empty Icons
     */

    public static final ResourceLocation LOCO_ROUTE_ICON = ResourceLocation.tryBuild(ShippingMod.MOD_ID, "item/empty_loco_route");
    public static final ResourceLocation TUG_ROUTE_ICON = ResourceLocation.tryBuild(ShippingMod.MOD_ID, "item/empty_tug_route");
    public static final ResourceLocation EMPTY_ENERGY = ResourceLocation.tryBuild(ShippingMod.MOD_ID, "item/empty_energy");


    /**
     * COMMON
     */
    public static final Supplier<Item> CONDUCTORS_WRENCH = register("conductors_wrench",
            () -> new WrenchItem(defaultItemProperties(1)), List.of(CreativeModeTabs.TOOLS_AND_UTILITIES));


    public static final Supplier<Item> SPRING = register("spring",
            () -> new SpringItem(defaultItemProperties(64)), List.of(CreativeModeTabs.TOOLS_AND_UTILITIES));

    /**
     * Vessels
     */

    public static final Supplier<Item> CHEST_BARGE = register("barge",
            () -> new VesselItem(
                    new Item.Properties(),
                    (level, x, y, z) -> new ChestBargeEntity(ModEntityTypes.CHEST_BARGE.get(), level, x, y, z)),
            List.of(CreativeModeTabs.TOOLS_AND_UTILITIES));

    public static final Supplier<Item> BARREL_BARGE = register("barrel_barge",
            () -> new VesselItem(
                    new Item.Properties(),
                    (level, x, y, z) -> new ChestBargeEntity(ModEntityTypes.BARREL_BARGE.get(), level, x, y, z)),
            List.of(CreativeModeTabs.TOOLS_AND_UTILITIES));

//    public static final Supplier<Item> CHUNK_LOADER_BARGE = register("chunk_loader_barge",
//            () -> new VesselItem(new Item.Properties(), ChunkLoaderBargeEntity::new), ImmutableList.of(CreativeModeTabs.TOOLS_AND_UTILITIES));

    public static final Supplier<Item> FISHING_BARGE = register("fishing_barge",
            () -> new VesselItem(new Item.Properties(), FishingBargeEntity::new), List.of(CreativeModeTabs.TOOLS_AND_UTILITIES));

    public static final Supplier<Item> FLUID_BARGE = register("fluid_barge",
            () -> new VesselItem(new Item.Properties(), FluidTankBargeEntity::new), List.of(CreativeModeTabs.TOOLS_AND_UTILITIES));

    public static final Supplier<Item> SEATER_BARGE = register("seater_barge",
            () -> new VesselItem(new Item.Properties(), SeaterBargeEntity::new), List.of(CreativeModeTabs.TOOLS_AND_UTILITIES));

    public static final Supplier<Item> VACUUM_BARGE = register("vacuum_barge",
            () -> new VesselItem(new Item.Properties(), VacuumBargeEntity::new), List.of(CreativeModeTabs.TOOLS_AND_UTILITIES));

    public static final Supplier<Item> STEAM_TUG = register("tug",
            () -> new VesselItem(new Item.Properties(), SteamTugEntity::new), List.of(CreativeModeTabs.TOOLS_AND_UTILITIES));

    public static final Supplier<Item> ENERGY_TUG = register("energy_tug",
            () -> new VesselItem(new Item.Properties(), EnergyTugEntity::new), List.of(CreativeModeTabs.TOOLS_AND_UTILITIES));

    /**
     * Trains
     */

    public static final Supplier<Item> TUG_ROUTE = register("tug_route",
            () -> new TugRouteItem(defaultItemPropertiesWithTag(16)), List.of(CreativeModeTabs.TOOLS_AND_UTILITIES));

    public static final Supplier<Item> CHEST_CAR = register("chest_car",
            () -> new TrainCarItem((level, x, y, z) ->
                    new ChestCarEntity(ModEntityTypes.CHEST_CAR.get(), level, x, y, z),
                    defaultItemProperties(64)),
            List.of(CreativeModeTabs.TOOLS_AND_UTILITIES));

    public static final Supplier<Item> BARREL_CAR = register("barrel_car",
            () -> new TrainCarItem((level, x, y, z) ->
                    new ChestCarEntity(ModEntityTypes.BARREL_CAR.get(), level, x, y, z),
                    defaultItemProperties(64)),
            List.of(CreativeModeTabs.TOOLS_AND_UTILITIES));

    public static final Supplier<Item> FLUID_CAR = register("fluid_car",
            () -> new TrainCarItem(FluidTankCarEntity::new, defaultItemProperties(64)), List.of(CreativeModeTabs.TOOLS_AND_UTILITIES));

    public static final Supplier<Item> SEATER_CAR = register("seater_car",
            () -> new TrainCarItem(SeaterCarEntity::new, defaultItemProperties(64)), List.of(CreativeModeTabs.TOOLS_AND_UTILITIES));

    public static final Supplier<Item> STEAM_LOCOMOTIVE = register("steam_locomotive",
            () -> new TrainCarItem(SteamLocomotiveEntity::new, defaultItemProperties(64)), List.of(CreativeModeTabs.TOOLS_AND_UTILITIES));

    public static final Supplier<Item> ENERGY_LOCOMOTIVE = register("energy_locomotive",
            () -> new TrainCarItem(EnergyLocomotiveEntity::new, defaultItemProperties(64)), List.of(CreativeModeTabs.TOOLS_AND_UTILITIES));

    public static final Supplier<Item> RECEIVER_COMPONENT = register("receiver_component",
            () -> new Item(defaultItemProperties(64)), List.of(CreativeModeTabs.TOOLS_AND_UTILITIES));

    public static final Supplier<Item> TRANSMITTER_COMPONENT = register("transmitter_component",
            () -> new Item(defaultItemProperties(64)), List.of(CreativeModeTabs.TOOLS_AND_UTILITIES));

    public static final Supplier<Item> LOCO_ROUTE = register("locomotive_route",
            () -> new LocoRouteItem(defaultItemPropertiesWithTag(16)), List.of(CreativeModeTabs.TOOLS_AND_UTILITIES));


    public static void buildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        PRIVATE_TAB_REGISTRY.getOrDefault(event.getTabKey(), new ArrayList<>())
                .forEach(supplier -> event.accept(supplier.get()));
    }

    private static <T extends Item> Supplier<T> register(String name, Supplier<T> itemSupplier, List<ResourceKey<CreativeModeTab>> tabs) {
        var res = Registration.ITEMS.register(name, itemSupplier);

        for (var tab : tabs) {
            PRIVATE_TAB_REGISTRY.putInsert(tab, res);
        }

        return res;
    }

    public static void register() {}


    private static Item.Properties defaultItemProperties(int pMaxStackSize) {
        return  new Item.Properties().stacksTo(pMaxStackSize);
    }

    private static Item.Properties defaultItemPropertiesWithTag(int pMaxStackSize) {
        return  new Item.Properties()
                .stacksTo(pMaxStackSize)
                .component(ModDataComponents.TAG_PROPERTIES.get(), new CompoundTag());
    }
}
