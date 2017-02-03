@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*
import kotlin.js.Json
import kotlin.js.json

object fedis {
    class LogGroup(val id: String, val prevID: String?)

    var died = false
    val groups = mutableListOf<LogGroup>()

    fun lrange(key: String, start: Long, end: Long): Promisoid<List<String>> = sendShit(json(
        "command" to "lrange",
        "key" to key,
        "start" to "" + start,
        "end" to "" + end
    ))

    fun mget(keys: List<String>): Promisoid<List<String>> = sendShit(json(
        "command" to "mget",
        "keys" to keys.toTypedArray()
    ))

    fun del(keys: List<String>): Promisoid<List<String>> = sendShit(json(
        "command" to "del",
        "keys" to keys.toTypedArray()
    ))

    fun beginLogGroup(title: String): Promisoid<String> = sendShit(json(
        "command" to "beginLogGroup",
        "title" to title,
        "beginMillis" to currentTimeInt()
    ))

    fun endLogGroup(id: String): Promisoid<Unit> = sendShit(json(
        "command" to "endLogGroup",
        "id" to id,
        "endMillis" to currentTimeInt()
    ))

    private fun <T> sendShit(request: Json): Promisoid<T> = async {
        if (died) return@async js("'Redis is dead'") // TODO:vgrechka Devise something better. Should return T...

        try {
            val res = await(send(PrivilegedRedisCommandRequest()-{o->
                o.json.value = JSON.stringify(request)
            }))
            dejsonize<T>(res.json)!!
        } catch(e: dynamic) {
            died = true
            dwarnStriking("Redis is dead now, thank you everyone")
        }
    }

    fun pushLogGroup(title: String): Promisoid<Unit> = async {
        val group = LogGroup(
            id = await(fedis.beginLogGroup(title)),
            prevID = Globus.rootRedisLogMessageID
        )
        groups += group
        Globus.rootRedisLogMessageID = group.id
    }

    fun popLogGroup(): Promisoid<Unit> = async {
        val group = groups.removeAt(groups.lastIndex)
        await(fedis.endLogGroup(group.id))
        Globus.rootRedisLogMessageID = group.prevID
    }

}


