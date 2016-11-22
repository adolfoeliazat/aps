package aps.front

fun <T> mutableListOf(vararg elements: T): MutableList<T> {
    val noise = DebugNoise("mutableListOf", mute = true)

    val list = kotlin.collections.mutableListOf(*elements)
    val dlist = list.asDynamic()
    val movementForbidders = kotlin.collections.mutableListOf<() -> Unit>()
    val listOrig = kotlin.collections.mutableMapOf<String, dynamic>()

    val mutatorKeys = gatherKeys(dlist, listOf("add", "remove", "addAll", "removeAll",
                                               "retainAll", "clear", "set", "removeAt"))
    for (key in mutatorKeys) {
        listOrig[key] = dlist[key]
        noise.clog("replacing in list: $key")
        dlist[key] = {
            noise.clog("called mutator: $key")
            val res = listOrig[key].apply(dlist, jsArrayLikeToJSArray(js("arguments")))

            // TODO:vgrechka What if collection is not actually mutated, like when removing non-existing element?
            movementForbidders.forEach {it()}

            res
        }
    }

    val iteratorFactoryKeys = gatherKeys(dlist, listOf("iterator", "listIterator"))
    for (key in iteratorFactoryKeys) {
        listOrig[key] = dlist[key]
        noise.clog("replacing in list: $key")
        dlist[key] = {
            noise.clog("called iterator factory: $key")
            val iter = listOrig[key].apply(dlist, jsArrayLikeToJSArray(js("arguments")))

            var movementAllowed = true
            movementForbidders += {movementAllowed = false}

            val iterOrig = kotlin.collections.mutableMapOf<String, dynamic>()
            val movementKeys = gatherKeys(iter, listOf("next", "previous"))
            for (key in movementKeys) {
                iterOrig[key] = iter[key]
                noise.clog("replacing in iterator: $key")
                iter[key] = {
                    noise.clog("called iterator: $key")
                    if (!movementAllowed) throw ConcurrentModificationException("Concurrent modification of a fucking list")
                    iterOrig[key].apply(iter, jsArrayLikeToJSArray(js("arguments")))
                }
            }
            iter
        }
    }

    return list
}

fun <K, V> mutableMapOf(vararg pairs: Pair<K, V>): MutableMap<K, V> {
    val noise = DebugNoise("mutableMapOf", mute = true)

    val map = kotlin.collections.mutableMapOf(*pairs)
    val dmap = map.asDynamic()
    val movementForbidders = kotlin.collections.mutableListOf<() -> Unit>()
    val mapOrig = kotlin.collections.mutableMapOf<String, dynamic>()

    val mutatorKeys = gatherKeys(dmap, listOf("put", "remove", "putAll", "clear"))
    for (key in mutatorKeys) {
        mapOrig[key] = dmap[key]
        noise.clog("replacing in map: $key")
        dmap[key] = {
            noise.clog("called mutator: $key")
            val res = mapOrig[key].apply(dmap, jsArrayLikeToJSArray(js("arguments")))

            // TODO:vgrechka What if collection is not actually mutated, like when removing non-existing element?
            movementForbidders.forEach {it()}

            res
        }
    }

    val dentries = dmap.entries
    val entriesOrig = kotlin.collections.mutableMapOf<String, dynamic>()
    val iteratorFactoryKeys = gatherKeys(dentries, listOf("iterator"))
    for (key in iteratorFactoryKeys) {
        entriesOrig[key] = dentries[key]
        noise.clog("replacing in entries: $key")
        dentries[key] = {
            noise.clog("called iterator factory: $key")
            val iter = entriesOrig[key].apply(dentries, jsArrayLikeToJSArray(js("arguments")))

            var movementAllowed = true
            movementForbidders += {movementAllowed = false}

            val iterOrig = kotlin.collections.mutableMapOf<String, dynamic>()
            val movementKeys = gatherKeys(iter, listOf("next"))
            for (key in movementKeys) {
                iterOrig[key] = iter[key]
                noise.clog("replacing in iterator: $key")
                iter[key] = {
                    noise.clog("called iterator: $key")
                    if (!movementAllowed) throw ConcurrentModificationException("Concurrent modification of a fucking map")
                    iterOrig[key].apply(iter, jsArrayLikeToJSArray(js("arguments")))
                }
            }
            iter
        }
    }

    return map
}

private fun gatherKeys(obj: dynamic, names: List<String>): List<String> {
    val res = kotlin.collections.mutableListOf<String>()
    val considerKey = {key: String ->
        if (names.any {key == it || key.startsWith("${it}_") && key.endsWith("\$")})
            res += key
    }
    js("for (var key in obj) { considerKey(key) }")
    return res
}






