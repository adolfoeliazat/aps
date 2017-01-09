package aps.front

import aps.*
import into.kommon.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.window
import kotlin.properties.Delegates.notNull

enum class HandDirection {
    UP, DOWN, LEFT, RIGHT
}

data class HandOpts(
    val direction: HandDirection = HandDirection.UP,
    val ms: Int = 1500,
    val pauseDescr: String? = null
)

object TestUserActionAnimation {
    private val testOpts get() = TestGlobal.lastTestOpts

    fun hand(target: Control2, opts: HandOpts = HandOpts()): Promise<Unit> = hand(target.elementID, opts)

    @JsName("hand")
    fun hand(
        elementID: String,
        opts: HandOpts = HandOpts(),
        whileHandVisible: () -> Promise<Unit> = {async{}}
    ): Promise<Unit> = async {
        if (!testOpts.animateUserActions) return@async

        val mycss = when (opts.direction) {
            HandDirection.UP -> css.test.animateUserActions.handUp
            HandDirection.DOWN -> css.test.animateUserActions.handDown
            HandDirection.LEFT -> css.test.animateUserActions.handLeft
            HandDirection.RIGHT -> css.test.animateUserActions.handRight
        }
        val el = byid(elementID)

        var left by notNull<Double>()
        var top by notNull<Double>()
        var marginTop: String? = null
        val verticalGap = 5
        val horizontalGap = 5
        exhaustive/when (opts.direction) {
            HandDirection.UP -> {
                left = el.offset().left + el.outerWidth() / 2
                top = el.offset().top + el.outerHeight() + verticalGap
            }
            HandDirection.DOWN -> {
                left = el.offset().left + el.outerWidth() / 2
                top = el.offset().top - verticalGap
                marginTop = "-3rem"
            }
            HandDirection.LEFT -> {
                imf("hand left/top for LEFT")
            }
            HandDirection.RIGHT -> {
                left = el.offset().left
                top = el.offset().top
//                top = el.offset().top + el.outerHeight() / 2
            }
        }
        // clog("left = $left; top = $top; marginTop = $marginTop")

        val handPane = debugPanes.put(kdiv(className = mycss.pane, left = left, top = top, marginTop = marginTop){o->
            o- kdiv(className = mycss.fillContainer){o->
                o- kdiv(className = mycss.fillBigFinger)
                o- kdiv(className = mycss.fillFist)
                o- kdiv(className = mycss.fillPointingFinger)
                o- kdiv(className = mycss.fillWrist)
            }

            val icon = when (opts.direction) {
                HandDirection.UP -> fa.handOUp
                HandDirection.DOWN -> fa.handODown
                HandDirection.LEFT -> fa.handOLeft
                HandDirection.RIGHT -> fa.handORight
            }
            o- ki(className = mycss.handIcon + " " + icon.className)
        })

        if (testOpts.handPauses && opts.pauseDescr != null) {
            val pause = ResolvableShit<Unit>()
            val bannerPane = debugPanes.put(kdiv(className = css.test.popup.pause){o->
                o- opts.pauseDescr
                o- Button(icon = fa.play, style = Style(marginLeft = "0.5em")) {
                    pause.resolve()
                }
            })
            fun keyListener(e: Event) {
                e as KeyboardEvent
                if (e.key == "n") {
                    pause.resolve()
                }
            }
            window.addEventListener("keydown", ::keyListener)

            try {
                await(pause.promise)
            } finally {
                window.removeEventListener("keydown", ::keyListener)
                debugPanes.remove(bannerPane)
            }
        } else {
            await(delay(opts.ms * testOpts.slowdown))
        }

        await(tillEndOfTime())
        await(whileHandVisible())

        debugPanes.remove(handPane)
    }

    fun scroll(finalY: Int): Promise<Unit> = async {
        if (testOpts.animateUserActions) {
            var y = jqbody.scrollTop()
            var steps = 30 * testOpts.slowdown
            val dy = (finalY - y) / steps
            while (steps-- > 0) {
                y += dy
                await(tillAnimationFrame())
                jqbody.scrollTop(y)
            }
            jqbody.scrollTop(finalY)
        }
        else {
            jqbody.scrollTop(finalY)
        }
    }

}

