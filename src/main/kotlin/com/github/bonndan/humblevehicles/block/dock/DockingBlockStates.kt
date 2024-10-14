package com.github.bonndan.humblevehicles.block.dock

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.HopperBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.HopperBlockEntity
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty
import java.util.*

object DockingBlockStates {

    val INVERTED: BooleanProperty = BlockStateProperties.INVERTED

    val FACING: DirectionProperty = HorizontalDirectionalBlock.FACING

    val POWERED: BooleanProperty = BlockStateProperties.POWERED

    fun fixHopperPos(world: Level, blockPos: BlockPos, targetLoc: Direction, targetDir: Direction) {
        getTileEntity(world, blockPos)
            .filter { obj -> isImport(obj) }
            .flatMap { dock: AbstractDockTileEntity<*> -> dock.getHopperAt(blockPos.relative(targetLoc)) }
            .ifPresent { te: HopperBlockEntity ->
                if (te.blockState.getValue(HopperBlock.FACING) == Direction.DOWN) {
                    world.setBlock(te.blockPos, te.blockState.setValue(HopperBlock.FACING, targetDir), 2)
                }
            }
    }

    private fun getTileEntity(world: Level, pos: BlockPos): Optional<AbstractDockTileEntity<*>> {
        val tileEntity = world.getBlockEntity(pos)
        return if (tileEntity is AbstractDockTileEntity<*>) Optional.of(tileEntity)
        else Optional.empty()
    }

    private fun isImport(dock: AbstractDockTileEntity<*>): Boolean =
        if (dock is AbstractTailDockTileEntity<*>) !dock.getBlockState().getValue(INVERTED) else true
}
