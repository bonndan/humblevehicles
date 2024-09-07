package dev.murad.shipping.entity.accessor

import net.minecraft.world.inventory.ContainerData

class TugRouteScreenDataAccessor(private val data: ContainerData) : DataAccessor(data) {

    val isOffHand: Boolean
        get() = data[1] == 1

    class Builder(uuid: Int) {

        var arr: SupplierIntArray = SupplierIntArray(2)

        init {
            arr[0] = uuid
        }

        fun withOffHand(isOffHand: Boolean): Builder {
            arr.setSupplier(1) { if (isOffHand) 1 else 0 }
            return this
        }

        fun build(): TugRouteScreenDataAccessor {
            return TugRouteScreenDataAccessor(this.arr)
        }
    }
}
