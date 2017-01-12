package aps.back

import aps.*
import into.kommon.*

fun <T> tracingSQL(descr: String, block: () -> T): T {
    if (!BackGlobus.tracingEnabled) return block()

    val rlm = RedisLogMessage.SQL() - {o ->
        o.shortDescription = descr
        o.stage = RedisLogMessage.SQL.Stage.PENDING
        o.text = "Not known yet"
    }
    redisLog.send(rlm)

    requestShit.actualSQLFromJOOQ = null

    try {
        val res = block()
        rlm.stage = RedisLogMessage.SQL.Stage.SUCCESS
        return res
    } catch (e: Throwable) {
        rlm.stage = RedisLogMessage.SQL.Stage.FAILURE
        rlm.exceptionStack = e.stackString()
        throw e
    } finally {
        requestShit.actualSQLFromJOOQ?.let {rlm.text = it}
        rlm.endMillis = currentTimeMillis()
        redisLog.amend(rlm)
    }
}

