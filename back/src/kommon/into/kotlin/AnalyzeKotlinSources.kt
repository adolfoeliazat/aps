///*
// * Into Kommon
// *
// * (C) Copyright 2016 Vladimir Grechka
// */
//
//package kommon.into.kotlin
//
//import into.kommon.*
//import org.jetbrains.kotlin.analyzer.AnalysisResult
//import kommon.org.jetbrains.kotlin.cli.js.IntoK2JSCompiler
//import org.jetbrains.kotlin.cli.jvm.IntoK2JVMCompiler
//import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
//
//class AnalyzeKotlinSources(val targetPlatform: TargetPlatform, val sourceRoots: Array<String>) {
//    enum class TargetPlatform {JVM, JS}
//
//    companion object {
//        lateinit var current: AnalyzeKotlinSources
//    }
//
//    lateinit var environment: KotlinCoreEnvironment
//    lateinit var analysisResult: AnalysisResult
//
//    init {
//        current = this
//
//        println("Running Kotlin to $targetPlatform compiler over your shit:")
//        sourceRoots.forEach {println("    - $it")}
//        measuringAndPrinting {
//            when (targetPlatform) {
//                TargetPlatform.JVM -> IntoK2JVMCompiler.main(sourceRoots)
//                TargetPlatform.JS -> IntoK2JSCompiler.main(*arrayOf(
//                    *sourceRoots,
//                    // IntoK2JSCompiler doesn't write out anything, but K2JSCompiler wants this argument
//                    "-output", "c:/tmp/into-kotlin/out.js"))
//            }
//        }
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//

