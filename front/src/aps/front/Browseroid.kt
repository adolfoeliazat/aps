package aps.front

import aps.*
import aps.front.Globus.effectsPane
import aps.front.testutils.*
import into.kommon.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.url.URL
import kotlin.browser.window
import kotlin.properties.Delegates.notNull

private val reactoidImpl = ReactoidImpl()

interface Browseroid {
    val name: String
    val location: LocationProxy
    val typedStorageLocal: TypedStorageLocal
    val reactoid: Reactoid
    val timer: Timeroid

    companion object {
        val current: Browseroid get() = Globus.currentBrowseroid
    }
}

interface Timeroid {
    fun setTimeout(cb: () -> Unit, ms: Int)
    fun setInterval(cb: () -> Unit, ms: Int)

}

object realBrowseroid : Browseroid {
    override val name = "real"
    override val location = Globus.realLocation
    override val typedStorageLocal = Globus.realTypedStorageLocal
    override val reactoid = reactoidImpl

    override val timer = object:Timeroid {
        override fun setTimeout(cb: () -> Unit, ms: Int) {
            window.setTimeout(cb, ms)
        }

        override fun setInterval(cb: () -> Unit, ms: Int) {
            window.setInterval(cb, ms)
        }
    }
}

class ReactoidImpl : Reactoid {
    override fun mount(rel: ReactElement, container: HTMLElement) {
        _DOMReact.render(rel, container)
    }
//    override fun render(rel: ReactElement?, container: HTMLElement) {
//        _DOMReact.render(rel, container)
//    }
//
//    override fun unmountComponentAtNode(container: HTMLElement) {
//        _DOMReact.unmountComponentAtNode(container)
//    }
}

class TestBrowseroid(override val name: String, val initialURL: String) : Browseroid {
    private var url = URL(initialURL)

    override val location = object:LocationProxy {
        override val href get() = url.href
        override val pathname get() = url.pathname
        override val search get() = url.search
        override val baseWithoutSlash get() = url.protocol + "//" + url.host
        override val host get() = url.host

        override fun pushState(data: Any?, title: String, newURL: String) {
            replaceState(data, title, newURL)
        }

        override fun replaceState(data: Any?, title: String, newURL: String) {
//            check(newURL.startsWith("/")) {"newURL should start with / (got: $newURL)"}
            var theNewURL = newURL
            if (!theNewURL.startsWith("/")) theNewURL = "/$theNewURL"
            url = URL(baseWithoutSlash + theNewURL)
            TestLocationBar.update()
        }

        override fun reload() {
            throw UnsupportedOperationException("reload")
        }
    }

    private val storageLocal = object:StorageLocal {
        val map = mutableMapOf<String, String>()

        override val length get() = map.size

        override fun key(index: Int) = map.keys.toList()[index]

        override fun clear() = map.clear()
        override fun getItem(key: String) = map[key]
        override fun setItem(key: String, value: String) {
            map[key] = value
        }

        override fun removeItem(key: String) {
            map.remove(key)
        }
    }

    override val typedStorageLocal = TypedStorageLocal(storageLocal)

    override val reactoid = reactoidImpl

    companion object {
        private var currentTestBrowseroid: TestBrowseroid? = null
    }

    private fun saveState() {

    }

    fun switchTo(): Promisoid<Unit> = async<Unit> {
        setDocInnerHTML("<head></head><body></body>")
        val content = measureAndReportToDocumentElement("Loading $initialURL") {
            await(fetchURL(initialURL, "GET", null))
        }

        val openingHeadTagIndex = content.indexOfOrDie("<head")
        val closingHTMLTagIndex = content.indexOfOrDie("</html>")
        val innerHTMLContent = content.substring(openingHeadTagIndex, closingHTMLTagIndex)

        setDocInnerHTML(innerHTMLContent)
        loadCSS()


//
//        currentTestBrowseroid?.saveState()
//
//        when {
//            !staticContentLoaded -> {
//                setDocInnerHTML("<head></head><body></body>")
//                val content = measureAndReportToDocumentElement("Loading $initialURL") {
//                    await(fetchURL(initialURL, "GET", null))
//                }
//
//                val openingHeadTagIndex = content.indexOfOrDie("<head")
//                val closingHTMLTagIndex = content.indexOfOrDie("</html>")
//                val innerHTMLContent = content.substring(openingHeadTagIndex, closingHTMLTagIndex)
//
//                setDocInnerHTML(innerHTMLContent)
//                loadCSS()
//                updateLocationControl()
//                ExternalGlobus.displayInitialShit()
//                restoreReactElements()
//                staticContentLoaded = true
//            }
//
//            staticContentLoaded -> {
//                setDocInnerHTML(docInnerHTML)
//                restoreReactElements()
//            }
//        }
//
//        bootedWorld?.let {Globus.worldMaybe = it}
//        currentBrowseroid = this
//        currentTestBrowseroid = this
    }

    var bootedWorld: World? = null

    fun boot(): Promisoid<Unit> = async {
        if (currentTestBrowseroid != this) bitch("Before booting switch to browseroid $name")
        if (bootedWorld != null) bitch("Browseroid $name is already booted")

        val world = World(name)
        await(world.boot())
        bootedWorld = world
    }

    fun replaceWholeURL(newURL: String) {
        url = URL(newURL)
        TestLocationBar.update()
    }

    override val timer = object:Timeroid {
        override fun setTimeout(cb: () -> Unit, ms: Int) {
            imf("TestBrowseroid.setTimeout")
        }

        override fun setInterval(cb: () -> Unit, ms: Int) {
            imf("TestBrowseroid.setInterval")
        }

    }
}

data class MordaCoitizeParams(
    val browseroidName: String,
    val url: String,
    val fillTypedStorageLocal: (TypedStorageLocal) -> Unit,
    val fillRawStorageLocal: (StorageLocal) -> Unit
)

class Morda {
    enum class State {
        VIRGIN, STATIC, ACTIVE, SHELVED
    }

    private var state: State = State.VIRGIN
    private var coitizeParams by notNull<MordaCoitizeParams>()
    private var world by notNullOnce<World >()
    private var bro by notNullOnce<TestBrowseroid>()
    private var stols by notNullOnce<IStorage>()
    private var bodyScrollTop by notNull<Double>()

    fun coitize(p: MordaCoitizeParams) = async<Unit> {
        check(state == State.VIRGIN)
        coitizeParams = p
        dlog("Coitizing ${p.browseroidName}")

        TestGlobal.currentMordaMaybe?.let {await(it.shelve())}

        run { // Create and set new environment
            bro = TestBrowseroid(p.browseroidName, p.url)
            p.fillRawStorageLocal(bro.typedStorageLocal.store)
            p.fillTypedStorageLocal(bro.typedStorageLocal)
            Globus.currentBrowseroidMaybe = bro

            stols = object:IStorage {
                override fun getItem(key: String) = bro.typedStorageLocal.store.getItem(key)
            }
            ExternalGlobus.storageLocalForStaticContent = stols
        }

        await(disposeAndShelveShit())

        run { // Load static content
            dlog("Navigating to static content: ${p.url}")
            val content = measureAndReportToDocumentElement("Loading ${p.url}") {
                await(fetchURL(p.url, "GET", null))
            }

            val openingHeadTagIndex = content.indexOfOrDie("<head")
            val closingHTMLTagIndex = content.indexOfOrDie("</html>")
            val innerHTMLContent = content.substring(openingHeadTagIndex, closingHTMLTagIndex)

            setDocInnerHTML(innerHTMLContent)
            jqbody.scrollTop(0)
            loadCSS()
        }

        TestLocationBar.update()
        ExternalGlobus.displayInitialShit()
        TestGlobal.currentMordaMaybe = this
        state = State.STATIC
    }

    fun boot() = async<Unit> {
        check(state == State.STATIC)
        world = World(coitizeParams.browseroidName)
        await(world.boot())
        state = State.ACTIVE
    }

    fun shelve() = async<Unit> {
        check(state == State.ACTIVE)
        bodyScrollTop = jqbody.scrollTop()
        await(disposeAndShelveShit())
        TestGlobal.currentMordaMaybe = null
        Globus.worldMaybe = null
        Globus.currentBrowseroidMaybe = null
        ExternalGlobus.storageLocalForStaticContent = null
        state = State.SHELVED
    }

    fun switchTo() = async<Unit> {
        check(state == State.SHELVED)
        Globus.worldMaybe = world
        TestGlobal.currentMordaMaybe = this
        Globus.currentBrowseroidMaybe = bro
        ExternalGlobus.storageLocalForStaticContent = stols
        TestLocationBar.update()
        world.unshelveVisualShit()
        jqbody.scrollTop(bodyScrollTop)
        state = State.ACTIVE
    }

    private fun disposeAndShelveShit() = async<Unit> {
        TestLocationBar.dispose()
        disposeEffects()
        Globus.worldMaybe?.shelveVisualShit()
        _DOMReact.checkNothingMounted()
    }

}

class MordaBuilder(private val o: TestScenarioBuilder) {
    private val morda = Morda()

    fun init(
        p: MordaCoitizeParams,
        buildStaticAssertion: () -> Unit,
        buildDynamicAssertion: () -> Unit
    ) {
        o.acta {morda.coitize(p)}
        buildStaticAssertion()
        o.acta {morda.boot()}
        buildDynamicAssertion()
    }

    fun switchTo() {
        o.acta {morda.switchTo()}
    }
}













