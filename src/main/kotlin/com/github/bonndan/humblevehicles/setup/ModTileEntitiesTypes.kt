package com.github.bonndan.humblevehicles.setup

import com.github.bonndan.humblevehicles.block.dock.BargeDockTileEntity
import com.github.bonndan.humblevehicles.block.dock.TugDockTileEntity
import com.github.bonndan.humblevehicles.block.fluid.FluidHopperTileEntity
import com.github.bonndan.humblevehicles.block.rail.blockentity.LocomotiveDockTileEntity
import com.github.bonndan.humblevehicles.block.rail.blockentity.TrainCarDockTileEntity
import com.github.bonndan.humblevehicles.block.vesseldetector.VesselDetectorTileEntity
import com.github.bonndan.humblevehicles.entity.custom.vessel.submarine.LightTileEntity
import com.github.bonndan.humblevehicles.setup.Registration.TILE_ENTITIES
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks.WATER
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import java.util.function.Supplier

object ModTileEntitiesTypes {

    val TUG_DOCK: Supplier<BlockEntityType<TugDockTileEntity>> = TILE_ENTITIES.register(
        "tug_dock",
        Supplier<BlockEntityType<TugDockTileEntity>> {
            BlockEntityType.Builder.of(
                { pos: BlockPos, state: BlockState -> TugDockTileEntity(pos, state) },
                ModBlocks.TUG_DOCK.get()
            ).build(null)
        }
    )

    val BARGE_DOCK: Supplier<BlockEntityType<BargeDockTileEntity>> = TILE_ENTITIES.register(
        "barge_dock",
        Supplier<BlockEntityType<BargeDockTileEntity>> {
            BlockEntityType.Builder.of(
                { pos: BlockPos, state: BlockState -> BargeDockTileEntity(pos, state) },
                ModBlocks.BARGE_DOCK.get()
            ).build(null)
        }
    )

    val LOCOMOTIVE_DOCK: Supplier<BlockEntityType<LocomotiveDockTileEntity>> = TILE_ENTITIES.register(
        "locomotive_dock",
        Supplier<BlockEntityType<LocomotiveDockTileEntity>> {
            BlockEntityType.Builder.of(
                { pos: BlockPos, state: BlockState -> LocomotiveDockTileEntity(pos, state) },
                ModBlocks.LOCOMOTIVE_DOCK_RAIL.get()
            ).build(null)
        }
    )

    val CAR_DOCK: Supplier<BlockEntityType<TrainCarDockTileEntity>> = TILE_ENTITIES.register(
        "car_dock",
        Supplier<BlockEntityType<TrainCarDockTileEntity>> {
            BlockEntityType.Builder.of(
                { pos: BlockPos, state: BlockState -> TrainCarDockTileEntity(pos, state) },
                ModBlocks.CAR_DOCK_RAIL
                    .get()
            ).build(null)
        })

    val VESSEL_DETECTOR: Supplier<BlockEntityType<VesselDetectorTileEntity>> = TILE_ENTITIES.register(
        "vessel_detector",
        Supplier<BlockEntityType<VesselDetectorTileEntity>> {
            BlockEntityType.Builder.of(
                { pos: BlockPos, state: BlockState -> VesselDetectorTileEntity(pos, state) },
                ModBlocks.VESSEL_DETECTOR.get()
            ).build(null)
        }
    )

    val FLUID_HOPPER: Supplier<BlockEntityType<FluidHopperTileEntity>> = TILE_ENTITIES.register(
        "fluid_hopper",
        Supplier<BlockEntityType<FluidHopperTileEntity>> {
            BlockEntityType.Builder.of(
                { pos: BlockPos, state: BlockState -> FluidHopperTileEntity(pos, state) },
                ModBlocks.FLUID_HOPPER.get()
            ).build(null)
        }
    )

    val LIGHT: Supplier<BlockEntityType<LightTileEntity>> = TILE_ENTITIES.register(
        "light",
        Supplier<BlockEntityType<LightTileEntity>> {
            BlockEntityType.Builder.of(
                { pos: BlockPos, state: BlockState -> LightTileEntity(pos, state) },
                ModBlocks.LIGHT_BLOCK.get(), WATER
            ).build(null)
        })

    fun register() {
    }
}
