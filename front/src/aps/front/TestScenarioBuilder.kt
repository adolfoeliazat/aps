/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import aps.front.testutils.*
import into.kommon.*
import jquery.jq

fun buildAndRunTestScenario(showTestPassedPane: Boolean, block: (TestScenarioBuilder) -> Unit): Promise<Throwable?> {"__async"
    val builder = TestScenarioBuilder()
    block(builder)
    return __reawait(builder.runScenario(showTestPassedPane))
}

val transformNavbarLineTidy = {it: String ->
    it.replace(Regex(" id=\"MakeStaticSites-\\d+\""), "")
}

val transformRootLineTidy = {it: String ->
    it
}

class TestScenarioBuilder {
    val instructions = mutableListOf<TestInstruction>()

    fun runScenario(showTestPassedPane: Boolean): Promise<Throwable?> {"__async"
        return __reawait(art.run(instructions, showTestPassedPane))
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

    fun acta(descr: String? = null, block: () -> Promise<Unit>) {
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
                    actualPaste = threeQuotes + rawActual.trim() + threeQuotes))
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
                   expected = {fuckingRemoteCall.loadTestShit(id)},
                   transformLine = transformNavbarLineTidy,
                   descr=descr)
        act {TestGlobal.testShitBeingAssertedID = null}
    }

    fun assertRootHTML(expected: String) {
        assertHTML(under = SELECTOR_ROOT, expected = expected, transformLine = {it})
    }

    fun assertRootHTMLExt(descr: String?, id: String) {
        act {TestGlobal.testShitBeingAssertedID = id}
        assertHTML(inside = SELECTOR_ROOT, expected = {fuckingRemoteCall.loadTestShit(id)}, transformLine = {it}, descr=descr)
        act {TestGlobal.testShitBeingAssertedID = null}
    }

    fun assertScreenHTML(descr: String?, id: String) {
        act {TestGlobal.testShitBeingAssertedID = id}

        val stepTitle = "HTML: $descr"
        checkOnAnimationFrame(stepTitle) {async{
            val expected = await(fuckingRemoteCall.loadTestShit(id))

            val actual = buildString {
                append("-------------------- NAVBAR --------------------\n\n")
                append(tidyHTML(takeHTMLForAssertion(SELECTOR_NAVBAR), transformNavbarLineTidy))
                if (!endsWith("\n")) append("\n")
                append("\n-------------------- ROOT --------------------\n\n")
                append(tidyHTML(takeHTMLForAssertion(SELECTOR_ROOT), transformRootLineTidy))
            }

            if (actual != expected) {
                throw ArtAssertionError(
                    stepTitle,
                    visualPayload = renderDiff(
                        expected = expected,
                        actual = actual,
                        actualTestShit = actual
                    )
                )
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
        beginSection(descr)
        todo("This section is commented out")
        endSection()
    }

}




