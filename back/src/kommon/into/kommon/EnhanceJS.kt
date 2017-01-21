/*
 * Into Kommon
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package kommon.into.kommon

import into.kommon.*
import jdk.nashorn.internal.ir.*
import jdk.nashorn.internal.ir.visitor.NodeVisitor
import jdk.nashorn.internal.parser.Parser
import jdk.nashorn.internal.parser.TokenType
import jdk.nashorn.internal.runtime.Context
import jdk.nashorn.internal.runtime.ErrorManager
import jdk.nashorn.internal.runtime.Source
import jdk.nashorn.internal.runtime.options.Options
import java.io.File

class EnhanceJS(path: String) {
    data class Change(val start: Int, val remove: Int, val insert: String)

    val DEBUG_OUT_FILE_PREFIX: String? = "c:/tmp/out-" // Null to turn off // TODO:vgrechka Unhardcode
    var emittedShitCount = 0
    private val noise = DebugNoise("EnhanceJS", mute = true)

    init {
        val inFile = File(path)
        println("Enhancing $path... ")
        measuringAndPrinting {
            File(inFile.absolutePath.replace(Regex("\\.js$"), "-enhanced.js")).run {
                writeText(inFile.readText())
                logEmitted(this)
            }
        }
    }

//    init {
//        val inFile = File(path)
//        println("Enhancing $path... ")
//        measuringAndPrinting {
//            val options = Options("nashorn")
//            options.set("anon.functions", true)
//            options.set("parse.only", true)
//            options.set("scripting", true)
//
//            val errors = ErrorManager()
//            val context = Context(options, errors, Thread.currentThread().contextClassLoader)
//            val origSourceCode = inFile.readText()
//            val source = Source.sourceFor(inFile.name, origSourceCode)
//            val parser = Parser(context.env, source, errors)
//            val root = parser.parse()
//            val changes = mutableListOf<Change>()
//            var inLambdaShit = 0
//
//            root.accept(object : NodeVisitor<BlockLexicalContext>(BlockLexicalContext()) {
//
//                override fun enterCallNode(callNode: CallNode?): Boolean {
//                    if (callNode == null) throw Exception("callNode is fuckingly null")
//
//                    if (inLambdaShit == 0) {
//                        if (callNode.function.isTokenType(TokenType.IDENT)) {
//                        }
//                        val function = callNode.function
//                        if (function is IdentNode) { // Can be CallNode: foo()()
//                            val ident = function
//                            if (ident.name == "__await" || ident.name == "__reawait") {
//                                changes.add(Change(ident.start, ident.length() + 1, "(yield "))
//                            }
//                            else if (ident.name == "__asyncResult") {
//                                changes.add(Change(ident.start, ident.length(), ""))
//                            }
//                        }
//                    }
//
//                    return super.enterCallNode(callNode)
//                }
//
//
//                override fun enterFunctionNode(functionNode: FunctionNode?): Boolean {
//                    if (functionNode == null) throw Exception("functionNode is fuckingly null")
//
//                    if (isShittyLambda(functionNode)) {
//                        noise.clog("Entering lambda shit:", functionNode.name)
//                        // if (functionNode.ident.name.contains("buildSteps")) _clog("shiiiit", functionNode.ident.name)
//                        ++inLambdaShit
//                    }
//
//                    // println("functionNode: ${functionNode.name} @ ${functionNode.position()}")
//                    val statements = functionNode.body.statements
//                    if (statements.size > 0) {
//                        for (statement in statements) {
//                            if (statement.isTokenType(TokenType.STRING)) {
//                                val firstString = statement.toString()
//                                // println("First string: [${firstString}]")
//                                if (firstString == "\"__async\"") {
//                                    // println("Transforming __async function ${functionNode.name} @ line ${functionNode.lineNumber}")
//                                    changes.add(Change(functionNode.start + 1, 0, "return __awaiter(this, void 0, void 0, function* () {"))
//                                    changes.add(Change(functionNode.finish - 1, 0, "});"))
//                                }
//
//                                break
//                            }
//                        }
//                    }
//
//                    return super.enterFunctionNode(functionNode)
//                }
//
//                override fun leaveFunctionNode(functionNode: FunctionNode?): Node {
//                    if (functionNode == null) throw Exception("functionNode is fuckingly null")
//
//                    if (isShittyLambda(functionNode)) {
//                        noise.clog("Leaving lambda shit:", functionNode.name)
//                        --inLambdaShit
//                    }
//
//                    return super.leaveFunctionNode(functionNode)
//                }
//
//                private fun isShittyLambda(functionNode: FunctionNode): Boolean {
//                    val hasLambdaSuffix = Regex("\\\$lambda(_\\d+)?\$").find(functionNode.name) != null
//                    if (hasLambdaSuffix) {
//                        val bodySource = origSourceCode.substring(functionNode.start, functionNode.finish)
//                        val lines = bodySource.lines()
//                        for ((i, _line) in lines.drop(1).withIndex()) {
//                            val line = _line.trim()
//                            if (i == 0 && line == "return function () {") {
//                                continue
//                            }
//                            else if (line.startsWith("var tmp\$")) {
//                                continue
//                            }
//                            else if (line == "'__async';") {
//                                noise.clog("------------- Not a shitty lambda -------------")
//                                noise.clog("${functionNode.ident.name} <<<" + origSourceCode.substring(functionNode.start, functionNode.finish) + ">>>")
//                                return false
//                            }
//                            else {
//                                break
//                            }
//                        }
//                    }
//                    return hasLambdaSuffix
//                }
//            })
//
//            changes.add(Change(0, 0, flattenJS("""
//                var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
//                    return new (P || (P = Promise))(function (resolve, reject) {
//                        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
//                        function rejected(value) { try { step(generator.throw(value)); } catch (e) { reject(e); } }
//                        function step(result) { result.done ? resolve(result.value) : new P(function (resolve) { resolve(result.value); }).then(fulfilled, rejected); }
//                        step((generator = generator.apply(thisArg, _arguments)).next());
//                    });
//                };
//            """)))
//
//            // TODO:vgrechka Detect and report overlapping changes
//            changes.sortBy { it.start }
//
//            val newSourceCode = buildString {
//                var pos = 0
//                for (change in changes) {
//                    append(origSourceCode.substring(pos, change.start))
//                    append(change.insert)
//                    pos = change.start + change.remove
//                }
//                append(origSourceCode.substring(pos))
//            }
//            .replace("this['into-kommon-js']", "global['into-kommon-js']") // TODO:vgrechka Generalize
//
//            if (DEBUG_OUT_FILE_PREFIX != null) {
//                File(DEBUG_OUT_FILE_PREFIX + inFile.name).run {
//                    writeText(newSourceCode)
//                    // logEmitted(this)
//                }
//            }
//
//            File(inFile.absolutePath.replace(Regex("\\.js$"), "-enhanced.js")).run {
//                writeText(newSourceCode)
//                logEmitted(this)
//            }
//
////            val inMapFile = File(mapFileEntry.pathString)
////            inMapFile.copyTo(File("$APS_ROOT/aps/built/ua-writer/kotlin/" + inMapFile.name), overwrite = true)
////
////            File("$APS_ROOT/aps/built/ua-writer/kotlin/" + inFile.name.replace(Regex("\\.js$"), "-enhanced.js")).run{
////                this.writeText(newSourceCode)
////                logEmitted(this)
////            }
////            File(inFile.absolutePath.replace(Regex("\\.js$"), "-enhanced.js")).run{
////                this.writeText(newSourceCode)
////                logEmitted(this)
////            }
//        }
//    }

    fun flattenJS(input: String): String {
        return input.replace(Regex("(\\r|\\n)+"), "; ")
    }

    fun logEmitted(f: File) {
        println("${++emittedShitCount}) Emitted $f")
    }


    companion object {
        @JvmStatic fun main(vararg args: String) {
            check(args.size == 1) {"I want one argument, which is JS file to enhance"}
            EnhanceJS(args[0])
        }
    }
}


// TODO:vgrechka @unduplicate
private class DebugNoise(val tag: String, val mute: Boolean, val style: Style = Style.IN_THREE_DASHES) {
    enum class Style {IN_THREE_DASHES, COLON}

    inline fun clog(vararg xs: Any?) {
        if (!mute) {
            when (style) {
                Style.IN_THREE_DASHES -> _clog("---$tag---", *xs)
                Style.COLON -> _clog("$tag: ", *xs)
                else -> wtf()
            }
        }
    }
}

// TODO:vgrechka @unduplicate
private fun _clog(vararg xs: Any?): Unit = println(xs.joinToString(" "))








