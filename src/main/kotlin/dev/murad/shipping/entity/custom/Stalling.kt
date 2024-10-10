package dev.murad.shipping.entity.custom

import dev.murad.shipping.capability.StallingCapability

interface Stalling {

    fun getStalling(): StallingCapability
}