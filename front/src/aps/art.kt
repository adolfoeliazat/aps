/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

import kotlin.browser.window
import aps.Color.*

object art {
    var testInstructions: dynamic = undefined

    fun run(spec: dynamic): Promise<Unit> {"__async"
        // #extract {instructions} from def
        val instructions = spec.instructions

        testInstructions = instructions

        val urlq = jshit.getURLQuery()
        // dlogs({urlq})
        var until = urlq.until
        if (until) until = global.parseInt(until, 10)
        val from = urlq.from || js("'start'")

        var skipping = from != js("'start'")

        val debugRPC = jshit.getDebugRPC()

        __await<dynamic>(debugRPC(json("fun" to "danger_clearSentEmails")))

        var stepIndex = 0
        for (instrIndex in 0 until instructions.length) {
            val instrdef = instructions[instrIndex]
            val opcode = global.Object.keys(instrdef).find({x: dynamic -> x[0] != "$"})
            if (!opcode) jshit.raiseWthMeta(json("message" to "Cannot determine opcode for instruction", "meta" to instrdef))

            if (instrIndex == until) {
                jshit.dlog("Stopping test before instruction ${instrIndex}")
                return __asyncResult(Unit)
            }

            val instr = instrdef[opcode]

            fun getControlForAction(arg: dynamic): dynamic {
                // {implementing}={}
                val implementing = if (arg) arg.implementing else undefined

                val control = global.testGlobal.controls[instr.shame]
                if (!control) jshit.raiseWithMeta(json("message" to "Control shamed ${instr.shame} is not found", "meta" to instrdef))
                if (implementing && !control[implementing]) jshit.raiseWithMeta(json("message" to "Control shamed ${instr.shame} is expected to implement ${implementing}", "meta" to instrdef))
                return control
            }

            if (opcode == "worldPoint") {
                val wpname = jshit.getCurrentTestScenarioName() + " -- " + instr.name
                if (skipping) {
                    if (instr.name == from) {
                        jshit.dlog("Restoring world point ${wpname}")
                        __await<dynamic>(debugRPC(json("db" to undefined, "fun" to "danger_restoreWorldPoint", "wpname" to wpname)))
                        skipping = false
                    }
                } else {
                    jshit.dlog("Saving world point ${wpname}")
                    __await(debugRPC(json("db" to undefined, "fun" to "danger_saveWorldPoint", "wpname" to wpname)))
                }
                continue
            }

            if (skipping) continue

            if (opcode == "do") {
                __await<dynamic>(instr.action())
                continue
            }
            if (opcode == "step") {
                instr.fulfilled = true
                continue
            }
            if (jsArrayOf("beginSection", "endSection").includes(opcode)) {
                continue
            }
            if (opcode == "assert") {
                __await<dynamic>(jshit.art.uiState(instr.asnn(jshit.pick(instrdef, "\$definitionStack"))))
                continue
            }
            if (opcode == "setValue") {
                val control = getControlForAction(json("implementing" to "testSetValue"))
                if (instr.timestamp) {
                    __await<dynamic>(debugRPC(json("fun" to "danger_imposeNextRequestTimestamp", "timestamp" to instr.timestamp)))
                }
                __await<dynamic>(control.testSetValue(json("value" to instr.value)))
                continue
            }
            if (opcode == "click") {
                val control = getControlForAction(json("implementing" to "testClick"))
                if (instr.timestamp) {
                    __await<dynamic>(debugRPC(json("fun" to "danger_imposeNextRequestTimestamp", "timestamp" to instr.timestamp)))
                }
                __await<dynamic>(control.testClick(instr))
                continue
            }
            if (opcode == "keyDown") {
                val control = getControlForAction(json("implementing" to "testKeyDown"))
                if (instr.timestamp) {
                    __await<dynamic>(debugRPC(json("fun" to "danger_imposeNextRequestTimestamp", "timestamp" to instr.timestamp)))
                }
                __await<dynamic>(control.testKeyDown(instr))
                continue
            }
            if (opcode == "actionPlaceholder") {
                // invariant(!art.actionPlaceholderTag, "Action placeholder tag is already set")
                jshit.art.actionPlaceholderTag = instr.`$tag`
                continue
            }

            jshit.raiseWithMeta(json("message" to "Unknown instruction opcode: ${opcode}", "meta" to instrdef))
        }

        if (skipping) {
            console.warn("WTF, I’ve just skipped all test steps")
        } else {
            jshit.dlog("Seems test is passed")
        }

        return __asyncResult(Unit)
    }

    fun renderStepDescriptions(): ReactElement {
        val testInstructions = art.testInstructions
        val els = mutableListOf<ReactElement>()

        var stepIndex = 0; var indent = 0
        for (instrIndex in 0 until testInstructions.length) {
            val instrdef = testInstructions[instrIndex]
            val opcode = dynamicKeys(instrdef).find { x: dynamic -> x[0] != "$" }
            val instr = instrdef[opcode]

            fun addLine(indent: Int, stepRowStyle: dynamic = null, rulerContent: dynamic = null, lineContent: ReactElement? = null, actions: Collection<ReactElement> = listOf()) {
                els.add(div { style { marginTop(5); display = "flex" }
                    -div { style { fontWeight = "bold"; width(40) }; - rulerContent }
                    // XXX This `width: 100%` is for fucking flexbox to not change `width: 40` above... http://stackoverflow.com/questions/7985021/css-flexbox-issue-why-is-the-width-of-my-flexchildren-affected-by-their-content
                    -div { className = "showOnParentHovered-parent"
                        style {
                            width = "100%"; display = "flex"
                            add(stepRowStyle)
                        }

                        + (1..indent).map { div { style { width(20); borderLeft = "2px dotted ${Color.GRAY_500}" } } }
                        - lineContent
                        -div { className = "showOnParentHovered"
                            - hor2 { style { marginLeft(8); paddingLeft(8); borderLeft = "2px solid ${Color.GRAY_500}" }
                                + actions
                                - jshit.OpenSourceCodeLink(json("where" to instrdef, "style" to json("marginLeft" to 20)))
                            } } } })
            }

            if (opcode == "step") {
                val title: Any? = instr.long
                val untilParamValue = if (instrIndex == jshit.art.stepDescriptions.length - 1) "infinity" else instrIndex

                val stepRowStyle = StyleBuilder()
                if (!instr.fulfilled) stepRowStyle {
                    opacity = 0.3
                }

                addLine(
                    indent, stepRowStyle = stepRowStyle,
                    rulerContent = "#" + (stepIndex++ + 1),

                    lineContent = div { style { display = "flex" }
                        - when (instr.kind) {
                            "action" -> span { style { marginRight(5); padding(3); backgroundColor = GREEN_100; fontSize = "75%" }; -"Action" }
                            "state" -> span { style { marginRight(5); padding(3); backgroundColor = LIGHT_BLUE_100; fontSize = "75%" }; -"State" }
                            "navigation" -> span { style { marginRight(5); padding(3); backgroundColor = BROWN_50; fontSize = "75%" }; -"Navigation" }
                            else -> raise("WTF is instr.kind")
                        }
                        - title
                    },

                    actions = listOf(
                        // TODO:vgrechka @duplication 4dfaa71f-4eaa-4ce9-992f-60f9587f69ae 1
                        jsLink(json("title" to "Run until " + untilParamValue, "onClick" to {
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
                addLine(indent, lineContent = div { style { fontWeight = "bold" }; - instr.long })
                ++indent
            }
            else if (opcode == "endSection") {
                --indent
            }
            else if (opcode == "worldPoint") {
                addLine(
                    indent,
                    lineContent = div { style { fontWeight = "normal"; fontStyle = "italic" }; - "World point: ${instr.name}" },
                    rulerContent = jdiva(json("style" to json("position" to "relative")),
                        jshit.ia(json("className" to "fa fa-circle", "style" to json("color" to jshit.GRAY_500))),
                        jdiva(json("style" to json("width" to 38, "position" to "absolute", "left" to 0, "top" to 9, "borderTop" to "2px dotted ${jshit.GRAY_500}")))
                    ),
                    // TODO:vgrechka @duplication 4dfaa71f-4eaa-4ce9-992f-60f9587f69ae 2
                    actions = listOf(
                        jsLink(json("title" to "Run from", "onClick" to {
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

//    return makeSwearBoxes().toReactElement()

        return jdiva(json("controlTypeName" to "renderStepDescriptions", "noStateContributions" to true), jdiva(json("style" to json("background" to jshit.GRAY_200, "fontWeight" to "bold")), "Steps"),
            *els.toTypedArray())
    }

}

fun jsFacing_invokeStateContributions(arg: dynamic) {
    // {actual}={}
    val actual = if (arg) arg.actual else undefined

    jshit.art.stateContributionsByControl = js("new Map()")

    for (contribute in jsArrayToIterable(jshit.values(jshit.art.uiStateContributions))) {
        contribute(json(
            "put" to {arg: dynamic ->
                // {$definitionStack, $callStack, control, key, value}
                val `$definitionStack` = arg.`$definitionStack`; val `$callStack` = arg.`$callStack`
                val control = arg.control; val key = arg.key; val value = arg.value

                jshit.invariant(control, "I want control for state.put()")
                if (jshit.keys(actual).includes(key)) {
                    val message = "uiStateContribution put duplication: key=${key}, value=${value}"

                    runni {"__async"
                        val thisDefinitionStackString = __await(anyControlDefinitionStackString(control, sep = "\n"))
                        val existingDefinitionStackString = __await(anyControlDefinitionStackString(actual[key].control, sep = "\n"))
                        console.error(
                            "$message\n\n" +
                            "This: $thisDefinitionStackString\n\n" +
                            "Existing: $existingDefinitionStackString")
                    }

                    jshit.raiseWithMeta(json(
                        "message" to message,
                        "metaItems" to jsArrayOf(
                            json("titlePrefix" to "This", "meta" to control),
                            json("titlePrefix" to "Existing", "meta" to actual[key].control))))
                }

                if (control) {
                    var contributions = jshit.art.stateContributionsByControl.get(control)
                    if (!contributions) {
                        contributions = js("({})")
                        jshit.art.stateContributionsByControl.set(control, contributions)
                    }
                    contributions[key] = value
                }

                if (actual) {
                    actual[key] = json(
                        "value" to value,
                        "control" to control,
                        "\$definitionStack" to `$definitionStack`,
                        "\$callStack" to `$callStack`)
                }
            }
        ))
    }
}

