package com.github.bonndan.humblevehicles.data.client

import com.github.bonndan.humblevehicles.HumVeeMod
import com.github.bonndan.humblevehicles.block.dock.DockingBlockStates
import com.github.bonndan.humblevehicles.block.fluid.FluidHopperBlock
import com.github.bonndan.humblevehicles.block.rail.AbstractDockingRail
import com.github.bonndan.humblevehicles.block.rail.SwitchRail
import com.github.bonndan.humblevehicles.setup.ModBlocks
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.RailShape
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ConfiguredModel
import net.neoforged.neoforge.common.data.ExistingFileHelper

class ModBlockStateProvider(output: PackOutput, exFileHelper: ExistingFileHelper) :
    BlockStateProvider(output, HumVeeMod.MOD_ID, exFileHelper) {


    override fun registerStatesAndModels() {

        getVariantBuilder(ModBlocks.FLUID_HOPPER.get()).forAllStates { state: BlockState ->
            ConfiguredModel.builder()
                .modelFile(
                    models()
                        .withExistingParent("fluid_hopper", modLoc("fluid_hopper_parent_model"))
                )
                .rotationY(
                    state.getValue(FluidHopperBlock.FACING).clockWise.toYRot().toInt()
                )
                .build()
        }

        getVariantBuilder(ModBlocks.SWITCH_RAIL.get()).forAllStates { state: BlockState ->
            val outDir = state.getValue(SwitchRail.OUT_DIRECTION).serializedName
            val powered = if (state.getValue(SwitchRail.POWERED)) "on" else "off"
            ConfiguredModel.builder()
                .modelFile(
                    models()
                        .withExistingParent("switch_rail_" + outDir + "_" + powered, mcLoc("rail_flat"))
                        .texture(
                            "rail",
                            getBlTx("switch_rail_" + outDir + "_" + powered)
                        )
                )
                .rotationY(state.getValue(SwitchRail.FACING).opposite.toYRot().toInt())
                .build()
        }

        getVariantBuilder(ModBlocks.AUTOMATIC_SWITCH_RAIL.get()).forAllStates { state: BlockState ->
            val outDir = state.getValue(SwitchRail.OUT_DIRECTION).serializedName
            val powered = if (state.getValue(SwitchRail.POWERED)) "on" else "off"
            ConfiguredModel.builder()
                .modelFile(
                    models()
                        .withExistingParent("automatic_switch_rail_" + outDir + "_" + powered, mcLoc("rail_flat"))
                        .texture(
                            "rail",
                            getBlTx("automatic_switch_rail_" + outDir + "_" + powered)
                        )
                )
                .rotationY(state.getValue(SwitchRail.FACING).opposite.toYRot().toInt())
                .build()
        }

        getVariantBuilder(ModBlocks.TEE_JUNCTION_RAIL.get()).forAllStates { state: BlockState ->
            val powered = if (state.getValue(SwitchRail.POWERED)) "on" else "off"
            ConfiguredModel.builder()
                .modelFile(
                    models()
                        .withExistingParent("tee_junction_rail_$powered", mcLoc("rail_flat"))
                        .texture("rail", getBlTx("tee_junction_rail_$powered"))
                )
                .rotationY(state.getValue(SwitchRail.FACING).opposite.toYRot().toInt())
                .build()
        }

        getVariantBuilder(ModBlocks.AUTOMATIC_TEE_JUNCTION_RAIL.get()).forAllStates { state: BlockState ->
            val powered = if (state.getValue(SwitchRail.POWERED)) "on" else "off"
            ConfiguredModel.builder()
                .modelFile(
                    models()
                        .withExistingParent("automatic_tee_junction_rail_$powered", mcLoc("rail_flat"))
                        .texture(
                            "rail",
                            getBlTx("automatic_tee_junction_rail_$powered")
                        )
                )
                .rotationY(state.getValue(SwitchRail.FACING).opposite.toYRot().toInt())
                .build()
        }

        getVariantBuilder(ModBlocks.JUNCTION_RAIL.get()).forAllStates { state: BlockState? ->
            ConfiguredModel.builder()
                .modelFile(
                    models()
                        .withExistingParent("junction_rail", mcLoc("rail_flat"))
                        .texture("rail", getBlTx("junction_rail"))
                )
                .build()
        }

        getVariantBuilder(ModBlocks.CAR_DOCK_RAIL.get()).forAllStates { state: BlockState ->
            val inv = if (state.getValue(DockingBlockStates.INVERTED)) "_extract" else ""
            ConfiguredModel.builder()
                .modelFile(
                    models()
                        .withExistingParent("car_dock_rail$inv", mcLoc("rail_flat"))
                        .texture("rail", getBlTx("car_dock_rail$inv"))
                )
                .rotationY(if (state.getValue(AbstractDockingRail.RAIL_SHAPE) == RailShape.NORTH_SOUTH) 0 else 90)
                .build()
        }

        getVariantBuilder(ModBlocks.LOCOMOTIVE_DOCK_RAIL.get()).forAllStates { state: BlockState ->
            val powered = if (state.getValue(DockingBlockStates.POWERED)) "_powered" else ""
            ConfiguredModel.builder()
                .modelFile(
                    models()
                        .withExistingParent("locomotive_dock_rail$powered", mcLoc("rail_flat"))
                        .texture("rail", getBlTx("locomotive_dock_rail$powered"))
                )
                .rotationY(
                    state.getValue(DockingBlockStates.FACING).opposite.toYRot().toInt()
                )
                .build()
        }
    }

    companion object {
        fun getBlTx(name: String): ResourceLocation {
            return ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, String.format("block/%s", name))
        }
    }
}
