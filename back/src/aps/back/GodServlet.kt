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
import org.jooq.Result
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.*
import javax.servlet.annotation.MultipartConfig
import javax.servlet.http.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess

private val requestGlobusThreadLocal = ThreadLocal<RequestGlobusType>()

val RequestGlobus
    get() = requestGlobusThreadLocal.get() ?: bitch("RequestGlobus? What else?")

fun isRequestThread() =
    requestGlobusThreadLocal.get() != null


class RequestGlobusType {
    val stamp by lazy {
        TestServerFiddling.nextRequestTimestamp.getAndReset()
            ?: Timestamp(System.currentTimeMillis())
    }

    var skipLoggingToRedis = false
    var actualSQLFromJOOQ: String? = null
    var resultFromJOOQ: Result<*>? = null
    val redisLogParentIDs = Stack<String>()
    lateinit var commonRequestFields: CommonRequestFieldsHolder
    lateinit var servletRequest: HttpServletRequest
    var procedureCtx by notNullOnce<ProcedureContext>()
    val retrievedFields = mutableSetOf<FormFieldBack>()
}

class GodServlet : HttpServlet() {
    val log by logger()

    override fun service(req: HttpServletRequest, res: HttpServletResponse) {
        val pathInfo = req.pathInfo

        requestGlobusThreadLocal.set(RequestGlobusType())
        RequestGlobus.skipLoggingToRedis = patternsToExcludeRedisLoggingCompletely.any {pathInfo.contains(it)}
        RequestGlobus.servletRequest = req

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
                    try {
                        val pnc = procedureName.capitalize()
//                        run {
//                            val server = springctx.getBean("serve" + pnc, BitchyProcedure::class.java)
//                            server.bpc = BitchyProcedureContext(req, res)
//                        }
                        val server = springctx.getBean("serve" + pnc, BitchyProcedure::class.java)
                        server.bpc = BitchyProcedureContext(req, res)

//                        val useTx = true
                        val useTx = !pathInfo.contains("RecreateTestDatabaseSchema") // XXX

                        if (useTx) {
                            TransactionTemplate(springctx.getBean(PlatformTransactionManager::class.java)).execute {
                                server.serve()
                            }
                        } else {
                            server.serve()
                        }
                    } catch (e: NoSuchBeanDefinitionException) {
//                        clog("NoSuchBeanDefinitionException: $e")
                        val factory = remoteProcedureNameToFactory[procedureName] ?: die("No fucking factory for procedure $procedureName")
                        @Suppress("UNCHECKED_CAST")
                        val service = factory.invoke(null) as (HttpServletRequest, HttpServletResponse) -> Unit

                        if (!pathInfo.contains("GetSoftwareVersion")) { // XXX
                            TransactionTemplate(springctx.getBean(PlatformTransactionManager::class.java)).execute {
                                service(req, res)
                            }
                        } else {
                            service(req, res)
                        }
                    }
                }

                pathInfo == "/orderFile" -> {
                    serveOrderFile(req, res)
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

    private fun serveOrderFile(req: HttpServletRequest, res: HttpServletResponse) {
        val id = req.getParameter("id") ?: bitch("I want `id`")
        val token = req.getHeader("token") ?: req.getParameter("token") ?: bitch("I want `token`")

        RequestGlobus.commonRequestFields = CommonRequestFieldsHolder()-{o->
        }

        // TODO:vgrechka Check permissions
        imf("serveOrderFile")

//        val db = DB.byID(databaseID)
//        db.joo {q->
//            val user = userByToken(q, token)
//            val rows = tracingSQL("Select file") {q
//                .select().from(FILES)
//                .where(FILES.ID.eq(id.toLong()))
//                .fetch().into(JQFiles::class.java)
//            }
//            if (rows.isEmpty()) bitch("No fucking file with ID $id")
//            val file = rows[0]
//
//            val forbidden = run {
//                val rows = tracingSQL("Select file-user permissions") {q
//                    .select().from(FILE_USER_PERMISSIONS)
//                    .where(FILE_USER_PERMISSIONS.FILE_ID.eq(id.toLong()))
//                    .and(FILE_USER_PERMISSIONS.USER_ID.eq(user.id.toLong()))
//                    .fetch().into(JQFileUserPermissions::class.java)
//                }
//                rows.isEmpty()
//            }
//
//            BackGlobus.lastDownloadedPieceOfShit = PieceOfShitDownload(file.id, file.name, forbidden, file.sha1)
//
////        if (forbidden) bitch("Some asshole, namely ${user.id}, wants to download forbidden shit, namely ${file.id}")
//            if (forbidden) {
//                log.info("Some asshole, namely ${user.id}, wants to download forbidden shit, namely ${file.id}")
//                res.writer.println("""
//                    <html>
//                        <body>
//                            This shit is forbidden for you
//
//                            <script>
//                                window.addEventListener('message', e => {
//                                    if (e.data === '${const.windowMessage.whatsUp.escapeSingleQuotes()}') {
//                                        e.source.postMessage('${const.windowMessage.fileForbidden.escapeSingleQuotes()}', e.origin)
//                                    }
//                                })
//                            </script>
//                        </body>
//                    </html>
//                """)
//            } else {
//                res.contentType = file.mime
//                res.setHeader("Content-disposition", "attachment; filename=${file.name}")
//                res.outputStream.write(file.content)
//                res.outputStream.flush()
//            }
//        }
    }
}

private fun HttpServletResponse.spitText(text: String) {
    this-{o->
        o.contentType = "text/plain; charset=utf-8"
        o.writer.println(text)
        o.status = HttpServletResponse.SC_OK
    }
}


val patternsToExcludeRedisLoggingCompletely = listOf(
    "getSoftwareVersion", "mapStack", "getLiveStatus",
    "getRedisLogMessages", "getGeneratedShit", "imposeNextGeneratedPassword"
)





















