package com.github.bonndan.humblevehicles.block.vesseldetector

import com.github.bonndan.humblevehicles.setup.ModTileEntitiesTypes
import com.github.bonndan.humblevehicles.util.MathUtil
import com.github.bonndan.humblevehicles.util.TickerUtil
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import org.joml.Vector3f

class VesselDetectorBlock(properties: Properties) : Block(properties), EntityBlock {

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return ModTileEntitiesTypes.VESSEL_DETECTOR.get().create(pos, state)
    }

    override fun canConnectRedstone(state: BlockState, world: BlockGetter, pos: BlockPos, side: Direction?): Boolean {
        return state.getValue(FACING) == side
    }

    public override fun getSignal(
        state: BlockState,
        reader: BlockGetter,
        blockPos: BlockPos,
        direction: Direction
    ): Int {
        return if (state.getValue(POWERED) && direction == state.getValue(FACING)) 15 else 0
    }


    public override fun getDirectSignal(
        state: BlockState,
        reader: BlockGetter,
        blockPos: BlockPos,
        direction: Direction
    ): Int {
        return if (state.getValue(POWERED) && direction == state.getValue(FACING)) 15 else 0
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
        builder.add(POWERED)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return defaultBlockState()
            .setValue(FACING, context.nearestLookingDirection.opposite)
            .setValue(POWERED, false)
    }

    // client only
    private fun showParticles(pos: BlockPos, state: BlockState, level: Level) {
        val bb: AABB = VesselDetectorTileEntity.getSearchBox(pos, state.getValue(FACING), level)
        val edges = MathUtil.getEdges(bb)

        for (edge in edges) {
            val from = edge.first
            val to = edge.second
            for (i in 0..9) {
                val pPos = MathUtil.lerp(from, to, (i.toFloat() / 10).toDouble())
                level.addParticle(PARTICLE, pPos.x, pPos.y, pPos.z, 0.0, 0.0, 0.0)
            }
        }
    }

    override fun useWithoutItem(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pPlayer: Player,
        pHitResult: BlockHitResult
    ): InteractionResult {
        if (pLevel.isClientSide()) {
            showParticles(pPos, pState, pPlayer.level())
        }

        return InteractionResult.SUCCESS
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (level.isClientSide()) null else TickerUtil.createTickerHelper(
            type,
            ModTileEntitiesTypes.VESSEL_DETECTOR.get()
        ) { pLevel, pPos, pState, e -> VesselDetectorTileEntity.serverTick(pLevel, pPos, pState, e) }
    }

    companion object {

        val POWERED: BooleanProperty = BlockStateProperties.POWERED

        val FACING: DirectionProperty = BlockStateProperties.FACING

        private val PARTICLE = DustParticleOptions(Vector3f(0.9f, 0.65f, 0.2f), 1.0f)
    }
}
