/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.RedisLogMessage.Separator.Type.*
import into.kommon.*
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.*
import javax.servlet.annotation.MultipartConfig
import javax.servlet.http.*
import kotlin.system.exitProcess

class GodServlet : HttpServlet() {
    val log by logger()

    override fun service(servletRequest: HttpServletRequest, servletResponse: HttpServletResponse) {
        val pathInfo = servletRequest.pathInfo

        _requestShit.set(RequestShit())
        requestShit.skipLoggingToRedis = patternsToExcludeRedisLoggingCompletely.any {pathInfo.contains(it)}

        try {
            when {
                pathInfo == "/fuck" -> {
                    servletResponse-{o->
                        o.contentType = "text/plain; charset=utf-8"
                        o.writer.println("----- WELCOME AND FUCK YOU -----")
                        o.status = HttpServletResponse.SC_OK
                    }
                }

                pathInfo == "/startMoment" -> {
                    servletResponse-{o->
                        o.contentType = "text/plain; charset=utf-8"
                        o.writer.println(SimpleDateFormat("YYYYMMDD-hhmmss").format(BackGlobus.startMoment))
                        o.status = HttpServletResponse.SC_OK
                    }
                }

                pathInfo == "/version" -> {
                    servletResponse-{o->
                        o.contentType = "text/plain; charset=utf-8"
                        o.writer.println(this::class.java.classLoader.getResource("aps/version.txt").readText())
                        o.status = HttpServletResponse.SC_OK
                    }
                }

                pathInfo == "/kill" -> {
                    System.exit(0)
                }

                pathInfo.startsWith("/rpc/") -> {
                    val procedureName = servletRequest.pathInfo.substring("/rpc/".length)
                    val factory = remoteProcedureNameToFactory[procedureName] ?: die("No fucking factory for procedure $procedureName")
                    @Suppress("UNCHECKED_CAST")
                    val service = factory.invoke(null) as (HttpServletRequest, HttpServletResponse) -> Unit
                    service(servletRequest, servletResponse)
                }

                else -> bitch("Weird request path: $pathInfo")
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
    var actualSQLFromJOOQ: String? = null
    val redisLogParentIDs = Stack<String>()
    lateinit var commonRequestFields: CommonRequestFieldsHolder
}

val _requestShit = ThreadLocal<RequestShit>()
val isRequestThread: Boolean get() = _requestShit.get() != null
val requestShit: RequestShit get() = _requestShit.get()

val patternsToExcludeRedisLoggingCompletely = listOf(
    "getSoftwareVersion", "mapStack", "getLiveStatus",
    "getRedisLogMessages", "getGeneratedShit", "imposeNextGeneratedPassword"
)





















