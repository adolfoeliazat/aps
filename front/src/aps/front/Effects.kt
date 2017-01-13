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

    fun fadeOut(elementID: String): Promise<Unit> = fade(elementID, decreaseOpacity = true)
    fun fadeIn(elementID: String): Promise<Unit> = fade(elementID, decreaseOpacity = false)

    fun fade(elementID: String, decreaseOpacity: Boolean): Promise<Unit> = async {
        val frames = 16
        check(frames % 2 == 0) {"frames should be even"}
        val initialOpacity = if (decreaseOpacity) 1.0 else 0.0
        val dopacity = 1.0 / frames * if (decreaseOpacity) -1 else 1

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
                if (isTest()) {
                    TestGlobal.animationHalfwaySignal.resolve()
                    await(TestGlobal.animationHalfwaySignalProcessedSignal.promise)
                }
                midpointReached = true
            }
        }
        check(midpointReached) {"midpointReached"}
    }
}

fun TestScenarioBuilder.animatedActionSequence(
    buildAction: () -> Unit,
    assertionDescr: String,
    halfwayAssertionID: String,
    finalAssertionID: String,
    actionTimeout: Int = 5000
) {
    val o = this
    o.act {
        TestGlobal.animationHalfwaySignal = ResolvableShit()
        TestGlobal.animationHalfwaySignalProcessedSignal = ResolvableShit()
        TestGlobal.formActionCompleted = ResolvableShit()
    }

    buildAction()

    o.acta {TestGlobal.animationHalfwaySignal.promise.orTimeout(1000)}
    o.assertScreenHTML(assertionDescr + " (animation halfway)", halfwayAssertionID)
    o.act {TestGlobal.animationHalfwaySignalProcessedSignal.resolve()}

    o.acta {TestGlobal.formActionCompleted.promise.orTimeout(actionTimeout)}
    o.assertScreenHTML(assertionDescr, finalAssertionID)
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
        panes.remove("initEffects")
        panes.put("initEffects", oldShitAsToReactElementable(effects.element))
    })
}





