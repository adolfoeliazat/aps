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



















