package dev.murad.shipping.setup

import dev.murad.shipping.HumVeeMod.Companion.MOD_ID
import dev.murad.shipping.entity.custom.train.locomotive.EnergyLocomotiveEntity
import dev.murad.shipping.entity.custom.train.locomotive.SteamLocomotiveEntity
import dev.murad.shipping.entity.custom.train.wagon.ChestCarEntity
import dev.murad.shipping.entity.custom.train.wagon.FluidTankCarEntity
import dev.murad.shipping.entity.custom.train.wagon.SeaterCarEntity
import dev.murad.shipping.entity.custom.vessel.barge.*
import dev.murad.shipping.entity.custom.vessel.tug.EnergyTugEntity
import dev.murad.shipping.entity.custom.vessel.tug.SteamTugEntity
import dev.murad.shipping.item.*
import dev.murad.shipping.util.MultiMap
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.registries.DeferredItem
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

object ModItems {

    private val PRIVATE_TAB_REGISTRY = MultiMap<ResourceKey<CreativeModeTab>, Supplier<out Item>>()

    /**
     * Empty Icons
     */
    val LOCO_ROUTE_ICON: ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, "item/empty_loco_route")
    val TUG_ROUTE_ICON: ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, "item/empty_tug_route")
    val EMPTY_ENERGY: ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, "item/empty_energy")

    /**
     * COMMON
     */
    val CONDUCTORS_WRENCH = registerItem("conductors_wrench", ::WrenchItem, defaultItemProperties(1))
    val SPRING = registerItem("spring", ::SpringItem, (defaultItemProperties(64)))

    /**
     * Vessels
     */
    val CHEST_BARGE = registerItem(
        "barge",
        {
            VesselItem(Item.Properties()) { level: Level, x: Double, y: Double, z: Double ->
                ChestBargeEntity(ModEntityTypes.CHEST_BARGE.get(), level, x, y, z)
            }
        },
        Item.Properties()
    )

    val BARREL_BARGE = registerItem(
        "barrel_barge",
        {
            VesselItem(Item.Properties()) { level: Level, x: Double, y: Double, z: Double ->
                ChestBargeEntity(ModEntityTypes.BARREL_BARGE.get(), level, x, y, z)
            }
        },
        Item.Properties()
    )

    //    public static final Supplier<Item> CHUNK_LOADER_BARGE = register("chunk_loader_barge",

    val FISHING_BARGE = registerItem(
        "fishing_barge",
        {
            VesselItem(Item.Properties()) { worldIn: Level, x: Double, y: Double, z: Double ->
                FishingBargeEntity(worldIn, x, y, z)
            }
        },
        Item.Properties()
    )

    val FLUID_BARGE = registerItem(
        "fluid_barge",
        {
            VesselItem(Item.Properties()) { worldIn: Level, x: Double, y: Double, z: Double ->
                FluidTankBargeEntity(worldIn, x, y, z)
            }
        },
        Item.Properties()
    )


    val SEATER_BARGE = registerItem(
        "seater_barge",
        {
            VesselItem(Item.Properties()) { worldIn: Level, x: Double, y: Double, z: Double ->
                SeaterBargeEntity(worldIn, x, y, z)
            }
        }, Item.Properties()
    )


    val VACUUM_BARGE: Supplier<Item> = registerItem(
        "vacuum_barge",
        {
            VesselItem(Item.Properties()) { worldIn, x: Double, y: Double, z: Double ->
                VacuumBargeEntity(worldIn, x, y, z)
            }
        },
        Item.Properties()
    )

    val STEAM_TUG: Supplier<Item> = registerItem(
        "tug",
        {
            VesselItem(Item.Properties()) { worldIn, x: Double, y: Double, z: Double ->
                SteamTugEntity(worldIn, x, y, z)
            }
        }, Item.Properties()
    )


    val ENERGY_TUG = registerItem(
        "energy_tug",
        {
            VesselItem(Item.Properties()) { worldIn, x: Double, y: Double, z: Double ->
                EnergyTugEntity(worldIn, x, y, z)
            }
        }, Item.Properties()
    )


    /**
     * Trains
     */
    val TUG_ROUTE = registerItem("tug_route", ::TugRouteItem, defaultItemPropertiesWithTag(16))

    val CHEST_CAR = registerItem(
        "chest_car",
        {
            TrainCarItem(
                { level: Level, x: Double, y: Double, z: Double ->
                    ChestCarEntity(ModEntityTypes.CHEST_CAR.get(), level, x, y, z)
                },
                Item.Properties()
            )
        },
        defaultItemProperties(64)
    )

    val BARREL_CAR = registerItem(
        "barrel_car",
        {
            TrainCarItem(
                { level: Level, x: Double, y: Double, z: Double ->
                    ChestCarEntity(ModEntityTypes.BARREL_CAR.get(), level, x, y, z)
                },
                Item.Properties()
            )
        },
        defaultItemProperties(64)
    )


    val FLUID_CAR = registerItem(
        "fluid_car",
        {
            TrainCarItem({ level: Level, aDouble: Double, aDouble1: Double, aDouble2: Double ->
                FluidTankCarEntity(
                    level,
                    aDouble,
                    aDouble1,
                    aDouble2
                )
            }, defaultItemProperties(64))
        },
        defaultItemProperties(64)
    )


    val SEATER_CAR = registerItem(
        "seater_car",
        {
            TrainCarItem({ level: Level, aDouble: Double, aDouble1: Double, aDouble2: Double ->
                SeaterCarEntity(level, aDouble, aDouble1, aDouble2)
            }, defaultItemProperties(64))
        },
        defaultItemProperties(64)
    )


    val STEAM_LOCOMOTIVE = registerItem(
        "steam_locomotive",
        {
            TrainCarItem(
                { level: Level, x: Double, y: Double, z: Double -> SteamLocomotiveEntity(level, x, y, z) },
                defaultItemProperties(64)
            )
        },
        defaultItemProperties(64)
    )

    val ENERGY_LOCOMOTIVE: Supplier<Item> = registerItem(
        "energy_locomotive",
        {
            TrainCarItem(
                { level: Level, x: Double, y: Double, z: Double -> EnergyLocomotiveEntity(level, x, y, z) },
                defaultItemProperties(64)
            )
        }, defaultItemProperties(64)
    )


    val RECEIVER_COMPONENT = registerItem("receiver_component", ::Item, defaultItemProperties(64))
    val TRANSMITTER_COMPONENT = registerItem("transmitter_component", ::Item, defaultItemProperties(64))
    val LOCO_ROUTE = registerItem("locomotive_route", ::LocoRouteItem, defaultItemPropertiesWithTag(16))

    //            () -> new VesselItem(new Item.Properties(), ChunkLoaderBargeEntity::new), ImmutableList.of(CreativeModeTabs.TOOLS_AND_UTILITIES));
    init {
        registerTabs(CONDUCTORS_WRENCH, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES))
        registerTabs(SPRING, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES))
        registerTabs(CHEST_BARGE, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES))
        registerTabs(BARREL_BARGE, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES))
        registerTabs(FISHING_BARGE, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES))
        registerTabs(FLUID_BARGE, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES))
        registerTabs(SEATER_BARGE, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES))
        registerTabs(VACUUM_BARGE, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES))
        registerTabs(STEAM_TUG, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES))
        registerTabs(ENERGY_TUG, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES))
        registerTabs(TUG_ROUTE, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES))
        registerTabs(CHEST_CAR, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES))
        registerTabs(BARREL_CAR, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES))
        registerTabs(FLUID_CAR, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES))
        registerTabs(SEATER_CAR, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES))
        registerTabs(STEAM_LOCOMOTIVE, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES))
        registerTabs(ENERGY_LOCOMOTIVE, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES))
        registerTabs(RECEIVER_COMPONENT, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES))
        registerTabs(TRANSMITTER_COMPONENT, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES))
        registerTabs(LOCO_ROUTE, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES))
    }


    fun buildCreativeTab(event: BuildCreativeModeTabContentsEvent) {
        PRIVATE_TAB_REGISTRY.getOrDefault(event.tabKey, ArrayList())
            .forEach(Consumer { supplier: Supplier<out Item> -> event.accept(supplier.get()) })
    }

    private fun registerItem(
        name: String,
        itemSupplier: Function<Item.Properties, Item>,
        props: Item.Properties,
    ): DeferredItem<Item> {

        return Registration.ITEMS.registerItem(name, itemSupplier, props)
    }

    private fun registerTabs(itemSupplier: Supplier<Item>, tabs: List<ResourceKey<CreativeModeTab>>) {
        for (tab in tabs) {
            PRIVATE_TAB_REGISTRY.putInsert(tab, itemSupplier)
        }
    }

    fun register() {

    }


    private fun defaultItemProperties(pMaxStackSize: Int): Item.Properties {
        return Item.Properties().stacksTo(pMaxStackSize)
    }

    private fun defaultItemPropertiesWithTag(pMaxStackSize: Int): Item.Properties {
        return Item.Properties()
            .stacksTo(pMaxStackSize)
            //TODO trying to access unbound value in DeferredHolder .component(ModDataComponents.getCompoundTag().get(), CompoundTag())
    }
}
