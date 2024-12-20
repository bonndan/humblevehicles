package com.github.bonndan.humblevehicles.setup


import com.github.bonndan.humblevehicles.block.fluid.FluidHopperBlock
import com.github.bonndan.humblevehicles.block.rail.*
import com.github.bonndan.humblevehicles.setup.Registration.BLOCKS
import com.github.bonndan.humblevehicles.setup.Registration.ITEMS
import com.github.bonndan.humblevehicles.util.MultiMap
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import java.util.function.Function
import java.util.function.Supplier


object ModBlocks {

    private val PRIVATE_TAB_REGISTRY = MultiMap<ResourceKey<CreativeModeTab>, Supplier<BlockItem>>()

    val FLUID_HOPPER: Supplier<Block> = register(
        "fluid_hopper",
        { props -> FluidHopperBlock(props) },
        BlockBehaviour.Properties
            .of()
            .mapColor(MapColor.METAL)
            .strength(0.5f, 6.0f)
            .sound(SoundType.METAL),
        listOf(CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.REDSTONE_BLOCKS)
    )

    val SWITCH_RAIL: Supplier<Block> = register(
        "switch_rail",
        { props -> SwitchRail(props) },
        BlockBehaviour.Properties.ofFullCopy(Blocks.RAIL),
        listOf(CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.REDSTONE_BLOCKS)
    )

    val AUTOMATIC_SWITCH_RAIL: Supplier<Block> = register(
        "automatic_switch_rail",
        { props -> SwitchRail(props, true) },
        BlockBehaviour.Properties.ofFullCopy(Blocks.RAIL),
        listOf(CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.REDSTONE_BLOCKS)
    )

    val TEE_JUNCTION_RAIL: Supplier<Block> = register(
        "tee_junction_rail",
        { props -> TeeJunctionRail(props) },
        BlockBehaviour.Properties.ofFullCopy(Blocks.RAIL),
        listOf(CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.REDSTONE_BLOCKS)
    )

    val AUTOMATIC_TEE_JUNCTION_RAIL: Supplier<Block> = register(
        "automatic_tee_junction_rail",
        { props -> TeeJunctionRail(props, true) },
        BlockBehaviour.Properties.ofFullCopy(Blocks.RAIL),
        listOf(CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.REDSTONE_BLOCKS)
    )

    val JUNCTION_RAIL: Supplier<Block> = register(
        "junction_rail",
        { props -> JunctionRail(props) },
        BlockBehaviour.Properties.ofFullCopy(Blocks.RAIL),
        listOf(CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.REDSTONE_BLOCKS)
    )

    val CAR_DOCK_RAIL: Supplier<Block> = register(
        "car_dock_rail",
        { props -> TrainCarDockingRail(props) },
        BlockBehaviour.Properties.ofFullCopy(Blocks.RAIL),
        listOf(CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.REDSTONE_BLOCKS)
    )

    val LOCOMOTIVE_DOCK_RAIL: Supplier<Block> = register(
        "locomotive_dock_rail",
        { props -> LocomotiveDockingRail(props) },
        BlockBehaviour.Properties.ofFullCopy(Blocks.RAIL),
        listOf(CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.REDSTONE_BLOCKS)
    )

    fun buildCreativeTab(event: BuildCreativeModeTabContentsEvent) {
        PRIVATE_TAB_REGISTRY.getOrDefault(event.tabKey, ArrayList())
            .forEach { supplier: Supplier<BlockItem> -> event.accept(supplier.get()) }
    }

    private fun register(
        name: String,
        block: Function<BlockBehaviour.Properties, Block>,
        properties: BlockBehaviour.Properties,
        tabs: List<ResourceKey<CreativeModeTab>>
    ): Supplier<Block> {

        val holder = BLOCKS.register(name) { registryName ->
            block.apply(properties.setId(ResourceKey.create(Registries.BLOCK, registryName)))
        }
        val item = ITEMS.registerSimpleBlockItem(name, holder, Item.Properties())

        for (tab in tabs) {
            PRIVATE_TAB_REGISTRY.putInsert(tab, item)
        }

        return holder
    }

    fun register() {

    }
}
