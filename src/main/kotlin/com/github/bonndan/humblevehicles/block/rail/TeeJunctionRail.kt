package com.github.bonndan.humblevehicles.block.rail

import com.mojang.serialization.MapCodec
import com.github.bonndan.humblevehicles.util.RailShapeUtil
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.vehicle.AbstractMinecart
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseRailBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.*
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class TeeJunctionRail : BaseRailBlock, MultiShapeRail {
    override val isAutomaticSwitching: Boolean

    constructor(pProperties: Properties) : super(false, pProperties) {
        this.isAutomaticSwitching = false
    }

    constructor(pProperties: Properties, automaticSwitching: Boolean) : super(false, pProperties) {
        this.isAutomaticSwitching = automaticSwitching
    }

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState? {
        val fluidstate = pContext.level.getFluidState(pContext.clickedPos)
        val flag = fluidstate.type === Fluids.WATER
        val blockstate = super.defaultBlockState()
        return setFacing(blockstate, pContext.horizontalDirection)
            .setValue(WATERLOGGED, flag)
            .setValue(POWERED, !isAutomaticSwitching && pContext.level.hasNeighborSignal(pContext.clickedPos))
    }

    override fun updateState(pState: BlockState, pLevel: Level, pPos: BlockPos, pIsMoving: Boolean): BlockState {
        return pState
    }

    private fun getRailShapeFromFacing(facing: Direction): RailShape {
        return if (facing.axis === Direction.Axis.X) RailShape.EAST_WEST else RailShape.NORTH_SOUTH
    }

    fun setFacing(state: BlockState, facing: Direction): BlockState {
        return state
            .setValue(RAIL_SHAPE, getRailShapeFromFacing(facing))
            .setValue(FACING, facing)
    }

    @Deprecated("")
    override fun getShapeProperty(): Property<RailShape> {
        return RAIL_SHAPE
    }

    override fun codec(): MapCodec<out BaseRailBlock?> {
        return CODEC
    }

    public override fun getShape(
        pState: BlockState,
        pLevel: BlockGetter,
        pPos: BlockPos,
        pContext: CollisionContext
    ): VoxelShape {
        return FLAT_AABB
    }

    override fun getPriorityDirectionsToCheck(state: BlockState, entrance: Direction): Set<Direction> {
        val c = getRailConfiguration(state)
        return if (entrance == c.poweredDirection) java.util.Set.of(c.unpoweredDirection) else setOf<Direction>()
    }

    override fun canMakeSlopes(state: BlockState, world: BlockGetter, pos: BlockPos): Boolean {
        return false
    }

    private fun getRailConfiguration(state: BlockState): BranchingRailConfiguration {
        val facing = state.getValue(FACING)
        val unpoweredDirection = facing.clockWise
        val poweredDirection = facing.counterClockWise
        val rootDirection = facing.opposite

        return BranchingRailConfiguration(rootDirection, unpoweredDirection, poweredDirection)
    }

    override fun getRailDirection(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        cart: AbstractMinecart?
    ): RailShape {
        val c = getRailConfiguration(state)
        val outDirection = if (state.getValue(POWERED)) c.poweredDirection else c.unpoweredDirection
        return RailShapeUtil.getRailShape(c.rootDirection, outDirection)
    }

    override fun setRailState(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        `in`: Direction,
        out: Direction
    ): Boolean {
        val c = getRailConfiguration(state)
        val possibilities = getPossibleOutputDirections(state, `in`)

        if (!isAutomaticSwitching) {
            return possibilities!!.contains(out)
        }

        if (!possibilities!!.contains(out)) return false

        if (`in` == c.rootDirection) {
            if (out == c.poweredDirection) {
                world.setBlock(pos, state.setValue(POWERED, true), 2)
                return true
            } else if (out == c.unpoweredDirection) {
                world.setBlock(pos, state.setValue(POWERED, false), 2)
                return true
            }
            return false
        }

        if (`in` == c.unpoweredDirection && out == c.rootDirection) {
            world.setBlock(pos, state.setValue(POWERED, false), 2)
            return true
        }

        if (`in` == c.poweredDirection && out == c.rootDirection) {
            world.setBlock(pos, state.setValue(POWERED, true), 2)
            return true
        }

        return false
    }

    override fun getPossibleOutputDirections(state: BlockState, inputSide: Direction): Set<Direction> {
        val c = getRailConfiguration(state)
        val powered = state.getValue(POWERED)
        val poss = c.getPossibleDirections(
            inputSide,
            isAutomaticSwitching, powered
        )
        return poss
    }

    override fun getVanillaRailShapeFromDirection(
        state: BlockState,
        pos: BlockPos,
        level: Level,
        direction: Direction
    ): RailShape {
        return getRailDirection(state, level, pos, null)
    }

    public override fun rotate(pState: BlockState, pRot: Rotation): BlockState {
        return setFacing(pState, pRot.rotate(pState.getValue(FACING)))
    }

    public override fun mirror(pState: BlockState, pMirror: Mirror): BlockState {
        return pState
    }

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(pBuilder)
        pBuilder.add(WATERLOGGED, FACING, RAIL_SHAPE, POWERED)
    }

    public override fun neighborChanged(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        p_220069_4_: Block,
        p_220069_5_: BlockPos,
        p_220069_6_: Boolean
    ) {
        super.neighborChanged(state, world, pos, p_220069_4_, p_220069_5_, p_220069_6_)
        if (isAutomaticSwitching) return

        if (!world.isClientSide) {
            val flag = state.getValue(POWERED)
            if (flag != world.hasNeighborSignal(pos)) {
                world.setBlock(pos, state.cycle(POWERED), 2)
            }
        }
    }

    override fun canConnectRedstone(state: BlockState, world: BlockGetter, pos: BlockPos, side: Direction?): Boolean {
        return true
    }

    @Deprecated("")
    override fun isValidRailShape(shape: RailShape): Boolean {
        return RAIL_SHAPE.possibleValues.contains(shape)
    }

    companion object {
        // for compatibility issues
        val RAIL_SHAPE: EnumProperty<RailShape> = RailShapeUtil.RAIL_SHAPE_STRAIGHT_FLAT

        // facing denotes direction of straight out
        val FACING: EnumProperty<Direction> = BlockStateProperties.HORIZONTAL_FACING

        // moving right is default non-powered direction
        val POWERED: BooleanProperty = BlockStateProperties.POWERED

        val CODEC: MapCodec<TeeJunctionRail?> = simpleCodec { pProperties: Properties ->
            TeeJunctionRail(
                pProperties
            )
        }
    }
}