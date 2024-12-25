package com.github.bonndan.humblevehicles.setup

import com.github.bonndan.humblevehicles.HumVeeMod
import com.github.bonndan.humblevehicles.entity.custom.train.locomotive.AbstractLocomotiveEntity
import com.github.bonndan.humblevehicles.entity.custom.train.locomotive.EnergyLocomotiveEntity
import com.github.bonndan.humblevehicles.entity.custom.train.locomotive.SteamLocomotiveEntity
import com.github.bonndan.humblevehicles.entity.custom.train.wagon.ChestCarEntity
import com.github.bonndan.humblevehicles.entity.custom.train.wagon.ChunkLoaderCarEntity
import com.github.bonndan.humblevehicles.entity.custom.train.wagon.FluidTankCarEntity
import com.github.bonndan.humblevehicles.entity.custom.train.wagon.SeaterCarEntity
import com.github.bonndan.humblevehicles.setup.Registration.ENTITIES
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.level.Level
import java.util.function.Supplier

private const val MINECART_DIMENSION_WIDTH = 1.25f
private const val MINECART_DIMENSION_HEIGHT = 1.0f

object ModEntityTypes {

    fun register() {
    }

    val CHEST_CAR: Supplier<EntityType<ChestCarEntity>> =
        ENTITIES.register("chest_car", Supplier<EntityType<ChestCarEntity>> {
            EntityType.Builder.of(
                { type: EntityType<ChestCarEntity>, level: Level -> ChestCarEntity(type, level) }, MobCategory.MISC
            ).sized(MINECART_DIMENSION_WIDTH, MINECART_DIMENSION_HEIGHT).clientTrackingRange(8)
                .setShouldReceiveVelocityUpdates(true)
                .build(asResourceKey("chest_car"))
        })


    val BARREL_CAR: Supplier<EntityType<ChestCarEntity>> =
        ENTITIES.register("barrel_car", Supplier<EntityType<ChestCarEntity>> {
            EntityType.Builder.of(
                { type: EntityType<ChestCarEntity>, level: Level -> ChestCarEntity(type, level) }, MobCategory.MISC
            ).sized(0.7f, MINECART_DIMENSION_HEIGHT).clientTrackingRange(8).setShouldReceiveVelocityUpdates(true)
                .build(asResourceKey("barrel_car"))
        })


    val SEATER_CAR: Supplier<EntityType<SeaterCarEntity>> =
        ENTITIES.register("seater_car", Supplier<EntityType<SeaterCarEntity>> {
            EntityType.Builder.of({ type, level -> SeaterCarEntity(type, level) }, MobCategory.MISC)
                .sized(0.7f, MINECART_DIMENSION_HEIGHT)
                .clientTrackingRange(8)
                .setShouldReceiveVelocityUpdates(true)
                .build(asResourceKey("seater_car"))
        })


    val FLUID_CAR: Supplier<EntityType<FluidTankCarEntity>> =
        ENTITIES.register("fluid_car", Supplier<EntityType<FluidTankCarEntity>> {
            EntityType.Builder.of(
                { type, level -> FluidTankCarEntity(type, level) }, MobCategory.MISC
            ).sized(MINECART_DIMENSION_WIDTH, MINECART_DIMENSION_HEIGHT)
                .clientTrackingRange(8)
                .setShouldReceiveVelocityUpdates(true)
                .build(asResourceKey("fluid_car"))
        })


    val CHUNK_LOADER_CAR: Supplier<EntityType<ChunkLoaderCarEntity>> =
        ENTITIES.register("chunk_loader_car", Supplier<EntityType<ChunkLoaderCarEntity>> {
            EntityType.Builder.of(
                { type, level ->
                    ChunkLoaderCarEntity(
                        type, level
                    )
                }, MobCategory.MISC
            ).sized(MINECART_DIMENSION_WIDTH, MINECART_DIMENSION_HEIGHT).clientTrackingRange(8).setShouldReceiveVelocityUpdates(true)
                .build(asResourceKey("chunk_loader_car"))
        })

    val STEAM_LOCOMOTIVE: Supplier<EntityType<AbstractLocomotiveEntity>> =
        ENTITIES.register("steam_locomotive", Supplier<EntityType<AbstractLocomotiveEntity>> {
            EntityType.Builder
                .of(
                    { type: EntityType<AbstractLocomotiveEntity>, level: Level -> SteamLocomotiveEntity(type, level) },
                    MobCategory.MISC
                )
                .sized(0.9f, 0.9f)
                .clientTrackingRange(8)
                .setShouldReceiveVelocityUpdates(true)
                .build(asResourceKey("steam_locomotive"))
        })

    val ENERGY_LOCOMOTIVE: Supplier<EntityType<AbstractLocomotiveEntity>> = ENTITIES.register(
        "energy_locomotive", Supplier<EntityType<AbstractLocomotiveEntity>> {
            EntityType.Builder.of(
                { type: EntityType<AbstractLocomotiveEntity>, level: Level ->
                    EnergyLocomotiveEntity(type, level)
                }, MobCategory.MISC
            ).clientTrackingRange(8).setShouldReceiveVelocityUpdates(true).sized(0.7f, 0.9f)
                .build(asResourceKey("energy_locomotive"))
        })


    private fun asResourceKey(path: String): ResourceKey<EntityType<*>?> = ResourceKey.create(
        Registries.ENTITY_TYPE,
        ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, path)
    )
}
