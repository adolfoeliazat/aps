package aps.front

import aps.*
import org.w3c.dom.url.URL
import kotlin.browser.window

interface LocationProxy {
    val href: String
    val pathname: String
    val search: String
    val baseWithoutSlash: String
    val host: String
    fun pushState(data: Any?, title: String, newURL: String)
    fun replaceState(data: Any?, title: String, newURL: String)
//    fun replaceWholeURL(newURL: String)
    fun reload()
}

class RealLocationProxy : LocationProxy {
    override var href: String
        get() = window.location.href
        set(value) { window.location.href = value }

    override val pathname: String
        get() = window.location.pathname

    override val search: String
        get() = window.location.search

    override val baseWithoutSlash: String
        get() = window.location.protocol + "//" + window.location.host

    override val host: String
        get() = window.location.host

    override fun reload() {
        window.location.reload()
    }

    override fun pushState(data: Any?, title: String, newURL: String) {
        window.history.pushState(data, title, newURL)
    }

    override fun replaceState(data: Any?, title: String, newURL: String) {
        window.history.replaceState(data, title, newURL)
    }

}

//class FakeLocationProxy(initialHref: String) : LocationProxy {
//    private var url = URL(initialHref)
//
//    init {
//        crossWorld.locationControl.update()
//    }
//
//    override val href get() = url.href
//    override val pathname get() = url.pathname
//    override val search get() = url.search
//    override val baseWithoutSlash get() = url.protocol + "//" + url.host
//    override val host get() = url.host
//
//    override fun pushState(data: Any?, title: String, newURL: String) {
//        replaceState(data, title, newURL)
//    }
//
//    override fun replaceState(data: Any?, title: String, newURL: String) {
//        check(newURL.startsWith("/")) {"newURL should start with /"}
//        url = URL(baseWithoutSlash + newURL)
//        crossWorld.locationControl.update()
//    }
//
//    override fun reload() {
//        throw UnsupportedOperationException(this::class.simpleName + ".reload")
//    }
//
//}

