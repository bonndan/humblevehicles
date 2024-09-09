package dev.murad.shipping.network

import dev.murad.shipping.HumVeeMod
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import java.util.function.BiFunction

@JvmRecord
data class SetEnginePacket(val locoId: Int, val state: Boolean) : CustomPacketPayload {



    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }


    companion object {

        val LOCATION: ResourceLocation = ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "locomotive_channel_engine_packet")

        @JvmField
        val TYPE: CustomPacketPayload.Type<SetEnginePacket> = CustomPacketPayload.Type<SetEnginePacket>(LOCATION)

        // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
        // 'name' will be encoded and decoded as a string
        // 'age' will be encoded and decoded as an integer
        // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
        @JvmField
        val STREAM_CODEC: StreamCodec<ByteBuf?, SetEnginePacket> =
            StreamCodec.composite<ByteBuf, SetEnginePacket, Int, Boolean>(
                ByteBufCodecs.VAR_INT,
                SetEnginePacket::locoId,
                ByteBufCodecs.BOOL,
                SetEnginePacket::state,
                BiFunction { locoId, state -> SetEnginePacket(locoId!!, state!!) }
            )
    }
}
