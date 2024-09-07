package dev.murad.shipping.block

import dev.murad.shipping.util.LinkableEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.capabilities.EntityCapability
import java.util.*
import java.util.function.Predicate

interface IVesselLoader {

    enum class Mode {
        EXPORT,
        IMPORT
    }

    fun <T> hold(vehicle: T, mode: Mode?): Boolean where T : Entity, T : LinkableEntity<T>

    fun <T> getEntityCapability(pos: BlockPos, capability: EntityCapability<T, *>, level: Level?): T? {
        val fluidEntities = level?.getEntities(
            null as Entity?,
            getSearchBox(pos),
            (Predicate { e: Entity -> entityPredicate(e, pos, capability) })
        ) ?: emptyList()

        if (fluidEntities.isEmpty()) {
            return null
        } else {
            return fluidEntities.first().getCapability(capability, null)
        }
    }

    fun entityPredicate(entity: Entity, pos: BlockPos, capability: EntityCapability<*, *>?): Boolean {
        return Optional.ofNullable(entity.getCapability(capability, null)).map { cap: Any? ->
            if (entity is LinkableEntity<*>) {
                return@map entity.allowDockInterface() && (entity.blockPos.x == pos.x && entity.blockPos.z == pos.z)
            } else {
                return@map true
            }
        }.orElse(false)
    }

    fun getSearchBox(pos: BlockPos): AABB {
        return AABB(
            pos.x.toDouble(),
            pos.y.toDouble(),
            pos.z.toDouble(),
            pos.x + 1.0,
            pos.y + 1.0,
            pos.z + 1.0
        )
    }
    companion object {



    }
}
