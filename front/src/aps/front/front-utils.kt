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
import org.w3c.dom.css.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import kotlin.browser.document
import kotlin.browser.window
import kotlin.dom.asList
import kotlin.properties.Delegates.notNull
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

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

fun reactCreateElement(tag: String, attrs: Json, children: Collection<ReactElement?> = listOf()): ReactElement {
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

fun tidyHTML(html: String, transformLine: ((String) -> String)? = null): String {
    val buf = StringBuilder()

    val ultimateHTML = html
        .replace("<span", "\n<span")
        .replace(">", ">\n")
        .replace("</", "\n</")

    val lines = ultimateHTML
        .lines()
        .map {
            (transformLine ?: {it})(it)
                .replace(Regex("<!--.*?react-text.*?-->"), "")
                .replace(Regex("<!-- react-empty: \\d+ -->"), "")
                .replace(Regex(" data-reactroot=\".*?\""), "")
                .replace(Regex(" id=\"\\d+\""), "")
                .trim()
        }
        .filterNot {it.isBlank()}

    var indent = 0
    lines.forEachIndexed {i, s ->
        if (s.startsWith("</")) --indent
        if (indent < 0) {
//            cwarn("Shitty HTML", lines.joinToString("\n"))
            cwarn("buf so far", buf.toString())
            cwarn("Shit left", lines.subList(i, lines.size).joinToString("\n"))
            cwarn("Shitty line", i, s)
            bitch("Can't figure out indentations")
        }
        buf.append(" ".repeat(indent * 2) + s + "\n")
        if (s.startsWith("<") && !s.startsWith("</")) ++indent
    }

    return buf.toString()
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

inline fun currentJSFunctionName(): String {
    val stack: String = js("Error().stack")
    // dwarnStriking(stack)
    val lines = stack.lines()
    check(lines.size >= 2)
    var line = lines[1]
    check(line.startsWith("    at "))
    line = line.substring("    at ".length)
    val spaceIndex = line.indexOf(" ")
    if (spaceIndex != -1) line = line.substring(0, spaceIndex)
    val dotIndex = line.lastIndexOf(".")
    if (dotIndex != -1) line = line.substring(dotIndex + 1)
    if (Regex("_......\\\$").containsMatchIn(line)) line = line.substring(0, line.lastIndexOf("_"))
    return line
}


inline fun dwarnStriking(vararg xs: Any?) = dwarn("**********", *xs)

object frontSymbols {
    val numberSign: String by lazy {when (Globus.lang) {
        Language.EN -> "#"
        Language.UA -> "№"
    }}
}

fun encodeURIComponent(s: String): String = global.encodeURIComponent(s)

fun arraysEquals(a: Array<*>, b: Array<*>): Boolean =
    a.asList() == b.asList()

fun scrollBodyToShitGradually(
    dy: Int = -10,
    bursts: Int = fconst.defaultScrollBursts,
    dontScrollToTopItem: Boolean = false,
    getShit: () -> JQuery
): Promise<Unit> = async {
    await(tillAnimationFrame())
    val shit = getShit()
    check(shit[0] != null) {"Shit to scroll to is not found"}

    if (dontScrollToTopItem) {
        if (shit.hasClass(css.item)) {
            var topItem = shit[0]!!
            jq(".${css.item}").each {_, el ->
                if (el.offsetTop < topItem.offsetTop)
                    topItem = el
            }
            if (shit[0] == topItem) return@async
        }
    }

    val targetTop: Double
    targetTop = shit.offset().top - const.topNavbarHeight + dy
    await(scrollBodyGradually(targetTop, bursts))
}

fun scrollBodyGradually(targetTop: Double, bursts: Int = fconst.defaultScrollBursts): Promise<Unit> = async {
    val startTop = jqbody.scrollTop()
    for (i in 1..bursts) {
        await(tillAnimationFrame())
        jqbody.scrollTop(startTop + (targetTop - startTop) / bursts * i)
    }
    jqbody.scrollTop(targetTop)
}

fun EventTarget.addEventLis(type: String, callback: ((Event) -> Unit)?) {
    addEventListener(type, callback)
}

fun String?.relaxedToBoolean(default: Boolean): Boolean {
    return when (this) {
        null -> default
        else -> when (this.toLowerCase()) {
            "true", "yes" -> true
            "false", "no" -> false
            else -> wtf("relaxedToBoolean for [$this]")
        }
    }
}

fun jsProto(x: Any): Any? = x.asDynamic().__proto__

fun touchObjectGraph(parent: Any?, debugParentPath: String = "root", visited: MutableSet<Any> = mutableSetOf()) {
    if (parent == null || jsTypeOf(parent) != "object") return

    if (parent in visited) return
    visited += parent

    val ownNames = JSObject.getOwnPropertyNames(parent)

    val protoChainNames = mutableListOf<String>()
    var proto = jsProto(parent)
    while (proto != null) {
        val protoNames = JSObject.getOwnPropertyNames(proto)
        protoChainNames += protoNames
        proto = jsProto(proto)
    }

    for (name in ownNames + protoChainNames) {
        // dlog("Touching property: $debugParentPath.$name")
        val value = try {
            parent.asDynamic()[name]
        } catch(e: dynamic) { // Various obscure shit do happen when traversing objects like that...
            continue
        }
        touchObjectGraph(value, "$debugParentPath.$name", visited)
    }
}

fun Double.toPhysicalPixelsDouble(): Double = this * window.devicePixelRatio

fun Double.toPhysicalPixels(): Int = Math.round(toPhysicalPixelsDouble())

fun Int.toLayoutPixels(): Double = this / window.devicePixelRatio

fun tillHourPasses(): Promise<Unit> = delay(1000 * 60 * 60)

fun dateNow(): Int = js("Date.now()")

object NamesOfThings {
    private val thingToName = WeakMap<Any, String>()
    private val sourceToSinks = WeakMap<Any, MutableList<Any>>()

    operator fun set(thing: Any, name: String) {
        thingToName[thing] = name
        val sinks = sourceToSinks[thing]
        sinks?.forEach {set(it, name)}
    }

    operator fun get(thing: Any): String? {
        return thingToName[thing]
    }

    fun flow(from: Any, to: Any) {
        val sinks = sourceToSinks.getOrPut(from) {mutableListOf()}
        sinks += to

        get(from)?.let {name->
            sinks.forEach {set(it, name)}
        }
    }

    fun unflow(from: Any, to: Any) {
        sourceToSinks[from]?.let {sinks->
            sinks -= to
        }
    }
}

fun <T: Any> notNullNamed(): ReadWriteProperty<Any?, T> = NotNullNamedVar(null)

fun <T: Any> notNullNamed(initial: T, parentNamed: Any? = null): ReadWriteProperty<Any?, T> = NotNullNamedVar(initial, parentNamed)

private class NotNullNamedVar<T: Any>(initial: T?, val parentNamed: Any? = null) : ReadWriteProperty<Any?, T> {
    private var value: T? = initial

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val res = value ?: throw IllegalStateException("Property ${property.name} should be initialized before get.")
        val namePrefix = parentNamed?.let {NamesOfThings[it]}?.let {"$it."} ?: ""
        NamesOfThings[res] = namePrefix + property.name
        return res
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}

val CSSRule.Companion.KEYFRAMES_RULE: Short get() = 7
val CSSRule.Companion.KEYFRAME_RULE: Short get() = 8

val progressTickerKeyframe100RuleStyle: CSSStyleDeclaration by lazy {
    for (sheetIndex in 0 until document.styleSheets.length) {
        val styleSheet = document.styleSheets[sheetIndex] as CSSStyleSheet
        val rules = styleSheet.cssRules
        for (ruleIndex in 0 until rules.length) {
            val rule = rules[ruleIndex]!!
            if (rule.type == CSSRule.KEYFRAMES_RULE) {
                if (rule.asDynamic().name == "progressTicker") {
                    val subRules: CSSRuleList = rule.asDynamic().cssRules
                    val rule100 = subRules[1]!!
                    check(rule100.asDynamic().keyText == "100%") {"keyText"}
                    val rule100Style: CSSStyleDeclaration = rule100.asDynamic().style
                    return@lazy rule100Style
                }
            }
        }
    }
    bitch("Cannot find progressTickerKeyframe100RuleStyle")
}

val progressTickerRuleStyle: CSSStyleDeclaration by lazy {
    for (sheetIndex in 0 until document.styleSheets.length) {
        val styleSheet = document.styleSheets[sheetIndex] as CSSStyleSheet
        val rules = styleSheet.cssRules
        for (ruleIndex in 0 until rules.length) {
            val rule = rules[ruleIndex]!!
            if (rule.type == CSSRule.STYLE_RULE) {
                val styleRule = rule as CSSStyleRule
                if (styleRule.selectorText == ".progressTicker") {
                    return@lazy styleRule.style
                }
            }
        }
    }
    bitch("Cannot find .progressTicker rule")
}

fun freezeProgressTicker() {
    progressTickerKeyframe100RuleStyle.opacity = "1"
}

fun unfreezeProgressTicker() {
    progressTickerKeyframe100RuleStyle.opacity = "0"
}

fun isModalShown() = jq(".modal-backdrop").length > 0



















