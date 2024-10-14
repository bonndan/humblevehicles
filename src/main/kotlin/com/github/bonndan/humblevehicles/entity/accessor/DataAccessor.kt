package com.github.bonndan.humblevehicles.entity.accessor

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.inventory.ContainerData

open class DataAccessor(private var rawData: ContainerData) : ContainerData {

    fun write(buffer: FriendlyByteBuf) {
        for (i in 0 until rawData.count) {
            buffer.writeInt(rawData[i])
        }
    }

    fun getEntityUUID(): Int? = if (rawData.count == 0) { null } else rawData[0]

    override fun get(i: Int): Int {
        return rawData[i]
    }

    override fun set(i: Int, j: Int) {
        rawData[i] = j
    }

    override fun getCount(): Int {
        return rawData.count
    }

    fun getRawData(): ContainerData {
        return rawData
    }
}
