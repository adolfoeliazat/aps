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
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class GodServlet : HttpServlet() {
    val log by logger()

    override fun service(req: HttpServletRequest, res: HttpServletResponse) {
        val pathInfo = req.pathInfo

        _requestShit.set(RequestShit())
        requestShit.skipLoggingToRedis = patternsToExcludeRedisLoggingCompletely.any {pathInfo.contains(it)}

        try {
            when {
                pathInfo == "/welcome" -> {
                    res.spitText("FUCK YOU")
                }

                pathInfo == "/startMoment" -> {
                    res.spitText(SimpleDateFormat("YYYYMMDD-hhmmss").format(BackGlobus.startMoment))
                }

                pathInfo == "/version" -> {
                    res.spitText(BackGlobus.version)
                }

                pathInfo.startsWith("/rpc/") -> {
                    val procedureName = req.pathInfo.substring("/rpc/".length)
                    val factory = remoteProcedureNameToFactory[procedureName] ?: die("No fucking factory for procedure $procedureName")
                    @Suppress("UNCHECKED_CAST")
                    val service = factory.invoke(null) as (HttpServletRequest, HttpServletResponse) -> Unit
                    service(req, res)
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

private fun HttpServletResponse.spitText(text: String) {
    this-{o->
        o.contentType = "text/plain; charset=utf-8"
        o.writer.println(text)
        o.status = HttpServletResponse.SC_OK
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





















