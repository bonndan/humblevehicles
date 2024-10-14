package com.github.bonndan.humblevehicles.data

import com.github.bonndan.humblevehicles.HumVeeMod
import com.github.bonndan.humblevehicles.setup.ModBlocks
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.tags.BlockTags
import net.neoforged.neoforge.common.data.BlockTagsProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class ModBlockTagsProvider(
    output: PackOutput,
    registries: CompletableFuture<HolderLookup.Provider?>,
    existingFileHelper: ExistingFileHelper?
) :
    BlockTagsProvider(output, registries, HumVeeMod.MOD_ID, existingFileHelper) {
    override fun addTags(lookupProvider: HolderLookup.Provider) {
        tag(BlockTags.RAILS).add(ModBlocks.SWITCH_RAIL.get())
        tag(BlockTags.RAILS).add(ModBlocks.AUTOMATIC_SWITCH_RAIL.get())
        tag(BlockTags.RAILS).add(ModBlocks.TEE_JUNCTION_RAIL.get())
        tag(BlockTags.RAILS).add(ModBlocks.AUTOMATIC_TEE_JUNCTION_RAIL.get())
        tag(BlockTags.RAILS).add(ModBlocks.JUNCTION_RAIL.get())
        tag(BlockTags.RAILS).add(ModBlocks.LOCOMOTIVE_DOCK_RAIL.get())
        tag(BlockTags.RAILS).add(ModBlocks.CAR_DOCK_RAIL.get())
    }
}
