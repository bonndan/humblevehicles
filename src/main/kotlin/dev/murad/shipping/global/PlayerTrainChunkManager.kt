package dev.murad.shipping.global

import dev.murad.shipping.ShippingConfig
import dev.murad.shipping.network.client.EntityPosition
import dev.murad.shipping.network.client.VehicleTrackerClientPacket
import dev.murad.shipping.network.client.VehicleTrackerPacketHandler
import dev.murad.shipping.setup.ModItems
import dev.murad.shipping.util.LinkableEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.TicketType
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.phys.Vec3
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors
import java.util.stream.Stream

class PlayerTrainChunkManager : SavedData {

    private val enrolled: MutableSet<Entity> = HashSet()
    private val tickets: MutableSet<ChunkPos> = HashSet()
    private val toLoad: MutableSet<ChunkPos> = HashSet()
    private val loadLevel: Int = ShippingConfig.Server.CHUNK_LOADING_LEVEL.get()
    private var changed = false
    var isActive: Boolean = false
        private set
    var numVehicles: Int = 0
        private set
    val uuid: UUID
    val level: ServerLevel

    internal constructor(level: ServerLevel, uuid: UUID) {
        this.level = level
        this.uuid = uuid
        TrainChunkManagerManager.Companion.get(level.server).enroll(this)
        // active when creating a new one
        isActive = true
        setDirty()
    }

    internal constructor(tag: CompoundTag, level: ServerLevel, uuid: UUID) {
        this.level = level
        this.uuid = uuid
        numVehicles = tag.getInt("numVehicles")
        Arrays.stream(tag.getLongArray("chunksToLoad")).forEach { chunk: Long -> toLoad.add(ChunkPos(chunk)) }
        if (ShippingConfig.Server.OFFLINE_LOADING.get()) {
            activate()
        }
    }

    fun deactivate() {
        updateToLoad()
        numVehicles = enrolled.size
        enrolled.clear()
        tickets.forEach { chunkPos -> level.chunkSource.removeRegionTicket(TRAVEL_TICKET, chunkPos, loadLevel, uuid) }
        tickets.clear()
        isActive = false
    }

    fun activate() {
        isActive = true
        level.server.execute {
            toLoad.forEach { chunkPos -> level.chunkSource.addRegionTicket(LOAD_TICKET, chunkPos, 2, uuid) }
        }
    }

    private fun getAllSubjectEntities(entity: Entity): List<Entity?> {
        val subjects: MutableList<Entity?> = ArrayList()
        subjects.add(entity)
        if (entity is LinkableEntity<*>) { // need to refactor this somehow to be more generic
            for (e in entity.getTrain().asListOfTugged()) {
                if (e is Entity) {
                    subjects.add(e)
                    subjects.addAll(e.passengers)
                }
            }
        }

        if (entity.parts != null) {
            subjects.addAll(listOf(*entity.parts))
        }
        return subjects
    }

    private fun updateToLoad() {
        toLoad.clear()
        enrolled.forEach(Consumer { e: Entity ->
            toLoad.addAll(
                getAllSubjectEntities(e).stream().map { obj: Entity? -> obj!!.chunkPosition() }
                    .collect(Collectors.toSet()))
        })
    }


    fun tick() {
        val changed = enrolled.removeIf { e: Entity -> !e.isAlive }
        if (!isActive) {
            return
        }

        enrolled.forEach(Consumer { entityHead: Entity ->
            getAllSubjectEntities(entityHead)
                .stream()
                .filter { entity: Entity? ->
                    !(entity!!.level() as ServerLevel).isPositionEntityTicking(
                        entity.blockPosition()
                    )
                }
                .forEach { obj: Entity? -> obj!!.tick() }
        })

        val player = level.getPlayerByUUID(uuid)
        if (player is ServerPlayer && usesWrenchInMainHand(player)) {
            VehicleTrackerPacketHandler.sendToPlayer(
                VehicleTrackerClientPacket.of(entityPositions, level.dimension().toString()),
                player
            )
        }

        val reduce = enrolled.stream()
            .map { e: Entity -> e.chunkPosition() != ChunkPos(BlockPos.containing(e.xOld, e.yOld, e.zOld)) }
            .reduce(java.lang.Boolean.FALSE) { a: Boolean, b: Boolean -> java.lang.Boolean.logicalOr(a, b) }
        if (this.changed || changed || reduce) {
            this.changed = false
            level.server.execute { this.onChanged() }
        }
    }

    val entityPositions: MutableList<EntityPosition>
        get() = enrolled.map { entity: Entity ->
            EntityPosition(
                entity.type.toString(),
                entity.id,
                entity.position(),
                Vec3(entity.xOld, entity.yOld, entity.zOld)
            )
        }.toMutableList()

    private fun onChanged() {
        val required: MutableSet<ChunkPos> = HashSet()
        numVehicles = enrolled.size
        if (ShippingConfig.Server.DISABLE_CHUNK_MANAGEMENT.get()) {
            removeUnneededTickets(required)
            return
        }
        enrolled.stream().map { entity: Entity -> this.computeRequiredTickets(entity) }.forEach { c: Set<ChunkPos>? ->
            required.addAll(
                c!!
            )
        }
        removeUnneededTickets(required)
        addNeededTickets(required)
        updateToLoad()
        setDirty()
    }

    private fun computeRequiredTickets(entity: Entity): Set<ChunkPos> {
        val set = HashSet<ChunkPos>()
        getAllSubjectEntities(entity).stream()
            .map { obj -> obj!!.chunkPosition() }
            .map { pos -> ChunkPos.rangeClosed(pos, 1) }
            .forEach { pos: Stream<ChunkPos> -> set.addAll(pos.collect(Collectors.toList())) }

        return set
    }

    private fun removeUnneededTickets(required: Set<ChunkPos>) {
        java.util.Set.copyOf(tickets)
            .stream()
            .filter { pos: ChunkPos -> !required.contains(pos) }
            .forEach { chunkPos: ChunkPos ->
                level.chunkSource.removeRegionTicket(TRAVEL_TICKET, chunkPos, loadLevel, uuid)
                tickets.remove(chunkPos)
            }
    }

    private fun addNeededTickets(required: Set<ChunkPos>) {
        required
            .stream()
            .filter { pos: ChunkPos -> !tickets.contains(pos) }
            .collect(Collectors.toSet()) // avoid mutation on the go
            .forEach(Consumer { chunkPos: ChunkPos ->
                level.chunkSource.addRegionTicket(TRAVEL_TICKET, chunkPos, loadLevel, uuid)
                tickets.add(chunkPos)
            })
    }




    override fun save(pTag: CompoundTag, pRegistries: HolderLookup.Provider): CompoundTag {
        pTag.putInt("numVehicles", numVehicles)
        pTag.putLongArray(
            "chunksToLoad",
            toLoad.stream().map { obj: ChunkPos -> obj.toLong() }.collect(Collectors.toList())
        )
        return pTag
    }

    companion object {

        private val TRAVEL_TICKET: TicketType<UUID> =
            TicketType.create("humblevehicles:travelticket") { obj, uuid: UUID? -> obj.compareTo(uuid) }
        private val LOAD_TICKET: TicketType<UUID> =
            TicketType.create("humblevehicles:loadticket", { obj, uuid: UUID? -> obj.compareTo(uuid) }, 500)

        fun get(level: ServerLevel, uuid: UUID): PlayerTrainChunkManager {
            val storage = level.dataStorage

            val factory = getPlayerTrainChunkManagerFactory(level, uuid)
            return storage.computeIfAbsent(factory, "humblevehicles_chunkmanager-$uuid")
        }

        fun getSaved(level: ServerLevel, uuid: UUID): Optional<PlayerTrainChunkManager> {
            val storage = level.dataStorage
            return Optional.ofNullable(
                storage.get(getPlayerTrainChunkManagerFactory(level, uuid), "humblevehicles_chunkmanager-$uuid")
            )
        }

        private fun getPlayerTrainChunkManagerFactory(
            level: ServerLevel,
            uuid: UUID
        ): Factory<PlayerTrainChunkManager> {
            return Factory(
                { PlayerTrainChunkManager(level, uuid) },
                { tag , _ -> PlayerTrainChunkManager(tag, level, uuid) },
                DataFixTypes.CHUNK
            )
        }

        fun enroll(entity: Entity, uuid: UUID): Boolean {
            if (!entity.level().isClientSide) {
                val manager = get(entity.level() as ServerLevel, uuid)
                if (!manager.isActive) {
                    return false
                }
                manager.enrolled.add(entity)
                manager.changed = true
                return true
            }
            return false
        }

        fun enrollIfAllowed(entity: Entity, uuid: UUID): Boolean {
            if (!entity.level().isClientSide) {
                val manager = get(entity.level() as ServerLevel, uuid)
                val player = manager.level.getPlayerByUUID(uuid) ?: return false
                val max = ShippingConfig.Server.MAX_REGISTRERED_VEHICLES_PER_PLAYER.get()
                val registered: Int =
                    TrainChunkManagerManager.Companion.get(manager.level.server).countVehicles(uuid) + 1
                if (registered > max) {
                    player.sendSystemMessage(
                        Component.translatable(
                            "global.humblevehicles.locomotive.register_success",
                            max
                        )
                    )
                    return false
                } else {
                    player.sendSystemMessage(
                        Component.translatable(
                            "global.humblevehicles.locomotive.register_fail",
                            registered,
                            max
                        )
                    )
                    manager.enrolled.add(entity)
                    manager.changed = true
                    return true
                }
            }
            return false
        }

        private fun usesWrenchInMainHand(serverPlayer: ServerPlayer): Boolean {
            return serverPlayer.getItemInHand(InteractionHand.MAIN_HAND).item == ModItems.CONDUCTORS_WRENCH.get()
        }
    }
}
