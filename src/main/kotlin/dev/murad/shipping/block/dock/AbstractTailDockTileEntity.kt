package dev.murad.shipping.block.dock

import dev.murad.shipping.block.IVesselLoader
import dev.murad.shipping.util.LinkableEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.Container
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.HopperBlockEntity
import net.minecraft.world.level.block.state.BlockState

abstract class AbstractTailDockTileEntity<T>(t: BlockEntityType<*>, pos: BlockPos, state: BlockState) :
    AbstractDockTileEntity<T>(t, pos, state) where T : Entity, T : LinkableEntity<T> {

    protected val isExtracting: Boolean
        get() = blockState.getValue(DockingBlockStates.INVERTED)

    override fun hold(vessel: T, direction: Direction): Boolean {

        if (checkBadDirCondition(direction)) {
            return false
        }

        for (p in getTargetBlockPos()) {
            if (checkInterface(vessel, p)) {
                return true
            }
        }
        return false
    }


    private fun handleItemHopper(bargeEntity: T, hopper: HopperBlockEntity): Boolean {

        if (bargeEntity !is Container) {
            return false
        }

        return if (isExtracting) {
            mayMoveIntoInventory(hopper, bargeEntity as Container)
        } else {
            mayMoveIntoInventory(bargeEntity as Container, hopper)
        }
    }

    private fun checkInterface(vessel: T, p: BlockPos): Boolean {

        return getHopperAt(p).map { h -> handleItemHopper(vessel, h) }
            .orElse(getVesselLoader(p).map { l: IVesselLoader ->
                l.hold(
                    vessel,
                    if (isExtracting) IVesselLoader.Mode.IMPORT else IVesselLoader.Mode.EXPORT
                )
            }
                .orElse(false))
    }

    protected abstract fun checkBadDirCondition(direction: Direction): Boolean
}
