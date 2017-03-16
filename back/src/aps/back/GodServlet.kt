/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import into.kommon.*
import org.jooq.Result
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.io.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.*
import javax.servlet.http.*
import kotlin.properties.Delegates.notNull
import java.io.PrintWriter
import java.io.CharArrayWriter
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper



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
    var requesterOrAnonymous by notNullOnce<User>()
    var requesterOrAnonymousInitialFields by notNullOnce<UserFields>()
    var shitIsDangerous by notNullOnce<Boolean>()
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

class GodFilter : Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val responseWrapper = object:HttpServletResponseWrapper(response as HttpServletResponse) {
            val stringWriter = StringWriter()
            val printWriter = PrintWriter(stringWriter)
            override fun getWriter() = printWriter
        }

        request as HttpServletRequest
        val pathInfo = request.pathInfo

        request.characterEncoding = "UTF-8"
        val requestJSON = request.reader.readText()
//        if (pathInfo.contains("CreateOrder")) {
//            "break on me"
//        }

        val requestWrapper = object:HttpServletRequestWrapper(request) {
            val stringReader = StringReader(requestJSON)
            val bufferedReader = BufferedReader(stringReader)
            override fun getReader() = bufferedReader
        }

        chain.doFilter(requestWrapper, responseWrapper)

        if (responseWrapper.contentType.startsWith("application/json;")) {
            response.contentType = responseWrapper.contentType
            val out = response.writer

            val responseJSON = responseWrapper.stringWriter.toString()

            val ignoredShit = listOf(
                "GetSoftwareVersion", "PrivilegedRedisCommand", "GetGeneratedShit", "Ping")
            if (!ignoredShit.any {pathInfo.endsWith("/$it")}) {
                BackGlobus.rrlog.entries += RRLogEntry(pathInfo, requestJSON, responseJSON)
            }

            out.write(responseJSON)
            out.close()
        } else {
            wtf("10e5d515-1ef0-4a60-ad2d-995c13bad634")
        }
    }

    override fun init(filterConfig: FilterConfig?) {}
    override fun destroy() {}
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




























