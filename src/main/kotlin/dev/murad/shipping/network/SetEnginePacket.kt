package dev.murad.shipping.network

import dev.murad.shipping.HumVeeMod
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import java.util.function.BiFunction

data class SetEnginePacket(val locoId: Int, val state: Boolean) : CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }

    companion object {

        private val LOCATION = ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "locomotive_channel_engine_packet")

        val TYPE: CustomPacketPayload.Type<SetEnginePacket> = CustomPacketPayload.Type<SetEnginePacket>(LOCATION)
        
        val STREAM_CODEC: StreamCodec<ByteBuf?, SetEnginePacket> =
            StreamCodec.composite<ByteBuf, SetEnginePacket, Int, Boolean>(
                ByteBufCodecs.VAR_INT,
                SetEnginePacket::locoId,
                ByteBufCodecs.BOOL,
                SetEnginePacket::state
            ) { locoId, state -> SetEnginePacket(locoId, state) }
    }
}
