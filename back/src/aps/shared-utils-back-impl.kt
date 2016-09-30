/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

import aps.back.debugLog

fun dlog(vararg xs: Any?) {
    debugLog.info(xs.joinToString(" "))
}

fun t(en: String, ru: String) = ru

val noImpl: Nothing get() = throw Exception("Don't call me, motherfucker")


