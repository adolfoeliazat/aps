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
                for (tag in listOf("kdiv", "kspan", "h3")) {
                    if (Regex("\\W$tag\\W").containsMatchIn(line)) {
                        if (line.trimEnd().endsWith("{")) {
                            val fname = f.path.substring(APS_HOME.length)
                            println("SHIT")
                            println("$fname:${lineIndex + 1}: Lambda parameter is mandatory for $tag")
                            exitProcess(1)
                        }
                    }
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


