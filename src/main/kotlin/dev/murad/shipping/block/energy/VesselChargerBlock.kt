package dev.murad.shipping.block.energy

import dev.murad.shipping.block.dock.AbstractDockBlock
import dev.murad.shipping.block.dock.DockingBlockStates
import dev.murad.shipping.setup.ModBlocks
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
import java.util.*
import java.util.stream.Stream

class VesselChargerBlock(p_i48440_1_: Properties?) : Block(p_i48440_1_), EntityBlock {
    override fun useItemOn(
        pStack: ItemStack,
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pPlayer: Player,
        pHand: InteractionHand,
        pHitResult: BlockHitResult
    ): ItemInteractionResult {
        if (!pLevel.isClientSide) {
            val entity = pLevel.getBlockEntity(pPos)
            if (entity is VesselChargerTileEntity) {
                entity.use(pPlayer, pHand)
                return ItemInteractionResult.CONSUME
            }
        }
        return ItemInteractionResult.SUCCESS
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return ModTileEntitiesTypes.VESSEL_CHARGER.get().create(pos, state)
    }

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

    fun getPlaceDir(pos: BlockPos, level: Level): Optional<Direction> {
        val below = level.getBlockState(pos.below())

        if (below.block is AbstractDockBlock) {
            return Optional.of(below.getValue(DockingBlockStates.FACING))
        }
        for (dir in listOf(Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH)) {
            val blockState = level.getBlockState(pos.relative(dir))
            if (blockState.`is`(ModBlocks.LOCOMOTIVE_DOCK_RAIL.get()) ||
                (blockState.`is`(ModBlocks.CAR_DOCK_RAIL.get()) && !blockState.getValue(DockingBlockStates.INVERTED))
            ) {
                return Optional.of(dir)
            }
        }
        return Optional.empty()
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return defaultBlockState()
            .setValue(
                FACING, getPlaceDir(context.clickedPos, context.level)
                    .orElse(context.horizontalDirection.opposite)
            )
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (level.isClientSide()) null else TickerUtil.createTickerHelper(
            type,
            ModTileEntitiesTypes.VESSEL_CHARGER.get()
        ) { pLevel: Level?, pPos: BlockPos?, pState: BlockState?, e: VesselChargerTileEntity ->
            VesselChargerTileEntity.serverTick(
                pLevel,
                pPos,
                pState,
                e
            )
        }
    }

    companion object {
        
        val FACING: DirectionProperty = HorizontalDirectionalBlock.FACING
        private val SHAPE_N = Stream.of(
            box(3.0, 2.0, 3.0, 13.0, 13.0, 13.0),
            box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
            box(2.0, 13.0, 2.0, 14.0, 15.0, 14.0),
            box(7.0, 4.0, 0.0, 9.0, 6.0, 3.0),
            box(7.0, 4.0, 0.0, 9.0, 6.0, 3.0),
            box(6.0, 15.0, 6.0, 10.0, 16.0, 10.0)
        ).reduce { v1: VoxelShape?, v2: VoxelShape? -> Shapes.join(v1, v2, BooleanOp.OR) }.get()
        private val SHAPE_W = Stream.of(
            box(3.0, 2.0, 3.0, 13.0, 13.0, 13.0),
            box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
            box(2.0, 13.0, 2.0, 14.0, 15.0, 14.0),
            box(0.0, 4.0, 7.0, 3.0, 6.0, 9.0),
            box(0.0, 4.0, 7.0, 3.0, 6.0, 9.0),
            box(6.0, 15.0, 6.0, 10.0, 16.0, 10.0)
        ).reduce { v1: VoxelShape?, v2: VoxelShape? -> Shapes.join(v1, v2, BooleanOp.OR) }.get()
        private val SHAPE_E = Stream.of(
            box(3.0, 2.0, 3.0, 13.0, 13.0, 13.0),
            box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
            box(2.0, 13.0, 2.0, 14.0, 15.0, 14.0),
            box(13.0, 4.0, 7.0, 16.0, 6.0, 9.0),
            box(13.0, 4.0, 7.0, 16.0, 6.0, 9.0),
            box(6.0, 15.0, 6.0, 10.0, 16.0, 10.0)
        ).reduce { v1: VoxelShape?, v2: VoxelShape? -> Shapes.join(v1, v2, BooleanOp.OR) }.get()
        private val SHAPE_S = Stream.of(
            box(3.0, 2.0, 3.0, 13.0, 13.0, 13.0),
            box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
            box(2.0, 13.0, 2.0, 14.0, 15.0, 14.0),
            box(7.0, 4.0, 13.0, 9.0, 6.0, 16.0),
            box(7.0, 4.0, 13.0, 9.0, 6.0, 16.0),
            box(6.0, 15.0, 6.0, 10.0, 16.0, 10.0)
        ).reduce { v1: VoxelShape?, v2: VoxelShape? -> Shapes.join(v1, v2, BooleanOp.OR) }.get()
    }
}


