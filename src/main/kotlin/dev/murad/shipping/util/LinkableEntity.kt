package dev.murad.shipping.util

import dev.murad.shipping.entity.custom.TrainInventoryProvider
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.items.ItemStackHandler
import java.util.*
import java.util.function.Function
import java.util.stream.Stream

interface LinkableEntity<V : LinkableEntity<V>> {

    fun getFollower():Optional<V>

    fun getLeader():Optional<V>

    fun setDominated(entity: V)
    fun setDominant(entity: V)
    fun removeDominated()
    fun removeDominant()
    fun handleShearsCut()
    fun getTrain(): Train<V>
    fun linkEntities(player: Player, target: Entity): Boolean
    fun setTrain(train: Train<V>)
    fun hasWaterOnSides(): Boolean

    fun handleLinkableKill() {
        getFollower().ifPresent { obj: V -> obj.removeDominant() }
        getLeader().ifPresent { obj: V -> obj.removeDominated() }
    }

    fun checkNoLoopsDominated(): Boolean {
        return checkNoLoopsHelper(this, { obj -> obj.getFollower() }, HashSet())
    }

    fun checkNoLoopsDominant(): Boolean {
        return checkNoLoopsHelper(this, { obj: LinkableEntity<V> -> obj.getLeader() }, HashSet())
    }

    fun checkNoLoopsHelper(
        entity: LinkableEntity<V>,
        next: Function<LinkableEntity<V>, Optional<V>?>,
        set: MutableSet<LinkableEntity<V>?>
    ): Boolean {
        if (set.contains(entity)) {
            return true
        }
        set.add(entity)
        val nextEntity = next.apply(entity)
        return nextEntity!!.map { e: V -> this.checkNoLoopsHelper(e, next, set) }.orElse(false)
    }

    fun <U> applyWithDominant(function: Function<LinkableEntity<V>?, U>): Stream<U> {
        val ofThis = Stream.of(function.apply(this))

        return if (checkNoLoopsDominant()) ofThis else getLeader().map { dom: V ->
            Stream.concat(
                ofThis,
                dom.applyWithDominant(function)
            )
        }.orElse(ofThis)
    }

    fun allowDockInterface(): Boolean

    fun getBlockPos(): BlockPos

    /**
     * Grabs a list of connected vessels that provides inventory to this vessel
     * For Example:
     * Tug F F F C C C -- All F barges are linked to all C barges
     * Tug F C F C F C -- Each F barge is linked to 1 C barge
     */
     fun getConnectedInventories(): List<TrainInventoryProvider> {

        val result = mutableListOf<TrainInventoryProvider>()

        var follower = getFollower()
        while (follower.isPresent) {
            // TODO generalize this to all inventory providers
            if (follower.get() is TrainInventoryProvider) {
                break
            }

            follower = follower.get().getFollower()
        }

        // vessel is either empty or is a chest barge
        while (follower.isPresent) {
            val get = follower.get()
            if (get is TrainInventoryProvider) {
                result.add(get as TrainInventoryProvider)
            } else {
                break
            }

            follower = follower.get().getFollower()
        }

        return result
    }

    enum class LinkSide {
        DOMINANT, DOMINATED
    }
}
