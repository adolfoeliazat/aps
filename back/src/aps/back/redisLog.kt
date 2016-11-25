package aps.back

import aps.*
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.time.LocalDateTime
import kotlin.system.exitProcess

val jedisPool by lazy {
    JedisPool(JedisPoolConfig(), "localhost").let {
        Runtime.getRuntime().addShutdownHook(object:Thread() {
            override fun run() {
                println("\n\n================ Shutting shit down ==============\n\n")
                it.destroy()
            }
        })
        it
    }
}

object redisLog {
    val MAX_LEN = 200L

    fun send(msg: RedisLogMessage) {
        if (isRequestThread && requestShit.skipLoggingToRedis) return

        // TODO:vgrechka Make this asynchronous
        msg.stamp = LocalDateTime.now().format(PG_LOCAL_DATE_TIME)
        val json = objectMapper.writeValueAsString(msg)

        jedisPool.resource.use {jedis->
            jedis.rpush("log", json)
            jedis.ltrim("log", -MAX_LEN, -1)
        }
    }
}

@RemoteProcedureFactory fun getRedisLogMessages() = testProcedure(
    GetRedisLogMessagesRequest(),
    needsDB = false,
    runShit = {ctx, req ->
        val items = jedisPool.resource.use {jedis ->
            jedis.lrange("log", 0, -1).map {objectMapper.readValue(it, RedisLogMessage::class.java)}
        }
        GetRedisLogMessagesRequest.Response(items)
    }
)





