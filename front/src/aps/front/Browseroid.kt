package aps.front

import aps.*
import aps.front.testutils.*
import into.kommon.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.url.URL
import kotlin.properties.Delegates.notNull

private val reactoidImpl = ReactoidImpl()

var currentBrowseroid: Browseroid = realBrowseroid

interface Browseroid {
    val name: String
    val location: LocationProxy
    val typedStorageLocal: TypedStorageLocal
    val reactoid: Reactoid
//    val timeroid: Timeroid

    companion object {
        val current: Browseroid get() = currentBrowseroid
    }
}


object realBrowseroid : Browseroid {
    override val name = "real"
    override val location = Globus.realLocation
    override val typedStorageLocal = Globus.realTypedStorageLocal
    override val reactoid = reactoidImpl
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
            crossWorld.createOrUpdate()
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
        crossWorld.createOrUpdate()
    }
}

//interface Timeroid {
//}

fun timeoutSet(ms: Int, cb: () -> Unit) {
    kotlin.browser.window.setTimeout(cb, ms)
}

fun intervalSet(ms: Int, cb: () -> Unit) {
    kotlin.browser.window.setInterval(cb, ms)
}

class FuckingBrowseroid(val name: String, val initialURL: String) {
//    companion object {
//        private val stack = mutableListOf<FuckingBrowseroid>()
//
//        fun pop() {
//            val prev = stack.removeAt(stack.lastIndex)
//            prev.switchTo()
//        }
//    }
//
//    private var _location: LocationProxy = zz
//
//    val location get() = _location
//
//    private var state: State = State.Mounted()
//    private sealed class State {
//        class NotBooted : State()
//
//        class Mounted : State()
//
//        class Unmounted(
//            val docInnerHTML: String,
//            val reactRoots: List<ReactRoot>,
//            val url: String
//        ) : State()
//    }
//
//    private var world by notNull<World>()
//
//
//    fun push(): Promise<Unit> = async {
//        _current?.let {stack += it}
//        await(switchTo())
//    }
//
//    fun switchTo(): Promise<Unit> = async {
//        // TODO:vgrechka Unmount current
//
//        exhaustive/when (state) {
//            is FuckingBrowseroid.State.NotBooted -> {
//                val world = World(name)
//                await(world.boot())
//            }
//            is FuckingBrowseroid.State.Mounted -> TODO()
//            is FuckingBrowseroid.State.Unmounted -> TODO()
//        }
//
////        current?.unmount()
////        mount()
////        current = this
//    }
//
//    fun refresh(): Promise<Unit> = async {
//        imf("refresh")
//    }
//
//    private fun unmount() {
//        imf()
//    }
//
//    private class ReactRoot(
//        val reactElement: ReactElement,
//        val htmlElementID: String
//    )
//
//    private fun mount() {
//
////        val sbu = stateBeforeUnmount
////        if (sbu != null) {
////            setDocInnerHTML(sbu.docInnerHTML)
////            for (root in sbu.reactRoots) {
////                DOMReact.render(root.reactElement, byid0ForSure(root.htmlElementID))
////            }
////            Globus.location.replaceWholeURL(sbu.url)
////        } else {
////            world = World(name)
////        }
//
//        imf()
//    }
}
















