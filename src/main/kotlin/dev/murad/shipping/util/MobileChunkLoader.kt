package dev.murad.shipping.util

import com.mojang.datafixers.util.Pair
import dev.murad.shipping.ShippingMod
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

    private fun getSurroundingChunks(chunk: Pair<Int, Int>): MutableSet<Pair<Int, Int>> {
        val set: MutableSet<Pair<Int, Int>> = HashSet()
        for (i in -1..1) {
            for (j in -1..1) {
                set.add(Pair(chunk.first + i, chunk.second + j))
            }
        }
        return set
    }

    @SubscribeEvent
    fun register(registerTicketControllersEvent: RegisterTicketControllersEvent) {
        registerTicketControllersEvent.register(ticketController)
    }

    private fun setChunkLoad(add: Boolean, chunk: Pair<Int, Int>) {
        ticketController.forceChunk(
            entity.level() as ServerLevel,
            entity, chunk.first, chunk.second, add, false
        )
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

    fun addAdditionalSaveData(p_213281_1_: CompoundTag) {
        if (loadedChunk.isPresent) {
            p_213281_1_.putInt("xchunk", loadedChunk.get().first)
            p_213281_1_.putInt("zchunk", loadedChunk.get().second)
        }
    }

    fun readAdditionalSaveData(p_70037_1_: CompoundTag) {
        if (p_70037_1_.contains("xchunk")) {
            val x = p_70037_1_.getInt("xchunk")
            val z = p_70037_1_.getInt("zchunk")
            loadedChunk = Optional.of(Pair(x, z))
        }
    }

    fun remove() {
        loadedChunk.ifPresent { c: Pair<Int, Int> ->
            getSurroundingChunks(c).forEach(
                Consumer { ch: Pair<Int, Int> ->
                    this.setChunkLoad(
                        false,
                        ch
                    )
                })
        }
    }

    companion object {
        private val ticketController =
            TicketController(ResourceLocation.fromNamespaceAndPath(ShippingMod.MOD_ID, "mobile_chunks"))
    }
}
