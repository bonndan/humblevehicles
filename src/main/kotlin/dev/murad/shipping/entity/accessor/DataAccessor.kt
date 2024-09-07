package dev.murad.shipping.entity.accessor

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.inventory.ContainerData

open class DataAccessor(var rawData: ContainerData) : ContainerData {

    fun write(buffer: FriendlyByteBuf) {
        for (i in 0 until rawData.count) {
            buffer.writeInt(rawData[i])
        }
    }

    val entityUUID: Int
        get() = rawData[0]

    override fun get(i: Int): Int {
        return rawData[i]
    }

    override fun set(i: Int, j: Int) {
        rawData[i] = j
    }

    override fun getCount(): Int {
        return rawData.count
    }
}
