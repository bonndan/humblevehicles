package dev.murad.shipping.block.guiderail

import dev.murad.shipping.entity.custom.vessel.VesselEntity
import dev.murad.shipping.entity.custom.vessel.barge.AbstractBargeEntity
import dev.murad.shipping.util.InteractionUtil
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class CornerGuideRailBlock(properties: Properties) : Block(properties) {
    override fun useItemOn(
        pStack: ItemStack,
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        pHitResult: BlockHitResult
    ): ItemInteractionResult {
        if (InteractionUtil.doConfigure(player, hand)) {
            world.setBlockAndUpdate(pos, state.setValue(INVERTED, !state.getValue(INVERTED)))
            return ItemInteractionResult.SUCCESS
        }

        return super.useItemOn(pStack, state, world, pos, player, hand, pHitResult)
    }

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
        builder.add(INVERTED)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return defaultBlockState()
            .setValue(FACING, context.horizontalDirection.opposite)
            .setValue(INVERTED, false)
    }

    public override fun entityInside(state: BlockState, level: Level, pos: BlockPos, entity: Entity) {
        val facing = state.getValue(FACING)
        if (entity.direction != facing.opposite || entity !is VesselEntity) {
            return
        }

        val arrows = getArrowsDirection(state)
        val modifier = if (entity is AbstractBargeEntity) 0.2 else 0.1
        entity.setDeltaMovement(
            entity.getDeltaMovement().add(
                Vec3(
                    (facing.opposite.stepX + arrows.stepX) * modifier,
                    0.0,
                    (facing.opposite.stepZ + arrows.stepZ) * modifier
                )
            )
        )
    }

    public override fun getCollisionShape(
        p_220071_1_: BlockState,
        p_220071_2_: BlockGetter,
        p_220071_3_: BlockPos,
        p_220071_4_: CollisionContext
    ): VoxelShape {
        return SHAPE
    }

    companion object {
        protected val SHAPE: VoxelShape = box(1.0, 0.0, 1.0, 15.0, 15.0, 15.0)

        @JvmField
        val FACING: DirectionProperty = HorizontalDirectionalBlock.FACING
        @JvmField
        val INVERTED: BooleanProperty = BlockStateProperties.INVERTED


        fun getArrowsDirection(state: BlockState): Direction {
            val facing = state.getValue(FACING)
            return if (state.getValue(INVERTED)) facing.clockWise else facing.counterClockWise
        }
    }
}
