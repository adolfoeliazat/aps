package aps.back

import aps.*
import into.kommon.*
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.time.LocalDateTime
import java.util.*

val jedisPool by lazy {
    JedisPool(JedisPoolConfig(), "localhost").let {
        Runtime.getRuntime().addShutdownHook(object:Thread() {
            override fun run() {
                println("Shutting Redis shit down")
                it.destroy()
            }
        })
        it
    }
}

// TODO:vgrechka Make this asynchronous
object redisLog {
    fun send(msg: RedisLogMessage) {
        if (shouldSkip()) return

        msg.id = UUID.randomUUID().toString()
        msg.parentID = RedisLogMessage.ROOT_ID
        if (isRequestThread) {
            requestShit.redisLogParentIDs.let {
                if (it.isNotEmpty())
                    msg.parentID = it.peek()
                else requestShit.commonRequestFields.rootRedisLogMessageID?.let {
                    msg.parentID = it
                }
            }
        }
        msg.beginMillis = tryOrDefault({msg.beginMillis}, {currentTimeMillis()})
        msg.stamp = LocalDateTime.now().format(PG_LOCAL_DATE_TIME)
        msg.stack = CaptureStackException().stackString()

        jedisPool.resource.use {jedis->
            jedis.set(msg.id, shittyObjectMapper.writeValueAsString(msg))
            jedis.rpush("${msg.parentID}:children", msg.id)
        }
    }

    fun amend(msg: RedisLogMessage) {
        if (shouldSkip()) return

        jedisPool.resource.use {jedis->
            jedis.set(msg.id, shittyObjectMapper.writeValueAsString(msg))
        }
    }

    private fun shouldSkip() = isRequestThread && requestShit.skipLoggingToRedis

    fun <T> group(title: String, block: () -> T): T {
        if (shouldSkip()) return block()

        val rlm = RedisLogMessage.Fuck()-{o->
            o.text = title
            o.stage = RedisLogMessage.Fuck.Stage.PENDING
        }
        redisLog.send(rlm)
        requestShit.redisLogParentIDs.push(rlm.id)

        try {
            val res = block()
            rlm.stage = RedisLogMessage.Fuck.Stage.SUCCESS
            return res
        } catch(e: Throwable) {
            rlm.stage = RedisLogMessage.Fuck.Stage.FAILURE
            rlm.exceptionStack = e.stackString()
            throw e
        } finally {
            rlm.endMillis = currentTimeMillis()
            requestShit.redisLogParentIDs.pop()
            redisLog.amend(rlm)
        }
    }
}

//@RemoteProcedureFactory fun getRedisLogMessages() = testProcedure(
//    GetRedisLogMessagesRequest(),
//    needsDB = false,
//    runShit = fun (ctx, req): GetRedisLogMessagesRequest.Response {
//        val jsons = jedisPool.resource.use {jedis ->
//            val ids = jedis.lrange("log", 0, -1)
//            if (ids.isNotEmpty())
//                jedis.mget(*ids.toTypedArray())
//            else
//                listOf()
//        }
//
//        return GetRedisLogMessagesRequest.Response(
//            jsons.filterNotNull().map {objectMapper.readValue(it, RedisLogMessage::class.java)})
//    }
//)

@RemoteProcedureFactory fun sendRedisLogMessage() = testProcedure(
    SendRedisLogMessageRequest(),
    needsDB = false,
    runShit = fun(ctx, req): GenericResponse {
        redisLog.send(RedisLogMessage.Separator()-{o->
            o.type = req.type.value
            o.text = req.text.value
        })
        return GenericResponse()
    }
)

private val idToLogGroupMessage = Collections.synchronizedMap(mutableMapOf<String, RedisLogMessage>())

@RemoteProcedureFactory fun privilegedRedisCommand() = testProcedure(
    PrivilegedRedisCommandRequest(),
    needsDB = false,
    runShit = fun (ctx, req): JSONResponse {
        val rmap = shittyObjectMapper.readValue(req.json.value, Map::class.java)
        val command: String = cast(rmap["command"])

        val res: Any = run {when (command) {
            "beginLogGroup" -> {
                val title: String = cast(rmap["title"])
                val beginMillis: Long = cast(rmap["beginMillis"])
                val rlm = RedisLogMessage.Fuck()-{o->
                    o.stage = RedisLogMessage.Fuck.Stage.PENDING
                    o.text = title
                    o.beginMillis = beginMillis
                }
                redisLog.send(rlm)
                idToLogGroupMessage[rlm.id] = rlm
                return@run rlm.id
            }
            "endLogGroup" -> {
                val id: String = cast(rmap["id"])
                val endMillis: Long = cast(rmap["endMillis"])
                val rlm = idToLogGroupMessage[id] ?: bitch("Unknown log group: $id")
                idToLogGroupMessage.remove(id)
                rlm.endMillis = endMillis
                redisLog.amend(rlm)
                return@run Unit
            }
            else -> jedisPool.resource.use {jedis ->
                when (command) {
                    "lrange" -> {
                        val key: String  = cast(rmap["key"])
                        val start: Long = (rmap["start"] as String).toLong()
                        val end: Long = (rmap["end"] as String).toLong()
                        return@run jedis.lrange(key, start, end)
                    }
                    "mget" -> {
                        val keys: List<String> = cast(rmap["keys"])
                        return@run jedis.mget(*keys.toTypedArray())
                    }
                    "del" -> {
                        val keys: List<String> = cast(rmap["keys"])
                        return@run jedis.del(*keys.toTypedArray())
                    }
                    else -> wtf("command: $command")
                }
            }

        }}

        return JSONResponse(shittyObjectMapper.writeValueAsString(res))
    }
)













