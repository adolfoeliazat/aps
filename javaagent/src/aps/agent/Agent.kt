package aps.agent

import java.lang.instrument.Instrumentation
import kotlin.system.exitProcess

class Agent {
    companion object {
        @JvmStatic
        fun premain(options: String?, instrumentation: Instrumentation) {
            println("Fuck you")
            exitProcess(1)
        }
    }
}

