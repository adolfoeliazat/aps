/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

fun sayHi() {
    println("Hi, fuck you")
}

inline fun <T, R> build(receiver: T, block: T.() -> R): T { receiver.block(); return receiver }

fun <T> T.oneOf(vararg xs: T) = xs.contains(this)

