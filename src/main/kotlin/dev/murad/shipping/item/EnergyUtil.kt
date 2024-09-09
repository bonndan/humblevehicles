package dev.murad.shipping.item

import dev.murad.shipping.setup.ModDataComponents.ENERGY
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.energy.EnergyStorage
import java.util.*

object EnergyUtil {
    @JvmStatic
    fun getEnergyStorage(stack: ItemStack): Optional<EnergyStorage> {
        return Optional.ofNullable<EnergyStorage>(stack.components.get(ENERGY.get()))
    }
}
