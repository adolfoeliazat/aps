package aps.front

import aps.*
import into.kommon.*
import kotlin.properties.Delegates.notNull

enum class HandDirection {
    UP, DOWN, LEFT, RIGHT
}

class HandOpts(
    val direction: HandDirection = HandDirection.UP,
    val ms: Int = 1500
)

object TestUserActionAnimation {
    private val testOpts get() = TestGlobal.lastTestOpts

    fun hand(target: Control2, opts: HandOpts = HandOpts()): Promise<Unit> = hand(target.elementID, opts)

    @JsName("hand")
    fun hand(elementID: String, opts: HandOpts = HandOpts()): Promise<Unit> = async {
        if (testOpts.animateUserActions) {
            val mycss = css.test.animateUserActions.hand
            val el = byid(elementID)

            var left by notNull<Double>()
            var top by notNull<Double>()
            var marginTop: String? = null
            val verticalOffset = 5
            val horizontalOffset = 5
            exhaustive/when (opts.direction) {
                HandDirection.UP -> {
                    left = el.offset().left + el.outerWidth() / 2
                    top = el.offset().top + el.outerHeight() + verticalOffset
                }
                HandDirection.DOWN -> {
                    left = el.offset().left + el.outerWidth() / 2
                    top = el.offset().top - verticalOffset
                    marginTop = "-3rem"
                }
                HandDirection.LEFT -> {
                    imf("hand left/top for LEFT")
                }
                HandDirection.RIGHT -> {
                    imf("hand left/top for RIGHT")
                }
            }
            clog("left = $left; top = $top; marginTop = $marginTop")

            val pane = debugPanes.put(kdiv(className = mycss.pane, left = left, top = top, marginTop = marginTop){o->
                o- kdiv(className = mycss.fillBigFinger)
                o- kdiv(className = mycss.fillFist)
                o- kdiv(className = mycss.fillPointingFinger)
                o- kdiv(className = mycss.fillWrist)

                val icon = when (opts.direction) {
                    HandDirection.UP -> fa.handOUp
                    HandDirection.DOWN -> fa.handODown
                    HandDirection.LEFT -> fa.handOLeft
                    HandDirection.RIGHT -> fa.handORight
                }
                o- ki(className = mycss.handIcon + " " + icon.className)
            })

            await(delay(opts.ms * testOpts.slowdown))

            debugPanes.remove(pane)
        }
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

