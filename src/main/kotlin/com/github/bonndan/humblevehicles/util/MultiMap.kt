package com.github.bonndan.humblevehicles.util

class MultiMap<K, V> : HashMap<K, MutableList<V>>() {

    fun putInsert(key: K, value: V) {
        val vList: MutableList<V> = computeIfAbsent(key) { k: K -> ArrayList() }
        vList.add(value)
    }
}
