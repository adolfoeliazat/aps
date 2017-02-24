/*
 * Into Kommon
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package into.kommon

import aps.*

val KOMMON_HOME: String get()= getenv("INTO_KOMMON_HOME") ?: die("I want INTO_KOMMON_HOME environment variable")

fun bitch(msg: String = "Just bitching..."): Nothing = throw Exception(msg)
fun imf(what: String = "me"): Nothing = throw Exception("Implement $what, please, fuck you")
fun wtf(msg: String = "...WTF didn't you describe this WTF?"): Nothing = throw Exception("WTF: $msg")
fun die(msg: String = "You've just killed me, motherfucker!"): Nothing = throw Exception("Aarrgghh... $msg")
fun fuckOff(msg: String = "Don't call me"): Nothing = throw Exception("Fuck off... $msg")
fun unsupported(what: String = "Didn't bother to describe"): Nothing = throw Exception("Unsupported: $what")

fun <R> measuringAndPrinting(block: () -> R): R {
    val start = currentTimeMillis()
    val res = block()
    val ms = currentTimeMillis() - start
    println("COOL [${ms}ms]")
    return res
}

inline fun ifOrEmpty(test: Boolean, block: () -> String): String =
    if (test) block()
    else ""

inline fun <T> T.letu(block: (T) -> Unit): Unit = block(this)












