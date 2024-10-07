package dev.murad.shipping.entity.custom

interface SaveStateCallback {

    fun saveState(engineState: Boolean, remainingBurnTime: Int)
}
