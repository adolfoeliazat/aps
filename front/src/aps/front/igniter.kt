/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*
import org.w3c.dom.MessageEvent
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.window

@JsName("ignite")
fun ignite() {
    println("----- Igniting front Kotlin shit -----")

    global.__asyncResult = {x: dynamic -> x}

    global.DEBUG_ACTION_HAND_DELAY = 1000
    global.ACTION_DELAY_FOR_FANCINESS = 1000
    global.LIVE_STATUS_POLLING_INTERVAL = 30000
//    global.MODE = "debug"
    global.DEBUG_SIMULATE_SLOW_NETWORK = true
    global.DEBUG_RPC_LAG_FOR_MANUAL_TESTS = 500
    global.BOOTSTRAP_VERSION = 3
    global.BACKEND_URL = "http://localhost:3100"

    js("Error").stackTraceLimit = js("Infinity")

    InitAutoReload()

//    global.Error.stackTraceLimit = js("Infinity")

//    global.igniteShit = makeUIShitIgniter()
//    global.igniteShit = makeUIShitIgniter(makeAPSShitImplCtor())

    // For hot reloading
//    global.makeAPSShitImplCtor = ::makeAPSShitImplCtor

    initEffects()
    Shitus.initTrains()

    global.Shitus = Shitus

    igniteShit()
}

fun igniteShit(): Promise<Unit> {"__async"
    hrss.browserOld = BrowserOld("default") // Kind of production

    hrss.lang = global.LANG
    hrss._t = Shitus.makeT(hrss.lang)

//    if (MODE == "debug") {
//        Shitus.initDebugFunctionsShit()
//    }

//    if (global.location.pathname.endsWith("/test.html")) {

//    fixModalJumping()

    loadCSS()

    window.onmessage = fun(e: Event) {
        e as MessageEvent

        if (e.data == const.windowMessage.fileForbidden) return openErrorModal(t("TOTE", "Этот файл не для тебя"))

        val type: String? = e.data?.asDynamic().type
        if (type != null) {
            when (type) {
                "captureVisualShit" -> return // Page -> chromext content script -> chromext background
                "visualShitCaptured" -> return visualShitCaptured(e.data.asDynamic())
            }
        }

        console.error("Obscure message", e.data)
        wtf("Some asshole has sent me an obscure message")
    }

    val search = window.location.search
    if (search.contains("test=") || search.contains("testSuite=")) {
        __await(jsFacing_igniteTestShit())
    } else {
//        global.DB = global.sessionStorage.getItem("DB") ?: "aps-dev" // TODO:vgrechka @security

        window.addEventListener("keydown", {e ->
            e as KeyboardEvent
            if (e.code == "Backquote") {
                preventAndStop(e)
                Globus.worldMaybe?.footer?.openBurger()
            }
        })

        __await(World("default").boot())
    }

    return __asyncResult(Unit)
}

val breatheBanner: dynamic by lazy {
    json(
        "show" to {
            js("$")("body").append("""
                <div id="breathe" style="position: fixed; left: 0px; top: 0px; z-index: 1000000; width: 100%; height: 100%; ">
                <div class="container">
                <div style="display: flex; align-items: center; justify-content: center; position: absolute; left: 0px; top: 200px; width: 100%; border-top: 2px dashed #b0bec5; border-bottom: 2px dashed #b0bec5; padding: 50px 0px; background-color: rgba(255,255,255,0.85);">
                <span style="margin-left: 10; font-weight: bold;">Дышите глубоко...</span>
                <div id="wholePageTicker" class="progressTicker" style="background-color: #546e7a; width: 14px; height: 28px; margin-left: 10px; margin-top: -5px"></div>
                </div>
                </div>
                </div>""")
        },

        "hide" to {
            Shitus.byid("breathe").remove()
        }
    )
}





