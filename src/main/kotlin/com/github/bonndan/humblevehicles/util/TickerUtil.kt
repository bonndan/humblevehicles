package com.github.bonndan.humblevehicles.util

import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType

object TickerUtil {
    fun <E : BlockEntity?, A : BlockEntity?> createTickerHelper(
        blockEntityTypeA: BlockEntityType<A>,
        blockEntityTypeE: BlockEntityType<E>,
        blockEntityTicker: BlockEntityTicker<in E>?
    ): BlockEntityTicker<A>? {
        return if (blockEntityTypeE === blockEntityTypeA) blockEntityTicker as BlockEntityTicker<A>? else null
    }
}
