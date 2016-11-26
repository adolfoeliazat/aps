/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps

import aps.DebugNoise.Style.*
import into.kommon.*
import kotlin.reflect.KProperty

val APS_HOME: String get() = getenv("APS_HOME") ?: die("I want APS_HOME environment variable")
val GENERATOR_BAK_DIR: String get() = "c:/tmp/aps-bak" // TODO:vgrechka @unhardcode
val TMP_DIR: String get() = "c:/tmp/aps-tmp" // TODO:vgrechka @unhardcode
val IDEA_EXE: String get() = "C:/opt/idea-eap/bin/idea64.exe" // TODO:vgrechka @unhardcode

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

// Rapier operator: x-{o->
inline operator fun <T, FRet> T.minus(f: (T) -> FRet): T { f(this); return this }

inline fun <T, reified CastTo> T.calet(f: (T) -> Unit): Unit { f(this) }

inline fun <T> T?.letoes(f: (T) -> String): String =
    if (this == null) ""
    else f(this)

fun <T> T.oneOf(vararg xs: T) = xs.contains(this)

class relazy<out T>(val initializer: () -> T) {
    private var backing = lazy(initializer)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = backing.value

    fun reset() {
        backing = lazy(initializer)
    }
}

inline fun dwarnStriking(vararg xs: Any?) = dwarn("**********", *xs)


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

object exhaustive {
    infix operator fun <T> div(rhs: T): Unit {}
}

fun <T> cast(x: Any?): T = x as T

fun want(b: Boolean, msg: () -> String = {"I want that"}) {
    if (!b) wtf(msg())
}

fun wantNull(x: Any?, msg: () -> String = {"I want null here"}) = want(x == null, msg)

fun <T> nif(cond: Boolean, block: () -> T): T? = if (cond) block() else null

val nbsp: String = "" + 0xa0.toChar()
val mdash = "—"
val ndash = "–"
val threeQuotes = "\"\"\""

fun String.indexOfOrDie(needle: String): Int {
    val idx = this.indexOf(needle)
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
            exhaustive/when (style) {
                IN_THREE_DASHES -> aps.clog("---$tag---", *xs)
                COLON -> aps.clog("$tag: ", *xs)
            }
        }
    }
}











