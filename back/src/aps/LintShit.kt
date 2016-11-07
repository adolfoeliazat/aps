/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

import into.kommon.*
import java.io.File
import kotlin.system.exitProcess

class LintShit {
    init {
        print("Linting your shit... ")
        visitSources("$APS_HOME/front/src") { f->
            f.useLines {it.forEachIndexed {lineIndex, line ->
                class Shit(message: String): Exception(message)
                try {
                    for (tag in listOf("kdiv", "kspan", "h3")) {
                        if (Regex("\\W$tag\\W").containsMatchIn(line)) {
                            if (line.trimEnd().endsWith("{")) {
                                throw Shit("Lambda parameter is mandatory for $tag")
                            }
                        }
                    }

                    if (Regex("\\Wrun \\{\\s*\"__async\"").containsMatchIn(line)) {
                        throw Shit("Don't pass async lambdas to inline functions. Use [runni]")
                    }
                } catch (e: Shit) {
                    println("SHIT")
                    val fname = f.path.substring(APS_HOME.length)
                    println("$fname:${lineIndex + 1}: ${e.message}")
                    exitProcess(1)
                }
            }}
        }
        println("COOL")
    }

    fun visitSources(root: String, block: (File) -> Unit) {
        fun visit(f: File) {
            if (f.isDirectory) return f.listFiles().forEach {visit(it)}
            if (!f.isFile) wtf("File: $f")
            if (f.extension != "kt") return

            block(f)
        }

        visit(File(root))
    }

    companion object {
        @JvmStatic fun main(vararg args: String) {
            LintShit()
        }
    }

}


