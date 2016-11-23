/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import aps.front.DebugNoise.Style.*

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

class DebugNoise(val tag: String, val mute: Boolean, val style: Style = IN_THREE_DASHES) {
    enum class Style {IN_THREE_DASHES, COLON}

    inline fun clog(vararg xs: Any?) {
        if (!mute) {
            exhaustive/when (style) {
                IN_THREE_DASHES -> aps.clog("---$tag---", *xs)
                COLON -> aps.clog("$tag: ", *xs)
            }
        }
    }
}









