package dev.murad.shipping.util

import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType

object TickerUtil {
    fun <E : BlockEntity?, A : BlockEntity?> createTickerHelper(
        p_152133_: BlockEntityType<A>,
        p_152134_: BlockEntityType<E>,
        p_152135_: BlockEntityTicker<in E>?
    ): BlockEntityTicker<A>? {
        return if (p_152134_ === p_152133_) p_152135_ as BlockEntityTicker<A>? else null
    }
}
