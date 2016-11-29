package aps.back

import aps.*
import into.kommon.*
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.io.PrintWriter
import java.io.StringWriter
import java.time.LocalDateTime
import java.util.*
import kotlin.system.exitProcess

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

object redisLog {
    val MAX_LEN = 200L

    fun send(msg: RedisLogMessage) {
        // TODO:vgrechka Make this asynchronous
        if (shouldSkip()) return

        msg.id = UUID.randomUUID().toString()
        msg.stamp = LocalDateTime.now().format(PG_LOCAL_DATE_TIME)
        msg.stack = CaptureStackException().stackString()

        jedisPool.resource.use {jedis->
            jedis.set(msg.id, shittyObjectMapper.writeValueAsString(msg))
            jedis.rpush("log", msg.id)
            jedis.ltrim("log", -MAX_LEN, -1)
        }
    }

    fun amend(msg: RedisLogMessage) {
        // TODO:vgrechka Make this asynchronous
        if (shouldSkip()) return

        jedisPool.resource.use {jedis->
            jedis.set(msg.id, shittyObjectMapper.writeValueAsString(msg))
        }
    }

    private fun shouldSkip() = isRequestThread && requestShit.skipLoggingToRedis

    fun <T> group(title: String, block: () -> T): T {
        return block()
    }
}

@RemoteProcedureFactory fun getRedisLogMessages() = testProcedure(
    GetRedisLogMessagesRequest(),
    needsDB = false,
    runShit = fun (ctx, req): GetRedisLogMessagesRequest.Response {
        val jsons = jedisPool.resource.use {jedis ->
            val ids = jedis.lrange("log", 0, -1)
            if (ids.isNotEmpty())
                jedis.mget(*ids.toTypedArray())
            else
                listOf()
        }

        return GetRedisLogMessagesRequest.Response(
            jsons.filterNotNull().map {objectMapper.readValue(it, RedisLogMessage::class.java)})
    }
)

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

@RemoteProcedureFactory fun privilegedRedisCommand() = testProcedure(
    PrivilegedRedisCommandRequest(),
    needsDB = false,
    runShit = fun (ctx, req): JSONResponse {
        val res = jedisPool.resource.use {jedis ->
            val rmap = shittyObjectMapper.readValue(req.json.value, Map::class.java)
            val command: String = cast(rmap["command"])
            when (command) {
                "lrange" -> {
                    jedis.lrange(rmap["key"] as String,
                                 (rmap["start"] as String).toLong(),
                                 (rmap["end"] as String).toLong())
                }
                "mget" -> {
                    val keys: List<String> = cast(rmap["keys"])
                    jedis.mget(*keys.toTypedArray())
                }
                else -> wtf("command: $command")
            }
        }

        return JSONResponse(shittyObjectMapper.writeValueAsString(res))
    }
)













