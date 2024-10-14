package com.github.bonndan.humblevehicles.entity.custom

import com.github.bonndan.humblevehicles.capability.StallingCapability

interface Stalling {

    fun getStalling(): StallingCapability
}