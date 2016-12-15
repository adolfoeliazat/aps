/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.PrintWriter
import java.io.StringWriter

fun <T: Any> T.logger(): Lazy<Logger> {
    return lazy { LoggerFactory.getLogger(this.javaClass) }
}

val debugLog = LoggerFactory.getLogger("::::: DEBUG :::::")

fun Throwable.stackString(): String {
    val sw = StringWriter()
    PrintWriter(sw).use {this.printStackTrace(it)}
    return sw.toString()
}

inline fun <T : AutoCloseable?, R> T.use(block: (T) -> R): R {
    var closed = false
    try {
        return block(this)
    } catch (e: Throwable) {
        closed = true
        if (this != null) {
            try {
                this.close()
            } catch (closeException: Throwable) {
                e.addSuppressed(closeException)
            }
        }
        throw e
    } finally {
        if (this != null && !closed) {
            close()
        }
    }
}

fun eprintln(msg: String = "") {
    System.err.println(msg)
}

inline fun dwarnStriking(vararg xs: Any?) = dwarn("\n\n", "**********", *xs, "\n")



