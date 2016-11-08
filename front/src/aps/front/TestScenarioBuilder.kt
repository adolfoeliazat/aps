/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

fun buildAndRunTestScenario(block: (TestScenarioBuilder) -> Unit): Promise<Unit> {"__async"
    val builder = TestScenarioBuilder()
    block(builder)
    __await(builder.runScenario())
    return __asyncResult(Unit)
}

class TestScenarioBuilder {
    val instructions = mutableListOf<TestInstruction>()

    fun runScenario(): Promise<Unit> {"__async"
        __await(art.run(instructions))
        return __asyncResult(Unit)
    }

    fun assert(descr: String, test: Boolean) {
        instructions.add(TestInstruction.Step.AssertionStep(descr))
        act {if (!test) throw ArtAssertionError(descr)}
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
            __await(block())
            __asyncResult(Unit)
        })
    }
}


