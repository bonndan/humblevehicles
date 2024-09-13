package dev.murad.shipping.block.rail

import com.mojang.serialization.MapCodec
import dev.murad.shipping.util.InteractionUtil
import dev.murad.shipping.util.RailShapeUtil
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.StringRepresentable
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.AbstractMinecart
import net.minecraft.world.item.ItemStack
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
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class SwitchRail : BaseRailBlock, MultiShapeRail {
    enum class OutDirection(private val serialName: String) : StringRepresentable {
        LEFT("left"), RIGHT("right");

        override fun getSerializedName(): String {
            return serialName
        }

        fun getOutDirection(inDirection: Direction): Direction {
            return if (this == RIGHT) inDirection.counterClockWise else inDirection.clockWise
        }

        fun opposite(): OutDirection {
            return if (this == LEFT) RIGHT else LEFT
        }
    }

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
            .setValue(OUT_DIRECTION, OutDirection.RIGHT)
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

    override fun canMakeSlopes(state: BlockState, world: BlockGetter, pos: BlockPos): Boolean {
        return false
    }

    private fun getRailConfiguration(state: BlockState): BranchingRailConfiguration {
        val out = state.getValue(OUT_DIRECTION)

        val unpoweredDirection = state.getValue(FACING)
        val rootDirection = unpoweredDirection.opposite
        val poweredDirection = out.getOutDirection(rootDirection)

        return BranchingRailConfiguration(rootDirection, unpoweredDirection, poweredDirection)
    }

    override fun getRailDirection(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        cart: AbstractMinecart?
    ): RailShape {
        val c = getRailConfiguration(state)
        return RailShapeUtil.getRailShape(
            c.rootDirection,
            if (state.getValue(POWERED)) c.poweredDirection else c.unpoweredDirection
        )
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
        return c.getPossibleDirections(inputSide, isAutomaticSwitching, powered)
    }

    override fun getPriorityDirectionsToCheck(state: BlockState, entrance: Direction): Set<Direction> {
        val c = getRailConfiguration(state)
        return if (entrance == c.poweredDirection) java.util.Set.of(c.unpoweredDirection) else setOf<Direction>()
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
        if (pMirror == Mirror.LEFT_RIGHT) return pState.setValue(
            OUT_DIRECTION, pState.getValue(
                OUT_DIRECTION
            ).opposite()
        )
        else if (pMirror == Mirror.FRONT_BACK) return rotate(
            pState, pMirror.getRotation(
                pState.getValue(
                    FACING
                )
            )
        )
        return pState
    }

    override fun useItemOn(
        pStack: ItemStack,
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pPlayer: Player,
        pHand: InteractionHand,
        pHitResult: BlockHitResult
    ): ItemInteractionResult {
        if (InteractionUtil.doConfigure(pPlayer, pHand)) {
            pLevel.setBlockAndUpdate(pPos, this.mirror(pState, Mirror.LEFT_RIGHT))
            return ItemInteractionResult.SUCCESS
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
    }


    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(pBuilder)
        pBuilder.add(WATERLOGGED, FACING, RAIL_SHAPE, OUT_DIRECTION, POWERED)
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

    override fun isValidRailShape(shape: RailShape): Boolean {
        return RAIL_SHAPE.possibleValues.contains(shape)
    }

    companion object {
        // for compatibilty issues
        val RAIL_SHAPE: EnumProperty<RailShape> = RailShapeUtil.RAIL_SHAPE_STRAIGHT_FLAT

        // facing denotes direction of straight out
        
        val FACING: EnumProperty<Direction> = BlockStateProperties.HORIZONTAL_FACING
        
        val OUT_DIRECTION: EnumProperty<OutDirection> = EnumProperty.create("out_direction", OutDirection::class.java)

        // is this rail track engaged?
        
        val POWERED: BooleanProperty = BlockStateProperties.POWERED

        val CODEC: MapCodec<SwitchRail?> = simpleCodec { pProperties: Properties ->
            SwitchRail(
                pProperties
            )
        }
    }
}
