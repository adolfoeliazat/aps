///*
// * APS
// *
// * (C) Copyright 2015-2016 Vladimir Grechka
// */
//
//package aps.back
//
//import aps.*
//import com.intellij.navigation.NavigationItem
//import into.kommon.*
//import kommon.into.kotlin.AnalyzeKotlinSources
//import org.jetbrains.kotlin.psi.*
//import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
//import org.jetbrains.kotlin.psi.psiUtil.getValueParameters
//import java.io.*
//import java.time.LocalDateTime
//
//class GenerateShit {
//    val nameToMixableType = mutableMapOf<String, KtClass>()
//    val shitAnnotatedWithGenerateSignatureMixes = mutableListOf<NavigationItem>()
//
//    init {
//        println("Generating some shit for you...")
//
//        run {
//            val interestingShit = mutableListOf<String>()
//            fun descend(file: File) {
//                if (file.name.contains("generated")) return
//                if (file.isDirectory) {
//                    file.listFiles().forEach(::descend)
//                } else {
//                    if (file.extension == "kt") {
//                        if (Regex("@MixableType|@GenerateSignatureMixes").containsMatchIn(file.readText())) {
//                            interestingShit.add(file.path)
//                        }
//                    }
//                }
//            }
//            descend(File("$APS_HOME/front/src"))
//            val aks = AnalyzeKotlinSources(AnalyzeKotlinSources.TargetPlatform.JS, interestingShit.toTypedArray())
//
//            for (file in aks.environment.getSourceFiles()) {
//                file.accept(object : KtTreeVisitorVoid() {
//                    override fun visitClass(klass: KtClass) {
//                        if (hasAnnotation(klass, "MixableType")) {
//                            nameToMixableType[klass.name!!] = klass
//                        }
//                        super.visitClass(klass)
//                    }
//
//                    override fun visitNamedFunction(function: KtNamedFunction) {
//                        if (hasAnnotation(function, "GenerateSignatureMixes")) {
//                            shitAnnotatedWithGenerateSignatureMixes.add(function)
//                        }
//                        super.visitNamedFunction(function)
//                    }
//
//                    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
//                        if (hasAnnotation(classOrObject, "GenerateSignatureMixes")) {
//                            if (classOrObject is KtClass) {
//                                shitAnnotatedWithGenerateSignatureMixes.add(classOrObject)
//                            }
//                        }
//                        super.visitClassOrObject(classOrObject)
//                    }
//                })
//            }
//
//            val generated = StringBuilder(dedent("""
//                /*
//                 * APS
//                 *
//                 * (C) Copyright 2015-2016 Vladimir Grechka
//                 *
//                 * SHIT IN THIS FILE IS GENERATED
//                 */
//
//                package aps.front
//
//                import aps.*
//                import aps.front.*
//                import org.w3c.dom.events.*
//
//            """) + "\n")
//
//            fun deleteLastComma() {
//                val lastCommaIndex = generated.length - 1 - ",\n".length
//                generated.delete(lastCommaIndex, lastCommaIndex + 1)
//            }
//
//            for (piece in shitAnnotatedWithGenerateSignatureMixes) {
//                println("Generating signature mixes for ${piece.name}")
//
//                if (piece is KtNamedFunction) {
//                    val modifiersText = piece.modifierList?.text ?: ""
//                    if (modifiersText.contains(Regex("\\boperator\\b")))
//                        generated.append("operator ")
//                }
//
//                generated.append("fun ")
//                if (piece is KtNamedFunction) {
//                    piece.containingClassOrObject?.name?.let {generated.append("$it.")}
//                }
//                generated.appendln("${piece.name} (")
//
//                val params = when (piece) {
//                    is KtNamedFunction -> piece.valueParameters
//                    is KtClass -> piece.getValueParameters()
//                    else -> wtf("I want params")
//                }
//
//                for (param in params) {
//                    when {
//                        hasAnnotation(param, "Mix") -> {
//                            val mixableTypeName = param.typeReference!!.text
//                            val mixableType = nameToMixableType[mixableTypeName] ?: bitch("Unknown mixable type: $mixableTypeName")
//                            generated.appendln("    // @Mix $mixableTypeName")
//                            generated.appendln("    base$mixableTypeName: $mixableTypeName? = null,")
//                            for (mixedParam in mixableType.getPrimaryConstructorParameters()) {
//                                generated.appendln("    " + unValVar(mixedParam.text) + ",")
//                            }
//                        }
//                        else -> {
//                            generated.appendln("    " + unValVar(param.text) + ",")
//                        }
//                    }
//                }
//
//                deleteLastComma()
////                    val returnType = function.typeReference?.text ?: "Unit"
//                generated.appendln(") = ${piece.name}(")
////                    generated.appendln("): $returnType = ${function.name}(")
//
//                for (param in params) {
//                    when {
//                        hasAnnotation(param, "Mix") -> {
//                            val mixableTypeName = param.typeReference!!.text
//                            val mixableType = nameToMixableType[mixableTypeName] ?: bitch("Unknown mixable type: $mixableTypeName")
//                            generated.appendln("    ${param.name} = $mixableTypeName(")
//                            for (mixedParam in mixableType.getPrimaryConstructorParameters()) {
//                                generated.appendln("        ${mixedParam.name} = ${mixedParam.name} ?: base$mixableTypeName?.${mixedParam.name},")
////                                generated.appendln("        ${mixedParam.name} = ${mixedParam.name},")
//                            }
//                            deleteLastComma()
//                            generated.appendln("    ),")
//                        }
//                        else -> {
//                            generated.appendln("    ${param.name} = ${param.name},")
//                        }
//                    }
//                }
//
//                deleteLastComma()
//                generated.appendln(")\n")
//            }
//
//            // println(generated)
//
//            val outFile = File("$APS_HOME/front/src/aps/front/generated-shit.kt")
//            if (outFile.exists()) backup(outFile)
//            println("Writing: ${outFile.name}")
//            outFile.writeText("" + generated)
//
//            println("COOL")
//        }
//    }
//
//    fun hasAnnotation(container: KtModifierListOwner, typeText: String): Boolean =
//        container.annotationEntries.any {it.typeReference?.text == typeText}
//
//    fun backup(file: File) {
//        val stamp = LocalDateTime.now().format(PG_LOCAL_DATE_TIME).replace(Regex("[ :\\.]"), "-")
//        val outPath = (
//            GENERATOR_BAK_DIR + "/" +
//                file.path.substring(APS_HOME.length)
//                    .replace("\\", "/")
//                    .replace(Regex("^/"), "")
//                    .replace("/", "--") +
//                stamp
//            ).replace("----", "--")
//
//        println("Backing up: $outPath")
//        File(outPath).writeText(file.readText())
//
//    }
//
//    fun unValVar(s: String): String =
//        if (s.startsWith("val ") || s.startsWith("var "))
//            s.substring("val ".length)
//        else s
//
//
//    companion object {
//        @JvmStatic fun main(args: Array<String>) {
//            GenerateShit()
//        }
//    }
//}

