package dev.murad.shipping.util

import java.util.*

class Train<V : LinkableEntity<V>>(var head: V) {
    val tug: Optional<V> =
        if (head is LinkableEntityHead<*>) Optional.of(head) else Optional.empty()
    var tail: V = head

    fun asListOfTugged(): List<V> {

        if (head.checkNoLoopsDominated()) {
            // just in case - to avoid crashing the world.
            head.removeDominated()
            head.getFollower().ifPresent { obj -> obj.removeDominant() }
            return ArrayList()
        }

        return tug.map<List<V>> { tugEntity: V ->
            val barges: MutableList<V> = ArrayList()
            var barge = getNext(tugEntity)
            while (barge.isPresent) {
                barges.add(barge.get())
                barge = getNext(barge.get())
            }
            barges
        }.orElse(ArrayList())
    }

    fun asList(): List<V> {
        if (head.checkNoLoopsDominated()) {
            // just in case - to avoid crashing the world.
            head.removeDominated()
            head.getFollower().ifPresent { obj: LinkableEntity<*> -> obj.removeDominant() }
            return ArrayList()
        }

        val barges: MutableList<V> = ArrayList()
        var barge: Optional<V> = Optional.of(head)
        while (barge.isPresent) {
            barges.add(barge.get())
            barge = getNext(barge.get())
        }
        return barges
    }

    fun getNext(entity: V): Optional<V> {
        return entity.getFollower().map { t -> t }
    }
}
