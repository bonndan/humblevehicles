package dev.murad.shipping.block.dock

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.HopperBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.HopperBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty
import java.util.*

object DockingBlockStates {
    @JvmField
    val INVERTED: BooleanProperty = BlockStateProperties.INVERTED
    @JvmField
    val FACING: DirectionProperty = HorizontalDirectionalBlock.FACING
    @JvmField
    val POWERED: BooleanProperty = BlockStateProperties.POWERED

    fun getTileEntity(world: Level, pos: BlockPos?): Optional<AbstractDockTileEntity<*>> {
        val tileEntity = world.getBlockEntity(pos)
        return if (tileEntity is AbstractDockTileEntity<*>) Optional.of(tileEntity)
        else Optional.empty()
    }

    @JvmStatic
    fun fixHopperPos(
        state: BlockState?,
        world: Level,
        p_220069_3_: BlockPos,
        targetLoc: Direction?,
        targetDir: Direction
    ) {
        getTileEntity(world, p_220069_3_)
            .filter { obj -> isImport(obj) }
            .flatMap { dock: AbstractDockTileEntity<*> -> dock.getHopper(p_220069_3_.relative(targetLoc)) }
            .ifPresent { te: HopperBlockEntity ->
                if (te.blockState.getValue(HopperBlock.FACING) == Direction.DOWN) {
                    world.setBlock(te.blockPos, te.blockState.setValue(HopperBlock.FACING, targetDir), 2)
                }
            }
    }

    private fun isImport(dock: AbstractDockTileEntity<*>): Boolean {
        return if (dock is AbstractTailDockTileEntity<*>) {
            !dock.getBlockState().getValue(INVERTED)
        } else true
    }
}
