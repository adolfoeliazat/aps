package aps.front

import aps.*
import into.kommon.*

external class WeakMap<in K: Any, V: Any?> {
    fun delete(key: K): Boolean = noImpl
    operator fun get(key: K): V? = noImpl
    fun has(key: K): Boolean = noImpl
    operator fun set(key: K, value: V): WeakMap<K, V> = noImpl
}

inline fun <K: Any, V> WeakMap<K, V>.getOrPut(key: K, defaultValue: () -> V): V {
    val value = get(key)
    return if (value == null) {
        val answer = defaultValue()
        set(key, answer)
        answer
    } else {
        value
    }
}

object SpikeWeakMap {
    fun test1() {
        data class Key(val name: String)
        val wm = WeakMap<Key, String>()
        gloshit.wm = wm
        wm[Key("boobs")] = "monster"
        wm[Key("cunt")] = "deep"
    }

    fun testInvalidKey() {
        val wm = WeakMap<String, String>()
        gloshit.wm = wm
        wm["boobs"] = "monster"
        wm["cunt"] = "deep"
    }
}

