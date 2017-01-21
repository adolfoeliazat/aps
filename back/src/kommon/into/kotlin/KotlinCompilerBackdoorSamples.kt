///*
// * Into Kotlin
// *
// * (C) Copyright 2016 Vladimir Grechka
// */
//
//package kommon.into.kotlin
//
//import org.jetbrains.kotlin.analyzer.AnalysisResult
//import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
//import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
//import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
//import org.jetbrains.kotlin.js.analyzer.JsAnalysisResult
//import org.jetbrains.kotlin.psi.*
//import org.jetbrains.kotlin.psi.psiUtil.parents
//import org.jetbrains.kotlin.resolve.BindingContext
//import org.jetbrains.kotlin.resolve.bindingContextUtil.get
//import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
//import org.jetbrains.kotlin.types.KotlinType
//import org.jetbrains.kotlin.types.TypeConstructor
//import org.jetbrains.kotlin.utils.sure
//
//object KotlinCompilerBackdoorSamples {
//    data class SimpleParam(val name: String, val type: String, val defaultValue: String?)
//
//    private fun sample2(bc: BindingContext) {
//        for ((annotationEntry, annotationDescriptor) in bc.getSliceContents(BindingContext.ANNOTATION)) {
//            annotationDescriptor.type.constructor.declarationDescriptor?.let {decl->
//                if (decl.fqNameSafe.asString() == "into.kotlin.test.shit2analyze.MixableType") {
//                    val aeParents = annotationEntry.parents.toList()
//                    // clog("aeParents: " + aeParents.joinToString())
//                    if (aeParents.size >= 2) {
//                        val klass = aeParents[1]
//                        if (aeParents[0] is KtDeclarationModifierList && klass is KtClass) {
//                            clog("Found mixable class: " + klass.fqName)
//                            val ctorParams = klass.getPrimaryConstructorParameters()
//                            // clog("ctorParams: " + ctorParams.joinToString())
//                            val simpleCtorParams = ctorParams.map {
//                                SimpleParam(
//                                name = it.name!!,
//                                type = it.typeReference!!.text,
//                                defaultValue = it.defaultValue?.text
//                            )
//                            }
//                            clog("simpleCtorParams: $simpleCtorParams")
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private fun sampleLookThroughBindingContext(bc: BindingContext) {
//        for ((annotationEntry, annotationDescriptor) in bc.getSliceContents(BindingContext.ANNOTATION)) {
//            annotationDescriptor.type.constructor.declarationDescriptor?.let {decl->
//                if (decl.fqNameSafe.asString() == "into.kotlin.test.shit2analyze.MixableType") {
//                    val aeParents = annotationEntry.parents.toList()
//                    // clog("aeParents: " + aeParents.joinToString())
//                    if (aeParents.size >= 2) {
//                        val klass = aeParents[1]
//                        if (aeParents[0] is KtDeclarationModifierList && klass is KtClass) {
//                            clog("Found mixable class: " + klass.fqName)
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private fun sampleLookThroughFiles(environment: KotlinCoreEnvironment, result: AnalysisResult) {
//        for (file in environment.getSourceFiles()) {
//            file.accept(object : KtTreeVisitorVoid() {
//                override fun visitClass(klass: KtClass) {
//                    // println("Found fucking class: " + klass.name)
//                    for (annotationEntry in klass.annotationEntries) {
//                        val ad: AnnotationDescriptor? = result.bindingContext.get(BindingContext.ANNOTATION, annotationEntry)
//                        if (ad != null) {
//                            val type: KotlinType = ad.type
//                            val ctor: TypeConstructor = type.constructor
//                            val decl: ClassifierDescriptor? = ctor.declarationDescriptor
//                            if (decl != null) {
//                                // clog("fqName: " + decl.fqNameSafe)
//                                if (decl.fqNameSafe.asString() == "into.kotlin.test.shit2analyze.MixableType") {
//                                    clog("Found mixable class: " + klass.fqName)
//                                }
//                            }
//                        }
//                    }
//
//                    super.visitClass(klass)
//                }
//            })
//        }
//    }
//
//    fun sample1(annotationEntry: KtAnnotationEntry) {
//        val typeReference = annotationEntry.typeReference!!
//        val typeElement = typeReference.typeElement
//        if (typeElement is KtUserType) {
//            clog(typeElement.referencedName)
//        }
//    }
//
//    fun clog(msg: Any?) {
//        println(msg)
//    }
//}

