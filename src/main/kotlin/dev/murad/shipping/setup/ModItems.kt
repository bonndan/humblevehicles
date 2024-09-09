package dev.murad.shipping.setup

import dev.murad.shipping.HumVeeMod
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
import java.util.function.Consumer
import java.util.function.Supplier

object ModItems {

    private val PRIVATE_TAB_REGISTRY = MultiMap<ResourceKey<CreativeModeTab>, Supplier<out Item>>()

    /**
     * Empty Icons
     */
    @JvmField
    val LOCO_ROUTE_ICON: ResourceLocation = ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "item/empty_loco_route")

    @JvmField
    val TUG_ROUTE_ICON: ResourceLocation = ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "item/empty_tug_route")

    @JvmField
    val EMPTY_ENERGY: ResourceLocation = ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "item/empty_energy")


    /**
     * COMMON
     */
    @JvmField
    val CONDUCTORS_WRENCH: Supplier<Item> = register(
        "conductors_wrench",
        { WrenchItem(defaultItemProperties(1)) }, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES)
    )


    @JvmField
    val SPRING: Supplier<Item> = register(
        "spring",
        { SpringItem(defaultItemProperties(64)) }, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES)
    )

    /**
     * Vessels
     */
    @JvmField
    val CHEST_BARGE: Supplier<Item> = register(
        "barge",
        {
            VesselItem(
                Item.Properties()
            ) { level: Level, x: Double, y: Double, z: Double ->
                ChestBargeEntity(
                    ModEntityTypes.CHEST_BARGE.get(),
                    level,
                    x,
                    y,
                    z
                )
            }
        },
        java.util.List.of(CreativeModeTabs.TOOLS_AND_UTILITIES)
    )

    @JvmField
    val BARREL_BARGE: Supplier<Item> = register(
        "barrel_barge",
        {
            VesselItem(
                Item.Properties()
            ) { level: Level, x: Double, y: Double, z: Double ->
                ChestBargeEntity(
                    ModEntityTypes.BARREL_BARGE.get(),
                    level,
                    x,
                    y,
                    z
                )
            }
        },
        listOf(CreativeModeTabs.TOOLS_AND_UTILITIES)
    )

    //    public static final Supplier<Item> CHUNK_LOADER_BARGE = register("chunk_loader_barge",
    //            () -> new VesselItem(new Item.Properties(), ChunkLoaderBargeEntity::new), ImmutableList.of(CreativeModeTabs.TOOLS_AND_UTILITIES));
    @JvmField
    val FISHING_BARGE: Supplier<Item> = register(
        "fishing_barge",
        {
            VesselItem(Item.Properties()) { worldIn: Level, x: Double, y: Double, z: Double ->
                FishingBargeEntity(
                    worldIn,
                    x,
                    y,
                    z
                )
            }
        }, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES)
    )

    @JvmField
    val FLUID_BARGE: Supplier<Item> = register(
        "fluid_barge",
        {
            VesselItem(Item.Properties()) { worldIn: Level, x: Double, y: Double, z: Double ->
                FluidTankBargeEntity(
                    worldIn,
                    x,
                    y,
                    z
                )
            }
        }, java.util.List.of(CreativeModeTabs.TOOLS_AND_UTILITIES)
    )

    @JvmField
    val SEATER_BARGE: Supplier<Item> = register(
        "seater_barge",
        {
            VesselItem(Item.Properties()) { worldIn: Level, x: Double, y: Double, z: Double ->
                SeaterBargeEntity(
                    worldIn,
                    x,
                    y,
                    z
                )
            }
        }, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES)
    )

    @JvmField
    val VACUUM_BARGE: Supplier<Item> = register(
        "vacuum_barge",
        {
            VesselItem(Item.Properties()) { worldIn, x: Double, y: Double, z: Double ->
                VacuumBargeEntity(
                    worldIn,
                    x,
                    y,
                    z
                )
            }
        }, java.util.List.of(CreativeModeTabs.TOOLS_AND_UTILITIES)
    )

    @JvmField
    val STEAM_TUG: Supplier<Item> = register(
        "tug",
        {
            VesselItem(Item.Properties()) { worldIn, x: Double, y: Double, z: Double ->
                SteamTugEntity(worldIn!!, x, y, z)
            }
        }, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES)
    )

    @JvmField
    val ENERGY_TUG: Supplier<Item> = register(
        "energy_tug",
        {
            VesselItem(Item.Properties()) { worldIn, x: Double, y: Double, z: Double ->
                EnergyTugEntity(
                    worldIn,
                    x,
                    y,
                    z
                )
            }
        }, java.util.List.of(CreativeModeTabs.TOOLS_AND_UTILITIES)
    )

    /**
     * Trains
     */
    @JvmField
    val TUG_ROUTE: Supplier<Item> = register(
        "tug_route",
        { TugRouteItem(defaultItemPropertiesWithTag(16)) }, java.util.List.of(CreativeModeTabs.TOOLS_AND_UTILITIES)
    )

    @JvmField
    val CHEST_CAR: Supplier<Item> = register(
        "chest_car",
        {
            TrainCarItem(
                { level: Level, x: Double, y: Double, z: Double ->
                    ChestCarEntity(ModEntityTypes.CHEST_CAR.get(), level, x, y, z)
                },
                defaultItemProperties(64)
            )
        },
        listOf(CreativeModeTabs.TOOLS_AND_UTILITIES)
    )

    @JvmField
    val BARREL_CAR: Supplier<Item> = register(
        "barrel_car",
        {
            TrainCarItem(
                { level: Level, x: Double, y: Double, z: Double ->
                    ChestCarEntity(ModEntityTypes.BARREL_CAR.get(), level, x, y, z)
                },
                defaultItemProperties(64)
            )
        },
        listOf(CreativeModeTabs.TOOLS_AND_UTILITIES)
    )

    @JvmField
    val FLUID_CAR: Supplier<Item> = register(
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
        }, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES)
    )

    @JvmField
    val SEATER_CAR: Supplier<Item> = register(
        "seater_car",
        {
            TrainCarItem({ level: Level, aDouble: Double, aDouble1: Double, aDouble2: Double ->
                SeaterCarEntity(
                    level,
                    aDouble,
                    aDouble1,
                    aDouble2
                )
            }, defaultItemProperties(64))
        }, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES)
    )

    @JvmField
    val STEAM_LOCOMOTIVE: Supplier<Item> = register(
        "steam_locomotive",
        {
            TrainCarItem(
                { level: Level, x: Double, y: Double, z: Double -> SteamLocomotiveEntity(level, x, y, z) },
                defaultItemProperties(64)
            )
        }, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES)
    )

    @JvmField
    val ENERGY_LOCOMOTIVE: Supplier<Item> = register(
        "energy_locomotive",
        {
            TrainCarItem(
                { level: Level, x: Double, y: Double, z: Double -> EnergyLocomotiveEntity(level, x, y, z) },
                defaultItemProperties(64)
            )
        }, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES)
    )

    val RECEIVER_COMPONENT: Supplier<Item> = register(
        "receiver_component",
        { Item(defaultItemProperties(64)) }, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES)
    )

    val TRANSMITTER_COMPONENT: Supplier<Item> = register(
        "transmitter_component",
        { Item(defaultItemProperties(64)) }, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES)
    )

    @JvmField
    val LOCO_ROUTE: Supplier<Item> = register(
        "locomotive_route",
        { LocoRouteItem(defaultItemPropertiesWithTag(16)) }, java.util.List.of(CreativeModeTabs.TOOLS_AND_UTILITIES)
    )


    fun buildCreativeTab(event: BuildCreativeModeTabContentsEvent) {
        PRIVATE_TAB_REGISTRY.getOrDefault(event.tabKey, ArrayList())
            ?.forEach(Consumer { supplier: Supplier<out Item> -> event.accept(supplier.get()) })
    }

    private fun register(
        name: String,
        itemSupplier: Supplier<Item>,
        tabs: List<ResourceKey<CreativeModeTab>>
    ): Supplier<Item> {
        val res = Registration.ITEMS.register(name, itemSupplier)

        for (tab in tabs) {
            PRIVATE_TAB_REGISTRY.putInsert(tab, itemSupplier)
        }

        return res
    }

    fun register() {}


    private fun defaultItemProperties(pMaxStackSize: Int): Item.Properties {
        return Item.Properties().stacksTo(pMaxStackSize)
    }

    private fun defaultItemPropertiesWithTag(pMaxStackSize: Int): Item.Properties {
        return Item.Properties()
            .stacksTo(pMaxStackSize)
            .component(ModDataComponents.TAG_PROPERTIES.get(), CompoundTag())
    }
}
