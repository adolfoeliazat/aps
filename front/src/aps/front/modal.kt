package aps.front

import aps.*
import aps.front.*
import jquery.jq

private var modalShownResolvable = ResolvableShit<Unit>()
private var modalHiddenResolvable = ResolvableShit<Unit>()

fun fixModalJumping() {

}

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

fun TestScenarioBuilder.willWaitForModal() {
    modalShownResolvable = ResolvableShit<Unit>()
}

fun TestScenarioBuilder.waitForModalShown() {
    val o = this
    o.acta("Waiting for modal shown") {async{
        val shit = ResolvableShit<Unit>()
        timeoutSet(1500) {shit.reject(Exception("Sick of waiting for modal"))}
        modalShownResolvable.promise.finally {shit.resolve(Unit)}
        await(shit.promise)
    }}
}

fun TestScenarioBuilder.clickModalOKAndWaitTillHidden() {
    val o = this
    modalHiddenResolvable = ResolvableShit<Unit>()

    o.act("Clicking modal's OK") {
        byid("fuckingModalOK").click()
    }

    o.acta("Waiting till modal is hidden") {async{
        val shit = ResolvableShit<Unit>()
        timeoutSet(1500) {shit.reject(Exception("Sick of waiting till modal is hidden"))}
        modalHiddenResolvable.promise.finally {shit.resolve(Unit)}
        await(shit.promise)
    }}
}



