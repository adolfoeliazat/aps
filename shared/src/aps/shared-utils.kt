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

inline fun <T> T.applet(block: (T) -> Unit): T { block(this); return this }

fun <T> T.oneOf(vararg xs: T) = xs.contains(this)

class relazy<T>(val initializer: () -> T) {
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
