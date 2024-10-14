package dev.murad.shipping.data.client

import dev.murad.shipping.HumVeeMod
import dev.murad.shipping.block.dock.DockingBlockStates
import dev.murad.shipping.block.fluid.FluidHopperBlock
import dev.murad.shipping.block.guiderail.CornerGuideRailBlock
import dev.murad.shipping.block.rail.AbstractDockingRail
import dev.murad.shipping.block.rail.SwitchRail
import dev.murad.shipping.block.vesseldetector.VesselDetectorBlock
import dev.murad.shipping.setup.ModBlocks
import net.minecraft.core.Direction
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.RailShape
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ConfiguredModel
import net.neoforged.neoforge.client.model.generators.ModelFile
import net.neoforged.neoforge.common.data.ExistingFileHelper

class ModBlockStateProvider(output: PackOutput, exFileHelper: ExistingFileHelper) :
    BlockStateProvider(output, HumVeeMod.MOD_ID, exFileHelper) {

    private fun getTugDockModel(state: BlockState): ModelFile {
        val inv = if (state.getValue(DockingBlockStates.INVERTED)) "_inv" else ""
        val powered = if (state.getValue(DockingBlockStates.POWERED)) "_powered" else ""
        return models().orientable(
            "tug_dock$inv$powered",
            getBlTx("tug_dock"),
            getBlTx("tug_dock_front$powered"),
            getBlTx("tug_dock_top$inv")
        )
    }

    private fun getCornerGuideRailModel(state: BlockState): ModelFile {
        val inv = if (state.getValue(CornerGuideRailBlock.INVERTED)) "_inv" else ""
        return models().orientable(
            "guide_rail_corner$inv",
            getBlTx("guide_rail_side"),
            getBlTx("guide_rail_front$inv"),
            getBlTx("guide_rail_top$inv")
        )
    }

    private fun getTugGuideRailModel(state: BlockState): ModelFile {
        return models().orientable(
            "guide_rail_tug",
            getBlTx("guide_rail_side"),
            getBlTx("guide_rail_side"),
            getBlTx("guide_rail_front")
        )
    }

    private fun getVesselDetectorModel(state: BlockState): ModelFile {
        val powered = if (state.getValue(VesselDetectorBlock.POWERED)) "_powered" else ""

        return models().withExistingParent("vessel_detector$powered", modLoc("orientable_with_back"))
            .texture("side", getBlTx("vessel_detector_side"))
            .texture("front", getBlTx("vessel_detector_front"))
            .texture("back", getBlTx("vessel_detector_back$powered"))
    }

    private fun getBargeDockModel(state: BlockState): ModelFile {
        val inv = if (state.getValue(DockingBlockStates.INVERTED)) "_extract" else ""
        return models().orientable(
            "barge_dock$inv",
            getBlTx("barge_dock"),
            getBlTx("barge_dock_front$inv"),
            getBlTx("barge_dock_top")
        )
    }

    private fun xRotFromDir(direction: Direction): Int {
        return when (direction) {
            Direction.DOWN -> 270
            Direction.UP -> 90
            else -> 0
        }
    }


    override fun registerStatesAndModels() {
        getVariantBuilder(ModBlocks.TUG_DOCK.get()).forAllStates { state: BlockState ->
            ConfiguredModel.builder()
                .modelFile(getTugDockModel(state))
                .rotationY(
                    state.getValue(DockingBlockStates.FACING).opposite.toYRot().toInt()
                )
                .build()
        }

        getVariantBuilder(ModBlocks.BARGE_DOCK.get()).forAllStates { state: BlockState ->
            ConfiguredModel.builder()
                .modelFile(getBargeDockModel(state))
                .rotationY(
                    state.getValue(DockingBlockStates.FACING).opposite.toYRot().toInt()
                )
                .build()
        }

        getVariantBuilder(ModBlocks.GUIDE_RAIL_CORNER.get()).forAllStates { state: BlockState ->
            ConfiguredModel.builder()
                .modelFile(getCornerGuideRailModel(state))
                .rotationY(
                    state.getValue(CornerGuideRailBlock.FACING).opposite.toYRot().toInt()
                )
                .build()
        }

        getVariantBuilder(ModBlocks.VESSEL_DETECTOR.get()).forAllStates { state: BlockState ->
            ConfiguredModel.builder()
                .modelFile(getVesselDetectorModel(state))
                .rotationY(
                    state.getValue(VesselDetectorBlock.FACING).opposite.toYRot().toInt()
                )
                .rotationX(xRotFromDir(state.getValue(VesselDetectorBlock.FACING).opposite))
                .build()
        }

        getVariantBuilder(ModBlocks.GUIDE_RAIL_TUG.get()).forAllStates { state: BlockState ->
            ConfiguredModel.builder()
                .modelFile(getTugGuideRailModel(state))
                .rotationY(
                    state.getValue(CornerGuideRailBlock.FACING).clockWise.toYRot().toInt()
                )
                .build()
        }

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
