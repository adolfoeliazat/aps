/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
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




class GodServlet : HttpServlet() {
    val log by logger()

    override fun service(req: HttpServletRequest, res: HttpServletResponse) {
        val pathInfo = req.pathInfo

        requestGlobusThreadLocal.set(RequestGlobusType())
//        RequestGlobus.skipLoggingToRedis = patternsToExcludeRedisLoggingCompletely.any {pathInfo.contains(it)}

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
                        val procNameCaps = procedureName.capitalize()
                        val server = springctx.getBean("serve" + procNameCaps, BitchyProcedure::class.java)
                        server.bpc = BitchyProcedureContext(req.tox(), res.tox())

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

private fun HttpServletResponse.tox(): XHttpServletResponse {
    val self = this
    return object:XHttpServletResponse {
        override var contentType get() = self.contentType; set(value) {self.contentType = value}

        override var status: XHttpServletResponse.Status
            get() {
                return when (self.status) {
                    HttpServletResponse.SC_OK -> XHttpServletResponse.Status.OK
                    else -> wtf("b652b37b-7bf9-4df7-af64-7b237a2955e0    self.status = ${self.status}")
                }
            }
            set(value) {
                self.status = when (value) {
                    XHttpServletResponse.Status.OK -> HttpServletResponse.SC_OK
                }
            }

        override val writer = object:XHttpServletResponse.Writer {
            override fun println(s: String) = self.writer.println(s)
        }
    }
}

private fun HttpServletRequest.tox(): XHttpServletRequest {
    val self = this
    return object:XHttpServletRequest {
        override var characterEncoding get() = self.characterEncoding; set(value) {self.characterEncoding = value}
        override val pathInfo get() = self.pathInfo

        override val reader = object:XHttpServletRequest.Reader {
            override fun readText() = self.reader.readText()
        }
    }
}

class GodFilter : Filter {
    var requestID = 0L

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
                BackGlobus.rrlog.entries += RRLogEntry(++requestID, pathInfo, requestJSON, responseJSON)
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


//val patternsToExcludeRedisLoggingCompletely = listOf(
//    "getSoftwareVersion", "mapStack", "getLiveStatus",
//    "getRedisLogMessages", "getGeneratedShit", "imposeNextGeneratedPassword"
//)




























