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
        acta(descr, {"__async"
            block()
            __asyncResult(Unit)
        })
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
        checkOnAnimationFrame(stepTitle) {
            if (!test()) throw ArtAssertionError(stepTitle)
        }
    }

    fun checkOnAnimationFrame(stepTitle: String, block: () -> Unit) {
        val step = TestInstruction.Step.AssertionStep(stepTitle)
        instructions.add(step)
        acta {"__async"
            __await(tillAnimationFrame())
            block()
            step.passed = true
            __asyncResult(Unit)
        }
    }

    fun halt() {
        act {die("Halted")}
    }

    fun assertHTML(under: CSSSelector, expected: String, transformLine: ((String) -> String)? = null) {
        val stepTitle = "HTML diff under $under"
        checkOnAnimationFrame(stepTitle) {
            val rawActual = stripUninterestingElements(jq(under)).innerHTML
            val tidyActual = tidyHTML(rawActual, transformLine=transformLine)
            val tidyExpected = tidyHTML(expected, transformLine=transformLine)
            if (tidyActual != tidyExpected) {
                throw ArtAssertionError(stepTitle, visualPayload = renderDiff(
                    expected = tidyExpected,
                    actual = tidyActual, actualPaste = rawActual.trim()))
            }
        }
    }

    fun assertNavbarHTML(expected: String) {
        assertHTML(under = "#topNavbarContainer",
                   expected = expected,
                   transformLine = {it.replace(Regex(" id=\"MakeStaticSites-\\d+\""), "")})
    }

    fun assertRootHTML(expected: String) {
        assertHTML(under = "#root",
                   expected = expected,
                   transformLine = {it})
    }

}





