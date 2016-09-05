/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

import kotlin.browser.document
import kotlin.browser.window

@native interface IReactElement {
}

@native interface IKotlinShit {
    fun ignite(_global: dynamic, _jshit: dynamic)
}

var global: dynamic = null
var jshit: dynamic = null

object KotlinShit : IKotlinShit {
    override fun ignite(_global: dynamic, _jshit: dynamic) {
        println("----- Igniting front Kotlin shit -----")
        global = _global; jshit = _jshit
        jshit.art.openTestPassedPane = ::openTestPassedPane
        jshit.art.renderStepDescriptions = ::renderStepDescriptions
    }
}

fun link(@Suppress("UNUSED_PARAMETER") vararg args: dynamic): IReactElement {
    @Suppress("UNUSED_VARIABLE") val shit = jshit
    return js("shit.link.apply(null, args)")
}

private fun t(s: String): dynamic {
    return s
}

private fun keys(@Suppress("UNUSED_PARAMETER") obj: dynamic): dynamic {
    return js("Object.keys(obj)")
}

fun diva(@Suppress("UNUSED_PARAMETER") vararg args: dynamic) {
    @Suppress("UNUSED_VARIABLE") val shit = jshit
    return js("shit.diva.apply(null, args)")
}

fun hor2(@Suppress("UNUSED_PARAMETER") vararg args: dynamic) {
    @Suppress("UNUSED_VARIABLE") val shit = jshit
    return js("shit.hor2.apply(null, args)")
}

private fun fuckingDiv(): dynamic {
    return jshit.diva(json(), "Fucking Divius")
}

fun openTestPassedPane(def: dynamic) {
    val scenario = def.scenario

    val testPassedPane = jshit.statefulElement(json("ctor" to { update: dynamic ->
        var scenarioName = scenario.name
        val links = mutableListOf<IReactElement>()

        val m = global.RegExp("\\s+([0-9a-z]{8})-([0-9a-z]{4})-([0-9a-z]{4})-([0-9a-z]{4})-([0-9a-z]{12})$").exec(scenarioName)
        if (m != undefined) { // TODO
            scenarioName = scenarioName.slice(0, m.index)
            links.add(jshit.OpenSourceCodeLink(json("where" to json("\$tag" to m[0].trim()), "style" to json("color" to jshit.WHITE))))
        }
        if (jshit.art.actionPlaceholderTag != undefined) {
            links.add(jshit.marginateLeft(10, jshit.OpenSourceCodeLink(json("where" to json("\$tag" to jshit.art.actionPlaceholderTag), "style" to json("color" to jshit.WHITE)))))
        }

        val uq = jshit.getURLQueryBeforeRunningTest()
        if (!uq.scrollToBottom || uq.scrollToBottom == "yes" || uq.scrollToBottom == "success") {
            window.requestAnimationFrame { document.body?.scrollTop = 99999 }
        }

        json(
            "render" to {
                when {
                    scenarioName == undefined -> null

                    else -> jshit.diva(json(
                        "noStateContributions" to true,
                        "style" to json(
                            "backgroundColor" to jshit.GREEN_700, "color" to jshit.WHITE,
                            "marginTop" to 10, "padding" to "10px 10px", "textAlign" to "center", "fontWeight" to "bold")),

                        jshit.diva(json("style" to json("paddingBottom" to 10)),
                            scenarioName,
                            diva(json("style" to json("display" to "flex", "justifyContent" to "center")), *links.toTypedArray())
                        ),

                        jshit.diva(json("style" to json("background" to jshit.WHITE, "color" to jshit.BLACK_BOOT, "fontWeight" to "normal", "textAlign" to "left", "padding" to 5)),
                            jshit.art.renderStepDescriptions())
                    )
                }
            })
    }))

    jshit.debugPanes.set(json(
        "name" to "openTestPassedPane",
        "parentJqel" to jshit.byid("underFooter"),
        "element" to jshit.spana(json(), testPassedPane.element)))
}

fun renderStepDescriptions() {
    val testInstructions = jshit.art.getTestInstructions()
    val els = mutableListOf<IReactElement>()

    var stepIndex = 0; var indent = 0
    for (instrIndex in 0 until testInstructions.length) {
        val instrdef = testInstructions[instrIndex]
        val opcode = keys(instrdef).find {x: dynamic -> x[0] != "$" } // TODO
        val instr = instrdef[opcode]

        fun addLine(stepRowStyle: dynamic = null, rulerContent: dynamic = null, lineContent: dynamic = null, actions: Collection<IReactElement> = listOf(), indent: dynamic = null) {
            els.add(jshit.diva(json("style" to json("marginTop" to 5, "display" to "flex")),
                diva(json("style" to json("fontWeight" to "bold", "width" to 40)), rulerContent),
                // XXX This `width: 100%` is for fucking flexbox to not change `width: 40` above... http://stackoverflow.com/questions/7985021/css-flexbox-issue-why-is-the-width-of-my-flexchildren-affected-by-their-content
                diva(json("className" to "showOnParentHovered-parent", "style" to json("width" to "100%", "display" to "flex").asDynamic().asnn(stepRowStyle)),
                    *(jshit.range(indent).map { diva(json("style" to json("width" to 20, "borderLeft" to "2px dotted ${jshit.GRAY_500}"))) }),
                lineContent,
                diva(json("className" to "showOnParentHovered"),
                    hor2(json("style" to json("marginLeft" to 8, "paddingLeft" to 8, "borderLeft" to "2px solid ${jshit.GRAY_500}")),
                        *actions.toTypedArray(),
                    jshit.OpenSourceCodeLink(json("where" to instrdef, "style" to json("marginLeft" to 20))))))
            ))
        }

        if (opcode == "step") {
            val stepRowStyle = js("({})")
            if (!instr.fulfilled) {
                stepRowStyle.opacity = 0.3
            }

            val untilParamValue = if (instrIndex == jshit.art.stepDescriptions.length - 1) "infinity" else instrIndex

            addLine(
                indent = indent, stepRowStyle = stepRowStyle,
                rulerContent = "#" + (stepIndex++ + 1),
                lineContent = diva(json("style" to json("display" to "flex")),
                    when (instr.kind) {
                        "action" -> jshit.spana(json("style" to json("marginRight" to 5, "padding" to 3, "background" to jshit.GREEN_100, "fontSize" to "75%")), t("Action"))
                        "state" -> jshit.spana(json("style" to json("marginRight" to 5, "padding" to 3, "background" to jshit.LIGHT_BLUE_100, "fontSize" to "75%")), t("State"))
                        "navigation" -> jshit.spana(json("style" to json("marginRight" to 5, "padding" to 3, "background" to jshit.BROWN_50, "fontSize" to "75%")), t("Navigation"))
                        else -> "WTF is instr.kind"
                    },
                    jshit.spana(json("style" to json()), instr.long)
                ),
                actions = listOf(
                    // TODO:vgrechka @duplication 4dfaa71f-4eaa-4ce9-992f-60f9587f69ae
                    link(json("title" to t("Run until ") + untilParamValue, "onClick" to {
                        var href = window.location.href
                        href = href.replace(Regex("&from[^&]*"), "")
                        href = href.replace(Regex("&until[^&]*"), "")
                        href += "&until=" + untilParamValue
                        window.location.href = href
                    }))
                )
            )
        }
        else if (opcode == "beginSection") {
            addLine(indent = indent, lineContent = diva(json("style" to json("fontWeight" to "bold")), instr.long))
            ++indent
        }
        else if (opcode == "endSection") {
            --indent
        }
        else if (opcode == "worldPoint") {
            addLine(
                indent = indent,
                lineContent = diva(json("style" to json("fontWeight" to "normal", "fontStyle" to "italic")), "World point: " + instr.name),
                rulerContent = diva(json("style" to json("position" to "relative")),
                    jshit.ia(json("className" to "fa fa-circle", "style" to json("color" to jshit.GRAY_500))),
                    diva(json("style" to json("width" to 38, "position" to "absolute", "left" to 0, "top" to 9, "borderTop" to "2px dotted ${jshit.GRAY_500}")))
                ),
                // TODO:vgrechka @duplication 4dfaa71f-4eaa-4ce9-992f-60f9587f69ae
                actions = listOf(
                    link(json("title" to t("Run from"), "onClick" to {
                        var href = window.location.href
                        href = href.replace(Regex("&from[^&]*"), "")
                        href = href.replace(Regex("&until[^&]*"), "")
                        href += "&from=" + instr.name
                        window.location.href = href
                    }))
                )
            )
        }
    }

    return diva(json("controlTypeName" to "renderStepDescriptions", "noStateContributions" to true), diva(json("style" to json("background" to jshit.GRAY_200, "fontWeight" to "bold")), t("Steps")),
        *els.toTypedArray())
}


