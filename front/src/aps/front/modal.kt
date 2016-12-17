package aps.front

import aps.*
import aps.front.*
import jquery.jq

private var modalShit = ResolvableShit<Unit>()

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

    jq("#fuckingModal").asDynamic().modal(json())
    modalShit.resolve(Unit)
}

fun TestScenarioBuilder.willWaitForModal() {
    modalShit = ResolvableShit<Unit>()
}

fun TestScenarioBuilder.waitForModal() {
    val o = this
    o.acta("Waiting for modal") {async{
        val shit = ResolvableShit<Unit>()
        timeoutSet(1500) {shit.reject(Exception("Sick of waiting for modal"))}
        modalShit.promise.finally {shit.resolve(Unit)}
        await(shit.promise)
    }}
}




