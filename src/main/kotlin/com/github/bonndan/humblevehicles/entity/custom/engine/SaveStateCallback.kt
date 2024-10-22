package com.github.bonndan.humblevehicles.entity.custom.engine

interface SaveStateCallback {

    fun saveState(engineState: Boolean, remainingBurnTime: Int)
}
