/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

import kotlin.browser.window
import aps.Color.*
import kotlin.reflect.KProperty

class HotReloadSurvivingShit(val nameInGlobalScope: String) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): dynamic {
        return global[nameInGlobalScope]
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: dynamic) {
        global[nameInGlobalScope] = value
    }
}

interface ShamedTestInstruction {
    val shame: String
}

val NILS: String = js("new String('NILS')")

abstract class Foo
class Bar : Foo()
class Baz : Foo()

fun tetete() {
}

val String.there: Boolean get() = this !== NILS

abstract class TestInstruction(val opcode: String) : DefinitionStackHolder {

    override fun promiseDefinitionStack(): Promise<dynamic> {
        throw UnsupportedOperationException("Implement me, please, fuck you")
    }

    override fun toString(): String {
        return "<opcode=$opcode>"
    }

    class WorldPoint(val name: String) : TestInstruction("WorldPoint")
    class Do(val action: () -> Promise<Unit>) : TestInstruction("Do")
    class Step(val kind: String, val long: String) : TestInstruction("Step") { var fulfilled = false }
    class BeginSection(val long: String) : TestInstruction("BeginSection")
    class EndSection() : TestInstruction("EndSection")

    class AssertGenerated(val tag: String, val expected: String, val expectedExtender: ExpectationExtender?) : TestInstruction("AssertGenerated")

    class AssertFuck(val tag: String, val expected: Json) : TestInstruction("AssertFuck")

    abstract class Action(opcode: String, override val shame: String, val timestamp: String) : TestInstruction(opcode), ShamedTestInstruction {
    }

    class SetValue(shame: String, val value: String, timestamp: String = NILS) : Action("SetValue", shame, timestamp)
    class SetCheckbox(shame: String, val value: Boolean, timestamp: String = NILS) : Action("SetCheckbox", shame, timestamp)

    class Click(shame: String, timestamp: String = NILS) : Action("Click", shame, timestamp)
    class KeyDown(shame: String, val keyCode: Int, timestamp: String = NILS) : Action("KeyDown", shame, timestamp)
//    class ActionPlaceholder() : TestInstruction()
}


object art {
    var testInstructions: Iterable<TestInstruction> by HotReloadSurvivingShit("art_testInstructions")

    fun run(instructions: Iterable<TestInstruction>): Promise<Unit> {"__async"
        testInstructions = instructions

        val urlq = jshit.getURLQuery()
        // dlogs({urlq})
        var until = urlq.until
        if (until) until = global.parseInt(until, 10)
        val from: String = if (urlq.from) urlq.from else "start"

        var skipping = from != js("'start'")

        val debugRPC = jshit.getDebugRPC()



        var stepIndex = 0
        instructions.forEachIndexed {instrIndex, instr ->
            if (instrIndex == until) {
                jshit.dlog("Stopping test before instruction ${instrIndex}")
                return __asyncResult(Unit)
            }

            if (instr is TestInstruction.WorldPoint) {
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
            }
            else if (!skipping) {
                fun getControlForAction(arg: dynamic): dynamic {
                    if (instr !is ShamedTestInstruction) throw JSException("I want ShamedTestInstruction, got $instr")

                    // {implementing}={}
                    val implementing = if (arg) arg.implementing else undefined

                    val control = global.testGlobal.controls[instr.shame]
                    if (!control) jshit.raiseWithMeta(json("message" to "Control shamed ${instr.shame} is not found", "meta" to instr))
                    if (implementing && !control[implementing]) jshit.raiseWithMeta(json("message" to "Control shamed ${instr.shame} is expected to implement ${implementing}", "meta" to instr))
                    return control
                }

                fun executeSetValueLike(): Promise<Unit> {"__async"
                    val instr = instr as TestInstruction.Action
                    val control = getControlForAction(json("implementing" to "testSetValue"))
                    if (instr.timestamp.there) {
                        __await<dynamic>(debugRPC(json("fun" to "danger_imposeNextRequestTimestamp", "timestamp" to instr.timestamp)))
                    }

                    __await<dynamic>(control.testSetValue(json("value" to when (instr) {
                        is TestInstruction.SetValue -> instr.value
                        is TestInstruction.SetCheckbox -> instr.value
                        else -> wtf()
                    })))
                    return __asyncResult(Unit)
                }

                when (instr) {
                    is TestInstruction.BeginSection -> {}
                    is TestInstruction.EndSection -> {}
                    is TestInstruction.Do -> {
                        __await(instr.action())
                    }
                    is TestInstruction.Step -> {
                        instr.fulfilled = true
                    }
                    is TestInstruction.AssertGenerated -> {
                        __await<dynamic>(jshit.art.uiState(json(
                            "\$tag" to instr.tag,
                            "expected" to "---generated-shit---",
                            "expectedExtender" to instr.expectedExtender
                        )))
                    }
                    is TestInstruction.AssertFuck -> {
                        __await<dynamic>(jshit.art.uiState(json(
                            "\$tag" to instr.tag,
                            "expected" to instr.expected
                        )))
                    }
                    is TestInstruction.SetValue -> {
                        __await(executeSetValueLike())
                    }
                    is TestInstruction.SetCheckbox -> {
                        __await(executeSetValueLike())
                    }
                    is TestInstruction.Click -> {
                        val control = getControlForAction(json("implementing" to "testClick"))
                        if (instr.timestamp.there) {
                            __await<dynamic>(debugRPC(json("fun" to "danger_imposeNextRequestTimestamp", "timestamp" to instr.timestamp)))
                        }
                        __await<dynamic>(control.testClick(instr))
                    }
                    is TestInstruction.KeyDown -> {
                        val control = getControlForAction(json("implementing" to "testKeyDown"))
                        if (instr.timestamp.there) {
                            __await<dynamic>(debugRPC(json("fun" to "danger_imposeNextRequestTimestamp", "timestamp" to instr.timestamp)))
                        }
                        __await<dynamic>(control.testKeyDown(instr))
                    }
//                    if (opcode == "actionPlaceholder") {
//                        // invariant(!art.actionPlaceholderTag, "Action placeholder tag is already set")
//                        jshit.art.actionPlaceholderTag = instr.`$tag`
//                        continue
//                    }
                    else -> wtf("Test instruction: $instr")
                }
            }
        }

        if (skipping) {
            console.warn("WTF, I’ve just skipped all test steps")
        } else {
            jshit.dlog("Seems test is passed")
        }

        return __asyncResult(Unit)
    }

//    fun bak_run(spec: dynamic): Promise<Unit> {"__async"
//        // #extract {instructions} from def
//        val instructions = spec.instructions
//
//        testInstructions = instructions
//
//        val urlq = jshit.getURLQuery()
//        // dlogs({urlq})
//        var until = urlq.until
//        if (until) until = global.parseInt(until, 10)
//        val from = urlq.from || js("'start'")
//
//        var skipping = from != js("'start'")
//
//        val debugRPC = jshit.getDebugRPC()
//
//        __await<dynamic>(debugRPC(json("fun" to "danger_clearSentEmails")))
//
//        var stepIndex = 0
//        for (instrIndex in 0 until instructions.length) {
//            val instrdef = instructions[instrIndex]
//            val opcode = global.Object.keys(instrdef).find({x: dynamic -> x[0] != "$"})
//            if (!opcode) jshit.raiseWthMeta(json("message" to "Cannot determine opcode for instruction", "meta" to instrdef))
//
//            if (instrIndex == until) {
//                jshit.dlog("Stopping test before instruction ${instrIndex}")
//                return __asyncResult(Unit)
//            }
//
//            val instr = instrdef[opcode]
//
//            fun getControlForAction(arg: dynamic): dynamic {
//                // {implementing}={}
//                val implementing = if (arg) arg.implementing else undefined
//
//                val control = global.testGlobal.controls[instr.shame]
//                if (!control) jshit.raiseWithMeta(json("message" to "Control shamed ${instr.shame} is not found", "meta" to instrdef))
//                if (implementing && !control[implementing]) jshit.raiseWithMeta(json("message" to "Control shamed ${instr.shame} is expected to implement ${implementing}", "meta" to instrdef))
//                return control
//            }
//
//            if (opcode == "worldPoint") {
//                val wpname = jshit.getCurrentTestScenarioName() + " -- " + instr.name
//                if (skipping) {
//                    if (instr.name == from) {
//                        jshit.dlog("Restoring world point ${wpname}")
//                        __await<dynamic>(debugRPC(json("db" to undefined, "fun" to "danger_restoreWorldPoint", "wpname" to wpname)))
//                        skipping = false
//                    }
//                } else {
//                    jshit.dlog("Saving world point ${wpname}")
//                    __await(debugRPC(json("db" to undefined, "fun" to "danger_saveWorldPoint", "wpname" to wpname)))
//                }
//                continue
//            }
//
//            if (skipping) continue
//
//            if (opcode == "do") {
//                __await<dynamic>(instr.action())
//                continue
//            }
//            if (opcode == "step") {
//                instr.fulfilled = true
//                continue
//            }
//            if (jsArrayOf("beginSection", "endSection").includes(opcode)) {
//                continue
//            }
//            if (opcode == "assert") {
//                __await<dynamic>(jshit.art.uiState(instr.asnn(jshit.pick(instrdef, "\$definitionStack"))))
//                continue
//            }
//            if (opcode == "setValue") {
//                val control = getControlForAction(json("implementing" to "testSetValue"))
//                if (instr.timestamp) {
//                    __await<dynamic>(debugRPC(json("fun" to "danger_imposeNextRequestTimestamp", "timestamp" to instr.timestamp)))
//                }
//                __await<dynamic>(control.testSetValue(json("value" to instr.value)))
//                continue
//            }
//            if (opcode == "click") {
//                val control = getControlForAction(json("implementing" to "testClick"))
//                if (instr.timestamp) {
//                    __await<dynamic>(debugRPC(json("fun" to "danger_imposeNextRequestTimestamp", "timestamp" to instr.timestamp)))
//                }
//                __await<dynamic>(control.testClick(instr))
//                continue
//            }
//            if (opcode == "keyDown") {
//                val control = getControlForAction(json("implementing" to "testKeyDown"))
//                if (instr.timestamp) {
//                    __await<dynamic>(debugRPC(json("fun" to "danger_imposeNextRequestTimestamp", "timestamp" to instr.timestamp)))
//                }
//                __await<dynamic>(control.testKeyDown(instr))
//                continue
//            }
//            if (opcode == "actionPlaceholder") {
//                // invariant(!art.actionPlaceholderTag, "Action placeholder tag is already set")
//                jshit.art.actionPlaceholderTag = instr.`$tag`
//                continue
//            }
//
//            jshit.raiseWithMeta(json("message" to "Unknown instruction opcode: ${opcode}", "meta" to instrdef))
//        }
//
//        if (skipping) {
//            console.warn("WTF, I’ve just skipped all test steps")
//        } else {
//            jshit.dlog("Seems test is passed")
//        }
//
//        return __asyncResult(Unit)
//    }

//    val opcode = instrdef.opcode
//    val instr = instrdef

    fun renderStepDescriptions(): ReactElement {
        val testInstructions = art.testInstructions
        val els = mutableListOf<ReactElement>()

        var stepIndex = 0; var indent = 0
        testInstructions.forEachIndexed {instrIndex, instr ->
//            val instrdef = testInstructions[instrIndex]
//            val opcode = dynamicKeys(instrdef).find { x: dynamic -> x[0] != "$" }
//            val instr = instrdef[opcode]

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
                                - renderExpandableOnDemandStack(instr)
//                                - jshit.OpenSourceCodeLink(json("where" to instrdef, "style" to json("marginLeft" to 20)))
                            } } } })
            }

            if (instr is TestInstruction.Step) {
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
            else if (instr is TestInstruction.BeginSection) {
                addLine(indent, lineContent = div { style { fontWeight = "bold" }; - instr.long })
                ++indent
            }
            else if (instr is TestInstruction.EndSection) {
                --indent
            }
            else if (instr is TestInstruction.WorldPoint) {
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

