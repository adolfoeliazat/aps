/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import org.w3c.dom.*
import org.w3c.dom.css.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Json
import kotlin.js.Math
import kotlin.js.RegExp
import kotlin.properties.Delegates.notNull
import kotlin.properties.ReadOnlyProperty
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

external interface JSArray

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

//    var haystack = Globus.location.search
    var haystack = href
    haystack = haystack.substring(haystack.indexOf('?') + 1, haystack.length)

    while (true) {
        match = regex.exec(haystack)
        if (!match) break
        store[global.decodeURIComponent(match[1])] = global.decodeURIComponent(match[2])
    }

    return store
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

fun tillAnimationFrame(): Promisoid<Unit> = Promisoid {resolve, reject ->
    requestAnimationFrame {
        resolve(Unit)
    }
}

suspend fun waitAnimationFrame() {
    await(tillAnimationFrame())
}

suspend fun waitVisualShit() {
    sleep(0)
    waitAnimationFrame()
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
        .replace("<a", "\n<a")
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

val testArtifactClasses = listOf(
    css.textField.labelContainerTestHint
)

fun stripUninterestingElements(jqel: JQuery): HTMLElement {
    fun descend_stripUninterestingElements(el: HTMLElement) {
        if (el.tagName == "SCRIPT" || el.style.display == "none") return el.remove()
        val classNames = el.className.split(Regex("\\s+"))
        if (classNames.contains("ignoreInTests")) return el.remove() // TODO:vgrechka @killme
        if (testArtifactClasses.any {classNames.contains(it)}) return el.remove()

        if (el.id.startsWith("MakeStaticSites-")) el.id = "whocares"
        if (el.id.startsWith("pane-")) el.id = "pane-whocares"

        // XXX
//        if (el.className.contains("effects-blinker")) {
//            val top = el.style.top
//            dwarnStriking(el)
//            val mr = Regex("calc\\(\\(\\((.*?)px - (.*?)\\) \\+ (.*?)\\) - (.*?)px\\)").matchEntire(top) ?: die("Unexpected `top` in `effects-blinker`: $top")
//            val a = mr.groupValues[1].toDouble()
//            val b = mr.groupValues[2]
//            val c = mr.groupValues[3]
//            val d = mr.groupValues[4].toDouble()
//            el.style.top = "/*fiddled*/calc(((${a-d}px - $b) + $c) - 0px)"
//        }

        val children = el.children.asList().toList() // Copying it because `children.asList()` is live
        for (child in children) {
            descend_stripUninterestingElements(child as HTMLElement)
        }
    }

    val root = jqel[0]!!.cloneNode(/*deep*/ true) as HTMLElement
    descend_stripUninterestingElements(root)
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

//inline fun currentJSFunctionName(): String {
//    val stack: String = js("Error().stack")
//    // dwarnStriking(stack)
//    val lines = stack.lines()
//    check(lines.size >= 2)
//    var line = lines[1]
//    check(line.startsWith("    at "))
//    line = line.substring("    at ".length)
//    val spaceIndex = line.indexOf(" ")
//    if (spaceIndex != -1) line = line.substring(0, spaceIndex)
//    val dotIndex = line.lastIndexOf(".")
//    if (dotIndex != -1) line = line.substring(dotIndex + 1)
//    if (Regex("_......\\\$").containsMatchIn(line)) line = line.substring(0, line.lastIndexOf("_"))
//    return line
//}


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

object scroll {
    object element {
        suspend fun toBottomGradually(jqel: JQuery) {
            sleep(0)
            val el = bang(jqel[0])
            val targetTop = el.scrollHeight - el.offsetHeight
            scrollElementGradually(jqel, targetTop.toDouble())
        }

    }

    object body {
        class toShitGradually(
            val dy: Int = -10,
            val bursts: Int = fconst.defaultScrollBursts,
            val dontScrollToTopItem: Boolean = false,
            val getShit: () -> JQuery
        ) {
            suspend fun go() = await(promise())

            fun promise() = async {
                await(tillAnimationFrame())
                val shit = getShit()
                check(shit[0] != null) {"Shit to scroll to is not found"}

                if (dontScrollToTopItem) {
                    if (shit.hasClass(css.lipsItem)) {
                        var topItem = shit[0]!!
                        jq(".${css.lipsItem}").each {_, el ->
                            if (el.offsetTop < topItem.offsetTop)
                                topItem = el
                        }
                        if (shit[0] == topItem) return@async
                    }
                }

                val targetTop: Double
                targetTop = shit.offset().top - const.topNavbarHeight + dy
                await(graduallyPromise(targetTop, bursts))
            }
        }

        fun graduallyPromise(targetTop: Double, bursts: Int = fconst.defaultScrollBursts) = async {
            scrollElementGradually(jqbody, targetTop, bursts)
        }

        suspend fun gradually(targetTop: Double, bursts: Int = fconst.defaultScrollBursts) {
            await(graduallyPromise(targetTop, bursts))
        }

        suspend fun gradually(targetTop: Int, bursts: Int = fconst.defaultScrollBursts) {
            gradually(targetTop.toDouble(), bursts)
        }

        suspend fun toBottomGradually() {
            sleep(0)
            gradually(jqbody.height().toDouble() - jqwindow.height().toDouble() + const.topNavbarHeight)
        }

        suspend fun toTopGradually() {
            sleep(0)
            gradually(0.0)
        }

        fun toShit(shit: JQuery, dy: Int = 0) {
            jqbody.scrollTop(shit.offset().top - 50 + dy)
        }

        suspend fun toMelindaItemGradually(id: Long) {
            val elementID = TestGlobal.melindaItemIDToHeaderElementID[id] ?: wtf("id = $id    4955591e-85ee-4c2b-8540-ad010d2f5352")
            toShitGradually{byid(elementID)}.go()
        }
    }
}



private suspend fun scrollElementGradually(jqel: JQuery, targetTop: Double, bursts: Int = fconst.defaultScrollBursts) {
    val startTop = jqel.scrollTop()
    for (i in 1..bursts) {
        await(tillAnimationFrame())
        jqel.scrollTop(startTop + (targetTop - startTop) / bursts * i)
    }
    jqel.scrollTop(targetTop)
}


suspend fun scrollElementToTopGradually(jqel: JQuery) {
    sleep(0)
    val el = bang(jqel[0])
    val targetTop = 0
    scrollElementGradually(jqel, targetTop.toDouble())
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

fun tillHourPasses(): Promisoid<Unit> = delay(1000 * 60 * 60)

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


//fun isModalShown() = jq(".modal-backdrop").length > 0

fun <T> Array<T>.jsPush(item: T) {
    this.asDynamic().push(item)
}

inline fun unit(block: () -> Unit) {
    block()
}

fun <T> flatten(xs: List<List<T>>) =
    xs.fold(listOf<T>(), {all, chunk -> all + chunk})

fun <T: Any> mere(value: T) = object:ReadOnlyProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value
}












