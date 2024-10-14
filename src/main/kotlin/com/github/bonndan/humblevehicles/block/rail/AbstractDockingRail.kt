package com.github.bonndan.humblevehicles.block.rail

import com.github.bonndan.humblevehicles.block.dock.DockingBlockStates.fixHopperPos
import com.github.bonndan.humblevehicles.setup.ModBlocks
import com.github.bonndan.humblevehicles.util.RailShapeUtil
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.BaseRailBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.level.block.state.properties.RailShape
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

abstract class AbstractDockingRail  // facing denotes direction of straight out
protected constructor(pProperties: Properties) : BaseRailBlock(true, pProperties), EntityBlock {

    override fun updateState(pState: BlockState, pLevel: Level, pPos: BlockPos, pIsMoving: Boolean): BlockState {
        return pState
    }

    @Deprecated("")
    override fun getShapeProperty(): Property<RailShape> {
        return RAIL_SHAPE
    }

    public override fun getShape(
        pState: BlockState,
        pLevel: BlockGetter,
        pPos: BlockPos,
        pContext: CollisionContext
    ): VoxelShape {
        return FLAT_AABB
    }

    override fun canMakeSlopes(state: BlockState, world: BlockGetter, pos: BlockPos): Boolean {
        return false
    }


    protected fun getRailShapeFromFacing(facing: Direction): RailShape {
        return if (facing.axis === Direction.Axis.X) RailShape.EAST_WEST else RailShape.NORTH_SOUTH
    }

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState? {
        val fluidstate = pContext.level.getFluidState(pContext.clickedPos)
        val flag = fluidstate.type === Fluids.WATER
        val blockstate = super.defaultBlockState()
        return blockstate
            .setValue(RAIL_SHAPE, getRailShapeFromFacing(pContext.horizontalDirection))
            .setValue(WATERLOGGED, flag)
    }

    public override fun canSurvive(pState: BlockState, pLevel: LevelReader, pPos: BlockPos): Boolean {
        return if (pLevel.getBlockState(pPos.below()).`is`(ModBlocks.FLUID_HOPPER.get())) {
            true
        } else super.canSurvive(pState, pLevel, pPos)
    }

    public override fun neighborChanged(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pBlock: Block,
        pFromPos: BlockPos,
        pIsMoving: Boolean
    ) {
        if (!pLevel.isClientSide && pLevel.getBlockState(pPos).`is`(this)) {
            if (!canSurvive(pState, pLevel, pPos)) {
                dropResources(pState, pLevel, pPos)
                pLevel.removeBlock(pPos, pIsMoving)
            } else {
                fixHopperPos(pState, pLevel, pPos)
                this.updateState(pState, pLevel, pPos, pBlock)
            }
        }
    }

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(pBuilder)
        pBuilder.add(WATERLOGGED, RAIL_SHAPE)
    }

    companion object {

        val RAIL_SHAPE: EnumProperty<RailShape> = RailShapeUtil.RAIL_SHAPE_STRAIGHT_FLAT

        fun fixHopperPos(state: BlockState, level: Level?, pos: BlockPos?) {
            val dirs = if (state.getValue(RAIL_SHAPE) == RailShape.EAST_WEST) {
                listOf(Direction.NORTH, Direction.SOUTH)
            } else {
                listOf(Direction.EAST, Direction.WEST)
            }

            dirs.forEach { direction -> fixHopperPos(level!!, pos!!, direction, direction.opposite) }
        }
    }
}
