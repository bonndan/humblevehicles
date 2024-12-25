package com.github.bonndan.humblevehicles.event

import com.github.bonndan.humblevehicles.HumVeeMod.Companion.MOD_ID
import com.github.bonndan.humblevehicles.block.fluid.render.FluidHopperTileEntityRenderer
import com.github.bonndan.humblevehicles.entity.models.EmptyModel
import com.github.bonndan.humblevehicles.entity.models.insert.*
import com.github.bonndan.humblevehicles.entity.models.train.*
import com.github.bonndan.humblevehicles.entity.render.AbstractMinecartRendererCopy
import com.github.bonndan.humblevehicles.entity.render.RendererConfig
import com.github.bonndan.humblevehicles.setup.ModBlocks
import com.github.bonndan.humblevehicles.setup.ModBlocks.buildCreativeTab
import com.github.bonndan.humblevehicles.setup.ModEntityTypes
import com.github.bonndan.humblevehicles.setup.ModItems
import com.github.bonndan.humblevehicles.setup.ModTileEntitiesTypes
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent

/**
 * Mod-specific event bus
 */
@EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ModClientEventHandler {

    @SubscribeEvent
    fun onRenderTypeSetup(event: FMLClientSetupEvent) {

        event.enqueueWork {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.FLUID_HOPPER.get(), RenderType.cutoutMipped())
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.JUNCTION_RAIL.get(), RenderType.cutoutMipped())
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.SWITCH_RAIL.get(), RenderType.cutoutMipped())
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.AUTOMATIC_SWITCH_RAIL.get(), RenderType.cutoutMipped())
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.TEE_JUNCTION_RAIL.get(), RenderType.cutoutMipped())
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.AUTOMATIC_TEE_JUNCTION_RAIL.get(), RenderType.cutoutMipped())
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.CAR_DOCK_RAIL.get(), RenderType.cutoutMipped())
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.LOCOMOTIVE_DOCK_RAIL.get(), RenderType.cutoutMipped())
        }
    }

    @SubscribeEvent
    fun onRegisterEntityRenderers(event: EntityRenderersEvent.RegisterRenderers) {

        event.registerEntityRenderer(ModEntityTypes.STEAM_LOCOMOTIVE.get()) { ctx: EntityRendererProvider.Context ->
            AbstractMinecartRendererCopy(
                context = ctx,
                RendererConfig(
                    layer = SteamLocomotiveModel.LAYER_LOCATION,
                    textureLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/entity/car/steam_locomotive_base.png"),
                    modelSupplier = { part -> SteamLocomotiveModel(part) },
                    modelYRotation = 90f,
                    colorTexture = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/entity/car/steam_locomotive_trim.png"),
                    colorLayer = SteamLocomotiveModel.LAYER_LOCATION
                )
            )
        }


//            event.registerEntityRenderer(ModEntityTypes.ENERGY_LOCOMOTIVE.get()) { ctx: EntityRendererProvider.Context ->
//                TrainRenderer(ctx, EnergyLocomotiveModel.LAYER_LOCATION)
//            MultipartCarRenderer.Builder<AbstractTrainCarEntity>(ctx)
//                .baseModel(
//                    { root -> EnergyLocomotiveModel(root) },
//                    EnergyLocomotiveModel.LAYER_LOCATION,
//                    entityTexture("car/energy_locomotive_base.png")
//                )
//                .trimModel(
//                    { root -> EnergyLocomotiveModel(root) },
//                    EnergyLocomotiveModel.LAYER_LOCATION,
//                    entityTexture("car/energy_locomotive_trim.png")
//                )
//                .emptyInsert()
//                .build()

        event.registerEntityRenderer(ModEntityTypes.CHEST_CAR.get()) { ctx: EntityRendererProvider.Context ->
            AbstractMinecartRendererCopy(
                context = ctx,
                RendererConfig(
                    colorTexture = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/entity/car/trim.png"),
                    colorLayer = TrimCarModel.LAYER_LOCATION,
                    blockStateYOffset = -0.5f
                )
            )
        }

        event.registerEntityRenderer(ModEntityTypes.FLUID_CAR.get()) { ctx: EntityRendererProvider.Context ->
            AbstractMinecartRendererCopy(
                context = ctx,
                RendererConfig(
                    colorTexture = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/entity/car/trim.png"),
                    colorLayer = TrimCarModel.LAYER_LOCATION,
                    colorModelSupplier = { part -> TrimCarModel(part) },
                    colorModelYOffset = -1.0f,
                    colorModelYRotation = 90f,
                    blockStateYOffset = -0.5f
                )
            )
        }

        event.registerEntityRenderer(ModEntityTypes.CHUNK_LOADER_CAR.get()) { ctx: EntityRendererProvider.Context ->
            AbstractMinecartRendererCopy(
                context = ctx,
                RendererConfig(
                    layer = ChunkLoaderCarModel.LAYER_LOCATION,
                    textureLocation = ResourceLocation.fromNamespaceAndPath(
                        MOD_ID,
                        "textures/entity/chunk_loader_car.png"
                    ),
                    colorTexture = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/entity/car/trim.png"),
                    colorLayer = TrimCarModel.LAYER_LOCATION,
                )
            )
        }


        event.registerBlockEntityRenderer(ModTileEntitiesTypes.FLUID_HOPPER.get()) { context ->
            FluidHopperTileEntityRenderer(context)
        }
    }

    @SubscribeEvent
    fun onRegisterEntityRenderers(event: EntityRenderersEvent.RegisterLayerDefinitions) {

        // COMMON
        event.registerLayerDefinition(ChainExtendedModel.LAYER_LOCATION) { ChainExtendedModel.createBodyLayer() }
        event.registerLayerDefinition(ChainModel.LAYER_LOCATION) { ChainModel.createBodyLayer() }

        event.registerLayerDefinition(EmptyModel.LAYER_LOCATION) { EmptyModel.createBodyLayer() }

        event.registerLayerDefinition(CubeInsertCarModel.LAYER_LOCATION) { CubeInsertCarModel.createBodyLayer() }

        // CAR
        event.registerLayerDefinition(TrimCarModel.LAYER_LOCATION) { TrimCarModel.createBodyLayer() }
        event.registerLayerDefinition(FluidTankInsertCarModel.LAYER_LOCATION) { FluidTankInsertCarModel.createBodyLayer() }

        event.registerLayerDefinition(SteamLocomotiveModel.LAYER_LOCATION) { SteamLocomotiveModel.createBodyLayer() }
        event.registerLayerDefinition(EnergyLocomotiveModel.LAYER_LOCATION) { EnergyLocomotiveModel.createBodyLayer() }

        // LEGACY
        event.registerLayerDefinition(ChunkLoaderCarModel.LAYER_LOCATION) { ChunkLoaderCarModel.createBodyLayer() }
    }

    /**
     * Subscribe to event when building each creative mode tab. Items are added to tabs here.
     * @param event The creative tab currently being built
     */
    @SubscribeEvent
    fun buildTabContents(event: BuildCreativeModeTabContentsEvent) {
        buildCreativeTab(event)
        ModItems.buildCreativeTab(event)
    }

    private fun entityTexture(suffix: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format("textures/entity/%s", suffix))
    }
}
