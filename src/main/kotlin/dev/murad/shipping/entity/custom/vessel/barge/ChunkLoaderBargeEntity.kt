package dev.murad.shipping.entity.custom.vessel.barge

import dev.murad.shipping.setup.ModEntityTypes
import dev.murad.shipping.setup.ModItems
import dev.murad.shipping.util.MobileChunkLoader
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

    public override fun remove(r: RemovalReason) {
        super.remove(r)
        if (!this.level().isClientSide) {
            mobileChunkLoader.remove()
        }
    }

    public override fun tick() {
        super.tick()
        if (!this.level().isClientSide) {
            mobileChunkLoader.serverTick()
        }
    }

    public override fun addAdditionalSaveData(p_213281_1_: CompoundTag) {
        mobileChunkLoader.addAdditionalSaveData(p_213281_1_)
    }

    public override fun readAdditionalSaveData(p_70037_1_: CompoundTag) {
        mobileChunkLoader.readAdditionalSaveData(p_70037_1_)
    }

    public override fun getDropItem(): Item? {
        return ModItems.SEATER_BARGE.get()
    }

    override fun doInteract(player: Player?) {
    }
}
