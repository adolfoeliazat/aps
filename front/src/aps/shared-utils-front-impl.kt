/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

import aps.front.clog

fun dlog(vararg xs: dynamic) {
    clog("[DEBUG]", *xs)
}


