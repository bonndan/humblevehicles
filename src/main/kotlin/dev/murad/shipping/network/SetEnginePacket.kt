package dev.murad.shipping.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static dev.murad.shipping.network.VehiclePacketHandler.LOCATION;

public record SetEnginePacket(int locoId, boolean state) implements CustomPacketPayload {

    public static final Type<SetEnginePacket> TYPE = new Type<>(LOCATION);

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // 'name' will be encoded and decoded as a string
    // 'age' will be encoded and decoded as an integer
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, SetEnginePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SetEnginePacket::locoId,
            ByteBufCodecs.BOOL,
            SetEnginePacket::state,
            SetEnginePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
