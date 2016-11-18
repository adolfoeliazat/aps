package aps.front

import aps.*

abstract class StepBasedTestScenario : TestScenario() {
    abstract fun buildSteps()

    lateinit var o: TestScenarioBuilder

    override fun run0(): Promise<Unit> {"__async"
        return __reawait(buildAndRunTestScenario {
            o = it
            buildSteps()
        })
    }
}
