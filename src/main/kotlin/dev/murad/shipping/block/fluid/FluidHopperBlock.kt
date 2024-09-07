package dev.murad.shipping.block.fluid

import dev.murad.shipping.setup.ModTileEntitiesTypes
import dev.murad.shipping.util.TickerUtil
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.stream.Stream


class FluidHopperBlock(p_i48440_1_: Properties) : Block(p_i48440_1_),
    EntityBlock {
    var SHAPE_N: VoxelShape = Stream.of(
        box(0.0, 0.0, 3.0, 16.0, 1.0, 16.0),
        box(0.0, 12.0, 3.0, 16.0, 13.0, 16.0),
        box(6.5, 13.0, 8.0, 9.5, 15.0, 11.0),
        box(6.5, 13.0, 8.0, 9.5, 15.0, 11.0),
        box(0.0, 1.0, 12.0, 2.0, 12.0, 16.0),
        box(2.0, 1.0, 7.0, 2.0, 12.0, 12.0),
        box(14.0, 1.0, 7.0, 14.0, 12.0, 12.0),
        box(0.0, 1.0, 3.0, 3.0, 12.0, 7.0),
        box(14.0, 1.0, 12.0, 16.0, 12.0, 16.0),
        box(13.0, 1.0, 3.0, 16.0, 12.0, 7.0),
        box(3.0, 1.0, 3.0, 5.0, 12.0, 5.0),
        box(5.0, 1.0, 4.0, 11.0, 12.0, 4.0),
        box(5.0, 1.0, 15.0, 11.0, 12.0, 15.0),
        box(11.0, 1.0, 3.0, 13.0, 12.0, 5.0),
        box(11.0, 1.0, 14.0, 14.0, 12.0, 16.0),
        box(2.0, 1.0, 14.0, 5.0, 12.0, 16.0),
        box(6.5, 2.0, 0.0, 9.5, 5.0, 5.0),
        box(6.5, 2.0, 0.0, 9.5, 5.0, 5.0)
    ).reduce { v1: VoxelShape?, v2: VoxelShape? -> Shapes.join(v1, v2, BooleanOp.OR) }.get()

    var SHAPE_E: VoxelShape = Stream.of(
        box(0.0, 0.0, 0.0, 13.0, 1.0, 16.0),
        box(0.0, 12.0, 0.0, 13.0, 13.0, 16.0),
        box(5.0, 13.0, 6.5, 8.0, 15.0, 9.5),
        box(5.0, 13.0, 6.5, 8.0, 15.0, 9.5),
        box(0.0, 1.0, 0.0, 4.0, 12.0, 2.0),
        box(4.0, 1.0, 2.0, 9.0, 12.0, 2.0),
        box(4.0, 1.0, 14.0, 9.0, 12.0, 14.0),
        box(9.0, 1.0, 0.0, 13.0, 12.0, 3.0),
        box(0.0, 1.0, 14.0, 4.0, 12.0, 16.0),
        box(9.0, 1.0, 13.0, 13.0, 12.0, 16.0),
        box(11.0, 1.0, 3.0, 13.0, 12.0, 5.0),
        box(12.0, 1.0, 5.0, 12.0, 12.0, 11.0),
        box(1.0, 1.0, 5.0, 1.0, 12.0, 11.0),
        box(11.0, 1.0, 11.0, 13.0, 12.0, 13.0),
        box(0.0, 1.0, 11.0, 2.0, 12.0, 14.0),
        box(0.0, 1.0, 2.0, 2.0, 12.0, 5.0),
        box(11.0, 2.0, 6.5, 16.0, 5.0, 9.5),
        box(11.0, 2.0, 6.5, 16.0, 5.0, 9.5)
    ).reduce { v1: VoxelShape?, v2: VoxelShape? -> Shapes.join(v1, v2, BooleanOp.OR) }.get()

    var SHAPE_S: VoxelShape = Stream.of(
        box(0.0, 0.0, 0.0, 16.0, 1.0, 13.0),
        box(0.0, 12.0, 0.0, 16.0, 13.0, 13.0),
        box(6.5, 13.0, 5.0, 9.5, 15.0, 8.0),
        box(6.5, 13.0, 5.0, 9.5, 15.0, 8.0),
        box(14.0, 1.0, 0.0, 16.0, 12.0, 4.0),
        box(14.0, 1.0, 4.0, 14.0, 12.0, 9.0),
        box(2.0, 1.0, 4.0, 2.0, 12.0, 9.0),
        box(13.0, 1.0, 9.0, 16.0, 12.0, 13.0),
        box(0.0, 1.0, 0.0, 2.0, 12.0, 4.0),
        box(0.0, 1.0, 9.0, 3.0, 12.0, 13.0),
        box(11.0, 1.0, 11.0, 13.0, 12.0, 13.0),
        box(5.0, 1.0, 12.0, 11.0, 12.0, 12.0),
        box(5.0, 1.0, 1.0, 11.0, 12.0, 1.0),
        box(3.0, 1.0, 11.0, 5.0, 12.0, 13.0),
        box(2.0, 1.0, 0.0, 5.0, 12.0, 2.0),
        box(11.0, 1.0, 0.0, 14.0, 12.0, 2.0),
        box(6.5, 2.0, 11.0, 9.5, 5.0, 16.0),
        box(6.5, 2.0, 11.0, 9.5, 5.0, 16.0)
    ).reduce { v1: VoxelShape?, v2: VoxelShape? -> Shapes.join(v1, v2, BooleanOp.OR) }.get()

    var SHAPE_W: VoxelShape = Stream.of(
        box(3.0, 0.0, 0.0, 16.0, 1.0, 16.0),
        box(3.0, 12.0, 0.0, 16.0, 13.0, 16.0),
        box(8.0, 13.0, 6.5, 11.0, 15.0, 9.5),
        box(8.0, 13.0, 6.5, 11.0, 15.0, 9.5),
        box(12.0, 1.0, 14.0, 16.0, 12.0, 16.0),
        box(7.0, 1.0, 14.0, 12.0, 12.0, 14.0),
        box(7.0, 1.0, 2.0, 12.0, 12.0, 2.0),
        box(3.0, 1.0, 13.0, 7.0, 12.0, 16.0),
        box(12.0, 1.0, 0.0, 16.0, 12.0, 2.0),
        box(3.0, 1.0, 0.0, 7.0, 12.0, 3.0),
        box(3.0, 1.0, 11.0, 5.0, 12.0, 13.0),
        box(4.0, 1.0, 5.0, 4.0, 12.0, 11.0),
        box(15.0, 1.0, 5.0, 15.0, 12.0, 11.0),
        box(3.0, 1.0, 3.0, 5.0, 12.0, 5.0),
        box(14.0, 1.0, 2.0, 16.0, 12.0, 5.0),
        box(14.0, 1.0, 11.0, 16.0, 12.0, 14.0),
        box(0.0, 2.0, 6.5, 5.0, 5.0, 9.5),
        box(0.0, 2.0, 6.5, 5.0, 5.0, 9.5)
    ).reduce { v1: VoxelShape?, v2: VoxelShape? -> Shapes.join(v1, v2, BooleanOp.OR) }.get()

    @Suppress("deprecation")
    public override fun rotate(state: BlockState, rot: Rotation): BlockState {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)))
    }

    @Suppress("deprecation")
    public override fun mirror(state: BlockState, mirrorIn: Mirror): BlockState {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return defaultBlockState()
            .setValue(FACING, context.horizontalDirection.opposite)
    }

    public override fun getShape(
        p_220053_1_: BlockState,
        p_220053_2_: BlockGetter,
        p_220053_3_: BlockPos,
        p_220053_4_: CollisionContext
    ): VoxelShape {
        return when (p_220053_1_.getValue(FACING)) {
            Direction.SOUTH -> SHAPE_S
            Direction.WEST -> SHAPE_W
            Direction.EAST -> SHAPE_E
            Direction.NORTH -> SHAPE_N
            else -> SHAPE_N
        }
    }

    override fun useItemOn(
        pStack: ItemStack,
        pState: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        pHitResult: BlockHitResult
    ): ItemInteractionResult {
        if (!world.isClientSide) {
            val entity = world.getBlockEntity(pos)
            if (entity is FluidHopperTileEntity) {
                if (entity.use(player, hand)) {
                    return ItemInteractionResult.CONSUME
                }
            }
        }
        return ItemInteractionResult.SUCCESS
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return ModTileEntitiesTypes.FLUID_HOPPER.get().create(pos, state)
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (level.isClientSide()) null else TickerUtil.createTickerHelper(
            type,
            ModTileEntitiesTypes.FLUID_HOPPER.get()
        ) { level: Level?, blockPos: BlockPos, blockState: BlockState, entity: FluidHopperTileEntity ->
            FluidHopperTileEntity.serverTick(
                level,
                blockPos,
                blockState,
                entity
            )
        }
    }

    companion object {
        @JvmField
        val FACING: DirectionProperty = HorizontalDirectionalBlock.FACING
    }
}
