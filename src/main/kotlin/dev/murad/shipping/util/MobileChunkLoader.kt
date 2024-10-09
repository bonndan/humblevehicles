package dev.murad.shipping.util

import com.mojang.datafixers.util.Pair
import dev.murad.shipping.HumVeeMod
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent
import net.neoforged.neoforge.common.world.chunk.TicketController
import java.util.*
import java.util.function.Consumer

class MobileChunkLoader(private val entity: Entity) {

    private var loadedChunk = Optional.empty<Pair<Int, Int>>()

    @SubscribeEvent
    fun register(registerTicketControllersEvent: RegisterTicketControllersEvent) {
        registerTicketControllersEvent.register(ticketController)
    }

    private fun setChunkLoad(add: Boolean, chunk: Pair<Int, Int>) {
        ticketController.forceChunk(entity.level() as ServerLevel, entity, chunk.first, chunk.second, add, false)
    }

    fun serverTick() {
        val currChunk = Pair(entity.chunkPosition().x, entity.chunkPosition().z)
        if (loadedChunk.isEmpty) {
            getSurroundingChunks(currChunk).forEach(Consumer { c: Pair<Int, Int> -> setChunkLoad(true, c) })
            loadedChunk = Optional.of(currChunk)
        } else if (currChunk != loadedChunk.get()) {
            val needsToBeLoaded = getSurroundingChunks(currChunk)

            val toUnload = getSurroundingChunks(loadedChunk.get())
            toUnload.removeAll(needsToBeLoaded)

            val prevLoaded: Set<Pair<Int, Int>> = getSurroundingChunks(loadedChunk.get())
            needsToBeLoaded.removeAll(prevLoaded)


            toUnload.forEach(Consumer { c: Pair<Int, Int> -> setChunkLoad(false, c) })
            needsToBeLoaded.forEach(Consumer { c: Pair<Int, Int> -> setChunkLoad(true, c) })

            loadedChunk = Optional.of(currChunk)
        }
    }

    fun addAdditionalSaveData(compoundTag: CompoundTag) {
        if (loadedChunk.isPresent) {
            compoundTag.putInt("xchunk", loadedChunk.get().first)
            compoundTag.putInt("zchunk", loadedChunk.get().second)
        }
    }

    fun readAdditionalSaveData(compoundTag: CompoundTag) {
        if (compoundTag.contains("xchunk")) {
            val x = compoundTag.getInt("xchunk")
            val z = compoundTag.getInt("zchunk")
            loadedChunk = Optional.of(Pair(x, z))
        }
    }

    fun remove() {
        loadedChunk.ifPresent { pair: Pair<Int, Int> ->
            getSurroundingChunks(pair).forEach { chunk -> this.setChunkLoad(false, chunk) }
        }
    }

    private fun getSurroundingChunks(chunk: Pair<Int, Int>): MutableSet<Pair<Int, Int>> {
        val set: MutableSet<Pair<Int, Int>> = HashSet()
        for (i in -1..1) {
            for (j in -1..1) {
                set.add(Pair(chunk.first + i, chunk.second + j))
            }
        }
        return set
    }

    companion object {
        private val ticketController =
            TicketController(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "mobile_chunks"))
    }
}
