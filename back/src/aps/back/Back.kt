/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.HiRequest
import aps.HiResponse
import co.paralleluniverse.fibers.Suspendable
import co.paralleluniverse.fibers.servlet.FiberHttpServlet
import com.fasterxml.jackson.databind.ObjectMapper
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

val objectMapper = ObjectMapper()

class HiRemoteProcedure {
    fun invoke(req: HiRequest, res: HiResponse) {
        res.saying = if (req.name != null)
            "Expecting hi? Fuck you, ${req.name}, OK?.."
        else
            "I need your name, motherfucker"

        res.backendInstance = "dev-1"
    }
}

class GodServlet : FiberHttpServlet() {
    @Suspendable
    override fun doPost(servletRequest: HttpServletRequest, servletResponse: HttpServletResponse) {
        servletResponse.addHeader("Access-Control-Allow-Origin", "*")

        val pathInfo = servletRequest.pathInfo
        when {
            pathInfo.startsWith("/rpc/") -> handleRPC(servletRequest, servletResponse)
            else -> bitch("Weird request path: $pathInfo")
        }
    }

    private fun handleRPC(servletRequest: HttpServletRequest, servletResponse: HttpServletResponse) {
        val procedureName = servletRequest.pathInfo.substring("/rpc/".length)
        val cnamePrefix = procedureName.capitalize()

        val procedureClass = Class.forName("aps.back.${cnamePrefix}RemoteProcedure")
        val requestClass = Class.forName("aps.${cnamePrefix}Request")
        val responseClass = Class.forName("aps.${cnamePrefix}Response")

        val requestJSON = servletRequest.reader.readText()
        servletRequest.reader.close()
        val request = objectMapper.readValue(requestJSON, requestClass)

        servletResponse.contentType = "application/json; charset=utf-8"
        servletResponse.status = HttpServletResponse.SC_OK

        val procedure = procedureClass.newInstance()
        val response = responseClass.newInstance()

        val call = procedure.javaClass.getMethod("invoke", requestClass, responseClass)
        call.invoke(procedure, request, response)

        val json = objectMapper.writeValueAsString(response)

        servletResponse.writer.println(json)
    }

}

fun main(args: Array<String>) {
    val server = Server(8080)

    val handler = ServletHandler()
    server.handler = handler
    handler.addServletWithMapping(GodServlet::class.java, "/*")

    server.start()

    println("APS backend shit is spinning...")
    server.join()
}


