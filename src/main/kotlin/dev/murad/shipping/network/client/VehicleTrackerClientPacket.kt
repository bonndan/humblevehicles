package dev.murad.shipping.network.client

import dev.murad.shipping.HumVeeMod
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3
import java.util.function.BiFunction
import java.util.stream.Collectors

@JvmRecord
data class VehicleTrackerClientPacket(val tag: CompoundTag, val dimension: String) : CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }

    fun parse(): MutableList<EntityPosition> {
        return tag.getAllKeys().stream().map<EntityPosition> { key ->
            val coords = tag.getCompound(key)
            EntityPosition(
                coords.getString("type"),
                coords.getInt("eid"),
                Vec3(
                    coords.getDouble("x"),
                    coords.getDouble("y"),
                    coords.getDouble("z")
                ),
                Vec3(
                    coords.getDouble("xo"),
                    coords.getDouble("yo"),
                    coords.getDouble("zo")
                )
            )
        }.collect(Collectors.toList())
    }

    companion object {

        val LOCATION: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "vehicle_tracker_channel")
        val TYPE: CustomPacketPayload.Type<VehicleTrackerClientPacket> =
            CustomPacketPayload.Type<VehicleTrackerClientPacket>(LOCATION)

        val STREAM_CODEC: StreamCodec<ByteBuf, VehicleTrackerClientPacket> =
            StreamCodec.composite<ByteBuf, VehicleTrackerClientPacket, CompoundTag, String>(
                ByteBufCodecs.COMPOUND_TAG,
                VehicleTrackerClientPacket::tag,
                ByteBufCodecs.STRING_UTF8,
                VehicleTrackerClientPacket::dimension,
                BiFunction { tag: CompoundTag, dimension: String -> VehicleTrackerClientPacket(tag, dimension) }
            )

        fun of(types: MutableList<EntityPosition>, dimension: String): VehicleTrackerClientPacket {
            val tag = CompoundTag()
            var i = 0
            for (position in types) {
                val coords = CompoundTag()
                coords.putDouble("x", position.pos.x)
                coords.putDouble("y", position.pos.y)
                coords.putDouble("z", position.pos.z)
                coords.putDouble("xo", position.oldPos.x)
                coords.putDouble("yo", position.oldPos.y)
                coords.putDouble("zo", position.oldPos.z)
                coords.putString("type", position.type)
                coords.putInt("eid", position.id)
                tag.put(i++.toString(), coords)
            }
            return VehicleTrackerClientPacket(tag, dimension)
        }
    }
}
