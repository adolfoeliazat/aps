package aps.front

import aps.*
import into.kommon.*
import org.w3c.dom.url.URL
import kotlin.browser.window

interface LocationProxy {
    val href: String
    val pathname: String
    val search: String
    fun pushState(data: Any?, title: String, newURL: String)
    fun replaceState(data: Any?, title: String, newURL: String)
    fun replaceWholeURL(newURL: String)
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

    override fun reload() {
        window.location.reload()
    }

    override fun pushState(data: Any?, title: String, newURL: String) {
        window.history.pushState(data, title, newURL)
    }

    override fun replaceState(data: Any?, title: String, newURL: String) {
        window.history.replaceState(data, title, newURL)
    }

    override fun replaceWholeURL(newURL: String) {
        throw UnsupportedOperationException(this::class.simpleName + ".replaceWholeURL")
    }
}

class FakeLocationProxy(initialHref: String) : LocationProxy {
    private var url = URL(initialHref)

    init {
        crossWorld.locationControl.update()
    }

    override val href get() = url.href
    override val pathname get() = url.pathname
    override val search get() = url.search

    override fun pushState(data: Any?, title: String, newURL: String) {
        replaceState(data, title, newURL)
    }

    override fun replaceState(data: Any?, title: String, newURL: String) {
        check(newURL.startsWith("/")) {"newURL should start with /"}
        url = URL(url.protocol + "//" + url.host + newURL)
        crossWorld.locationControl.update()
    }

    override fun replaceWholeURL(newURL: String) {
        url = URL(newURL)
        crossWorld.locationControl.update()
    }

    override fun reload() {
        throw UnsupportedOperationException(this::class.simpleName + ".reload")
    }

}

