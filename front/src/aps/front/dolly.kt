package aps.front

import aps.*
import into.kommon.*
import kotlin.reflect.KFunction1

interface DollyStyles {
    val container: String
    val containerBusy: String
    val message: String
    val button: String
}

class DollyButton(
    val title: String,
    val level: Button.Level,
    val key: ButtonKey,
    val onClick: suspend (DollyInterface) -> Unit
)

interface DollyInterface {
    fun setBusy()
}

fun sendingDollyButtonHandler(
    sendRequest: suspend () -> FormResponse2<*>,
    onSuccess: suspend () -> Unit
)
    : suspend (DollyInterface) -> Unit =
{di->
    di.setBusy()
    TestGlobal.shitHalfwayLock.resumeTestAndPauseSutFromSut()

    val res = sendRequest()
    exhaustive / when (res) {
        is FormResponse2.Shitty -> {
            console.error("SHITTY RESPONSE", res.error)
            imf("Handle shitty response in lalala")
        }
        is FormResponse2.Hunky -> {
            onSuccess()
            TestGlobal.shitDoneLock.resumeTestFromSut()
        }
    }
}

data class DollyParams(
    val busy: Boolean = false,
    val styles: DollyStyles,
    val message: String,
    val buttons: List<DollyButton>,
    val bottomGap: Boolean = false
)

class Dolly(val p: DollyParams): ToReactElementable {
    val place = Placeholder(renderDolly(p))

    override fun toReactElement() = place.toReactElement()

    private fun renderDolly(p: DollyParams): ToReactElementable {
        return kdiv(className = if (p.busy) p.styles.containerBusy else p.styles.container,
                    marginBottom = if (p.bottomGap) "1rem" else null){o->
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
                            button.onClick(object:DollyInterface {
                                override fun setBusy() {
                                    place.setContent(renderDolly(p.copy(busy = true)))
                                }
                            })
                        }
                    )
                }
            }
        }
    }

}

fun <EntityRTO : TabithaEntityRTO> acceptOrRejectDolly(
    message: String,
    blankRejectionRequest: RejectionRequest,
    entityID: Long,
    tabitha: Tabitha<EntityRTO>,
    acceptButtonTitle: String,
    makeAcceptanceRequestParams: KFunction1<Long, ReginaParams<*>>,
    bottomGap: Boolean = false
): Dolly {
    return Dolly(DollyParams(
        styles = css.dolly.normal,
        message = message,
        bottomGap = bottomGap,
        buttons = listOf(
            DollyButton(
                title = t("TOTE", "Завернуть"), level = Button.Level.DANGER, key = buttons.reject,
                onClick = {
                    val executed = openDangerFormModalAndWaitExecution(
                        title = t("TOTE", "Возвращаем на доработку"),
                        primaryButtonTitle = t("TOTE", "Завернуть"),
                        cancelButtonTitle = t("TOTE", "Не надо"),
                        req = blankRejectionRequest-{o->
                            o.entityID.value = entityID
                            o.rejectionReason.value = t("TOTE", "Что нужно исправить?")
                        }
                    )
                    if (executed)
                        tabitha.reloadPage()
                }),
            DollyButton(
                title = acceptButtonTitle, level = Button.Level.PRIMARY, key = buttons.accept,
                onClick = sendingDollyButtonHandler(
                    sendRequest = {
                        askRegina(makeAcceptanceRequestParams(entityID))
                    },
                    onSuccess = {
                        tabitha.reloadPage()
                    })))))
}


