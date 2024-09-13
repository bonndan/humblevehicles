package dev.murad.shipping.global

import com.google.common.collect.Table
import com.google.common.collect.TreeBasedTable
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.level.Level
import net.minecraft.world.level.saveddata.SavedData
import java.util.*
import java.util.function.Consumer

class TrainChunkManagerManager : SavedData {
    private val managers: Table<ResourceKey<Level>, UUID?, PlayerTrainChunkManager> = TreeBasedTable.create()

    private constructor(level: MinecraftServer)

    private constructor(tag: CompoundTag, server: MinecraftServer) {
        for (cell in tag.getList("saved", 10)) {
            if (cell is CompoundTag) {
                val dimension = ResourceKey.create(
                    Registries.DIMENSION,
                    ResourceLocation.read(cell.getString("level")).getOrThrow()
                )
                val level = server.getLevel(dimension) ?: return
                val uuid = cell.getUUID("UUID")
                server.execute {
                    PlayerTrainChunkManager.Companion.getSaved(level, uuid)
                        .ifPresent { manager: PlayerTrainChunkManager ->
                            managers.put(dimension, uuid, manager)
                        }
                }
            }
        }
    }

    override fun save(pTag: CompoundTag, pRegistries: HolderLookup.Provider): CompoundTag {
        val topList = ListTag()
        for (cell in managers.cellSet()) {
            val inner = CompoundTag()
            inner.putString("level", cell.rowKey.location().toString())
            inner.putUUID("UUID", cell.columnKey)
            topList.add(inner)
        }
        pTag.put("saved", topList)
        return pTag
    }

    fun enroll(playerTrainChunkManager: PlayerTrainChunkManager) {
        managers.put(playerTrainChunkManager.level.dimension(), playerTrainChunkManager.uuid, playerTrainChunkManager)
        setDirty()
    }

    fun getManagers(dimension: ResourceKey<Level>): Set<PlayerTrainChunkManager> {
        return HashSet(managers.row(dimension).values)
    }

    fun getManagers(uuid: UUID?): Set<PlayerTrainChunkManager> {
        return HashSet(managers.column(uuid).values)
    }

    fun countVehicles(uuid: UUID?): Int {
        return getManagers(uuid).stream().reduce(0,
            { i: Int, manager: PlayerTrainChunkManager -> i + manager.numVehicles },
            { a: Int, b: Int -> Integer.sum(a, b) })
    }

    companion object {
        fun get(server: MinecraftServer): TrainChunkManagerManager {
            return server
                .overworld()
                .dataStorage
                .computeIfAbsent(
                    getPlayerTrainChunkManagerFactory(server),
                    "humblevehicles_trainchunkmanagermanager"
                )
        }

        private fun getPlayerTrainChunkManagerFactory(server: MinecraftServer): Factory<TrainChunkManagerManager> {
            return Factory({ TrainChunkManagerManager(server) },
                { tag: CompoundTag, _ -> TrainChunkManagerManager(tag, server) },
                DataFixTypes.CHUNK
            )
        }
    }
}
