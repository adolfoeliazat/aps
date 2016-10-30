/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import into.kommon.*

var effects: dynamic = null

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

                blinker = Shitus.diva(json(
                    "className" to "progressTicker",
                    "style" to json(
                        "position" to if (fixed) "fixed" else "absolute",
                        "zIndex" to 10000,
                        "backgroundColor" to Color.BLUE_GRAY_600.toString(),
                        "left" to left,
                        "top" to top,
                        "width" to width,
                        "height" to height)))
                update()
            },

            "blinkOff" to {
                blinker = null
                update()
            }
        )

        return@ctor me
    })

    global.requestAnimationFrame({
        DebugPanes.remove("initEffects")
        DebugPanes.put("initEffects", oldShitAsReactElementable(effects.element))
    })
}





