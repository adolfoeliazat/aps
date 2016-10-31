/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import aps.front.Color.*
import into.kommon.*
import kotlin.browser.window

var revealStackCalledTimes = 0

fun jsFacing_revealStack(arg: dynamic): Promise<Unit> {"__async"
    val exception: dynamic = arg.exception
    var stack: dynamic = arg.stack
    val stackBeforeAwait: dynamic = arg.stackBeforeAwait
    val fuckedStackBeforeAwait: dynamic = arg.fuckedStackBeforeAwait
    val `$definitionStack`: dynamic = arg.`$definitionStack`

    if (++revealStackCalledTimes > 3) {
        return console.warn("Too much of stack revealing") /ignora
    }

//        console.error("revealStack: " + exception.message + "\n\n" + exception.stack)
//        return

    Shitus.invariant(!(exception && stack), "I want either exception or stack")

    var message: dynamic = null
    var causeStack: dynamic = null
    var asyncStack: dynamic = null

    if (exception) {
        message = exception.message
//            if (!message) {
//                global.strangeException = exception
//
//                for (const value of values(exception)) {
//                    if (typeof value == "string") {
//                        message = value
//                        break
//                    }
//                }
//                if (!message) message = "<no message>"
//
//                if (typeof exception.getClass__jl_Class == "function") {
//                    message = exception.getClass__jl_Class().toString().replace(/^class /, "") + ": " + message
//                }
//            }
        stack = __await(errorToMappedClientStackString(exception, skipMessage=true))

        if (exception.cause) {
            causeStack = __await(errorToMappedClientStackString(exception.cause, skipMessage=true))
        }

        if (exception.asyncStack) {
            asyncStack = __await(errorToMappedClientStackString(exception.asyncStack, skipMessage=true))
        }
    }

//        if (relevantSourceLocation) {
//            console.error("Relevant source location: " + relevantSourceLocation)
//        }

    var errorToLog = "revealStack: " + message + "\n\n" + stack
    if (causeStack) {
        errorToLog += "\n\n----- Cause -----\n\n" + exception.cause.message + "\n" + causeStack
    }
    if (asyncStack) {
        errorToLog += "\n\n----- Async -----\n\n" + asyncStack
    }
    errorToLog += "\n\n----- Raw -----\n\n" + exception.stack
    console.error(errorToLog)


    if (DebugPanes.contains("revealStack")) {
        return console.warn("Some other stack is already revealed, so not showing") /ignora
    }

    DebugPanes.put("revealStack", oldShitAsReactElementable(Shitus.updatableElement(json(), ctor@{ update: dynamic ->
        val lineEls = jsArrayOf()

        val bottomLineStyle = json("borderBottom" to "1px solid ${GRAY_500}", "marginBottom" to 5, "paddingBottom" to 5, "marginRight" to 40)

        if (message) {
            lineEls.push(Shitus.diva(json("style" to bottomLineStyle), message))
        }

        if (exception && exception.`$render`) {
            lineEls.push(Shitus.diva(json("style" to bottomLineStyle), exception.`$render()`))
        }

        fun addStackItemElements(stack: dynamic) {
            if (stack == null) return
            for (line in jsArrayToList(stack.split("\n"))) {
                if (line.trim()) {
                    val m = line.match(global.RegExp("^(.*?)([^\\s():]+:\\d+:\\d+)(.*)\$"))
                    if (m) {
                        val prefix = m[1]
                        val suffix = m[3]
                        val link = OpenSourceCodeLink(json("where" to json("\$sourceLocation" to m[2])))
                        lineEls.push(Shitus.diva(json("style" to json("display" to "flex")), prefix.replace(global.RegExp("\\s", "g"), nbsp), link, suffix.replace(global.RegExp("\\s", "g"), nbsp)))
                    } else {
                        lineEls.push(Shitus.diva(json(), line.replace(global.RegExp("\\s", "g"), nbsp)))
                    }
                }
            }
        }

        fun sectionTitle(title: dynamic): dynamic {
            return Shitus.diva(json("style" to json("marginTop" to 5, "marginBottom" to 5, "backgroundColor" to GRAY_300.toString(), "fontWeight" to "bold")), title)
        }

        addStackItemElements(stack)

        if (stackBeforeAwait) {
            lineEls.push(sectionTitle("Stack before await"))
            addStackItemElements(stackBeforeAwait)
        }
        if (fuckedStackBeforeAwait) {
            lineEls.push(sectionTitle("Fucked stack before await"))
            addStackItemElements(fuckedStackBeforeAwait)
        }
        if (`$definitionStack`) {
            lineEls.push(sectionTitle("\$definitionStack"))
            addStackItemElements(`$definitionStack`.map{x: dynamic -> "    at " + x}.join("\n"))
        }


        return@ctor {
            Shitus.diva(json("noStateContributions" to true),
                Shitus.diva.apply(null, js("[]").concat(
                    json("style" to json(
                        "position" to "fixed",
                        "left" to 0, "bottom" to 0, "width" to "100%", "padding" to 10, "maxHeight" to 200, "overflow" to "auto", "zIndex" to Shitus.topZIndex++,
                        "borderTop" to "3px solid ${BLACK}", "background" to WHITE.toString())),

                    /*...*/lineEls,

                    Shitus.button(json("level" to "danger", "icon" to "close", "style" to json("position" to "absolute", "right" to 5, "top" to 5),
                        "onClick" to {
                            Shitus.hideStackRevelation()
                        }, "untested" to true))
                )))
        }
    })))

    return __asyncResult(Unit)
}

fun jsFacing_renderDefinitionStackStrip(arg: dynamic): dynamic {
    return "Implement renderDefinitionStackStrip, please, fuck you"

//    export function renderDefinitionStackStrip(def) {
//        #extract {stack, title} from def
//
//        return updatableElement(s{}, update => {
//            let loading = stack instanceof Promise
//                if (loading) {
//                    stack.then(res => {
//                        stack = res
//                        loading = false
//                        update()
//                    })
//                }
//
//            return _=> {
//            if (loading) return Shitus.spana({}, 'Loading definition stack...')
//            return Shitus.diva({controlTypeName: 'renderDefinitionStackStrip', style: {display: 'flex', flexWrap: 'wrap'}},
//                Shitus.diva({style: {fontWeight: 'bold', marginRight: 10}}, title || t('$definitionStack:')),
//                ...stack.map(stackItem =>
//            Shitus.diva({style: {marginRight: 10}}, OpenSourceCodeLink({stackItem}))))
//        }
//        })
//    }
}

var lastTestScenarioName: String? = null

fun jsFacing_isOrWasInTestScenario(): Boolean {
    return lastTestScenarioName != null
}

interface StorageLocal {
    fun clear()
    fun getItem(key: String): String?
    fun setItem(key: String, value: String)
}

class RealStorageLocal : StorageLocal {
    override fun clear() {
        window.localStorage.clear()
    }

    override fun getItem(key: String): String? {
        return window.localStorage.getItem(key)
    }

    override fun setItem(key: String, value: String) {
        window.localStorage.setItem(key, value)
    }
}

class Browser(val name: String) {
    var storageLocalItems = js("({})")
    var topNavbarElement: dynamic = null
    var rootElement: dynamic = null
    lateinit var ui: ShitPile
    var impl: dynamic = null

    val storageLocal = object : StorageLocal {
        override fun clear() {
            storageLocalItems = js("({})")
        }

        override fun getItem(key: dynamic): dynamic {
            return storageLocalItems[key]
        }

        override fun setItem(key: dynamic, value: dynamic) {
            storageLocalItems[key] = value
            global.localStorage.setItem(key, value) // TODO:vgrechka @kill
        }
    }
}

class HotReloadSurvivingShit {
    var debugFunctionsKeydownListener: dynamic = null
    var onUnhandledRejection: dynamic = null
    var preventExceptionRevelation: Boolean = false
    val controlBeingRevealed: dynamic = null
    val browsers = mutableMapOf<String, Browser>()
    lateinit var browser: Browser
    lateinit var lang: String
    var _t: dynamic = null
    var isHotReloading = false
    var debugCheckEmail_cachedEmails: dynamic = null
    var storageLocal: StorageLocal = RealStorageLocal()
    var preventScrollToBottomOnAssertionError = false
    var preventUIAssertionThrowing = false
    var alternativeTestSpeed: dynamic = null
    var urlQueryBeforeRunningTest: dynamic = null
    var hotCodeUpdateDisabled : dynamic = null
    var liveStatusPollingViaIntervalDisabled: dynamic = null
    var currentTestScenarioName: dynamic = null
    var preventRestoringURLAfterTest: dynamic = null
    var openTestPassedPaneArgs: dynamic = null
    var lastSeenTag: String? = null
    var artLastSuccessfulExpected: dynamic = null
    val closeControlRevealer: dynamic = {}
    var reassertUIState: dynamic = null
    var thingsToDoAfterHotUpdate: dynamic = json()
    var thingsToDoBeforeHotUpdate: dynamic = json()
    var hotCodeListener: dynamic = null
    var worldIsHalted: Boolean = false
}

val hrss: HotReloadSurvivingShit get() {
    if (global.hotReloadSurvivingShit == null) global.hotReloadSurvivingShit = HotReloadSurvivingShit()
    return global.hotReloadSurvivingShit
}
























