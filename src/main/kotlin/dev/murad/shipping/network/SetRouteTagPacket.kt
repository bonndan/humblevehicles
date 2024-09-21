package dev.murad.shipping.network

import dev.murad.shipping.HumVeeMod
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

class SetRouteTagPacket(private val routeChecksum: Int, private val isOffhand: Boolean, private val tag: CompoundTag?) :
    CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }

    fun getRouteChecksum(): Int {
        return routeChecksum
    }

    fun isOffhand(): Boolean {
        return isOffhand
    }

    fun getTag(): CompoundTag? {
        return tag
    }

    companion object {

        val TYPE: CustomPacketPayload.Type<SetRouteTagPacket> = CustomPacketPayload.Type<SetRouteTagPacket>(
            ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "tug_route_channel")
        )

        val STREAM_CODEC: StreamCodec<ByteBuf, SetRouteTagPacket> = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, { obj: SetRouteTagPacket? -> obj!!.getRouteChecksum() },
            ByteBufCodecs.BOOL, { obj: SetRouteTagPacket? -> obj!!.isOffhand() },
            ByteBufCodecs.COMPOUND_TAG, { obj: SetRouteTagPacket? -> obj!!.getTag() },
            { routeChecksum: Int?, isOffhand: Boolean?, tag: CompoundTag? ->
                SetRouteTagPacket(routeChecksum!!, isOffhand!!, tag)
            }
        )
    }
}
