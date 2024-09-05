package dev.murad.shipping.item;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.EnergyStorage;

import java.util.Optional;

import static dev.murad.shipping.setup.ModDataComponents.ENERGY;

public class EnergyUtil {

    private EnergyUtil() {
    }

    public static Optional<EnergyStorage> getEnergyStorage(ItemStack stack) {

        return Optional.ofNullable(stack.getComponents().get(ENERGY.get()));
    }
}
