package com.github.bonndan.humblevehicles.entity.custom.vessel.barge

import com.github.bonndan.humblevehicles.setup.ModEntityTypes
import com.github.bonndan.humblevehicles.setup.ModItems
import com.github.bonndan.humblevehicles.util.MobileChunkLoader
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level

class ChunkLoaderBargeEntity : AbstractBargeEntity {

    private val mobileChunkLoader: MobileChunkLoader

    constructor(type: EntityType<out ChunkLoaderBargeEntity?>, world: Level) : super(type, world) {
        mobileChunkLoader = MobileChunkLoader(this)
    }

    constructor(worldIn: Level, x: Double, y: Double, z: Double) : super(
        ModEntityTypes.CHUNK_LOADER_BARGE.get(),
        worldIn,
        x,
        y,
        z
    ) {
        mobileChunkLoader = MobileChunkLoader(this)
    }

     override fun remove(r: RemovalReason) {
        super.remove(r)
        if (!this.level().isClientSide) {
            mobileChunkLoader.remove()
        }
    }

     override fun tick() {
        super.tick()
        if (!this.level().isClientSide) {
            mobileChunkLoader.serverTick()
        }
    }

     override fun addAdditionalSaveData(p_213281_1_: CompoundTag) {
        mobileChunkLoader.addAdditionalSaveData(p_213281_1_)
    }

    override fun readAdditionalSaveData(p_70037_1_: CompoundTag) {
        mobileChunkLoader.readAdditionalSaveData(p_70037_1_)
    }

    override fun getDropItem(): Item? {
        return ModItems.SEATER_BARGE.get()
    }

    override fun doInteract(player: Player?) {
    }
}
