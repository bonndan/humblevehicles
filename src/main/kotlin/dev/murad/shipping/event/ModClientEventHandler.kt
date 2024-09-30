package dev.murad.shipping.event

import dev.murad.shipping.HumVeeMod
import dev.murad.shipping.block.fluid.render.FluidHopperTileEntityRenderer
import dev.murad.shipping.entity.custom.train.AbstractTrainCarEntity
import dev.murad.shipping.entity.custom.train.wagon.FluidTankCarEntity
import dev.murad.shipping.entity.custom.vessel.VesselEntity
import dev.murad.shipping.entity.custom.vessel.barge.FishingBargeEntity
import dev.murad.shipping.entity.custom.vessel.barge.FluidTankBargeEntity
import dev.murad.shipping.entity.models.SubmarineModel
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
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.ItemEntityRenderer
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent

/**
 * Mod-specific event bus
 */
@EventBusSubscriber(modid = HumVeeMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
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
        ) { ctx: EntityRendererProvider.Context ->
            MultipartVesselRenderer.Builder<VesselEntity>(ctx)
                .baseModel(
                    { root -> BaseBargeModel(root) }, BaseBargeModel.CLOSED_LOCATION,
                    HumVeeMod.entityTexture("barge/base.png")
                )
                .insertModel(
                    { root -> CubeInsertBargeModel(root) }, CubeInsertBargeModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("barge/chest_insert.png")
                )
                .trimModel(
                    { root -> TrimBargeModel(root) }, TrimBargeModel.CLOSED_LOCATION,
                    HumVeeMod.entityTexture("barge/trim.png")
                )
                .build()
        }

        event.registerEntityRenderer(
            ModEntityTypes.BARREL_BARGE.get()
        ) { ctx: EntityRendererProvider.Context ->
            MultipartVesselRenderer.Builder<VesselEntity>(ctx)
                .baseModel(
                    { root -> BaseBargeModel(root) }, BaseBargeModel.CLOSED_LOCATION,
                    HumVeeMod.entityTexture("barge/base.png")
                )
                .insertModel(
                    { root -> CubeInsertBargeModel(root) }, CubeInsertBargeModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("barge/barrel_insert.png")
                )
                .trimModel(
                    { root -> TrimBargeModel(root) }, TrimBargeModel.CLOSED_LOCATION,
                    HumVeeMod.entityTexture("barge/trim.png")
                )
                .build()
        }

        event.registerEntityRenderer(
            ModEntityTypes.CHUNK_LOADER_BARGE.get()
        ) { ctx: EntityRendererProvider.Context ->
            MultipartVesselRenderer.Builder<VesselEntity>(ctx)
                .baseModel(
                    { root -> BaseBargeModel(root) }, BaseBargeModel.CLOSED_LOCATION,
                    HumVeeMod.entityTexture("barge/base.png")
                )
                .insertModel(
                    { root -> RingsInsertBargeModel(root) }, RingsInsertBargeModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("barge/chunk_loader_insert.png")
                )
                .trimModel(
                    { root -> TrimBargeModel(root) }, TrimBargeModel.CLOSED_LOCATION,
                    HumVeeMod.entityTexture("barge/trim.png")
                )
                .build()
        }

        event.registerEntityRenderer(
            ModEntityTypes.SEATER_BARGE.get()
        ) { ctx: EntityRendererProvider.Context ->
            MultipartVesselRenderer.Builder<VesselEntity>(ctx)
                .baseModel(
                    { root -> BaseBargeModel(root) }, BaseBargeModel.OPEN_FRONT_LOCATION,
                    HumVeeMod.entityTexture("barge/base.png")
                )
                .insertModel(
                    { root -> SeaterInsertBargeModel(root) }, SeaterInsertBargeModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("barge/seater_insert.png")
                )
                .trimModel(
                    { root -> TrimBargeModel(root) }, TrimBargeModel.OPEN_FRONT_LOCATION,
                    HumVeeMod.entityTexture("barge/trim.png")
                )
                .build()
        }

        event.registerEntityRenderer(
            ModEntityTypes.VACUUM_BARGE.get()
        ) { ctx: EntityRendererProvider.Context ->
            MultipartVesselRenderer.Builder<VesselEntity>(ctx)
                .baseModel(
                    { root -> BaseBargeModel(root) }, BaseBargeModel.CLOSED_LOCATION,
                    HumVeeMod.entityTexture("barge/base.png")
                )
                .insertModel(
                    { root -> RingsInsertBargeModel(root) }, RingsInsertBargeModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("barge/vacuum_insert.png")
                )
                .trimModel(
                    { root -> TrimBargeModel(root) }, TrimBargeModel.CLOSED_LOCATION,
                    HumVeeMod.entityTexture("barge/trim.png")
                )
                .build()
        }

        event.registerEntityRenderer(
            ModEntityTypes.FLUID_TANK_BARGE.get()
        ) { ctx: EntityRendererProvider.Context ->
            FluidTankBargeRenderer.Builder<FluidTankBargeEntity>(ctx)
                .baseModel(
                    { root -> BaseBargeModel(root) }, BaseBargeModel.CLOSED_LOCATION,
                    HumVeeMod.entityTexture("barge/base.png")
                )
                .insertModel(
                    { root -> FluidTankInsertBargeModel(root) }, FluidTankInsertBargeModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("barge/fluid_tank_insert.png")
                )
                .trimModel(
                    { root -> TrimBargeModel(root) }, TrimBargeModel.CLOSED_LOCATION,
                    HumVeeMod.entityTexture("barge/trim.png")
                )
                .build()
        }

        event.registerEntityRenderer(
            ModEntityTypes.FISHING_BARGE.get()
        ) { ctx: EntityRendererProvider.Context ->
            FishingBargeRenderer.Builder<FishingBargeEntity>(ctx)
                .transitionInsertModel(
                    { root -> FishingInsertBargeModel(root) }, FishingInsertBargeModel.TRANSITION_LOCATION,
                    HumVeeMod.entityTexture("barge/fishing_insert.png")
                )
                .deployedInsertModel(
                    { root -> FishingInsertBargeModel(root) }, FishingInsertBargeModel.DEPLOYED_LOCATION,
                    HumVeeMod.entityTexture("barge/fishing_insert.png")
                )
                .baseModel(
                    { root -> BaseBargeModel(root) }, BaseBargeModel.OPEN_SIDES_LOCATION,
                    HumVeeMod.entityTexture("barge/base.png")
                )
                .insertModel(
                    { root -> FishingInsertBargeModel(root) }, FishingInsertBargeModel.STASHED_LOCATION,
                    HumVeeMod.entityTexture("barge/fishing_insert.png")
                )
                .trimModel(
                    { root -> TrimBargeModel(root) }, TrimBargeModel.OPEN_SIDES_LOCATION,
                    HumVeeMod.entityTexture("barge/trim.png")
                )
                .build()
        }

        // Tugs
        event.registerEntityRenderer(
            ModEntityTypes.ENERGY_TUG.get()
        ) { ctx: EntityRendererProvider.Context ->
            MultipartVesselRenderer.Builder<VesselEntity>(ctx)
                .baseModel(
                    { root -> EnergyTugModel(root) }, EnergyTugModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("barge/energy_tug_base.png")
                )
                .emptyInsert()
                .trimModel(
                    { root -> EnergyTugModel(root) }, EnergyTugModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("barge/energy_tug_trim.png")
                )
                .build() // TODO: this is a hack
                .derotate()
        }

        event.registerEntityRenderer(ModEntityTypes.STEAM_TUG.get()) { ctx: EntityRendererProvider.Context ->
            MultipartVesselRenderer.Builder<VesselEntity>(ctx)
                .baseModel(
                    { root -> SteamTugModel(root) }, SteamTugModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("barge/steam_tug_base.png")
                )
                .emptyInsert()
                .trimModel(
                    { root -> SteamTugModel(root) }, SteamTugModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("barge/steam_tug_trim.png")
                )
                .build()
                .derotate()
        }

        event.registerEntityRenderer(ModEntityTypes.STEAM_LOCOMOTIVE.get()) { ctx: EntityRendererProvider.Context ->
            MultipartCarRenderer.Builder<AbstractTrainCarEntity>(ctx)
                .baseModel(
                    { root -> SteamLocomotiveModel(root) },
                    SteamLocomotiveModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("car/steam_locomotive_base.png")
                )
                .trimModel(
                    { root -> SteamLocomotiveModel(root) },
                    SteamLocomotiveModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("car/steam_locomotive_trim.png")
                )
                .emptyInsert()
                .build()
        }

        event.registerEntityRenderer(ModEntityTypes.ENERGY_LOCOMOTIVE.get()) { ctx: EntityRendererProvider.Context ->
            MultipartCarRenderer.Builder<AbstractTrainCarEntity>(ctx)
                .baseModel(
                    { root -> EnergyLocomotiveModel(root) },
                    EnergyLocomotiveModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("car/energy_locomotive_base.png")
                )
                .trimModel(
                    { root -> EnergyLocomotiveModel(root) },
                    EnergyLocomotiveModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("car/energy_locomotive_trim.png")
                )
                .emptyInsert()
                .build()
        }

        event.registerEntityRenderer(ModEntityTypes.CHEST_CAR.get()) { ctx: EntityRendererProvider.Context ->
            MultipartCarRenderer.Builder<AbstractTrainCarEntity>(ctx)
                .baseModel(
                    { root -> BaseCarModel(root) },
                    BaseCarModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("car/base.png")
                )
                .trimModel(
                    { root -> TrimCarModel(root) },
                    TrimCarModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("car/trim.png")
                )
                .insertModel(
                    { root -> CubeInsertCarModel(root) },
                    CubeInsertCarModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("car/chest_insert.png")
                )
                .build()
        }

        event.registerEntityRenderer(ModEntityTypes.BARREL_CAR.get()) { ctx: EntityRendererProvider.Context ->
            MultipartCarRenderer.Builder<AbstractTrainCarEntity>(ctx)
                .baseModel(
                    { root -> BaseCarModel(root) },
                    BaseCarModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("car/base.png")
                )
                .trimModel(
                    { root -> TrimCarModel(root) },
                    TrimCarModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("car/trim.png")
                )
                .insertModel(
                    { root -> CubeInsertCarModel(root) },
                    CubeInsertCarModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("car/barrel_insert.png")
                )
                .build()
        }

        event.registerEntityRenderer(ModEntityTypes.FLUID_CAR.get()) { ctx: EntityRendererProvider.Context ->
            FluidTankCarRenderer.Builder<FluidTankCarEntity>(ctx)
                .baseModel(
                    { root -> BaseCarModel(root) },
                    BaseCarModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("car/base.png")
                )
                .trimModel(
                    { root -> TrimCarModel(root) },
                    TrimCarModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("car/trim.png")
                )
                .insertModel(
                    { root -> FluidTankInsertCarModel(root) },
                    FluidTankInsertCarModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("car/fluid_tank_insert.png")
                )
                .build()
        }

        event.registerEntityRenderer(ModEntityTypes.CHUNK_LOADER_CAR.get()) { ctx: EntityRendererProvider.Context ->
            TrainCarRenderer(
                ctx,
                { root -> ChunkLoaderCarModel(root!!) },
                ChunkLoaderCarModel.LAYER_LOCATION,
                "textures/entity/chunk_loader_car.png"
            )
        }

        event.registerEntityRenderer(ModEntityTypes.SEATER_CAR.get()) { ctx: EntityRendererProvider.Context ->
            MultipartCarRenderer.Builder<AbstractTrainCarEntity>(ctx)
                .baseModel(
                    { root -> BaseCarModel(root) },
                    BaseCarModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("car/base.png")
                )
                .trimModel(
                    { root -> TrimCarModel(root) },
                    TrimCarModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("car/trim.png")
                )
                .emptyInsert()
                .build()
        }

        event.registerBlockEntityRenderer(ModTileEntitiesTypes.FLUID_HOPPER.get()) { context ->
            FluidHopperTileEntityRenderer(context)
        }

        //SUBMARINE
        event.registerEntityRenderer(ModEntityTypes.SUBMARINE.get()) { ctx: EntityRendererProvider.Context ->
            MultipartVesselRenderer.Builder<VesselEntity>(ctx)
                .baseModel(
                    { root -> SubmarineModel(root) },
                    SubmarineModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("submarine.png")
                )
                .emptyInsert()
                .trimModel(
                    { root -> SubmarineModel(root) },
                    SubmarineModel.LAYER_LOCATION,
                    HumVeeMod.entityTexture("submarine.png")
                )
                .build()
                .derotate()
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

        //SUBMARINE
        event.registerLayerDefinition(SubmarineModel.LAYER_LOCATION) { SubmarineModel.createBodyLayer() }
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
