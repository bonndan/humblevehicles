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
    @JvmField
    val BLOCKS: DeferredRegister<Block> = create(BuiltInRegistries.BLOCK)
    @JvmField
    val CONTAINERS: DeferredRegister<MenuType<*>> = create(BuiltInRegistries.MENU)
    @JvmField
    val ENTITIES: DeferredRegister<EntityType<*>> = create(BuiltInRegistries.ENTITY_TYPE)
    @JvmField
    val ITEMS: DeferredRegister<Item> = create(BuiltInRegistries.ITEM)
    @JvmField
    val RECIPE_SERIALIZERS: DeferredRegister<RecipeSerializer<*>> = create(BuiltInRegistries.RECIPE_SERIALIZER)
    @JvmField
    val TILE_ENTITIES: DeferredRegister<BlockEntityType<*>> = create(BuiltInRegistries.BLOCK_ENTITY_TYPE)
    @JvmField
    val SOUND_EVENTS: DeferredRegister<SoundEvent> = create(BuiltInRegistries.SOUND_EVENT)


    private fun <T> create(registry: Registry<T>): DeferredRegister<T> {
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

        ModItems.register()

        ModTileEntitiesTypes.register()
        ModRecipeSerializers.register()
        ModMenuTypes.register()
        eventBus.register(TugRoutePacketHandler::class.java)
        eventBus.register(VehicleTrackerPacketHandler::class.java)
        eventBus.register(VehiclePacketHandler::class.java)
        ModSounds.register()
        eventBus.addListener { event: RegisterCapabilitiesEvent? -> CauldronFluidContent.registerCapabilities(event) }

        ModDataComponents.initialise(eventBus)
    }
}
