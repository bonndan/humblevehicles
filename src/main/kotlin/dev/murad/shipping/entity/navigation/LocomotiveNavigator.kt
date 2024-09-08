package dev.murad.shipping.entity.navigation

import com.mojang.datafixers.util.Pair
import dev.murad.shipping.block.rail.MultiShapeRail
import dev.murad.shipping.entity.custom.train.locomotive.AbstractLocomotiveEntity
import dev.murad.shipping.util.LocoRoute
import dev.murad.shipping.util.LocoRouteNode
import dev.murad.shipping.util.RailHelper
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.IntArrayTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import java.util.*
import java.util.stream.Collectors

class LocomotiveNavigator(private val locomotive: AbstractLocomotiveEntity) {
    private val routeNodes: MutableSet<BlockPos> = HashSet()

    private val visitedNodes: MutableSet<BlockPos> = HashSet()

    private val decisionCache =
        HashMap<BlockPos, Direction>()

    val routeSize: Int
        get() = routeNodes.size

    val visitedSize: Int
        get() = visitedNodes.size


    private fun reset() {
        visitedNodes.clear()
        routeNodes.clear()
        decisionCache.clear()
    }

    init {
        reset()
    }

    private fun getDirectionFromHorizontalOffset(x: Int, z: Int): Optional<Direction> {
        if (x > 0) return Optional.of(Direction.EAST)
        if (x < 0) return Optional.of(Direction.WEST)
        if (z > 0) return Optional.of(Direction.SOUTH)
        if (z < 0) return Optional.of(Direction.NORTH)
        return Optional.empty()
    }

    fun serverTick() {
        RailHelper.getRail(locomotive.onPos.above(), locomotive.level()).ifPresent { railPos: BlockPos ->
            if (routeNodes.contains(railPos)) {
                visitedNodes.add(railPos)
            }
            if (visitedNodes.size == routeNodes.size) {
                visitedNodes.clear()
            }
            decisionCache.remove(railPos)

            // guaranteed not null on serverside
            val oldHorizontalBlockPos = locomotive.oldHorizontalBlockPos
            val blockPos = locomotive.getBlockPos()

            // figure out direction the locomotive came from.
            val offset = blockPos.offset(oldHorizontalBlockPos!!.multiply(-1))
            val moveDirOpt =
                getDirectionFromHorizontalOffset(offset.x, offset.z)
            val moveDir = moveDirOpt.orElse(locomotive.direction)

            val railHelper = RailHelper(locomotive)
            railHelper.getNext(railPos, moveDir)
                .ifPresent { pair: Pair<BlockPos, Direction> ->
                    val nextRail = pair.first
                    val prevExitTaken = pair.second
                    val state = locomotive.level().getBlockState(nextRail)
                    val block = state.block
                    if (block is MultiShapeRail && block.isAutomaticSwitching) {
                        val choices = block.getPossibleOutputDirections(state, prevExitTaken.opposite).toList()
                        if (choices.size == 1) {
                            block.setRailState(state, locomotive.level(), nextRail, prevExitTaken.opposite, choices[0])
                        } else if (choices.size > 1 && !routeNodes.isEmpty()) {
                            val potential: MutableSet<BlockPos> = HashSet(routeNodes)
                            potential.removeAll(visitedNodes)
                            if (!decisionCache.containsKey(nextRail)) {
                                val decision = railHelper.pickCheaperDir(
                                    choices,
                                    nextRail,
                                    RailHelper.samePositionHeuristicSet(potential),
                                    locomotive.level()
                                )
                                decisionCache[nextRail] = decision
                            }

                            block.setRailState(
                                state,
                                locomotive.level(),
                                nextRail,
                                prevExitTaken.opposite,
                                decisionCache[nextRail]!!
                            )
                        }
                    }
                }
        }
    }

    fun updateWithLocoRouteItem(route: LocoRoute) {
        val newRouteNodes: MutableSet<BlockPos> =
            route.stream().map { obj -> obj?.toBlockPos() }
                .collect(Collectors.toSet())
        if (newRouteNodes == routeNodes) return

        reset()
        routeNodes.addAll(newRouteNodes.toList())
    }

    fun loadFromNbt(tag: CompoundTag?) {
        reset()
        if (tag == null) return

        // list of intarrays (type 11)
        routeNodes.addAll(convertTagToSet(tag.getList(ROUTE_TAG, Tag.TAG_INT_ARRAY.toInt())))
        visitedNodes.addAll(convertTagToSet(tag.getList(VISITED_TAG, Tag.TAG_INT_ARRAY.toInt())))
    }

    fun saveToNbt(): CompoundTag {
        val tag = CompoundTag()
        tag.put(ROUTE_TAG, convertSetToTag(routeNodes))
        tag.put(VISITED_TAG, convertSetToTag(visitedNodes))
        return tag
    }

    companion object {
        private const val ROUTE_TAG = "route"
        private const val VISITED_TAG = "visited"

        private fun convertTagToSet(tag: ListTag?): Set<BlockPos> {
            if (tag == null) return HashSet()
            val set = HashSet<BlockPos>()

            for (i in tag.indices) {
                val pos = tag.getIntArray(i)
                if (pos.size != 3) continue
                set.add(BlockPos(pos[0], pos[1], pos[2]))
            }
            return set
        }

        private fun convertSetToTag(set: Set<BlockPos>): ListTag {
            val tag = ListTag()
            for (pos in set) {
                tag.add(IntArrayTag(java.util.List.of(pos.x, pos.y, pos.z)))
            }
            return tag
        }
    }
}
