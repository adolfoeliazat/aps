package aps.front

import aps.*
import into.kommon.*

object fedis {
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
        val res = await(send(PrivilegedRedisCommandRequest()-{o->
            o.json.value = JSON.stringify(request)
        }))
        dejsonize(res.json)!!
    }
}


