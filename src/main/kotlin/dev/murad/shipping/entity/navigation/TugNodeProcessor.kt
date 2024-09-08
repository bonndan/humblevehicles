package dev.murad.shipping.entity.navigation

import dev.murad.shipping.block.guiderail.TugGuideRailBlock.Companion.getArrowsDirection
import dev.murad.shipping.setup.ModBlocks
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction
import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.core.Direction
import net.minecraft.tags.FluidTags
import net.minecraft.util.Mth
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.pathfinder.*
import net.minecraft.world.level.pathfinder.Target
import java.util.*

class TugNodeProcessor(private val level: Level) : SwimNodeEvaluator(false) {
    private fun isOppositeGuideRail(Node: Node, direction: Direction): Boolean {
        val state = level.getBlockState(Node.asBlockPos().below())
        if (state.`is`(ModBlocks.GUIDE_RAIL_TUG.get())) {
            return getArrowsDirection(state).opposite == direction
        }
        return false
    }

    override fun getNeighbors(p_222859_1_: Array<Node>, p_222859_2_: Node): Int {
        var i = 0

        for (direction in Arrays.asList<Direction>(Direction.WEST, Direction.EAST, Direction.SOUTH, Direction.NORTH)) {
            val Node = this.getWaterNode(
                p_222859_2_.x + direction.stepX,
                p_222859_2_.y + direction.stepY,
                p_222859_2_.z + direction.stepZ
            )
            if (Node != null && !Node.closed && !isOppositeGuideRail(Node, direction)) {
                p_222859_1_[i++] = Node
            }
        }

        return i
    }

    private fun getNodeSimple(p_176159_1_: Int, p_176159_2_: Int, p_176159_3_: Int): Node {
        return nodes.computeIfAbsent(
            Node.createHash(p_176159_1_, p_176159_2_, p_176159_3_),
            Int2ObjectFunction { p_215743_3_: Int -> Node(p_176159_1_, p_176159_2_, p_176159_3_) })
    }

    override fun getTarget(pX: Double, pY: Double, pZ: Double): Target {
        return Target(getNodeSimple(Mth.floor(pX), Mth.floor(pY), Mth.floor(pZ)))
    }

    override fun getNode(p_176159_1_: Int, p_176159_2_: Int, p_176159_3_: Int): Node {
        val Node = super.getNode(p_176159_1_, p_176159_2_, p_176159_3_)
        if (Node != null) {
            val pos = Node.asBlockPos()
            var penalty = 0f
            for (surr in Arrays.asList<BlockPos>(
                pos.east(),
                pos.west(),
                pos.south(),
                pos.north(),
                pos.north().west(),
                pos.north().east(),
                pos.south().east(),
                pos.south().west(),
                pos.north().west().north().west(),
                pos.north().east().north().east(),
                pos.south().west().south().west(),
                pos.south().east().south().east()
            )
            ) {
                // if the point's neighbour has land, penalty is 5 unless there is a dock
                if (!level.getFluidState(surr).`is`(Fluids.WATER)) {
                    penalty = 5f
                }
                if (level.getBlockState(surr).`is`(ModBlocks.GUIDE_RAIL_CORNER.get()) ||
                    level.getBlockState(surr).`is`(ModBlocks.BARGE_DOCK.get()) ||
                    level.getBlockState(surr).`is`(ModBlocks.TUG_DOCK.get())

                ) {
                    penalty = 0f
                    break
                }
            }
            Node.costMalus += penalty
        }


        return Node
    }

    private fun getWaterNode(p_186328_1_: Int, p_186328_2_: Int, p_186328_3_: Int): Node? {
        val pathType = this.isFree(p_186328_1_, p_186328_2_, p_186328_3_)
        return if (pathType != PathType.WATER) null else this.getNode(p_186328_1_, p_186328_2_, p_186328_3_)
    }

    private fun isFree(p_186327_1_: Int, p_186327_2_: Int, p_186327_3_: Int): PathType {
        val `blockpos$mutable` = MutableBlockPos()

        for (i in p_186327_1_ until p_186327_1_ + this.entityWidth) {
            for (j in p_186327_2_ until p_186327_2_ + this.entityHeight) {
                for (k in p_186327_3_ until p_186327_3_ + this.entityDepth) {
                    val fluidstate = level.getFluidState(`blockpos$mutable`.set(i, j, k))
                    val blockstate = level.getBlockState(`blockpos$mutable`.set(i, j, k))
                    if (fluidstate.isEmpty && blockstate.isPathfindable(PathComputationType.WATER) && blockstate.isAir) {
                        return PathType.BREACH
                    }

                    if (!fluidstate.`is`(FluidTags.WATER)) {
                        return PathType.BLOCKED
                    }
                }
            }
        }

        val blockstate1 = level.getBlockState(`blockpos$mutable`)
        return if (blockstate1.isPathfindable(PathComputationType.WATER)) PathType.WATER else PathType.BLOCKED
    }
}
