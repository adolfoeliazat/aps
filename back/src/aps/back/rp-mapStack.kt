/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import into.kommon.*
import com.google.debugging.sourcemap.SourceMapConsumerFactory
import com.google.debugging.sourcemap.SourceMapping
import java.io.File

val mapPathToSourceMapping = mutableMapOf<String, SourceMapping>()

val NORMAL_APS_HOME = normalizePath(APS_HOME)
val NORMAL_KOMMON_HOME = normalizePath(KOMMON_HOME)

@RemoteProcedureFactory @Synchronized fun mapStack() = testProcedure(
    MapStackRequest(),
    runShit = fun(ctx, req): MapStackRequest.Response {
        val noisy = false

        // Ex:    at Object.die_61zpoe$ (http://aps-ua-writer.local:3022/into-kommon-js-enhanced.js:32:17)
        // Ex:    at http://aps-ua-writer.local:3022/front-enhanced.js:12225:48
        // Ex:    at Generator.next (<anonymous>)
        // Ex:    at __awaiter (http://aps-ua-writer.local:3022/into-kommon-js-enhanced.js:1:138)

        val mangledStack = req.mangledStack.value
        val resultLines = mutableListOf<String>()

        for (mangledLine in mangledStack.lines()) {
            class Skip(val reason: String) : Exception()
            class Verbatim(val reason: String) : Exception()

            try {
                if (!mangledLine.startsWith("    at ")) throw Verbatim("Doesn't start with [at]")

                val mr = Regex("(.*?)\\(?(https?://.*):(\\d+):(\\d+)\\)?").matchEntire(mangledLine)
                    ?: run {
                        if (mangledLine.contains("at Generator.next")) throw Skip("Useless junk")
                        throw Verbatim("Unrecognized")
                    }
                if (noisy) dwarnStriking("Recognized: \n" + inspectMatchResult(mr))
                val (prefix, resource, lineString, columnString) = mr.destructured

                val (line, column) = try {Pair(lineString.toInt(), columnString.toInt())}
                catch (e: NumberFormatException) {throw Verbatim("Bad line or column number")}

                if (line == 1) throw Skip("Ignoring line 1, as it's probably __awaiter() or some other junk")

                val mapPath = when {
                    resource.contains("/front-enhanced.js") -> "$APS_HOME/front/out/front.js.map"
                    resource.contains("/into-kommon-js-enhanced.js") -> "$KOMMON_HOME/js/out/into-kommon-js.js.map"
                    else -> throw Verbatim("No map file for $resource")
                }
                val sourceMapping = mapPathToSourceMapping.getOrPut(mapPath) {
                    SourceMapConsumerFactory.parse(File(mapPath).readText())
                }
                val orig = sourceMapping.getMappingForLine(line, column)
                    ?: throw Verbatim("No mapping for line")

                var longPath = orig.originalFile
                var shortPath = orig.originalFile
                if (longPath.startsWith("file://")) {
                    longPath = normalizePath(shortPath.substring("file://".length))
                    shortPath = when {
                        longPath.startsWith(NORMAL_APS_HOME) -> "APS" + longPath.substring(NORMAL_APS_HOME.length)
                        longPath.startsWith(NORMAL_KOMMON_HOME) -> "KOMMON" + longPath.substring(NORMAL_KOMMON_HOME.length)
                        else -> shortPath
                    }
                }

                var marginNotes = mutableListOf<String>()
                try {
                    val line = File(longPath).readLines()[orig.lineNumber - 1]
                    if (line.contains(Regex("\\so\\."))) marginNotes.add("o.")
                    Regex("\\bassert(\\w|\\d|_)*").find(line)?.let {marginNotes.add(it.value)}
                    when {
                        line.contains("\"\"\"") -> marginNotes.add("\"\"\"")
                        line.contains("\"") -> marginNotes.add("\"")
                    }
                } catch (e: Exception) {}

                val result = "$prefix ($shortPath:${orig.lineNumber}:${orig.columnPosition})" +
                             nbsp.repeat(5) + marginNotes.joinToString(nbsp.repeat(3))
                resultLines.add(result)
            }
            catch (e: Skip) {
                if (noisy) dwarnStriking("Skip: ${e.reason}: $mangledLine")
            }
            catch (e: Verbatim) {
                if (noisy) dwarnStriking("Verbatim: ${e.reason}: $mangledLine")
                resultLines.add(
                    if (mangledLine.startsWith("    at ")) mangledLine.replaceRange(0, 1, when {
                        mangledLine.contains("kotlin") -> "K" // Standard library
                        else -> "?"
                    })
                    else mangledLine
                )
            }
        }

        return MapStackRequest.Response(resultLines.joinToString("\n"))
    }
)

