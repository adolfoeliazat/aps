/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

object ServeDevStaticSites {
    val require = js("require")
    val express = require("express")

    @JsName("runShit")
    fun runShit(argv: dynamic) {
        serve("customer-en", 3011)
        serve("customer-ua", 3012)
        serve("writer-en", 3021)
        serve("writer-ua", 3022)
    }

    fun serve(site: String, port: Int) {
        val app = express()
        app.use(express.static("$APS_HOME/front/out/static/$site"))

        app.listen(port) {
            println("Serving $site on 127.0.0.1:$port")
        }
    }

}


