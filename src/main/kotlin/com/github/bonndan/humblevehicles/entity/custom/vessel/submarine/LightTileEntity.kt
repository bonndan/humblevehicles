package com.github.bonndan.humblevehicles.entity.custom.vessel.submarine

import com.github.bonndan.humblevehicles.setup.ModTileEntitiesTypes
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class LightTileEntity(pos: BlockPos, state: BlockState) : BlockEntity(ModTileEntitiesTypes.LIGHT.get(), pos, state)
