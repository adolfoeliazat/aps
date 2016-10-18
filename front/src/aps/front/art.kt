/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import kotlin.browser.window
import aps.front.Color.*
import aps.*
import aps.WorldPointRequest.Action.RESTORE
import aps.WorldPointRequest.Action.SAVE
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

    class AssertGenerated(val tag: String, val expected: String, val expectedExtender: ((dynamic) -> Unit)?) : TestInstruction("AssertGenerated")

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
    var actionPlaceholderTag: String? = null
    var testInstructions: Iterable<TestInstruction> by HotReloadSurvivingShit("art_testInstructions")

    fun uiState(def: dynamic): Promise<Unit> {"__async"
        val url: String = global.location.href // Capture location for reassertion on hot reload, as it will be changed to `/test.html...`

        jshit.reassertUIState = {arg: dynamic -> "__async"
            val scrollThere: dynamic = arg.scrollThere
            __await<dynamic>(jshit.debugCheckEmail())

//            val actual = json("url" to url)
            val actual = mutableMapOf<String, Any>("url" to url)
            invokeStateContributions(actual)
            gertrude(global.Object.assign(def, json("actual" to actual, "scrollThere" to scrollThere)))
        }

        __await<dynamic>(jshit.reassertUIState(json("scrollThere" to true)))
        return __asyncResult(Unit)
    }


    fun run(instructions: Iterable<TestInstruction>): Promise<Unit> {"__async"
        testInstructions = instructions

        val urlq = jshit.getURLQuery()
        // dlogs({urlq})
        var until = urlq.until
        if (until) until = global.parseInt(until, 10)
        val from: String = if (urlq.from) urlq.from else "start"

        var skipping = from != js("'start'")

//        val debugRPC = jshit.getDebugRPC()



        var stepIndex = 0
        instructions.forEachIndexed {instrIndex, instr ->
            if (instrIndex == until) {
                dlog("Stopping test before instruction ${instrIndex}")
                return __asyncResult(Unit)
            }

            if (instr is TestInstruction.WorldPoint) {
                val wpname: String = jshit.getCurrentTestScenarioName() + " -- " + instr.name
                if (skipping) {
                    if (instr.name == from) {
                        dlog("Restoring world point ${wpname}")
//                        __await<dynamic>(debugRPC(json("db" to undefined, "fun" to "danger_restoreWorldPoint", "wpname" to wpname)))
                        __await(WorldPointRequest.send(wpname, RESTORE))
                        skipping = false
                    }
                } else {
                    dlog("Saving world point ${wpname}")
//                    __await(debugRPC(json("db" to undefined, "fun" to "danger_saveWorldPoint", "wpname" to wpname)))
                    __await(WorldPointRequest.send(wpname, SAVE))
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
//                        __await<dynamic>(debugRPC(json("fun" to "danger_imposeNextRequestTimestamp", "timestamp" to instr.timestamp)))
                        __await(ImposeNextRequestTimestampRequest.send(instr.timestamp))
//                        __await(ImposeNextRequestTimestampRequest(instr.timestamp).rpc())
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
                        __await(art.uiState(json(
                            "\$tag" to instr.tag,
                            "expected" to "---generated-shit---",
                            "expectedExtender" to instr.expectedExtender
                        )))
                    }
                    is TestInstruction.AssertFuck -> {
                        __await(art.uiState(json(
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
//                            __await<dynamic>(debugRPC(json("fun" to "danger_imposeNextRequestTimestamp", "timestamp" to instr.timestamp)))
                            __await(ImposeNextRequestTimestampRequest.send(instr.timestamp))
                        }
                        __await<dynamic>(control.testClick(instr))
                    }
                    is TestInstruction.KeyDown -> {
                        val control = getControlForAction(json("implementing" to "testKeyDown"))
                        if (instr.timestamp.there) {
//                            __await<dynamic>(debugRPC(json("fun" to "danger_imposeNextRequestTimestamp", "timestamp" to instr.timestamp)))
                            __await(ImposeNextRequestTimestampRequest.send(instr.timestamp))
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
            dlog("Seems test is passed")
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
//                jshit.utils.dlog("Stopping test before instruction ${instrIndex}")
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
//                        jshit.utils.dlog("Restoring world point ${wpname}")
//                        __await<dynamic>(debugRPC(json("db" to undefined, "fun" to "danger_restoreWorldPoint", "wpname" to wpname)))
//                        skipping = false
//                    }
//                } else {
//                    jshit.utils.dlog("Saving world point ${wpname}")
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
//                __await<dynamic>(jshit.art.uiState(instr.asnn(jshit.utils.pick(instrdef, "\$definitionStack"))))
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
//            jshit.utils.dlog("Seems test is passed")
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
                            else -> Shitus.raise("WTF is instr.kind")
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
                        Shitus.ia(json("className" to "fa fa-circle", "style" to json("color" to Color.GRAY_500))),
                        jdiva(json("style" to json("width" to 38, "position" to "absolute", "left" to 0, "top" to 9, "borderTop" to "2px dotted ${Color.GRAY_500}")))
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

        return jdiva(json("controlTypeName" to "renderStepDescriptions", "noStateContributions" to true), jdiva(json("style" to json("background" to Color.GRAY_200, "fontWeight" to "bold")), "Steps"),
            *els.toTypedArray())
    }

    fun initArtShit() {
        jshit.art = js("({uiStateContributions: {}, stepDescriptions: []})")
//        jshit.art = js("({uiStateContributions: {}, stateContributionsByControl: new Map(), stepDescriptions: []})")
    }

}

fun gertrude(def: dynamic) {
    // {descr='Describe me', $tag, $definitionStack, actual, expected, expectedExtender, scrollThere=true, thisIsReassertion}

    val tag: String = def.`$tag`; val definitionStack = def.`$definitionStack`;
    var expected = def.expected; val expectedExtender = def.expectedExtender
    val thisIsReassertion: Boolean = def.thisIsReassertion
    val descr: String = if (def.descr == undefined) "Describe me" else def.descr
    val scrollThere: Boolean = if (def.scrollThere == undefined) true else def.scrollThere

    val kactual: MutableMap<String, Any> = def.actual
    val actual: dynamic = js("({})")
    for ((k, v) in kactual) actual[k] = v

    jshit.setLastSeenTag(tag)
    if (expected == undefined) {
        Shitus.raise("WTF")
    }
    if (expected == "---generated-shit---") {
        expected = global.GENERATED_SHIT[tag]
        if (expected == undefined) expected = js("({})")
    }

    var extenderKeys = js("[]")
    if (expectedExtender) {
        expectedExtender(json("expected" to expected))

        val fromExtender = js("({})")
        expectedExtender(json("expected" to fromExtender))
        extenderKeys = global.Object.keys(fromExtender)

        for (k in jsArrayToList(extenderKeys)) {
            val komega = k.replace(js("/-i\\d\\d\\d/g"), "-ω")
            if (!extenderKeys.includes(komega)) {
                extenderKeys.push(komega)
            }
        }
    }

    val keyToDefinitionStack = js("({})"); val keyToCallStack = js("({})"); val keyToControl = js("({})")
    for (key: String in jsArrayToList(global.Object.keys(actual))) {
        val value = actual[key]
        if (value && value.`$definitionStack`) {
            actual[key] = value.value
            keyToDefinitionStack[key] = value.`$definitionStack`
        }
        if (value && value.`$callStack`) {
            actual[key] = value.value
            keyToCallStack[key] = value.`$callStack`
        }
        if (value && value.control) {
            actual[key] = value.value
            keyToControl[key] = value.control
        }
    }

    if (global.deepEql(actual, expected)) {
        if (thisIsReassertion) {
            console.warn("// TODO:vgrechka If reassertion after hot reload passed, make it clear in UI    e5d48597-62d4-4740-b915-04f6934c2bc0 ")
        }
        jshit.setArtLastSuccessfulExpected(expected)
        return
    }

    var detailsUI: dynamic = null
    if (jshit.getURLQueryBeforeRunningTest().minimalGertrude == "yes" || global.testGlobal.minimalGertrude) {
        detailsUI = div { styleKludge = json("background" to Color.WHITE); -"I am minimal because of minimalGertrude" }
//        detailsUI = Shitus.diva(json("style" to json("background" to jshit.WHITE)), t("I am minimal because of minimalGertrude"))
    } else {
        detailsUI = Shitus.updatableElement(js("({})"), wholeShitCtor@ {updateWholeShit ->
            var stripFuckingIndices = true; var hideFuckingKeyRepetitions = false; var tabs: dynamic = null
            var paneControls: dynamic = null; var lineHideFilter = Shitus.noop
            var highlightedKeys = js("({})"); var pileOfShit = js("({})")
            val progressPlaceholder = jshit.Placeholder()
            var unifyIndicesCheck: dynamic = null; var hideKeyRepetitionsCheck: dynamic = null

            fun makeFuckingTabs() {
                fun repr(it: dynamic, opts: dynamic = js("({})")): dynamic {
                    // {stripIndices}={}
                    val stripIndices: Boolean = opts.stripIndices

                    var s = global.nodeUtil.inspect(it, json("depth" to null))
                    // s = s.replace(/\\n/g, "\n")

                    if (stripIndices) {
                        s = s.replace(js("/-i\\d\\d\\d/g"), "-ω") // omega
                    }

                    if (s[0] == "{") s = s[0] + "\n " + s.slice(1)
                    if (s[s.length - 1] == "}") s = s.slice(0, s.length - 1) + "\n" + s[s.length - 1]

                    s = s
                        .split("\n")
                        .map({x: dynamic ->
                            var res = x
                            res = res.trim()
                            if (res == "{" || res == "}" || Shitus.isBlank(res)) return@map res
                            if (!res.endsWith(",")) res += ","
                            return@map res
                        })
                    .join("\n")

                    return s
                }

                fun isExtValueLine(valueLine: dynamic): Boolean {
                    if (valueLine == "{" || valueLine == "}") return false
                    if (Shitus.isBlank(valueLine)) return false

                    val colonIndex = valueLine.indexOf(":")
                    Shitus.invariant(colonIndex != -1, "Expecting colon: ${valueLine}")
                    val key = Shitus.trim(valueLine.slice(0, colonIndex).replace(js("/'/g"), ""))
                    if (extenderKeys.includes(key)) {
                        return true
                    }
                    return false
                }


                val pileOfShit = js("({})")

                Shitus.sortKeys(actual) // Order of keys sent over the wire is mangled
                Shitus.sortKeys(expected)

                val definitionStacks = js("[]"); val callStacks = js("[]"); val controls = js("[]"); val origKeys = js("[]")
                for (key in jsArrayToList(global.Object.keys(actual))) {
                    definitionStacks.push(keyToDefinitionStack[key])
                    callStacks.push(keyToCallStack[key])
                    controls.push(keyToControl[key])
                    origKeys.push(key)
                }

                val actualString = repr(actual, json("stripIndices" to stripFuckingIndices))
                val actualStringOrig = repr(actual, json("stripIndices" to false))
                val expectedString = repr(expected, json("stripIndices" to stripFuckingIndices))
                val lastExpectedString = if(jshit.getArtLastSuccessfulExpected()) repr(jshit.getArtLastSuccessfulExpected(), json("stripIndices" to stripFuckingIndices)) else "--- Nothing ---"
                val diffDivs = js("[]"); val diffLastDivs = js("[]")

                var lineIndex = 0; var actualLineIndex = 0

                fun fillDiffDivs(arg: dynamic) {
                    // {divs, string1, string2, greenTitle}
                    val divs = arg.divs; val string1 = arg.string1; val string2 = arg.string2; var greenTitle = arg.greenTitle

                    if (!greenTitle) greenTitle = "Expected"
                    var prevLabel: dynamic = undefined
                    val diffLineItems = global.JsDiff.diffLines(string1, string2)
                    for (item in jsArrayToList(diffLineItems)) {
                        var backgroundColor: dynamic = undefined
                        var label: dynamic = undefined
                        if (item.added) {
                            backgroundColor = Color.RED_100.string
                            label = "Actual"
                        } else if (item.removed) {
                            backgroundColor = Color.GREEN_100.string
                            label = greenTitle
                        } else {
                            backgroundColor = Color.WHITE.string
                            label = undefined
                        }
                        if (label && label != prevLabel) {
                            divs.push(div {
                                styleKludge = json("backgroundColor" to backgroundColor, "fontWeight" to "bold")
                                label
                            })
                        }
                        prevLabel = label

                        item.value = item.value.replace(js("/\\n*\$/"), "")
                        val valueLines = item.value.split("\n")
                        val keysAdded = js("({})")
                        for (valueLine in jsArrayToList(valueLines)) {
                            var shouldBeHiddenCuaseItsFuckingKeyRepetition: dynamic = undefined
                            var shouldBeHighlighted: dynamic = undefined

                            val lineDivID = puid()
                            var gotoIcon: dynamic = undefined; var metaBox: dynamic = undefined
                            if (valueLine != "{" && valueLine != "}" && !item.removed) {
                                val definitionStack = definitionStacks[actualLineIndex] || js("[]")
                                var callStack = callStacks[actualLineIndex] || js("[]")
                                if (definitionStack || callStack) {
                                    var open = false
                                    metaBox = Shitus.updatableElement(js("({})"), { update: dynamic ->
                                        {
                                            div {
                                                styleKludge = json("display" to "inline-block", "verticalAlign" to "top", "marginLeft" to 10)
                                                -span {
                                                    className = "fa fa-caret-${if (open) "up" else "down"}"; styleKludge = json("cursor" to "pointer")
                                                    onClick {
                                                        open = !open
                                                        update()
                                                    }
                                                }

                                                if (open)
                                                    -div {
                                                        styleKludge = json("display" to "inline-block", "verticalAlign" to "top", "marginLeft" to 10)
                                                        -jshit.renderStacks(json("definitionStack" to definitionStack, "callStack" to callStack))
                                                    }
                                            }
                                        }
                                    })
                                }

                                val control = controls[actualLineIndex]
                                if (control) {
                                    gotoIcon = div {
                                        className = "showOnParentHovered"
                                        -span {
                                            className = "fa fa-search"; styleKludge = json("cursor" to "pointer", "marginLeft" to 10)
                                            onClick {
                                                jshit.revealControl(control, json("scrollToTarget" to true))
                                            }
                                        }
                                    }
                                }

                                val colonIndex = valueLine.indexOf(":")
                                if (colonIndex != -1) {
                                    var key = valueLine.slice(0, colonIndex).trim()
                                    if (key.startsWith("\"") && key.endsWith("'")) {
                                        key = key.slice(1, key.length - 1)
                                    }
                                    shouldBeHiddenCuaseItsFuckingKeyRepetition = hideFuckingKeyRepetitions && keysAdded[key]
                                    keysAdded[key] = true

                                    val origKey = origKeys[actualLineIndex]
                                    shouldBeHighlighted = highlightedKeys[origKey]
                                    pileOfShit["scrollToDivForKey-${origKey}"] = {
                                        global.requestAnimationFrame { jshit.utils.jQuery(kotlin.browser.document).scrollTop(jshit.byid(lineDivID).offset().top - 50 - 20) }
                                    }
                                } else {
                                    console.warn("WTF colonIndex is -1")
                                }

                                ++actualLineIndex
                            }

                            divs.push({
                                if (!lineHideFilter(valueLine) && !shouldBeHiddenCuaseItsFuckingKeyRepetition)
                                    Shitus.updatableElement(js("({})"), { update: dynamic ->
                                        val style = json("backgroundColor" to backgroundColor, "position" to "relative")
                                        if (shouldBeHighlighted) {
                                            global.Object.assign(style, json(
                                                "borderLeft" to "5px solid black",
                                                "paddingLeft" to 5,
                                                "background" to Color.ORANGE_100))
                                        }

                                        return@updatableElement {
                                            div {
                                                id = "" + lineDivID
                                                className = "showOnParentHovered-parent"
                                                styleKludge = style

                                                if (isExtValueLine(valueLine))
                                                    -span {
                                                        styleKludge = json("marginRight" to 5, "padding" to 3, "background" to Color.ORANGE_200, "fontSize" to "75%")
                                                        -"ext"
                                                    }
                                                -div {
                                                    styleKludge = json("display" to "inline-block", "verticalAlign" to "top")
                                                    -valueLine
                                                }

                                                -metaBox
                                                -gotoIcon
                                            }
                                        }
                                    })
                                else undefined
                            })

                            ++lineIndex
                        }
                    }
                }

                fillDiffDivs(json("divs" to diffDivs, "string1" to expectedString, "string2" to actualString))
                lineIndex = 0; actualLineIndex = 0
                fillDiffDivs(json("divs" to diffLastDivs, "string1" to lastExpectedString, "string2" to actualString, "greenTitle" to "Last"))

                val my = js("({})")

                var actualStringForPasting = actualStringOrig.trim()
                if (actualStringForPasting[0] == "{" || actualStringForPasting[0] == "[") {
                    actualStringForPasting = Shitus.trimStart(actualStringForPasting.slice(1, actualStringForPasting.length - 1))
                }
                val chars = actualStringForPasting.split("")

                val slash = "\\"
                for (i in 0 until chars.length) {
                    if (chars[i] == "'" && (i == 0 || chars[i - 1] != slash)) {
                        chars[i] = "`"
                    }
                }
                actualStringForPasting = chars.join("")
                actualStringForPasting = actualStringForPasting.replace(js("/`((\\w|\\d\\|_|-|\\$|\\.|:)*?)`: /g"), "'$1': ")
                var replacements = js("[]")
                var backtickIndex: dynamic = undefined; var from = 0; var btis = js("[]")
                while (true) {
                    backtickIndex = actualStringForPasting.indexOf("`", from)
                    if (backtickIndex == -1) break
                    btis.push(backtickIndex)
                    if (btis.length == 2) {
                        var literal = actualStringForPasting.slice(btis[0], btis[1] + 1)
                        if (js("/\\r|\\n/").test(literal)) {
                            literal = literal[0] + "\n" + literal.slice(1)
                            literal = literal.replace(js("/(\\r?\\n)/g"), "$1        ")
                            literal = "dedent(${literal})"
                            replacements.push(json("from" to btis[0], "oldStringLength" to btis[1] - btis[0] + 1, "newString" to literal))
                        }
                        btis = js("[]")
                    }
                    from = backtickIndex + 1
                }
                replacements = Shitus.sortBy(replacements, "from")
                var newActualStringForPasting = ""; from = 0
                for (replacement in jsArrayToList(replacements)) {
                    newActualStringForPasting += actualStringForPasting.slice(from, replacement.from) + replacement.newString
                    from = replacement.from + replacement.oldStringLength
                }
                newActualStringForPasting += actualStringForPasting.slice(from)
                actualStringForPasting = newActualStringForPasting
                actualStringForPasting = actualStringForPasting + "\n"

                val actualStringForPastingPlusExt = actualStringForPasting

                actualStringForPasting = actualStringForPasting
                        .split("\n")
                        .filter({line -> !isExtValueLine(line)})
                        .join("\n")

                tabs = jshit.Tabs(json(
                    "activeTab" to if (Shitus.isEmpty(expected)) "diffLast" else "diff",
                    "tabs" to json(
                        "diff" to json(
                            "title" to "Diff",
                            "content" to run {
                                var args = js("[]")
                                args.push(json("style" to json("whiteSpace" to "pre-wrap")))
                                args = args.concat(diffDivs)
                                Shitus.diva.apply(null, args)
                            }),
                        "diffLast" to json(
                            "title" to "Diff Last",
                            "content" to run {
                                var args = js("[]")
                                args.push(json("style" to json("whiteSpace" to "pre-wrap")))
                                args = args.concat(diffLastDivs)
                                Shitus.diva.apply(null, args)
                            }),
                            "actual" to json(
                                "title" to "Actual",
                                "content" to Shitus.diva(json("style" to json("whiteSpace" to "pre-wrap")), actualString)),
                            "expected" to json(
                                "title" to "Expected",
                                "content" to Shitus.diva(json("style" to json("whiteSpace" to "pre-wrap")), expectedString)),
                            "actualPaste" to json(
                                "title" to "Actual Paste",
                                "content" to Shitus.diva(json("style" to json("whiteSpace" to "pre-wrap")),
                                    Shitus.Input(json("initialValue" to actualStringForPasting, "kind" to "textarea", "rows" to 10, "style" to json("width" to "100%", "height" to "100%"), "untested" to true)))),
                            "actualPasteWithExt" to json(
                                "title" to "Actual Paste + ext",
                                "content" to Shitus.diva(json("style" to json("whiteSpace" to "pre-wrap")),
                                    Shitus.Input(json("initialValue" to actualStringForPastingPlusExt, "kind" to "textarea", "rows" to 10, "style" to json("width" to "100%", "height" to "100%"), "untested" to true))))
                    )
                ))

                paneControls = div {
                    -hor2 {
                        -hor1 { -unifyIndicesCheck; -"Unify indices" }
                        -hor1 { -hideKeyRepetitionsCheck; -"Hide key repetitions" }
                        -button {
                            level = "primary"; title = "Update Assertion Code"; icon = "pencil"; onClickp {
                            global.testGlobal.minimalGertrude = true
                            jshit.callDebugRPWithProgress(json("msg" to json("fun" to "danger_updateAssertionCode", "assertionTag" to tag, "actualStringForPasting" to actualStringForPasting), "progressPlaceholder" to progressPlaceholder, "progressTitle" to "Updating assertion code"))
                        }
                        }
                    }
                    -progressPlaceholder
                }
            }

            unifyIndicesCheck = Shitus.Checkbox(json("initialValue" to stripFuckingIndices, "onChange" to {
                stripFuckingIndices = unifyIndicesCheck.getValue()
                makeFuckingTabs()
                updateWholeShit()
            }))
            hideKeyRepetitionsCheck = Shitus.Checkbox(json("initialValue" to hideFuckingKeyRepetitions, "onChange" to {
                hideFuckingKeyRepetitions = hideKeyRepetitionsCheck.getValue()
                makeFuckingTabs()
                updateWholeShit()
            }))


            makeFuckingTabs()

            global.assertionPane = json(
                "setLineHideFilter" to {f: dynamic ->
                    lineHideFilter = f
                    updateWholeShit()
                },

                "highlightStuff" to {arg: dynamic ->
                    // ({keys, scrollThere})
                    val keys = arg.keys; val scrollThere = arg.scrollThere
                    highlightedKeys = js("({})")
                    for (k in jsArrayToList(keys)) {
                        highlightedKeys[k] = true
                    }

                    makeFuckingTabs()
                    updateWholeShit()

                    if (scrollThere && keys.length) {
                        shittyFov(pileOfShit["scrollToDivForKey-${keys[0]}"])
                    }
                }
            )

            return@wholeShitCtor {
                jshit.art.assertionDetailsWithSourceLink(json("\$tag" to tag, "details" to tabs, "controls" to paneControls,
                    "collapsedDetails" to Shitus.updatableElement(js("({})"), {update ->
                        return@updatableElement {
                            Shitus.diva(js("({})"),
                                definitionStack && Shitus.diva(json("style" to json("marginBottom" to 8)), jshit.renderDefinitionStackStrip(json("stack" to definitionStack))),
                                art.renderStepDescriptions())
                        }
                    })
                ))
            }

        })
    }

    jshit.art.assert(false, descr, json("scrollThere" to scrollThere, "detailsUI" to detailsUI))
}

@native interface LegacyControl
@native interface LegacyDefinitionStack
@native interface LegacyCallStack

val stateContributionsByControl = mutableMapOf<LegacyControl, MutableMap<String, dynamic>>()

@native class TestUIStateContribution(
    val value: String,
    val control: LegacyControl,
    val definitionStack: LegacyDefinitionStack,
    val callStack: LegacyCallStack
)

fun invokeStateContributions(actual: MutableMap<String, Any>?) {
    // {actual}={}
//    val actual = if (arg) arg.actual else undefined

    jshit.art.stateContributionsByControl = js("new Map()")

    for (contribute in jsArrayToList(Shitus.values(jshit.art.uiStateContributions))) {
        contribute(json(
            "put" to {arg: dynamic ->
                // {$definitionStack, $callStack, control, key, value}
                val `$definitionStack` = arg.`$definitionStack`; val `$callStack` = arg.`$callStack`
                val control = arg.control; val key = arg.key; val value = arg.value

                Shitus.invariant(control, "I want control for state.put()")
                if (actual != null && global.Object.keys(actual).includes(key)) {
                    val message = "uiStateContribution put duplication: key=${key}, value=${value}"

                    runni {"__async"
                        val thisDefinitionStackString = __await(anyControlDefinitionStackString(control, sep = "\n"))
                        val existingDefinitionStackString = __await(anyControlDefinitionStackString(actual!![key].asDynamic().control, sep = "\n"))
                        console.error(
                            "$message\n\n" +
                                "This: $thisDefinitionStackString\n\n" +
                                "Existing: $existingDefinitionStackString")
                    }

                    jshit.raiseWithMeta(json(
                        "message" to message,
                        "metaItems" to jsArrayOf(
                            json("titlePrefix" to "This", "meta" to control),
                            json("titlePrefix" to "Existing", "meta" to actual!![key].asDynamic().control))))
                }

                if (control) {
                    var contributions = jshit.art.stateContributionsByControl.get(control)
                    if (!contributions) {
                        contributions = js("({})")
                        jshit.art.stateContributionsByControl.set(control, contributions)
                    }
                    contributions[key] = value
                }

                if (actual != null) {
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

//fun _new_invokeStateContributions(actual_: MutableMap<String, dynamic>) {
//
////    jshit.art.stateContributionsByControl = js("new Map()")
//    stateContributionsByControl.clear()
//
//    for (contribute: (Json) -> Unit in jsArrayToList(jshit.utils.values(jshit.art.uiStateContributions))) {
//        contribute(json(
//            "put" to {arg: dynamic ->
//                // {$definitionStack, $callStack, control, key, value}
//                val `$definitionStack` = arg.`$definitionStack`; val `$callStack` = arg.`$callStack`
//                val control: LegacyControl = arg.control
//                val key: String = arg.key
//                val value: String = arg.value
//
//                jshit.utils.invariant(control, "I want control for state.put()")
//                if (actual_.containsKey(key)) {
//                    val message = "uiStateContribution put duplication: key=${key}, value=${value}"
//
//                    runni {"__async"
//                        val thisDefinitionStackString = __await(anyControlDefinitionStackString(control, sep = "\n"))
//                        val existingDefinitionStackString = __await(anyControlDefinitionStackString(actual_[key]!!.control, sep = "\n"))
//                        console.error(
//                            "$message\n\n" +
//                            "This: $thisDefinitionStackString\n\n" +
//                            "Existing: $existingDefinitionStackString")
//                    }
//
//                    jshit.raiseWithMeta(json(
//                        "message" to message,
//                        "metaItems" to jsArrayOf(
//                            json("titlePrefix" to "This", "meta" to control),
//                            json("titlePrefix" to "Existing", "meta" to actual_[key]!!.control))))
//                }
//
//                if (control != null) {
//                    var contributions: MutableMap<String, String>? = stateContributionsByControl[control]
//                    if (contributions == null) {
//                        contributions = mutableMapOf()
//                        stateContributionsByControl.set(control, contributions)
//                    }
//                    contributions[key] = value
//                }
//
//// Old
////                if (control) {
////                    var contributions: MutableMap<String, String>? = stateContributionsByControl.get(control)
//////                    var contributions = jshit.art.stateContributionsByControl.get(control)
////                    if (contributions == null) {
////                        contributions = js("({})")
////                        stateContributionsByControl.set(control, contributions)
//////                        jshit.art.stateContributionsByControl.set(control, contributions)
////                    }
////                    contributions[key] = value
////                }
//
////                if (actual_ != null) {
////                    actual_[key] = TestUIStateContribution(value, control, `$definitionStack`, `$callStack`)
//                    actual_[key] = json("value" to value, "control" to control, "\$definitionStack" to `$definitionStack`, "\$callStack" to `$callStack`)
////                }
//            }
//        ))
//    }
//}

fun openTestPassedPane(def: dynamic) {
    val scenario = def.scenario

    val testPassedPane = Shitus.statefulElement(json("ctor" to { update: dynamic ->
        var scenarioName: String = scenario.name
        val links = mutableListOf<ReactElement>()

        val m = global.RegExp("\\s+([0-9a-z]{8})-([0-9a-z]{4})-([0-9a-z]{4})-([0-9a-z]{4})-([0-9a-z]{12})$").exec(scenarioName)
        if (m != undefined) {
            scenarioName = scenarioName.substring(0, m.index)
            links.add(jshit.OpenSourceCodeLink(json("where" to json("\$tag" to m[0].trim()), "style" to json("color" to Color.WHITE))))
        }
        if (jshit.art.actionPlaceholderTag != undefined) {
            links.add(jshit.marginateLeft(10, jshit.OpenSourceCodeLink(json("where" to json("\$tag" to jshit.art.actionPlaceholderTag), "style" to json("color" to Color.WHITE)))))
        }
        val uq = jshit.getURLQueryBeforeRunningTest()
        if (!uq.scrollToBottom || uq.scrollToBottom == "yes" || uq.scrollToBottom == "success") {
            kotlin.browser.window.requestAnimationFrame { kotlin.browser.document.body?.scrollTop = 99999 }
        }

        json(
            "render" to {
                when {
                    scenarioName == undefined -> null
                    else -> div {
                        noStateContributions = true
                        style {
                            backgroundColor = Color.GREEN_700; color = Color.WHITE
                            marginTop(10); padding = "10px 10px"; textAlign = "center"; fontWeight = "bold"
                        }

                        -div {
                            style { paddingBottom(10) }
                            -scenarioName
                            -div {
                                style { display = "flex"; justifyContent = "center" }
                                +links
                            }
                        }

                        -div {
                            style { backgroundColor = Color.WHITE; color = Color.BLACK_BOOT; fontWeight = "normal"; textAlign = "left"; padding(5) }
                            -art.renderStepDescriptions()
                        }
                    }
                }
            })
    }))

    jshit.debugPanes.set(json(
        "name" to "openTestPassedPane",
        "parentJqel" to jshit.byid("underFooter"),
        "element" to Shitus.spana(json(), testPassedPane.element)))
}
