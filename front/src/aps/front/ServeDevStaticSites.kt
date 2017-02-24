/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*

object ServeDevStaticSites {
    val express = nodeRequire("express")

    @JsName("runShit")
    fun runShit(argv: dynamic) {
        serveStaticSite("customer-en", 3011)
        serveStaticSite("customer-ua", 3012)
        serveStaticSite("writer-en", 3021)
        serveStaticSite("writer-ua", 3022)

        run {
            val app = express()
            app.use(express.static("e:/work"))

            val port = 3030
            app.listen(port) {
                println("Serving sources on 127.0.0.1:$port")
            }
        }
    }

    fun serveStaticSite(site: String, port: Int) {
        serveDir(site, port, "${const.file.APS_HOME}/front/out/static/$site")
    }

    private fun serveDir(siteName: String, port: Int, dir: String) {
        val app = express()
        app.use {req: dynamic, res: dynamic, next: dynamic ->
            res.header("Access-Control-Allow-Origin", "*")
            res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept")
            next()
        }
        app.use(express.static(dir))

        app.listen(port) {
            println("Serving $siteName on 127.0.0.1:$port")
        }
    }

}


