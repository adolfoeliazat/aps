package aps.back

import aps.*
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

val backendPlatform = object:XBackendPlatform {
    private val paramClassToServeMethod = ConcurrentHashMap<Class<*>, Method>()

    override fun currentTimeMillis() = System.currentTimeMillis()

    override fun getServeObjectRequestFunction(params: Any): (Any) -> Any {
        val method = paramClassToServeMethod.computeIfAbsent(params::class.java) {
            for (cname in listOf("Generated_backKt", "Rp_orderKt", "Rp_userKt", "Rp_historyKt", "Rp_testKt", "Rp_test_2Kt")) {
                val clazz = Class.forName("aps.back.$cname")
                try {
                    return@computeIfAbsent clazz.getDeclaredMethod("serve", params::class.java)
                } catch (e: Throwable) {
                }
            }
            wtf("p::class = ${params::class}    a322c2b4-25af-45e1-a7ae-a5484a941ec3")
        }

        val serveFunction = {p: Any -> method.invoke(null, p)}
        return serveFunction
    }

    override fun captureStackTrace() = Exception().stackTrace.map(::JVM_XStackTraceElement).toTypedArray()
}


class JVM_XStackTraceElement(val stackTraceElement: StackTraceElement) : XStackTraceElement {
    override fun toString(): String {
        return stackTraceElement.toString()
    }
}


















