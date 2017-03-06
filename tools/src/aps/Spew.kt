package aps

import into.kommon.*
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import java.io.File
import javax.script.Invocable
import javax.script.ScriptContext
import javax.script.ScriptEngineManager
import javax.script.SimpleBindings
import kotlin.properties.Delegates.notNull

object Spew {
    sealed class Token(val text: String) {
        class LittleMotherfucker(text: String) : Token(text)
        class ScriptStart(text: String) : Token(text)
        class ScriptEnd(text: String) : Token(text)
        class GeneratedShitEnd(text: String) : Token(text)
        class EOF(text: String) : Token(text)
    }

    var scriptContext by notNull<SpewScriptContext>()

    class SpewScriptContext(val output: StringBuilder, val indent: Int) {
        @Suppress("Unused")
        fun spewTestFiddlers(type: String) {
            clog("Spewing test fiddlers for $type")
            out("""
                suspend fun setValue(field: TestRef<CheckboxFieldSpec>, value: Boolean, subscript: Any? = null) {
                    setValue(FieldSpecToCtrlKey[field.it], value, subscript)
                }

                suspend fun setValue(key: TestRef<CheckboxKey>, value: Boolean, subscript: Any? = null) {
                    setValue(key.it, value, subscript)
                }

                private suspend fun setValue(key: CheckboxKey, value: Boolean, subscript: Any?) {
                    val target = Checkbox.instance(if (subscript == null) key
                                                   else SubscriptCheckboxKey(key, subscript))
                    if (target is Control2) {
                        target.hand()
                    }
                    target.testSetValue(value)
                }
            """)
        }

        fun out(src: String) {
            var s = dedent(src)
            s = reindent(indent, s)
            if (!s.endsWith("\n")) s += "\n"
            output += s
        }
    }

    @JvmStatic fun main(args: Array<String>) {
        for (f in listOf(File(const.file.APS_HOME + "/front/src/aps/front/Checkbox.kt"))) {
            object {
                val fileText = f.readText()
                var pos = 0
                val output = StringBuilder()

                init {
                    parseFile()
                    File("c:/tmp/fuck.kt").writeText(output.toString())
                }

                fun parseFile() {
                    fucking@while (true) {
                        when (readToken()) {
                            is Token.LittleMotherfucker -> parsePlain()
                            is Token.ScriptStart -> parseScript()
                            is Token.EOF -> break@fucking
                            else -> {
                                clog("Shit at position: [${fileText.substring(pos, pos + 10)}]")
                                wtf("ca8c26bb-2d28-4e1c-94fa-342a2a4611aa")
                            }
                        }
                    }
                }

                fun readToken(): Token {
                    if (pos > fileText.lastIndex) return Token.EOF("")
                    val restText = fileText.substring(pos)
                    Regex("^[ \\t]*//// ----\\r?\\n").find(restText)?.let {return Token.ScriptEnd(it.value)}
                    Regex("^[ \\t]*//// ====\\r?\\n").find(restText)?.let {return Token.GeneratedShitEnd(it.value)}
                    "////".let {if (restText.startsWith(it)) return Token.ScriptStart(it)}
                    return Token.LittleMotherfucker(restText.substring(0, 1))
                }

                inline fun <reified T : Token> slurp(): T {
                    val token = readToken() as? T ?: bitch("Expecting fucking ${T::class.simpleName}")
                    pos += token.text.length
                    return token
                }

                fun parsePlain() {
                    output += slurp<Token.LittleMotherfucker>().text
                    while (readToken() is Token.LittleMotherfucker) {
                        output += slurp<Token.LittleMotherfucker>().text
                    }
                }

                fun parseScript() {
                    output += slurp<Token.ScriptStart>().text

                    val scriptBuf = StringBuilder()
                    while (readToken() is Token.LittleMotherfucker) {
                        val text = slurp<Token.LittleMotherfucker>().text
                        output += text
                        scriptBuf += text
                    }
                    val endText = slurp<Token.ScriptEnd>().text
                    output += endText

                    val indent = endText.length - endText.trimStart().length
                    val script = scriptBuf.toString()

                    clog("Executing script: [$script]")
                    K2JVMCompiler.main(arrayOf("-version")) // XXX Without this it shits with "Failed to initialize native filesystem for Windows"
                    val engine = ScriptEngineManager().getEngineByExtension("kts")!!
                    clog("Engine: $engine")

                    scriptContext = SpewScriptContext(output, indent)
                    engine.eval("""
                        val ctx = ${Spew::class.qualifiedName}.scriptContext
                        $script
                    """)

                    output += " ".repeat(indent)  + "// Here is some generated shit for you\n"

                    while (readToken() is Token.LittleMotherfucker) { // Skipping previously generated shit
                        slurp<Token.LittleMotherfucker>().text
                    }
                    output += slurp<Token.GeneratedShitEnd>().text
                }
            }
        }

        clog("We good")
    }
}






