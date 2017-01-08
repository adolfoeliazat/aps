/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*
import jquery.JQuery

object effects2 {
    fun blinkOn(
        target: JQuery,
        fixed: Boolean = false,
        dleft: Int = 0,
        dtop: Int = 0,
        dwidth: Int = 0,
        widthCountMargin: Boolean = true,
        heightCountMargin: Boolean = true,
        widthCalcSuffix: String? = null
    ) {
        effects.blinkOn(json(
            "target" to target,
            "fixed" to fixed,
            "dleft" to dleft,
            "dtop" to dtop,
            "dwidth" to dwidth,
            "widthCountMargin" to widthCountMargin,
            "heightCountMargin" to heightCountMargin,
            "widthCalcSuffix" to widthCalcSuffix
        ))
    }

    fun blinkOff() {
        effects.blinkOff()
    }

    fun blinkOffFadingOut() {
        effects.blinkOffFadingOut()
    }

    fun fadeOut(elementID: String): Promise<Unit> = async {
        fun setOpacity(value: Double) {
            byid0(elementID)!!.style.opacity = value.toString()
        }

        val animationTime = 250
        val startTime = dateNow()
        val endTime = startTime + animationTime
        while (true) {
            await(tillAnimationFrame())

            val now = dateNow()
            if (now > endTime) {
                setOpacity(0.0)
                break
            }

            val advancedInTimeFraction = (now - startTime).toDouble() / (endTime - startTime)
            val opacity = 1.0 - advancedInTimeFraction
            // dlog("opacity = $opacity")
            setOpacity(opacity)
        }
    }
}

@native interface ILegacyEffects {
    val element: ReactElement
    fun blinkOn(arg: dynamic)
    fun blinkOff()
    fun blinkOffFadingOut()
}

var effects: ILegacyEffects = null.asDynamic()

fun initEffects() {
    effects = Shitus.statefulElement(ctor@{update ->
        var me: dynamic = null
        var blinker: dynamic = null
        var blinkerInterval: dynamic = null

        me = json(
            "render" to {
                Shitus.diva(json(), blinker)
            },

            "blinkOn" to {arg: dynamic ->
                val target: dynamic = arg.target
                val fixed: dynamic = arg.fixed
                val dleft: dynamic = arg.dleft ?: 0
                val dtop: dynamic = arg.dtop ?: 0
                val dwidth: dynamic = arg.dwidth ?: 0
                val widthCountMargin: dynamic = arg.widthCountMargin ?: true
                val heightCountMargin: dynamic = arg.heightCountMargin ?: true
                val widthCalcSuffix: String? = arg.widthCalcSuffix

                me.blinkOff()

                val targetOffset = target.offset()
                val targetWidth = target.outerWidth(widthCountMargin)
                val targetHeight = target.outerHeight(heightCountMargin)
                val width = targetWidth + dwidth
                val height = 3
                val left = targetOffset.left + dleft
                var top = targetOffset.top + targetHeight - height + dtop
                if (fixed) {
                    top -= js("$")(global.document).scrollTop()
                }
                // dlog({left, top, width, height})

                val blinkerStyle = json(
                    "position" to if (fixed) "fixed" else "absolute",
                    "zIndex" to 10000,
                    "backgroundColor" to Color.BLUE_GRAY_600.toString(),
                    "left" to left,
                    "top" to top,
                    "width" to run {
                        var s = width.toString() + "px"
                        widthCalcSuffix?.let {
                            s = "calc($s $it)"
                        }
                        s
                    },
                    "height" to height)
                // clog("blinkerStyle", blinkerStyle)

                blinker = Shitus.diva(json(
                    "id" to "fucking-blinker",
                    "className" to "progressTicker",
                    "style" to blinkerStyle))
                update()
            },

            "blinkOff" to {
                blinker = null
                update()
            },

            "blinkOffFadingOut" to {async<Unit> {
                val el = byid0("fucking-blinker")!!
                el.className = ""
                el.style.opacity = "0.5"
                el.style.transition = "opacity 0.5s"
                await(delay(0)) // XXX requestAnimationFrame doesn't help
                el.style.opacity = "0"
            }}
        )

        return@ctor me
    })

    global.requestAnimationFrame({
        debugPanes.remove("initEffects")
        debugPanes.put("initEffects", oldShitAsToReactElementable(effects.element))
    })
}





