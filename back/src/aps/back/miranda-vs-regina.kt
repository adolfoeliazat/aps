package aps.back

import aps.*
import org.springframework.data.repository.findOrDie

@Servant class ServeMiranda : BitchyProcedure() {
    override fun serve() {
        fuckDangerously(FuckDangerouslyParams(
            bpc = bpc,
            makeRequest = {MirandaRequest()},
            runShit = fun(ctx, req: MirandaRequest): MirandaRequest.Response {
                val params = req.params.value
                return when (params) {
                    is MirandaTestImposeNextGeneratedUserToken -> serveMirandaTestImposeNextGeneratedUserToken(params)
                }
            }
        ))
    }
}

@Servant class ServeRegina : BitchyProcedure() {
    override fun serve() {
        fuckAnyUser(FuckAnyUserParams(
            bpc = bpc,
            makeRequest = {ReginaRequest()},
            runShit = fun(ctx, req: ReginaRequest): ReginaRequest.Response {
                val params = req.params.value
                return when (params) {
                    is ReginaCustomerSendOrderForApprovalAfterFixing -> serveReginaCustomerSendOrderForApprovalAfterFixing(params)
                }
            }
        ))
    }
}
















