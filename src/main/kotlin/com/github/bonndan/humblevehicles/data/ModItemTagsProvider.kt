package com.github.bonndan.humblevehicles.data

import com.github.bonndan.humblevehicles.HumVeeMod
import com.github.bonndan.humblevehicles.setup.ModItems
import com.github.bonndan.humblevehicles.setup.ModTags
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.ItemTagsProvider
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class ModItemTagsProvider(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider?>,
    blockTagProvider: CompletableFuture<TagLookup<Block?>?>,
    existingFileHelper: ExistingFileHelper?
) :
    ItemTagsProvider(output, lookupProvider, blockTagProvider, HumVeeMod.MOD_ID, existingFileHelper) {
    override fun addTags(lookupProvider: HolderLookup.Provider) {
        tag(ModTags.Items.WRENCHES).add(ModItems.CONDUCTORS_WRENCH.get())
    }
}
