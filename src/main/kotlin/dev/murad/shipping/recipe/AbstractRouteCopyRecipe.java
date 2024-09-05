package dev.murad.shipping.recipe;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Optional;

public abstract class AbstractRouteCopyRecipe extends CustomRecipe {

    private final Item item;

    public AbstractRouteCopyRecipe(CraftingBookCategory cat, Item matchingItem) {
        super(cat);
        this.item = matchingItem;
    }

    public abstract boolean stackHasNodes(ItemStack stack);

    @Nonnull
    @Override
    public abstract RecipeSerializer<?> getSerializer();

    /**
     * If hasNodes is set, return if stack has nodes,
     * otherwise return if stack is empty.
     */
    private boolean isRouteWithNodes(ItemStack stack, boolean hasNodes) {
        if (stack.getItem() == item) {
            return !stackHasNodes(stack) ^ hasNodes;
        }
        return false;
    }

    // returns a pair of <Filled Tug Route, Unfilled Tug Route>
    private Optional<Pair<ItemStack, Integer>> checkTugRoutes(CraftingInput input) {
        int i = 0;
        ItemStack filledRoute = ItemStack.EMPTY;

        for (int j = 0; j < input.size(); ++j) {
            ItemStack stack = input.getItem(j);
            if (!stack.isEmpty()) {
                if (isRouteWithNodes(stack, true)) {
                    if (!filledRoute.isEmpty()) {
                        // can't have 2 filled routes
                        return Optional.empty();
                    }

                    filledRoute = stack;
                } else {
                    if (!isRouteWithNodes(stack, false)) {
                        return Optional.empty();
                    }

                    ++i;
                }
            }
        }

        // if we have a filled route
        if (!filledRoute.isEmpty() && i <= filledRoute.getMaxStackSize() - 1) {
            return Optional.of(new Pair<>(filledRoute, i));
        }

        return Optional.empty();
    }

    @Override
    public boolean matches(@NotNull CraftingInput pInput, @NotNull Level pLevel) {

        return checkTugRoutes(pInput).isPresent();
    }

    @Override
    public ItemStack assemble(@NotNull CraftingInput pInput, HolderLookup.@NotNull Provider pRegistries) {
        Optional<Pair<ItemStack, Integer>> matchOpt = checkTugRoutes(pInput);
        if (matchOpt.isEmpty()) return ItemStack.EMPTY;

        Pair<ItemStack, Integer> match = matchOpt.get();
        ItemStack filled = match.getFirst();
        int num = match.getSecond();

        if (num == 0) {
            // clear!
            return new ItemStack(item, 1);
        } else {
            // copy
            ItemStack output = filled.copy();
            output.setCount(num + 1);
            return output;
        }
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x * y >= 2;
    }
}
