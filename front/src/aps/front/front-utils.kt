/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.*
import jquery.*
import org.w3c.dom.*
import kotlin.browser.document

val REALLY_BIG_Z_INDEX = 2147483647

fun ctorName(x: Any): String = x.asDynamic().__proto__.constructor.name

class FatException(override val message: String, val asyncStack: String? = null, val markdownPayload: String? = null) : Throwable(message) {
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

fun parseQueryString(href: String): Map<String, String> {
    val regex = global.RegExp("([^&=]+)=?([^&]*)", "g")
    var match: dynamic = null
    val store = mutableMapOf<String, String>()

    var haystack = global.location.search
    haystack = haystack.substring(haystack.indexOf('?') + 1, haystack.length)

    while (true) {
        match = regex.exec(haystack)
        if (!match) break
        store[global.decodeURIComponent(match[1])] = global.decodeURIComponent(match[2])
    }

    return store
}


@native object ReactDOM {
    fun render(rel: ReactElement, container: HTMLElement): Unit = noImpl
    fun unmountComponentAtNode(container: HTMLElement): Unit = noImpl
}

inline fun measure(what: String, block: () -> Unit) {
    val m = jsFacing_beginLogTime(what)
    block()
    m.end()
}

fun asReactElement(x: Any?): ReactElement {
    val didi: dynamic = x
    return didi
}

fun Any?.asDynamicReactElement(): ReactElement = asReactElement(this)

fun reactCreateElement(tag: String, attrs: Json, children: Collection<ReactElement>): ReactElement {
    return React.createElement(tag, attrs, *children.toTypedArray())
}

annotation class MixableType
annotation class GenerateSignatureMixes
annotation class Mix

fun tillAnimationFrame(): Promise<Unit> = Promise {resolve, reject ->
    requestAnimationFrame {
        resolve(Unit)
    }
}

typealias CSSSelector = String

fun cwarnTitle(title: String) {
    cwarn(title)
    cwarn("-".repeat(title.length))
}


















