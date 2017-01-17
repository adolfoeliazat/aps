/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.nodeRequire

object ServeDevStaticSites {
    val express = nodeRequire("express")

    @JsName("runShit")
    fun runShit(argv: dynamic) {
        serve("customer-en", 3011)
        serve("customer-ua", 3012)
        serve("writer-en", 3021)
        serve("writer-ua", 3022)

        run {
            val app = express()
            app.use(express.static("e:/work"))

            val port = 3030
            app.listen(port) {
                println("Serving sources on 127.0.0.1:$port")
            }
        }
    }

    fun serve(site: String, port: Int) {
        val app = express()
        app.use {req: dynamic, res: dynamic, next: dynamic ->
            res.header("Access-Control-Allow-Origin", "*")
            res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept")
            next()
        }
        app.use(express.static("$APS_HOME/front/out/static/$site"))

        app.listen(port) {
            println("Serving $site on 127.0.0.1:$port")
        }
    }

}


