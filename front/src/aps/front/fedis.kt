package aps.front

import aps.*

object fedis {
    fun lrange(key: String, start: Long, end: Long): Promise<List<String>> = async {
        val res = await(send(PrivilegedRedisCommandRequest()-{o->
            o.json.value = JSON.stringify(json(
                "command" to "lrange",
                "key" to key,
                "start" to "" + start,
                "end" to "" + end
            ))
        }))

        dejsonize(res.json)!!
    }

    fun mget(keys: List<String>): Promise<List<String>> = async {
        val res = await(send(PrivilegedRedisCommandRequest()-{o->
            o.json.value = JSON.stringify(json(
                "command" to "mget",
                "keys" to keys.toTypedArray()
            ))
        }))

        dejsonize(res.json)!!
    }
}


