package aps.front

import aps.*
import aps.const.text.symbols.times
import aps.front.fconst.test.testOffClassSuffix
import into.kommon.*
import kotlin.js.json

private var currentModalOperations: ModalOperations? = null

class OpenModalParamsButton(
    val title: String,
    val level: Button.Level,
    val onClicka: (suspend () -> Unit)? = null
)

class OpenModalParams(
    val leftMarginColor: Color? = null,
    val title: String,
    val body: ToReactElementable,
    val width: String? = null,
    val footer: ToReactElementable? = null
)

interface ModalOperations {
    suspend fun close()
}

fun openModal(p: OpenModalParams): ModalOperations {
    // TODO:vgrechka Escape key

    val shit = ResolvableShit<Boolean>()
    var result = false

    val modalID = puid()
    val timesButtonID = puid()
    val pane = old_panes.put(kdiv(className = css.shebang.modalPane){o->
        o- kdiv(className = "modal", display = "block") // XXX To display right scrollbar that doesn't fade in/out
        var fadeClass = "fade"
        if (isTest() && testOpts().addTestOffClassSuffixes)
            fadeClass += testOffClassSuffix
        o- kdiv(Attrs(className = "modal $fadeClass", id = modalID, tabIndex = -1)){o->
            o- kdiv(className = "modal-dialog", width = p.width){o->
                val borderLeft = p.leftMarginColor?.let {"0.5em solid $it"}
                o- kdiv(className = "modal-content", borderLeft = borderLeft){o->
                    o- kdiv(className = "modal-header", baseStyle = Style(borderTopLeftRadius = 6, borderTopRightRadius = 6)){o->
                        o- Button(id = timesButtonID, title = times, className = "close", dataDismiss = "modal")
                        o- h4(className = "modal-title"){o->
                            o- p.title
                        }
                    }
                    o- kdiv(className = "modal-body"){o->
                        o- p.body
                        val debug_makeItTall = false
                        if (debug_makeItTall) {
                            for (i in 1..50) {
                                o- div("pizda $i")
                            }
                        }
                    }
                    if (p.footer != null) {
                        o- kdiv(className = "modal-footer"){o->
                            o- p.footer
                        }
                    }
                }
            }
        }
    })

    val jqModal = byid(modalID).asDynamic()
    jqModal.on("show.bs.modal") {
        jqbody.css("overflow-y", "hidden")
        jq(".navbar-fixed-top").css("padding-right", "${fconst.scrollbarWidth}px")
        jqbody.addClass(css.shebang.paddingRightScrollbarWidthImportant)
        jq(".${css.test.crossWorld.locationPane}").addClass(css.shebang.paddingRightScrollbarWidthImportant)
    }
    jqModal.on("shown.bs.modal") {
        TestGlobal.modalShownLock.resumeTestFromSut()
    }
    jqModal.on("hide.bs.modal") {
    }
    jqModal.on("hidden.bs.modal") {
        currentModalOperations = null

        jqbody.css("overflow-y", "scroll")
        jq(".navbar-fixed-top").css("padding-right", "")
        jqbody.removeClass(css.shebang.paddingRightScrollbarWidthImportant)
        jq(".${css.test.crossWorld.locationPane}").removeClass(css.shebang.paddingRightScrollbarWidthImportant)
        jqModal.data("bs.modal", null)
        old_panes.remove(pane)
        TestGlobal.modalHiddenLock.resumeTestFromSut()
        shit.resolve(result)
        Unit
    }
    jqModal.modal(json())

    val operations = object:ModalOperations {
        override suspend fun close() {
            byid(timesButtonID).click()
            await(shit.promise)
        }
    }
    currentModalOperations = operations
    return operations
}

fun <Req : RequestMatumba, Res : CommonResponseFields>
    openEditModal(title: String,
                  formSpec: FormSpec<Req, Res>,
                  onSuccessBeforeClosingModal: suspend (Res) -> Unit = {},
                  onSuccessAfterClosingModal: suspend (Res) -> Unit = {}
)
    : ModalOperations
{
    var modal by notNullOnce<ModalOperations>()

    val form = FormMatumba(formSpec.copy(
        buttonLocation = FormSpec.ButtonLocation.RIGHT,
        primaryButtonTitle = t("TOTE", "Сохранить"),
        cancelButtonTitle = t("TOTE", "Не стоит"),
        onSuccessa = {res->
            onSuccessBeforeClosingModal(res)
            modal.close()
            onSuccessAfterClosingModal(res)
        },
        onCancela = {
            modal.close()
        }
    ))

    modal = openModal(OpenModalParams(
        width = "80rem",
        leftMarginColor = Color.BLUE_GRAY_300,
        title = title,
        body = form.fieldsAndBanner,
        footer = form.buttonsAndTicker
    ))
    return modal
}

suspend fun modalConfirmAndDelete(msg: String, req: DeleteRequest): Boolean {
    if (TestGlobal.deleteWithoutConfirmation) {
        val form = FormMatumba(FormSpec<DeleteRequest, Any?>(
            req = req, ui = Globus.world))
        val res = form.submit()
        if (res is FormResponse.Shitty)
            die("Tried to delete shit without confirmation, got shitty response: ${res.error}")
        return true
    }

    return openDangerFormModalAndWaitExecution(
        title = t("No kidding?", "Серьезно?"),
        primaryButtonTitle = t("TOTE", "Мочи!"),
        cancelButtonTitle = t("TOTE", "Я очкую"),
        renderBelowFieldsAndBanner = {o->
            o- msg
        },
        width = "60rem",
        req = req)
}

suspend fun <Request : RequestMatumba> openDangerFormModalAndWaitExecution(
    title: String,
    primaryButtonTitle: String,
    cancelButtonTitle: String,
    renderBelowFieldsAndBanner: (ElementBuilder) -> Unit = {},
    width: String = "80rem",
    req: Request
)
    : Boolean
{
    var modal by notNullOnce<ModalOperations>()
    val ret = ResolvableShit<Boolean>()

    val form = FormMatumba(FormSpec<Request, Any?>(
        req = req, ui = Globus.world,
        buttonLocation = FormSpec.ButtonLocation.RIGHT,
        primaryButtonTitle = primaryButtonTitle,
        primaryButtonLevel = Button.Level.DANGER,
        cancelButtonTitle = cancelButtonTitle,
        onSuccessa = {
            modal.close()
            ret.resolve(true)
        },
        onCancela = {
            modal.close()
            ret.resolve(false)
        }
    ))

    modal = openModal(OpenModalParams(
        width = width,
        leftMarginColor = Color.RED_300,
        title = title,
        body = kdiv{o->
            o- form.fieldsAndBanner
            renderBelowFieldsAndBanner(o)
        },
        footer = form.buttonsAndTicker
    ))

    return await(ret.promise)
}

fun openErrorModal(msg: String, buttonTitle: String? = null): ModalOperations {
    var modal by notNullOnce<ModalOperations>()

    modal = openModal(OpenModalParams(
        width = "60rem",
        leftMarginColor = Color.RED_300,
        title = t("TOTE", "Облом"),
        body = kdiv{o->
            o- msg
        },
        footer = kdiv{o->
            o- Button(title = buttonTitle ?: t("Cool", "Круто"), level = Button.Level.DEFAULT, key = buttons.primary, onClicka = {
                modal.close()
            })
        }
    ))

    return modal
}

fun openInfoModal(msg: String, title: String? = null): ModalOperations {
    var modal by notNullOnce<ModalOperations>()

    modal = openModal(OpenModalParams(
        width = "60rem",
        leftMarginColor = Color.BLUE_GRAY_300,
        title = title ?: t("TOTE", "Привет :)"),
        body = kdiv{o->
            o- msg
        },
        footer = kdiv{o->
            o- Button(title = t("Cool", "Круто"), level = Button.Level.DEFAULT, key = buttons.primary, onClicka = {
                modal.close()
            })
        }
    ))

    return modal
}

object tmodal {
    fun close() {
        async {bang(currentModalOperations).close()}
    }

    suspend fun closeWaiting() {
        bang(currentModalOperations).close()
    }
}



//=============================== Kill me, please ================================


private var modalShownResolvable = ResolvableShit<Unit>()
private var modalHiddenResolvable = ResolvableShit<Unit>()

//fun openErrorModal(msg: String) {
//    val container = byid0("modalContainer") ?: run {
//        byid("root").append("<div id='modalContainer'></div>")
//        byid0("modalContainer")!!
//    }
//
//    container.innerHTML = """
//        <div class="modal fade" id="fuckingModal" tabindex="-1" role="dialog" aria-labelledby="fuckingModalLabel">
//          <div class="modal-dialog" role="document">
//            <div class="modal-content" style="border-left: 0.5em solid ${Color.RED_300};">
//              <div class="modal-header" style="border-top-left-radius: 6px; border-top-right-radius: 6px;">
//                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
//                <h4 class="modal-title" id="fuckingModalLabel">${t("TOTE", "Облом")}</h4>
//              </div>
//              <div class="modal-body">
//                $msg
//              </div>
//              <div class="modal-footer">
//                <button id="fuckingModalOK" type="button" class="btn btn-default" data-dismiss="modal">${t("TOTE", "Ладно")}</button>
//              </div>
//            </div>
//          </div>
//        </div>
//    """
//
//    val jqModal = jq("#fuckingModal").asDynamic()
//    jqModal.modal(json())
//    jqModal.on("shown.bs.modal") {
//        modalShownResolvable.resolve(Unit)
//    }
//    jqModal.on("hidden.bs.modal") {
//        jqModal.data("bs.modal", null)
//        container.innerHTML = ""
//        modalHiddenResolvable.resolve(Unit)
//        Unit
//    }
//}

fun modalConfirmDeletion(msg: String): Promisoid<Boolean> {
    val shit = ResolvableShit<Boolean>()
    var result = false

    val modalID = puid()
    val timesButtonID = puid()
    val pane = old_panes.put(kdiv(Attrs(className = "modal fade", id = modalID, tabIndex = -1)){o->
        o- kdiv(className = "modal-dialog"){o->
            o- kdiv(className = "modal-content", borderLeft = "0.5em solid ${Color.RED_300}"){o->
                o- kdiv(className = "modal-header", baseStyle = Style(borderTopLeftRadius = 6, borderTopRightRadius = 6)){o->
                    o- Button(id = timesButtonID, title = times, className = "close", dataDismiss = "modal")
                    o- h4(className = "modal-title"){o->
                        o- t("TOTE", "Внимание")
                    }
                }
                o- kdiv(className = "modal-body"){o->
                    o- msg
                }
                o- kdiv(className = "modal-footer"){o->
                    o- Button(title = t("TOTE", "Мочи!"), level = Button.Level.DANGER, key = buttons.modal.ok) {
                        result = true
                        byid(timesButtonID).click()
                    }
                    o- Button(title = t("TOTE", "Я очкую"), dataDismiss = "modal", key = buttons.modal.cancel) {
                        result = false
                        byid(timesButtonID).click()
                    }
                }
            }
        }
    })

    val jqModal = byid(modalID).asDynamic()
    jqModal.modal(json())
    jqModal.on("shown.bs.modal") {
        modalShownResolvable.resolve(Unit)
    }
    jqModal.on("hidden.bs.modal") {
        jqModal.data("bs.modal", null)
        old_panes.remove(pane)
        modalHiddenResolvable.resolve(Unit)
        shit.resolve(result)
        Unit
    }

    return shit.promise
}

fun modalConfirmAndDelete_killme(msg: String, req: DeleteRequest): Promisoid<Boolean> {
    val shit = ResolvableShit<Boolean>()
    var result = false

    val modalID = puid()
    val timesButtonID = puid()
    val pane = old_panes.put(kdiv(Attrs(className = "modal fade", id = modalID, tabIndex = -1)){o->
        o- kdiv(className = "modal-dialog"){o->
            o- kdiv(className = "modal-content", borderLeft = "0.5em solid ${Color.RED_300}"){o->
                val errorPlace = Placeholder()
                o- kdiv(className = "modal-header", baseStyle = Style(borderTopLeftRadius = 6, borderTopRightRadius = 6)){o->
                    o- Button(id = timesButtonID, title = times, className = "close", dataDismiss = "modal")
                    o- h4(className = "modal-title"){o->
                        o- t("TOTE", "Внимание")
                    }
                }
                o- kdiv(className = "modal-body"){o->
                    o- errorPlace
                    o- msg
                }
                o- kdiv(className = "modal-footer"){o->
                    val tickerPlace = Placeholder()
                    o- tickerPlace
                    o- Button(title = t("TOTE", "Мочи!"), level = Button.Level.DANGER, key = buttons.modal.ok) {
                        tickerPlace.setContent(renderTicker("left"))
                        async {
                            val res = await(send(req))
                            exhaustive=when (res) {
                                is ZimbabweResponse.Shitty -> {
                                    errorPlace.setContent(renderErrorBanner(res.error))
                                    tickerPlace.setContent(NOTRE)
                                }
                                is ZimbabweResponse.Hunky -> {
                                    result = true
                                    byid(timesButtonID).click()
                                }
                            }
                            // TODO Resume test here
                        }
                    }
                    o- Button(title = t("TOTE", "Я очкую"), dataDismiss = "modal", key = buttons.modal.cancel) {
                        result = false
                        byid(timesButtonID).click()
                    }
                }
            }
        }
    })

    val jqModal = byid(modalID).asDynamic()
    jqModal.modal(json())
    jqModal.on("shown.bs.modal") {
        modalShownResolvable.resolve(Unit)
    }
    jqModal.on("hidden.bs.modal") {
        jqModal.data("bs.modal", null)
        old_panes.remove(pane)
        modalHiddenResolvable.resolve(Unit)
        shit.resolve(result)
        Unit
    }

    return shit.promise
}


fun TestScenarioBuilder.willWaitForModalShown() {
    act {
        modalShownResolvable = ResolvableShit<Unit>()
    }
}

fun TestScenarioBuilder.willWaitForModalHidden() {
    act {
        modalHiddenResolvable = ResolvableShit<Unit>()
    }
}

fun TestScenarioBuilder.waitForModalShown() {
    acta("Waiting for modal shown") {async{
        val shit = ResolvableShit<Unit>()
        timeoutSet(1500) {shit.reject(Exception("Sick of waiting for modal"))}
        modalShownResolvable.promise.then {shit.resolve(Unit)}
        await(shit.promise)
    }}
}

fun TestScenarioBuilder.waitForModalHidden() {
    acta("Waiting till modal is hidden") {async{
        val shit = ResolvableShit<Unit>()
        timeoutSet(1500) {shit.reject(Exception("Sick of waiting till modal is hidden"))}
        modalHiddenResolvable.promise.then {shit.resolve(Unit)}
        await(shit.promise)
    }}
}

fun TestScenarioBuilder.clickModalOKAndWaitTillHidden() {
    willWaitForModalHidden()
    act("Clicking modal's OK") {
        byid("fuckingModalOK").click()
    }
    waitForModalHidden()
}

fun TestScenarioBuilder.modalSequence(action: () -> Unit,
                                      assertModal: () -> Unit,
                                      modalAction: () -> Unit,
                                      assertAfterModal: () -> Unit) {
    willWaitForModalShown()
    action()
    waitForModalShown()
    assertModal()

    willWaitForModalHidden()
    modalAction()
    waitForModalHidden()
    assertAfterModal()
}



