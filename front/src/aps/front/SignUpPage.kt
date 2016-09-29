/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.ReactElement

//class SignUpPage(val ui: dynamic) {
//
//    fun load(): Promise<Unit> {"__async"
//        val form = FormUI(ui, SignUpForm)
//
//        ui.setPage(json(
//            "header" to jshit.pageHeader(json("title" to t("Sign Up", "Регистрация"))),
//            "body" to jshit.diva(json(),
//                form.toReactElement(),
//
//                jshit.diva(json(),
//                    jshit.hr(),
//                    jshit.diva(json("tame" to "signInSuggestion", "style" to json("textAlign" to "left")),
//                        jshit.spanc(json("tame" to "prose", "content" to t("TOTE", "Уже есть аккаунт? Тогда "))),
//                        ui.urlLink(json("tamy" to true, "title" to t("TOTE", "входим сюда"), "url" to "sign-in.html", "delayActionForFanciness" to true)),
//                        "."
//                    )
//                )
//            )
//        ))
//
//        return __asyncResult(Unit)
//    }
//}

//class FormUI(ui: dynamic, blueprint: FormBlueprint) : Control() {
//    override fun defaultControlTypeName() = "Form"
//
//    val form: dynamic
//
//    init {
//        val jsArrayOfFields = js("[]")
//        for (field in blueprint.fields) {
//            jsArrayOfFields.push(field.toOldFormField(ui))
//        }
//
////        jsArrayOf(
////                ui.TextField(json(
////                    "name" to "email",
////                    "title" to t("Email", "Почта")
////                )),
////                ui.TextField(json(
////                    "name" to "firstName",
////                    "title" to t("First Name", "Имя")
////                )),
////                ui.TextField(json(
////                    "name" to "lastName",
////                    "title" to t("Last Name", "Фамилия")
////                )),
////                ui.AgreeTermsField(json())
////            )
//
//        form = ui.Form(json(
//            "debugName" to "Sign Up",
//            "primaryButtonTitle" to t("Proceed", "Вперед"),
//            "autoFocus" to "email",
//            "fields" to jsArrayOfFields,
//            "rpcFun" to "signUp",
//            "onSuccess" to {res: dynamic -> "__async"
//                ui.signedUpOK = true
//                __await<dynamic>(ui.pushNavigate("sign-in.html"))
//            }
//        ))
//    }
//
//    override fun render(): ReactElement {
//        return form.element
//    }
//
//}














