/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import java.io.*
import javax.servlet.*
import javax.servlet.http.*
import java.io.PrintWriter
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper


class GodServlet : HttpServlet() {
    override fun service(req: HttpServletRequest, res: HttpServletResponse) {
        serveShit(req, res)
    }
}

private fun HttpServletResponse.tox(): FuckingHttpServletResponse {
    val self = this
    return object: FuckingHttpServletResponse {
        override var contentType get() = self.contentType; set(value) {self.contentType = value}

        override var status: FuckingHttpServletResponse.Status
            get() {
                return when (self.status) {
                    HttpServletResponse.SC_OK -> FuckingHttpServletResponse.Status.OK
                    else -> wtf("b652b37b-7bf9-4df7-af64-7b237a2955e0    self.status = ${self.status}")
                }
            }
            set(value) {
                self.status = when (value) {
                    FuckingHttpServletResponse.Status.OK -> HttpServletResponse.SC_OK
                }
            }

        override val writer = object: FuckingHttpServletResponse.Writer {
            override fun println(s: String) = self.writer.println(s)
        }
    }
}

private fun HttpServletRequest.tox(): FuckingHttpServletRequest {
    val self = this
    return object: FuckingHttpServletRequest {
        override var characterEncoding get() = self.characterEncoding; set(value) {self.characterEncoding = value}
        override val pathInfo get() = self.pathInfo

        override val reader = object: FuckingHttpServletRequest.Reader {
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
        val queryString = request.queryString

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
                BackGlobus.rrlog.entries += RRLogEntry(++requestID, pathInfo, queryString, requestJSON, responseJSON)
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


fun HttpServletResponse.spitText(text: String) {
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




























