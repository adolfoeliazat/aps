package aps

import aps.back.*
import into.kommon.*
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import java.io.File
import java.time.LocalDateTime
import javax.script.ScriptEngineManager
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
                // pizda 2
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
        val frontFiles = listOf("Checkbox.kt")
        val allPaths = frontFiles.map {const.file.APS_HOME + "/front/src/aps/front/$it"}
        for (path in allPaths) {
            object {
                val file = File(path)
                val fileText = file.readText()
                var pos = 0
                val output = StringBuilder()

                init {
                    parseFile()
                    val newCode = output.toString()
                    File("${const.file.APS_TEMP}/fuck.kt").writeText(newCode)
                    backup(file)
                    file.writeText(newCode)
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
                    Regex("^[ \\t]*//// -----\\r?\\n").find(restText)?.let {return Token.ScriptEnd(it.value)}
                    Regex("^[ \\t]*//// =====\\r?\\n").find(restText)?.let {return Token.GeneratedShitEnd(it.value)}
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

                    engine.eval("// Print all your dumb warnings, which we don't give a fuck about")
                    clog(); clog(); clog(); clog()

                    scriptContext = SpewScriptContext(output, indent)
                    engine.eval("""
                        val ctx = ${Spew::class.qualifiedName}.scriptContext
                        $script
                    """)

                    while (readToken() is Token.LittleMotherfucker) { // Skipping previously generated shit
                        slurp<Token.LittleMotherfucker>().text
                    }
                    output += slurp<Token.GeneratedShitEnd>().text
                }

                fun backup(file: File) {
                    check(file.path.startsWith(const.file.APS_HOME)){"9911cfc6-6435-4a54-aa74-ad492162181a"}

                    val stamp = LocalDateTime.now().format(PG_LOCAL_DATE_TIME).replace(Regex("[ :\\.]"), "-")
                    val outPath = (
                        const.file.APS_TEMP + "/spew-bak/" +
                            file.path
                                .substring(const.file.APS_HOME.length)
                                .replace("\\", "/")
                                .replace(Regex("^/"), "")
                                .replace("/", "--")
                            + "----$stamp"
                        )

                    clog("Backing up: $outPath")
                    File(outPath).writeText(file.readText())
                }
            }
        }

        clog("We good")
    }
}








