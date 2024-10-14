package com.github.bonndan.humblevehicles.entity.custom

interface SaveStateCallback {

    fun saveState(engineState: Boolean, remainingBurnTime: Int)
}
