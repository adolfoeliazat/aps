/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import into.kommon.*
import javax.servlet.*
import javax.servlet.http.*

class GodServlet : HttpServlet() {
    val log by logger()

    override fun service(servletRequest: HttpServletRequest, servletResponse: HttpServletResponse) {
        val pathInfo = servletRequest.pathInfo
        try {
            when {
                pathInfo.startsWith("/rpc/") -> {
                    val procedureName = servletRequest.pathInfo.substring("/rpc/".length)
                    val factory = remoteProcedureNameToFactory[procedureName] ?: die("No fucking factory for procedure $procedureName")
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

