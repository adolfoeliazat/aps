/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

fun newNativePromise(arg: dynamic): dynamic {
    return js("new Promise(arg)")
}


fun jsFacing_initHotCodeShit(impl: dynamic,
                             instantiateImpl: dynamic,
                             makeUIShitIgniterDef: dynamic,
                             ui: dynamic,
                             initDebugFunctions: dynamic,
                             hotFunctions: dynamic,
                             kokoko: dynamic,
                             getMakeOrRemakeExportedShitFunction: dynamic, setMakeOrRemakeExportedShitFunction: dynamic,
                             getMakeOrRemakeArtFunction: dynamic, setMakeOrRemakeArtFunction: dynamic,
                             getInitUIFunctionsFunction: dynamic, setInitUIFunctionsFunction: dynamic) {
    "__async"
    if (MODE != "debug") return

    if (jshit.hotCodeListener) {
        jshit.hotCodeListener.kill()
    }

    jshit.hotCodeListener = __await<dynamic>(jshit.runHotCodeUpdateStuff(json(
        "hotFunctions" to js("[]").concat(
            getMakeOrRemakeExportedShitFunction(),
            makeUIShitIgniterDef.Impl,
            initDebugFunctions,
            getInitUIFunctionsFunction(),
            getMakeOrRemakeArtFunction(),
        /*...*/hotFunctions.map({hof: dynamic -> hof.`fun`})),

        "applyHotCode" to {arg: dynamic -> "__async"
            val updatedHotFunctions = arg.updatedHotFunctions

            jshit.isHotReloading = true
            jshit.revealStack.calledTimes = 0

            run {
                val things = jshit.utils.values(jshit.thingsToDoBeforeHotUpdate)
                jshit.thingsToDoBeforeHotUpdate = {}
                for (thing in jsArrayToList(things)) {
                    __await<dynamic>(thing())
                }
            }

            for (hof in jsArrayToList(hotFunctions)) {
                val `fun` = updatedHotFunctions[hof.`fun`.hotShitTag]
                if (`fun` != null) {
                    `fun`()
                    dlog("Hot-refreshed ${`fun`.name}")
                }
            }

            val hotImpl = updatedHotFunctions[makeUIShitIgniterDef.Impl.hotShitTag]
            val hotMakeOrRemakeExportedShit = updatedHotFunctions[getMakeOrRemakeExportedShitFunction().hotShitTag]

            // const shouldMakeOrRemakeExportedShit = hotMakeOrRemakeExportedShit || hotImpl
            if (true) {
                if (hotMakeOrRemakeExportedShit) {
                    setMakeOrRemakeExportedShitFunction(hotMakeOrRemakeExportedShit)
                }
                getMakeOrRemakeExportedShitFunction()()
                dlog("Hot-refreshed makeOrRemakeExportedShit")
            }


            run { // Kotlin
                jshit.utils.invalidateKotlinStackSourceMapConsumer()


                __await<dynamic>(newNativePromise {resolve: dynamic, reject: dynamic ->
                    val script = global.document.createElement("script")
                    script.type = "text/javascript"
                    script.async = true
                    script.onload = {
                        resolve()
                    }
                    script.src = "kotlin/front-enhanced.js"
                    global.document.getElementsByTagName("head")[0].appendChild(script)
                })

                dlog("Hot-refreshed kotlin/front-enhanced.js")

                val hotIgnition = true
                kokoko(hotIgnition)
            }

            val hotMakeOrRemakeArt = updatedHotFunctions[getMakeOrRemakeArtFunction().hotShitTag]
            if (hotMakeOrRemakeArt != null) {
                setMakeOrRemakeArtFunction(hotMakeOrRemakeArt)
            }
//                        if (hotMakeOrRemakeArt) {
//                            makeOrRemakeArt = hotMakeOrRemakeArt
            val oldStepDescriptions = jshit.art.stepDescriptions
            getMakeOrRemakeArtFunction()()
            jshit.art.stepDescriptions = oldStepDescriptions

            dlog("Hot-refreshed makeOrRemakeArt")
//                        }


            val hotInitUIFunctions = updatedHotFunctions[getInitUIFunctionsFunction().hotShitTag]
            if (hotInitUIFunctions != null) {
                setInitUIFunctionsFunction(hotInitUIFunctions)
                getInitUIFunctionsFunction()(ui)
                dlog("Hot-refreshed initUIFunctions")
            }

            if (hotImpl != null) {
                makeUIShitIgniterDef.Impl = hotImpl
            }
//                        if (hotImpl) {
            impl.stale = true // @ctx forgetmenot-1-4
//                            def.Impl = hotImpl
            instantiateImpl()
            jshit.byid("css").replaceWith("<style id='css'>${impl.css()}</style>")
            dlog("Hot-refreshed Impl")
//                        }

            val hotInitDebugFunctions = updatedHotFunctions[initDebugFunctions.hotShitTag]
            if (hotInitDebugFunctions != null) {
                hotInitDebugFunctions()
                dlog("Hot-refreshed initDebugFunctions")
            }

//                        const hotRevealControl = updatedHotFunctions[revealer.revealControl.hotShitTag]
//                        if (hotRevealControl) {
//                            revealer.revealControl = hotRevealControl
//                            dlog("Hot-refreshed revealer.revealControl")
//                        }

            // const shouldReloadPage = hotMakeOrRemakeExportedShit || hotInitUIFunctions || hotImpl || hotMakeOrRemakeArt || hotInitDebugFunctions || hotRevealControl
            val shouldReloadPage = true

            if (shouldReloadPage) {
                dlog("----- Reloading page -----")
                jshit.utils.fov(jshit.closeControlRevealer)

                // debugPanes.deleteAll()

                val prevDebugSimulateSlowNetwork = global.DEBUG_SIMULATE_SLOW_NETWORK
                global.DEBUG_SIMULATE_SLOW_NETWORK = false
                try {
                    global.testGlobal.controls = json()
                    val scrollTop = js("\$(document)").scrollTop()
                    __await<dynamic>(ui.loadPageForURL())
                    js("\$(document)").scrollTop(scrollTop)
                    __await<dynamic>(ui.pollLiveStatus())
//                                art.invokeStateContributions()
                    invokeStateContributions(null)
                } finally {
                    global.DEBUG_SIMULATE_SLOW_NETWORK = prevDebugSimulateSlowNetwork
                }

                run {
                    val things = jshit.utils.values(jshit.thingsToDoAfterHotUpdate)
                    jshit.thingsToDoAfterHotUpdate = json()
                    for (thing in jsArrayToList(things)) {
                        __await<dynamic>(thing())
                    }
                }

                if (global.testGlobal.explicitMinimalGertrude == undefined) global.testGlobal.explicitMinimalGertrude = false
                global.testGlobal.minimalGertrude = global.testGlobal.explicitMinimalGertrude

                if (jshit.reassertUIState) {
                    dlog("Reasserting fucking UI state")
                    __await<dynamic>(jshit.reassertUIState(json("scrollThere" to false, "thisIsReassertion" to true)))

                    // WTF is this?
//                    noException: art.openTestPassedPane(openTestPassedPaneArgs)
                }
            }
        }
    )))
}

