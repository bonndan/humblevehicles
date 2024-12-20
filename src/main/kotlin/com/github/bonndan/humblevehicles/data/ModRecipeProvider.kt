package com.github.bonndan.humblevehicles.data

import com.github.bonndan.humblevehicles.setup.ModBlocks
import com.github.bonndan.humblevehicles.setup.ModItems
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.level.block.Blocks
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.common.conditions.ICondition
import java.util.concurrent.CompletableFuture

class ModRecipeProvider(recipeOutput: RecipeOutput, pRegistries: HolderLookup.Provider) :
    RecipeProvider(pRegistries, recipeOutput) {

    override fun buildRecipes() {


        this.shaped(RecipeCategory.TRANSPORTATION, ModBlocks.SWITCH_RAIL.get(), 4)
            .define('#', Items.RAIL)
            .pattern("# ")
            .pattern("##")
            .pattern("# ")
            .unlockedBy("has_item", has(Items.RAIL))
            .save(output)

        this.shaped(RecipeCategory.TRANSPORTATION, ModBlocks.TEE_JUNCTION_RAIL.get(), 4)
            .define('#', Items.RAIL)
            .pattern("###")
            .pattern(" # ")
            .unlockedBy("has_item", has(Items.RAIL))
            .save(output)

        this.shaped(RecipeCategory.TRANSPORTATION, ModBlocks.JUNCTION_RAIL.get(), 5)
            .define('#', Items.RAIL)
            .pattern(" # ")
            .pattern("###")
            .pattern(" # ")
            .unlockedBy("has_item", has(Items.RAIL))
            .save(output)

        this.shapeless(RecipeCategory.TRANSPORTATION, ModBlocks.AUTOMATIC_SWITCH_RAIL.get(), 1)
            .requires(ModBlocks.SWITCH_RAIL.get())
            .requires(ModItems.RECEIVER_COMPONENT.get())
            .unlockedBy("has_item", has(Items.RAIL))
            .save(output)

        this.shapeless(RecipeCategory.TRANSPORTATION, ModBlocks.AUTOMATIC_TEE_JUNCTION_RAIL.get(), 1)
            .requires(ModBlocks.TEE_JUNCTION_RAIL.get())
            .requires(ModItems.RECEIVER_COMPONENT.get())
            .unlockedBy("has_item", has(Items.RAIL))
            .save(output)

        this.shaped(RecipeCategory.TRANSPORTATION, ModBlocks.LOCOMOTIVE_DOCK_RAIL.get(), 2)
            .define('#', Items.RAIL)
            .define('$', ModItems.SPRING.get())
            .pattern(" $ ")
            .pattern(" # ")
            .pattern(" # ")
            .unlockedBy("has_item", has(Items.RAIL))
            .save(output)

        this.shaped(RecipeCategory.TRANSPORTATION, ModBlocks.CAR_DOCK_RAIL.get(), 3)
            .define('#', Items.RAIL)
            .define('$', ModItems.SPRING.get())
            .pattern(" # ")
            .pattern("$#$")
            .pattern(" # ")
            .unlockedBy("has_item", has(Items.RAIL))
            .save(output)

        this.shaped(RecipeCategory.REDSTONE, ModBlocks.FLUID_HOPPER.get(), 1)
            .define('_', Items.GLASS)
            .define('$', Items.HOPPER)
            .pattern("_\$_")
            .pattern(" _ ")
            .unlockedBy("has_item", has(Items.HOPPER))
            .save(output)

        this.shaped(RecipeCategory.TRANSPORTATION, ModItems.SPRING.get(), 6)
            .define('_', Tags.Items.STRINGS)
            .define('$', Items.IRON_NUGGET)
            .pattern("_\$_")
            .pattern("\$_$")
            .unlockedBy("has_item", has(Items.STRING))
            .save(output)

        this.shaped(RecipeCategory.TOOLS, ModItems.TUG_ROUTE.get())
            .define('_', ModItems.TRANSMITTER_COMPONENT.get())
            .define('#', Items.REDSTONE)
            .define('$', Items.IRON_NUGGET)
            .pattern(" # ")
            .pattern("\$_$")
            .pattern(" # ")
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output)

        this.shaped(RecipeCategory.TOOLS, ModItems.LOCO_ROUTE.get())
            .define('_', ModItems.TRANSMITTER_COMPONENT.get())
            .define('#', Items.IRON_NUGGET)
            .define('$', Items.REDSTONE)
            .pattern(" # ")
            .pattern("\$_$")
            .pattern(" # ")
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output)

        this.shaped(RecipeCategory.TRANSPORTATION, ModItems.SEATER_CAR.get())
            .define('#', ItemTags.PLANKS)
            .define('$', Items.IRON_INGOT)
            .pattern("   ")
            .pattern("###")
            .pattern("$ $")
            .unlockedBy("has_item", has(Items.IRON_INGOT))
            .save(output)

        this.shaped(RecipeCategory.TRANSPORTATION, ModItems.CHEST_CAR.get())
            .define('#', Items.CHEST)
            .define('$', ModItems.SEATER_CAR.get())
            .pattern("   ")
            .pattern(" # ")
            .pattern(" $ ")
            .unlockedBy("has_item", has(ModItems.SEATER_CAR.get()))
            .save(output)

        this.shaped(RecipeCategory.TRANSPORTATION, ModItems.BARREL_CAR.get())
            .define('#', Items.BARREL)
            .define('$', ModItems.SEATER_CAR.get())
            .pattern("   ")
            .pattern(" # ")
            .pattern(" $ ")
            .unlockedBy("has_item", has(ModItems.SEATER_CAR.get()))
            .save(output)

        this.shaped(RecipeCategory.TRANSPORTATION, ModItems.FLUID_CAR.get())
            .define('#', Items.GLASS)
            .define('$', ModItems.SEATER_CAR.get())
            .pattern("# #")
            .pattern(" # ")
            .pattern(" $ ")
            .unlockedBy("has_item", has(ModItems.SEATER_CAR.get()))
            .save(output)

        this.shaped(RecipeCategory.TRANSPORTATION, ModItems.ENERGY_LOCOMOTIVE.get())
            .define('-', Items.REDSTONE_TORCH)
            .define('#', Items.IRON_INGOT)
            .define('.', Items.POWERED_RAIL)
            .define('_', Blocks.PISTON)
            .define('o', Items.COPPER_INGOT)
            .define('$', ModItems.SEATER_CAR.get())
            .pattern("o-o")
            .pattern("o._")
            .pattern("#$#")
            .unlockedBy("has_item", has(ModItems.SEATER_CAR.get()))
            .save(output)

        this.shaped(RecipeCategory.TRANSPORTATION, ModItems.STEAM_LOCOMOTIVE.get())
            .define('#', Items.IRON_INGOT)
            .define('.', Items.FURNACE)
            .define('_', Blocks.PISTON)
            .define('$', ModItems.SEATER_CAR.get())
            .pattern(" # ")
            .pattern("_._")
            .pattern("#$#")
            .unlockedBy("has_item", has(ModItems.SEATER_CAR.get()))
            .save(output)

        this.shaped(RecipeCategory.MISC, ModItems.RECEIVER_COMPONENT.get(), 8)
            .define('o', Items.ENDER_EYE)
            .define('#', Items.REDSTONE)
            .define('_', Items.STONE_SLAB)
            .pattern("o")
            .pattern("#")
            .pattern("_")
            .unlockedBy("has_item", has(Items.ENDER_EYE))
            .save(output)

        this.shaped(RecipeCategory.MISC, ModItems.TRANSMITTER_COMPONENT.get(), 4)
            .define('o', Items.ENDER_PEARL)
            .define('#', Items.GLOWSTONE_DUST)
            .define('_', Items.STONE_SLAB)
            .pattern("o")
            .pattern("#")
            .pattern("_")
            .unlockedBy("has_item", has(Items.ENDER_EYE))
            .save(output)

        this.shaped(RecipeCategory.TOOLS, ModItems.CONDUCTORS_WRENCH.get(), 1)
            .define('-', Items.IRON_INGOT)
            .define('^', ModItems.SPRING.get())
            .define('r', Items.RED_DYE)
            .pattern("  ^")
            .pattern(" -r")
            .pattern("-  ")
            .unlockedBy("has_item", has(ModItems.SPRING.get()))
            .save(output)
    }

    class Runner(output: PackOutput, lookupProvider: CompletableFuture<HolderLookup.Provider>) :
        RecipeProvider.Runner(output, lookupProvider) {

        @Override
        override fun createRecipeProvider(lookupProvider: HolderLookup.Provider, output: RecipeOutput): RecipeProvider {
            return ModRecipeProvider(CombinedOutPut(output), lookupProvider)
        }

        @Override
        override fun getName(): String {
            return "HumVee recipes"
        }
    }

    class CombinedOutPut(private val output: RecipeOutput) : RecipeOutput {

        private val graph = RecipeGraph()

        override fun accept(
            key: ResourceKey<Recipe<*>?>,
            recipe: Recipe<*>,
            advancement: AdvancementHolder?,
            vararg conditions: ICondition?
        ) {
            output.accept(key, recipe, advancement, *conditions)
            graph.accept(key, recipe, advancement, *conditions)
        }

        override fun advancement(): Advancement.Builder {
            graph.advancement()
            return output.advancement()
        }

        override fun includeRootAdvancement() {
            output.includeRootAdvancement()
            graph.includeRootAdvancement()
        }

    }
}
