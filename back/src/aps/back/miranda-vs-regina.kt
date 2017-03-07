package aps.back

import aps.*
import into.kommon.*
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

private val paramClassToServeMethod = ConcurrentHashMap<Class<*>, Method>()

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
    val method = paramClassToServeMethod.computeIfAbsent(p::class.java) {
        for (cname in listOf("Rp_orderKt", "Rp_userKt", "Rp_historyKt", "Rp_testKt", "Rp_test_2Kt")) {
            val clazz = Class.forName("aps.back.$cname")
            try {
                return@computeIfAbsent clazz.getDeclaredMethod("serve", p::class.java)
            } catch (e: Throwable) {
            }
        }
        wtf("p::class = ${p::class}    a322c2b4-25af-45e1-a7ae-a5484a941ec3")
    }

    val res = method.invoke(null, p)
    return when (res) {
        null /*Unit*/ -> GenericResponse()
        is CommonResponseFields -> res
        else -> wtf("d07d35f4-c52b-46f4-92d1-f5b25f76bac1")
    }
}













