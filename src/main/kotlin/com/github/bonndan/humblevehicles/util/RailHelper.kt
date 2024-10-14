package com.github.bonndan.humblevehicles.util

import com.google.common.collect.Maps
import com.mojang.datafixers.util.Pair
import com.github.bonndan.humblevehicles.block.rail.MultiShapeRail
import com.github.bonndan.humblevehicles.entity.custom.train.AbstractTrainCarEntity
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.world.entity.vehicle.AbstractMinecart
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseRailBlock
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.properties.RailShape
import net.minecraft.world.phys.Vec3
import java.util.*
import java.util.function.BiPredicate
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collectors
import kotlin.collections.HashSet
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.set
import kotlin.math.abs

class RailHelper(private val minecart: AbstractMinecart) {

    class RailDir {
        var horizontal: Direction
        var above: Boolean

        internal constructor(h: Direction, v: Boolean) {
            horizontal = h
            above = v
        }

        internal constructor(h: Direction) {
            horizontal = h
            above = false
        }
    }

    fun getShape(pos: BlockPos): RailShape {
        val state = minecart.level().getBlockState(pos)
        return (state.block as BaseRailBlock).getRailDirection(state, minecart.level(), pos, minecart)
    }

    fun getShape(pos: BlockPos, direction: Direction): RailShape {
        val state = minecart.level().getBlockState(pos)
        return if (state.block is MultiShapeRail) {
            (state.block as MultiShapeRail).getVanillaRailShapeFromDirection(state, pos, minecart.level(), direction)
        } else {
            (state.block as BaseRailBlock).getRailDirection(state, minecart.level(), pos, minecart)
        }
    }


    fun traverseBi(
        railPos: BlockPos,
        predicate: BiPredicate<Direction, BlockPos>,
        limit: Int,
        car: AbstractTrainCarEntity
    ): Optional<Pair<Direction, Int>> {

        return getRail(railPos, minecart.level()).flatMap { pos: BlockPos ->
            val shape = getShape(pos, car.direction.opposite)
            val dirs = EXITS_DIRECTION[shape]!!
            val first = traverse(pos, minecart.level(), dirs.second.horizontal.opposite, predicate, limit)
            val second = traverse(pos, minecart.level(), dirs.first.horizontal.opposite, predicate, limit)
            if (second.isEmpty) {
                return@flatMap first.map<Pair<Direction, Int>> { i: Int ->
                    Pair.of<Direction, Int>(
                        dirs.first.horizontal,
                        i
                    )
                }
            } else if (first.isEmpty) {
                return@flatMap second.map<Pair<Direction, Int>> { i: Int ->
                    Pair.of<Direction, Int>(
                        dirs.second.horizontal,
                        i
                    )
                }
            } else {
                return@flatMap Optional.of<Pair<Direction, Int>>(
                    if (first.get() < second.get()) Pair.of<Direction, Int>(
                        dirs.first.horizontal,
                        first.get()
                    ) else Pair.of<Direction, Int>(
                        dirs.second.horizontal,
                        second.get()
                    )
                )
            }
        }
    }

    fun traverse(
        railPos: BlockPos,
        level: Level,
        prevExitTaken: Direction,
        predicate: BiPredicate<Direction, BlockPos>,
        limit: Int
    ): Optional<Int> {
        if (predicate.test(prevExitTaken, railPos)) {
            return Optional.of(0)
        } else if (limit < 1) {
            return Optional.empty()
        }
        val entrance = prevExitTaken.opposite
        return getRail(railPos, level)
            .flatMap { pos ->
                val shape = getShape(pos, prevExitTaken)
                getOtherExit(entrance, shape).flatMap { raildir: RailDir ->
                    traverse(
                        if (raildir.above) pos.relative(raildir.horizontal).above()
                        else pos.relative(raildir.horizontal), level, raildir.horizontal, predicate, limit - 1
                    ).map { ans: Int -> ans + 1 }
                }
            }
    }

    fun getNext(railpos: BlockPos, direction: Direction): Optional<Pair<BlockPos, Direction>> {
        val shape = getShape(railpos, direction)
        val entrance = direction.opposite

        return getOtherExit(entrance, shape)
            .flatMap { raildir: RailDir ->
                getRail(
                    if (raildir.above) railpos.relative(raildir.horizontal)
                        .above() else railpos.relative(raildir.horizontal), minecart.level()
                )
                    .map { pos: BlockPos ->
                        Pair.of(
                            pos,
                            raildir.horizontal
                        )
                    }
            }
    }

    class RailPathFindNode(
        var pos: BlockPos,
        var prevExitTaken: Direction,
        var pathLength: Int, var heuristicValue: Double
    ) : Comparable<RailPathFindNode> {
        override fun compareTo(o: RailPathFindNode): Int {
            return if (this.heuristicValue == o.heuristicValue) {
                pathLength - o.pathLength
            } else if (this.heuristicValue - o.heuristicValue < 0) -1 else 1
        }
    }

    /**
     * @param prevExitTaken the direction of travel for the train
     */
    private fun getNextNodes(pos: BlockPos, prevExitTaken: Direction): List<RailDir?> {
        val inputSide = prevExitTaken.opposite

        // todo: we need to check if blocks are actually loaded
        val state = minecart.level().getBlockState(pos)
        if (state.block is MultiShapeRail) {
            // if rail is a MultiShapeRail, return all possible outputs from the input side
            // it doesn't matter if this rail is automatically switching.
            val rail = state.block as MultiShapeRail
            return rail.getPossibleOutputDirections(state, inputSide).map { h: Direction -> RailDir(h) }
        }

        val shape = getShape(pos, prevExitTaken)
        val shapes = java.util.List.of(shape)
        return shapes.stream().map<RailDir?> { shape1: RailShape? ->
            val dirs = EXITS_DIRECTION[shape]!!
            if (dirs.first.horizontal == inputSide) {
                return@map dirs.second
            } else if (dirs.second.horizontal == inputSide) {
                return@map dirs.first
            }
            null
        }.filter { obj: RailDir? -> Objects.nonNull(obj) }.collect(Collectors.toList<RailDir?>())
    }

    fun pathfind(
        railPos: BlockPos,
        prevDirTaken: Direction,
        heuristic: Function<BlockPos, Double>
    ): Optional<RailPathFindNode> {

        val visited: MutableSet<Pair<BlockPos, Direction>> = HashSet()
        val queue = PriorityQueue<RailPathFindNode>()
        val ends = PriorityQueue<RailPathFindNode>()
        queue.add(RailPathFindNode(railPos, prevDirTaken, 0, heuristic.apply(railPos)))

        while (!queue.isEmpty() && visited.size < MAX_VISITED && queue.peek().heuristicValue > 0.0) {
            val curr = queue.poll()
            // already explored this path
            if (visited.contains(Pair.of(curr.pos, curr.prevExitTaken))) continue

            visited.add(Pair.of(curr.pos, curr.prevExitTaken))

            getNextNodes(curr.pos, curr.prevExitTaken).forEach(Consumer { raildir: RailDir? ->
                val pos = if (raildir!!.above) curr.pos.relative(raildir.horizontal).above() else curr.pos.relative(
                    raildir.horizontal
                )
                if (minecart.level().getBlockState(pos).`is`(Blocks.VOID_AIR)) {
                    ends.add(RailPathFindNode(pos, raildir.horizontal, curr.pathLength + 1, heuristic.apply(pos)))
                } else {
                    getRail(pos, minecart.level()).ifPresent { nextPos: BlockPos ->
                        queue.add(
                            RailPathFindNode(
                                nextPos,
                                raildir.horizontal,
                                curr.pathLength + 1,
                                heuristic.apply(nextPos)
                            )
                        )
                    }
                }
            })
        }

        queue.addAll(ends)
        return if (queue.isEmpty()) Optional.empty() else Optional.of(queue.peek())
    }

    fun pickCheaperDir(
        directions: List<Direction>,
        pos: BlockPos,
        heuristic: Function<BlockPos, Double>,
        level: Level
    ): Direction {
        // get all directions where output has a possible rail
        val hasOutputDirections = directions.stream()
            .map { d: Direction -> Pair(d, getRail(pos.relative(d), level)) }
            .filter { p: Pair<Direction, Optional<BlockPos>> -> p.second.isPresent }
            .map { p: Pair<Direction, Optional<BlockPos>> -> Pair(p.first, p.second.get()) }
            .collect(Collectors.toList())

        // fallback
        if (hasOutputDirections.isEmpty()) return directions[0]

        val hasPath = hasOutputDirections.stream()
            .map { p: Pair<Direction, BlockPos> -> Pair(p.first, pathfind(p.second, p.first, heuristic)) }
            .filter { p: Pair<Direction, Optional<RailPathFindNode>> -> p.second.isPresent }
            .map { p: Pair<Direction, Optional<RailPathFindNode>> -> Pair(p.first, p.second.get()) }
            .collect(Collectors.toList())

        // fallback
        if (hasPath.isEmpty()) return hasOutputDirections[0].first

        val best = hasPath
            .stream()
            .min(Comparator.comparing { obj: Pair<Direction, RailPathFindNode> -> obj.second })
            .get()
        return best.first
    }

    companion object {
        val EXITS: Map<RailShape, Pair<Vec3i, Vec3i>> =
            Util.make(Maps.newEnumMap(RailShape::class.java)) { map: EnumMap<RailShape, Pair<Vec3i, Vec3i>> ->
                val west = Direction.WEST.normal
                val east = Direction.EAST.normal
                val north = Direction.NORTH.normal
                val south = Direction.SOUTH.normal
                val westb = west.below()
                val eastb = east.below()
                val nothb = north.below()
                val southb = south.below()
                map[RailShape.NORTH_SOUTH] = Pair.of(north, south)
                map[RailShape.EAST_WEST] = Pair.of(west, east)
                map[RailShape.ASCENDING_EAST] = Pair.of(westb, east)
                map[RailShape.ASCENDING_WEST] = Pair.of(west, eastb)
                map[RailShape.ASCENDING_NORTH] = Pair.of(north, southb)
                map[RailShape.ASCENDING_SOUTH] = Pair.of(nothb, south)
                map[RailShape.SOUTH_EAST] = Pair.of(south, east)
                map[RailShape.SOUTH_WEST] = Pair.of(south, west)
                map[RailShape.NORTH_WEST] = Pair.of(north, west)
                map[RailShape.NORTH_EAST] = Pair.of(north, east)
            }

        private const val MAX_VISITED = 200

        val EXITS_DIRECTION: Map<RailShape, Pair<RailDir, RailDir>> =
            Util.make(Maps.newEnumMap(RailShape::class.java)) { map: EnumMap<RailShape, Pair<RailDir, RailDir>> ->
                map[RailShape.NORTH_SOUTH] = Pair.of(RailDir(Direction.NORTH), RailDir(Direction.SOUTH))
                map[RailShape.EAST_WEST] = Pair.of(RailDir(Direction.WEST), RailDir(Direction.EAST))
                map[RailShape.ASCENDING_EAST] = Pair.of(RailDir(Direction.WEST), RailDir(Direction.EAST, true))
                map[RailShape.ASCENDING_WEST] = Pair.of(RailDir(Direction.WEST, true), RailDir(Direction.EAST))
                map[RailShape.ASCENDING_NORTH] = Pair.of(RailDir(Direction.NORTH, true), RailDir(Direction.SOUTH))
                map[RailShape.ASCENDING_SOUTH] = Pair.of(RailDir(Direction.NORTH), RailDir(Direction.SOUTH, true))
                map[RailShape.SOUTH_EAST] = Pair.of(RailDir(Direction.SOUTH), RailDir(Direction.EAST))
                map[RailShape.SOUTH_WEST] = Pair.of(RailDir(Direction.WEST), RailDir(Direction.SOUTH))
                map[RailShape.NORTH_WEST] = Pair.of(RailDir(Direction.WEST), RailDir(Direction.NORTH))
                map[RailShape.NORTH_EAST] = Pair.of(RailDir(Direction.NORTH), RailDir(Direction.EAST))
            }

        fun getShape(pos: BlockPos, level: Level): RailShape {
            val state = level.getBlockState(pos)
            return (state.block as BaseRailBlock).getRailDirection(state, level, pos, null)
        }

        fun getRail(inpos: BlockPos, level: Level): Optional<BlockPos> { // if using with carts, pass in getOnPos.above
            for (pos in Arrays.asList(inpos, inpos.below())) { // check for ascending rail.
                val state = level.getBlockState(pos)
                if (state.block is BaseRailBlock) {
                    return Optional.of(pos)
                }
            }
            return Optional.empty()
        }

        fun directionFromVelocity(deltaMovement: Vec3): Direction {
            return if (abs(deltaMovement.x) > abs(deltaMovement.z)) {
                if (deltaMovement.x > 0) Direction.EAST else Direction.WEST
            } else {
                if (deltaMovement.z > 0) Direction.SOUTH else Direction.NORTH
            }
        }

        fun getOtherExit(direction: Direction, shape: RailShape): Optional<RailDir> {
            val dirs = EXITS_DIRECTION[shape]!!
            return if (dirs.first.horizontal == direction) {
                Optional.of(dirs.second)
            } else if (dirs.second.horizontal == direction) {
                Optional.of(dirs.first)
            } else {
                Optional.empty()
            }
        }

        fun getDirectionToOtherExit(direction: Direction, shape: RailShape): Optional<Vec3i> {
            return getOtherExit(direction, shape).map { other: RailDir ->
                direction.normal.subtract(
                    other.horizontal.normal
                )
            }
        }

        fun samePositionPredicate(entity: AbstractTrainCarEntity): BiPredicate<Direction, BlockPos> {
            val targetRail = getRail(entity.onPos.above(), entity.level())
            return BiPredicate { direction: Direction?, p: BlockPos ->
                getRail(p, entity.level())
                    .flatMap { pos: BlockPos -> targetRail.map { rp: BlockPos -> rp == pos } }
                    .orElse(false)
            }
        }

        fun samePositionHeuristicSet(potentialDestinations: Set<BlockPos>): Function<BlockPos, Double> {
            return Function { pos: BlockPos ->
                potentialDestinations.stream().map { p: BlockPos -> p.distSqr(pos) }
                    .min { obj: Double, anotherDouble: Double? ->
                        obj.compareTo(
                            anotherDouble!!
                        )
                    }.orElse(0.0)
            }
        }
    }
}
