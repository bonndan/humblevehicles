package com.github.bonndan.humblevehicles.setup

import com.github.bonndan.humblevehicles.block.fluid.FluidHopperTileEntity
import com.github.bonndan.humblevehicles.block.rail.blockentity.LocomotiveDockTileEntity
import com.github.bonndan.humblevehicles.block.rail.blockentity.TrainCarDockTileEntity
import com.github.bonndan.humblevehicles.setup.Registration.TILE_ENTITIES
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier
import net.minecraft.world.level.block.state.BlockState
import java.util.function.Supplier

object ModTileEntitiesTypes {


    val LOCOMOTIVE_DOCK: Supplier<BlockEntityType<LocomotiveDockTileEntity>> = TILE_ENTITIES.register(
        "locomotive_dock",
        supplier(
            ModBlocks.LOCOMOTIVE_DOCK_RAIL,
            BlockEntitySupplier { pos: BlockPos, state: BlockState -> LocomotiveDockTileEntity(pos, state) }
        )
    )

    val CAR_DOCK: Supplier<BlockEntityType<TrainCarDockTileEntity>> = TILE_ENTITIES.register(
        "car_dock",
        supplier(
            ModBlocks.CAR_DOCK_RAIL,
            BlockEntitySupplier { pos: BlockPos, state: BlockState -> TrainCarDockTileEntity(pos, state) }
        )
    )

    val FLUID_HOPPER: Supplier<BlockEntityType<FluidHopperTileEntity>> = TILE_ENTITIES.register(
        "fluid_hopper",
        supplier(
            ModBlocks.FLUID_HOPPER,
            BlockEntitySupplier { pos: BlockPos, state: BlockState -> FluidHopperTileEntity(pos, state) }
        )
    )

    fun register() {
    }

    private fun <T : BlockEntity> supplier(
        block: Supplier<Block>,
        supplier: BlockEntitySupplier<T>
    ): Supplier<BlockEntityType<T>> =
        Supplier<BlockEntityType<T>> { BlockEntityType<T>(supplier, block.get()) }
}
