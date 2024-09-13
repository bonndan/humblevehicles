package dev.murad.shipping.entity.accessor

import net.minecraft.world.inventory.ContainerData
import java.util.function.IntSupplier

class SteamHeadVehicleDataAccessor(private val data: ContainerData) : HeadVehicleDataAccessor(data) {

    fun getBurnProgress(): Int {
        return data[15]
    }

    class Builder : HeadVehicleDataAccessor.Builder() {

        fun withBurnProgress(burnProgress: IntSupplier): Builder {
            arr.setSupplier(15, burnProgress)
            return this
        }

        override fun build(): SteamHeadVehicleDataAccessor {
            return SteamHeadVehicleDataAccessor(this.arr)
        }
    }
}
