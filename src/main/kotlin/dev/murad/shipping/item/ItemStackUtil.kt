package dev.murad.shipping.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

import static dev.murad.shipping.setup.ModDataComponents.TAG_PROPERTIES;

public class ItemStackUtil {

    private ItemStackUtil() {
    }

    public static Optional<CompoundTag> getCompoundTag(ItemStack stack) {

        return Optional.ofNullable(stack.getComponents().get(TAG_PROPERTIES.get()));
    }

    public static boolean contains(ItemStack stack, String pKey) {
        return getCompoundTag(stack).map(compoundTag -> compoundTag.contains(pKey)).orElse(false);
    }
}
