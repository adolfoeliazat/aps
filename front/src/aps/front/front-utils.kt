/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*
import jquery.*
import org.w3c.dom.*
import kotlin.browser.window
import kotlin.dom.asList

val REALLY_BIG_Z_INDEX = 2147483647

open class FatException(
    message: String,
    val asyncStack: String? = null,
    val markdownPayload: String? = null,
    val visualPayload: ToReactElementable? = null
) : Exception(message) {
//    val stack = js("Error")(message).stack
}

//abstract class ExceptionWithVisualPayload(message: String): Exception(message) {
//    abstract val visualPayload: ToReactElementable?
//}

@native interface JSArray

fun <T> Array<T>.toJSArray(): JSArray = this.asIterable().toJSArray()

fun <T> Iterable<T>.toJSArray(): JSArray {
    val res = js("[]")
    this.forEach { res.push(it) }
    return res
}

fun evalAny(code: String): Any = eval(code)

fun <T> jsArrayToList(arr: dynamic, transform: (dynamic) -> T = {it}): List<T> =
    (0 until arr.length).map {transform(arr[it])}

fun jsArrayToListOfDynamic(arr: dynamic, transform: (dynamic) -> dynamic = {it}): List<dynamic> =
    jsArrayToList<dynamic>(arr, transform)

fun jsKeys(x: Any): Iterable<String> = jsArrayToListOfDynamic(js("Object.keys(x)"))

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

object DOMReact {
    val containers = mutableListOf<HTMLElement>()

    fun render(rel: ReactElement?, container: HTMLElement) {
        ReactDOM.render(rel, container)
        containers += container
    }

    fun unmountComponentAtNode(container: HTMLElement) {
        ReactDOM.unmountComponentAtNode(container)
        containers -= container
    }
}

@native object ReactDOM {
    fun render(rel: ReactElement?, container: HTMLElement): Unit = noImpl
    fun unmountComponentAtNode(container: HTMLElement): Unit = noImpl
}

inline fun <T> measure(what: String, block: () -> T): T {
    val m = jsFacing_beginLogTime(what)
    val res = block()
    m.end()
    return res
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

fun tidyHTML(html: String, transformLine: ((String) -> String)? = null): String = buildString {
    var indent = 0
    html
        .replace(">", ">\n")
        .replace("</", "\n</")
        .lines()
        .forEach {
            var s = (transformLine ?: {it})(it)
                .replace(Regex("<!--.*?react-text.*?-->"), "")
                .replace(Regex(" data-reactroot=\".*?\""), "")
                .trim()
            if (s.isBlank()) return@forEach
            s = s.replace(Regex(" id=\"\\d+\""), "")
            if (s.startsWith("</")) --indent
            if (indent < 0) {
                cwarn("Shitty HTML", html)
                bitch("Can't figure out indentations")
            }
            append(" ".repeat(indent * 2) + s + "\n")
            if (s.startsWith("<") && !s.startsWith("</")) ++indent
        }
}

fun stripUninterestingElements(jqel: JQuery): HTMLElement {
    fun descend(el: HTMLElement) {
        if (el.tagName == "SCRIPT" || el.style.display == "none") return el.remove()
        if (el.className.contains("ignoreInTests")) return el.remove()

        val children = el.children.asList().toList() // Copying it because `children.asList()` is live
        for (child in children) {
            descend(child)
        }
    }

    val root = jqel[0]!!.cloneNode(/*deep*/ true) as HTMLElement
    descend(root)
    return root
}

operator fun String?.plus(rhs: String?): String =
    if (this == null && rhs == null) "nullnull"
    else this.asDynamic() + rhs

fun dumpLocalStorage() {
    val ls = window.localStorage
    for (i in 0 until ls.length) {
        val key = ls.key(i)
        clog(key + ": " + ls[key!!])
    }
}

fun jsArrayLikeToJSArray(xs: Any?) {
    return js("Array").prototype.slice.call(xs)
}













