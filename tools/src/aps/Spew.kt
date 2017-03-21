package aps

import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.calls.callUtil.getCalleeExpressionIfAny
import java.time.LocalDateTime
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import kotlin.properties.Delegates.notNull
import java.io.File
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

object Spew {

    @JvmStatic fun main(args: Array<String>) {
        // generateInPlaceShit()
        generateSeparateShit()
        clog("We good")
    }



    class generateSeparateShit {
        val backOutFile = File(const.file.APS_HOME + "/back/src/aps/back/generated-back.kt")
        val backCode = StringBuilder()
        val shitBack = Shitter(backCode, indent = 0)

        val frontOutFile = File(const.file.APS_HOME + "/front/src/aps/front/generated-front.kt")
        val frontCode = StringBuilder()
        val shitFront = Shitter(frontCode, indent = 0)

        val sharedOutFile = File(const.file.APS_HOME + "/shared/src/aps/generated-shared.kt")
        val sharedCode = StringBuilder()
        val shitShared = Shitter(sharedCode, indent = 0)

        init {
            shitHeaderComment(shitBack, "aps.back")
            shitHeaderComment(shitFront, "aps.front")
            shitHeaderComment(shitShared, "aps")

            val remoteProcFiles = listOf("rp-test-2.kt", "rp-shebang.kt", "rp-user.kt", "rp-order.kt", "rp-history.kt")
            val ctrlIndexFile = "ctrl-index.kt"
            val backSrcFiles = remoteProcFiles
            val frontSrcFiles = listOf(ctrlIndexFile)
            val paths = backSrcFiles.map {const.file.APS_HOME + "/back/src/aps/back/$it"} +
                        frontSrcFiles.map {const.file.APS_HOME + "/front/src/aps/front/$it"}

            val e = try {
                clog("Working like a dog, analyzing your crappy sources...")
                FuckedCLICompiler.doMain(FuckedK2JVMCompiler(), paths.toTypedArray())
                wtf("85b5227a-6b8d-4be9-b185-5e8bf6c2491f")
            } catch (e: EnoughFuckedCompiling) {
                e
            }

            val filesLeft = e.environment.getSourceFiles().toMutableSet()

            shitFront.bigSection("REMOTE PROCEDURE STUBS")
            shitBack.bigSection("REMOTE PROCEDURE SKELETONS")
            shitShared.bigSection("REMOTE PROCEDURE TOS")
            for (file in filesLeft.filter {it.name in remoteProcFiles}) {
                filesLeft -= file
                file.accept(object:KtVisitor<Unit, Unit>() {
                    override fun visitKtFile(file: KtFile, data: Unit?) {
                        for (decl in file.declarations) {
                            decl.accept(this)
                        }
                    }

                    override fun visitNamedFunction(function: KtNamedFunction, data: Unit?) {
                        // clog("Function:", function.name)
                        for (annotationEntry in function.annotationEntries) {
                            val chars = annotationEntry.node.chars
                            // clog("Annotation:", chars)
                            if (chars == "@Remote")
                                processRemoteFunction(function)
                        }
                    }
                })
            }

            shitFront.bigSection("CONTROL TESTING HELPERS")
            run {
                val file = filesLeft.find {it.name == ctrlIndexFile} ?: wtf("85b9f077-825d-45e8-adbe-f36697ddc76c")
                filesLeft -= file
                file.accept(object:KtVisitor<Unit, Unit>() {
                    override fun visitKtFile(file: KtFile, data: Unit?) {
                        val selectsObject = file.declarations
                            .find {it is KtObjectDeclaration && it.name == "selects"}!!
                        shitFront.smallSection("Select")
                        for (decl in (selectsObject as KtObjectDeclaration).declarations) {
                            if (decl is KtProperty) {
                                processSelectControlKey(decl)
                            }
                        }

                    }
                })
            }

            check(filesLeft.isEmpty()){"c81d4e2f-d2b7-47ee-9485-0ef002c309bf"}

            backupAndWrite(backOutFile, backCode.toString())
            backupAndWrite(frontOutFile, frontCode.toString())
            backupAndWrite(sharedOutFile, sharedCode.toString())
        }

        fun shitHeaderComment(shit: Shitter, packageName: String) {
            shit("""
                /*
                 * APS
                 *
                 * (C) Copyright 2015-2017 Vladimir Grechka
                 *
                 * YOU DON'T MESS AROUND WITH THIS SHIT, IT WAS GENERATED BY A TOOL SMARTER THAN YOU
                 */

                package $packageName

                import aps.*
                import kotlin.reflect.KClass
            """)
            shit("")
        }


    }




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
            val out = Shitter(output, indent)
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
    }

//    fun generateInPlaceShit() {
//        val frontFiles = listOf("Checkbox.kt")
//        val allPaths = frontFiles.map {const.file.APS_HOME + "/front/src/aps/front/$it"}
//        for (path in allPaths) {
//            object {
//                val file = File(path)
//                val fileText = file.readText()
//                var pos = 0
//                val output = StringBuilder()
//
//                init {
//                    parseFile()
//                    val newCode = output.toString()
//                    File("${const.file.APS_TEMP}/fuck.kt").writeText(newCode)
//                    backupAndWrite(file, newCode)
//                }
//
//                fun parseFile() {
//                    fucking@ while (true) {
//                        when (readToken()) {
//                            is Token.LittleMotherfucker -> parsePlain()
//                            is Token.ScriptStart -> parseScript()
//                            is Token.EOF -> break@fucking
//                            else -> {
//                                clog("Shit at position: [${fileText.substring(pos, pos + 10)}]")
//                                wtf("ca8c26bb-2d28-4e1c-94fa-342a2a4611aa")
//                            }
//                        }
//                    }
//                }
//
//                fun readToken(): Token {
//                    if (pos > fileText.lastIndex) return Token.EOF("")
//                    val restText = fileText.substring(pos)
//                    Regex("^[ \\t]*//// -----\\r?\\n").find(restText)?.let {return Token.ScriptEnd(it.value)}
//                    Regex("^[ \\t]*//// =====\\r?\\n").find(restText)?.let {return Token.GeneratedShitEnd(it.value)}
//                    "////".let {if (restText.startsWith(it)) return Token.ScriptStart(it)}
//                    return Token.LittleMotherfucker(restText.substring(0, 1))
//                }
//
//                inline fun <reified T : Token> slurp(): T {
//                    val token = readToken() as? T ?: bitch("Expecting fucking ${T::class.simpleName}")
//                    pos += token.text.length
//                    return token
//                }
//
//                fun parsePlain() {
//                    output += slurp<Token.LittleMotherfucker>().text
//                    while (readToken() is Token.LittleMotherfucker) {
//                        output += slurp<Token.LittleMotherfucker>().text
//                    }
//                }
//
//                fun parseScript() {
//                    output += slurp<Token.ScriptStart>().text
//
//                    val scriptBuf = StringBuilder()
//                    while (readToken() is Token.LittleMotherfucker) {
//                        val text = slurp<Token.LittleMotherfucker>().text
//                        output += text
//                        scriptBuf += text
//                    }
//                    val endText = slurp<Token.ScriptEnd>().text
//                    output += endText
//
//                    val indent = endText.length - endText.trimStart().length
//                    val script = scriptBuf.toString()
//
//                    clog("Executing script: [$script]")
//                    val engine = getKTSEngine()
//                    scriptContext = SpewScriptContext(output, indent)
//                    engine.eval("""
//                            val ctx = ${Spew::class.qualifiedName}.scriptContext
//                            $script
//                        """)
//
//                    while (readToken() is Token.LittleMotherfucker) { // Skipping previously generated shit
//                        slurp<Token.LittleMotherfucker>().text
//                    }
//                    output += slurp<Token.GeneratedShitEnd>().text
//                }
//            }
//        }
//    }

    fun getKTSEngine(): ScriptEngine {
        K2JVMCompiler.main(arrayOf("-version")) // XXX Without this it shits with "Failed to initialize native filesystem for Windows"
        val engine = ScriptEngineManager().getEngineByExtension("kts")!!
        clog("Engine: $engine")

        engine.eval("// Print all your dumb warnings, which we don't give a fuck about")
        clog(); clog(); clog(); clog()
        return engine
    }

    private fun backupAndWrite(file: File, newCode: String) {
        backup(file)
        file.writeText(newCode)
    }


    fun backup(file: File) {
        check(file.path.startsWith(const.file.APS_HOME)) {"9911cfc6-6435-4a54-aa74-ad492162181a"}

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

    class Shitter(val output: StringBuilder, val indent: Int) {
        operator fun invoke(src: String) {
            var s = dedent(src)
            s = reindent(indent, s)
            if (!s.endsWith("\n")) s += "\n"
            output += s
        }

        fun bigSection(title: String) {
            this("")
            this("// ==================================================================")
            this("// $title")
            this("// ==================================================================")
            this("")
        }

        fun smallSection(title: String) {
            this("// ------------------------------------------------------------------")
            this("// $title")
            this("// ------------------------------------------------------------------")
            this("")
        }
    }
}




private fun Spew.generateSeparateShit.processRemoteFunction(function: KtNamedFunction) {
    val fname = function.name ?: wtf("90c32afc-d7ae-4179-9f2e-77ef6d6c1bb9")
    when {
        fname.startsWith("miranda") -> processRemoteFunctionTemplate(
            function = function,
            humanName = "Miranda",
            genericFrontFunction = "_askMiranda",
            frontReturnType = {it},
            simplifyResponseOnFront = {"$it.value"})

        fname.startsWith("regina") -> processRemoteFunctionTemplate(
            function = function,
            humanName = "Regina",
            genericFrontFunction = "_askRegina",
            frontReturnType = {"FormResponse2<$it>"},
            simplifyResponseOnFront = {"_simplifyFormResponseMeat($it)"})

        else -> wtf("711d4e81-a807-47df-9101-57e4cae5f500")
    }
}

private fun Spew.generateSeparateShit.processRemoteFunctionTemplate(function: KtNamedFunction, humanName: String, genericFrontFunction: String, frontReturnType: (returnType: String) -> String, simplifyResponseOnFront: (String) -> String) {
    val fname = function.name!!
    clog("Generating $humanName function stub/skeleton for $fname")

    val sharedParamsClassName = "__${fname.capitalize()}"
    val typeParams = function.typeParameterList?.text ?: ""
    val typeParamsSpaced = if (typeParams.isNotBlank()) " " + typeParams else typeParams
    val typeParamsOnlyNames = when {
        typeParams.isBlank() -> ""
        else -> "<" + function.typeParameters
            .map {it.name}
            .joinToString(", ") + ">"
    }
    val declaredReturnType = function.typeReference?.node?.chars
    val returnType = when {
        declaredReturnType == null -> {
            if (!function.hasBlockBody()) wtf("No expression-body @Remote functions, please. I'm not going to guess your fucking types")
            "Unit"
        }
        else -> declaredReturnType.toString()
    }
    val sharedResponseClassName = "${sharedParamsClassName}_Response"

    val paramsText = function.valueParameterList?.node?.chars?.toString() ?: wtf("d1e169a7-6502-4fae-bdd6-30ed3d32ea17")
    val sharedFuckers = when {
        function.valueParameterList!!.parameters.isEmpty() -> "()"
        else -> paramsText.replace("(", "(val ").replace(",", ", val ")
    }
    shitShared("""
        @Generated @Ser class $sharedParamsClassName$typeParams$sharedFuckers
        @Generated class $sharedResponseClassName$typeParams(override val value: $returnType) : CommonResponseFieldsImpl(), SingleValueResponse<$returnType>
    """)

    val frontFuckers1 = function.valueParameters
        .map {
            val maybeDefaultValue = it.defaultValue?.let {" = ${it.text}"} ?: ""
            it.name + ": " + (it.typeReference?.node?.chars ?: wtf("b5c1e203-e1e0-476a-9f52-5f360fe79406")) + maybeDefaultValue
        }
        .joinToString(", ")
    val frontFuckers2 = function.valueParameters
        .map {"${it.name} = ${it.name}"}
        .joinToString(", ")
    shitFront("""
        @Generated suspend fun$typeParamsSpaced $fname($frontFuckers1): ${frontReturnType(returnType)} = ${simplifyResponseOnFront("$genericFrontFunction<$sharedResponseClassName$typeParamsOnlyNames>($sharedParamsClassName($frontFuckers2))")}
    """)

    val backFuckers = function.valueParameters
        .map {"${it.name} = this.${it.name}"}
        .joinToString(", ")
    shitBack("""
        @Generated fun$typeParamsSpaced $sharedParamsClassName$typeParamsOnlyNames.serve(): $sharedResponseClassName$typeParamsOnlyNames {
            return $sharedResponseClassName($fname($backFuckers))
        }
    """)
}

private fun Spew.generateSeparateShit.processSelectControlKey(decl: KtProperty) {
    val initer = decl.initializer as KtCallExpression
    check(initer.calleeExpression?.text == "SelectKey"){"5f0fded2-1788-43b5-9d1b-0c0acc660790"}

    val keyName = decl.name!!
    clog("Generating Select test helpers for key $keyName")
    val testerObjectName = "__${keyName.capitalize()}Tester"
    val testerEnums = mutableListOf<String>()

    for (arg in initer.valueArguments) {
        val argName = arg.getArgumentName()?.text
        when (argName) {
            "testerEnums" -> {
                val call = arg.getArgumentExpression() as KtCallExpression
                check(call.calleeExpression!!.text == "listOf"){"79f27ce8-52c0-4e60-b25e-af099b5c60c3"}
                for (fuck in call.valueArguments) {
                    val text = fuck.text
                    check(text.endsWith("::class")){"f8e79c69-f704-473b-8ac9-fcd59dd26be7"}
                    testerEnums += text.replace("::class", "")
                }
            }
            else -> wtf("argName = $argName    f04b3714-1687-4f9a-bbe1-00709fa00d01")
        }
    }

    val setValueFunsForEnums = testerEnums
        .map {"suspend fun setValue(value: $it) = setRawValue(value.name)"}
        .joinToString("        \n")
    shitFront("""
        // $keyName

            val tselect.$keyName get() = $testerObjectName
            object $testerObjectName {
                suspend fun setRawValue(value: String) = tselect.setValue(selects.$keyName, value)
                $setValueFunsForEnums
            }
    """)
    shitFront("")
}








// TODO:vgrechka Extract below from `back` into something stable that cannot be broken by code generation


object const {
    object file {
        val APS_HOME = "e:\\work\\aps"
        val APS_TEMP = "c:/tmp/aps-tmp"
    }
}

fun clog(vararg xs: Any?): Unit = println(xs.joinToString(" "))

fun wtf(msg: String = "...WTF didn't you describe this WTF?"): Nothing = throw Exception("WTF: $msg")

fun dedent(it: String): String {
    var lines = it.split(Regex("\\r?\\n"))
    if (lines.size > 0 && lines[0].isBlank()) {
        lines = lines.drop(1)
    }
    if (lines.size > 0 && lines.last().isBlank()) {
        lines = lines.dropLast(1)
    }

    var minIndent = 9999 // TODO:vgrechka Platform-specific max integer (for JS: Number.MAX_SAFE_INTEGER)
    for (line in lines) {
        if (!line.isBlank()) {
            val lineIndent = line.length - line.trimStart().length
            if (lineIndent < minIndent) {
                minIndent = lineIndent
            }
        }
    }

    return lines.map {line ->
        if (line.trim().isBlank()) ""
        else line.substring(minIndent)
    }.joinToString("\n")
}

fun reindent(newIndent: Int, it: String): String {
    return dedent(it).split("\n").joinToString("\n") {" ".repeat(newIndent) + it}
}

operator fun StringBuilder.plusAssign(x: Any?) {
    this.append(x)
}

val PG_LOCAL_DATE_TIME = DateTimeFormatterBuilder()
    .parseCaseInsensitive()
    .append(DateTimeFormatter.ISO_LOCAL_DATE)
    .appendLiteral(' ')
    .append(DateTimeFormatter.ISO_LOCAL_TIME)
    .toFormatter()!!

fun bitch(msg: String = "Just bitching..."): Nothing = throw Exception(msg)

fun stringBuild(block: (StringBuilder) -> Unit) =
    StringBuilder().also(block).toString()
















//for (ann in decl.annotationEntries) {
//    if (ann.typeReference?.text == "SelectTesterEnums") {
//        for (arg in ann.valueArgumentList!!.arguments) {
//            testerEnums += arg.text.replace("::class", "")
//        }
//    }
//}


























