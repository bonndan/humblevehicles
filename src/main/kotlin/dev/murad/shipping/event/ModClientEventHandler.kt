package dev.murad.shipping.event

import dev.murad.shipping.ShippingMod
import dev.murad.shipping.block.fluid.render.FluidHopperTileEntityRenderer
import dev.murad.shipping.entity.custom.train.AbstractTrainCarEntity
import dev.murad.shipping.entity.custom.train.wagon.FluidTankCarEntity
import dev.murad.shipping.entity.custom.vessel.VesselEntity
import dev.murad.shipping.entity.custom.vessel.barge.FishingBargeEntity
import dev.murad.shipping.entity.custom.vessel.barge.FluidTankBargeEntity
import dev.murad.shipping.entity.models.insert.*
import dev.murad.shipping.entity.models.train.*
import dev.murad.shipping.entity.models.vessel.EmptyModel
import dev.murad.shipping.entity.models.vessel.EnergyTugModel
import dev.murad.shipping.entity.models.vessel.SteamTugModel
import dev.murad.shipping.entity.models.vessel.base.BaseBargeModel
import dev.murad.shipping.entity.models.vessel.base.TrimBargeModel
import dev.murad.shipping.entity.render.barge.FishingBargeRenderer
import dev.murad.shipping.entity.render.barge.FluidTankBargeRenderer
import dev.murad.shipping.entity.render.barge.MultipartVesselRenderer
import dev.murad.shipping.entity.render.train.FluidTankCarRenderer
import dev.murad.shipping.entity.render.train.MultipartCarRenderer
import dev.murad.shipping.entity.render.train.TrainCarRenderer
import dev.murad.shipping.setup.ModBlocks
import dev.murad.shipping.setup.ModBlocks.buildCreativeTab
import dev.murad.shipping.setup.ModEntityTypes
import dev.murad.shipping.setup.ModItems
import dev.murad.shipping.setup.ModTileEntitiesTypes
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent

/**
 * Mod-specific event bus
 */
@EventBusSubscriber(modid = ShippingMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ModClientEventHandler {
    @SubscribeEvent
    fun onRenderTypeSetup(event: FMLClientSetupEvent) {
        event.enqueueWork {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.FLUID_HOPPER.get(), RenderType.cutoutMipped())
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.VESSEL_CHARGER.get(), RenderType.cutoutMipped())
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
        // Barges
        event.registerEntityRenderer(
            ModEntityTypes.CHEST_BARGE.get()
        ) { ctx: EntityRendererProvider.Context? ->
            MultipartVesselRenderer.Builder<VesselEntity>(ctx)
                .baseModel(
                    { root: ModelPart? -> BaseBargeModel(root) }, BaseBargeModel.CLOSED_LOCATION,
                    ShippingMod.entityTexture("barge/base.png")
                )
                .insertModel(
                    { root: ModelPart? -> CubeInsertBargeModel(root) }, CubeInsertBargeModel.LAYER_LOCATION,
                    ShippingMod.entityTexture("barge/chest_insert.png")
                )
                .trimModel(
                    { root: ModelPart? -> TrimBargeModel(root) }, TrimBargeModel.CLOSED_LOCATION,
                    ShippingMod.entityTexture("barge/trim.png")
                )
                .build()
        }

        event.registerEntityRenderer(
            ModEntityTypes.BARREL_BARGE.get()
        ) { ctx: EntityRendererProvider.Context? ->
            MultipartVesselRenderer.Builder<VesselEntity>(ctx)
                .baseModel(
                    { root: ModelPart? -> BaseBargeModel(root) }, BaseBargeModel.CLOSED_LOCATION,
                    ShippingMod.entityTexture("barge/base.png")
                )
                .insertModel(
                    { root: ModelPart? -> CubeInsertBargeModel(root) }, CubeInsertBargeModel.LAYER_LOCATION,
                    ShippingMod.entityTexture("barge/barrel_insert.png")
                )
                .trimModel(
                    { root: ModelPart? -> TrimBargeModel(root) }, TrimBargeModel.CLOSED_LOCATION,
                    ShippingMod.entityTexture("barge/trim.png")
                )
                .build()
        }

        event.registerEntityRenderer(
            ModEntityTypes.CHUNK_LOADER_BARGE.get()
        ) { ctx: EntityRendererProvider.Context? ->
            MultipartVesselRenderer.Builder<VesselEntity>(ctx)
                .baseModel(
                    { root: ModelPart? -> BaseBargeModel(root) }, BaseBargeModel.CLOSED_LOCATION,
                    ShippingMod.entityTexture("barge/base.png")
                )
                .insertModel(
                    { root: ModelPart? -> RingsInsertBargeModel(root) }, RingsInsertBargeModel.LAYER_LOCATION,
                    ShippingMod.entityTexture("barge/chunk_loader_insert.png")
                )
                .trimModel(
                    { root: ModelPart? -> TrimBargeModel(root) }, TrimBargeModel.CLOSED_LOCATION,
                    ShippingMod.entityTexture("barge/trim.png")
                )
                .build()
        }

        event.registerEntityRenderer(
            ModEntityTypes.SEATER_BARGE.get()
        ) { ctx: EntityRendererProvider.Context? ->
            MultipartVesselRenderer.Builder<VesselEntity>(ctx)
                .baseModel(
                    { root: ModelPart? -> BaseBargeModel(root) }, BaseBargeModel.OPEN_FRONT_LOCATION,
                    ShippingMod.entityTexture("barge/base.png")
                )
                .insertModel(
                    { root: ModelPart? -> SeaterInsertBargeModel(root) }, SeaterInsertBargeModel.LAYER_LOCATION,
                    ShippingMod.entityTexture("barge/seater_insert.png")
                )
                .trimModel(
                    { root: ModelPart? -> TrimBargeModel(root) }, TrimBargeModel.OPEN_FRONT_LOCATION,
                    ShippingMod.entityTexture("barge/trim.png")
                )
                .build()
        }

        event.registerEntityRenderer(
            ModEntityTypes.VACUUM_BARGE.get()
        ) { ctx: EntityRendererProvider.Context? ->
            MultipartVesselRenderer.Builder<VesselEntity>(ctx)
                .baseModel(
                    { root: ModelPart? -> BaseBargeModel(root) }, BaseBargeModel.CLOSED_LOCATION,
                    ShippingMod.entityTexture("barge/base.png")
                )
                .insertModel(
                    { root: ModelPart? -> RingsInsertBargeModel(root) }, RingsInsertBargeModel.LAYER_LOCATION,
                    ShippingMod.entityTexture("barge/vacuum_insert.png")
                )
                .trimModel(
                    { root: ModelPart? -> TrimBargeModel(root) }, TrimBargeModel.CLOSED_LOCATION,
                    ShippingMod.entityTexture("barge/trim.png")
                )
                .build()
        }

        event.registerEntityRenderer(
            ModEntityTypes.FLUID_TANK_BARGE.get()
        ) { ctx: EntityRendererProvider.Context? ->
            FluidTankBargeRenderer.Builder<FluidTankBargeEntity>(ctx)
                .baseModel(
                    { root: ModelPart? -> BaseBargeModel(root) }, BaseBargeModel.CLOSED_LOCATION,
                    ShippingMod.entityTexture("barge/base.png")
                )
                .insertModel(
                    { root: ModelPart? -> FluidTankInsertBargeModel(root) }, FluidTankInsertBargeModel.LAYER_LOCATION,
                    ShippingMod.entityTexture("barge/fluid_tank_insert.png")
                )
                .trimModel(
                    { root: ModelPart? -> TrimBargeModel(root) }, TrimBargeModel.CLOSED_LOCATION,
                    ShippingMod.entityTexture("barge/trim.png")
                )
                .build()
        }

        event.registerEntityRenderer(
            ModEntityTypes.FISHING_BARGE.get()
        ) { ctx: EntityRendererProvider.Context? ->
            FishingBargeRenderer.Builder<FishingBargeEntity>(ctx)
                .transitionInsertModel(
                    { root: ModelPart? -> FishingInsertBargeModel(root) }, FishingInsertBargeModel.TRANSITION_LOCATION,
                    ShippingMod.entityTexture("barge/fishing_insert.png")
                )
                .deployedInsertModel(
                    { root: ModelPart? -> FishingInsertBargeModel(root) }, FishingInsertBargeModel.DEPLOYED_LOCATION,
                    ShippingMod.entityTexture("barge/fishing_insert.png")
                )
                .baseModel(
                    { root: ModelPart? -> BaseBargeModel(root) }, BaseBargeModel.OPEN_SIDES_LOCATION,
                    ShippingMod.entityTexture("barge/base.png")
                )
                .insertModel(
                    { root: ModelPart? -> FishingInsertBargeModel(root) }, FishingInsertBargeModel.STASHED_LOCATION,
                    ShippingMod.entityTexture("barge/fishing_insert.png")
                )
                .trimModel(
                    { root: ModelPart? -> TrimBargeModel(root) }, TrimBargeModel.OPEN_SIDES_LOCATION,
                    ShippingMod.entityTexture("barge/trim.png")
                )
                .build()
        }

        // Tugs
        event.registerEntityRenderer(
            ModEntityTypes.ENERGY_TUG.get()
        ) { ctx: EntityRendererProvider.Context? ->
            MultipartVesselRenderer.Builder<VesselEntity>(ctx)
                .baseModel(
                    { root: ModelPart? -> EnergyTugModel(root) }, EnergyTugModel.LAYER_LOCATION,
                    ShippingMod.entityTexture("barge/energy_tug_base.png")
                )
                .emptyInsert()
                .trimModel(
                    { root: ModelPart? -> EnergyTugModel(root) }, EnergyTugModel.LAYER_LOCATION,
                    ShippingMod.entityTexture("barge/energy_tug_trim.png")
                )
                .build() // TODO: this is a hack
                .derotate()
        }

        event.registerEntityRenderer(
            ModEntityTypes.STEAM_TUG.get()
        ) { ctx: EntityRendererProvider.Context? ->
            MultipartVesselRenderer.Builder<VesselEntity>(ctx)
                .baseModel(
                    { root: ModelPart? -> SteamTugModel(root) }, SteamTugModel.LAYER_LOCATION,
                    ShippingMod.entityTexture("barge/steam_tug_base.png")
                )
                .emptyInsert()
                .trimModel(
                    { root: ModelPart? -> SteamTugModel(root) }, SteamTugModel.LAYER_LOCATION,
                    ShippingMod.entityTexture("barge/steam_tug_trim.png")
                )
                .build()
                .derotate()
        }

        event.registerEntityRenderer(ModEntityTypes.STEAM_LOCOMOTIVE.get()) { ctx: EntityRendererProvider.Context? ->
            MultipartCarRenderer.Builder<AbstractTrainCarEntity>(ctx)
                .baseModel(
                    { root: ModelPart? -> SteamLocomotiveModel(root) },
                    SteamLocomotiveModel.LAYER_LOCATION,
                    ShippingMod.entityTexture("car/steam_locomotive_base.png")
                )
                .trimModel(
                    { root: ModelPart? -> SteamLocomotiveModel(root) },
                    SteamLocomotiveModel.LAYER_LOCATION,
                    ShippingMod.entityTexture("car/steam_locomotive_trim.png")
                )
                .emptyInsert()
                .build()
        }

        event.registerEntityRenderer(ModEntityTypes.ENERGY_LOCOMOTIVE.get()) { ctx: EntityRendererProvider.Context? ->
            MultipartCarRenderer.Builder<AbstractTrainCarEntity>(ctx)
                .baseModel(
                    { root: ModelPart? -> EnergyLocomotiveModel(root) },
                    EnergyLocomotiveModel.LAYER_LOCATION,
                    ShippingMod.entityTexture("car/energy_locomotive_base.png")
                )
                .trimModel(
                    { root: ModelPart? -> EnergyLocomotiveModel(root) },
                    EnergyLocomotiveModel.LAYER_LOCATION,
                    ShippingMod.entityTexture("car/energy_locomotive_trim.png")
                )
                .emptyInsert()
                .build()
        }

        event.registerEntityRenderer(ModEntityTypes.CHEST_CAR.get()) { ctx: EntityRendererProvider.Context? ->
            MultipartCarRenderer.Builder<AbstractTrainCarEntity>(ctx)
                .baseModel(
                    { root: ModelPart? -> BaseCarModel(root) },
                    BaseCarModel.LAYER_LOCATION,
                    ShippingMod.entityTexture("car/base.png")
                )
                .trimModel(
                    { root: ModelPart? -> TrimCarModel(root) },
                    TrimCarModel.LAYER_LOCATION,
                    ShippingMod.entityTexture("car/trim.png")
                )
                .insertModel(
                    { root: ModelPart? -> CubeInsertCarModel(root) },
                    CubeInsertCarModel.LAYER_LOCATION,
                    ShippingMod.entityTexture("car/chest_insert.png")
                )
                .build()
        }

        event.registerEntityRenderer(ModEntityTypes.BARREL_CAR.get()) { ctx: EntityRendererProvider.Context? ->
            MultipartCarRenderer.Builder<AbstractTrainCarEntity>(ctx)
                .baseModel(
                    { root: ModelPart? -> BaseCarModel(root) },
                    BaseCarModel.LAYER_LOCATION,
                    ShippingMod.entityTexture("car/base.png")
                )
                .trimModel(
                    { root: ModelPart? -> TrimCarModel(root) },
                    TrimCarModel.LAYER_LOCATION,
                    ShippingMod.entityTexture("car/trim.png")
                )
                .insertModel(
                    { root: ModelPart? -> CubeInsertCarModel(root) },
                    CubeInsertCarModel.LAYER_LOCATION,
                    ShippingMod.entityTexture("car/barrel_insert.png")
                )
                .build()
        }

        event.registerEntityRenderer(ModEntityTypes.FLUID_CAR.get()) { ctx: EntityRendererProvider.Context? ->
            FluidTankCarRenderer.Builder<FluidTankCarEntity>(ctx)
                .baseModel(
                    { root: ModelPart? -> BaseCarModel(root) },
                    BaseCarModel.LAYER_LOCATION,
                    ShippingMod.entityTexture("car/base.png")
                )
                .trimModel(
                    { root: ModelPart? -> TrimCarModel(root) },
                    TrimCarModel.LAYER_LOCATION,
                    ShippingMod.entityTexture("car/trim.png")
                )
                .insertModel(
                    { root: ModelPart? -> FluidTankInsertCarModel(root) },
                    FluidTankInsertCarModel.LAYER_LOCATION,
                    ShippingMod.entityTexture("car/fluid_tank_insert.png")
                )
                .build()
        }

        event.registerEntityRenderer(ModEntityTypes.CHUNK_LOADER_CAR.get()) { ctx: EntityRendererProvider.Context? ->
            TrainCarRenderer(
                ctx,
                { root: ModelPart? -> ChunkLoaderCarModel(root) },
                ChunkLoaderCarModel.LAYER_LOCATION,
                "textures/entity/chunk_loader_car.png"
            )
        }

        event.registerEntityRenderer(ModEntityTypes.SEATER_CAR.get()) { ctx: EntityRendererProvider.Context? ->
            MultipartCarRenderer.Builder<AbstractTrainCarEntity>(ctx)
                .baseModel(
                    { root: ModelPart? -> BaseCarModel(root) },
                    BaseCarModel.LAYER_LOCATION,
                    ShippingMod.entityTexture("car/base.png")
                )
                .trimModel(
                    { root: ModelPart? -> TrimCarModel(root) },
                    TrimCarModel.LAYER_LOCATION,
                    ShippingMod.entityTexture("car/trim.png")
                )
                .emptyInsert()
                .build()
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

        event.registerLayerDefinition(CubeInsertBargeModel.LAYER_LOCATION) { CubeInsertBargeModel.createBodyLayer() }
        event.registerLayerDefinition(CubeInsertCarModel.LAYER_LOCATION) { CubeInsertCarModel.createBodyLayer() }

        // VESSEL
        event.registerLayerDefinition(BaseBargeModel.CLOSED_LOCATION) { BaseBargeModel.createBodyLayer(true, true) }
        event.registerLayerDefinition(BaseBargeModel.OPEN_FRONT_LOCATION) {
            BaseBargeModel.createBodyLayer(
                false,
                true
            )
        }
        event.registerLayerDefinition(BaseBargeModel.OPEN_SIDES_LOCATION) {
            BaseBargeModel.createBodyLayer(
                true,
                false
            )
        }

        event.registerLayerDefinition(TrimBargeModel.CLOSED_LOCATION) { TrimBargeModel.createBodyLayer(true, true) }
        event.registerLayerDefinition(TrimBargeModel.OPEN_FRONT_LOCATION) {
            TrimBargeModel.createBodyLayer(
                false,
                true
            )
        }
        event.registerLayerDefinition(TrimBargeModel.OPEN_SIDES_LOCATION) {
            TrimBargeModel.createBodyLayer(
                true,
                false
            )
        }

        event.registerLayerDefinition(RingsInsertBargeModel.LAYER_LOCATION) { RingsInsertBargeModel.createBodyLayer() }
        event.registerLayerDefinition(SeaterInsertBargeModel.LAYER_LOCATION) { SeaterInsertBargeModel.createBodyLayer() }
        event.registerLayerDefinition(FluidTankInsertBargeModel.LAYER_LOCATION) { FluidTankInsertBargeModel.createBodyLayer() }

        event.registerLayerDefinition(FishingInsertBargeModel.STASHED_LOCATION) {
            FishingInsertBargeModel.createBodyLayer(
                FishingBargeEntity.Status.STASHED
            )
        }
        event.registerLayerDefinition(FishingInsertBargeModel.TRANSITION_LOCATION) {
            FishingInsertBargeModel.createBodyLayer(
                FishingBargeEntity.Status.TRANSITION
            )
        }
        event.registerLayerDefinition(FishingInsertBargeModel.DEPLOYED_LOCATION) {
            FishingInsertBargeModel.createBodyLayer(
                FishingBargeEntity.Status.DEPLOYED
            )
        }

        event.registerLayerDefinition(EnergyTugModel.LAYER_LOCATION) { EnergyTugModel.createBodyLayer() }
        event.registerLayerDefinition(SteamTugModel.LAYER_LOCATION) { SteamTugModel.createBodyLayer() }

        // CAR
        event.registerLayerDefinition(TrimCarModel.LAYER_LOCATION) { TrimCarModel.createBodyLayer() }
        event.registerLayerDefinition(BaseCarModel.LAYER_LOCATION) { BaseCarModel.createBodyLayer() }
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
}
