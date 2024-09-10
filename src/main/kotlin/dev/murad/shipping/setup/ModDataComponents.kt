package dev.murad.shipping.setup

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import dev.murad.shipping.HumVeeMod
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.Unit
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.energy.EnergyStorage
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier
import java.util.function.UnaryOperator

/**
 * Registers this mod's [DataComponentTypes][DataComponentType].
 *
 * @author Choonster
 */
object ModDataComponents {

    private val DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(HumVeeMod.MOD_ID)

    private var isInitialised = false

    private val TAG_PROPERTIES_BUILDER: DataComponentType.Builder<CompoundTag> =
        DataComponentType.Builder<CompoundTag>()
            .persistent(CompoundTag.CODEC) //TODO.networkSynchronized(CompoundTag.NETWORK_CODEC)
            .cacheEncoding()

    private val ENERGY_PROPERTIES_BUILDER: DataComponentType.Builder<EnergyStorage> =
        DataComponentType.Builder<EnergyStorage>()
            .persistent(EnergyProperties.CODEC)
            .networkSynchronized(EnergyProperties.NETWORK_CODEC)
            .cacheEncoding()

    private var TAG_PROPERTIES: Supplier<DataComponentType<CompoundTag>>? = null

    private var ENERGY: Supplier<DataComponentType<EnergyStorage>>? = null

    /**
     * Registers the [DeferredRegister] instance with the mod event bus.
     *
     *
     * This should be called during mod construction.
     *
     * @param modEventBus The mod event bus
     */
    fun initialise(modEventBus: IEventBus) {
        check(!isInitialised) { "Already initialised" }

        DATA_COMPONENT_TYPES.register(modEventBus)

        TAG_PROPERTIES = register("tag_properties") { TAG_PROPERTIES_BUILDER }
        ENERGY = register("energy") { ENERGY_PROPERTIES_BUILDER }

        isInitialised = true
    }

    fun getCompoundTag(): Supplier<DataComponentType<CompoundTag>> {
        if (!isInitialised) throw IllegalStateException("Get before initialised")

        return TAG_PROPERTIES!!
    }

    fun getEnergyStorage(): Supplier<DataComponentType<EnergyStorage>> {
        return ENERGY!!
    }

    private fun <T> register(
        name: String,
        builder: UnaryOperator<DataComponentType.Builder<T>>
    ): Supplier<DataComponentType<T>> {
        return DATA_COMPONENT_TYPES.register(name, Supplier { builder.apply(DataComponentType.builder()).build() })
    }

    private fun unit(builder: DataComponentType.Builder<Unit>): DataComponentType.Builder<Unit> {
        return builder
            .persistent(Codec.unit(Unit.INSTANCE))
            .networkSynchronized(StreamCodec.unit(Unit.INSTANCE))
    }

    @JvmRecord
    data class EnergyProperties(val energy: Int) {
        companion object {
            val CODEC: Codec<EnergyStorage> = object : Codec<EnergyStorage> {
                override fun <T> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<EnergyStorage, T>>? {
                    return ops
                        .getNumberValue(input)
                        .map { Pair.of(EnergyStorage(64), input) }
                }

                override fun <T> encode(input: EnergyStorage, ops: DynamicOps<T>, prefix: T): DataResult<T>? {
                    return null
                }
            }

            val NETWORK_CODEC: StreamCodec<RegistryFriendlyByteBuf, EnergyStorage> = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                { obj: EnergyStorage -> obj.energyStored },
                { capacity: Int -> EnergyStorage(capacity) }
            )
        }
    }
}