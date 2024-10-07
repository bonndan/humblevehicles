package dev.murad.shipping.util

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
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

    enum class LinkSide {
        DOMINANT, DOMINATED
    }
}
