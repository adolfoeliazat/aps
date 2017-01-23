/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import jquery.JQuery

val effects by PassivableHolder(EffectsInitializer())

class Effects {
    val legacyEffects: dynamic = makeLegacyEffects()

    fun blinkOn(target: JQuery, opts: BlinkOpts = BlinkOpts()) {
        legacyEffects.blinkOn(json(
            "target" to target,
            "fixed" to opts.fixed,
            "dleft" to opts.dleft,
            "dtop" to opts.dtop,
            "dwidth" to opts.dwidth,
            "widthCountMargin" to opts.widthCountMargin,
            "heightCountMargin" to opts.heightCountMargin,
            "widthCalcSuffix" to opts.widthCalcSuffix
        ))
    }

    fun blinkOff() {
        legacyEffects.blinkOff()
    }

    fun blinkOffFadingOut() {
        legacyEffects.blinkOffFadingOut()
    }

    fun fadeOut(elementID: String): Promisoid<Unit> = fade(elementID, decreaseOpacity = true)
    fun fadeIn(elementID: String): Promisoid<Unit> = fade(elementID, decreaseOpacity = false)

    private fun fade(elementID: String, decreaseOpacity: Boolean): Promisoid<Unit> = async {
        val frames = 16
        check(frames % 2 == 0) {"frames should be even"}
        val initialOpacity = if (decreaseOpacity) 1.0 else 0.0
        val dopacity = 1.0 / frames * if (decreaseOpacity) -1 else 1

//        await(tillAnimationFrame())

        fun setOpacity(value: Double) {
            byid0(elementID)!!.style.opacity = value.toString()
        }

        var framesLeft = frames
        var opacity = initialOpacity
        var midpoint = opacity
        for (i in 1..framesLeft / 2) midpoint += dopacity
        check("$midpoint" == "0.5") {"fade midpoint"}

        var midpointReached = false
        while (framesLeft-- > 0) {
            await(tillAnimationFrame())

            opacity += dopacity
            // dlog("opacity = $opacity")

            setOpacity(opacity)

            if (opacity == midpoint) {
                await(TestGlobal.fadeHalfwayLock.sutPause())
//                if (isTest()) {
//                    TestGlobal.animationHalfwaySignal.resolve()
//                    await(TestGlobal.animationHalfwaySignalProcessedSignal.promise)
//                }
                midpointReached = true
            }
        }
        check(midpointReached) {"midpointReached"}
        await(TestGlobal.fadeDoneLock.sutPause())
    }
}

data class BlinkOpts(
    val fixed: Boolean = false,
    val dleft: Int = 0,
    val dtop: Int = 0,
    val dwidth: Int = 0,
    val widthCountMargin: Boolean = true,
    val heightCountMargin: Boolean = true,
    val widthCalcSuffix: String? = null
)

class EffectsInitializer : PassivableInitializer<Effects> {
    override fun initialize() = async {
        val api = Effects()
        val pane = old_panes.put(oldShitAsToReactElementable(api.legacyEffects.element))
        await(tillAnimationFrame())
        EffectsPassivable(pane, api)
    }
}

class EffectsPassivable(val pane: String, override val api: Effects) : Passivable<Effects> {
    override fun passivate() = async {
        old_panes.remove(pane)
        EffectsPassivated(api)
    }
}

class EffectsPassivated(val api: Effects) : Passivated<Effects> {
    override fun activate(): Promisoid<Passivable<Effects>> = async {
        val pane = old_panes.put(oldShitAsToReactElementable(api.legacyEffects.element))
        await(tillAnimationFrame())
        EffectsPassivable(pane, api)
    }
}

private fun makeLegacyEffects(): dynamic {
    return Shitus.statefulElement(ctor@{update ->
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
                    top -= js("$")(aps.global.document).scrollTop()
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
}



