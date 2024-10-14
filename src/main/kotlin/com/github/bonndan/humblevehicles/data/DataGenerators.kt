package com.github.bonndan.humblevehicles.data

import com.github.bonndan.humblevehicles.HumVeeMod
import com.github.bonndan.humblevehicles.data.client.ModBlockStateProvider
import com.github.bonndan.humblevehicles.data.client.ModItemModelProvider
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent

@EventBusSubscriber(modid = HumVeeMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object DataGenerators {

    @SubscribeEvent
    fun gatherData(gatherDataEvent: GatherDataEvent) {
        val gen = gatherDataEvent.generator
        val existingFileHelper = gatherDataEvent.existingFileHelper
        val pack = gen.packOutput
        val lookupProvider = gatherDataEvent.lookupProvider

        gen.addProvider(true, ModBlockStateProvider(pack, existingFileHelper))
        gen.addProvider(true, ModItemModelProvider(pack, existingFileHelper))

        val blockTags = ModBlockTagsProvider(pack, lookupProvider, existingFileHelper)
        gen.addProvider(true, blockTags)
        gen.addProvider(true, ModItemTagsProvider(pack, lookupProvider, blockTags.contentsGetter(), existingFileHelper))
        gen.addProvider(true, ModLootTableProvider(pack, lookupProvider))

        val modRecipeProvider = ModRecipeProvider(pack, lookupProvider)
        gen.addProvider(true, modRecipeProvider)
        if (gatherDataEvent.includeDev()) {
            val graph = RecipeGraph(modRecipeProvider)
            graph.build()
        }
    }
}
