/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */
package aps

import jdk.nashorn.internal.ir.*
import java.nio.file.*
import java.nio.file.StandardWatchEventKinds.*
import java.io.File

import jdk.nashorn.internal.ir.visitor.NodeVisitor
import jdk.nashorn.internal.parser.Parser;
import jdk.nashorn.internal.parser.TokenType
import jdk.nashorn.internal.runtime.Context;
import jdk.nashorn.internal.runtime.ErrorManager;
import jdk.nashorn.internal.runtime.Source;
import jdk.nashorn.internal.runtime.options.Options;


val MODE = "watch"
val DEBUG_OUT_FILE = true

var emittedShitCount = 0

fun main(args: Array<String>) {
    when (MODE) {
        "test" -> test()
        "watch" -> watch()
        else -> throw Exception("Weird MODE: $MODE")
    }
    println("OK")
}

fun watch() {
    val watcher = FileSystems.getDefault().newWatchService()
    val dir = Paths.get("E:/work/aps/front/out")
    dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE)

    val pathStringToEntry = mutableMapOf<String, Entry>()

    println("WatchAndEnhanceJS shit is spinning...")
    while (true) {
        val key = watcher.take()

        for (event in key.pollEvents()) {
            val kind = event.kind()

            if (kind === OVERFLOW) {
                println("WTF: I've got an OVERFLOW event")
                continue
            }

            val ev = @Suppress("UNCHECKED_CAST") (event as WatchEvent<Path>)
            val path: Path = dir.resolve(ev.context()) ?: throw Exception("Path is null")
            // println("kind = $kind; path = ${path}")

            val pathString = path.toString()
            if (!pathString.endsWith(".meta.js") && (pathString.endsWith(".js") || pathString.endsWith(".js.map"))) {
                val entry = pathStringToEntry[pathString]

                if (entry == null) {
                    if (kind == ENTRY_MODIFY) {
                        val newEntry = Entry(pathString)
                        pathStringToEntry[pathString] = newEntry
                    } else throw Exception("Expecting ENTRY_MODIFY as first event, but got $kind ($path)")
                } else {
                    when (entry.stage) {
                        1 -> {
                            if (kind != ENTRY_DELETE) throw Exception("Expecting ENTRY_DELETE at state ${entry.stage} ($path)")
                            entry.stage = 2
                        }
                        2 -> {
                            if (kind != ENTRY_CREATE) throw Exception("Expecting ENTRY_CREATE at state ${entry.stage} ($path)")
                            entry.stage = 3
                        }
                        3 -> {
                            if (kind != ENTRY_MODIFY) throw Exception("Expecting ENTRY_MODIFY at state ${entry.stage} ($path)")

                            if (pathString.endsWith(".js")) {
                                // println("JS file is ready: $path")
                            } else if (pathString.endsWith(".js.map")) {
                                val basePath = pathString.substring(0, pathString.length - ".js.map".length)
                                val jsFileEntry = pathStringToEntry[basePath + ".js"] ?: throw Exception("Map file is ready before corresponding JS ($path)")
                                val mapFileEntry = entry

                                doStuff(jsFileEntry, mapFileEntry)

                                pathStringToEntry.remove(jsFileEntry.pathString)
                                pathStringToEntry.remove(mapFileEntry.pathString)
                            } else throw Exception("Unexpected file: ${path}")
                        }
                        else -> throw Exception("Weird stage: ${entry.stage} ($path)")
                    }
                }
            }
        }

        val valid = key.reset()
        if (!valid) {
            println("FUCKUP: Cannot reset watch key")
            break
        }
    }
}

fun test() {
    println("Testing...")
    val jsFileEntry = Entry("E:/work/aps/front/out/front.js")
    val mapFileEntry = Entry("E:/work/aps/front/out/front.js.map")
    doStuff(jsFileEntry, mapFileEntry)
}

fun doStuff(jsFileEntry: Entry, mapFileEntry: Entry) {
    // println("Doing stuff with ${jsFileEntry} and ${mapFileEntry}")

    val options = Options("nashorn")
    options.set("anon.functions", true)
    options.set("parse.only", true)
    options.set("scripting", true)

    val errors = ErrorManager()
    val context = Context(options, errors, Thread.currentThread().contextClassLoader)
    val inJSFile = File(jsFileEntry.pathString)
    val origSourceCode = inJSFile.readText()
    val source = Source.sourceFor(jsFileEntry.pathString, origSourceCode)
    val parser = Parser(context.env, source, errors)
    val root = parser.parse()

    val changes = mutableListOf<Change>()

    root.accept(object : NodeVisitor<BlockLexicalContext>(BlockLexicalContext()) {

        override fun enterCallNode(callNode: CallNode?): Boolean {
            if (callNode == null) throw Exception("callNode is fuckingly null")

            val tos = callNode.function.toString()
            if (callNode.function.isTokenType(TokenType.IDENT)) {
                val ident = callNode.function as IdentNode
                if (ident.name == "__await") {
                    changes.add(Change(ident.start, ident.length(), "yield "))
                }
                else if (ident.name == "__asyncResult") {
                    changes.add(Change(ident.start, ident.length(), ""))
                }
            }

            return super.enterCallNode(callNode)
        }

        override fun enterFunctionNode(functionNode: FunctionNode?): Boolean {
            if (functionNode == null) throw Exception("functionNode is fuckingly null")

//            if (functionNode.name.contains("someAsyncShit")) {
//            }

            // println("functionNode: ${functionNode.name} @ ${functionNode.position()}")
            val statements = functionNode.body.statements
            if (statements.size > 0) {
                val firstStatement = statements[0]
                if (firstStatement.isTokenType(TokenType.STRING)) {
                    val firstString = firstStatement.toString()
                    // println("First string: [${firstString}]")
                    if (firstString == "\"__async\"") {
                        // println("Transforming __async function ${functionNode.name} @ line ${functionNode.lineNumber}")
                        changes.add(Change(functionNode.start + 1, 0, "return __awaiter(this, void 0, void 0, function* () {"))
                        changes.add(Change(functionNode.finish - 1, 0, "});"))
                    }
                }
            }

            return super.enterFunctionNode(functionNode)
        }
    })

    changes.add(Change(0, 0, flattenJS("""
        var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
            return new (P || (P = Promise))(function (resolve, reject) {
                function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
                function rejected(value) { try { step(generator.throw(value)); } catch (e) { reject(e); } }
                function step(result) { result.done ? resolve(result.value) : new P(function (resolve) { resolve(result.value); }).then(fulfilled, rejected); }
                step((generator = generator.apply(thisArg, _arguments)).next());
            });
        };
    """)))

    // TODO:vgrechka Detect and report overlapping changes    a56f7601-81f1-4a31-81f3-e51908933453
    changes.sortBy { it.start }

    val newSourceCode = buildString {
        var pos = 0
        for (change in changes) {
            append(origSourceCode.substring(pos, change.start))
            append(change.insert)
            pos = change.start + change.remove
        }
        append(origSourceCode.substring(pos))
    }

    fun logEmitted(f: File) {
        println("${++emittedShitCount}) Emitted $f")
    }

    if (DEBUG_OUT_FILE) {
        val debugOutJSFile = File("c:/tmp/out-" + inJSFile.name)
        // logEmitted(debugOutJSFile)
        debugOutJSFile.writeText(newSourceCode)
    }

    val inMapFile = File(mapFileEntry.pathString)
    val outMapFile = File("E:/work/aps/aps/built/ua-writer/kotlin/" + inMapFile.name)
    inMapFile.copyTo(outMapFile, overwrite = true)
    // logEmitted(outMapFile)

    val outJSFile = File("E:/work/aps/aps/built/ua-writer/kotlin/" + inJSFile.name)
    outJSFile.writeText(newSourceCode)
    logEmitted(outJSFile)
}

fun flattenJS(input: String): String {
    return input.replace(Regex("(\\r|\\n)+"), "; ")
}

data class Entry(val pathString: String) {
    var stage = 1
}

data class Change(val start: Int, val remove: Int, val insert: String) {}


