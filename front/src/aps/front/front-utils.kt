/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

class JSException(override val message: String, val asyncStack: String? = null) : Throwable(message) {
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















