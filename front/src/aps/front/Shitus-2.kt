/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import aps.Color.*
import into.kommon.*
import kotlin.browser.window

var revealStackCalledTimes = 0

val Throwable.stack: String get() = this.asDynamic().stack

fun revealRawStack(stack: String) {
    DebugPanes.put("revealStack", oldShitAsToReactElementable(Shitus.updatableElement(json(), ctor@{ update: dynamic ->
        return@ctor {
            Shitus.diva(json("noStateContributions" to true, "style" to json(
                "position" to "fixed",
                "fontSize" to "85%",
                "left" to 0, "bottom" to 0, "width" to "100%", "padding" to 10, "maxHeight" to 200, "overflow" to "auto", "zIndex" to Shitus.topZIndex++,
                "borderTop" to "3px solid ${BLACK}", "background" to WHITE.toString())),
                        renderRawStackTrace(stack, marginRight=40).toReactElement(),


                        Shitus.button(json("level" to "danger", "icon" to "close", "style" to json("position" to "absolute", "right" to 5, "top" to 5),
                                           "onClick" to {
                                               Shitus.hideStackRevelation()
                                           }, "untested" to true))
            )
        }
    })))
}

fun revealStack(exception: Throwable, muteConsole: Boolean = false, skipAllForeignLines: Boolean = false): Promise<Unit> {"__async"
    if (++revealStackCalledTimes > 3) {
        return console.warn("Too much of stack revealing") /ignora
    }

    val stack = __await(errorToMappedClientStackString(exception))
    if (!muteConsole) {
        var errorToLog = "revealStack: " + stack
        errorToLog += "\n\n----- Raw -----\n\n" + exception.stack
        console.error(errorToLog)
    }

    if (DebugPanes.contains("revealStack")) {
        return console.warn("Some other stack is already revealed, so not showing") /ignora
    }

    DebugPanes.put("revealStack", oldShitAsToReactElementable(Shitus.updatableElement(json(), ctor@{ update: dynamic ->
        return@ctor {
            Shitus.diva(json("noStateContributions" to true, "style" to json(
                        "position" to "fixed",
                        "fontSize" to "85%",
                        "left" to 0, "bottom" to 0, "width" to "100%", "padding" to 10, "maxHeight" to 200, "overflow" to "auto", "zIndex" to Shitus.topZIndex++,
                        "borderTop" to "3px solid ${BLACK}", "background" to WHITE.toString())),
                renderStackTrace(exception, skipAllForeignLines=skipAllForeignLines, marginRight=40).toReactElement(),


                Shitus.button(json("level" to "danger", "icon" to "close", "style" to json("position" to "absolute", "right" to 5, "top" to 5),
                                   "onClick" to {
                                       Shitus.hideStackRevelation()
                                   }, "untested" to true))
                        )
        }
    })))

    return __asyncResult(Unit)
}

fun updatableShit(render: (update: () -> Unit) -> ToReactElementable) = object:Control2(Attrs()) {
    override fun render() = render({update()})
}

fun renderRawStackTrace(stack: String, marginRight: Int? = null): ToReactElementable {
    val ctx = RenderStackTraceContext(marginRight=marginRight, skipAllForeignLines=false)
    ctx.addStackTitle(stack)
    ctx.addStackItemElements(stack)
    return ctx.render()
}

fun renderStackTrace(e: Throwable, onRendered: (() -> Unit)? = null, skipAllForeignLines: Boolean = false, marginRight: Int? = null): ToReactElementable {
    val shit = Placeholder(kdiv{it-"Loading mapped stack..."})

    fun fuck(): Promise<Unit> = async {
        val ctx = RenderStackTraceContext(marginRight=marginRight, skipAllForeignLines=skipAllForeignLines)

        val stack = await(errorToMappedClientStackString(e))
        ctx.addStackTitle(stack)
        ctx.addStackItemElements(stack)

        if (e is FatException) {
            e.asyncStack?.let {
                await(stackToMappedClientStackString(it)).let {
                    ctx.addStackTitle(it, prefix="Async stack: ", topBorder=true)
                    ctx.addStackItemElements(it)
                }
            }

            e.markdownPayload?.let {
                ctx.addStackTitle("")
                ctx.lineEls += markdown(it)
            }

            e.visualPayload?.let {
                ctx.addStackTitle("")
                ctx.lineEls += it
            }
        }

        shit.setContent(ctx.render())
        onRendered?.invoke()
        Unit
    }

    fuck()
    return shit
}

class RenderStackTraceContext(val marginRight: Int?, val skipAllForeignLines: Boolean) {
    val noise = DebugNoise("RenderStackTraceContext", mute = false)
    val lineEls = mutableListOf<ToReactElementable>()

    fun render() = kdiv {it+ lineEls}

    fun addTitle(title: String, topBorder: Boolean = false) {
        val borderStyle = "1px solid $GRAY_500"
        var style = Style(borderBottom=borderStyle, marginBottom=5, paddingBottom=5, marginRight=marginRight)
        if (topBorder) style = style.copy(borderTop=borderStyle, marginTop=5, paddingTop=5)

        lineEls += kdiv(style){ o->
            o- title
        }
    }

    fun addStackTitle(stack: String, prefix: String = "", topBorder: Boolean = false) {
        addTitle(prefix + stack.lines().first(), topBorder=topBorder)
    }

    fun addStackItemElements(stack: String) {
        for (line in stack.lines().drop(1)) {
            if (skipAllForeignLines && (line.startsWith("? ") || line.startsWith("K ")))
                continue

            val m = Regex("^(.*?)([^\\s():]+:\\d+(?::\\d+)?)(.*)\$").matchEntire(line)
            if (m != null) {
                val prefix = m.groupValues[1]
                val suffix = m.groupValues[3]
                val link = OpenSourceCodeLink(json("where" to json("\$sourceLocation" to m.groupValues[2])))
                lineEls += kdiv(display="flex"){o->
                    o- ("" + prefix.replace(Regex("\\s"), nbsp))
                    o- link
                    o- ("" + suffix.replace(Regex("\\s"), nbsp))
                }
            } else {
                lineEls += kdiv {o->
                    o- ("" + line.replace(Regex("\\s"), nbsp))
                }
            }
        }
    }
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



class HotReloadSurvivingShit {
    var debugFunctionsKeydownListener: dynamic = null
    var onUnhandledRejection: dynamic = null
    var preventExceptionRevelation: Boolean = false
    val controlBeingRevealed: dynamic = null
    val browsers = mutableMapOf<String, BrowserOld>()
    lateinit var browserOld: BrowserOld
    lateinit var lang: String
    var _t: dynamic = null
    var isHotReloading = false
    var debugCheckEmail_cachedEmails: dynamic = null
//    var storageLocal: StorageLocal = RealStorageLocal()
    var preventScrollToBottomOnAssertionError = false
    var preventUIAssertionThrowing = false
    var alternativeTestSpeed: dynamic = null
    var urlQueryBeforeRunningTest: dynamic = null
    var hotCodeUpdateDisabled : dynamic = null
    var liveStatusPollingViaIntervalDisabled: dynamic = null
    var currentTestScenario: TestScenario? = null
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





















