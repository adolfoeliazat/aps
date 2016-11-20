package aps.front

import aps.*
import kotlin.browser.*

fun delay(ms: Int): Promise<Unit> = Promise {resolve, reject ->
    window.setTimeout({resolve(Unit)}, ms)
}

abstract class BootTestScenario : StepBasedTestScenario() {
    abstract val clientKind: ClientKind
    abstract fun fillLocalStorage()
    abstract fun buildStepsAfterDisplayInitialShit()
    abstract fun buildStepsAfterWorldBoot()

    override fun buildSteps() {
//        val siteName = clientKind.name.toLowerCase()
//        o.assert(window.location.hostname.contains("$siteName"), "Scenario should be tested against $siteName site")
//        o.assert(window.location.pathname == "/", "Scenario should be tested against / path")

        o.acta {"__async"
            val url = "http://aps-ua-writer.local:3022"
            val content = measureAndReportToDocumentElement("Loading $url") {
                __await(fetchURL(url, "GET", null))
            }

            if (SLOWISH) __await(delay(1000))
            window.history.pushState(null, "", url)
            val openingHeadTagIndex = content.indexOfOrDie("<head")
            val closingHTMLTagIndex = content.indexOfOrDie("</html>")
            val innerHTMLContent = content.substring(openingHeadTagIndex, closingHTMLTagIndex)
            docInnerHTML = innerHTMLContent
            __asyncResult(Unit)
        }

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