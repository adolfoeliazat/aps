/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

object Shitus {
    val delay = ::jsFacing_delay
    val run = ::jsFacing_run
    val runa = ::jsFacing_runa
    val repeat = ::jsFacing_repeat
    val fov = ::jsFacing_fov
    val fova = ::jsFacing_fova
    val tokens = ::jsFacing_tokens
    val values = ::jsFacing_values
    val invariant = ::jsFacing_invariant
    val raise = ::jsFacing_raise
    val isEqual = ::jsFacing_isEqual
    val clone = ::jsFacing_clone
    val isObject = ::jsFacing_isObject
}

fun jsFacing_arrayDeleteFirstThat(arr: dynamic, pred: dynamic): dynamic {
    for (i in 0 until arr.length) {
        if (pred(arr[i])) {
            arr.splice(i, 1)
            return arr
        }
    }
    return arr
}

fun jsFacing_deleteKey(obj: dynamic, key: dynamic) {
    js("delete obj[key]")
}

fun jsFacing_delay(ms: dynamic): dynamic {
    return newNativePromise({resolve: dynamic ->
        global.setTimeout(resolve, ms)
    })
}

fun jsFacing_run(f: dynamic): dynamic {
    return f()
}

fun jsFacing_runa(f: dynamic): Promise<dynamic> {"__async"
    return __asyncResult(__await(f()))
}

fun jsFacing_repeat(s: String, count: Int): String {
    return s.repeat(count)
}

fun jsFacing_fov(): dynamic {
    val all = js("Array.prototype.slice.call(arguments)")
    val x = all[0]
    return if (jsTypeOf(x) == "function") x.apply(null, all.slice(1)) else x
}

fun shittyFov(x: dynamic): dynamic {
    return if (jsTypeOf(x) == "function") x() else x
}

fun shittyFov(x: dynamic, y: dynamic): dynamic {
    return if (jsTypeOf(x) == "function") x(y) else x
}

fun jsFacing_fova(): Promise<dynamic> {"__async"
    val all = js("Array.prototype.slice.call(arguments)")
    val x = all[0]
    return if (jsTypeOf(x) == "function") __await<dynamic>(x.apply(null, x.slice(1))) else x
}

fun jsFacing_tokens(s: dynamic): dynamic {
    return s.trim().split(global.RegExp("\\s+"))
}

fun jsFacing_values(x: dynamic): dynamic {
    val res = js("[]")
    for (k in jsArrayToList(global.Object.keys(x)))
        res.push(x[k])
    return res
}

fun jsFacing_invariant(cond: dynamic, msg: dynamic) {
    if (!cond) {
        val message = "[INVARIANT VIOLATION] " + msg
        console.error(message)
        Shitus.raise(message)
    }
}

fun jsFacing_raise(msg: dynamic) {
    throw JSException(msg)
}

fun jsFacing_isEqual(a: Any?, b: Any?): Boolean {
    imf("jsFacing_isEqual")
}

fun jsFacing_clone(x: Any?): Any? {
    imf("jsFacing_clone")
}

fun jsFacing_isObject(value: dynamic): Boolean {
    val type = jsTypeOf(value)
    return value != null && (type == "object" || type == "function");
}
















