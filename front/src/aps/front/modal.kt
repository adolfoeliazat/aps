package aps.front

import aps.*
import aps.front.*
import jquery.jq
import kotlin.js.json

private var modalShownResolvable = ResolvableShit<Unit>()
private var modalHiddenResolvable = ResolvableShit<Unit>()

fun openErrorModal(msg: String) {
    val container = byid0("modalContainer") ?: run {
        byid("root").append("<div id='modalContainer'></div>")
        byid0("modalContainer")!!
    }

    container.innerHTML = """
        <div class="modal fade" id="fuckingModal" tabindex="-1" role="dialog" aria-labelledby="fuckingModalLabel">
          <div class="modal-dialog" role="document">
            <div class="modal-content" style="border-left: 0.5em solid ${Color.RED_300};">
              <div class="modal-header" style="border-top-left-radius: 6px; border-top-right-radius: 6px;">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="fuckingModalLabel">${t("TOTE", "Облом")}</h4>
              </div>
              <div class="modal-body">
                $msg
              </div>
              <div class="modal-footer">
                <button id="fuckingModalOK" type="button" class="btn btn-default" data-dismiss="modal">${t("TOTE", "Ладно")}</button>
              </div>
            </div>
          </div>
        </div>
    """

    val jqModal = jq("#fuckingModal").asDynamic()
    jqModal.modal(json())
    jqModal.on("shown.bs.modal") {
        modalShownResolvable.resolve(Unit)
    }
    jqModal.on("hidden.bs.modal") {
        jqModal.data("bs.modal", null)
        container.innerHTML = ""
        modalHiddenResolvable.resolve(Unit)
        Unit
    }
}

fun modalConfirmDeletion(msg: String): Promisoid<Boolean> {
    val shit = ResolvableShit<Boolean>()
    var result = false

    val modalID = puid()
    val timesButtonID = puid()
    val pane = old_panes.put(kdiv(Attrs(className = "modal fade", id = modalID, tabIndex = -1)){o->
        o- kdiv(className = "modal-dialog"){o->
            o- kdiv(className = "modal-content", borderLeft = "0.5em solid ${Color.RED_300}"){o->
                o- kdiv(className = "modal-header", baseStyle = Style(borderTopLeftRadius = 6, borderTopRightRadius = 6)){o->
                    o- Button(id = timesButtonID, title = symbols.times, className = "close", dataDismiss = "modal")
                    o- h4(className = "modal-title"){o->
                        o- t("TOTE", "Внимание")
                    }
                }
                o- kdiv(className = "modal-body"){o->
                    o- msg
                }
                o- kdiv(className = "modal-footer"){o->
                    o- Button(key = "modal-yes", title = t("TOTE", "Мочи!"), level = Button.Level.DANGER) {
                        result = true
                        byid(timesButtonID).click()
                    }
                    o- Button(key = "modal-no", title = t("TOTE", "Я очкую"), dataDismiss = "modal") {
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

fun modalConfirmAndPerformDeletion(msg: String, req: DeleteRequest): Promisoid<Boolean> {
    val shit = ResolvableShit<Boolean>()
    var result = false

    val modalID = puid()
    val timesButtonID = puid()
    val pane = old_panes.put(kdiv(Attrs(className = "modal fade", id = modalID, tabIndex = -1)){o->
        o- kdiv(className = "modal-dialog"){o->
            o- kdiv(className = "modal-content", borderLeft = "0.5em solid ${Color.RED_300}"){o->
                val errorPlace = Placeholder()
                o- kdiv(className = "modal-header", baseStyle = Style(borderTopLeftRadius = 6, borderTopRightRadius = 6)){o->
                    o- Button(id = timesButtonID, title = symbols.times, className = "close", dataDismiss = "modal")
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
                    o- Button(key = "modal-yes", title = t("TOTE", "Мочи!"), level = Button.Level.DANGER) {
                        tickerPlace.setContent(renderTicker("left"))
                        async {
                            val res = await(send(req))
                            exhaustive/when (res) {
                                is ZimbabweResponse.Shitty -> {
                                    errorPlace.setContent(renderErrorBanner(res.error))
                                    tickerPlace.setContent(NOTRE)
                                }
                                is ZimbabweResponse.Hunky -> {
                                    result = true
                                    byid(timesButtonID).click()
                                }
                            }
                            responseProcessed()
                        }
                    }
                    o- Button(key = "modal-no", title = t("TOTE", "Я очкую"), dataDismiss = "modal") {
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



