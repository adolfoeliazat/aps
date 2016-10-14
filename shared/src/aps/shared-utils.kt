/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

import kotlin.reflect.KProperty

fun sayHi() {
    println("Hi, fuck you")
}

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

fun probe(msg: Any?): ProbeLHS {
    dlog("--[probe]----------> ", msg)
    return ProbeLHS
}

object ProbeLHS {
    infix operator fun <T> div(rhs: T): T = rhs
}

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

var _puid = 1L
fun puid(): String = "" + _puid++

fun <T> Iterable<T>.without(xs: Iterable<T>) = this.filter{!xs.contains(it)}

object exhaustive {
    infix operator fun <T> div(rhs: T): Unit {}
}

fun <T> cast(x: Any?): T = x as T

fun want(b: Boolean, msg: () -> String = {"I want that"}) {
    if (!b) wtf(msg())
}

fun wantNull(x: Any?, msg: () -> String = {"I want null here"}) = want(x == null, msg)

















