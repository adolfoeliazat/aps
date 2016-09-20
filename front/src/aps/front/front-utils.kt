/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */
package aps.front

class JSException(override val message: String) : Throwable(message) {
    val stack = js("Error")(message).stack
}

fun bitch(msg: String = "Just bitching..."): Nothing = throw JSException(msg)

