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

