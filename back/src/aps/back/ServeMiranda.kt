package aps.back

import aps.*

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

fun serveMirandaTestImposeNextGeneratedUserToken(p: MirandaTestImposeNextGeneratedUserToken): MirandaRequest.Response {
    TestServerFiddling.nextGeneratedUserToken.set(p.token)
    return MirandaRequest.Response()
}

