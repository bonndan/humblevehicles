package dev.murad.shipping.block.rail

import com.mojang.serialization.MapCodec
import dev.murad.shipping.util.RailHelper
import dev.murad.shipping.util.RailShapeUtil
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
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.level.block.state.properties.RailShape
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class JunctionRail(pProperties: Properties) : BaseRailBlock(true, pProperties), MultiShapeRail {
    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState? {
        val fluidstate = pContext.level.getFluidState(pContext.clickedPos)
        val flag = fluidstate.type === Fluids.WATER
        val blockstate = super.defaultBlockState()
        return blockstate
            .setValue(WATERLOGGED, flag)
            .setValue(RAIL_SHAPE, RailShapeUtil.DEFAULT)
    }

    override fun updateState(pState: BlockState, pLevel: Level, pPos: BlockPos, pIsMoving: Boolean): BlockState {
        return pState
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

    override fun canMakeSlopes(state: BlockState, world: BlockGetter, pos: BlockPos): Boolean {
        return false
    }

    override fun getRailDirection(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        cart: AbstractMinecart?
    ): RailShape {
        if (cart == null) {
            return state.getValue(shapeProperty)
        }

        return if (RailHelper.directionFromVelocity(cart.deltaMovement).axis === Direction.Axis.X) RailShape.EAST_WEST else RailShape.NORTH_SOUTH
    }

    public override fun rotate(pState: BlockState, pRot: Rotation): BlockState {
        return pState
    }

    public override fun mirror(pState: BlockState, pMirror: Mirror): BlockState {
        return pState
    }

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(pBuilder)
        pBuilder.add(WATERLOGGED, RAIL_SHAPE)
    }

    override fun setRailState(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        `in`: Direction,
        out: Direction
    ): Boolean {
        return `in`.axis.isHorizontal && `in`.opposite == out
    }

    override fun getPossibleOutputDirections(state: BlockState, inputSide: Direction): Set<Direction?>? {
        if (inputSide.axis.isHorizontal) {
            return java.util.Set.of(inputSide.opposite)
        }
        return NO_POSSIBILITIES
    }

    override fun getPriorityDirectionsToCheck(state: BlockState, entrance: Direction): Set<Direction?> {
        return if (entrance == Direction.EAST || entrance == Direction.WEST) {
            java.util.Set.of(
                Direction.NORTH,
                Direction.SOUTH
            )
        } else setOf<Direction>()
    }

    override fun getVanillaRailShapeFromDirection(
        state: BlockState,
        pos: BlockPos,
        level: Level,
        direction: Direction
    ): RailShape {
        return if (direction == Direction.EAST || direction == Direction.WEST) {
            RailShape.EAST_WEST
        } else RailShape.NORTH_SOUTH
    }

    override val isAutomaticSwitching: Boolean
        get() = false

    @Deprecated("")
    override fun isValidRailShape(shape: RailShape): Boolean {
        return RAIL_SHAPE.possibleValues.contains(shape)
    }

    companion object {
        val CODEC: MapCodec<JunctionRail?> = simpleCodec { pProperties: Properties ->
            JunctionRail(
                pProperties
            )
        }

        // for compatibilty issues
        val RAIL_SHAPE: EnumProperty<RailShape> = RailShapeUtil.RAIL_SHAPE_STRAIGHT_FLAT

        val NO_POSSIBILITIES: Set<Direction?> = setOf<Direction>()
    }
}
