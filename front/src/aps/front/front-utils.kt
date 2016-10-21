/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.ReactElement
import jquery.JQuery
import jquery.jq
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import kotlin.browser.document

fun constructorName(x: Any): String = x.asDynamic().__proto__.constructor.name

class JSException(override val message: String, val asyncStack: String? = null) : Throwable(message) {
    val stack = js("Error")(message).stack
}

@native interface JSArray

fun <T> Array<T>.toJSArray(): JSArray = this.asIterable().toJSArray()

fun <T> Iterable<T>.toJSArray(): JSArray {
    val res = js("[]")
    this.forEach { res.push(it) }
    return res
}

fun evalAny(code: String): Any = eval(code)

fun jsArrayToList(arr: dynamic, transform: (dynamic) -> dynamic = {it}): List<dynamic> {
    val list = mutableListOf<dynamic>()
    for (i in 0 until arr.length)
        list.add(transform(arr[i]))
    return list
}

fun jsKeys(x: Any): Iterable<String> = jsArrayToList(js("Object.keys(x)"))

fun jsSet(target: Any, prop: String, value: Any?) = eval("target[prop] = value")

fun jsIsArray(x: Any?): Boolean = eval("Array.isArray(x)")

fun dyna(build: (dynamic) -> Unit): dynamic {
    val res = js("({})")
    build(res)
    return res
}

fun jsFacing_parseQueryString(href: dynamic): dynamic {
    val regex = global.RegExp("([^&=]+)=?([^&]*)", "g")
    var match: dynamic = null
    val store = json()

    var haystack = global.location.search
    haystack = haystack.substring(haystack.indexOf('?') + 1, haystack.length)

    while (true) {
        match = regex.exec(haystack)
        if (!match) break
        store[global.decodeURIComponent(match[1])] = global.decodeURIComponent(match[2])
    }

    return store
}

val jqbody: JQuery get() = jq(document.body!!)

fun byid(id: String): JQuery {
    val selector = "#$id".replace(Regex("\\."), "\\.")
    return jq(selector)
}

fun byid0(id: String): HTMLElement? {
    val selector = "#$id".replace(Regex("\\."), "\\.")
    return jq(selector)[0]
}

fun byid0ForSure(id: String): HTMLElement {
    return requireNotNull(byid0(id)) {"I want fucking element #$id"}
}

operator fun JQuery.get(index: Int): HTMLElement? = this.asDynamic()[index]
fun JQuery.scrollTop(value: Int): Unit = this.asDynamic().scrollTop(value)
fun JQuery.scrollTop(): Int = this.asDynamic().scrollTop()

@native object ReactDOM {
    fun render(rel: ReactElement, container: HTMLElement): Unit = noImpl
    fun unmountComponentAtNode(container: HTMLElement): Unit = noImpl
}

val REALLY_BIG_Z_INDEX = 2147483647









