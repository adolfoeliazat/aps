/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import kotlin.browser.window
import aps.Color.*
import aps.*
import aps.WorldPointRequest.Action.RESTORE
import aps.WorldPointRequest.Action.SAVE
import aps.const.text.symbols.emdash
import aps.front.testutils.*
import org.w3c.dom.events.KeyboardEvent
import kotlin.js.Json
import kotlin.js.json
import kotlin.reflect.KProperty

class HotReloadSurvivingFuckingShit(val nameInGlobalScope: String) {
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

sealed class TestInstruction() : DefinitionStackHolder {
    val definitionStackCapture = CaptureStackException()
    var isAssertion = false
    var snapshot: Snapshot? = null
    var debugDescription: String? = null
    var alwaysPassCondition = false

    override fun promiseDefinitionStack(): Promisoid<dynamic> {
        throw UnsupportedOperationException("Implement me, please, fuck you")
    }

    override fun toString(): String {
        return "<opcode=${ctorName(this)}>"
    }

    class WorldPoint(val name: String) : TestInstruction()
    class Do(val action: () -> Promisoid<Unit>) : TestInstruction()

    sealed class Step(val long: String, val rowBackgroundColor: Color?) : TestInstruction() {
        var executed = false
        var passed = false

        class ActionStep(long: String): Step(long, rowBackgroundColor = null)
        class StateStep(long: String): Step(long, rowBackgroundColor = null)
        class NavigationStep(long: String): Step(long, rowBackgroundColor = null)
        class AssertionStep(long: String): Step(long, rowBackgroundColor = null)
        class HaltStep(long: String): Step(long, rowBackgroundColor = null)

        class CustomStep(
            long: String,
            rowBackgroundColor: Color?,
            val label: String,
            val labelBackgroundColor: Color,
            val labelColor: Color
        ): Step(long, rowBackgroundColor)
    }

    class BeginSection(val long: String) : TestInstruction()
    class EndSection() : TestInstruction()

    class AssertGenerated(val tag: String, val expected: String, val expectedExtender: ((dynamic) -> Unit)?) : TestInstruction()

    class AssertFuck(val tag: String, val expected: Json) : TestInstruction()

    abstract class Action(override val shame: String, val timestamp: String) : TestInstruction(), ShamedTestInstruction

    class SetValue(shame: String, val value: String, timestamp: String = NILS) : Action(shame, timestamp)
    class SetCheckbox(shame: String, val value: Boolean, timestamp: String = NILS) : Action(shame, timestamp)

    class Click(shame: String, timestamp: String = NILS) : Action(shame, timestamp)
    class KeyDown(shame: String, val keyCode: Int, timestamp: String = NILS) : Action(shame, timestamp)
//    class ActionPlaceholder() : TestInstruction()
}

typealias ControlID = String
typealias TestStateContributor = (TestStateContributions) -> Unit

interface TestStateContributions {
    fun put(control: WithElementID, key: String, value: String)
}

object art {
    lateinit var testSpeed: String
    val stateContributionsByControl = mutableMapOf<WithElementID, MutableMap<String, String>>()
    val halted: Boolean = false
    var respectArtPauses: Boolean = false
    var stepDescriptions: dynamic = jsArrayOf()
    val uiStateContributions = mutableMapOf<ControlID, TestStateContributor>()
    var actionPlaceholderTag: String? = null
    var testInstructions: Iterable<TestInstruction> by HotReloadSurvivingFuckingShit("art_testInstructions")

    fun uiState(def: dynamic): Promisoid<Unit> = async {
        val url: String = loc.href // Capture location for reassertion on hot reload, as it will be changed to "/test.html..."

        hrss.reassertUIState = {arg: dynamic -> async {
            val scrollThere: dynamic = arg.scrollThere
            await<dynamic>(debugCheckEmail())

//            val actual = json("url" to url)
            val actual = mutableMapOf<String, Any>("url" to url)
            invokeStateContributions(actual)
            gertrude(global.Object.assign(def, json("actual" to actual, "scrollThere" to scrollThere)))
        }}

        await<dynamic>(hrss.reassertUIState(json("scrollThere" to true)))
        return@async Unit
    }


    fun run(shit: TestShit, instructions: List<TestInstruction>, showTestPassedPane: Boolean): Promisoid<Throwable?> = async {
        try {
            testInstructions = instructions

            val urlq = getURLQuery()
            val until = urlq["until"]?.let {it.toInt()} ?: Int.MAX_VALUE
            val from = urlq["from"] ?: "start"

            var skipping = from != "start"

            var stepIndex = 0
//            instructions.forEachIndexed {instrIndex, instr ->
            var instrIndex = 0
            while (instrIndex <= instructions.lastIndex) {
                shit.nextInstructionIndex = instrIndex + 1
                val instr = instructions[instrIndex]
                fun getControlForAction(arg: dynamic): dynamic {
                    if (instr !is ShamedTestInstruction) throw FatException("I want ShamedTestInstruction, got $instr")

                    val implementing = if (arg) arg.implementing else undefined

                    val control = TestGlobal.shameToControl[instr.shame]
                    if (!control) Shitus.raiseWithMeta(json("message" to "Control shamed ${instr.shame} is not found", "meta" to instr))
                    if (implementing && !control[implementing]) Shitus.raiseWithMeta(json("message" to "Control shamed ${instr.shame} is expected to implement ${implementing}", "meta" to instr))
                    return control
                }

                fun executeSetValueLike(): Promisoid<Unit> {
                    die("@kill executeSetValueLike")
                }

                if (instrIndex == until) {
                    dlog("Stopping test before instruction ${instrIndex}")
                    return@async null
                }

                when {
                    skipping -> {
                        when (instr) {
                            is TestInstruction.WorldPoint -> {
                                die("Don't fucking use me    8b6af0bf-7fc5-4e9d-8ffb-57aee0e37174")
//                                val wpname: String = fullWorldPointName(instr)
//                                if (instr.name == from) {
//                                    dlog("Restoring world point ${wpname}")
//                                    await(WorldPointRequest.send(wpname, RESTORE))
//                                    skipping = false
//                                }
                            }
                        }
                    }
                    !skipping -> {
                        exhaustive=when (instr) {
                            is TestInstruction.WorldPoint -> {
                                die("Don't fucking use me    5583ccb6-9a7b-4128-bfa4-8b20da2e7f2e")
//                                val wpname: String = fullWorldPointName(instr)
//                                dlog("Saving world point ${wpname}")
//                                await(WorldPointRequest.send(wpname, SAVE))
                            }

                            is TestInstruction.BeginSection -> {}
                            is TestInstruction.EndSection -> {}
                            is TestInstruction.Do -> {unit{
                                if (instr.alwaysPassCondition || TestGlobal.instructionCondition()) {
                                    await(instr.action())
                                }
                            }}
                            is TestInstruction.Step -> {
                                instr.executed = true
                            }
                            is TestInstruction.AssertGenerated -> {
                                await(art.uiState(json(
                                    "\$tag" to instr.tag,
                                    "expected" to "---generated-shit---",
                                    "expectedExtender" to instr.expectedExtender
                                )))
                            }
                            is TestInstruction.AssertFuck -> {
                                await(art.uiState(json(
                                    "\$tag" to instr.tag,
                                    "expected" to instr.expected
                                )))
                            }
                            is TestInstruction.SetValue -> {
                                await(executeSetValueLike())
                            }
                            is TestInstruction.SetCheckbox -> {
                                await(executeSetValueLike())
                            }
                            is TestInstruction.Click -> {
                                die("Fuck you 175a1d6f-90ec-4973-9e26-b3fed616f992")
//                                val control = getControlForAction(json("implementing" to "testClick"))
//                                if (instr.timestamp.there) {
//                                    await(ImposeNextRequestTimestampRequest.send(instr.timestamp))
//                                }
//                                await<dynamic>(control.testClick(instr))
                            }
                            is TestInstruction.KeyDown -> {
                                die("Fuck you 90f96c88-ddc9-4feb-86d5-70621d19a23a")
//                                val control = getControlForAction(json("implementing" to "testKeyDown"))
//                                if (instr.timestamp.there) {
//                                    await(ImposeNextRequestTimestampRequest.send(instr.timestamp))
//                                }
//                                await<dynamic>(control.testKeyDown(instr))
                            }
                            is TestInstruction.Action -> {} // This is label?
                        }
                    }
                }

                instrIndex = shit.nextInstructionIndex
            }

            if (skipping) {
                console.warn("WTF, I’ve just skipped all test steps")
            } else {
                dlog("Seems test is passed")
            }

            if (showTestPassedPane) openTestPassedPane()
            return@async null
        }
        catch (e: ArtFuckingError) {
            showAssertionErrorPane(e, e.detailsUI)
            return@async e
        }
        catch (e: Throwable) {
            showAssertionErrorPane(e,
                                   kdiv(padding=5){o->
                                       o- renderStackTrace(e, onRendered = {
                                           scrollRevealing("debug_assertionErrorPane")
                                       })
                                       o- renderCurrentTestScenarioDetails(marginTop=5)
                                   },
                                   backgroundColor = when (e) {
                                       is ArtAssertionError -> RED_700
                                       else -> RED_700
                                   })
            return@async e
        }
    }

    fun fullWorldPointName(instr: TestInstruction.WorldPoint): String {
        return hrss.currentTestScenario!!.name + " -- " + instr.name
    }
//    fun fullWorldPointName(instr: TestInstruction.WorldPoint) = hrss.currentTestScenarioName + " -- " + instr.name

    fun scrollRevealing(id: String) {
        requestAnimationFrame {
            jqbody.scrollTop(byid(id).offset().top - 60)
        }
    }

    fun showAssertionErrorPane(exception: Throwable, detailsUI: ToReactElementable, backgroundColor: Color = RED_700) {
        async {
            console.error(await(errorToMappedClientStackString(exception)))
        }
        val message = exception.message
        val assertionErrorPane = object : Control2(Attrs()) {
            override fun defaultControlTypeName() = "assertionErrorPane"

            var visible = false
            var content: dynamic = null

            override fun render(): ToReactElementable {
                if (!visible) return NOTRE

                val messageStyle = json("fontWeight" to "bold", "paddingBottom" to 5, "marginBottom" to 5, "color" to WHITE.toString())
                if (content.stack) {
                    global.Object.assign(messageStyle, json(
                        "borderBottom" to "2px solid white")
                    )
                }
                return oldShitAsToReactElementable(Shitus.diva(json("id" to "debug_assertionErrorPane", "className" to css.test.assertionErrorPane, "style" to json("backgroundColor" to backgroundColor, "marginTop" to 10, "padding" to "10px 10px", "textAlign" to "left")),
                                                               Shitus.diva(json("style" to messageStyle), content.message),
                                                               content.stack && Shitus.diva(json("style" to json("whiteSpace" to "pre-wrap", "color" to WHITE.toString())), content.stack),
                                                               content.detailsUI
                ))
            }

            fun set(def: dynamic) {
                val scrollThere: dynamic = def.scrollThere
                content = def
                visible = true
                update()

                if (scrollThere && !hrss.preventScrollToBottomOnAssertionError) {
                    scrollRevealing("debug_assertionErrorPane")
                }
            }
        }

        old_debugPanes.put(byid(fconst.elementID.underFooter), assertionErrorPane)

//        val existingDiv = Shitus.byid("debug_assertionErrorPane")
//        if (existingDiv[0]) {
//            global.ReactDOM.unmountComponentAtNode(existingDiv[0])
//            existingDiv.remove()
//        }
//        Shitus.byid("footer").after("<div id='debug_assertionErrorPane'></div>")
//        val container = Shitus.byid0("debug_assertionErrorPane")
//        if (container != null) {
//            DOMReact.render(assertionErrorPane.toReactElement(), container)
//        } else {
//            cwarn("Failed to assertion error pane -- no container for it")
//            console.error(exception.asDynamic().stack)
//            if (exception is FatException) {
//                exception.markdownPayload?.let {
//                    console.error(it)
//                }
//            }
//        }

        val stack = null
        assertionErrorPane.set(json(
            "message" to (message ?: "No fucking message") + emdash + hrss.currentTestScenario!!.name,
            "stack" to stack,
            "detailsUI" to kdiv(backgroundColor = WHITE){it-detailsUI}.toReactElement(),
            "scrollThere" to true))
    }

    fun renderStepDescriptions(): ReactElement {
        val testInstructions = art.testInstructions
        val els = mutableListOf<ReactElement>()

        var stepIndex = 0; var indent = 0
        testInstructions.forEachIndexed {instrIndex, instr ->

            fun addLine(indent: Int, stepRowStyle: Style = Style(), rulerContent: ReactElement? = null, lineContent: ToReactElementable? = null, actions: Collection<ReactElement> = listOf()) {
                els.add(kdiv(marginTop=5, display="flex"){o->
                    o- kdiv(fontWeight="bold", width=40){it- rulerContent}
                    // XXX This `width: 100%` is for fucking flexbox to not change `width: 40` above... http://stackoverflow.com/questions/7985021/css-flexbox-issue-why-is-the-width-of-my-flexchildren-affected-by-their-content
                    o- kdiv(className="showOnParentHovered-parent", baseStyle=stepRowStyle, width="100%", display="flex"){o->
                        o+ (1..indent).map {kdiv(width=20, borderLeft="2px dotted ${Color.GRAY_500}")}
                        o- lineContent
                        o- kdiv(className="showOnParentHovered"){o->
                            o- hor2(style = Style(marginLeft=8, paddingLeft=8, borderLeft="2px solid ${Color.GRAY_500}")){o->
                                o+ actions
                                o- renderStackLink(instr.definitionStackCapture)
//                                o- renderExpandableOnDemandStack(instr)
//                                - jshit.OpenSourceCodeLink(json("where" to instrdef, "style" to json("marginLeft" to 20)))
                            }}}}.toReactElement())
            }

            if (instr is TestInstruction.Step) {
                val title = instr.long
                val untilParamValue = if (instrIndex == art.stepDescriptions.length - 1) "infinity" else instrIndex

                val stepRowStyle = Style()
                if (!instr.executed) {
                    stepRowStyle.opacity = 0.3
                }

                fun labelBack(passed: Color, failed: Color, skipped: Color? = null): Color {
                    val _skipped = skipped ?: passed
                    return when {
                        instr.executed -> when {
                            instr.passed -> passed
                            else -> failed
                        }
                        else -> _skipped
                    }
                }

                addLine(
                    indent, stepRowStyle,
                    rulerContent = ("#" + (stepIndex++ + 1)).asDynamicReactElement(),

                    lineContent = kdiv(display = "flex", backgroundColor = instr.rowBackgroundColor){o->
                        o- when (instr) { // TODO:vgrechka Use sealed class
                            is TestInstruction.Step.ActionStep -> kspan(marginRight=5, padding=3, fontSize="75%", backgroundColor=labelBack(passed=BLUE_200, failed=RED_200)) {it-"Action"}
                            is TestInstruction.Step.StateStep -> kspan(marginRight=5, padding=3, backgroundColor=ORANGE_200, fontSize="75%") {it-"State"}
                            is TestInstruction.Step.NavigationStep -> kspan(marginRight=5, padding=3, backgroundColor=BROWN_50, fontSize="75%") {it-"Navigation"}
                            is TestInstruction.Step.AssertionStep -> kspan(marginRight=5, padding=3, fontSize="75%", backgroundColor=labelBack(passed=GREEN_200, failed=RED_200, skipped=GRAY_300)) {it-"Assertion"}
                            is TestInstruction.Step.HaltStep -> kspan(marginRight=5, padding=3, fontSize="75%", backgroundColor=labelBack(passed=GREEN_200, failed=RED_200, skipped=GRAY_300)) {it-"Halt"}
                            is TestInstruction.Step.CustomStep -> kspan(
                                marginRight=5,
                                padding=3,
                                fontSize="75%",
                                backgroundColor=instr.labelBackgroundColor,
                                color=instr.labelColor){o->
                                        o- instr.label
                                    }
                        }
                        o- markdown(title, stripP=true)
                    },

                    actions = listOf(
                        // TODO:vgrechka @duplication 4dfaa71f-4eaa-4ce9-992f-60f9587f69ae 1
                        jsLink(json("title" to "Run until " + untilParamValue, "onClick" to {
                            var href = Globus.realLocation.href
                            href = href.replace(Regex("&from[^&]*"), "")
                            href = href.replace(Regex("&until[^&]*"), "")
                            href += "&until=" + untilParamValue
                            Globus.realLocation.href = href
                        }))
                    )
                )
            }
            else if (instr is TestInstruction.BeginSection) {
                addLine(indent, lineContent = kdiv(fontWeight="bold") {it- instr.long})
                ++indent
            }
            else if (instr is TestInstruction.EndSection) {
                --indent
            }
            else if (instr is TestInstruction.WorldPoint) {
                addLine(
                    indent,
                    lineContent = kdiv(fontWeight="normal", fontStyle="italic"){it- "World point: ${instr.name}"},
                    rulerContent = jdiva(json("style" to json("position" to "relative")),
                        Shitus.ia(json("className" to "fa fa-circle", "style" to json("color" to Color.GRAY_500))),
                        jdiva(json("style" to json("width" to 38, "position" to "absolute", "left" to 0, "top" to 9, "borderTop" to "2px dotted ${Color.GRAY_500}")))
                    ),
                    // TODO:vgrechka @duplication 4dfaa71f-4eaa-4ce9-992f-60f9587f69ae 2
                    actions = listOf(
                        jsLink(json("title" to "Run from", "onClick" to {
                            var href = Globus.realLocation.href
                            href = href.replace(Regex("&from[^&]*"), "")
                            href = href.replace(Regex("&until[^&]*"), "")
                            href += "&from=" + instr.name
                            Globus.realLocation.href = href
                        }))
                    )
                )
            }
        }

        return jdiva(json("controlTypeName" to "renderStepDescriptions", "noStateContributions" to true), jdiva(json("style" to json("background" to Color.GRAY_200, "fontWeight" to "bold")), "Steps"),
            *els.toTypedArray())
    }

    fun assert(condition: Boolean, errorMessage: String, detailsUI: ToReactElementable) {
        if (!condition) throw ArtFuckingError(errorMessage, detailsUI)
    }

    fun assertionDetailsWithSourceLink(arg: dynamic): dynamic {
        val `$tag`: dynamic = arg.`$tag`
        val details: dynamic = arg.details
        val collapsedDetails: dynamic = arg.collapsedDetails
        val controls: dynamic = arg.controls

        val cshit = CollapsibleShit(json("initialOpen" to true, "content" to Shitus.diva(json("style" to json("marginTop" to 5, "marginBottom" to 5)),
            collapsedDetails || Shitus.spana(json(), "Nothing particularly interesting here"))))

        return Shitus.diva(json("noStateContributions" to true, "style" to json("marginTop" to 5, "padding" to 5, "backgroundColor" to WHITE.toString(), "position" to "relative")),
            Shitus.horiza(json("style" to json("marginBottom" to 5)),
                Shitus.spana(json("style" to json("fontWeight" to "bold")), "Assertion ID: "),
                OpenSourceCodeLink(json("where" to json("\$tag" to `$tag`))),
                cshit.renderCaret(json("marginLeft" to 10))),
            cshit.renderContent(),
            details,
            Shitus.diva(json("style" to json("position" to "absolute", "right" to 5, "top" to 5)), controls)
        )
    }

    fun showTestActionHand(arg: dynamic): dynamic {
        imf("showTestActionHand")
    }

    fun initDebugFunctionsShit() {
        die("@kill initDebugFunctionsShit")
    }

    fun fail(message: String) {
        throw ArtAssertionError(message)
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

    hrss.lastSeenTag = tag
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

        for (k in jsArrayToListOfDynamic(extenderKeys)) {
            val komega = k.replace(js("/-i\\d\\d\\d/g"), "-ω")
            if (!extenderKeys.includes(komega)) {
                extenderKeys.push(komega)
            }
        }
    }

    val keyToDefinitionStack = js("({})"); val keyToCallStack = js("({})"); val keyToControl = js("({})")
    for (key: String in jsArrayToListOfDynamic(global.Object.keys(actual))) {
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
        hrss.artLastSuccessfulExpected = expected
        return
    }

    val detailsUI: ToReactElementable
    if (hrss.urlQueryBeforeRunningTest.minimalGertrude == "yes" || TestGlobal.minimalGertrude) {
        detailsUI = oldShitAsToReactElementable(div { styleKludge = json("background" to Color.WHITE); -"I am minimal because of minimalGertrude" })
//        detailsUI = Shitus.diva(json("style" to json("background" to jshit.WHITE)), t("I am minimal because of minimalGertrude"))
    } else {
        detailsUI = oldShitAsToReactElementable(Shitus.updatableElement(js("({})"), wholeShitCtor@ { updateWholeShit ->
            var stripFuckingIndices = true; var hideFuckingKeyRepetitions = false; var tabs: dynamic = null
            var paneControls: dynamic = null; var lineHideFilter = Shitus.noop
            var highlightedKeys = js("({})"); val pileOfShit = js("({})")
            val progressPlaceholder = Shitus.Placeholder()
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
                    val shit: String = valueLine.slice(0, colonIndex).replace(js("/'/g"), "")
                    val key = shit.trim()
                    if (extenderKeys.includes(key)) {
                        return true
                    }
                    return false
                }



                Shitus.sortKeys(actual) // Order of keys sent over the wire is mangled
                Shitus.sortKeys(expected)

                val definitionStacks = js("[]"); val callStacks = js("[]"); val controls = js("[]"); val origKeys = js("[]")
                for (key in jsArrayToListOfDynamic(global.Object.keys(actual))) {
                    definitionStacks.push(keyToDefinitionStack[key])
                    callStacks.push(keyToCallStack[key])
                    controls.push(keyToControl[key])
                    origKeys.push(key)
                }

                val actualString = repr(actual, json("stripIndices" to stripFuckingIndices))
                val actualStringOrig = repr(actual, json("stripIndices" to false))
                val expectedString = repr(expected, json("stripIndices" to stripFuckingIndices))
                val lastExpectedString = if (hrss.artLastSuccessfulExpected != null) repr(hrss.artLastSuccessfulExpected, json("stripIndices" to stripFuckingIndices)) else "--- Nothing ---"
                val diffDivs = js("[]"); val diffLastDivs = js("[]")

                var lineIndex = 0; var actualLineIndex = 0

                fun fillDiffDivs(arg: dynamic) {
                    // {divs, string1, string2, greenTitle}
                    val divs = arg.divs; val string1 = arg.string1; val string2 = arg.string2; var greenTitle = arg.greenTitle

                    if (!greenTitle) greenTitle = "Expected"
                    var prevLabel: dynamic = undefined
                    val diffLineItems = global.JsDiff.diffLines(string1, string2)
                    for (item in jsArrayToListOfDynamic(diffLineItems)) {
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
                        for ((i, valueLine) in jsArrayToListOfDynamic(valueLines).withIndex()) {
                            var shouldBeHiddenCuaseItsFuckingKeyRepetition: dynamic = undefined
                            var shouldBeHighlighted: dynamic = undefined

                            val lineDivID = "diffLine-$i"
//                            val lineDivID = puid()
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
                                                        -renderStacks(json("definitionStack" to definitionStack, "callStack" to callStack))
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
                                                imf("Control revealing in art")
//                                                Shitus.revealControl(control, json("scrollToTarget" to true))
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
                                        global.requestAnimationFrame {
                                            js("$")(global.document).scrollTop(Shitus.byid(lineDivID).offset().top - 50 - 20)
                                        }
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
                                                "background" to ORANGE_100))
                                        }

                                        return@updatableElement {
                                            div {
                                                id = "" + lineDivID
                                                className = "showOnParentHovered-parent"
                                                styleKludge = style

                                                if (isExtValueLine(valueLine))
                                                    -span {
                                                        styleKludge = json("marginRight" to 5, "padding" to 3, "background" to ORANGE_200, "fontSize" to "75%")
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
                    val shit: String = actualStringForPasting.slice(1, actualStringForPasting.length - 1)
                    actualStringForPasting = shit.trimStart()
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

                val shit: Array<dynamic> = replacements
                shit.sortBy {val from: Int = it; from}
//                replacements = Shitus.sortBy(replacements, "from")

                var newActualStringForPasting = ""; from = 0
                for (replacement in jsArrayToListOfDynamic(replacements)) {
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

                tabs = Tabs(json(
                    "activeTab" to if (JSObject.keys(expected).isEmpty()) "diffLast" else "diff",
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
                        -hor1_killme { -unifyIndicesCheck; -"Unify indices" }
                        -hor1_killme { -hideKeyRepetitionsCheck; -"Hide key repetitions" }
                        -button {
                            level = "primary"; title = "Update Assertion Code"; icon = "pencil"; onClickp {
                            TestGlobal.minimalGertrude = true
                            callDebugRPWithProgress(json("msg" to json("fun" to "danger_updateAssertionCode", "assertionTag" to tag, "actualStringForPasting" to actualStringForPasting), "progressPlaceholder" to progressPlaceholder, "progressTitle" to "Updating assertion code"))
                        }
                        }
                    }
                    -progressPlaceholder
                }
            }

            unifyIndicesCheck = jsFacing_Checkbox(json("initialValue" to stripFuckingIndices, "onChange" to {
                stripFuckingIndices = unifyIndicesCheck.getValue()
                makeFuckingTabs()
                updateWholeShit()
            }))
            hideKeyRepetitionsCheck = jsFacing_Checkbox(json("initialValue" to hideFuckingKeyRepetitions, "onChange" to {
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
                    for (k in jsArrayToListOfDynamic(keys)) {
                        highlightedKeys[k] = true
                    }

                    makeFuckingTabs()
                    updateWholeShit()

                    if (scrollThere && keys.length) {
                        val scrollToShitFunction = pileOfShit["scrollToDivForKey-${keys[0]}"]
                        if (scrollToShitFunction != null) {
                            scrollToShitFunction()
                        }
                    }
                }
            )

            return@wholeShitCtor {
                art.assertionDetailsWithSourceLink(json("\$tag" to tag, "details" to tabs, "controls" to paneControls,
                    "collapsedDetails" to Shitus.updatableElement(js("({})"), {update ->
                        return@updatableElement {
                            Shitus.diva(js("({})"),
                                definitionStack && Shitus.diva(json("style" to json("marginBottom" to 8)), Shitus.renderDefinitionStackStrip(json("stack" to definitionStack))),
                                art.renderStepDescriptions())
                        }
                    })
                ))
            }

        }))
    }

    art.assert(false, descr, detailsUI)
}

external interface LegacyControl
external interface LegacyDefinitionStack
external interface LegacyCallStack


fun invokeStateContributions(actual: MutableMap<String, Any>?) {
    // println("--- invokeStateContributions ---")
    art.stateContributionsByControl.clear()

    for (contribute in art.uiStateContributions.values) {
        contribute(object:TestStateContributions {
            override fun put(control: WithElementID, key: String, value: String) {
                if (actual != null && actual.containsKey(key)) {
                    val message = "uiStateContribution put duplication: key=${key}, value=${value}"

                    runni {
                        async {
                            val thisDefinitionStackString = await(anyControlDefinitionStackString(control, sep = "\n"))
                            val existingDefinitionStackString = await(anyControlDefinitionStackString(actual!![key].asDynamic().control, sep = "\n"))
                            console.error(
                                "$message\n\n" +
                                    "This: $thisDefinitionStackString\n\n" +
                                    "Existing: $existingDefinitionStackString")
                        }
                    }

                    Shitus.raiseWithMeta(json(
                        "message" to message,
                        "metaItems" to jsArrayOf(
                            json("titlePrefix" to "This", "meta" to control),
                            json("titlePrefix" to "Existing", "meta" to actual!![key].asDynamic().control))))
                }

//                if (control) {
                    var contributions: MutableMap<String, String>? = art.stateContributionsByControl.get(control)
                    if (contributions == null) {
                        contributions = mutableMapOf()
                        art.stateContributionsByControl.put(control, contributions)
                    }
                    contributions[key] = value
//                }

                if (actual != null) {
                    actual[key] = json(
                        "value" to value,
                        "control" to control
//                        "\$definitionStack" to `$definitionStack`,
//                        "\$callStack" to `$callStack`
                    )
                }
            }
        })
    }
}

fun openTestListPane() {
//    val defaultOpts = TestOptions(stopOnAssertions = true, dontStopOnCorrectAssertions = true)
    val defaultOpts = TestOptions(stopOnAssertions = true, dontStopOnCorrectAssertions = true, animateUserActions = true, handPauses = true)

    old_debugPanes.put("openTestListPane", Shitus.byid(fconst.elementID.underFooter), kdiv(className = css.test.pane.testList.pane){o->
        o- kdiv(paddingBottom = 10){o->
            o- "Tests"
        }

        o- kdiv(className = css.test.pane.testList.content){o->
            fun testLink(testName: String, opts: TestOptions, title: String) = link(
                title = title,
                onClick = {
                    Globus.realLocation.href = testNameToURL(testName, opts)
                })

            val lastName = TestGlobal.lastTest.name
            o- kdiv{o->
                val opts = TestGlobal.lastTestOpts
                val descr = TestOptionsTemplates.all.find {it.opts == opts}?.title ?: opts.toString()
                o- testLink(lastName, opts, title = "$lastName: $descr")
            }
            o- kdiv(className = css.test.pane.testList.separator)

            for (opts in TestOptionsTemplates.all.filter {it.opts != TestGlobal.lastTestOpts}) {
                o- kdiv {o->
                    o- testLink(lastName, opts.opts, title = "$lastName: ${opts.title}")
                }
            }

            fun renderSuite(suite: TestSuite) = kdiv(marginTop = "1em"){o->
                o- kdiv(fontWeight = "bold"){o->
                    val testSuiteName = suite::class.simpleName!!
                    o- link(title = testSuiteName, color = BLACK_BOOT, onClick = {
                        Globus.realLocation.href = testSuiteNameToURL(testSuiteName, defaultOpts)
                    })
                }
                for (x in suite.scenarios) {
                    o- kspan(marginRight = "1em"){o->
                        o- testLink(x.name, defaultOpts, title = x.name)
                    }
                    o- " "
                }
            }

            o- renderSuite(TestSuite_UACustomer_Shebang())
            o- renderSuite(TestSuite_UA_CrazyLong())
        }
    })
}

fun openTestPassedPane() {
    openShitPassedPane(
        title = run {
            val scenario = hrss.currentTestScenario!!
            var title = scenario.name
            scenario.shortDescription?.let {if (it != title) title += ". $it"}
            title
        },
        details = renderCurrentTestScenarioDetails())
}

fun openShitPassedPane(title: String, details: ElementBuilder) {
    old_debugPanes.put("openTestPassedPane", Shitus.byid(fconst.elementID.underFooter), kdiv(noStateContributions = true) {o ->
        o - Style(backgroundColor = GREEN_700, color = WHITE,
                  marginTop = 10, padding = "10px 10px", textAlign = "center", fontWeight = "bold")

        o - kdiv(paddingBottom = 10) {o ->
            o - run {
                title
            }
        }

        o - kdiv {o ->
            o - Style(backgroundColor = WHITE, color = BLACK_BOOT,
                      fontWeight = "normal", textAlign = "left", padding = 5)
            o - details
        }
    })

    if (!Globus.realTypedStorageLocal.dontScrollOnTestPassed)
        art.scrollRevealing(fconst.elementID.underFooter)

    var notice = ""
    if (TestGlobal.hasScenarioTODOs) notice += " (with TODOs)"
    if (TestGlobal.hasScenarioRems) notice += " (with rems)"
    clog("----- We good $notice-----")
}


//        async scroll({origY, destY, totalTime}) {
//            const bottom = $(document).height() - window.innerHeight
//            const current = $(document).scrollTop()
//            if (origY === 'bottom') origY = bottom
//            if (origY === 'current') origY = current
//            if (destY === 'bottom') destY = bottom
//            if (destY === 'current') destY = current
//
//            if ($(document).scrollTop() !== origY) {
//                if (testSpeed === 'slow') {
//                    await doit({origY: $(document).scrollTop(), destY: origY, totalTime: 150})
//                } else {
//                    $(document).scrollTop(origY)
//                }
//            }
//
//            await doit({origY, destY, totalTime})
//
//            /*async*/ function doit({origY, destY, totalTime}) {
//                return new Promise(resolve => {
//                    if (!totalTime) {
//                        if (testSpeed === 'slow') totalTime= 1000
//                        else totalTime= 50
//                    }
//                    const startTime = Date.now()
//                    requestAnimationFrame(function it() {
//                        let y
//                        const dt = Date.now() - startTime
//                        if (dt >= totalTime) {
//                            y = destY
//                        } else {
//                            y = origY + (destY - origY) * dt / totalTime
//                        }
//                        $(document).scrollTop(y)
//                        if (y === destY) {
//                            // dlog('No more scrolling')
//                            resolve()
//                        } else {
//                            requestAnimationFrame(it)
//                        }
//                    })
//                })
//            }
//        },


//        async pausePoint({$tag, title, theme='lime', locus='bottom'}) {
//            lastSeenTag = $tag
//            if (!art.respectArtPauses) return
//
//            await art.ui.pollLiveStatus()
//
//            await Shitus.run(function() {
//                return new Promise(resolve => {
//                    debugPanes.set({name: 'artPause', element: updatableElement(s{}, update => {
//                        window.addEventListener('keydown', keyListener)
//
//                        if (/\n/.test(title)) {
//                            title = Shitus.spana({style: {whiteSpace: 'pre'}}, dedent(title))
//                        }
//
//                        let top, bottom, left, right, width, borders
//                        if (locus === 'bottom') {
//                            bottom = 0
//                            width = '100%'
//                            borders = 'top'
//                        } else if (locus === 'top-right') {
//                            top = 0
//                            right = 0
//                            width = '50%'
//                            borders = 'left bottom'
//                        } else {
//                            Shitus.raise(`Weird locus: ${locus}`)
//                        }
//
//                        let backgroundColor, borderColor
//                        if (theme === 'lime') {
//                            backgroundColor = LIME_100
//                            borderColor = LIME_900
//                        } else if (theme === 'blue') {
//                            backgroundColor = BLUE_50
//                            borderColor = BLUE_700
//                        }
//
//                        const bos = `3px solid ${borderColor}`
//                        const borderStyles = {
//                            borderTop: borders.includes('top') && bos,
//                            borderRight: borders.includes('right')  && bos,
//                            borderBottom: borders.includes('bottom') && bos,
//                            borderLeft: borders.includes('left')  && bos
//                        }
//
//                        return _=> Shitus.diva({style: Object.assign({position: 'fixed', zIndex: 100000, display: 'flex', bottom, top, right, left, width, backgroundColor, padding: '10px 10px'}, borderStyles)},
//                                       Shitus.diva({},
//                                           Shitus.diva({style: {fontWeight: 'bold'}}, title),
//                                           OpenSourceCodeLink({where: {$tag}})),
//                                       button({icon: 'play', style: {marginLeft: 10}, onClick() {
//                                           resume()
//                                       }, untested: true}))
//
//
//                        function resume() {
//                            window.removeEventListener('keydown', keyListener)
//                            debugPanes.delete({name: 'artPause'})
//                            resolve()
//                        }
//
//                        function keyListener(e) {
//                            if (document.activeElement.tagName === 'INPUT') return
//                            if (e.key === ' ') {
//                                e.preventDefault()
//                                e.stopPropagation()
//                                if (e.ctrlKey) {
//                                    tinkeringWithTestActionHand = true
//                                }
//                                resume()
//                            }
//                        }
//                    })})
//                })
//            })
//        },

//async shitBlinksForMax({$tag, name, max, kind='link'}) {
//    lastSeenTag = $tag
//    art.shitBlinks({$tag, name, kind})
//
//    const t0 = Date.now()
//    while (Date.now() - t0 < max) {
//        if (!testGlobal[kind + '_' + name + '_blinks']) return
//        await Shitus.delay(50)
//    }
//
//    art.assertWithSourceLink(false, `I expected ${kind} ${name} to stop blinking in ${max}ms`, $tag)
//},

//        shitBlinks({$tag, name, kind='link'}) {
//            lastSeenTag = $tag
//            art.assertWithSourceLink(testGlobal[kind + '_' + name + '_blinks'], `I want ${kind} ${name} to be blinking`, $tag)
//        },

fun dumpContributionsByControlAndChildren(target: dynamic, _opts: dynamic) {
    val opts: dynamic = _opts ?: json()
    val sorting: dynamic = opts.sorting ?: "key"
    val commonPrefixesToEllipsis = opts.commonPrefixesToEllipsis

    clog("--------------------")
    clog("CONTRIBS (+CHILDREN): ${controlDisplayNameForDumping(target)}")
    clog("--------------------")
    var contributions = findContributionsByControlAndChildren(target)

    var sortf: dynamic = null
    if (sorting === "key") sortf = {pair: dynamic -> pair[0]}
    else if (sorting === "keyLength") sortf = {pair: dynamic -> pair[0].length}
    else Shitus.raise("Bad sorting option: " + sorting)
    sortObjectBy(contributions, sortf)

    if (commonPrefixesToEllipsis) {
        val pairs = lodash.toPairs(contributions)
        var prevKey: dynamic = null
        for (pair in jsArrayToListOfDynamic(pairs)) {
            val key: dynamic = pair[0]
            val value: dynamic = pair[1]
            if (prevKey) {
                var commonPrefixLen = 0
                while ((commonPrefixLen + 1).asDynamic() < key.length && (commonPrefixLen + 1).asDynamic() < prevKey.length) {
                    if (key[commonPrefixLen] === prevKey[commonPrefixLen]) {
                        ++commonPrefixLen
                    } else {
                        break
                    }
                }

                pair[0] = Shitus.repeat(".", commonPrefixLen) + key.slice(commonPrefixLen)
            }
            prevKey = key
        }
        contributions = lodash.fromPairs(pairs)
    }

    clog(global.nodeUtil.inspect(contributions, json("depth" to null)))
}

fun controlDisplayNameForDumping(target: dynamic): dynamic {
    return if (target.tame) target.getTamePath() else target.debugDisplayName
}

fun sortObjectBy(o: dynamic, f: dynamic) {
    if (lodash.isArray(o)) {
        o.filter(lodash.isObject).forEach(::sortKeys)
    } else if (lodash.isObject(o)) {
        var pairs = lodash.toPairs(o)
        pairs = lodash.sortBy(pairs, {x: dynamic -> f(x)})
        pairs.forEach {arg: dynamic ->
            val k = arg[0]
            val v = arg[1]
            jsDeleteKey(o, k)
        }
        pairs.forEach {arg: dynamic ->
            val k = arg[0]
            val v = arg[1]
            o[k] = v
            if (lodash.isObject(v)) {
                sortKeys(v)
            }
        }
    }
}

fun sortKeys(o: dynamic) {
    if (lodash.isArray(o)) {
        o.filter(lodash.isObject).forEach(::sortKeys)
    } else if (lodash.isObject(o)) {
        var pairs = lodash.toPairs(o)
        pairs = lodash.sortBy(pairs, {x: dynamic -> x[0]})
        pairs.forEach {arg: dynamic ->
            val k = arg[0]
            val v = arg[1]
            jsDeleteKey(o, k)
        }
        pairs.forEach {arg: dynamic ->
            val k = arg[0]
            val v = arg[1]
            o[k] = v
            if (lodash.isObject(v)) {
                sortKeys(v)
            }
        }
    }
}

fun findContributionsByControlAndChildren(target: WithElementID): Map<String, String> {
    val contributions = mutableMapOf<String, String>()
    art.stateContributionsByControl[target]?.let {contributions.putAll(it)}
    Shitus.byid(target.elementID).find('*').each({
        if (js("this").id) {
            val controls = Shitus.elementIDToControls[js("this").id] ?: jsArrayOf()
            for (control in jsArrayToListOfDynamic(controls)) {
                art.stateContributionsByControl[control]?.let {contributions.putAll(it)}
            }
        }
    })
    return contributions
}

fun makeHrundels(def: dynamic): dynamic {
    val controls: dynamic = def.controls
    val borderColor: dynamic = def.borderColor
    val normalBorderWidth: dynamic = def.normalBorderWidth
    val thickBorderWidth: dynamic = def.thickBorderWidth
    val hoverStyleString: dynamic = def.hoverStyleString
    val onClick: dynamic = def.onClick
    val onMouseEnter: dynamic = def.onMouseEnter
    val onLens: dynamic = def.onLens

    val clazz = "makeHrunderls-${puid()}"

    val divs: dynamic = js("[]")
    val possToStyles = js("({})")
    for (control in jsArrayToListOfDynamic(controls)) {
        // dlog('co', control.debugDisplayName)
        val jqel = Shitus.byid(control.elementID)
        val ofs = jqel.offset()
        var width = jqel.outerWidth(true) // + 10
        var height = jqel.outerHeight(true) // + 10
        var left = ofs.left // - 5
        var top = ofs.top // - 5

        val poss = "${left}:${top}:${width}:${height}"
        for (style in jsArrayToListOfDynamic(possToStyles[poss] ?: jsArrayOf())) {
            // style.left -= 5; style.top -= 5; style.width += 10; style.height += 10
            style.width += 10
            style.borderRight = "${thickBorderWidth}px solid ${borderColor}"
        }

        val style = json(
            "position" to "absolute",
            "zIndex" to Shitus.topZIndex++,
            "left" to left,
            "top" to top,
            "width" to width,
            "height" to height,
            "border" to "${normalBorderWidth}px solid ${borderColor}"
        )
        if (!possToStyles[poss]) possToStyles[poss] = jsArrayOf()
        possToStyles[poss].push(style)

        divs.push(Shitus.diva(json(
            "className" to "${clazz} showOnParentHovered-parent",
            "style" to style,
            "onMouseEnter" to {
                Shitus.fov(onMouseEnter, json("control" to control))
            },
            "onClick" to {
                Shitus.fov(onClick, json("control" to control))
            }),

            onLens && Shitus.diva(json("className" to "showOnParentHovered"),
                Shitus.diva(json("style" to json("position" to "absolute", "right" to 0, "top" to -20, "height" to 40, "width" to 40, "textAlign" to "right")),
                    Shitus.spana(json("className" to "fa fa-search", "style" to json("cursor" to "pointer"),
                        "onClick" to {e: KeyboardEvent ->
                            preventAndStop(e)
                            onLens(json("control" to control))
                        }
                    ))
                )
            )
        ))
    }

    return Shitus.diva.apply(null, js("[]").concat(
        json(),
        rawHTML("""
            <style>
                    .${clazz}:hover {${hoverStyleString}}
            </style>"""),
        /*...*/divs
    ))
}

fun isInTestScenario(): Boolean {
    return hrss.currentTestScenario != null
}

fun renderStacks(def: dynamic): dynamic {
    return "Implement renderStacks, please, fuck you"

}

fun openDebugPane(def: dynamic) {
    imf("openDebugPane")
}

class ArtFuckingError(message: String, val detailsUI: ToReactElementable): Exception(message)

class ArtAssertionError(
    message: String,
    visualPayload: ToReactElementable? = null
) : FatException(message, visualPayload=visualPayload)


fun renderCurrentTestScenarioDetails(marginTop: Int = 0) = kdiv{o->
    val scenario = hrss.currentTestScenario!!

    o- scenario.longDescription?.let {markdown(dedent(it))}
    o- kdiv(marginTop=marginTop) {it-art.renderStepDescriptions()}
}













