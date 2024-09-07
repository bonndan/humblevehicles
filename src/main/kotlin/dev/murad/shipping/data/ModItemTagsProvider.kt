package dev.murad.shipping.data

import dev.murad.shipping.ShippingMod
import dev.murad.shipping.setup.ModItems
import dev.murad.shipping.setup.ModTags
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
    ItemTagsProvider(output, lookupProvider, blockTagProvider, ShippingMod.MOD_ID, existingFileHelper) {
    override fun addTags(lookupProvider: HolderLookup.Provider) {
        tag(ModTags.Items.WRENCHES).add(ModItems.CONDUCTORS_WRENCH.get())
    }
}
