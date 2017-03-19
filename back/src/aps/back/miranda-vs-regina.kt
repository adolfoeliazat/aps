package aps.back

import aps.*
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

@Servant class ServeMiranda : BitchyProcedure() {
    override fun serve() {
        fuckDangerously(FuckDangerouslyParams(
            bpc = bpc,
            makeRequest = {ObjectRequest()},
            runShit = fun(ctx, req: ObjectRequest): CommonResponseFields {
                return serveObjectRequest(req)
            }
        ))
    }
}

@Servant class ServeRegina : BitchyProcedure() {
    override fun serve() {
        fuckAnyUser(FuckAnyUserParams(
            bpc = bpc,
            makeRequest = {ObjectRequest()},
            runShit = fun(ctx, req: ObjectRequest): CommonResponseFields {
                return serveObjectRequest(req)
            }
        ))
    }
}

private fun serveObjectRequest(req: ObjectRequest): CommonResponseFields {
    val p = req.params.value
    val serveFunction = backPlatform.getServeObjectRequestFunction(p)
    val res = serveFunction(p)
    return when (res) {
        null /*Unit*/ -> GenericResponse()
        is CommonResponseFields -> res
        else -> wtf("d07d35f4-c52b-46f4-92d1-f5b25f76bac1")
    }
}














