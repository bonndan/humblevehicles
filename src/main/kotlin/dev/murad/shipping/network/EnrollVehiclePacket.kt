package dev.murad.shipping.network

import dev.murad.shipping.HumVeeMod
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import java.util.function.Function

@JvmRecord
data class EnrollVehiclePacket(val locoId: Int) : CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }

    companion object {

        val LOCATION: ResourceLocation = ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "locomotive_channel_enroll_packet")

        
        val TYPE: CustomPacketPayload.Type<EnrollVehiclePacket> = CustomPacketPayload.Type<EnrollVehiclePacket>(LOCATION)

        // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
        // 'name' will be encoded and decoded as a string
        // 'age' will be encoded and decoded as an integer
        // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
        
        val STREAM_CODEC: StreamCodec<ByteBuf, EnrollVehiclePacket> =
            StreamCodec.composite<ByteBuf, EnrollVehiclePacket, Int>(
                ByteBufCodecs.VAR_INT,
                EnrollVehiclePacket::locoId,
                Function { locoId: Int -> EnrollVehiclePacket(locoId) }
            )
    }
}
