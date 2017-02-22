package aps.back

import aps.*
import org.springframework.data.repository.findOrDie

@Servant class ServeMiranda : BitchyProcedure() {
    override fun serve() {
        fuckDangerously(FuckDangerouslyParams(
            bpc = bpc,
            makeRequest = {MirandaRequest()},
            runShit = fun(ctx, req: MirandaRequest): MirandaRequest.Response {
                val p = req.params.value
                return when (p) {
                    is MirandaTestImposeNextGeneratedUserToken -> serveMirandaTestImposeNextGeneratedUserToken(p)
                    is MirandaTestImposeNextGeneratedPassword -> serveMirandaTestImposeNextGeneratedPassword(p)
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
                val p = req.params.value
                return when (p) {
                    is ReginaCustomerSendOrderForApprovalAfterFixing -> serveReginaCustomerSendOrderForApprovalAfterFixing(p)
                    is ReginaAdminSendOrderToStore -> serveReginaAdminSendOrderToStore(p)
                }
            }
        ))
    }
}
















