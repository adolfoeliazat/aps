/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import jquery.JQuery
import kotlin.js.json
import kotlin.properties.Delegates.notNull

// TODO:vgrechka Simplify this shit

private var initialized = false
private var _effects by notNull<EffectsAPI>()
private var pane by notNull<String>()

val effects: Promisoid<EffectsAPI> get() = async {
    if (!initialized) {
        val api = EffectsAPI()
        pane = old_panes.put(oldShitAsToReactElementable(api.legacyEffects.element))
        await(tillAnimationFrame())
        _effects = api
        initialized = true
    }

    _effects
}

fun disposeEffects() {
    if (initialized) {
        old_panes.remove(pane)
        initialized = false
    }
}





val effects_killme by PassivableHolder(EffectsInitializer())

interface BlinkerOperations {
    fun unblink()
//    fun unblinkGradually()
}

class EffectsAPI {
    val legacyEffects: dynamic = makeLegacyEffects()

    fun blinkOn(target: JQuery, opts: BlinkOpts = BlinkOpts()): BlinkerOperations {
        val legacyBlinker = legacyEffects.addBlinker(json(
            "target" to target,
            "fixed" to opts.fixed,
            "dleft" to opts.dleft,
            "dtop" to opts.dtop,
            "dwidth" to opts.dwidth,
            "widthCountMargin" to opts.widthCountMargin,
            "heightCountMargin" to opts.heightCountMargin,
            "widthCalcSuffix" to opts.widthCalcSuffix,
            "overHeader" to opts.overHeader
        ))

        return object:BlinkerOperations {
            override fun unblink() {
                legacyEffects.removeBlinker(legacyBlinker)
            }
//            override fun unblinkGradually() {
//                legacyEffects.removeBlinkerFadingOut(legacyBlinker)
//            }
        }
    }

    fun unblinkAll() {
        legacyEffects.removeAllBlinkers()
    }

    suspend fun fadeOut(elementID: String) = fade(elementID, decreaseOpacity = true)
    suspend fun fadeIn(elementID: String) = fade(elementID, decreaseOpacity = false)

    private suspend fun fade(elementID: String, decreaseOpacity: Boolean) {
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
                TestGlobal.fadeHalfwayLock.resumeTestAndPauseSutFromSut()
//                if (isTest()) {
//                    TestGlobal.animationHalfwaySignal.resolve()
//                    await(TestGlobal.animationHalfwaySignalProcessedSignal.promise)
//                }
                midpointReached = true
            }
        }
        check(midpointReached) {"midpointReached"}
        TestGlobal.fadeDoneLock.resumeTestAndPauseSutFromSut()
    }
}

data class BlinkOpts(
    val fixed: Boolean = false,
    val dleft: Int = 0,
    val dtop: Int = 0,
    val dwidth: Int = 0,
    val widthCountMargin: Boolean = true,
    val heightCountMargin: Boolean = true,
    val widthCalcSuffix: String? = null,
    val overHeader: Boolean = false
)

class EffectsInitializer : PassivableInitializer<EffectsAPI> {
    override fun initialize() = async {
        val api = EffectsAPI()
        val pane = old_panes.put(oldShitAsToReactElementable(api.legacyEffects.element))
        await(tillAnimationFrame())
        EffectsPassivable(pane, api)
    }
}

class EffectsPassivable(val pane: String, override val api: EffectsAPI) : Passivable<EffectsAPI> {
    override fun passivate() = async {
        old_panes.remove(pane)
        EffectsPassivated(api)
    }
}

class EffectsPassivated(val api: EffectsAPI) : Passivated<EffectsAPI> {
    override fun activate(): Promisoid<Passivable<EffectsAPI>> = async {
        val pane = old_panes.put(oldShitAsToReactElementable(api.legacyEffects.element))
        await(tillAnimationFrame())
        EffectsPassivable(pane, api)
    }
}

private fun makeLegacyEffects(): dynamic {
    return Shitus.statefulElement(ctor@{update ->
        var me: dynamic = null
        var blinkers = mutableListOf<ReactElement>()
        var blinkerInterval: dynamic = null
        val blinkerToFuckingID = WeakMap<ReactElement, String>()

        me = json(
            "render" to {
                kdiv{o->
                    for (blinker in blinkers) {
                        o- blinker
                    }
                }.toReactElement()
            },

            "addBlinker" to {arg: dynamic ->
                val target: dynamic = arg.target
                val fixed: dynamic = arg.fixed
                val dleft: dynamic = arg.dleft ?: 0
                val dtop: dynamic = arg.dtop ?: 0
                val dwidth: dynamic = arg.dwidth ?: 0
                val overHeader: Boolean = arg.overHeader
                val widthCountMargin: dynamic = arg.widthCountMargin ?: true
                val heightCountMargin: dynamic = arg.heightCountMargin ?: true
                val widthCalcSuffix: String? = arg.widthCalcSuffix

                val targetOffset = target.offset()
                val targetWidth = target.outerWidth(widthCountMargin)
                val targetHeight = target.outerHeight(heightCountMargin)
                val width = targetWidth + dwidth
                val height = "0.3rem"
                val left = targetOffset.left + dleft
                var top = "calc(${targetOffset.top}px + ${targetHeight}px - $height + ${dtop}px"
                if (fixed) {
                    top += " - " + js("$")(aps.global.document).scrollTop() + "px"
                }
                top += ")"
                // dlog({left, top, width, height})

                val blinkerStyle = json(
                    "position" to if (fixed) "fixed" else "absolute",
                    "zIndex" to if (overHeader) 10000 else 1000,
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

                val id = puid()
                val blinker = Shitus.diva(json(
                    "id" to id,
                    "className" to "progressTicker",
                    "style" to blinkerStyle))
                blinkerToFuckingID[blinker] = id
                blinkers.add(blinker)
                update()
                blinker
            },

            "removeBlinker" to {blinker: ReactElement ->
                blinkers.remove(blinker)
                update()
            },

//            "removeBlinkerFadingOut" to {blinker: ReactElement ->
//                async {
//                    val el = byid0(bang(blinkerToFuckingID[blinker]))
//                    if (el != null) {
//                        el.className = ""
//                        el.style.transition = "opacity 5s"
//                        el.style.opacity = "0"
////                        el.style.opacity = "0.5"
//                        await(delay(5000)) // XXX requestAnimationFrame doesn't help
//                    }
//                    me.removeBlinker(blinker)
//                }
//            },

            "removeAllBlinkers" to {
                blinkers.clear()
                update()
            }
        )

        return@ctor me
    })
}



