package aps.front

import aps.*
import aps.front.testutils.*

abstract class StepBasedTestScenario : TestScenario() {
    abstract fun buildSteps()

    lateinit var o: TestScenarioBuilder

    override fun run0(showTestPassedPane: Boolean): Promisoid<Throwable?> = async {
        await(buildAndRunTestScenario(this, showTestPassedPane) {
            o = it
            buildSteps()
        })
    }
}
