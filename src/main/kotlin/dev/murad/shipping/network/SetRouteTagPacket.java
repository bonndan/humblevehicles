package dev.murad.shipping.network;

import dev.murad.shipping.ShippingMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SetRouteTagPacket implements CustomPacketPayload {
    public final int routeChecksum;
    public final boolean isOffhand;
    public final CompoundTag tag;

    public SetRouteTagPacket(int routeChecksum, boolean isOffhand, CompoundTag tag) {
        this.routeChecksum = routeChecksum;
        this.isOffhand = isOffhand;
        this.tag = tag;
    }

    public static final CustomPacketPayload.Type<SetRouteTagPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(ShippingMod.MOD_ID, "tug_route_channel")
    );

    public static final StreamCodec<ByteBuf, SetRouteTagPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SetRouteTagPacket::getRouteChecksum,
            ByteBufCodecs.BOOL,
            SetRouteTagPacket::isOffhand,
            ByteBufCodecs.COMPOUND_TAG,
            SetRouteTagPacket::getTag,
            SetRouteTagPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public int getRouteChecksum() {
        return routeChecksum;
    }

    public boolean isOffhand() {
        return isOffhand;
    }

    public CompoundTag getTag() {
        return tag;
    }
}
