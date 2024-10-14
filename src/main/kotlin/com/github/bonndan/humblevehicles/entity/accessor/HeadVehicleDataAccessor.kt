package com.github.bonndan.humblevehicles.entity.accessor

import net.minecraft.world.inventory.ContainerData
import java.util.function.BooleanSupplier
import java.util.function.IntSupplier

class HeadVehicleDataAccessor(private val data: ContainerData) : DataAccessor(data) {

    val isLit: Boolean
        get() = data[1] == 1

    val isOn: Boolean
        get() = data[2] == 1

    fun visitedSize(): Int {
        return data[3]
    }

    fun routeSize(): Int {
        return data[4]
    }

    fun canMove(): Boolean {
        return data[5] == 1
    }

    fun getBurnProgress(): Int {
        return data[15]
    }

    class Builder {

        private val arr: SupplierIntArray = SupplierIntArray(20)

        fun withId(id: Int): Builder {
            arr[0] = id
            return this
        }

        fun withLit(lit: BooleanSupplier): Builder {
            arr.setSupplier(1) { if (lit.asBoolean) 1 else -1 }
            return this
        }

        fun withOn(on: BooleanSupplier): Builder {
            arr.setSupplier(2) { if (on.asBoolean) 1 else -1 }
            return this
        }

        fun withVisitedSize(s: IntSupplier?): Builder {
            arr.setSupplier(3, s)
            return this
        }

        fun withRouteSize(s: IntSupplier?): Builder {
            arr.setSupplier(4, s)
            return this
        }

        fun withCanMove(lit: BooleanSupplier): Builder {
            arr.setSupplier(5) { if (lit.asBoolean) 1 else -1 }
            return this
        }

        fun withBurnProgressPct(burnProgress: IntSupplier): Builder {
            arr.setSupplier(15, burnProgress)
            return this
        }

        fun build(): HeadVehicleDataAccessor {
            return HeadVehicleDataAccessor(this.arr)
        }
    }
}
