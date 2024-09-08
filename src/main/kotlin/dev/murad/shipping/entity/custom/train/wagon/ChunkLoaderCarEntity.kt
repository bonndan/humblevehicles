package dev.murad.shipping.entity.custom.train.wagon

import dev.murad.shipping.setup.ModEntityTypes
import dev.murad.shipping.setup.ModItems
import dev.murad.shipping.util.MobileChunkLoader
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class ChunkLoaderCarEntity : AbstractWagonEntity {
    private val mobileChunkLoader: MobileChunkLoader

    constructor(p_38087_: EntityType<*>, p_38088_: Level) : super(p_38087_, p_38088_) {
        mobileChunkLoader = MobileChunkLoader(this)
    }

    constructor(
        level: Level,
        aDouble: Double,
        aDouble1: Double,
        aDouble2: Double
    ) : super(ModEntityTypes.CHUNK_LOADER_CAR.get(), level, aDouble, aDouble1, aDouble2) {
        mobileChunkLoader = MobileChunkLoader(this)
    }

    override fun getPickResult(): ItemStack {
        return ItemStack(ModItems.SEATER_CAR.get())
    }

    override fun remove(r: RemovalReason) {
        super.remove(r)
        if (!level().isClientSide) {
            mobileChunkLoader.remove()
        }
    }

    override fun tick() {
        super.tick()
        if (!level().isClientSide) {
            mobileChunkLoader.serverTick()
        }
    }

    public override fun addAdditionalSaveData(p_213281_1_: CompoundTag) {
        super.addAdditionalSaveData(p_213281_1_)
        mobileChunkLoader.addAdditionalSaveData(p_213281_1_)
    }

    public override fun readAdditionalSaveData(p_70037_1_: CompoundTag) {
        super.readAdditionalSaveData(p_70037_1_)
        mobileChunkLoader.readAdditionalSaveData(p_70037_1_)
    }
}
