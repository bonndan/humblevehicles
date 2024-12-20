package com.github.bonndan.humblevehicles.setup

import com.github.bonndan.humblevehicles.HumVeeMod.Companion.MOD_ID
import com.github.bonndan.humblevehicles.item.LocoRouteItem
import com.github.bonndan.humblevehicles.item.SpringItem
import com.github.bonndan.humblevehicles.item.TugRouteItem
import com.github.bonndan.humblevehicles.item.WrenchItem
import com.github.bonndan.humblevehicles.util.MultiMap
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.minecraft.world.item.MinecartItem
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
    val EMPTY_ENERGY: ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, "item/empty_energy")

    /**
     * COMMON
     */
    val CONDUCTORS_WRENCH = registerItem("conductors_wrench", ::WrenchItem, defaultItemProperties(1))
    val SPRING = registerItem("spring", ::SpringItem, defaultItemPropertiesWithTag(64))

    /**
     * Trains
     */
    val TUG_ROUTE = registerItem("tug_route", ::TugRouteItem, defaultItemPropertiesWithTag(16))

    val CHEST_CAR: DeferredItem<MinecartItem> = Registration.ITEMS.registerItem(
        "chest_car",
        { props -> MinecartItem(ModEntityTypes.CHEST_CAR.get(), props) },
        defaultItemProperties(64)
    )

    val BARREL_CAR = Registration.ITEMS.registerItem(
        "barrel_car",
        { props -> MinecartItem(ModEntityTypes.BARREL_CAR.get(), props) },
        defaultItemProperties(64)
    )

    val FLUID_CAR = Registration.ITEMS.registerItem(
        "fluid_car",
        { props -> MinecartItem(ModEntityTypes.FLUID_CAR.get(), props) },
        defaultItemProperties(64)
    )

    val SEATER_CAR = Registration.ITEMS.registerItem(
        "seater_car",
        { props -> MinecartItem(ModEntityTypes.SEATER_CAR.get(), props) },
        defaultItemProperties(64)
    )

    val STEAM_LOCOMOTIVE = Registration.ITEMS.registerItem(
        "steam_locomotive",
        { props ->MinecartItem(ModEntityTypes.STEAM_LOCOMOTIVE.get(), props) },
        defaultItemProperties(64)
    )

    val ENERGY_LOCOMOTIVE: Supplier<Item> = Registration.ITEMS.registerItem(
        "energy_locomotive",
        { props ->MinecartItem(ModEntityTypes.ENERGY_LOCOMOTIVE.get(), props) },
        defaultItemProperties(64)
    )

    val RECEIVER_COMPONENT = registerItem("receiver_component", ::Item, defaultItemProperties(64))
    val TRANSMITTER_COMPONENT = registerItem("transmitter_component", ::Item, defaultItemProperties(64))
    val LOCO_ROUTE = registerItem("locomotive_route", ::LocoRouteItem, defaultItemPropertiesWithTag(16))

    init {
        registerTabs(CONDUCTORS_WRENCH, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES))
        registerTabs(SPRING, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES))
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

    private fun registerTabs(item: DeferredItem<MinecartItem>, tabs: List<ResourceKey<CreativeModeTab>>) {
        for (tab in tabs) {
            PRIVATE_TAB_REGISTRY.putInsert(tab, item)
        }
    }

    private fun registerTabs(itemSupplier: Supplier<Item>, tabs: List<ResourceKey<CreativeModeTab>>) {
        for (tab in tabs) {
            PRIVATE_TAB_REGISTRY.putInsert(tab, itemSupplier)
        }
    }

    fun register() {}


    private fun defaultItemProperties(pMaxStackSize: Int = 64): Item.Properties {
        return Item.Properties().stacksTo(pMaxStackSize)
    }

    private fun defaultItemPropertiesWithTag(pMaxStackSize: Int): Item.Properties {
        return Item.Properties()
            .stacksTo(pMaxStackSize)
//            .component<CompoundTag>(ModDataComponents.TAG_PROPERTIES!!.get(), CompoundTag())
    }
}
