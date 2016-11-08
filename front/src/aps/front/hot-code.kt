/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import kotlin.browser.window

class InitAutoReload {
    lateinit var initialCtime: String

    init {runni {"__async"
        initialCtime = __await(GetSoftwareVersionRequest.send()).ctime
        schedule()
    }}

    fun schedule() {
        window.setTimeout({tick()}, 500)
    }

    fun tick() {"__async"
        if (initialCtime != __await(GetSoftwareVersionRequest.send()).ctime) {
            val href = window.location.href
                .replace(Regex("#.*$"), "") // Otherwise it doesn't actually reload page
            window.location.href = href
        } else {
            schedule()
        }
    }
}

//fun runHotCodeUpdateStuff(arg: dynamic): dynamic {"__async"
//    val hotFunctions: dynamic = arg.hotFunctions
//    val applyHotCode: dynamic = arg.applyHotCode
//
//    var lastBackendInstanceID: dynamic = null
//    var lastBundleCtime: dynamic = null
//    var hotUpdateNumber: dynamic = 0
//    var lastUpdateAttemptAt: dynamic = null
//    var killed: dynamic = null
//
//    val hotEntries = js("[]")
////    run {
////        val res = __await<dynamic>(jshit.debugRPC(json("fun" to "danger_getHotShitCodeEntries", "db" to null)))
////        for (entry in jsArrayToList(res.items)) {
////            entry.`fun` = hotFunctions.find{x: dynamic -> x.hotShitTag == entry.hotShitTag}
////            if (!entry.`fun`) {
////                console.error("Gimme hot function for tag [${entry.hotShitTag}]")
////                return null
////            }
////            hotEntries.push(entry)
////        }
////    }
//
//    val period = 500
//
//    fun tick() {"__async"
//        if (killed) {
//            dlog("You fucking killed hot code update")
//            return@tick
//        }
//
//        var shouldScheduleAgain = true
//
//        try {
//            if (hrss.hotCodeUpdateDisabled) return@tick
//
//            try {
////                val sofv = __await<dynamic>(jshit.debugRPC(json("fun" to "danger_getSoftwareVersion", "db" to null)))
//                val sofv = __await(GetSoftwareVersionRequest.send())
//
////                if (sofv.ctime) { // Can be undefined if bundle is deleted
//                    if (lastBundleCtime && lastBundleCtime !== sofv.ctime) {
//                        dlog("Bundle was updated, you can refresh this shit")
//                    }
//                    lastBundleCtime = sofv.ctime
////                }
//
//                val biid = sofv.backendInstanceID
//                if (!lastBackendInstanceID) {
//                    lastBackendInstanceID = biid
//                    return@tick
//                }
//
//                if (lastBackendInstanceID !== biid) {
//                    lastBackendInstanceID = biid
//                    val shouldUpdate = !lastUpdateAttemptAt || (global.Date.now() - lastUpdateAttemptAt >= 10000) // XXX Work around of nodemon's bug with restarting shit twice (possibly https://github.com/remy/nodemon/issues/763)
//                    // const shouldUpdate = true
//                    if (shouldUpdate) {
//                        lastUpdateAttemptAt = global.Date.now()
//                        dlog("Starting hot update #${++hotUpdateNumber}...")
//                        Shitus.hideStackRevelation()
//
//                        val updatedHotFunctions = json()
//
////                        val res = __await<dynamic>(jshit.debugRPC(json("fun" to "danger_getHotShitCodeEntries", "db" to null)))
////                        for (entry in jsArrayToList(res.items)) {
////                            val hotShitTag: dynamic = entry.hotShitTag
////                            val newCode: dynamic = entry.code
////                            val hotEntry = hotEntries.find{x: dynamic -> x.hotShitTag == hotShitTag}
////                            if (!hotEntry) {
////                                return@tick console.error("Hot entry [${hotShitTag}] arrived from backend, but we are not tracking it. Giving up")
////                            }
////
////                            fun stripVolatileStuff(code: dynamic): dynamic {
////                                return code.replace(global.RegExp("'[^']*?\\.ts\\[\\d+\\]:\\d+:\\d+'", "g"), "SOMEWHERE")
////                            }
////
////                            val currentCodeWithoutVolatileStuff = stripVolatileStuff(hotEntry.code)
////                            val newCodeWithoutVolatileStuff = stripVolatileStuff(newCode)
////
////                            if (currentCodeWithoutVolatileStuff === newCodeWithoutVolatileStuff) {
////                            } else {
////                                try {
////                                    val oldFun = hotEntry.`fun`
////                                    val newFun = oldFun.nearEval("(" + newCode + ")")
////                                    newFun.hotShitTag = oldFun.hotShitTag
////                                    newFun.nearEval = oldFun.nearEval
////
////                                    hotEntry.code = newCode
////                                    hotEntry.`fun` = newFun
////                                    updatedHotFunctions[hotShitTag] = newFun
////                                    // dlog("Evaluated new code for hot entry [${hotShitTag}]")
////                                } catch (e: Throwable) {
////                                    jshit.clogError(e, "Failed to evaluate new code for hot entry [${hotShitTag}]. Giving up")
////                                    return@tick
////                                }
////                            }
////                        }
//
//                        try {
//                            __await<dynamic>(applyHotCode(json("updatedHotFunctions" to updatedHotFunctions)))
//                        } catch (e: Throwable) {
//                            if (e.message != "UI assertion failed") {
//                                clogError(e, "Failed to apply hot code")
//                                Shitus.revealStack(json("exception" to e))
//                                return@tick
//                            }
//                        }
//
//                        dlog("Finished hot update #${hotUpdateNumber} ")
//                    }
//                }
//            } catch (_e: Throwable) {
//                val e: dynamic = _e
//                if (e.requestHasBeenTerminated) {
//                    console.warn("Hot update tick failed slightly, will try again soon...")
//                } else {
//                    clogError(e, "Hot update tick failed badly, giving up")
//                    shouldScheduleAgain = false
//                }
//            }
//        } finally {
//            if (shouldScheduleAgain) {
//                timeoutSet(period, ::tick)
//            }
//        }
//    }
//
//    timeoutSet(period, ::tick)
//
//    val me = json(
//        "kill" to {
//            killed = true
//        }
//    )
//
//    dlog("Listening for hot code updates")
//    return me
//}
//
//
//fun jsFacing_initHotCodeShit(impl: dynamic,
//                             instantiateImpl: dynamic,
////                             makeUIShitIgniterDef: dynamic,
//                             ui: World,
//                             hotFunctions: dynamic) {
//    "__async"
//    if (MODE != "debug") return
//
//    if (hrss.hotCodeListener) {
//        hrss.hotCodeListener.kill()
//    }
//
//    hrss.hotCodeListener = __await<dynamic>(runHotCodeUpdateStuff(json(
//        "hotFunctions" to js("[]").concat(
////            getMakeOrRemakeExportedShitFunction(),
////            makeUIShitIgniterDef.Impl,
////            initDebugFunctions,
////            getInitUIFunctionsFunction(),
////            getMakeOrRemakeArtFunction(),
//        /*...*/hotFunctions.map({hof: dynamic -> hof.`fun`})),
//
//        "applyHotCode" to {arg: dynamic -> "__async"
//            val updatedHotFunctions = arg.updatedHotFunctions
//
//            hrss.isHotReloading = true
//            Shitus.revealStack.calledTimes = 0
//
//            run {
//                val things = Shitus.values(hrss.thingsToDoBeforeHotUpdate)
//                hrss.thingsToDoBeforeHotUpdate = json()
//                for (thing in jsArrayToList(things)) {
//                    __await<dynamic>(thing())
//                }
//            }
//
////            for (hof in jsArrayToList(hotFunctions)) {
////                val `fun` = updatedHotFunctions[hof.`fun`.hotShitTag]
////                if (`fun` != null) {
////                    `fun`()
////                    dlog("Hot-refreshed ${`fun`.name}")
////                }
////            }
////
////            val hotImpl = updatedHotFunctions[makeUIShitIgniterDef.Impl.hotShitTag]
////            val hotMakeOrRemakeExportedShit = updatedHotFunctions[getMakeOrRemakeExportedShitFunction().hotShitTag]
////
////            // const shouldMakeOrRemakeExportedShit = hotMakeOrRemakeExportedShit || hotImpl
////            if (true) {
////                if (hotMakeOrRemakeExportedShit) {
////                    setMakeOrRemakeExportedShitFunction(hotMakeOrRemakeExportedShit)
////                }
////                getMakeOrRemakeExportedShitFunction()()
////                dlog("Hot-refreshed makeOrRemakeExportedShit")
////            }
//
//
//            run { // Kotlin
////                Shitus.invalidateKotlinStackSourceMapConsumer()
//
//                fun loadScript(src: String): Promise<Unit> {"__async"
//                    __await<dynamic>(newNativePromise {resolve: dynamic, reject: dynamic ->
//                        val script = global.document.createElement("script")
//                        script.type = "text/javascript"
//                        script.async = true
//                        script.onload = {
//                            resolve()
//                        }
//                        script.src = src
//                        global.document.getElementsByTagName("head")[0].appendChild(script)
//                    })
//
//                    dlog("Loaded $src")
//                    return __asyncResult(Unit)
//                }
//
//                __await(loadScript("into-kommon-js-enhanced.js"))
//                __await(loadScript("front-enhanced.js"))
//
//                js("""
//                    global.kot = kotlin.modules['front']
//                    kot.aps.front.ignite()
//                """)
//
////                val hotIgnition = true
////                kokoko(hotIgnition)
//            }
//
////            val hotMakeOrRemakeArt = updatedHotFunctions[getMakeOrRemakeArtFunction().hotShitTag]
////            if (hotMakeOrRemakeArt != null) {
////                setMakeOrRemakeArtFunction(hotMakeOrRemakeArt)
////            }
//////                        if (hotMakeOrRemakeArt) {
//////                            makeOrRemakeArt = hotMakeOrRemakeArt
////            val oldStepDescriptions = art.stepDescriptions
////            getMakeOrRemakeArtFunction()()
////            art.stepDescriptions = oldStepDescriptions
////
////            dlog("Hot-refreshed makeOrRemakeArt")
////                        }
//
//
////            val hotInitUIFunctions = updatedHotFunctions[getInitUIFunctionsFunction().hotShitTag]
////            if (hotInitUIFunctions != null) {
////                setInitUIFunctionsFunction(hotInitUIFunctions)
////                getInitUIFunctionsFunction()(ui)
////                dlog("Hot-refreshed initUIFunctions")
////            }
//
////            makeUIShitIgniterDef.Impl = global.makeAPSShitImplCtor()
//
////            if (hotImpl != null) {
////                makeUIShitIgniterDef.Impl = hotImpl
////            }
////                        if (hotImpl) {
//            impl.stale = true // @ctx forgetmenot-1-4
////                            def.Impl = hotImpl
//            instantiateImpl()
//            Shitus.byid("css").replaceWith("<style id='css'>${impl.css()}</style>")
//            dlog("Hot-refreshed Impl")
////                        }
//
////            val hotInitDebugFunctions = updatedHotFunctions[initDebugFunctions.hotShitTag]
////            if (hotInitDebugFunctions != null) {
////                hotInitDebugFunctions()
////                dlog("Hot-refreshed initDebugFunctions")
////            }
//
////                        const hotRevealControl = updatedHotFunctions[revealer.revealControl.hotShitTag]
////                        if (hotRevealControl) {
////                            revealer.revealControl = hotRevealControl
////                            dlog("Hot-refreshed revealer.revealControl")
////                        }
//
//            // const shouldReloadPage = hotMakeOrRemakeExportedShit || hotInitUIFunctions || hotImpl || hotMakeOrRemakeArt || hotInitDebugFunctions || hotRevealControl
//            val shouldReloadPage = true
//
//            if (shouldReloadPage) {
//                dlog("----- Reloading page -----")
//                shittyFov(hrss.closeControlRevealer)
//
//                // debugPanes.deleteAll()
//
//                val prevDebugSimulateSlowNetwork = global.DEBUG_SIMULATE_SLOW_NETWORK
//                global.DEBUG_SIMULATE_SLOW_NETWORK = false
//                try {
////                    TestGlobal.controls = json()
//                    val scrollTop = js("\$(document)").scrollTop()
//                    __await<dynamic>(ui.loadPageForURL())
//                    js("\$(document)").scrollTop(scrollTop)
//                    __await<dynamic>(ui.pollLiveStatus())
////                                art.invokeStateContributions()
//                    invokeStateContributions(null)
//                } finally {
//                    global.DEBUG_SIMULATE_SLOW_NETWORK = prevDebugSimulateSlowNetwork
//                }
//
//                run {
//                    val things = Shitus.values(hrss.thingsToDoAfterHotUpdate)
//                    hrss.thingsToDoAfterHotUpdate = json()
//                    for (thing in jsArrayToList(things)) {
//                        __await<dynamic>(thing())
//                    }
//                }
//
//                if (hrss.reassertUIState) {
////                    dlog("Reasserting fucking UI state")
////                    __await<dynamic>(hrss.reassertUIState(json("scrollThere" to false, "thisIsReassertion" to true)))
//
//                    // WTF is this?
////                    noException: art.openTestPassedPane(openTestPassedPaneArgs)
//                }
//            }
//        }
//    )))
//}

