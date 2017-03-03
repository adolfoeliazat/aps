package aps.back

import aps.*
import into.kommon.*
import org.springframework.data.repository.findOrDie
import kotlin.reflect.full.cast
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.safeCast

@Servant class ServeMiranda : BitchyProcedure() {
    override fun serve() {
        fuckDangerously(FuckDangerouslyParams(
            bpc = bpc,
            makeRequest = {MirandaRequest()},
            runShit = fun(ctx, req: MirandaRequest): CommonResponseFields {
                fun <Params : MirandaParams<Res>, Res> tie(p: Params, f: (Params) -> Res): Res = f(p)

                val p = req.params.value
                return when (p) {
                    is MirandaTestImposeNextGeneratedUserToken -> tie(p, ::serveMirandaTestImposeNextGeneratedUserToken)
                    is MirandaTestImposeNextGeneratedPassword -> tie(p, ::serveMirandaTestImposeNextGeneratedPassword)
                    is MirandaGetGeneratedTestTimestamps -> tie(p, ::serveMirandaGetGeneratedTestTimestamps)
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
            runShit = fun(ctx, req: ReginaRequest): CommonResponseFields {
                fun <Params : ReginaParams<Res>, Res> tie(p: Params, f: (Params) -> Res): Res = f(p)

                val p = req.params.value
                return when (p) {
                    is ReginaCustomerSendOrderForApprovalAfterFixing -> tie(p, ::serveReginaCustomerSendOrderForApprovalAfterFixing)
                    is ReginaAdminSendOrderToStore -> tie(p, ::serveReginaAdminSendOrderToStore)
                    is ReginaLoadUser -> tie(p, ::serveReginaLoadUser)
                    is ReginaAcceptProfile -> tie(p, ::serveReginaAcceptProfile)
                    is ReginaGetPairOfLastHistoryItems<*> -> {
                        val res = serveReginaGetPairOfLastHistoryItems(p)
//                        check(res.type.isSubclassOf(p.type)){"ecc76402-0199-4115-be07-82694c6fe02d"}
                        res
                    }
                    is ReginaGetDocumentCategories -> tie(p, ::serveReginaGetDocumentCategories)
                }
            }
        ))
    }
}















