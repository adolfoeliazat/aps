/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import aps.Color.*
import aps.front.testutils.*
import into.kommon.*
import jquery.JQuery
import jquery.jq
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.window
import kotlin.properties.Delegates.notNull

private var pausedOnAssertion = ResolvableShit<Unit>()
private var assertionBannerPause by notNull<ResolvableShit<Unit>>()
private var _currentAssertionBannerKind by notNull<AssertionBannerKind>()

val currentAssertionBannerKind get() = _currentAssertionBannerKind

enum class AssertionBannerKind(val className: String) {
    NOT_HARDENED(css.test.popup.assertion.notHardened),
    CORRECT(css.test.popup.assertion.correct),
    INCORRECT(css.test.popup.assertion.incorrect)
}

fun buildAndRunTestScenario(showTestPassedPane: Boolean, block: (TestScenarioBuilder) -> Unit): Promise<Throwable?> = async {
    val builder = TestScenarioBuilder()
    block(builder)
    await(builder.runScenario(showTestPassedPane))
}

val transformNavbarLineTidy = {it: String ->
    it.replace(Regex(" id=\"MakeStaticSites-\\d+\""), "")
}

val transformRootLineTidy = {it: String ->
    it
}

class TestScenarioBuilder {
    val instructions = mutableListOf<TestInstruction>()

    fun runScenario(showTestPassedPane: Boolean): Promise<Throwable?> = async {
        await(art.run(instructions, showTestPassedPane))
    }

    fun state(descr: String) {
        instructions.add(TestInstruction.Step.StateStep(descr))
    }

    fun assert(test: Boolean, descr: String) {
        val step = TestInstruction.Step.AssertionStep(descr)
        instructions.add(step)
        act {
            step.passed = test
            if (!step.passed) art.fail(descr)
        }
    }

    fun act(descr: String? = null, block: () -> Unit) {
        acta(descr, {async{
            block()
        }})
    }

    fun <T> acta(descr: String? = null, block: () -> Promise<T>) {
        var step: TestInstruction.Step? = null
        if (descr != null) {
            step = TestInstruction.Step.ActionStep(descr)
            instructions.add(step)
        }

        instructions.add(TestInstruction.Do {"__async"
            __await(block())
            step?.passed = true
            __asyncResult(Unit)
        })
    }

    fun <T> await(block: () -> Promise<T>) {
        acta(null, block)
    }

    fun assertVisibleText(expected: String, under: CSSSelector = "body") {
        assertOnAnimationFrame("Page should contain in $under: _${expected}_", {
            val actual = visibleText(under)
            actual.contains(expected)
        })
    }

    fun assertNoVisibleText(expected: String, under: CSSSelector = "body") {
        assertOnAnimationFrame("Page should not contain in $under: ~~_${expected}_~~", {
            val actual = visibleText(under)
            // dwarnStriking(actual)
            !actual.contains(expected)
        })
    }

    private fun visibleText(under: CSSSelector) = jq("$under *:not(:has(*)):visible").text()

    fun assertOnAnimationFrame(stepTitle: String, test: () -> Boolean) {
        checkOnAnimationFrame(stepTitle) {async{
            if (!test()) throw ArtAssertionError(stepTitle)
        }}
    }

    fun checkOnAnimationFrame(stepTitle: String, block: () -> Promise<Unit>) {
        val step = TestInstruction.Step.AssertionStep(stepTitle)
        instructions.add(step)
        acta {async{
            await(tillAnimationFrame())
            await(block())
            step.passed = true
        }}
    }

    fun halt() {
        val step = TestInstruction.Step.HaltStep("Fucking halt")
        instructions.add(step)
        act {
            throw FatException("Fucking halted", visualPayload = kdiv{o->
                fun makeTab(title: String, paste: String) = TabSpec(
                    id = title,
                    title = title,
                    content = kdiv{o->
                        o- Input(json("initialValue" to paste,
                                      "kind" to "textarea",
                                      "rows" to 10,
                                      "style" to json("width" to "100%",
                                                      "height" to "100%"),
                                      "untested" to true))
                    })

                o- Tabs2(initialActiveID = "Navbar Paste", tabs = listOf(
                    makeTab("Navbar Paste", "o.assertNavbarHTML(\"\"\"${takeHTMLForAssertion(SELECTOR_NAVBAR)}\"\"\")"),
                    makeTab("Root Paste", "o.assertRootHTML(\"\"\"${takeHTMLForAssertion(SELECTOR_ROOT)}\"\"\")")
                ))
            })
        }
    }

    fun assertHTML(under: CSSSelector, expected: String, transformLine: ((String) -> String)? = null) {
        assertHTML(inside=under, transformLine=transformLine, expected = {async{expected}})
    }

    fun assertHTML(inside: CSSSelector,
                   expected: () -> Promise<String>,
                   transformLine: ((String) -> String)? = null,
                   descr: String? = null
    ) {
        var stepTitle = "HTML inside `$inside`"
        descr?.let {stepTitle += ": $it"}
        checkOnAnimationFrame(stepTitle) {async{
            val rawActual = takeHTMLForAssertion(inside)
            val tidyActual = tidyHTML(rawActual, transformLine=transformLine)
            val tidyExpected = tidyHTML(await(expected()), transformLine=transformLine)
            if (tidyActual != tidyExpected) {
                throw ArtAssertionError(stepTitle, visualPayload = renderDiff(
                    expected = tidyExpected,
                    actual = tidyActual,
                    actualTestShit = rawActual.trim(),
                    actualPaste = symbols.threeQuotes + rawActual.trim() + symbols.threeQuotes))
            }
        }}
    }

    fun assertNavbarHTML(expected: String) {
        assertHTML(under = SELECTOR_NAVBAR,
                   expected = expected,
                   transformLine = transformNavbarLineTidy)
    }

    fun assertNavbarHTMLExt(descr: String?, id: String) {
        act {TestGlobal.testShitBeingAssertedID = id}
        assertHTML(inside = SELECTOR_NAVBAR,
                   expected = {async{await(fuckingRemoteCall.loadTestShit(id)) ?: "--- kill me ---"}},
                   transformLine = transformNavbarLineTidy,
                   descr=descr)
        act {TestGlobal.testShitBeingAssertedID = null}
    }

    fun assertRootHTML(expected: String) {
        assertHTML(under = SELECTOR_ROOT, expected = expected, transformLine = {it})
    }

    fun assertRootHTMLExt(descr: String?, id: String) {
        act {TestGlobal.testShitBeingAssertedID = id}
        assertHTML(inside = SELECTOR_ROOT, expected = {async{await(fuckingRemoteCall.loadTestShit(id)) ?: "--- kill me ---"}}, transformLine = {it}, descr=descr)
        act {TestGlobal.testShitBeingAssertedID = null}
    }

    fun assertScreenHTML(descr: String?, assertionID: String) {
        act {TestGlobal.testShitBeingAssertedID = assertionID}

        val stepTitle = "HTML: $descr"
        checkOnAnimationFrame(stepTitle) {async<Unit>{
            val expected = await(fuckingRemoteCall.loadTestShit(assertionID))

            val actual = buildString {
                append("-------------------- NAVBAR --------------------\n\n")
                append(tidyHTML(takeHTMLForAssertion(SELECTOR_NAVBAR), transformNavbarLineTidy))
                if (!endsWith("\n")) append("\n")
                append("\n-------------------- ROOT --------------------\n\n")
                append(tidyHTML(takeHTMLForAssertion(SELECTOR_ROOT), transformRootLineTidy))
            }

            if (TestGlobal.lastTestOpts.stopOnAssertions) {
                await(object {
                    val bannerButtonStyle = Style()

                    var banner by notNull<Control2>()
                    var verticalPosition = VerticalPosition.BOTTOM
                    var horizontalPosition = HorizontalPosition.LEFT
                    var capturedVisualShit = false

                    fun acceptCurrentShit(): Promise<Unit> = async {
                        await(captureVisualShitIfNeeded())
                        await(send(SaveCapturedVisualShitRequest()))
                        await(send(HardenScreenHTMLRequest()-{o->
                            o.assertionID = assertionID
                            o.html = actual
                        }))
                        assertionBannerPause.resolve()
                    }

                    val shit = async {
                        when {
                            expected == null -> {
                                await(showBanner(AssertionBannerKind.NOT_HARDENED))
                            }

                            actual == expected -> {
                                await(showBanner(AssertionBannerKind.CORRECT))
                            }

                            actual != expected -> {
                                val pane = debugPanes.put(byid(ELID_UNDER_FOOTER), kdiv(
                                    id = "fuckingDiff",
                                    backgroundColor = RED_700, color = WHITE, marginTop = 10, padding = "10px 10px",
                                    textAlign = "center", fontWeight = "bold"
                                ){o->
                                    o- kdiv(paddingBottom = 10){o->
                                        o- "Diff"
                                    }

                                    o- kdiv(
                                        backgroundColor = WHITE, color = BLACK_BOOT,
                                        fontWeight = "normal", textAlign = "left", padding = 5
                                    ){o->
                                        o- renderDiff(expected = expected, actual = actual, actualTestShit = actual)
                                    }
                                })

                                try {
                                    await(showBanner(
                                        AssertionBannerKind.INCORRECT,
                                        renderSpecificButtons = {o->
                                            o- Button(title = "Diff", style = bannerButtonStyle, onClick = {
                                                verticalPosition = VerticalPosition.TOP
                                                horizontalPosition = HorizontalPosition.LEFT
                                                banner.update()
//                                                byid("fuckingDiff").scrollBodyToShit()
                                                nextDiff().scrollBodyToShit(dy = -70)
                                            })
                                            o- Button(key = "assertionBanner-vdiff", title = "VDiff", style = bannerButtonStyle, onClicka = {async<Unit>{
                                                openVisualDiff()
                                            }})
                                            o- Button(key = "assertionBanner-accept", icon = fa.check, style = bannerButtonStyle, onClicka = this::acceptCurrentShit)
                                        }))
                                } finally {
                                    debugPanes.remove(pane)
                                }
                            }

                            else -> wtf()
                        }
                    }

                    var diffIndex = -1
                    val diffElements by lazy {
                        val shit = jq(".${css.diff.expected.title}, .${css.diff.actual.title}")
                        check(shit.length > 0)
                        shit
                    }

                    fun nextDiff(): JQuery {
                        if (++diffIndex > diffElements.length - 1) {
                            diffIndex = 0
                        }
                        return jq(diffElements[diffIndex]!!)
                    }

                    inner class openVisualDiff {
                        val mycss = css.test.popup.imageViewer
                        var visualDiffPane by notNull<String>()

                        inner abstract class Mode {
                            abstract val buttonTitle: String

                            val scrollerID: String = puid()
                            var scrollLeft: Double = 0.0
                            var scrollTop: Double = 0.0

                            val view by lazy {
                                val place = Placeholder(kdiv{o->
                                    o- hor2{o->
                                        o- kdiv(marginTop = "0.7rem"){o->
                                            o- "Loading shit..."
                                        }
                                        o- renderTicker(float = null)
                                    }
                                })

                                async {
                                    try {
                                        val base64 = await(promiseBase64())
                                        place.setContent(kdiv(id = scrollerID, style = Style(position = "absolute", width = "100%", height = "100%", overflow = "auto")){o->
                                            val imgURL = "data:image/png;base64,$base64"
                                            o- img2(src = imgURL, style = Style(width = "100%"))
                                        })
                                    } catch (e: dynamic) {
                                        place.setContent(kdiv{o->
                                            o- hor2 {o ->
                                                o- ki(iconClass = fa.frownO)
                                                o- "It didn't work. See your fucking server log..."
                                            }
                                        })
                                    }
                                }

                                return@lazy place
                            }

                            abstract fun promiseBase64(): Promise<String>

                            fun renderButton() = Button(
                                title = buttonTitle,
                                style = bannerButtonStyle.copy(
                                    borderLeft = if (mode != this) null else
                                        "1rem solid $ORANGE_300"
                                ),
                                onClick = {
                                    val scroller = byid0(mode.scrollerID)!!
                                    mode.scrollLeft = scroller.scrollLeft
                                    mode.scrollTop = scroller.scrollTop

                                    mode = this
                                    ctrl.update()

                                    timeoutSet(0) {
                                        byid0(mode.scrollerID)?.let {scroller -> // May be loading, so no scroller yet
                                            scroller.scrollLeft = mode.scrollLeft
                                            scroller.scrollTop = mode.scrollTop
                                        }
                                    }
                                }
                            )
                        }

                        val diffMode = object:Mode() {
                            override val buttonTitle = "Diff"

                            override fun promiseBase64() = async {
                                val res = await(send(DiffCapturedVisualShitWithSavedRequest()-{o->
                                    o.id = assertionID
                                }))
                                res.base64
                            }
                        }

                        val hardenedMode = object:Mode() {
                            override val buttonTitle = "Hardened"

                            override fun promiseBase64() = async {
                                val res = await(send(GetCapturedVisualShitRequest()-{o->
                                    o.id = assertionID
                                }))
                                res.base64
                            }
                        }

                        val currentMode = object:Mode() {
                            override val buttonTitle = "Current"

                            override fun promiseBase64() = async {
                                val res = await(send(GetCurrentCapturedVisualShitRequest()))
                                res.base64
                            }
                        }

                        var mode: Mode = diffMode

                        val ctrl: Control2 by lazy {Control2.from {
                            kdiv(className = mycss.pane){o->
                                o- kdiv(className = mycss.titleBar){o->
                                    o- kdiv(className = mycss.title){o->
                                        o- "Visual Diff"
                                    }
                                    o- hor1(baseStyle = Style(justifyContent = "flex-end")){o->
                                        for (m in listOf(diffMode, hardenedMode, currentMode)) {
                                            o- m.renderButton()
                                        }
                                        o- kdiv(width = "1rem")
                                        o- Button(key = "visualDiffPane-accept", icon = fa.check, title = "Accept", style = bannerButtonStyle, onClick = {
                                            imf()
                                        })
                                        o- Button(icon = fa.close, style = bannerButtonStyle, onClick = {
                                            debugPanes.remove(visualDiffPane)
                                        })
                                    }
                                }
                                o- kdiv(className = mycss.content){o->
                                    o- mode.view
                                }
                            }
                        }}

                        init {
                            async {
                                await(captureVisualShitIfNeeded())
                                visualDiffPane = debugPanes.put(ctrl)
                            }
                        }
                    }

                    fun captureVisualShitIfNeeded() = async {
                        if (!capturedVisualShit) {
                            captureVisualShit(assertionID)
                            capturedVisualShit = true
                        }
                    }


                    fun rerunTestButton() = Button(
                        icon = fa.refresh, style = bannerButtonStyle,
                        onClick = {
                            window.location.href = Globus.realTypedStorageLocal.lastTestURL!!
                        })

                    fun showBanner(kind: AssertionBannerKind, renderSpecificButtons: (ElementBuilder) -> Unit = {}) = async {
                        _currentAssertionBannerKind = kind
                        val className = kind.className
                        assertionBannerPause = ResolvableShit<Unit>()
                        banner = Control2.from {
                            val style = Style()
                            exhaustive/when (verticalPosition) {
                                VerticalPosition.TOP -> style.top = 0
                                VerticalPosition.BOTTOM -> style.bottom = 0
                            }
                            exhaustive/when (horizontalPosition) {
                                HorizontalPosition.LEFT -> style.left = 0
                                HorizontalPosition.RIGHT -> style.right = 0
                            }
                            kdiv(className = className, baseStyle = style){o->
                                o- hor1(marginBottom = "0.5rem"){o->
                                    o- Button(key = "assertionBanner-play", icon = fa.play, style = bannerButtonStyle, onClick = {
                                        assertionBannerPause.resolve()
                                    })
                                    o- Button(icon = fa.bomb, style = bannerButtonStyle, onClick = {
                                        assertionBannerPause.reject(Exception("Fucking killed"))
                                    })
                                    o- Button(
                                        icon = when (verticalPosition) {
                                            VerticalPosition.TOP -> fa.arrowDown
                                            VerticalPosition.BOTTOM -> fa.arrowUp
                                        },
                                        style = bannerButtonStyle,
                                        onClick = {
                                            verticalPosition = when (verticalPosition) {
                                                VerticalPosition.TOP -> VerticalPosition.BOTTOM
                                                VerticalPosition.BOTTOM -> VerticalPosition.TOP
                                            }
                                            banner.update()
                                        })
                                    o- Button(
                                        icon = when (horizontalPosition) {
                                            HorizontalPosition.LEFT -> fa.arrowRight
                                            HorizontalPosition.RIGHT -> fa.arrowLeft
                                        },
                                        style = bannerButtonStyle,
                                        onClick = {
                                            horizontalPosition = when (horizontalPosition) {
                                                HorizontalPosition.LEFT -> HorizontalPosition.RIGHT
                                                HorizontalPosition.RIGHT -> HorizontalPosition.LEFT
                                            }
                                            banner.update()
                                        })
                                    o- rerunTestButton()
                                    renderSpecificButtons(o)
                                }
                                o- "Assertion: $descr"
                            }
                        }
                        val pane = debugPanes.put(banner)

                        fun keyListener(e: Event) {
                            e as KeyboardEvent
                            if (e.key == " ") {
                                assertionBannerPause.resolve()
                            }
                        }
                        window.addEventListener("keydown", ::keyListener)

                        try {
                            pausedOnAssertion.resolve()
                            await(assertionBannerPause.promise)
                        } finally {
                            pausedOnAssertion = ResolvableShit()
                            window.removeEventListener("keydown", ::keyListener)
                            debugPanes.remove(pane)
                        }
                    }
                }.shit)
            } else {
                if (actual != expected) {
                    throw ArtAssertionError(
                        stepTitle,
                        visualPayload = renderDiff(
                            expected = expected ?: "--- Not yet hardened ---",
                            actual = actual,
                            actualTestShit = actual
                        )
                    )
                }
            }
        }}

        act {TestGlobal.testShitBeingAssertedID = null}
    }

    fun assertRootHTMLExt(id: String) {
        assertRootHTMLExt(null, id)
    }

    fun assertUnderFooterHTML(descr: String, expected: String) {
        assertHTML(inside = "#$ELID_UNDER_FOOTER", expected = {Promise.resolve(expected)}, transformLine = {it}, descr=descr)
    }

    fun click(shame: String) {
        instructions.add(TestInstruction.Click(shame))
    }

    fun clickDescribingStep(shame: String) {
        val descr = "Clicking on `$shame`"
        val step = TestInstruction.Step.ActionStep(descr)
        instructions.add(step)
        click(shame)
        act {step.passed = true}
    }

    fun actionStep(descr: String, block: () -> Unit) {
        val step = TestInstruction.Step.ActionStep(descr)
        instructions.add(step)
        block()
        act {step.passed = true}
    }

    fun setValue(shame: String, value: String) {
        acta {async{
            val control = getShamedControl(shame)
            await<dynamic>(control.testSetValue(json("value" to value)))
        }}
    }

    fun getShamedControl(shame: String): dynamic {
        val control = TestGlobal.shameToControl[shame]
        if (control == null) Shitus.raiseWithMeta(json("message" to "Control shamed $shame is not found"))
        return control
    }

    fun setValueDescribingStep(shame: String, value: String) {
        val descr = "Typing into `$shame`: ${markdownItalicVerbatim(value)}"
        val step = TestInstruction.Step.ActionStep(descr)
        instructions.add(step)
        setValue(shame, value)
        act {step.passed = true}
    }

    fun <E : Enum<E>> setValueDescribingStep(shame: String, value: E) {
        setValueDescribingStep(shame, value.name)
    }

    fun setValueDescribingStep(shame: String, value: Int) {
        setValueDescribingStep(shame, value.toString())
    }

    fun setValueDescribingStep(shame: String, value: Boolean) {
        val action = if (value) "Checking" else "Unchecking"
        val descr = "$action `$shame`"
        val step = TestInstruction.Step.ActionStep(descr)
        instructions.add(step)
        setValue(shame, value)
        act {step.passed = true}
    }

    fun setValue(shame: String, value: Boolean) {
        instructions.add(TestInstruction.SetCheckbox(shame, value))
    }

    fun assertMailInFooter(descr: String, expectedTo: String, expectedSubject: String, expectedBody: String) {
        assertUnderFooterHTML("Email: $descr",
            """<div id="debugPanes-initDebugFunctions-mailbox"><div data-reactroot="" id="544"><div id="542" style="margin-top: 10px;"><div id="514" style="font-weight: bold; background: rgb(178, 223, 219);">Mailbox</div><div id="540" style="margin-top: 5px; padding-bottom: 5px; border-bottom: 2px dotted rgb(158, 158, 158);"><div id="536" style="background: rgb(255, 255, 255); margin-bottom: 5px;"><div spacing="4" class="" id="524" style="display: flex;"><div id="520" style="margin-left: 0px;"><span id="516" class="" style="font-weight: bold;">To:</span></div><div id="522" style="margin-left: 4px;"><span id="518" class="">""" +
            expectedTo.replace("<", "&lt;").replace(">", "&gt;") +
            """</span></div></div><div spacing="4" class="" id="534" style="display: flex;"><div id="530" style="margin-left: 0px;"><span id="526" class="" style="font-weight: bold;">Subject:</span></div><div id="532" style="margin-left: 4px;"><span id="528" class="">""" +
            expectedSubject +
            """</span></div></div></div><div id="538"><div>""" +
            dedent(expectedBody) +
            """</div></div></div></div></div></div>""")
    }

    fun beginSection(descr: String) {
        instructions.add(TestInstruction.BeginSection(descr))
    }

    fun endSection() {
        instructions.add(TestInstruction.EndSection())
    }

    fun section(descr: String, block: () -> Unit) {
        beginSection(descr)
        block()
        endSection()
    }

    fun section_rem(descr: String, block: () -> Unit) {
        TestGlobal.hasScenarioRems = true
        beginSection(descr)
        todo("This section is commented out")
        endSection()
    }

}

private enum class VerticalPosition {
    TOP, BOTTOM
}

private enum class HorizontalPosition {
    LEFT, RIGHT
}

fun tillPausedOnAssertion() = pausedOnAssertion.promise

fun resumePausedAssertion() = assertionBannerPause.resolve()













