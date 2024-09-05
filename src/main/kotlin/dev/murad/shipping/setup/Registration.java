package dev.murad.shipping.setup;

import dev.murad.shipping.ShippingMod;
import dev.murad.shipping.network.TugRoutePacketHandler;
import dev.murad.shipping.network.VehiclePacketHandler;
import dev.murad.shipping.network.client.VehicleTrackerPacketHandler;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.CauldronFluidContent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Registration  {

    public static final DeferredRegister<Block> BLOCKS = create(BuiltInRegistries.BLOCK);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = create(BuiltInRegistries.MENU);
    public static final DeferredRegister<EntityType<?>> ENTITIES = create(BuiltInRegistries.ENTITY_TYPE);
    public static final DeferredRegister<Item> ITEMS = create(BuiltInRegistries.ITEM);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = create(BuiltInRegistries.RECIPE_SERIALIZER);
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = create(BuiltInRegistries.BLOCK_ENTITY_TYPE);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = create(BuiltInRegistries.SOUND_EVENT);


    private static<T> DeferredRegister<T> create(Registry<T> registry) {
        return DeferredRegister.create(
                // The registry we want to use.
                // Minecraft's registries can be found in BuiltInRegistries, NeoForge's registries can be found in NeoForgeRegistries.
                // Mods may also add their own registries, refer to the individual mod's documentation or source code for where to find them.
                registry,
                // Our mod id.
                ShippingMod.MOD_ID
        );
    }

    public static void register (IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        CONTAINERS.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);
        TILE_ENTITIES.register(eventBus);
        ENTITIES.register(eventBus);
        SOUND_EVENTS.register(eventBus);

        ModItems.register();
        ModBlocks.register();
        ModTileEntitiesTypes.register();
        ModRecipeSerializers.register();
        ModMenuTypes.register();
        eventBus.register(TugRoutePacketHandler.class);
        eventBus.register(VehicleTrackerPacketHandler.class);
        eventBus.register(VehiclePacketHandler.class);
        ModSounds.register();
        eventBus.addListener(CauldronFluidContent::registerCapabilities);

        ModDataComponents.initialise(eventBus);
    }
}
