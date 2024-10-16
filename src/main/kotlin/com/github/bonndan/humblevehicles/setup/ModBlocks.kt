package com.github.bonndan.humblevehicles.setup

import com.github.bonndan.humblevehicles.block.dock.BargeDockBlock
import com.github.bonndan.humblevehicles.block.dock.TugDockBlock
import com.github.bonndan.humblevehicles.block.fluid.FluidHopperBlock
import com.github.bonndan.humblevehicles.block.guiderail.CornerGuideRailBlock
import com.github.bonndan.humblevehicles.block.guiderail.TugGuideRailBlock
import com.github.bonndan.humblevehicles.block.rail.*
import com.github.bonndan.humblevehicles.block.vesseldetector.VesselDetectorBlock
import com.github.bonndan.humblevehicles.setup.Registration.BLOCKS
import com.github.bonndan.humblevehicles.setup.Registration.ITEMS
import com.github.bonndan.humblevehicles.util.MultiMap
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
import java.util.function.Supplier


object ModBlocks {

    private val PRIVATE_TAB_REGISTRY = MultiMap<ResourceKey<CreativeModeTab>, Supplier<BlockItem>>()

    // Taken from IRON_BLOCK
    private val METAL_BLOCK_BEHAVIOUR: BlockBehaviour.Properties = BlockBehaviour.Properties
        .of()
        .mapColor(MapColor.METAL)
        .strength(0.5f, 6.0f)
        .sound(SoundType.METAL)

    private val RAIL_BLOCK_BEHAVIOUR: BlockBehaviour.Properties = BlockBehaviour.Properties.ofFullCopy(Blocks.RAIL)


    val TUG_DOCK: Supplier<Block> = register(
        "tug_dock", { TugDockBlock(METAL_BLOCK_BEHAVIOUR) }, listOf(
            CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.REDSTONE_BLOCKS
        )
    )

    val BARGE_DOCK: Supplier<Block> = register(
        "barge_dock", { BargeDockBlock(METAL_BLOCK_BEHAVIOUR) }, java.util.List.of(
            CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.REDSTONE_BLOCKS
        )
    )

    val GUIDE_RAIL_CORNER: Supplier<Block> = register(
        "guide_rail_corner", { CornerGuideRailBlock(METAL_BLOCK_BEHAVIOUR) }, java.util.List.of(
            CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.REDSTONE_BLOCKS
        )
    )

    val VESSEL_DETECTOR: Supplier<Block> = register(
        "vessel_detector", { VesselDetectorBlock(METAL_BLOCK_BEHAVIOUR) }, java.util.List.of(
            CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.REDSTONE_BLOCKS
        )
    )

    val GUIDE_RAIL_TUG: Supplier<Block> = register(
        "guide_rail_tug", { TugGuideRailBlock(METAL_BLOCK_BEHAVIOUR) }, java.util.List.of(
            CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.REDSTONE_BLOCKS
        )
    )

    val FLUID_HOPPER: Supplier<Block> = register(
        "fluid_hopper", { FluidHopperBlock(METAL_BLOCK_BEHAVIOUR) }, java.util.List.of(
            CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.REDSTONE_BLOCKS
        )
    )

    val SWITCH_RAIL: Supplier<Block> = register(
        "switch_rail", { SwitchRail(RAIL_BLOCK_BEHAVIOUR, false) }, java.util.List.of(
            CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.REDSTONE_BLOCKS
        )
    )

    val AUTOMATIC_SWITCH_RAIL: Supplier<Block> = register(
        "automatic_switch_rail", { SwitchRail(RAIL_BLOCK_BEHAVIOUR, true) }, java.util.List.of(
            CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.REDSTONE_BLOCKS
        )
    )


    val TEE_JUNCTION_RAIL: Supplier<Block> = register(
        "tee_junction_rail", { TeeJunctionRail(RAIL_BLOCK_BEHAVIOUR, false) }, listOf(
            CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.REDSTONE_BLOCKS
        )
    )


    val AUTOMATIC_TEE_JUNCTION_RAIL: Supplier<Block> = register(
        "automatic_tee_junction_rail", { TeeJunctionRail(RAIL_BLOCK_BEHAVIOUR, true) }, java.util.List.of(
            CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.REDSTONE_BLOCKS
        )
    )

    val JUNCTION_RAIL: Supplier<Block> = register(
        "junction_rail", { JunctionRail(RAIL_BLOCK_BEHAVIOUR) }, java.util.List.of(
            CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.REDSTONE_BLOCKS
        )
    )

    val CAR_DOCK_RAIL: Supplier<Block> = register(
        "car_dock_rail", { TrainCarDockingRail(RAIL_BLOCK_BEHAVIOUR) }, listOf(
            CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.REDSTONE_BLOCKS
        )
    )

    val LOCOMOTIVE_DOCK_RAIL: Supplier<Block> = register(
        "locomotive_dock_rail", { LocomotiveDockingRail(RAIL_BLOCK_BEHAVIOUR) }, listOf(
            CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.REDSTONE_BLOCKS
        )
    )

    fun buildCreativeTab(event: BuildCreativeModeTabContentsEvent) {
        PRIVATE_TAB_REGISTRY.getOrDefault(event.tabKey, ArrayList())
            .forEach { supplier: Supplier<BlockItem> -> event.accept(supplier.get()) }
    }

    private fun register(
        name: String,
        block: Supplier<Block>,
        tabs: List<ResourceKey<CreativeModeTab>>
    ): Supplier<Block> {

        val ret = BLOCKS.register(name, block)
        val item = ITEMS.registerItem(name) { _ -> BlockItem(ret.get(), Item.Properties()) }

        for (tab in tabs) {
            PRIVATE_TAB_REGISTRY.putInsert(tab, item)
        }

        return ret
    }

    fun register() {

    }
}
