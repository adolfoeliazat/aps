package aps.front

import aps.*
import org.w3c.dom.Storage
import kotlin.browser.window

fun delay(ms: Int): Promise<Unit> = Promise {resolve, reject ->
    window.setTimeout({resolve(Unit)}, ms)
}

abstract class BootTestScenario : StepBasedTestScenario() {
    abstract val clientKind: ClientKind
    abstract fun fillStorageLocal()
    abstract fun buildStepsAfterDisplayInitialShit()
    abstract fun buildStepsAfterWorldBoot()

    override fun buildSteps() {
//        val siteName = clientKind.name.toLowerCase()
//        o.assert(window.location.hostname.contains("$siteName"), "Scenario should be tested against $siteName site")
//        o.assert(window.location.pathname == "/", "Scenario should be tested against / path")

        o.act {
            val fakeStorageLocal = object : StorageLocal {
                val map = mutableMapOf<String, String>()
                override fun clear() = map.clear()
                override fun getItem(key: String) = map[key]
                override fun setItem(key: String, value: String) {map[key] = value}
                override fun removeItem(key: String) {map.remove(key)}
            }

            Globus.browser = Browser(
                typedStorageLocal = TypedStorageLocal(fakeStorageLocal)
            )

            ExternalGlobus.storageLocalForStaticContent = object:Storage {
                override fun getItem(key: String) = fakeStorageLocal.getItem(key)
            }

            fillStorageLocal()
        }

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
            ExternalGlobus.displayInitialShit()
        }
        buildStepsAfterDisplayInitialShit()

        o.acta {"__async"
            __reawait(World().boot())
        }
        buildStepsAfterWorldBoot()
    }
}




