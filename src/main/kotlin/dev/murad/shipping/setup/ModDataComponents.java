package dev.murad.shipping.setup;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.murad.shipping.ShippingMod;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.registries.DeferredRegister;


import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Registers this mod's {@link DataComponentType DataComponentTypes}.
 *
 * @author Choonster
 */
public class ModDataComponents {

    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, ShippingMod.MOD_ID);

    private static boolean isInitialised = false;

    public static final Supplier<DataComponentType<CompoundTag>> TAG_PROPERTIES = register("tag_properties",
            builder -> builder
                    .persistent(CompoundTag.CODEC)
                    //TODO.networkSynchronized(CompoundTag.NETWORK_CODEC)
                    .cacheEncoding()
    );


    public static final Supplier<DataComponentType<EnergyStorage>> ENERGY = register("energy",
            builder -> builder
                    .persistent(EnergyProperties.CODEC)
                    .networkSynchronized(EnergyProperties.NETWORK_CODEC)
                    .cacheEncoding()
    );

    /**
     * Registers the {@link DeferredRegister} instance with the mod event bus.
     * <p>
     * This should be called during mod construction.
     *
     * @param modEventBus The mod event bus
     */
    public static void initialise(final IEventBus modEventBus) {
        if (isInitialised) {
            throw new IllegalStateException("Already initialised");
        }

        DATA_COMPONENT_TYPES.register(modEventBus);

        isInitialised = true;
    }

    private static <T> Supplier<DataComponentType<T>> register(final String name, final UnaryOperator<DataComponentType.Builder<T>> builder) {
        return DATA_COMPONENT_TYPES.register(name, () -> builder.apply(DataComponentType.builder()).build());
    }

    private static DataComponentType.Builder<Unit> unit(final DataComponentType.Builder<Unit> builder) {
        return builder
                .persistent(Codec.unit(Unit.INSTANCE))
                .networkSynchronized(StreamCodec.unit(Unit.INSTANCE));
    }

    public record EnergyProperties(int energy) {
        public static final Codec<EnergyStorage> CODEC = new Codec<EnergyStorage>() {
            @Override
            public <T> DataResult<Pair<EnergyStorage, T>> decode(DynamicOps<T> ops, T input) {
                return ops
                        .getNumberValue(input)
                        .map(number -> Pair.of(new EnergyStorage(64), input));
            }

            @Override
            public <T> DataResult<T> encode(EnergyStorage input, DynamicOps<T> ops, T prefix) {
                return null;
            }
        };

        public static final StreamCodec<RegistryFriendlyByteBuf, EnergyStorage> NETWORK_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                EnergyStorage::getEnergyStored,
                EnergyStorage::new
        );
    }

}