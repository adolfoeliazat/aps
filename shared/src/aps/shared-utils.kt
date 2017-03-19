/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps

import aps.DebugNoise.Style.*
import aps.front.*
import kotlin.properties.Delegates
import kotlin.properties.Delegates.notNull
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun formatFileSizeApprox(lang: Language, totalBytes: Int): String {
    val kb = 1024
    val mb = 1024 * kb
    val gb = 1024 * mb

    if (totalBytes >= gb) bitch("You fucking crazy, I'm not dealing with gigabyte files")

    val point = when (lang) {
        Language.EN -> "."
        Language.UA -> ","
    }

    val megs = totalBytes / mb
    val kils = (totalBytes - megs * mb) / kb
    val bytes = totalBytes - megs * mb * kils * kb

    if (megs > 0) return "" +
        megs +
        (if (kils >= 100) "$point${kils / 100}" else "") +
        when (lang) {
            Language.EN -> " MB"
            Language.UA -> " МБ"
        }

    if (kils > 0) return "" +
        kils +
        when (lang) {
            Language.EN -> " KB"
            Language.UA -> " КБ"
        }

    return "" +
        bytes +
        when (lang) {
            Language.EN -> " B"
            Language.UA -> " Б"
        }
}

fun Any?.toUnit() {}

fun String?.nullifyBlank(): String? =
    if (this.isNullOrBlank()) null
    else this

fun <T> tryOrDefault(attempt: () -> T, default: () -> T): T =
    try {attempt()}
    catch (e: Throwable) {default()}

fun markdownItalicVerbatim(s: String): String {
    return "_" + escapeMarkdown(s) + "_"
}

fun escapeMarkdown(s: String): String {
    return s.replace("_", "\\_")
}

class AbortException : Throwable()
fun abort() {throw AbortException()}

class SkipException : Throwable()
fun skip() {throw SkipException()}

inline fun <T> Sequence<T>.saforEachIndexed(action: (Int, T) -> Unit): Unit {
    var index = 0
    for (item in this) {
        try {
            action(index++, item)
        } catch (e: AbortException) {
            break
        } catch (e: SkipException) {
            continue
        }
    }
}

//inline fun arun(block: () -> Unit) {
//    try {
//        block()
//    } catch (e: Abortion) {
//        // That's OK
//    }
//}

inline fun <T, FRet> T.applet(f: (T) -> FRet): T { f(this); return this }

inline fun <T, reified CastTo> T.calet(f: (T) -> Unit): Unit { f(this) }

inline fun <T> T?.letOrEmpty(f: (T) -> String): String =
    if (this == null) ""
    else f(this)

inline fun <T> T?.letoes(f: (T) -> String): String = this.letOrEmpty(f)

fun <T> T.oneOf(vararg xs: T) = xs.contains(this)

class relazy<out T>(val initializer: () -> T) {
    private var backing = lazy(initializer)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = backing.value

    fun reset() {
        backing = lazy(initializer)
    }
}



class Probe<T>(val transform: (T) -> Any?)
fun <T> probe(transform: (T) -> Any?) = Probe(transform)

infix operator fun <T> T.div(probe: Probe<T>): T {
    dwarn("--[probe]----------> ", probe.transform(this))
    return this
}

object probe
infix operator fun <T> T.div(the: probe): T = this / probe {it}


//fun probe(msg: Any?): ProbeLHS {
//    dlog("--[probe]----------> ", msg)
//    return ProbeLHS
//}
//
//object ProbeLHS {
//    infix operator fun <T> div(rhs: T): T = rhs
//}

fun checkAtMostOneOf(vararg pairs: Pair<String, Any?>) {
    val definedNames = pairs.filter{it.second != null}.map{it.first}
    check(definedNames.size <= 1) {"These motherfuckers violate checkAtMostOneOf: " + definedNames.joinToString()}
}

fun <T> firstNotNull(vararg branches: () -> T?): T? {
    for (branch in branches) {
        branch()?.let {
            return it
        }
    }
    return null
}

fun <T> String?.ifNotBlankApplet(block: (String) -> T?): T? =
    if (this.isNullOrBlank()) null
    else block(this!!)

fun <T> checking(cond: Boolean, block: () -> T) =
    if (cond) block()
    else die("Fucking illegal state")

fun dedent(it: String): String {
    var lines = it.split(Regex("\\r?\\n"))
    if (lines.size > 0 && lines[0].isBlank()) {
        lines = lines.drop(1)
    }
    if (lines.size > 0 && lines.last().isBlank()) {
        lines = lines.dropLast(1)
    }

    var minIndent = 9999 // TODO:vgrechka Platform-specific max integer (for JS: Number.MAX_SAFE_INTEGER)
    for (line in lines) {
        if (!line.isBlank()) {
            val lineIndent = line.length - line.trimStart().length
            if (lineIndent < minIndent) {
                minIndent = lineIndent
            }
        }
    }

    return lines.map {line ->
        if (line.trim().isBlank()) ""
        else line.substring(minIndent)
    }.joinToString("\n")
}

fun reindent(newIndent: Int, it: String): String {
    return dedent(it).split("\n").joinToString("\n") {" ".repeat(newIndent) + it}
}

var puidPrefix = ""

var _puid = 1L
fun puid(): String = puidPrefix + _puid++

fun <T> Iterable<T>.without(xs: Iterable<T>) = this.filter{!xs.contains(it)}

fun <T> cast(x: Any?): T = x as T

fun want(b: Boolean, msg: () -> String = {"I want that"}) {
    if (!b) wtf(msg())
}

fun wantNull(x: Any?, msg: () -> String = {"I want null here"}) = want(x == null, msg)

fun <T> nif(cond: Boolean, block: () -> T): T? = if (cond) block() else null

fun String.indexOfOrDie(needle: String, startIndex: Int = 0): Int {
    val idx = this.indexOf(needle, startIndex)
    if (idx == -1) die("Needle not found: [$needle]")
    return idx
}

fun String.lastIndexOfOrDie(needle: String, startIndex: Int = lastIndex): Int {
    val idx = this.lastIndexOf(needle, startIndex)
    if (idx == -1) die("Needle not found: [$needle]")
    return idx
}

inline fun loopNotTooLong(maxIterations: Int = 1000, block: () -> Unit) {
    for (i in 1..maxIterations) {
        try {
            block()
        } catch (e: SkipException) {
            continue
        } catch (e: AbortException) {
            return
        }
    }

    die("It took too long")
}

fun <T> Iterable<T>.toDebugString(): String {
    return "\n" + this.mapIndexed{i, x -> "$i) $x"}.joinToString("\n")
}

class DebugNoise(val tag: String, val mute: Boolean, val style: Style = IN_THREE_DASHES) {
    enum class Style {IN_THREE_DASHES, COLON}

    inline fun clog(vararg xs: Any?) {
        if (!mute) {
            exhaustive=when (style) {
                IN_THREE_DASHES -> aps.clog("---$tag---", *xs)
                COLON -> aps.clog("$tag: ", *xs)
            }
        }
    }
}

class CaptureStackException : Exception("Hi, fucker") {
}

fun String.escapeSingleQuotes(): String =
    this.replace("'", "\\'")

//inline fun <reified E : Enum<E>> relaxedStringToEnum(s: String?, default: E): E =
//    enumValues<E>().find {it.name.toUpperCase() == s?.toUpperCase()}
//        ?: default

fun <E : Enum<E>> String?.relaxedToEnumOrNull(values: Array<E>) =
    values.find {it.name.toUpperCase() == this?.toUpperCase()}

fun <E : Enum<E>> String?.relaxedToEnum(values: Array<E>, default: E): E {
    return relaxedToEnumOrNull(values) ?: default
}

fun <E : Enum<E>> String?.relaxedToEnumOrDie(values: Array<E>): E =
    relaxedToEnumOrNull(values) ?: wtf("this = $this    c526f090-69ce-4ffe-9faa-beb678c6e409")

fun <T: Any> selfy(make: (() -> T) -> T): T {
    val obj = object {
        var prop by Delegates.notNull<T>()
    }
    val value = make(obj::prop)
    obj.prop = value
    return value
}

fun <This, Value : Any> lazyEx(makeValue: (This, KProperty<*>) -> Value): ReadOnlyProperty<This, Value> =
    object : ReadOnlyProperty<This, Value> {
        private var value: Value? = null

        override fun getValue(thisRef: This, property: KProperty<*>): Value {
            if (value == null) {
                value = makeValue(thisRef, property)
            }
            return value!!
        }
    }

class eagerEx<in This, out Value : Any>(val makeValue: (This, KProperty<*>) -> Value) {
    private var value by notNull<Value>()

    operator fun provideDelegate(thisRef: This, property: KProperty<*>): ReadOnlyProperty<This, Value> {
        value = makeValue(thisRef, property)
        return object : ReadOnlyProperty<This, Value> {
            override fun getValue(thisRef: This, property: KProperty<*>): Value {
                return value
            }
        }
    }
}

abstract class KeyDef {
    @Suppress("LeakingThis")
    protected val name = this::class.simpleName!!
}

fun StringBuilder.appendln(s: String) {
    append(s + "\n")
}

fun StringBuilder.lnappendln2(s: String) {
    append("\n" + s + "\n\n")
}


class TestRef<out T>(val it: T)















