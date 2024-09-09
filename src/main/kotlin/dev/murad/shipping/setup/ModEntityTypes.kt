package dev.murad.shipping.setup

import dev.murad.shipping.HumVeeMod
import dev.murad.shipping.entity.custom.train.locomotive.AbstractLocomotiveEntity
import dev.murad.shipping.entity.custom.train.locomotive.EnergyLocomotiveEntity
import dev.murad.shipping.entity.custom.train.locomotive.SteamLocomotiveEntity
import dev.murad.shipping.entity.custom.train.wagon.ChestCarEntity
import dev.murad.shipping.entity.custom.train.wagon.ChunkLoaderCarEntity
import dev.murad.shipping.entity.custom.train.wagon.FluidTankCarEntity
import dev.murad.shipping.entity.custom.train.wagon.SeaterCarEntity
import dev.murad.shipping.entity.custom.vessel.barge.*
import dev.murad.shipping.entity.custom.vessel.tug.EnergyTugEntity
import dev.murad.shipping.entity.custom.vessel.tug.SteamTugEntity
import dev.murad.shipping.setup.Registration.ENTITIES
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.level.Level
import java.util.function.Supplier

object ModEntityTypes {

    @JvmField
    val CHEST_BARGE: Supplier<EntityType<ChestBargeEntity>> =
        ENTITIES.register("barge", Supplier<EntityType<ChestBargeEntity>> {
            EntityType.Builder.of({ type: EntityType<ChestBargeEntity>, world: Level? ->
                ChestBargeEntity(
                    type, world
                )
            }, MobCategory.MISC).sized(0.6f, 0.9f).clientTrackingRange(8)
                .build(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "barge").toString())
        })

    @JvmField
    val BARREL_BARGE: Supplier<EntityType<ChestBargeEntity>> =
        ENTITIES.register("barrel_barge", Supplier<EntityType<ChestBargeEntity>> {
            EntityType.Builder.of<ChestBargeEntity?>(
                { type: EntityType<ChestBargeEntity?>?, world: Level? -> ChestBargeEntity(type, world) },
                MobCategory.MISC
            ).sized(0.6f, 0.9f).clientTrackingRange(8)
                .build(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "barrel_barge").toString())
        })

    @JvmField
    val CHUNK_LOADER_BARGE: Supplier<EntityType<ChunkLoaderBargeEntity>> =
        ENTITIES.register("chunk_loader_barge", Supplier<EntityType<ChunkLoaderBargeEntity>> {
            EntityType.Builder.of(
                { type: EntityType<ChunkLoaderBargeEntity>, world: Level? ->
                    ChunkLoaderBargeEntity(
                        type, world
                    )
                }, MobCategory.MISC
            ).sized(0.6f, 0.9f).clientTrackingRange(8)
                .build(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "chunk_loader_barge").toString())
        })

    @JvmField
    val FISHING_BARGE: Supplier<EntityType<FishingBargeEntity>> =
        ENTITIES.register("fishing_barge", Supplier<EntityType<FishingBargeEntity>> {
            EntityType.Builder.of(
                { type: EntityType<FishingBargeEntity>, world: Level? -> FishingBargeEntity(type, world) },
                MobCategory.MISC
            ).sized(0.6f, 0.9f).clientTrackingRange(8)
                .build(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "fishing_barge").toString())
        })

    @JvmField
    val FLUID_TANK_BARGE: Supplier<EntityType<FluidTankBargeEntity>> =
        ENTITIES.register("fluid_barge", Supplier<EntityType<FluidTankBargeEntity>> {
            EntityType.Builder.of(
                { type: EntityType<FluidTankBargeEntity>, world: Level? -> FluidTankBargeEntity(type, world) },
                MobCategory.MISC
            ).sized(0.6f, 0.9f).clientTrackingRange(8)
                .build(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "fluid_barge").toString())
        })

    @JvmField
    val SEATER_BARGE: Supplier<EntityType<SeaterBargeEntity>> =
        ENTITIES.register("seater_barge", Supplier<EntityType<SeaterBargeEntity>> {
            EntityType.Builder.of(
                { type: EntityType<SeaterBargeEntity>, world: Level? -> SeaterBargeEntity(type, world) },
                MobCategory.MISC
            ).sized(0.6f, 0.9f).clientTrackingRange(8)
                .build(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "seater_barge").toString())
        })

    @JvmField
    val VACUUM_BARGE: Supplier<EntityType<VacuumBargeEntity>> =
        ENTITIES.register("vacuum_barge", Supplier<EntityType<VacuumBargeEntity>> {
            EntityType.Builder.of(
                { type: EntityType<VacuumBargeEntity>, world: Level? -> VacuumBargeEntity(type, world) },
                MobCategory.MISC
            ).sized(0.6f, 0.9f).clientTrackingRange(8)
                .build(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "vacuum_barge").toString())
        })

    @JvmField
    val STEAM_TUG: Supplier<EntityType<SteamTugEntity>> =
        ENTITIES.register("tug", Supplier<EntityType<SteamTugEntity>> {
            EntityType.Builder.of(
                { type: EntityType<SteamTugEntity>, world -> SteamTugEntity(type, world) }, MobCategory.MISC
            ).sized(0.7f, 0.9f).clientTrackingRange(8)
                .build(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "tug").toString())
        })

    @JvmField
    val ENERGY_TUG: Supplier<EntityType<EnergyTugEntity>> =
        ENTITIES.register("energy_tug", Supplier<EntityType<EnergyTugEntity>> {
            EntityType.Builder.of(
                { type: EntityType<EnergyTugEntity>, world -> EnergyTugEntity(type, world) }, MobCategory.MISC
            ).sized(0.7f, 0.9f).clientTrackingRange(8)
                .build(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "energy_tug").toString())
        })

    val CHEST_CAR: Supplier<EntityType<ChestCarEntity>> =
        ENTITIES.register("chest_car", Supplier<EntityType<ChestCarEntity>> {
            EntityType.Builder.of(
                { type: EntityType<ChestCarEntity>, level: Level -> ChestCarEntity(type, level) }, MobCategory.MISC
            ).sized(0.7f, 0.9f).clientTrackingRange(8).setShouldReceiveVelocityUpdates(true)
                .build(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "chest_car").toString())
        })

    @JvmField
    val BARREL_CAR: Supplier<EntityType<ChestCarEntity>> =
        ENTITIES.register("barrel_car", Supplier<EntityType<ChestCarEntity>> {
            EntityType.Builder.of(
                { type: EntityType<ChestCarEntity>, level: Level -> ChestCarEntity(type, level) }, MobCategory.MISC
            ).sized(0.7f, 0.9f).clientTrackingRange(8).setShouldReceiveVelocityUpdates(true)
                .build(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "barrel_car").toString())
        })

    @JvmField
    val SEATER_CAR: Supplier<EntityType<SeaterCarEntity>> =
        ENTITIES.register("seater_car", Supplier<EntityType<SeaterCarEntity>> {
            EntityType.Builder.of({ type, level -> SeaterCarEntity(type, level) }, MobCategory.MISC)
                .sized(0.7f, 0.9f)
                .clientTrackingRange(8).setShouldReceiveVelocityUpdates(true)
                .build(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "seater_car").toString())
        })

    @JvmField
    val FLUID_CAR: Supplier<EntityType<FluidTankCarEntity>> =
        ENTITIES.register("fluid_car", Supplier<EntityType<FluidTankCarEntity>> {
            EntityType.Builder.of(
                { type, level -> FluidTankCarEntity(type, level) }, MobCategory.MISC
            ).sized(0.7f, 0.9f).clientTrackingRange(8).setShouldReceiveVelocityUpdates(true)
                .build(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "fluid_car").toString())
        })

    @JvmField
    val CHUNK_LOADER_CAR: Supplier<EntityType<ChunkLoaderCarEntity>> =
        ENTITIES.register("chunk_loader_car", Supplier<EntityType<ChunkLoaderCarEntity>> {
            EntityType.Builder.of(
                { type, level ->
                    ChunkLoaderCarEntity(
                        type, level
                    )
                }, MobCategory.MISC
            ).sized(0.7f, 0.9f).clientTrackingRange(8).setShouldReceiveVelocityUpdates(true)
                .build(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "chunk_loader_car").toString())
        })


    @JvmField
    val STEAM_LOCOMOTIVE: Supplier<EntityType<AbstractLocomotiveEntity>> =
        ENTITIES.register("steam_locomotive", Supplier<EntityType<AbstractLocomotiveEntity>> {
            EntityType.Builder.of(
                { type: EntityType<AbstractLocomotiveEntity>, level: Level ->
                    SteamLocomotiveEntity(
                        type, level
                    )
                }, MobCategory.MISC
            ).sized(0.7f, 0.9f).clientTrackingRange(8).setShouldReceiveVelocityUpdates(true)
                .build(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "steam_locomotive").toString())
        })

    @JvmField
    val ENERGY_LOCOMOTIVE: Supplier<EntityType<AbstractLocomotiveEntity>> =
        ENTITIES.register("energy_locomotive", Supplier<EntityType<AbstractLocomotiveEntity>> {
            EntityType.Builder.of(
                { type: EntityType<AbstractLocomotiveEntity>, level: Level ->
                    EnergyLocomotiveEntity(type, level)
                }, MobCategory.MISC
            ).clientTrackingRange(8).setShouldReceiveVelocityUpdates(true).sized(0.7f, 0.9f)
                .build(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "energy_locomotive").toString())
        })
}
