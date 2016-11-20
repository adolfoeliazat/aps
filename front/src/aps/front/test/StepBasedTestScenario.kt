package aps.front

import aps.*

abstract class StepBasedTestScenario : TestScenario() {
    abstract fun buildSteps()

    lateinit var o: TestScenarioBuilder

    override fun run0(showTestPassedPane: Boolean): Promise<Throwable?> {"__async"
        return __reawait(buildAndRunTestScenario(showTestPassedPane) {
            o = it
            buildSteps()
        })
    }
}
