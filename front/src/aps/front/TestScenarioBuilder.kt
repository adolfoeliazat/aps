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
import kotlin.properties.Delegates
import kotlin.properties.Delegates.notNull

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

    fun assertScreenHTML(descr: String?, id: String) {
        val assertionID = id
        act {TestGlobal.testShitBeingAssertedID = id}

        val stepTitle = "HTML: $descr"
        checkOnAnimationFrame(stepTitle) {async<Unit>{
            val expected = await(fuckingRemoteCall.loadTestShit(id))

            val actual = buildString {
                append("-------------------- NAVBAR --------------------\n\n")
                append(tidyHTML(takeHTMLForAssertion(SELECTOR_NAVBAR), transformNavbarLineTidy))
                if (!endsWith("\n")) append("\n")
                append("\n-------------------- ROOT --------------------\n\n")
                append(tidyHTML(takeHTMLForAssertion(SELECTOR_ROOT), transformRootLineTidy))
            }

            if (TestGlobal.lastTestOpts.stopOnAssertions) {
                await(object {
                    var banner by notNull<Control2>()
                    var verticalPosition = VerticalPosition.BOTTOM
                    var horizontalPosition = HorizontalPosition.LEFT

                    val shit = async {
                        when {
                            expected == null -> {
                                await(showBanner(css.test.popup.assertion.notHardened))
                            }

                            actual == expected -> {
                                await(showBanner(css.test.popup.assertion.correct))
                            }

                            actual != expected -> {
                                await(captureVisualShit(assertionID))

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
                                        css.test.popup.assertion.incorrect,
                                        renderSpecificButtons = {o->
                                            o- Button(title = "Diff", style = Style(marginRight = "1rem"), onClick = {
                                                verticalPosition = VerticalPosition.TOP
                                                horizontalPosition = HorizontalPosition.LEFT
                                                banner.update()
//                                                byid("fuckingDiff").scrollBodyToShit()
                                                nextDiff().scrollBodyToShit(dy = -70)
                                            })
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

                    fun showBanner(className: String, renderSpecificButtons: (ElementBuilder) -> Unit = {}) = async {
                        val shit = ResolvableShit<Unit>()
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
                                o- kdiv(marginBottom = "0.5rem"){o->
                                    o- Button(icon = fa.play, style = Style(marginRight = "1rem"), onClick = {
                                        shit.resolve(Unit)
                                    })
                                    o- Button(icon = fa.bomb, style = Style(marginRight = "1rem"), onClick = {
                                        shit.reject(Exception("Fucking killed"))
                                    })
                                    o- Button(
                                        icon = when (verticalPosition) {
                                            VerticalPosition.TOP -> fa.arrowDown
                                            VerticalPosition.BOTTOM -> fa.arrowUp
                                        },
                                        style = Style(marginRight = "1rem"),
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
                                        style = Style(marginRight = "1rem"),
                                        onClick = {
                                            horizontalPosition = when (horizontalPosition) {
                                                HorizontalPosition.LEFT -> HorizontalPosition.RIGHT
                                                HorizontalPosition.RIGHT -> HorizontalPosition.LEFT
                                            }
                                            banner.update()
                                        })
                                    renderSpecificButtons(o)
                                }
                                o- "Assertion: $descr"
                            }
                        }
                        val pane = debugPanes.put(banner)

                        fun keyListener(e: Event) {
                            e as KeyboardEvent
                            if (e.key == " ") {
                                shit.resolve(Unit)
                            }
                        }
                        window.addEventListener("keydown", ::keyListener)

                        try {
                            await(shit.promise)
                        } finally {
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


