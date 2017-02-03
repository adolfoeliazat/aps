/*
 * Into Kommon
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

import kotlin.js.Date

val global: dynamic get() = if (aps.isBrowser()) js("window") else js("global")
val gloshit: dynamic get() = aps.global

fun isBrowser(): Boolean = js("typeof window") == "object"
fun isNodeJS(): Boolean = !aps.isBrowser()

val process: dynamic get()= js("process")

fun currentTimeMillis(): Long = Date().getTime().toLong()
fun currentTimeInt(): Double = Date().getTime()

fun getenv(name: String): String? = aps.process.env[name]

fun <T> newNativePromise(arg: dynamic): Promise<T> {
    return js("new Promise(arg)")
}

external @JsName("Object")
object JSObject {
    fun keys(x: Any): Array<String>
    fun getOwnPropertyNames(x: Any): Array<String>
}

external @JsName("require")
fun nodeRequire(module: String): dynamic

