/*
 * Into Kommon
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

val global: dynamic get() = if (aps.isBrowser()) js("window") else js("global")
val gloshit: dynamic get() = aps.global

fun isBrowser(): Boolean = js("typeof window") == "object"
fun isNodeJS(): Boolean = !aps.isBrowser()

val process: dynamic get()= js("process")

fun currentTimeMillis(): Long = Date().getTime().toLong()
fun currentTimeInt(): Int = Date().getTime()

fun getenv(name: String): String? = aps.process.env[name]

fun <T> newNativePromise(arg: dynamic): Promise<T> {
    return js("new Promise(arg)")
}

external @JsName("Object")
object JSObject {
    fun keys(x: Any): Array<String> = noImpl
    fun getOwnPropertyNames(x: Any): Array<String> = noImpl
}

@native @JsName("require")
fun nodeRequire(module: String): dynamic = noImpl

