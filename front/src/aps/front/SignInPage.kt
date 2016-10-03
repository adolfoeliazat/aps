package aps.front

import aps.*

class FormMatumba<Req: RequestMatumba, Res>(val req: Req, val dummyRes: Res, ui: LegacyUIShit, build: FormMatumba<Req, Res>.() -> Unit) {
    var className = ""
    var errorBannerStyle: dynamic = js("undefined")
    var primaryButtonTitle = t("Send", "Заслать")
    var cancelButtonTitle: String? = null
    var dontShameButtons: Boolean = false
    var onCancel: () -> Unit = {}
    var onCancela: () -> Promise<Unit> = {"__async"; __asyncResult(Unit)}
    var onError: (FormResponse.Shitty) -> Unit = {}
    var onErrora: (FormResponse.Shitty) -> Promise<Unit> = {"__async"; __asyncResult(Unit)}
    var onSuccess: (Res) -> Unit = {}
    var onSuccessa: (Res) -> Promise<Unit> = {"__async"; __asyncResult(Unit)}

    init {
        build()
    }

//    val fields = mutableListOf<FormField>()
    var error: String? = null
    var working: Boolean = false
    lateinit var actualVisibleFieldNames: Iterable<String>

    val control = object:Control() {
        override fun defaultControlTypeName() = "FormMatumba"

        override fun render(): ReactElement {
            figureOutActualVisibleFieldNames()

            fun formTicker(): dynamic {
                return jshit.elcl(json(
                    "render" to {
                        jshit.diva(json("className" to "progressTicker", "style" to json("float" to "right", "width" to 14, "height" to 28, "backgroundColor" to jshit.BLUE_GRAY_600)))
                    },
                    "componentDidMount" to {
                        global.testGlobal["shitSpins"] = true
                    },
                    "componentWillUnmount" to {
                        global.testGlobal["shitSpins"] = false
                    }
                ))
            }

            return jshit.diva(json(),
                jshit.forma.apply(null, js("[]").concat(
                    jsArrayOf(
                        json("className" to className),
                        if (error != null) jshit.errorBanner(json("content" to error, "style" to errorBannerStyle)) else undefined),

                    req.fields
                        .filter{x -> actualVisibleFieldNames.contains(x.name)}
                        .map{x -> x.render()}
                        .toJSArray(),

                    jshit.diva(json("style" to json("textAlign" to "left")),
                        jshit.button(json("tamy" to "primary", "shamy" to if (dontShameButtons) undefined else "primary",
                            "level" to "primary", "title" to primaryButtonTitle, "disabled" to working,
                            "onClick" to {"__async"
                                jshit.beginTrain(json("name" to "Submit fucking form")); try {
                                    for (field: FormFieldFront in req.fields) {
                                        field.error = null
                                        field.disabled = true
                                    }
                                    error = null
                                    working = true
                                    update()

                                    val res: FormResponse = __await(callRemoteProcedurePassingJSONObject(remoteProcedureNameForRequest(req), dyna{ r->
                                        r.clientKind = global.CLIENT_KIND
                                        r.lang = global.LANG
                                        ui.token?.let {r.token = it}

                                        r.fields = dyna{o->
                                            for (field in req.fields)
                                                o[field.name] = field.value
                                        }
                                    }))

                                    when (res) {
                                        is FormResponse.Shitty -> {
                                            error = res.error
                                            onError(res)
                                            __await(onErrora(res))
                                        }
                                        is FormResponse.Hunky<*> -> {
                                            error = null
                                            val meat = res.meat as Res
                                            onSuccess(meat)
                                            __await(onSuccessa(meat))
                                        }
                                    }

                                    for (field in req.fields) {
                                        field.error = if (res !is FormResponse.Shitty) null else
                                            res.fieldErrors.find{it.field == field.name}?.error
                                        field.disabled = false
                                    }

                                    working = false
                                    update()
                                } finally { jshit.endTrain() }
                            }
                        )),

                        if (cancelButtonTitle != null)
                            jshit.button(json("tamy" to "cancel", "shamy" to if (dontShameButtons) undefined else "cancel",
                                "title" to cancelButtonTitle, "disabled" to working, "style" to json("marginLeft" to 10),
                                "onClick" to {"__async"
                                    onCancel()
                                    __await(onCancela())
                                })) else undefined,

                        working && formTicker()
                    )
                )))
        }
    }

    fun figureOutActualVisibleFieldNames() {
        actualVisibleFieldNames = req.fields.map{x -> x.name}
//        if (getInvisibleFieldNames) {
//            actualVisibleFieldNames = jshit.utils.without.apply(null, js("[]").concat(jsArrayOf(actualVisibleFieldNames), getInvisibleFieldNames()))
//        }
    }

    fun toReactElement(): ReactElement {
        return control.toReactElement()
    }
}

fun jsFacing_loadSignInPageCtor(ui: LegacyUIShit): dynamic {
    fun loadSignInPage() {

        ui.setPage(json(
            "header" to jshit.pageHeader(json("title" to t("Sign In", "Вход"))),
            "body" to jshit.diva(json(),
                ui.signedUpOK && jshit.preludeWithGreenCheck(json("title" to t(
                    "Cool. You have an account now. We sent you email with password.",
                    "Все круто. Теперь у тебя есть аккаунт. Пароль мы отправили письмом."))),

                FormMatumba(SignInWithPasswordRequest(), SignInWithPasswordRequest.Response(), ui=ui) {
                    primaryButtonTitle = t("Sign In", "Войти")

                    onError = {
                        req.password.value = ""
                    }

                    onSuccessa = {res -> "__async"
                        ui.user = res.user
                        ui.startLiveStatusPolling()
                        ui.token = res.token
                        jshit.storageLocal.setItem("token", ui.token)

                        __await(ui.pushNavigate(when (res.user.state) {
                            UserState.COOL -> "dashboard.html"
                            else -> "profile.html"
                        })) /ignora
                    }
                }.toReactElement(),

                !ui.signedUpOK && jshit.diva(json(),
                    jshit.hr(),
                    jshit.diva(json("style" to json("textAlign" to "left")),
                        t("TOTE", "Как? Еще нет аккаунта? "),
                        ui.urlLink(json("tamyShamy" to "createAccount", "title" to t("TOTE", "Срочно создать!"), "url" to "sign-up.html", "delayActionForFanciness" to true))))
            )
        ))
    }

    return ::loadSignInPage
}







//fun bak_jsFacing_loadSignInPageCtor(ui: dynamic): dynamic {
//    fun loadSignInPage() {
//        val form = object:CoolForm<SignInWithPasswordResponse>("signInWithPassword", primaryButtonTitle = t("Sign In", "Войти")) {
//            val email = TextField(this, "email", t("TOTE", "Почта"))
//            val password = TextField(this, "password", t("TOTE", "Пароль"), type = TextField.Type.PASSWORD)
//
//            override fun onError(res: FormResponse.Shitty) {
//                password.value = ""
//            }
//
//            override fun onSuccessa(res: SignInWithPasswordResponse): Promise<Unit> {"__async"
//                ui.user = res.user
//                ui.startLiveStatusPolling()
//                ui.token = res.token
//                jshit.storageLocal.setItem("token", ui.token)
//                if (res.user.state == UserState.COOL) {
//                    return __await<dynamic>(ui.pushNavigate("dashboard.html"))
//                } else {
//                    return __await<dynamic>(ui.pushNavigate("profile.html"))
//                }
//            }
//        }
//        form.ui = ui
//
//
//        ui.setPage(json(
//            "header" to jshit.pageHeader(json("title" to t("Sign In", "Вход"))),
//            "body" to jshit.diva(json(),
//                ui.signedUpOK && jshit.preludeWithGreenCheck(json("title" to t(
//                    "Cool. You have an account now. We sent you email with password.",
//                    "Все круто. Теперь у тебя есть аккаунт. Пароль мы отправили письмом."))),
//
//                form.toReactElement(),
//
//                !ui.signedUpOK && jshit.diva(json(),
//                    jshit.hr(),
//                    jshit.diva(json("style" to json("textAlign" to "left")),
//                        t("TOTE", "Как? Еще нет аккаунта? "),
//                        ui.urlLink(json("tamyShamy" to "createAccount", "title" to t("TOTE", "Срочно создать!"), "url" to "sign-up.html", "delayActionForFanciness" to true))))
//            )
//        ))
//    }
//
//    return ::loadSignInPage
//}
