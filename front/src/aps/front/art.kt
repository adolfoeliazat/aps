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
import jquery.jq
import kotlin.browser.document
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
    lateinit var testSpeed: String
    var stateContributionsByControl: dynamic = null
    val halted: Boolean = false
    var respectArtPauses: Boolean = false
    var stepDescriptions: dynamic = jsArrayOf()
    val uiStateContributions: dynamic = json()
    var actionPlaceholderTag: String? = null
    var testInstructions: Iterable<TestInstruction> by HotReloadSurvivingFuckingShit("art_testInstructions")

    fun uiState(def: dynamic): Promise<Unit> {"__async"
        val url: String = global.location.href // Capture location for reassertion on hot reload, as it will be changed to "/test.html..."

        hrss.reassertUIState = {arg: dynamic -> "__async"
            val scrollThere: dynamic = arg.scrollThere
            __await<dynamic>(debugCheckEmail())

//            val actual = json("url" to url)
            val actual = mutableMapOf<String, Any>("url" to url)
            invokeStateContributions(actual)
            gertrude(global.Object.assign(def, json("actual" to actual, "scrollThere" to scrollThere)))
        }

        __await<dynamic>(hrss.reassertUIState(json("scrollThere" to true)))
        return __asyncResult(Unit)
    }


    fun run(instructions: Iterable<TestInstruction>): Promise<Unit> {"__async"
        testInstructions = instructions

        val urlq = getURLQuery()
        val until = urlq["until"]?.let {parseInt(it)} ?: Int.MAX_VALUE
        val from = urlq["from"] ?: "start"

        var skipping = from != "start"

        var stepIndex = 0
        instructions.forEachIndexed {instrIndex, instr ->
            if (instrIndex == until) {
                dlog("Stopping test before instruction ${instrIndex}")
                return __asyncResult(Unit)
            }

            if (instr is TestInstruction.WorldPoint) {
                val wpname: String = hrss.currentTestScenarioName + " -- " + instr.name
                if (skipping) {
                    if (instr.name == from) {
                        dlog("Restoring world point ${wpname}")
                        __await(WorldPointRequest.send(wpname, RESTORE))
                        skipping = false
                    }
                } else {
                    dlog("Saving world point ${wpname}")
                    __await(WorldPointRequest.send(wpname, SAVE))
                }
            }
            else if (!skipping) {
                fun getControlForAction(arg: dynamic): dynamic {
                    if (instr !is ShamedTestInstruction) throw JSException("I want ShamedTestInstruction, got $instr")

                    val implementing = if (arg) arg.implementing else undefined

                    val control = TestGlobal.shameToControl[instr.shame]
                    if (!control) Shitus.raiseWithMeta(json("message" to "Control shamed ${instr.shame} is not found", "meta" to instr))
                    if (implementing && !control[implementing]) Shitus.raiseWithMeta(json("message" to "Control shamed ${instr.shame} is expected to implement ${implementing}", "meta" to instr))
                    return control
                }

                fun executeSetValueLike(): Promise<Unit> {"__async"
                    val instr = instr as TestInstruction.Action
                    val control = getControlForAction(json("implementing" to "testSetValue"))
                    if (instr.timestamp.there) {
                        __await(ImposeNextRequestTimestampRequest.send(instr.timestamp))
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
                            __await(ImposeNextRequestTimestampRequest.send(instr.timestamp))
                        }
                        __await<dynamic>(control.testClick(instr))
                    }
                    is TestInstruction.KeyDown -> {
                        val control = getControlForAction(json("implementing" to "testKeyDown"))
                        if (instr.timestamp.there) {
                            __await(ImposeNextRequestTimestampRequest.send(instr.timestamp))
                        }
                        __await<dynamic>(control.testKeyDown(instr))
                    }
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


    fun renderStepDescriptions(): ReactElement {
        val testInstructions = art.testInstructions
        val els = mutableListOf<ReactElement>()

        var stepIndex = 0; var indent = 0
        testInstructions.forEachIndexed {instrIndex, instr ->

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
                val untilParamValue = if (instrIndex == art.stepDescriptions.length - 1) "infinity" else instrIndex

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

        return jdiva(json("controlTypeName" to "renderStepDescriptions", "noStateContributions" to true), jdiva(json("style" to json("background" to Color.GRAY_200, "fontWeight" to "bold")), "Steps"),
            *els.toTypedArray())
    }

    fun assert(condition: dynamic, errorMessage: dynamic, _opts: Any? = null) {
        val opts: dynamic = (_opts ?: json())
        val detailsUI: dynamic = opts.detailsUI
        val scrollThere: dynamic = (opts.scrollThere ?: true)

        if (condition) return

        val assertionErrorPane = object : Control2(A()) {
            override fun defaultControlTypeName() = "assertionErrorPane"

            var visible = false
            var content: dynamic = null

            override fun render(): ReactElement {
                if (!visible) return NORE

                val messageStyle = json("fontWeight" to "bold", "paddingBottom" to 5, "marginBottom" to 5, "color" to WHITE.toString())
                if (content.stack) {
                    global.Object.assign(messageStyle, json(
                        "borderBottom" to "2px solid white")
                    )
                }
                return Shitus.diva(json("style" to json("backgroundColor" to RED_700, "marginTop" to 10, "padding" to "10px 10px", "textAlign" to "left")),
                    Shitus.diva(json("style" to messageStyle), content.message),
                    content.stack && Shitus.diva(json("style" to json("whiteSpace" to "pre-wrap", "color" to WHITE.toString())), content.stack),
                    content.detailsUI
                )
            }

            fun set(def: dynamic) {
                val scrollThere: dynamic = def.scrollThere
                content = def
                visible = true
                update()

                if (scrollThere && !hrss.preventScrollToBottomOnAssertionError) {
                    global.requestAnimationFrame({
                        global.document.body.scrollTop = js("$")("#debug_assertionErrorPane").offset().top - 60
                        Unit
                    })
                }
            }
        }

//        val assertionErrorPane_ = Shitus.statefulElement(ctor@{update: dynamic ->
//            var visible: dynamic = null
//            var content: dynamic = null
//            var top: dynamic = null
//
//            val me: dynamic = json(
//                "render" to render@{
//                    if (!visible) return@render null
//
//                    val messageStyle = json("fontWeight" to "bold", "paddingBottom" to 5, "marginBottom" to 5, "color" to WHITE.toString())
//                    if (content.stack) {
//                        global.Object.assign(messageStyle, json(
//                            "borderBottom" to "2px solid white")
//                        )
//                    }
//                    return@render Shitus.diva(json("style" to json("backgroundColor" to RED_700, "marginTop" to 10, "padding" to "10px 10px", "textAlign" to "left")),
//                        Shitus.diva(json("style" to messageStyle), content.message),
//                        content.stack && Shitus.diva(json("style" to json("whiteSpace" to "pre-wrap", "color" to WHITE.toString())), content.stack),
//                        content.detailsUI
//                    )
//                },
//
//                "set" to {def: dynamic ->
//                    val scrollThere: dynamic = def.scrollThere
//                    content = def
//                    visible = true
//                    update()
//
//                    if (scrollThere && !hrss.preventScrollToBottomOnAssertionError) {
//                        global.requestAnimationFrame({
//                            global.document.body.scrollTop =  js("$")("#debug_assertionErrorPane").offset().top - 60
//                            Unit
//                        })
//                    }
//                }
//            )
//
//            return@ctor me
//        })

        val existingDiv = Shitus.byid("debug_assertionErrorPane")
        if (existingDiv[0]) {
            global.ReactDOM.unmountComponentAtNode(existingDiv[0])
            existingDiv.remove()
        }
        Shitus.byid("footer").after("<div id='debug_assertionErrorPane'></div>")
        global.ReactDOM.render(assertionErrorPane.toReactElement(), Shitus.byid0("debug_assertionErrorPane"))

        val stack = null
        assertionErrorPane.set(json("message" to errorMessage + mdash + hrss.currentTestScenarioName, "stack" to stack, "detailsUI" to detailsUI, "scrollThere" to scrollThere))
        if (!hrss.preventUIAssertionThrowing) Shitus.raise("UI assertion failed")
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
        //export function showTestActionHand({target, pointingFrom='bottom', dleft=0, dtop=0, noBlinking}={}) {
        //    const targetOffset = target.offset()
        //    const targetWidth = target.outerWidth(true)
        //    const targetHeight = target.outerHeight(true)
        //    const paneID = 'testIndicatorOfLinkToClick-' + puid()
        //    debugPanes.set({name: paneID, element: updatableElement(s{}, update => {
        //        let glyph, left, top
        //        if (pointingFrom === 'bottom') {
        //            const handWidth = 24
        //            glyph = 'fa-hand-o-up'
        //            left = targetOffset.left + targetWidth / 2 - handWidth / 2 + dleft
        //            top = targetOffset.top + targetHeight + 10 + dtop
        //        } else if (pointingFrom === 'top') {
        //            const handWidth = 24, handHeight = 28
        //            glyph = 'fa-hand-o-down'
        //            left = targetOffset.left + targetWidth / 2 - handWidth / 2 + dleft
        //            top = targetOffset.top - 10 - handHeight + dtop
        //        } else if (pointingFrom === 'right') {
        //            const handWidth = 28, handHeight = 28
        //            glyph = 'fa-hand-o-left'
        //            left = targetOffset.left + targetWidth + 10 + dleft
        //            top = targetOffset.top + targetHeight / 2 - handHeight / 2 + dtop
        //        } else if (pointingFrom === 'left') {
        //            const handWidth = 28, handHeight = 28
        //            glyph = 'fa-hand-o-right'
        //            left = targetOffset.left - handWidth - 10 + dleft
        //            top = targetOffset.top + targetHeight / 2 - handHeight / 2 + dtop
        //        } else {
        //            Shitus.raise(`Weird pointingFrom: ${pointingFrom}`)
        //        }
        //
        //        let clazz = 'aniBlinkingFast'
        //        if (noBlinking) clazz = ''
        //
        //        if (pointingFrom === 'right') {
        //            return _=> Shitus.diva({id: 'testActionHand', className: clazz, style: {zIndex: 100000, position: 'absolute', left, top}},
        //                Shitus.diva({style: {position: 'relative', width: '2em', width: '2em'}},
        //                    ia({className: `fa fa-2x ${glyph}`, style: {color: WHITE, position: 'absolute', left: '0em', top: '0em', transform: 'scale(0.9)'}}),
        //                    ia({className: `fa fa-2x ${glyph}`, style: {color: WHITE, position: 'absolute', left: '0em', top: '0em', transform: 'scale(0.8)'}}),
        //                    ia({className: `fa fa-2x ${glyph}`, style: {color: WHITE, position: 'absolute', left: '0em', top: '0em', transform: 'scale(0.7)'}}),
        //                    ia({className: `fa fa-2x ${glyph}`, style: {color: WHITE, position: 'absolute', left: '0em', top: '0em', transform: 'scale(0.6)'}}),
        //                    ia({className: `fa fa-2x ${glyph}`, style: {color: WHITE, position: 'absolute', left: '0em', top: '0em', transform: 'scale(0.55)'}}),
        //                    ia({className: `fa fa-2x fa-circle`, style: {color: WHITE, position: 'absolute', left: '0.04em', top: '0em', transform: 'scale(0.5)'}}),
        //                    ia({className: `fa fa-2x fa-circle`, style: {color: WHITE, position: 'absolute', left: '0.07em', top: '0.03em', transform: 'scale(0.5)'}}),
        //                    ia({className: `fa fa-2x ${glyph}`, style: {color: BROWN_500, position: 'absolute', left: '0em', top: '0em'}}),
        //                    ))
        //        } else if (pointingFrom === 'bottom') {
        //            return _=> Shitus.diva({id: 'testActionHand', className: clazz, style: {zIndex: 100000, position: 'absolute', left, top}},
        //                Shitus.diva({style: {position: 'relative', width: '2em', width: '2em'}},
        //                    ia({className: `fa fa-2x ${glyph}`, style: {color: WHITE, position: 'absolute', left: '0em', top: '0em', transform: 'scale(0.9)'}}),
        //                    ia({className: `fa fa-2x ${glyph}`, style: {color: WHITE, position: 'absolute', left: '0em', top: '0em', transform: 'scale(0.8)'}}),
        //                    ia({className: `fa fa-2x ${glyph}`, style: {color: WHITE, position: 'absolute', left: '0em', top: '0em', transform: 'scale(0.7)'}}),
        //                    ia({className: `fa fa-2x ${glyph}`, style: {color: WHITE, position: 'absolute', left: '0em', top: '0em', transform: 'scale(0.6)'}}),
        //                    ia({className: `fa fa-2x ${glyph}`, style: {color: WHITE, position: 'absolute', left: '0em', top: '0em', transform: 'scale(0.55)'}}),
        //                    ia({className: `fa fa-2x fa-circle`, style: {color: WHITE, position: 'absolute', left: '0.01em', top: '-0.05em', transform: 'scale(0.5)'}}),
        //                    ia({className: `fa fa-2x fa-circle`, style: {color: WHITE, position: 'absolute', left: '0.07em', top: '0.03em', transform: 'scale(0.5)'}}),
        //                    ia({className: `fa fa-2x ${glyph}`, style: {color: BROWN_500, position: 'absolute', left: '0em', top: '0em'}}),
        //                    ))
        //        } else {
        //            return _=> ia({id: 'testActionHand', className: `${clazz} fa fa-2x ${glyph}`, style: {color: BROWN_500, zIndex: 100000, position: 'absolute', left, top}})
        //        }
        //    })})
        //
        //    return {
        //        delete() {
        //            if (!tinkeringWithTestActionHand) {
        //                debugPanes.delete({name: paneID})
        //            } else {
        //                haltTheWorld()
        //
        //                let movingHand, startScreenX, startScreenY, dx, dy, startOffset
        //
        //                document.addEventListener('mousemove', e => {
        //                    if (movingHand) {
        //                        if (!e.ctrlKey) {
        //                            movingHand = false
        //                            dleft = dleft + dx
        //                            dtop = dtop + dy
        //                            return
        //                        }
        //
        //                        dx = e.screenX - startScreenX
        //                        dy = e.screenY - startScreenY
        //                        Shitus.byid('testActionHand').offset({left: startOffset.left + dx, top: startOffset.top + dy})
        //                        dlogs({dleft: dleft + dx, dtop: dtop + dy})
        //                    } else {
        //                        if (e.ctrlKey) {
        //                            movingHand = true
        //                            startScreenX = e.screenX
        //                            startScreenY = e.screenY
        //                            startOffset = Shitus.byid('testActionHand').offset()
        //                            return
        //                        }
        //                    }
        //                })
        //            }
        //        }
        //    }
        //}
    }

    fun initDebugFunctionsShit() {
        global.window.removeEventListener("unhandledrejection", hrss.onUnhandledRejection)
        hrss.onUnhandledRejection = { event: dynamic ->
            if (!hrss.preventExceptionRevelation && event.reason.message != "UI assertion failed") {
                console.error(event.reason.message)
                Shitus.revealStack(json("exception" to event.reason))
            }
        }
        global.window.addEventListener("unhandledrejection", hrss.onUnhandledRejection)

        DebugPanes.put("initDebugFunctions-shit", oldShitAsReactElementable(Shitus.updatableElement(json(), paneCtor@{ updateShit: dynamic ->
            var shitVisible = false
            var shitToRender: dynamic= null

            global.removeEventListener("keydown", hrss.debugFunctionsKeydownListener)
            hrss.debugFunctionsKeydownListener = { e: dynamic ->
                if (e.code == "Backquote") {
                    e.preventDefault()
                    e.stopPropagation()

                    shitVisible = !shitVisible
                    if (shitVisible) {
                        global.lightStateContributions()
                    } else {
                        shitToRender = null
                    }

                    updateShit()
                }
            }
            global.addEventListener("keydown", hrss.debugFunctionsKeydownListener)

//            Object.assign(testGlobal, {
//                printCurrentStamp() {
//                    clog(moment.tz("UTC").format("YYYY/MM/DD HH:mm:ss"))
//                },
//            })

//            global.hideSomeAssertionDiffKeys = function({includesAny=[]}={}) {
//                global.assertionPane.setLineHideFilter(lineValue => {
//                    for (const includes of includesAny) {
//                    if (lineValue.includes(includes)) return true
//                }
//                })
//            }

            global.lightStateContributions = lightStateContributions@{
                console.log("Lighting state contributions")

                shitToRender = makeHrundels(json(
                    "controls" to global.Array.from(Shitus.getArtStateContributionsByControl().keys()),
                    "borderColor" to PURPLE_500.toString(),
                    "normalBorderWidth" to 1,
                    "thickBorderWidth" to 3,
                    "hoverStyleString" to "background: rgba(156, 39, 176, 0.1);",
                    "onClick" to {arg: dynamic ->
                        val control = arg.control
                        dumpContributionsByControlAndChildren(control, js("({})"))
                    },
                    "onLens" to {arg: dynamic ->
                        val control = arg.control
                        val contribs = Shitus.getArtStateContributionsByControl().get(control)
                        global.assertionPane.highlightStuff(json("keys" to global.Object.keys(contribs), "scrollThere" to true))
                    },
                    "onMouseEnter" to {arg: dynamic ->
                        val control = arg.control
                        console.log("--------------------")
                        console.log("CONTRIBS: ${controlDisplayNameForDumping(control)}")
                        console.log("--------------------")
                        console.log(global.nodeUtil.inspect(Shitus.getArtStateContributionsByControl().get(control)))
                    }
                ))

                updateShit()
            }

//            global.dumpStateContributionKeysShortestToLongest = function({unifyIndices}={}) {
//                const actual = {}
//                art.invokeStateContributions({actual})
//                let items = Object.keys(actual)
//                if (unifyIndices) {
//                    items = items.map(x => x.replace(/-i\d\d\d/g, "-ω")) // omega
//                }
//                items = sortBy(items, x => x.length)
//                items = uniq(items)
//                console.log("===================================================")
//                console.log("Contribution keys, shortest key to longest:")
//                console.log(global.nodeUtil.inspect(items))
//                console.log("title", actual.title)
//            }

            return@paneCtor {shitToRender}
        })))

        initDebugMailbox()
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
        hrss.artLastSuccessfulExpected = expected
        return
    }

    var detailsUI: dynamic = null
    if (hrss.urlQueryBeforeRunningTest.minimalGertrude == "yes" || TestGlobal.minimalGertrude) {
        detailsUI = div { styleKludge = json("background" to Color.WHITE); -"I am minimal because of minimalGertrude" }
//        detailsUI = Shitus.diva(json("style" to json("background" to jshit.WHITE)), t("I am minimal because of minimalGertrude"))
    } else {
        detailsUI = Shitus.updatableElement(js("({})"), wholeShitCtor@ {updateWholeShit ->
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
                    val key = Shitus.trim(valueLine.slice(0, colonIndex).replace(js("/'/g"), ""))
                    if (extenderKeys.includes(key)) {
                        return true
                    }
                    return false
                }



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
                val lastExpectedString = if (hrss.artLastSuccessfulExpected != null) repr(hrss.artLastSuccessfulExpected, json("stripIndices" to stripFuckingIndices)) else "--- Nothing ---"
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
                        for ((i, valueLine) in jsArrayToList(valueLines).withIndex()) {
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
                                                Shitus.revealControl(control, json("scrollToTarget" to true))
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

                tabs = Tabs(json(
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
                            TestGlobal.minimalGertrude = true
                            callDebugRPWithProgress(json("msg" to json("fun" to "danger_updateAssertionCode", "assertionTag" to tag, "actualStringForPasting" to actualStringForPasting), "progressPlaceholder" to progressPlaceholder, "progressTitle" to "Updating assertion code"))
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

        })
    }

    art.assert(false, descr, json("scrollThere" to scrollThere, "detailsUI" to detailsUI))
}

@native interface LegacyControl
@native interface LegacyDefinitionStack
@native interface LegacyCallStack

//val stateContributionsByControl = mutableMapOf<LegacyControl, MutableMap<String, dynamic>>()

@native class TestUIStateContribution(
    val value: String,
    val control: LegacyControl,
    val definitionStack: LegacyDefinitionStack,
    val callStack: LegacyCallStack
)

fun invokeStateContributions(actual: MutableMap<String, Any>?) {
    // {actual}={}
//    val actual = if (arg) arg.actual else undefined

    art.stateContributionsByControl = js("new Map()")

    for (contribute in jsArrayToList(Shitus.values(art.uiStateContributions))) {
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

                    Shitus.raiseWithMeta(json(
                        "message" to message,
                        "metaItems" to jsArrayOf(
                            json("titlePrefix" to "This", "meta" to control),
                            json("titlePrefix" to "Existing", "meta" to actual!![key].asDynamic().control))))
                }

                if (control) {
                    var contributions = art.stateContributionsByControl.get(control)
                    if (!contributions) {
                        contributions = js("({})")
                        art.stateContributionsByControl.set(control, contributions)
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
//                    Shitus.raiseWithMeta(json(
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
    val scenario: Any = def.scenario

//    val testPassedPane = Shitus.statefulElement({update ->
//        var scenarioName: String = scenario.name
//        val links = mutableListOf<ReactElement>()
//
//        val m = global.RegExp("\\s+([0-9a-z]{8})-([0-9a-z]{4})-([0-9a-z]{4})-([0-9a-z]{4})-([0-9a-z]{12})$").exec(scenarioName)
//        if (m != undefined) {
//            scenarioName = scenarioName.substring(0, m.index)
//            links.add(OpenSourceCodeLink(json("where" to json("\$tag" to m[0].trim()), "style" to json("color" to Color.WHITE))))
//        }
//        if (art.actionPlaceholderTag != undefined) {
//            links.add(marginateLeft(10, OpenSourceCodeLink(json("where" to json("\$tag" to art.actionPlaceholderTag), "style" to json("color" to Color.WHITE)))))
//        }
//        val uq = hrss.urlQueryBeforeRunningTest
//        if (!uq.scrollToBottom || uq.scrollToBottom == "yes" || uq.scrollToBottom == "success") {
//            kotlin.browser.window.requestAnimationFrame { kotlin.browser.document.body?.scrollTop = 99999 }
//        }
//
//        val me: dynamic = json(
//            "render" to {
//                when {
//                    scenarioName == undefined -> null
//                    else -> div {
//                        noStateContributions = true
//                        style {
//                            backgroundColor = Color.GREEN_700; color = Color.WHITE
//                            marginTop(10); padding = "10px 10px"; textAlign = "center"; fontWeight = "bold"
//                        }
//
//                        -div {
//                            style { paddingBottom(10) }
//                            -scenarioName
//                            -div {
//                                style { display = "flex"; justifyContent = "center" }
//                                +links
//                            }
//                        }
//
//                        -div {
//                            style { backgroundColor = Color.WHITE; color = Color.BLACK_BOOT; fontWeight = "normal"; textAlign = "left"; padding(5) }
//                            -art.renderStepDescriptions()
//                        }
//                    }
//                }
//            }
//        )
//
//        me
//    })

    DebugPanes.put("openTestPassedPane", Shitus.byid("underFooter"), kdiv(A(noStateContributions=true)){o->
        o-Style(backgroundColor=Color.GREEN_700, color=Color.WHITE,
                marginTop=10, padding="10px 10px", textAlign="center", fontWeight="bold")

        o-kdiv(Style(paddingBottom=10)){o->
            o-constructorName(scenario)
        }

        o-kdiv{o->
            o-Style(backgroundColor=Color.WHITE, color = Color.BLACK_BOOT,
                    fontWeight="normal", textAlign="left", padding=5)
            o-art.renderStepDescriptions()
        }
    })

    requestAnimationFrame {
        jqbody.scrollTop(jq("#underFooter").offset().top - 60)
    }

//        DebugPanes.put("openTestPassedPane", Shitus.byid("underFooter"), oldShitAsReactElementable(Shitus.spana(json(), testPassedPane.element)))
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
        for (pair in jsArrayToList(pairs)) {
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
            jsFacing_deleteKey(o, k)
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
            jsFacing_deleteKey(o, k)
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

fun findContributionsByControlAndChildren(target: dynamic): dynamic {
    val contributions: dynamic = json()
    global.Object.assign(contributions, art.stateContributionsByControl.get(target))
    Shitus.byid(target.elementID).find('*').each({
        if (js("this").id) {
            val controls = Shitus.elementIDToControls[js("this").id] ?: jsArrayOf()
            for (control in jsArrayToList(controls)) {
                global.Object.assign(contributions, art.stateContributionsByControl.get(control))
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
    for (control in jsArrayToList(controls)) {
        // dlog('co', control.debugDisplayName)
        val jqel = Shitus.byid(control.elementID)
        val ofs = jqel.offset()
        var width = jqel.outerWidth(true) // + 10
        var height = jqel.outerHeight(true) // + 10
        var left = ofs.left // - 5
        var top = ofs.top // - 5

        val poss = "${left}:${top}:${width}:${height}"
        for (style in jsArrayToList(possToStyles[poss] ?: jsArrayOf())) {
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
                        "onClick" to {e: dynamic ->
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
        rawHtml("""
            <style>
                    .${clazz}:hover {${hoverStyleString}}
            </style>"""),
        /*...*/divs
    ))
}

fun isInTestScenario(): Boolean {
    return hrss.currentTestScenarioName != null
}

fun renderStacks(def: dynamic): dynamic {
    return "Implement renderStacks, please, fuck you"

//export function renderStacks(def) {
//    #extract {definitionStack, callStack} from def
//
//    return updatableElement(s{}, update => {
//        let thinking = true
//
//        Shitus.run(async function() {
//            if (callStack) {
//                if (callStack instanceof Error) {
//                    const stackString = await Shitus.errorToMappedClientStackString(callStack)
//                    callStack = []
//                    for (const line of stackString.split('\n')) {
//                        if (line.startsWith('    at ') && !line.includes('__awaiter')) {
//                            // dlog('Stack line', line)
//                            const m = line.match(/(.*?)\(([^()]+?:\d+:\d+)\)/)
//                            if (m) {
//                                callStack.push({title: m[1].trim(), location: m[2]})
//                            }
//                        }
//                    }
//                    callStack.shift() // Remove stack capturing function
//                }
//                if (!isArray(callStack)) Shitus.raise('I want callStack to be an array or Error')
//            }
//
//            update(thinking = false)
//        })
//
//        return _=> {
//        const ctn = 'renderStacks'
//        if (thinking) return Shitus.spana({ctn, style: {fontStyle: 'italic'}}, t('Thinking deeply...'))
//
//        if (definitionStack && !definitionStack.length) definitionStack = undefined
//        if (callStack && !callStack.length) callStack = undefined
//        if (!(definitionStack || callStack)) return Shitus.spana({ctn, style: {fontStyle: 'italic'}}, t('No fucking stacks'))
//
//        return Shitus.diva({controlTypeName: 'renderStacks', style: {display: 'flex'}},
//            definitionStack && Shitus.diva({},
//                Shitus.diva({style: {fontWeight: 'bold', textDecoration: ''}}, t('Definitions:')),
//                ...definitionStack.map(stackItem => OpenSourceCodeLink({where: {$sourceLocation: stackItem.loc}}))),
//
//        callStack && Shitus.diva({style: {marginLeft: 15}},
//            Shitus.diva({style: {fontWeight: 'bold', textDecoration: ''}}, t('Locations:')),
//            ...callStack.map(item => Shitus.diva({style: {display: 'flex'}},
//        Shitus.diva({style: {marginRight: 10}}, item.title),
//        OpenSourceCodeLink({where: {$sourceLocation: item.location}})))),
//        )
//    }
//    })
//}
}

fun openDebugPane(def: dynamic) {
    imf("openDebugPane")
}






