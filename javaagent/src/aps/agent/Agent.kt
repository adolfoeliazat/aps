package aps.agent

import net.bytebuddy.agent.builder.AgentBuilder
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.implementation.bind.annotation.Origin
import net.bytebuddy.matcher.ElementMatchers
import java.lang.instrument.Instrumentation
import java.util.concurrent.Callable
import net.bytebuddy.implementation.bind.annotation.SuperCall
import net.bytebuddy.implementation.bind.annotation.RuntimeType
import java.lang.reflect.Method


class Agent {
    companion object {
        @JvmStatic
        fun premain(options: String?, instrumentation: Instrumentation) {
            println("fuuuuuuuuuuuuuuuuuck youuuuuuuuuuuuuuuuuuu")
            AgentBuilder.Default()
      .type(ElementMatchers.named("aps.back.BrutalWelcomer"))
                .transform { builder, typeDescription, classLoader, module ->
                    println("zzzzzzzz " + typeDescription)
                    builder.method(ElementMatchers.any())
                        .intercept(MethodDelegation.to(TimingInterceptor::class.java))
                }
      .installOn(instrumentation)
        }
    }
}


class TimingInterceptor {
    @RuntimeType
    fun intercept(@Origin method: Method,
                  @SuperCall callable: Callable<*>): Any {
        println("aaaaaaaaaaaaaaaa")
        val start = System.currentTimeMillis()
        try {
            return callable.call()
        } finally {
            println(method.toString() + " took " + (System.currentTimeMillis() - start))
        }
    }
}
