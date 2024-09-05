package dev.murad.shipping.setup;

import dev.murad.shipping.ShippingMod;
import dev.murad.shipping.entity.custom.train.locomotive.AbstractLocomotiveEntity;
import dev.murad.shipping.entity.custom.train.locomotive.EnergyLocomotiveEntity;
import dev.murad.shipping.entity.custom.train.locomotive.SteamLocomotiveEntity;
import dev.murad.shipping.entity.custom.train.wagon.ChestCarEntity;
import dev.murad.shipping.entity.custom.train.wagon.ChunkLoaderCarEntity;
import dev.murad.shipping.entity.custom.train.wagon.FluidTankCarEntity;
import dev.murad.shipping.entity.custom.train.wagon.SeaterCarEntity;
import dev.murad.shipping.entity.custom.vessel.barge.*;
import dev.murad.shipping.entity.custom.vessel.tug.EnergyTugEntity;
import dev.murad.shipping.entity.custom.vessel.tug.SteamTugEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

import static dev.murad.shipping.setup.Registration.ENTITIES;


public class ModEntityTypes {

    public static final Supplier<EntityType<ChestBargeEntity>> CHEST_BARGE =
            ENTITIES.register("barge",
                    () -> EntityType.Builder.<ChestBargeEntity>of(ChestBargeEntity::new, MobCategory.MISC).sized(0.6f, 0.9f)
                            .clientTrackingRange(8)
                            .build(ResourceLocation.fromNamespaceAndPath(ShippingMod.MOD_ID, "barge").toString()));

    public static final Supplier<EntityType<ChestBargeEntity>> BARREL_BARGE =
            ENTITIES.register("barrel_barge",
                    () -> EntityType.Builder.<ChestBargeEntity>of(ChestBargeEntity::new,
                                    MobCategory.MISC).sized(0.6f, 0.9f)
                            .clientTrackingRange(8)
                            .build(ResourceLocation.fromNamespaceAndPath(ShippingMod.MOD_ID, "barrel_barge").toString()));

    public static final Supplier<EntityType<ChunkLoaderBargeEntity>> CHUNK_LOADER_BARGE =
            ENTITIES.register("chunk_loader_barge",
                    () -> EntityType.Builder.<ChunkLoaderBargeEntity>of(ChunkLoaderBargeEntity::new,
                                    MobCategory.MISC).sized(0.6f, 0.9f)
                            .clientTrackingRange(8)
                            .build(ResourceLocation.fromNamespaceAndPath(ShippingMod.MOD_ID, "chunk_loader_barge").toString()));

    public static final Supplier<EntityType<FishingBargeEntity>> FISHING_BARGE =
            ENTITIES.register("fishing_barge",
                    () -> EntityType.Builder.<FishingBargeEntity>of(FishingBargeEntity::new,
                                    MobCategory.MISC).sized(0.6f, 0.9f)
                            .clientTrackingRange(8)
                            .build(ResourceLocation.fromNamespaceAndPath(ShippingMod.MOD_ID, "fishing_barge").toString()));

    public static final Supplier<EntityType<FluidTankBargeEntity>> FLUID_TANK_BARGE =
            ENTITIES.register("fluid_barge",
                    () -> EntityType.Builder.<FluidTankBargeEntity>of(FluidTankBargeEntity::new,
                                    MobCategory.MISC).sized(0.6f, 0.9f)
                            .clientTrackingRange(8)
                            .build(ResourceLocation.fromNamespaceAndPath(ShippingMod.MOD_ID, "fluid_barge").toString()));

    public static final Supplier<EntityType<SeaterBargeEntity>> SEATER_BARGE =
            ENTITIES.register("seater_barge",
                    () -> EntityType.Builder.<SeaterBargeEntity>of(SeaterBargeEntity::new,
                                    MobCategory.MISC).sized(0.6f, 0.9f)
                            .clientTrackingRange(8)
                            .build(ResourceLocation.fromNamespaceAndPath(ShippingMod.MOD_ID, "seater_barge").toString()));

    public static final Supplier<EntityType<VacuumBargeEntity>> VACUUM_BARGE =
            ENTITIES.register("vacuum_barge",
                    () -> EntityType.Builder.<VacuumBargeEntity>of(VacuumBargeEntity::new,
                                    MobCategory.MISC).sized(0.6f, 0.9f)
                            .clientTrackingRange(8)
                            .build(ResourceLocation.fromNamespaceAndPath(ShippingMod.MOD_ID, "vacuum_barge").toString()));

    public static final Supplier<EntityType<SteamTugEntity>> STEAM_TUG =
            ENTITIES.register("tug",
                    () -> EntityType.Builder.<SteamTugEntity>of(SteamTugEntity::new,
                                    MobCategory.MISC).sized(0.7f, 0.9f)
                            .clientTrackingRange(8)
                            .build(ResourceLocation.fromNamespaceAndPath(ShippingMod.MOD_ID, "tug").toString()));

    public static final Supplier<EntityType<EnergyTugEntity>> ENERGY_TUG =
            ENTITIES.register("energy_tug",
                    () -> EntityType.Builder.<EnergyTugEntity>of(EnergyTugEntity::new,
                                    MobCategory.MISC).sized(0.7f, 0.9f)
                            .clientTrackingRange(8)
                            .build(ResourceLocation.fromNamespaceAndPath(ShippingMod.MOD_ID, "energy_tug").toString()));

    public static final Supplier<EntityType<ChestCarEntity>> CHEST_CAR =
            ENTITIES.register("chest_car",
                    () -> EntityType.Builder.<ChestCarEntity>of(ChestCarEntity::new,
                                    MobCategory.MISC).sized(0.7f, 0.9f)
                            .clientTrackingRange(8)
                            .setShouldReceiveVelocityUpdates(true)
                            .build(ResourceLocation.fromNamespaceAndPath(ShippingMod.MOD_ID, "chest_car").toString()));

    public static final Supplier<EntityType<ChestCarEntity>> BARREL_CAR =
            ENTITIES.register("barrel_car",
                    () -> EntityType.Builder.<ChestCarEntity>of(ChestCarEntity::new,
                                    MobCategory.MISC).sized(0.7f, 0.9f)
                            .clientTrackingRange(8)
                            .setShouldReceiveVelocityUpdates(true)
                            .build(ResourceLocation.fromNamespaceAndPath(ShippingMod.MOD_ID, "barrel_car").toString()));

    public static final Supplier<EntityType<SeaterCarEntity>> SEATER_CAR =
            ENTITIES.register("seater_car",
                    () -> EntityType.Builder.<SeaterCarEntity>of(SeaterCarEntity::new,
                                    MobCategory.MISC).sized(0.7f, 0.9f)
                            .clientTrackingRange(8)
                            .setShouldReceiveVelocityUpdates(true)
                            .build(ResourceLocation.fromNamespaceAndPath(ShippingMod.MOD_ID, "seater_car").toString()));

    public static final Supplier<EntityType<FluidTankCarEntity>> FLUID_CAR =
            ENTITIES.register("fluid_car",
                    () -> EntityType.Builder.<FluidTankCarEntity>of(FluidTankCarEntity::new,
                                    MobCategory.MISC).sized(0.7f, 0.9f)
                            .clientTrackingRange(8)
                            .setShouldReceiveVelocityUpdates(true)
                            .build(ResourceLocation.fromNamespaceAndPath(ShippingMod.MOD_ID, "fluid_car").toString()));

    public static final Supplier<EntityType<ChunkLoaderCarEntity>> CHUNK_LOADER_CAR =
            ENTITIES.register("chunk_loader_car",
                    () -> EntityType.Builder.<ChunkLoaderCarEntity>of(ChunkLoaderCarEntity::new,
                                    MobCategory.MISC).sized(0.7f, 0.9f)
                            .clientTrackingRange(8)
                            .setShouldReceiveVelocityUpdates(true)
                            .build(ResourceLocation.fromNamespaceAndPath(ShippingMod.MOD_ID, "chunk_loader_car").toString()));


    public static final Supplier<EntityType<AbstractLocomotiveEntity>> STEAM_LOCOMOTIVE =
            ENTITIES.register("steam_locomotive",
                    () -> EntityType.Builder.<AbstractLocomotiveEntity>of(SteamLocomotiveEntity::new,
                                    MobCategory.MISC).sized(0.7f, 0.9f)
                            .clientTrackingRange(8)
                            .setShouldReceiveVelocityUpdates(true)
                            .build(ResourceLocation.fromNamespaceAndPath(ShippingMod.MOD_ID, "steam_locomotive").toString()));

    public static final Supplier<EntityType<AbstractLocomotiveEntity>> ENERGY_LOCOMOTIVE =
            ENTITIES.register("energy_locomotive",
                    () -> EntityType.Builder.<AbstractLocomotiveEntity>of(EnergyLocomotiveEntity::new,
                                    MobCategory.MISC)
                            .clientTrackingRange(8)
                            .setShouldReceiveVelocityUpdates(true)
                            .sized(0.7f, 0.9f)
                            .build(ResourceLocation.fromNamespaceAndPath(ShippingMod.MOD_ID, "energy_locomotive").toString()));
    
}
