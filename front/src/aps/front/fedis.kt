@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*

object fedis {
    class LogGroup(val id: String, val prevID: String?)

    var died = false
    val groups = mutableListOf<LogGroup>()

    fun lrange(key: String, start: Long, end: Long): Promise<List<String>> = sendShit(json(
        "command" to "lrange",
        "key" to key,
        "start" to "" + start,
        "end" to "" + end
    ))

    fun mget(keys: List<String>): Promise<List<String>> = sendShit(json(
        "command" to "mget",
        "keys" to keys.toTypedArray()
    ))

    fun del(keys: List<String>): Promise<List<String>> = sendShit(json(
        "command" to "del",
        "keys" to keys.toTypedArray()
    ))

    fun beginLogGroup(title: String): Promise<String> = sendShit(json(
        "command" to "beginLogGroup",
        "title" to title,
        "beginMillis" to currentTimeInt()
    ))

    fun endLogGroup(id: String): Promise<Unit> = sendShit(json(
        "command" to "endLogGroup",
        "id" to id,
        "endMillis" to currentTimeInt()
    ))

    private fun <T> sendShit(request: Json): Promise<T> = async {
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

    fun pushLogGroup(title: String): Promise<Unit> = async {
        val group = LogGroup(
            id = await(fedis.beginLogGroup(title)),
            prevID = Globus.rootRedisLogMessageID
        )
        groups += group
        Globus.rootRedisLogMessageID = group.id
    }

    fun popLogGroup(): Promise<Unit> = async {
        val group = groups.removeAt(groups.lastIndex)
        await(fedis.endLogGroup(group.id))
        Globus.rootRedisLogMessageID = group.prevID
    }

}


