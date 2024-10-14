package com.github.bonndan.humblevehicles.block.vesseldetector

import com.github.bonndan.humblevehicles.entity.custom.train.AbstractTrainCarEntity
import com.github.bonndan.humblevehicles.entity.custom.vessel.VesselEntity
import com.github.bonndan.humblevehicles.setup.ModTileEntitiesTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.tags.BlockTags
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB

class VesselDetectorTileEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(ModTileEntitiesTypes.VESSEL_DETECTOR.get(), pos, state) {

    private var cooldown = 0

    private fun checkForVessel() {
        val direction = blockState.getValue(VesselDetectorBlock.FACING)
        val found = level!!.getEntities(null as Entity?, getSearchBox(this.blockPos, direction, level!!))
        { e: Entity? -> e is VesselEntity || e is AbstractTrainCarEntity }.isNotEmpty()
        val previousPowered = blockState.getValue(VesselDetectorBlock.POWERED)

        blockState.setValue(VesselDetectorBlock.POWERED, found)
        level!!.setBlockAndUpdate(
            blockPos,
            blockState.setValue(VesselDetectorBlock.POWERED, found)
        )

        if (found != previousPowered) {
            // update back neighbour
            val neighbour = blockPos.relative(direction.opposite)
            val block = blockState.block
            level!!.neighborChanged(neighbour, block, blockPos)
            level!!.updateNeighborsAtExceptFromFacing(neighbour, block, direction)
        }
    }

    fun serverTickInternal() {
        if (cooldown < 0) {
            cooldown = 10
            checkForVessel()
        } else {
            cooldown--
        }
    }

    companion object {
        private const val MAX_RANGE = 3
        private fun isValidBlock(state: BlockState): Boolean {
            return state.`is`(Blocks.WATER) || state.`is`(Blocks.AIR) || state.`is`(BlockTags.RAILS)
        }

        private fun getSearchLimit(pos: BlockPos, direction: Direction, level: Level): Int {
            var pos = pos
            var i = 0
            while (i < MAX_RANGE && isValidBlock(level.getBlockState(pos))) {
                pos = pos.relative(direction)
                i++
            }
            return i
        }

        fun getSearchBox(pos: BlockPos, direction: Direction, level: Level): AABB {
            val searchLimit = getSearchLimit(pos.relative(direction), direction, level)

            val posNeg = direction.axisDirection
            val start = if (posNeg == Direction.AxisDirection.POSITIVE) pos.relative(direction) else pos

            val offX = if (direction.stepX == 0) 1 else direction.stepX * searchLimit
            val offY = if (direction.stepY == 0) 1 else direction.stepY * searchLimit
            val offZ = if (direction.stepZ == 0) 1 else direction.stepZ * searchLimit

            val end = start.offset(offX, offY, offZ)

            return AABB(start.center, end.center) //TODO
        }

        fun serverTick(pLevel: Level?, pPos: BlockPos?, pState: BlockState?, e: VesselDetectorTileEntity) {
            e.serverTickInternal()
        }
    }
}
