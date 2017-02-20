package aps.back

import aps.*

@Servant class ServeMiranda : BitchyProcedure() {
    override fun serve() {
        fuckDangerously(FuckDangerouslyParams(
            bpc = bpc,
            makeRequest = {MirandaRequest()},
            runShit = fun(ctx, req: MirandaRequest): MirandaRequest.Response {
                val arg = req.payload.value as MirandaPayload

                exhaustive/when (arg) {
                    is MirandaTestImposeNextGeneratedUserToken -> {
                        dwarnStriking("miiiiiiiiimiiiiiimiiiii", arg.token)
                        dwarnStriking("vaaaaaa", arg.vagina.size)
                    }
                }
                return MirandaRequest.Response()
            }
        ))
    }
}


