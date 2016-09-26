/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun bitch(msg: String = "Just bitching..."): Nothing = throw Exception(msg)
fun imf(what: String = "me"): Nothing = throw Exception("Implement $what, please, fuck you")
fun wtf(msg: String = "...WTF didn't you describe this WTF?"): Nothing = throw Exception("WTF: $msg")

fun <T: Any> T.logger(): Lazy<Logger> {
    return lazy { LoggerFactory.getLogger(this.javaClass) }
}

val debugLog = LoggerFactory.getLogger("::::: DEBUG :::::")


