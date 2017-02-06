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
    data class GUIDEntry(val guid: String, val file: File, val lineIndex: Int)

    init {
        print("Linting your shit... ")

        val guids = mutableMapOf<String, GUIDEntry>()

        visitSources("$APS_HOME/front/src") {f->
            f.useLines {
                for ((lineIndex, line) in it.withIndex()) {
                    if (line.startsWith("//")) continue
                    class Shit(message: String): Exception(message)
                    try {
                        for (tag in listOf("kdiv", "kspan", "h3", "kul", "kli", "ka", "kform")) {
                            if (Regex("\\W$tag\\W").containsMatchIn(line)) {
                                if (line.trimEnd().endsWith("{")) {
                                    throw Shit("Lambda parameter is mandatory for $tag")
                                }
                            }
                        }

                        if (Regex("\\Wrun \\{\\s*\"__async\"").containsMatchIn(line)) {
                            throw Shit("Don't pass async lambdas to inline functions. Use [runni]")
                        }

                        Regex("\"([0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12})\"")
                            .findAll(line)
                            .map {GUIDEntry(it.groupValues[1], f, lineIndex)}
                            .forEach {thiz->
                                guids[thiz.guid]?.let {prev->
                                    throw Shit("\nGUID duplication:\n" +
                                                   "1) ${prev.file.name}:${prev.lineIndex}    $prev\n" +
                                                   "2) ${thiz.file.name}:${thiz.lineIndex}    $thiz\n")
                                }
                                guids[thiz.guid] = thiz
                            }
                    } catch (e: Shit) {
                        println("SHIT")
                        val fname = f.path.substring(APS_HOME.length)
                        println("$fname:${lineIndex + 1}: ${e.message}")
                        exitProcess(1)
                    }
                }
            }
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


