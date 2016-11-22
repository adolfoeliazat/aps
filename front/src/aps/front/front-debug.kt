/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

// TODO:vgrechka @revisit
object __dlog {
    val requestJSONObject = Dlogger(false, "requestJSONObject")
    val responseJSONObject = Dlogger(false, "responseJSONObject")
    val pushNavigate = Dlogger(true, "pushNavigate")

    class Dlogger(val enabled: Boolean, val prefix: String) {
        operator fun invoke(vararg args: Any?) {
            if (enabled) {
                dlog(prefix, *args)
            }
        }
    }
}

class DebugNoise(val tag: String, val mute: Boolean) {
    inline fun clog(vararg xs: Any?) {
        if (!mute) aps.clog("---$tag---", *xs)
    }
}









