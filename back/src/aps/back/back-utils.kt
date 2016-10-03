/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun <T: Any> T.logger(): Lazy<Logger> {
    return lazy { LoggerFactory.getLogger(this.javaClass) }
}

val debugLog = LoggerFactory.getLogger("::::: DEBUG :::::")


