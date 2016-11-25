/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.RedisLogMessage.Type.*
import into.kommon.*
import javax.servlet.*
import javax.servlet.http.*
import kotlin.system.exitProcess

class GodServlet : HttpServlet() {
    val log by logger()

    override fun service(servletRequest: HttpServletRequest, servletResponse: HttpServletResponse) {
        val pathInfo = servletRequest.pathInfo

        _requestShit.set(RequestShit())
        val patternsToNotLogToRedis = listOf(
            "getSoftwareVersion", "getLiveStatus", "getRedisLogMessages", "resetTestDatabase",
            "getGeneratedShit", "imposeNextGeneratedPassword", "mapStack")
        requestShit.skipLoggingToRedis = patternsToNotLogToRedis.any {pathInfo.contains(it)}

        try {
            try {
                redisLog.send(RedisLogMessage(SEPARATOR, "Begin service: $pathInfo {"))
                when {
                    pathInfo.startsWith("/rpc/") -> {
                        val procedureName = servletRequest.pathInfo.substring("/rpc/".length)
                        val factory = remoteProcedureNameToFactory[procedureName] ?: die("No fucking factory for procedure $procedureName")
                        val service = factory.invoke(null) as (HttpServletRequest, HttpServletResponse) -> Unit
                        service(servletRequest, servletResponse)
                    }

                    else -> bitch("Weird request path: $pathInfo")
                }
            } finally {
                redisLog.send(RedisLogMessage(SEPARATOR, "End service: $pathInfo }"))
            }
        } catch(fuckup: Throwable) {
            log.error("Can't fucking service [$pathInfo]: ${fuckup.message}", fuckup)

            if (fuckup is WithCulprit) {
                log.section("Culprit:\n\n" + fuckup.culprit.constructionStack.joinToString("\n"){it.toString()})
            }

            throw ServletException(fuckup)
        }
    }
}

class RequestShit {
    var skipLoggingToRedis = false
}

val _requestShit = ThreadLocal<RequestShit>()
val isRequestThread: Boolean get() = _requestShit.get() != null
val requestShit: RequestShit get() = _requestShit.get()




























