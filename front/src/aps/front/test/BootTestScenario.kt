@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import aps.front.TestUtils.bootWorld
import aps.front.TestUtils.initNewBrowser
import aps.front.testutils.*
import org.w3c.dom.Storage
import kotlin.browser.window

fun delay(ms: Int): Promisoid<Unit> = Promisoid {resolve, _ ->
    global.setTimeout({resolve(Unit)}, ms)
}

abstract class BootTestScenario : StepBasedTestScenario() {
    abstract val clientKind: ClientKind
    abstract fun fillStorageLocal(tsl: TypedStorageLocal)
    abstract fun buildStepsAfterDisplayInitialShit()
    abstract fun buildStepsAfterWorldBoot()
    abstract val path: String

    lateinit var initialWorld: World
    lateinit var initialStorageLocal: TypedStorageLocal

    override fun buildSteps() {
        initNewBrowser(o, fillStorageLocal = {initialStorageLocal = it; fillStorageLocal(it)})

        o.acta {async{
            val url = "http://aps-ua-writer.local:3022$path"
            val content = measureAndReportToDocumentElement("Loading $url") {
                await(fetchURL(url, "GET", null))
            }

            if (SLOWISH) await(delay(1000))
            loc.pushState(null, "", url)
            val openingHeadTagIndex = content.indexOfOrDie("<head")
            val closingHTMLTagIndex = content.indexOfOrDie("</html>")
            val innerHTMLContent = content.substring(openingHeadTagIndex, closingHTMLTagIndex)
            setDocInnerHTML(innerHTMLContent)
        }}

        o.act {
            ExternalGlobus.displayInitialShit()
        }
        buildStepsAfterDisplayInitialShit()

        bootWorld(o, "initial") {initialWorld = it}
        buildStepsAfterWorldBoot()
    }
}




