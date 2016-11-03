/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import into.kommon.*
import into.kotlin.AnalyzeKotlinSources
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import java.io.*
import java.time.LocalDateTime

class GenerateShit {
    val nameToMixableType = mutableMapOf<String, KtClass>()
    val functionsAnnotatedWithGenerateSignatureMixes = mutableListOf<KtNamedFunction>()

    init {
        println("Generating some shit for you...")

        run {
            val interestingShit = mutableListOf<String>()
            fun descend(file: File) {
                if (file.name.contains("generated")) return
                if (file.isDirectory) {
                    file.listFiles().forEach(::descend)
                } else {
                    if (file.extension == "kt") {
                        if (Regex("@MixableType|@GenerateSignatureMixes").containsMatchIn(file.readText())) {
                            interestingShit.add(file.path)
                        }
                    }
                }
            }
            descend(File("$APS_HOME/front/src"))
            val aks = AnalyzeKotlinSources(AnalyzeKotlinSources.TargetPlatform.JS, interestingShit.toTypedArray())

            for (file in aks.environment.getSourceFiles()) {
                file.accept(object : KtTreeVisitorVoid() {
                    override fun visitClass(klass: KtClass) {
                        if (hasAnnotation(klass, "MixableType")) {
                            nameToMixableType[klass.name!!] = klass
                        }
                        super.visitClass(klass)
                    }

                    override fun visitNamedFunction(function: KtNamedFunction) {
                        if (hasAnnotation(function, "GenerateSignatureMixes")) {
                            functionsAnnotatedWithGenerateSignatureMixes.add(function)
                        }
                        super.visitNamedFunction(function)
                    }
                })
            }

            val generated = StringBuilder(dedent("""
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
                import org.w3c.dom.events.*

            """) + "\n")

            fun deleteLastComma() {
                val lastCommaIndex = generated.length - 1 - ",\n".length
                generated.delete(lastCommaIndex, lastCommaIndex + 1)
            }

            for (function in functionsAnnotatedWithGenerateSignatureMixes) {
                val modifiersText = function.modifierList?.text ?: ""
                if (modifiersText.contains(Regex("\\boperator\\b")))
                    generated.append("operator ")

                generated.append("fun ")
                function.containingClassOrObject?.name?.let {generated.append("$it.")}
                generated.appendln("${function.name} (")

                for (vp in function.valueParameters) {
                    when {
                        hasAnnotation(vp, "Mix") -> {
                            val mixableTypeName = vp.typeReference!!.text
                            val mixableType = nameToMixableType[mixableTypeName] ?: bitch("Unknown mixable type: $mixableTypeName")
                            generated.appendln("    // @Mix $mixableTypeName")
                            for (p in mixableType.getPrimaryConstructorParameters()) {
                                generated.appendln("    " + p.text.substring("val ".length) + ",")
                            }
                        }
                        else -> {
                            generated.appendln("    ${vp.text},")
                        }
                    }
                }

                deleteLastComma()
                val returnType = function.typeReference?.text ?: "Unit"
                generated.appendln("): $returnType = ${function.name}(")

                for (vp in function.valueParameters) {
                    when {
                        hasAnnotation(vp, "Mix") -> {
                            val mixableTypeName = vp.typeReference!!.text
                            val mixableType = nameToMixableType[mixableTypeName] ?: bitch("Unknown mixable type: $mixableTypeName")
                            generated.appendln("    ${vp.name} = $mixableTypeName(")
                            for (p in mixableType.getPrimaryConstructorParameters()) {
                                generated.appendln("        ${p.name} = ${p.name},")
                            }
                            deleteLastComma()
                            generated.appendln("    ),")
                        }
                        else -> {
                            generated.appendln("    ${vp.name} = ${vp.name},")
                        }
                    }
                }

                deleteLastComma()
                generated.appendln(")\n")
            }

            // println(generated)

            val outFile = File("$APS_HOME/front/src/aps/front/generated-shit.kt")
            if (outFile.exists()) backup(outFile)
            println("Writing: ${outFile.name}")
            outFile.writeText("" + generated)

            println("COOL")
        }
    }

    fun hasAnnotation(container: KtModifierListOwner, typeText: String): Boolean =
        container.annotationEntries.any {it.typeReference?.text == typeText}

    fun backup(file: File) {
        val stamp = LocalDateTime.now().format(PG_LOCAL_DATE_TIME).replace(Regex("[ :\\.]"), "-")
        val outPath = (
            GENERATOR_BAK_DIR + "/" +
                file.path.substring(APS_HOME.length)
                    .replace("\\", "/")
                    .replace(Regex("^/"), "")
                    .replace("/", "--") +
                stamp
            ).replace("----", "--")

        println("Backing up: $outPath")
        File(outPath).writeText(file.readText())

    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            GenerateShit()
        }
    }
}

