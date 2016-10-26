/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import java.io.*
import aps.*
import java.time.LocalDateTime

class GenerateShit {
    class MixableType(val name: String, val props: List<Prop> = mutableListOf())

    enum class PAState {
        WAITING_INITIAL_ANNOTATION,
        REQUIRING_FUNCTION_NAME_AND_FIRST_PARAM,
        REQUIRING_PARAM_OR_END_OF_FUNCTION_SIGNATURE,
        REQUIRING_CLASS_NAME,
        REQUIRING_PROP_OR_END_OF_CLASS_SIGNATURE
    }

    enum class PAPass {
        FIRST,
        SECOND
    }

    enum class ShitKind {
        NONE,
        SIGNATURE_MIXES,
        MIXABLE_TYPE
    }

    enum class ParamKind {
        OWN,
        MIX,
        AFTER_MIX
    }

    class Param {
        lateinit var kind: ParamKind
        lateinit var signature: String
        lateinit var name: String
        lateinit var type: String

        override fun toString(): String {
            return "Param(kind=$kind, signature='$signature', name='$name', type='$type')"
        }
    }

    class Prop {
        lateinit var name: String
        lateinit var type: String
        lateinit var defaultValue: String

        override fun toString(): String {
            return "Prop(name='$name', type='$type', defaultValue='$defaultValue')"
        }
    }

    class Shit {
        var shitKind = ShitKind.NONE
        var functionName = "<undefined>"
        var params = mutableListOf<Param>()
        var plainFunctionSignature = StringBuilder()
        var className = "<undefined>"
        var props = mutableListOf<Prop>()
        var paramSection = ParamKind.OWN
        var extensionOf: String? = null
    }

    val nameToMixableType = mutableMapOf<String, MixableType>()

    val generatedShit = StringBuilder(dedent("""
        /*
         * APS
         *
         * (C) Copyright 2015-2016 Vladimir Grechka
         *
         * SHIT IN THIS FILE IS GENERATED
         */

        package aps.front

        import aps.*
        import aps.front.*

    """) + "\n")

    init {
        println("Generating some shit for you...")
//        renameme1()
        processAnnotations()
        File("$APS_ROOT/front/src/aps/front/generated-shit.kt").writeText("" + generatedShit)
        println("COOL")
    }

    fun processAnnotations() {
        for (pass in PAPass.values()) {
            visitSources("$APS_ROOT/front/src") {f->
                var state = PAState.WAITING_INITIAL_ANNOTATION
                var shit = Shit()

                fun doShit() {
                    exhaustive/when (shit.shitKind) {
                        ShitKind.NONE -> wtf()

                        ShitKind.SIGNATURE_MIXES -> {
                            with (generatedShit) {
                                fun deleteLastComma() {
                                    val lastCommaIndex = length - 1 - ",\n".length
                                    delete(lastCommaIndex, lastCommaIndex + 1)
                                }

                                val pfsLines = shit.plainFunctionSignature.trim().lines()
                                var firstLine = pfsLines.first().substringBefore("(") + "("
                                shit.extensionOf?.let {
                                    firstLine = firstLine.replace(Regex("(^|\\s)fun "), "\$1fun $it.")
                                }
                                appendln(firstLine)
                                val indent = " ".repeat(4)

                                var shouldBlankSeparate = false
                                for (param in shit.params.filter {it.kind == ParamKind.OWN}) {
                                    appendln(indent + param.signature + ",")
                                    shouldBlankSeparate = true
                                }

                                for (param in shit.params.filter {it.kind == ParamKind.MIX}) {
                                    if (shouldBlankSeparate) appendln()
                                    shouldBlankSeparate = true
                                    appendln(indent + "// @Mix ${param.name}")
                                    val mixableType = nameToMixableType[param.type] ?: wtf("Unknown mixable type: ${param.type}")
                                    for (prop in mixableType.props) {
                                        appendln(indent + "${prop.name}: ${prop.type} = ${prop.defaultValue},")
                                    }
                                }

                                val afterMixParams = shit.params.filter {it.kind == ParamKind.AFTER_MIX}
                                if (afterMixParams.isNotEmpty()) {
                                    appendln()
                                    appendln(indent + "// @AfterMix")
                                    for (param in afterMixParams) {
                                        appendln(indent + param.signature + ",")
                                    }
                                }

                                deleteLastComma()
                                appendln(pfsLines.last().trim())

                                appendln("    return ${shit.functionName}(")
                                shouldBlankSeparate = false
                                for (param in shit.params) {
                                    exhaustive/when (param.kind) {
                                        GenerateShit.ParamKind.OWN -> {
                                            appendln("        ${param.name} = ${param.name},")
                                            shouldBlankSeparate = true
                                        }

                                        GenerateShit.ParamKind.MIX -> {
                                            if (shouldBlankSeparate) appendln()
                                            shouldBlankSeparate = true
                                            appendln("        ${param.name} = ${param.type}(")
                                            val mixableType = nameToMixableType[param.type] ?: wtf("Unknown mixable type: ${param.type}")
                                            for (prop in mixableType.props) {
                                                appendln("            ${prop.name} = ${prop.name},")
                                            }
                                            deleteLastComma()
                                            appendln("        ),")
                                        }

                                        GenerateShit.ParamKind.AFTER_MIX -> {
                                            appendln()
                                            appendln("        ${param.name} = ${param.name},")
                                        }
                                    }
                                }

                                deleteLastComma()
                                appendln("    )")
                                appendln("}")
                                appendln()
                            }
                        }

                        GenerateShit.ShitKind.MIXABLE_TYPE -> {
                            val mt = MixableType(shit.className, shit.props)
                            nameToMixableType[mt.name] = mt
                        }
                    }

                    shit.shitKind = ShitKind.NONE
                }

                fun parseParam(_from: String) {
                    var from = _from.trim()
                    val param = Param()

                    if (from.startsWith("@AfterMix ")) {
                        param.kind = ParamKind.AFTER_MIX
                        shit.paramSection = ParamKind.AFTER_MIX
                        from = from.substring("@AfterMix ".length)
                    } else if (from.startsWith("@Mix ")) {
                        if (shit.paramSection > ParamKind.MIX) wtf("Mix param in ${shit.paramSection} section")
                        param.kind = ParamKind.MIX
                        shit.paramSection = ParamKind.MIX
                        from = from.substring("@Mix ".length)
                    } else {
                        if (shit.paramSection > ParamKind.OWN) wtf("Own param in ${shit.paramSection} section")
                        param.kind = ParamKind.OWN
                    }
                    if (from.endsWith(",")) from = from.substring(0, from.length - 1)

                    param.signature = from

                    val colonIndex = from.indexOf(":")
                    val eqIndex = from.indexOf("=")
                    param.name = from.substring(0, colonIndex)
                    param.type = from.substring(colonIndex + 1, if (eqIndex == -1) from.length else eqIndex).trim()

                    // println(param)
                    shit.params.add(param)
                }

                for ((lineIndex, line) in f.readLines().withIndex()) {
                    try {
                        exhaustive/when (state) {
                            PAState.WAITING_INITIAL_ANNOTATION -> {
                                when (pass) {
                                    PAPass.FIRST -> {
                                        when {
                                            line == "@MixableType" -> {
                                                shit = Shit()
                                                shit.shitKind = ShitKind.MIXABLE_TYPE
                                                state = PAState.REQUIRING_CLASS_NAME
                                            }
                                            else -> Unit
                                        }
                                    }
                                    PAPass.SECOND -> {
                                        when {
                                            line.contains("@GenerateSignatureMixes") -> {
                                                shit = Shit()
                                                shit.shitKind = ShitKind.SIGNATURE_MIXES
                                                Regex("extensionOf=\"(\\w+)\"").find(line)?.let {
                                                    shit.extensionOf = it.groups[1]!!.value
                                                }
                                                state = PAState.REQUIRING_FUNCTION_NAME_AND_FIRST_PARAM
                                            }
                                            else -> Unit
                                        }
                                    }
                                }
                            }

                            PAState.REQUIRING_CLASS_NAME -> {
                                val re = Regex("(?:^|\\s)class (\\w*)\\($")
                                re.find(line)?.let {mr->
                                    shit.className = mr.groups[1]?.let {it.value} ?: wtf("fun re group 1")
                                } ?: wtf("fun re")
                                state = PAState.REQUIRING_PROP_OR_END_OF_CLASS_SIGNATURE
                            }

                            PAState.REQUIRING_PROP_OR_END_OF_CLASS_SIGNATURE -> {
                                when {
                                    line == ")" || line == ") {" -> {
                                        doShit()
                                        state = PAState.WAITING_INITIAL_ANNOTATION
                                    }
                                    else -> {
                                        // var tame: String? = null,
                                        var from = line.trim()
                                        val prop = Prop()

                                        if (!from.startsWith("var ") && !from.startsWith("val ")) wtf("I want var or val here")
                                        from = from.substring("var ".length)
                                        if (from.endsWith(",")) from = from.substring(0, from.length - 1)

                                        val colonIndex = from.indexOf(":")
                                        val eqIndex = from.indexOf("=")
                                        if (eqIndex == -1) wtf("I want default value for this property")
                                        prop.name = from.substring(0, colonIndex)
                                        prop.type = from.substring(colonIndex + 1, eqIndex).trim()
                                        prop.defaultValue = from.substring(eqIndex + 1).trim()

                                        shit.props.add(prop)
                                        // println("Scanned property of $className: $prop")
                                    }
                                }
                            }

                            PAState.REQUIRING_FUNCTION_NAME_AND_FIRST_PARAM -> {
                                val re = Regex("^\\s*(?:operator )?fun (\\w*)\\(")
                                re.find(line)?.let {mr->
                                    shit.functionName = mr.groups[1]?.let {it.value} ?: wtf("fun re group 1")
                                } ?: wtf("fun re")

                                val restOfLine = line.substringAfter("(")
                                parseParam(restOfLine)
                                shit.plainFunctionSignature.appendln(line)

                                state = PAState.REQUIRING_PARAM_OR_END_OF_FUNCTION_SIGNATURE
                            }

                            PAState.REQUIRING_PARAM_OR_END_OF_FUNCTION_SIGNATURE -> {
                                shit.plainFunctionSignature.appendln(line)
                                when {
                                    line.trim().startsWith(")") -> {
                                        doShit()
                                        state = PAState.WAITING_INITIAL_ANNOTATION
                                    }
                                    else -> parseParam(line)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        throw Exception(
                            "\npass=$pass; state=$state; f=${f.path}; lineIndex+1=${lineIndex + 1}" +
                                "\nline=$line" +
                                "\n${e.message}",
                            e)
                    }
                }

                if (state != PAState.WAITING_INITIAL_ANNOTATION) wtf("Bad final state: $state; file=${f.path}")
            }
        }
    }

//    fun renameme1() {
//        val attrsProps = loadProps("$APS_ROOT/front/src/aps/front/Control2.kt", "class Attrs(", ")")
//        val styleProps = loadProps("$APS_ROOT/front/src/aps/front/new-shit.kt", "class Style(", ") {")
//        val newCode = reindent(4, """
//                operator fun invoke(${genParams(attrsProps)}, ${genParams(styleProps)},
//                        block: ((ElementBuilder) -> Unit)? = null): ElementBuilder
//                    = invoke(
//                        Attrs(${genArgs(attrsProps)}),
//                        Style(${genArgs(styleProps)}),
//                        block)
//            """) + "\n"
//
//        val file = File("$APS_ROOT/front/src/aps/front/ElementBuilderFactory.kt")
//        val stamp = LocalDateTime.now().format(PG_LOCAL_DATE_TIME).replace(Regex("[ :\\.]"), "-")
//        backup(file, toDir=GENERATOR_BAK_DIR, suffix=stamp)
//        val currentCode = file.readText()
//
//        val beginMarker = "//---------- BEGIN GENERATED SHIT { ----------"
//        val beginMarkerIndex = currentCode.indexOf(beginMarker)
//        if (beginMarkerIndex == -1) wtf("No beginMarkerIndex in ${file.path}")
//        val endMarker = "    //---------- END GENERATED SHIT } ----------"
//        val endMarkerIndex = currentCode.indexOf(endMarker)
//        if (endMarkerIndex == -1) wtf("No endMarkerIndex in ${file.path}")
//
//        val before = currentCode.substring(0, beginMarkerIndex + beginMarker.length)
//        val after = currentCode.substring(endMarkerIndex)
//        File("$APS_ROOT/front/src/aps/front/ElementBuilderFactory.kt").writeText(
//            before + "\n\n" + newCode + "\n" + after)
//    }

    fun loadProps(path: String, beginSnippet: String, endSnippet: String): List<String> {
        return mutableListOf<String>().applet {res->
            File(path).useLines {lines->
                var readingProps = false; var doneReadingProps = false
                lines.saforEachIndexed {lineIndex, line ->
                    try {
                        if (!readingProps) {
                            if (line.trimEnd().endsWith(beginSnippet)) readingProps = true
                        } else {
                            if (line.trim() == endSnippet) {
                                doneReadingProps = true
                                abort()
                            }
                            Regex("\\s+var ([^,]*),?$").find(line)?.let {
                                res.add(it.groups[1]?.value ?: wtf("group"))
                            } ?: wtf("find")
                        }
                    } catch (e: Exception) {
                        throw Exception("\nLine ${lineIndex + 1} in $path: " + e.message, e)
                    }
                }
                if (!readingProps) wtf("I want [$beginSnippet] in $path")
                if (!doneReadingProps) wtf("I want [$endSnippet] in $path")
            }
        }
    }

    fun genParams(props: Iterable<String>) =
        props.map {
            "\n                    $it"
        }
            .joinToString(",")

    fun genArgs(props: Iterable<String>) =
        props.map {
            val name = it.substring(0, it.indexOf(":"))
            "\n                        $name=$name"
        }
            .joinToString(",")

    fun debugWriteOut(descr: String, origPath: String, toDir: String, content: String,
                      prefix: String? = null, suffix: String? = null) {
        val outPath = (
            toDir + "/" +
                (prefix?.let {it + "--"} ?: "") +
                origPath.substring(APS_ROOT.length)
                    .replace("\\", "/")
                    .replace(Regex("^/"), "")
                    .replace("/", "--") +
                (suffix?.let {"--" + it} ?: "")
            ).replace("----", "--")

        println("$descr: $outPath")
        File(outPath).writeText(content)
    }

    fun backup(file: File, toDir: String, prefix: String? = null, suffix: String? = null) {
        debugWriteOut(descr="Backing up", origPath=file.path, content=file.readText(),
                      toDir=toDir, prefix=prefix, suffix=suffix)
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            GenerateShit()
        }
    }
}

