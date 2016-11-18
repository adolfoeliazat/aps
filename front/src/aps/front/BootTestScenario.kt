package aps.front

import aps.*
import kotlin.browser.*

abstract class BootTestScenario : StepBasedTestScenario() {
    abstract val clientKind: ClientKind
    abstract fun fillLocalStorage()
    abstract fun buildStepsAfterWorldBoot()
    abstract fun buildStepsAfterDisplayInitialShit()

    override fun buildSteps() {
        val siteName = clientKind.name.toLowerCase()
        o.assert(window.location.hostname.contains("$siteName"), "Scenario should be tested against $siteName site")
        o.assert(window.location.pathname == "/", "Scenario should be tested against / path")

        o.act {
            localStorage.clear()
            fillLocalStorage()
            ExternalGlobus.displayInitialShit()
        }
        buildStepsAfterDisplayInitialShit()

        o.acta {"__async"
            __reawait(World().boot())
        }
        buildStepsAfterWorldBoot()
    }
}