package dev.murad.shipping.data;

import dev.murad.shipping.setup.ModBlocks;
import dev.murad.shipping.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ModLootTableProvider extends LootTableProvider {

    public ModLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(
                output,
                Set.of(),
                List.of(new SubProviderEntry(ModBlockLootTables::new, LootContextParamSets.BLOCK)),
                lookupProvider
        );
    }

    public static class ModBlockLootTables extends BlockLootSubProvider {
        ModBlockLootTables(HolderLookup.Provider provider) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
        }

        @Override
        protected void generate() {
            dropSelf(ModBlocks.TUG_DOCK.get());
            dropSelf(ModBlocks.BARGE_DOCK.get());
            dropSelf(ModBlocks.GUIDE_RAIL_CORNER.get());
            dropSelf(ModBlocks.GUIDE_RAIL_TUG.get());
            dropSelf(ModBlocks.FLUID_HOPPER.get());
            dropSelf(ModBlocks.VESSEL_CHARGER.get());
            dropSelf(ModBlocks.VESSEL_DETECTOR.get());
            dropSelf(ModBlocks.SWITCH_RAIL.get());
            dropSelf(ModBlocks.AUTOMATIC_SWITCH_RAIL.get());
            dropSelf(ModBlocks.TEE_JUNCTION_RAIL.get());
            dropSelf(ModBlocks.AUTOMATIC_TEE_JUNCTION_RAIL.get());
            dropSelf(ModBlocks.JUNCTION_RAIL.get());
            dropSelf(ModBlocks.RAPID_HOPPER.get());
            dropSelf(ModBlocks.CAR_DOCK_RAIL.get());
            dropSelf(ModBlocks.LOCOMOTIVE_DOCK_RAIL.get());
        }

        @Override
        public @NotNull Iterable<Block> getKnownBlocks() {
            return Registration.BLOCKS.getEntries().stream()
                    .map(DeferredHolder::get)
                    .collect(Collectors.toList());
        }
    }
}
