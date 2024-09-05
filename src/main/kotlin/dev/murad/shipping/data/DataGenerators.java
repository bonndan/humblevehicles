package dev.murad.shipping.data;

import dev.murad.shipping.ShippingMod;
import dev.murad.shipping.data.client.ModBlockStateProvider;
import dev.murad.shipping.data.client.ModItemModelProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = ShippingMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class DataGenerators {
    private DataGenerators () {}

    @SubscribeEvent
    public static void gatherData(GatherDataEvent gatherDataEvent){
        var gen = gatherDataEvent.getGenerator();
        var existingFileHelper = gatherDataEvent.getExistingFileHelper();
        var pack = gen.getPackOutput();
        var lookupProvider = gatherDataEvent.getLookupProvider();

        gen.addProvider(true, new ModBlockStateProvider(pack, existingFileHelper));
        gen.addProvider(true, new ModItemModelProvider(pack, existingFileHelper));

        var blockTags = new ModBlockTagsProvider(pack, lookupProvider, existingFileHelper);
        gen.addProvider(true, blockTags);
        gen.addProvider(true, new ModItemTagsProvider(pack, lookupProvider, blockTags.contentsGetter(), existingFileHelper));
        gen.addProvider(true, new ModLootTableProvider(pack,lookupProvider));
        gen.addProvider(true, new ModRecipeProvider(pack, lookupProvider));
    }

}
