package dev.murad.shipping.setup

import dev.murad.shipping.block.dock.BargeDockTileEntity
import dev.murad.shipping.block.dock.TugDockTileEntity
import dev.murad.shipping.block.energy.VesselChargerTileEntity
import dev.murad.shipping.block.fluid.FluidHopperTileEntity
import dev.murad.shipping.block.rail.blockentity.LocomotiveDockTileEntity
import dev.murad.shipping.block.rail.blockentity.TrainCarDockTileEntity
import dev.murad.shipping.block.rapidhopper.RapidHopperTileEntity
import dev.murad.shipping.block.vesseldetector.VesselDetectorTileEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier
import net.minecraft.world.level.block.state.BlockState
import java.util.function.Supplier

object ModTileEntitiesTypes {

    val TUG_DOCK: Supplier<BlockEntityType<TugDockTileEntity>> = register(
        "tug_dock",
        { pos: BlockPos, state: BlockState -> TugDockTileEntity(pos, state) },
        ModBlocks.TUG_DOCK
    )

    val BARGE_DOCK: Supplier<BlockEntityType<BargeDockTileEntity>> = register(
        "barge_dock",
        { pos: BlockPos, state: BlockState -> BargeDockTileEntity(pos, state) },
        ModBlocks.BARGE_DOCK
    )

    val LOCOMOTIVE_DOCK: Supplier<BlockEntityType<LocomotiveDockTileEntity>> = register(
        "locomotive_dock",
        { pos: BlockPos, state: BlockState -> LocomotiveDockTileEntity(pos, state) },
        ModBlocks.LOCOMOTIVE_DOCK_RAIL
    )

    val CAR_DOCK: Supplier<BlockEntityType<TrainCarDockTileEntity>> = register(
        "car_dock",
        { pos: BlockPos, state: BlockState -> TrainCarDockTileEntity(pos, state) },
        ModBlocks.CAR_DOCK_RAIL
    )

    val VESSEL_DETECTOR: Supplier<BlockEntityType<VesselDetectorTileEntity>> = register(
        "vessel_detector",
        { pos: BlockPos, state: BlockState -> VesselDetectorTileEntity(pos, state) },
        ModBlocks.VESSEL_DETECTOR
    )

    val FLUID_HOPPER: Supplier<BlockEntityType<FluidHopperTileEntity>> = register(
        "fluid_hopper",
        { pos: BlockPos, state: BlockState -> FluidHopperTileEntity(pos, state) },
        ModBlocks.FLUID_HOPPER
    )

    val VESSEL_CHARGER: Supplier<BlockEntityType<VesselChargerTileEntity>> = register(
        "vessel_charger",
        { pos: BlockPos, state: BlockState -> VesselChargerTileEntity(pos, state) },
        ModBlocks.VESSEL_CHARGER
    )

    
    val RAPID_HOPPER: Supplier<BlockEntityType<RapidHopperTileEntity>> = register(
        "rapid_hopper",
        { pWorldPosition: BlockPos, pBlockState: BlockState -> RapidHopperTileEntity(pWorldPosition, pBlockState) },
        ModBlocks.RAPID_HOPPER
    )

    private fun <T : BlockEntity> register(
        name: String,
        factory: BlockEntitySupplier<T>,
        block: Supplier<out Block>
    ): Supplier<BlockEntityType<T>> {
        return Registration.TILE_ENTITIES.register(
            name,
            Supplier { BlockEntityType.Builder.of(factory, block.get()).build(null) })
    }

    fun register() {
    }
}
