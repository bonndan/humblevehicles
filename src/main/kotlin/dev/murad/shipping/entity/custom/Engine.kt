package dev.murad.shipping.entity.custom

import net.minecraft.core.RegistryAccess
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.ItemStackHandler
import kotlin.math.ceil


abstract class Engine(private var fuelMultiplier: Double = 1.0) : ItemStackHandler(1) {

    private var engineOn: Boolean = false
    private var remainingBurnTime: Int = 0
    private var totalBurnTime: Int = 0

    fun getBurnProgressPct(): Int {
        return (remainingBurnTime.toFloat() / totalBurnTime.toFloat() * 100).toInt()
    }

    fun isLit(): Boolean = remainingBurnTime > 0 && isOn()

    fun isOn(): Boolean {
        return engineOn
    }

    /**
     * Consume an item of fuel
     * @return number of base ticks the fuel burns for
     */
    fun tickFuel(): Int {

        if (remainingBurnTime > 0) {
            remainingBurnTime--
            return remainingBurnTime
        }

        val burnTime: Int = tryConsumeFuel()
        val adjustedBurnTime: Int = ceil(burnTime * fuelMultiplier).toInt()
        this.totalBurnTime = adjustedBurnTime
        this.remainingBurnTime = adjustedBurnTime
        return remainingBurnTime
    }

    private fun tryConsumeFuel(): Int {

        val stack = getStackInSlot(0)
        val burnTime = calculateBurnTimeOfNextItem(stack)

        if (burnTime > 0) {
            stack.shrink(1)
        }

        return burnTime
    }

    fun setEngineOn(state: Boolean) {
        this.engineOn = state
    }

    abstract override fun isItemValid(slot: Int, stack: ItemStack): Boolean

    abstract fun calculateBurnTimeOfNextItem(stack: ItemStack): Int

    fun readAdditionalSaveData(compound: CompoundTag, registryAccess: RegistryAccess) {
        setBurnTime(if (compound.contains(BURN)) compound.getInt(BURN) else 0)
        setTotalBurnTime(if (compound.contains(TOTAL_BURN_CAPACITY)) compound.getInt(TOTAL_BURN_CAPACITY) else 0)
        setEngineOn(if (compound.contains(ENGINE_ON)) compound.getBoolean(ENGINE_ON) else false)
        deserializeNBT(registryAccess, compound.getCompound(FUEL_ITEMS))
    }

    fun addAdditionalSaveData(compound: CompoundTag, registryAccess: RegistryAccess) {
        compound.putInt(BURN, remainingBurnTime)
        compound.putInt(TOTAL_BURN_CAPACITY, totalBurnTime)
        compound.putBoolean(ENGINE_ON, engineOn)
        compound.put(FUEL_ITEMS, serializeNBT(registryAccess))
    }

    private fun setBurnTime(burnTime: Int) {
        this.remainingBurnTime = burnTime
    }

    private fun setTotalBurnTime(totalBurnTime: Int) {
        this.totalBurnTime = totalBurnTime
    }

    /**
     * CLient side updates only.
     */
    fun setRemainingBurnTime(remainingBurnTime: Int) {
        this.remainingBurnTime = remainingBurnTime
    }

    companion object {
        const val BURN = "burn"
        const val TOTAL_BURN_CAPACITY = "burn_capacity"
        const val ENGINE_ON = "eo"
        const val FUEL_ITEMS = "fuelItems"
    }
}