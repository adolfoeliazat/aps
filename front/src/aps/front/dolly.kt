package aps.front

import aps.*
import into.kommon.*

class DollyButton(
    val title: String,
    val level: Button.Level,
    val key: ButtonKey,
    val sendRequest: suspend () -> FormResponse2<*>,
    val onSuccess: suspend () -> Unit
)

data class DollyParams(
    val busy: Boolean = false,
    val styles: DollyStyles,
    val message: String,
    val buttons: List<DollyButton>
)

class Dolly(val p: DollyParams): ToReactElementable {
    val place = Placeholder(renderDolly(p))

    override fun toReactElement() = place.toReactElement()

    private fun renderDolly(p: DollyParams): ToReactElementable {
        return kdiv(className = if (p.busy) p.styles.containerBusy else p.styles.container){o->
            o- kdiv(className = p.styles.message){o->
                if (p.busy) {
                    o- renderTicker()
                } else {
                    o- p.message
                }
            }
            o- hor2{o->
                for (button in p.buttons) {
                    o- Button(
                        title = button.title, disabled = p.busy, level = button.level, className = p.styles.button, key = button.key,
                        onClicka = {
                            place.setContent(renderDolly(p.copy(busy = true)))
                            TestGlobal.shitHalfwayLock.resumeTestAndPauseSutFromSut()

                            val res = button.sendRequest()
                            exhaustive/when (res) {
                                is FormResponse2.Shitty -> {
                                    imf("Handle shitty response in lalala")
                                }
                                is FormResponse2.Hunky -> {
                                    button.onSuccess()
                                    TestGlobal.shitDoneLock.resumeTestFromSut()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}




