/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.die
import into.kommon.global
import jquery.jq

fun buildAndRunTestScenario(showTestPassedPane: Boolean, block: (TestScenarioBuilder) -> Unit): Promise<Throwable?> {"__async"
    val builder = TestScenarioBuilder()
    block(builder)
    return __reawait(builder.runScenario(showTestPassedPane))
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

//    fun acta2(descr: String? = null, block: () -> Promise<Unit>) {
//        var step: TestInstruction.Step? = null
//        if (descr != null) {
//            step = TestInstruction.Step.ActionStep(descr)
//            instructions.add(step)
//        }
//
//        instructions.add(TestInstruction.Do {"__async"
//            __await(block())
//            step?.passed = true
//            __asyncResult(Unit)
//        })
//    }

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
        checkOnAnimationFrame(stepTitle) {
            if (!test()) throw ArtAssertionError(stepTitle)
        }
    }

    fun checkOnAnimationFrame(stepTitle: String, block: () -> Unit) {
        val step = TestInstruction.Step.AssertionStep(stepTitle)
        instructions.add(step)
        acta {async{
            await(tillAnimationFrame())
            block()
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
        val stepTitle = "HTML diff under $under"
        checkOnAnimationFrame(stepTitle) {
            val rawActual = takeHTMLForAssertion(under)
            val tidyActual = tidyHTML(rawActual, transformLine=transformLine)
            val tidyExpected = tidyHTML(expected, transformLine=transformLine)
            if (tidyActual != tidyExpected) {
                throw ArtAssertionError(stepTitle, visualPayload = renderDiff(
                    expected = tidyExpected,
                    actual = tidyActual, actualPaste = threeQuotes + rawActual.trim() + threeQuotes))
            }
        }
    }

    fun assertNavbarHTML(expected: String) {
        assertHTML(under = SELECTOR_NAVBAR,
                   expected = expected,
                   transformLine = {it.replace(Regex(" id=\"MakeStaticSites-\\d+\""), "")})
    }

    fun assertRootHTML(expected: String) {
        assertHTML(under = SELECTOR_ROOT,
                   expected = expected,
                   transformLine = {it})
    }

}




