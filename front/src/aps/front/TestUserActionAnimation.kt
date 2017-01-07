package aps.front

import aps.*

object TestUserActionAnimation {
    private val opts get() = TestGlobal.lastTestOpts

    fun hand(target: Control2): Promise<Unit> = async {
        if (opts.animateUserActions) {
            val el = byid(target.elementID)

            val mycss = css.test.animateUserActions.hand
            val pane = debugPanes.put(kdiv(
                className = mycss.pane,
                left = el.offset().left + el.outerWidth() / 2,
                top = el.offset().top + el.outerHeight() + 5
            ){o->
                o - ki(className = mycss.handIcon + " " + fa.handOUp.className)
            })

            await(delay(1500 * opts.slowdown))

            debugPanes.remove(pane)
        }
    }

    fun scroll(finalY: Int): Promise<Unit> = async {
        if (opts.animateUserActions) {
            var y = jqbody.scrollTop()
            var steps = 30 * opts.slowdown
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
