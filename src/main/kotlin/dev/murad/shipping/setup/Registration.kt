package dev.murad.shipping.setup

import dev.murad.shipping.HumVeeMod
import dev.murad.shipping.network.TugRoutePacketHandler
import dev.murad.shipping.network.VehiclePacketHandler
import dev.murad.shipping.network.client.VehicleTrackerPacketHandler
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.entity.EntityType
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.fluids.CauldronFluidContent
import net.neoforged.neoforge.registries.DeferredRegister

object Registration {

    val BLOCKS: DeferredRegister<Block> = createRegister(BuiltInRegistries.BLOCK)
    val CONTAINERS: DeferredRegister<MenuType<*>> = createRegister(BuiltInRegistries.MENU)
    val ENTITIES: DeferredRegister<EntityType<*>> = createRegister(BuiltInRegistries.ENTITY_TYPE)
    val ITEMS: DeferredRegister<Item> = createRegister(BuiltInRegistries.ITEM)
    val RECIPE_SERIALIZERS: DeferredRegister<RecipeSerializer<*>> = createRegister(BuiltInRegistries.RECIPE_SERIALIZER)
    val TILE_ENTITIES: DeferredRegister<BlockEntityType<*>> = createRegister(BuiltInRegistries.BLOCK_ENTITY_TYPE)
    val SOUND_EVENTS: DeferredRegister<SoundEvent> = createRegister(BuiltInRegistries.SOUND_EVENT)


    private fun <T> createRegister(registry: Registry<T>): DeferredRegister<T> {
        return DeferredRegister.create(registry, HumVeeMod.MOD_ID)
    }

    fun register(eventBus: IEventBus) {
        BLOCKS.register(eventBus)
        ITEMS.register(eventBus)
        CONTAINERS.register(eventBus)
        RECIPE_SERIALIZERS.register(eventBus)
        TILE_ENTITIES.register(eventBus)
        ENTITIES.register(eventBus)
        SOUND_EVENTS.register(eventBus)

        //TODO static calls used to ensure correct loading sequence
        ModItems.register()
        ModBlocks.register()
        ModEntityTypes.register()
        ModTileEntitiesTypes.register()
        ModRecipeSerializers.register()
        ModMenuTypes.register()
        eventBus.register(TugRoutePacketHandler)
        eventBus.register(VehicleTrackerPacketHandler)
        eventBus.register(VehiclePacketHandler)
        ModSounds.register()
        eventBus.addListener { event: RegisterCapabilitiesEvent -> CauldronFluidContent.registerCapabilities(event) }

        ModDataComponents.initialise(eventBus)
    }
}
