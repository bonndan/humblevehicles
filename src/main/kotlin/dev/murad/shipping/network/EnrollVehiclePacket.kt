package dev.murad.shipping.network

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import java.util.function.Function

@JvmRecord
data class EnrollVehiclePacket(val locoId: Int) : CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }

    companion object {
        @JvmField
        val TYPE: CustomPacketPayload.Type<EnrollVehiclePacket> = CustomPacketPayload.Type<EnrollVehiclePacket>(VehiclePacketHandler.LOCATION)

        // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
        // 'name' will be encoded and decoded as a string
        // 'age' will be encoded and decoded as an integer
        // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
        @JvmField
        val STREAM_CODEC: StreamCodec<ByteBuf, EnrollVehiclePacket> =
            StreamCodec.composite<ByteBuf, EnrollVehiclePacket, Int>(
                ByteBufCodecs.VAR_INT,
                EnrollVehiclePacket::locoId,
                Function { locoId: Int -> EnrollVehiclePacket(locoId) }
            )
    }
}
