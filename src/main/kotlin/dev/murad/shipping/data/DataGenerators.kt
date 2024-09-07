package dev.murad.shipping.data

import dev.murad.shipping.ShippingMod
import dev.murad.shipping.data.client.ModBlockStateProvider
import dev.murad.shipping.data.client.ModItemModelProvider
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent

@EventBusSubscriber(modid = ShippingMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
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
        gen.addProvider(true, ModRecipeProvider(pack, lookupProvider))
    }
}
