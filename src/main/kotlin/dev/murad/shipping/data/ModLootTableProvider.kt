package dev.murad.shipping.data

import dev.murad.shipping.setup.ModBlocks
import dev.murad.shipping.setup.Registration
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import java.util.concurrent.CompletableFuture

class ModLootTableProvider(output: PackOutput, lookupProvider: CompletableFuture<HolderLookup.Provider?>) :
    LootTableProvider(
        output,
        setOf(),
        listOf(
            SubProviderEntry(
                { provider: HolderLookup.Provider -> ModBlockLootTables(provider) },
                LootContextParamSets.BLOCK
            )
        ),
        lookupProvider
    ) {
    class ModBlockLootTables internal constructor(provider: HolderLookup.Provider) :
        BlockLootSubProvider(setOf(), FeatureFlags.REGISTRY.allFlags(), provider) {
        override fun generate() {
            dropSelf(ModBlocks.TUG_DOCK.get())
            dropSelf(ModBlocks.BARGE_DOCK.get())
            dropSelf(ModBlocks.GUIDE_RAIL_CORNER.get())
            dropSelf(ModBlocks.GUIDE_RAIL_TUG.get())
            dropSelf(ModBlocks.FLUID_HOPPER.get())
            dropSelf(ModBlocks.VESSEL_CHARGER.get())
            dropSelf(ModBlocks.VESSEL_DETECTOR.get())
            dropSelf(ModBlocks.SWITCH_RAIL.get())
            dropSelf(ModBlocks.AUTOMATIC_SWITCH_RAIL.get())
            dropSelf(ModBlocks.TEE_JUNCTION_RAIL.get())
            dropSelf(ModBlocks.AUTOMATIC_TEE_JUNCTION_RAIL.get())
            dropSelf(ModBlocks.JUNCTION_RAIL.get())
            dropSelf(ModBlocks.CAR_DOCK_RAIL.get())
            dropSelf(ModBlocks.LOCOMOTIVE_DOCK_RAIL.get())
        }

        public override fun getKnownBlocks(): Iterable<Block> = Registration.BLOCKS.entries.map { obj -> obj.get() }
    }
}
