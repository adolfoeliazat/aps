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
            }
        }
        msg.beginMillis = currentTimeMillis()
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













