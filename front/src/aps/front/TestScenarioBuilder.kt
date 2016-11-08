/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.global
import jquery.jq

fun buildAndRunTestScenario(block: (TestScenarioBuilder) -> Unit): Promise<Unit> {"__async"
    val builder = TestScenarioBuilder()
    block(builder)
    return __reawait(builder.runScenario())
}

class TestScenarioBuilder {
    val instructions = mutableListOf<TestInstruction>()

    fun runScenario(): Promise<Unit> {"__async"
        return __reawait(art.run(instructions))
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
        if (descr != null) instructions.add(TestInstruction.Step.ActionStep(descr))

        instructions.add(TestInstruction.Do {"__async"
            __reawait(block())
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
            !actual.contains(expected)
        })
    }

    private fun visibleText(under: CSSSelector) = jq("$under *:not(:has(*)):visible").text()

    fun assertOnAnimationFrame(descr: String, test: () -> Boolean) {
        val step = TestInstruction.Step.AssertionStep(descr)
        instructions.add(step)
        acta {"__async"
            __await(tillAnimationFrame())
            step.passed = test()
            if (!step.passed) art.fail(descr)
            __asyncResult(Unit)
        }
    }
}


