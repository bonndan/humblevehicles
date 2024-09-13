package dev.murad.shipping.entity.accessor

import net.minecraft.world.inventory.ContainerData
import java.util.function.IntSupplier

class EnergyHeadVehicleDataAccessor(private val data: ContainerData) : HeadVehicleDataAccessor(data) {

    val energy: Int
        /**
         * Lil-endian
         */
        get() {
            val lo = data[15] and SHORT_MASK
            val hi = data[16] and SHORT_MASK
            return lo or (hi shl 16)
        }

    val capacity: Int
        get() {
            val lo = data[17] and SHORT_MASK
            val hi = data[18] and SHORT_MASK
            return lo or (hi shl 16)
        }

    class Builder : HeadVehicleDataAccessor.Builder() {
       
        fun withEnergy(energy: IntSupplier): Builder {
            arr.setSupplier(15) { energy.asInt and SHORT_MASK }
            arr.setSupplier(16) { (energy.asInt shr 16) and SHORT_MASK }
            return this
        }

        fun withCapacity(capacity: IntSupplier): Builder {
            arr.setSupplier(17) { capacity.asInt and SHORT_MASK }
            arr.setSupplier(18) { (capacity.asInt shr 16) and SHORT_MASK }
            return this
        }

        override fun build(): EnergyHeadVehicleDataAccessor {
            return EnergyHeadVehicleDataAccessor(arr)
        }
    }

    companion object {
        private const val SHORT_MASK = 0xFFFF
    }
}
