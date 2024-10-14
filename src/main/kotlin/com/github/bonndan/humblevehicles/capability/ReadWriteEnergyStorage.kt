package com.github.bonndan.humblevehicles.capability

import net.minecraft.nbt.CompoundTag
import net.neoforged.neoforge.energy.EnergyStorage
import net.neoforged.neoforge.energy.IEnergyStorage
import kotlin.math.max
import kotlin.math.min

/**
 * Re-implementation of EnergyStorage so we can read and write it from/to NBT data
 */
class ReadWriteEnergyStorage(private val maxCapacity: Int, private val maxReceive: Int, private val maxExtract: Int) :
    IEnergyStorage {
    private var proxyStorage: EnergyStorage? = null

    private fun clampInclusive(n: Int, lo: Int, hi: Int): Int {
        return max(lo.toDouble(), min(n.toDouble(), hi.toDouble())).toInt()
    }

    // when a TileEntity/Item/Entity is created, call this to set it up
    fun setEnergy(energy: Int) {
        proxyStorage = EnergyStorage(
            maxCapacity,
            maxReceive,
            maxExtract, clampInclusive(energy, 0, maxCapacity)
        )
    }

    fun readAdditionalSaveData(compound: CompoundTag) {
        val energy = if (compound.contains(ENERGY_TAG)) compound.getInt(ENERGY_TAG) else 0
        proxyStorage = EnergyStorage(
            maxCapacity,
            maxReceive,
            maxExtract,
            clampInclusive(energy, 0, maxCapacity)
        )
    }

    fun addAdditionalSaveData(compound: CompoundTag) {
        compound.putInt(ENERGY_TAG, proxyStorage!!.energyStored)
    }

    override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int {
        return proxyStorage!!.receiveEnergy(maxReceive, simulate)
    }

    override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int {
        return proxyStorage!!.extractEnergy(maxExtract, simulate)
    }

    override fun getEnergyStored(): Int {
        return proxyStorage!!.energyStored
    }

    override fun getMaxEnergyStored(): Int {
        return proxyStorage!!.maxEnergyStored
    }

    override fun canExtract(): Boolean {
        return proxyStorage!!.canExtract()
    }

    override fun canReceive(): Boolean {
        return proxyStorage!!.canReceive()
    }

    companion object {
        const val ENERGY_TAG: String = "energy"
    }
}
