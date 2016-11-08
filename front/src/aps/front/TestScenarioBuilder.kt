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

    fun assert(test: Boolean, descr: String) {
        instructions.add(TestInstruction.Step.AssertionStep(descr))
        act {if (!test) art.fail(descr)}
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

    fun assertVisibleText(expected: String) {
        assertOnAnimationFrame("Page should contain text: [$expected]", {
            val actual = jq("body *:not(:has(*)):visible").text()
            actual.contains(expected)
        })
    }

    private fun assertOnAnimationFrame(descr: String, test: () -> Boolean) {
        instructions.add(TestInstruction.Step.AssertionStep(descr))
        acta {"__async"
            __await(tillAnimationFrame())
            if (!test()) art.fail(descr)
            __asyncResult(Unit)
        }
    }
}


