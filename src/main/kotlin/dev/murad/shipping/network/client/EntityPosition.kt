package dev.murad.shipping.network.client

import net.minecraft.world.phys.Vec3

@JvmRecord
data class EntityPosition(val type: String, val id: Int, val pos: Vec3, val oldPos: Vec3)
