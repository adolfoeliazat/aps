package aps.agent

import java.lang.instrument.Instrumentation

class Agent {
    companion object {
        @JvmStatic
        fun premain(options: String?, instrumentation: Instrumentation) {
            println("222 fuuuuuuuuuuuuuuuuuck youuuuuuuuuuuuuuuuuuu")
        }
    }
}

