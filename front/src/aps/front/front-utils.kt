/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

class JSException(override val message: String, val asyncStack: String? = null) : Throwable(message) {
    val stack = js("Error")(message).stack
}

fun bitch(msg: String = "Just bitching..."): Nothing = throw JSException(msg)
fun imf(what: String = "me"): Nothing = throw JSException("Implement $what, please, fuck you")
fun wtf(msg: String = "...WTF didn't you describe this WTF?"): Nothing = throw JSException("WTF: $msg")
fun die(msg: String = "You killed me, motherfucker..."): Nothing = throw JSException(msg)

fun clog(vararg xs: dynamic) {
    global.console.log.apply(global.console.log, xs.toList().toJSArray())
}

@native interface JSArray

fun <T> Array<T>.toJSArray(): JSArray = this.asIterable().toJSArray()

fun <T> Iterable<T>.toJSArray(): JSArray {
    val res = js("[]")
    this.forEach { res.push(it) }
    return res
}


