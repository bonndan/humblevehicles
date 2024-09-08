package dev.murad.shipping.block.dock

import dev.murad.shipping.block.IVesselLoader
import dev.murad.shipping.util.LinkableEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.HopperBlockEntity
import net.minecraft.world.level.block.state.BlockState
import java.util.*

abstract class AbstractDockTileEntity<T>(p_i48289_1_: BlockEntityType<*>?, pos: BlockPos?, s: BlockState?) :
    BlockEntity(p_i48289_1_, pos, s) where T : Entity, T : LinkableEntity<T> {

    abstract fun hold(vessel: T, direction: Direction): Boolean

    fun getHopper(p: BlockPos): Optional<HopperBlockEntity> {
        val mayBeHopper = level!!.getBlockEntity(p)
        return if (mayBeHopper is HopperBlockEntity) {
            Optional.of(mayBeHopper)
        } else Optional.empty()
    }

    fun getVesselLoader(p: BlockPos): Optional<IVesselLoader> {
        val mayBeHopper = level!!.getBlockEntity(p)
        return if (mayBeHopper is IVesselLoader) {
            Optional.of(mayBeHopper)
        } else Optional.empty()
    }

    protected abstract val targetBlockPos: List<BlockPos>
        get
}
