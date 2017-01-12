/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.RedisLogMessage.Separator.Type.*
import aps.back.generated.jooq.*
import aps.back.generated.jooq.Tables.*
import aps.back.generated.jooq.tables.pojos.*
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
        requestShit.servletRequest = req

        res.addHeader("Access-Control-Allow-Origin", "*")

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

                pathInfo == "/file" -> {
                    serveFile(req, res)
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

    private fun serveFile(req: HttpServletRequest, res: HttpServletResponse) {
        val fileID = req.getParameter("fileID") ?: bitch("I want `fileID`")
        val databaseID = req.getHeader("databaseID") ?: req.getParameter("databaseID") ?: bitch("I want `databaseID`")
        val token = req.getHeader("token") ?: req.getParameter("token") ?: bitch("I want `token`")

        val db = DB.byID(databaseID)
        db.jooshit {q->
            val user = userByToken(q, token)
            val rows = q("Select file")
                .select().from(FILES)
                .where(FILES.ID.eq(fileID.toLong()))
                .fetch().into(JQFiles::class.java)
            if (rows.isEmpty()) bitch("No fucking file with ID $fileID")
            val file = rows[0]

            val forbidden = run {
                val rows = q("Select file-user permissions")
                    .select().from(FILE_USER_PERMISSIONS)
                    .where(FILE_USER_PERMISSIONS.FILE_ID.eq(fileID.toLong()))
                    .and(FILE_USER_PERMISSIONS.USER_ID.eq(user.id.toLong()))
                    .fetch().into(JQFileUserPermissions::class.java)
                rows.isEmpty()
            }

            BackGlobus.lastDownloadedPieceOfShit = PieceOfShitDownload(file.id, file.name, forbidden, file.sha1)

//        if (forbidden) bitch("Some asshole, namely ${user.id}, wants to download forbidden shit, namely ${file.id}")
            if (forbidden) {
                log.info("Some asshole, namely ${user.id}, wants to download forbidden shit, namely ${file.id}")
                res.writer.println("""
                    <html>
                        <body>
                            This shit is forbidden for you

                            <script>
                                window.addEventListener('message', e => {
                                    if (e.data === '${const.windowMessage.whatsUp.escapeSingleQuotes()}') {
                                        e.source.postMessage('${const.windowMessage.fileForbidden.escapeSingleQuotes()}', e.origin)
                                    }
                                })
                            </script>
                        </body>
                    </html>
                """)
            } else {
                res.contentType = file.mime
                res.setHeader("Content-disposition", "attachment; filename=${file.name}")
                res.outputStream.write(file.content)
                res.outputStream.flush()
            }
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
    lateinit var servletRequest: HttpServletRequest
}

val _requestShit = ThreadLocal<RequestShit>()
val isRequestThread: Boolean get() = _requestShit.get() != null
val requestShit: RequestShit get() = _requestShit.get()

val patternsToExcludeRedisLoggingCompletely = listOf(
    "getSoftwareVersion", "mapStack", "getLiveStatus",
    "getRedisLogMessages", "getGeneratedShit", "imposeNextGeneratedPassword"
)





















