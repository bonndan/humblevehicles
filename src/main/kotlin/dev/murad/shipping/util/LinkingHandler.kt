package dev.murad.shipping.util

import dev.murad.shipping.capability.StallingCapability
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB
import java.util.*
import kotlin.math.floor

class LinkingHandler<T>(
    private val entity: T,
    private val clazz: Class<T>,
    private val dominantID: EntityDataAccessor<Int>,
    private val dominatedID: EntityDataAccessor<Int>
) where T : Entity, T : LinkableEntity<T> {

    private var waitForDominated = false

    var leader: Optional<T> = Optional.empty()
    var follower: Optional<T> = Optional.empty()
    var train: Train<T>? = null

    private var dominantNBT: CompoundTag? = null


    fun tickLoad() {

        if (entity.level().isClientSide) {
            fetchDominantClient()
            fetchDominatedClient()
            return
        }

        if (leader.isEmpty && dominantNBT != null) {
            tryToLoadFromNBT(dominantNBT!!).ifPresent { entity1: T -> entity.setDominant(entity1) }
            leader.ifPresent { d: T ->
                d.setDominated(entity)
                dominantNBT = null // done loading
            }
        }
        if (follower.isPresent) {
            waitForDominated = false
            stallNonTicking()
        } else if (waitForDominated) {
            Optional.ofNullable(entity.getCapability(StallingCapability.STALLING_CAPABILITY))
                .ifPresent { obj: StallingCapability -> obj.stall() }
        }
        entity.entityData?.set(dominantID, leader.map { it.id }.orElse(-1))
        entity.entityData?.set(dominatedID, follower.map { it.id }.orElse(-1))
    }

    private fun stallNonTicking() {
//        boolean skip = entity.getTrain()
//                .getTug()
//                .flatMap(tug -> {
//                    if (tug instanceof HeadVehicle h)
//                        return Optional.of(h);
//                    else return Optional.empty();
//                })
//                .map(HeadVehicle::hasOwner).orElse(true);
//
//        if(!skip && !((ServerLevel) entity.level).isPositionEntityTicking(dominated.get().blockPosition())){
//            entity.getCapability(StallingCapability.STALLING_CAPABILITY).ifPresent(StallingCapability::stall);
//        }
    }

    fun readAdditionalSaveData(compound: CompoundTag) {
        dominantNBT = compound.getCompound("dominant")
        waitForDominated = compound.getBoolean("hasChild")
    }

    fun addAdditionalSaveData(compound: CompoundTag) {
        if (leader.isPresent) {
            writeNBT(leader.get(), compound)
        } else if (dominantNBT != null) {
            compound.put(LinkableEntity.LinkSide.DOMINANT.name, dominantNBT)
        }

        compound.putBoolean("hasChild", follower.isPresent)
    }

    private fun writeNBT(entity: Entity, globalCompound: CompoundTag) {
        val compound = CompoundTag()
        compound.putInt("X", floor(entity.x).toInt())
        compound.putInt("Y", floor(entity.y).toInt())
        compound.putInt("Z", floor(entity.z).toInt())

        compound.putString("UUID", entity.uuid.toString())

        globalCompound.put("dominant", compound)
    }

    fun onSyncedDataUpdated(key: EntityDataAccessor<*>) {
        if (entity.level().isClientSide) {
            if (dominatedID == key || dominantID == key) {
                fetchDominantClient()
                fetchDominatedClient()
            }
        }
    }

    private fun fetchDominantClient() {
        val potential = entity.level().getEntity(
            entity.entityData?.get(dominantID) ?: 0 //TODO was treated as non-null
        )
        if (clazz.isInstance(potential)) {
            leader = Optional.of(clazz.cast(potential))
        } else {
            leader = Optional.empty()
        }
    }

    private fun tryToLoadFromNBT(compound: CompoundTag): Optional<T> {
        try {
            val pos = MutableBlockPos()
            pos[compound.getInt("X"), compound.getInt("Y")] = compound.getInt("Z")
            val uuid = compound.getString("UUID")
            val searchBox = AABB(
                (pos.x - 2).toDouble(),
                (pos.y - 2).toDouble(),
                (pos.z - 2).toDouble(),
                (pos.x + 2).toDouble(),
                (pos.y + 2).toDouble(),
                (pos.z + 2).toDouble()
            )
            val entities = entity.level().getEntities(
                entity, searchBox
            ) { e: Entity -> e.stringUUID == uuid && clazz.isInstance(e) }
            return entities.stream().findFirst().map { e: Entity? -> clazz.cast(e) }
        } catch (e: Exception) {
            return Optional.empty()
        }
    }

    private fun fetchDominatedClient() {
        val potential = entity.level().getEntity(entity.entityData.get(dominatedID))
        if (clazz.isInstance(potential)) {
            follower = Optional.of((clazz.cast(potential)))
        } else {
            follower = Optional.empty()
        }
    }

}
