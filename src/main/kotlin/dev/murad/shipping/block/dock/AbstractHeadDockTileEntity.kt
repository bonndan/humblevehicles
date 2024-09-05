package dev.murad.shipping.block.dock

import com.mojang.datafixers.util.Pair
import dev.murad.shipping.block.IVesselLoader
import dev.murad.shipping.util.InventoryUtils
import dev.murad.shipping.util.LinkableEntity
import dev.murad.shipping.util.LinkableEntityHead
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.Container
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.HopperBlockEntity
import net.minecraft.world.level.block.state.BlockState
import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream
import kotlin.math.min

abstract class AbstractHeadDockTileEntity<T>(t: BlockEntityType<*>?, pos: BlockPos?, state: BlockState?) :
    AbstractDockTileEntity<T>(t, pos, state) where T : Entity?, T : LinkableEntity<T>? {

    protected fun handleItemHopper(tugEntity: T, hopper: HopperBlockEntity?): Boolean {
        if (tugEntity !is Container) {
            return false
        }
        return InventoryUtils.mayMoveIntoInventory(tugEntity as Container, hopper)
    }


    override fun hold(tug: T, direction: Direction): Boolean {
        if (tug !is LinkableEntityHead<*> || checkBadDirCondition(tug, direction)) {
            return false
        }

        // force tug to be docked when powered
        // todo: add UI for inverted mode toggle?
        if (blockState.getValue(DockingBlockStates.POWERED)) {
            return true
        }

        for (p in targetBlockPos) {
            if (getHopper(p).map { hopper: HopperBlockEntity? -> handleItemHopper(tug, hopper) }
                    .orElse(getVesselLoader(p).map { l: IVesselLoader -> l.hold<T>(tug as T, IVesselLoader.Mode.EXPORT) }
                        .orElse(false))) {
                return true
            }
        }


        val barges = getTailDockPairs(tug)


        if (barges.stream()
                .map { pair: Pair<T, AbstractTailDockTileEntity<T>> -> pair.second.hold(pair.first, direction) }
                .reduce(false) { a: Boolean, b: Boolean -> java.lang.Boolean.logicalOr(a, b) }
        ) {
            return true
        }

        return false
    }

    protected abstract fun checkBadDirCondition(tug: T, direction: Direction?): Boolean

    protected abstract fun getRowDirection(facing: Direction?): Direction

    private fun getTailDockPairs(tug: T): List<Pair<T, AbstractTailDockTileEntity<T>>> {
        val barges = tug!!.train.asListOfTugged()
        val docks = tailDocks
        return IntStream.range(
            0, min(barges.size.toDouble(), docks.size.toDouble())
                .toInt()
        )
            .mapToObj { i: Int ->
                Pair(
                    barges[i], docks[i]
                )
            }
            .collect(Collectors.toList())
    }

    private val tailDocks: List<AbstractTailDockTileEntity<T>>
        get() {
            val facing = blockState.getValue(DockingBlockStates.FACING)
            val rowDirection = getRowDirection(facing)
            val docks: MutableList<AbstractTailDockTileEntity<T>> = ArrayList()
            var dock = getNextBargeDock(rowDirection, this.blockPos)
            while (dock.isPresent
            ) {
                docks.add(dock.get())
                dock = getNextBargeDock(rowDirection, dock.get().blockPos)
            }
            return docks
        }

    private fun getNextBargeDock(rowDirection: Direction, pos: BlockPos): Optional<AbstractTailDockTileEntity<T>> {
        val next = pos.relative(rowDirection)
        return Optional.ofNullable(level!!.getBlockEntity(next))
            .filter { e: BlockEntity? -> e is AbstractTailDockTileEntity<*> }
            .map { e: BlockEntity -> e as AbstractTailDockTileEntity<T> }
    }
}
