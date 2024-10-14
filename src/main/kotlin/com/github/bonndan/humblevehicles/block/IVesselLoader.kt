package com.github.bonndan.humblevehicles.block

import com.github.bonndan.humblevehicles.util.LinkableEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.capabilities.EntityCapability
import java.util.*

interface IVesselLoader {

    enum class Mode {
        EXPORT,
        IMPORT
    }

    fun <T> hold(vehicle: T, mode: Mode): Boolean where T : Entity, T : LinkableEntity<T>

    fun <T> getEntityCapability(pos: BlockPos, capability: EntityCapability<T, *>, level: Level): T? {
        val fluidEntities = level.getEntities(null as Entity?, getSearchBox(pos))
            { entity -> hasCapabilityAndCanDock(entity, pos, capability) } ?: emptyList()

        return if (fluidEntities.isEmpty()) {
            null
        } else {
            fluidEntities.first().getCapability(capability, null)
        }
    }

    private fun hasCapabilityAndCanDock(entity: Entity, pos: BlockPos, capability: EntityCapability<*, *>): Boolean {

        return Optional.ofNullable(entity.getCapability(capability, null)).map { _ ->
            if (entity is LinkableEntity<*>) {
                entity.allowDockInterface() && (entity.getBlockPos().x == pos.x && entity.getBlockPos().z == pos.z)
            } else {
                true
            }
        }.orElse(false)
    }

    fun getSearchBox(pos: BlockPos): AABB {
        return AABB(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), pos.x + 1.0, pos.y + 1.0, pos.z + 1.0)
    }

}
