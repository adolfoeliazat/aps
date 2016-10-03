/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*


//open class CoolForm<Res>(
//    val remoteProcedureName: String,
//    val primaryButtonTitle: String = t("Submit", "Заслать"),
//    val cancelButtonTitle: String? = null,
//    val className: String = "",
//    val errorBannerStyle: dynamic = undefined,
//    dontShameButtons: Boolean = false,
//    debugName: String? = null) {
//
//    val fields = mutableListOf<FormField>()
//    lateinit var ui: LegacyUIShit
//    var working: Boolean = false
//    var error: String? = null
//    var focusedField: String? = null
//    lateinit var actualVisibleFieldNames: Iterable<String>
//
//    open fun onError(res: FormResponse.Shitty): Unit {}
//    open fun onErrora(res: FormResponse.Shitty): Promise<Unit> = __asyncResult(Unit)
//    open fun onSuccess(res: Res): Unit {}
//    open fun onSuccessa(res: Res): Promise<Unit> = __asyncResult(Unit)
//    open fun onCancel(): Unit {}
//    open fun onCancela(): Promise<Unit> = __asyncResult(Unit)
//
//    fun figureOutActualVisibleFieldNames() {
//        actualVisibleFieldNames = fields.map{x -> x.name}
////        if (getInvisibleFieldNames) {
////            actualVisibleFieldNames = jshit.utils.without.apply(null, js("[]").concat(jsArrayOf(actualVisibleFieldNames), getInvisibleFieldNames()))
////        }
//    }
//
//    val control = object:Control() {
//        override fun defaultControlTypeName() = "CoolForm"
//
//        override fun render(): ReactElement {
//            figureOutActualVisibleFieldNames()
//
//            fun formTicker(): dynamic {
//                return jshit.elcl(json(
//                    "render" to {
//                        jshit.diva(json("className" to "progressTicker", "style" to json("float" to "right", "width" to 14, "height" to 28, "backgroundColor" to jshit.BLUE_GRAY_600)))
//                    },
//                    "componentDidMount" to {
//                        global.testGlobal["shitSpins"] = true
//                    },
//                    "componentWillUnmount" to {
//                        global.testGlobal["shitSpins"] = false
//                    }
//                ))
//            }
//
//            return jshit.diva(json(),
//                jshit.forma.apply(null, js("[]").concat(
//                    jsArrayOf(
//                    json("className" to className),
//                    if (error != null) jshit.errorBanner(json("content" to error, "style" to errorBannerStyle)) else undefined),
//
//                    fields
//                        .filter{x -> actualVisibleFieldNames.contains(x.name)}
//                        .map{x -> x.render()}
//                        .toJSArray(),
//
//                    jshit.diva(json("style" to json("textAlign" to "left")),
//                        jshit.button(json("tamy" to "primary", "shamy" to if (dontShameButtons) undefined else "primary",
//                            "level" to "primary", "title" to primaryButtonTitle, "disabled" to working,
//                            "onClick" to {"__async"
//                                jshit.beginTrain(json("name" to "Submit fucking${if (debugName != null) " " + debugName else ""} form")); try {
//                                    for (field in fields) {
//                                        field.error = null
//                                        field.disabled = true
//                                    }
//                                    error = null
//                                    working = true
//                                    update()
//
//                                    // TODO:vgrechka Use Request class here instead of generic map
//                                    val req: dynamic = json(
//                                        "clientKind" to global.CLIENT_KIND,
//                                        "lang" to global.LANG
//                                    )
//                                    req.fields = js("({})")
//                                    for (field in fields) {
//                                        req.fields[field.name] = field.value
//                                    }
//                                    ui.token?.let {req.token = it}
//                                    val res: FormResponse = __await(callRemoteProcedure(remoteProcedureName, req))
//
//                                    when (res) {
//                                        is FormResponse.Shitty -> {
//                                            error = res.error
//                                            onError(res)
//                                            __await(onErrora(res))
//                                        }
//                                        is FormResponse.Hunky<*> -> {
//                                            error = null
//                                            val meat = res.meat as Res
//                                            onSuccess(meat)
//                                            __await(onSuccessa(meat))
//                                        }
//                                    }
//
//                                    for (field in fields) {
//                                        field.error = if (res !is FormResponse.Shitty) null else
//                                            res.fieldErrors.find{it.field == field.name}?.error
//                                        field.disabled = false
//                                    }
//
//                                    working = false
//                                    update()
//                                } finally { jshit.endTrain() }
//                            }
//                        )),
//
//                        if (cancelButtonTitle != null)
//                            jshit.button(json("tamy" to "cancel", "shamy" to if (dontShameButtons) undefined else "cancel",
//                                "title" to cancelButtonTitle, "disabled" to working, "style" to json("marginLeft" to 10),
//                                "onClick" to {"__async"
//                                    onCancel()
//                                    __await(onCancela())
//                                })) else undefined,
//
//                        working && formTicker()
//                    )
//                    )))
//        }
//    }
//
//    fun toReactElement(): dynamic {
//        return control.toReactElement()
//    }
//}





//fun orig_jsFacing_loadSignInPageCtor(ui: dynamic): dynamic {
//    fun loadSignInPage() {
//        var form: dynamic = undefined
//        form = ui.Form(json(
//            "debugName" to "Sign In",
//            "primaryButtonTitle" to t("Sign In", "Войти"),
//            "autoFocus" to "email",
//            "fields" to jsArrayOf(
//                ui.TextField(json(
//                    "name" to "email",
//                    "title" to t("Email", "Почта")
//                )),
//                ui.TextField(json(
//                    "name" to "password",
//                    "type" to "password",
//                    "title" to t("Password", "Пароль")
//                ))
//            ),
//            "rpcFun" to "signInWithPassword",
//
//            "onError" to {res: dynamic ->
//                form.getField("password").setValue("")
//            },
//
//            "onSuccess" to {res: dynamic -> "__async"
//                ui.user = res.user
//                ui.startLiveStatusPolling()
//                ui.token = res.token
//                jshit.storageLocal.setItem("token", ui.token)
//                if (ui.user.state == "COOL") {
//                    __await<dynamic>(ui.pushNavigate("dashboard.html"))
//                } else {
//                    __await<dynamic>(ui.pushNavigate("profile.html"))
//                }
//            }
//        ))
//
//        ui.setPage(json(
//            "header" to jshit.pageHeader(json("title" to t("Sign In", "Вход"))),
//            "body" to jshit.diva(json(),
//                ui.signedUpOK && jshit.preludeWithGreenCheck(json("title" to t(
//                    "Cool. You have an account now. We sent you email with password.",
//                    "Все круто. Теперь у тебя есть аккаунт. Пароль мы отправили письмом."))),
//
//                form,
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


fun jsFacing_makeFormCtor(ui: dynamic): dynamic {
    fun jsFacing_Form(spec: dynamic): dynamic {
        val primaryButtonTitle = spec.primaryButtonTitle; val cancelButtonTitle = spec.cancelButtonTitle; val autoFocus = spec.autoFocus;
        val fields = spec.fields; val rpcFun = spec.rpcFun; val onSuccess = spec.onSuccess; val onError = spec.onError;
        val className = spec.className; val dontShameButtons = spec.dontShameButtons; val errorBannerStyle = spec.errorBannerStyle;
        val debugName = spec.debugName; val getInvisibleFieldNames = spec.getInvisibleFieldNames; val onCancel = spec.onCancel;

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

        return jshit.statefulElement(json("ctor" to statefulElementCtor@{update: dynamic ->
            var working: dynamic = undefined
            var error: dynamic = undefined
            var focusedField: dynamic = undefined
            var actualVisibleFieldNames: dynamic = undefined

            fun figureOutActualVisibleFieldNames() {
                actualVisibleFieldNames = fields.map({x -> x.getName()})
                if (getInvisibleFieldNames) {
                    actualVisibleFieldNames = jshit.utils.without.apply(null, js("[]").concat(jsArrayOf(actualVisibleFieldNames), getInvisibleFieldNames()))
                }
            }

            val me = json(
                "getField" to outta@{name: dynamic ->
                    for (field in jsArrayToList(fields)) {
                        if (field.getName() == name) return@outta field
                    }
                    jshit.utils.raise("No fucking field [${name}] in the form")
                },

                "fieldChanged" to {
                    // @wip rejection
                    if (getInvisibleFieldNames) {
                        val oldVisible = jshit.utils.clone(actualVisibleFieldNames)
                        figureOutActualVisibleFieldNames()
                        if (!jshit.utils.isEqual(oldVisible, actualVisibleFieldNames)) {
                            update()
                        }
                    }
                },

                "fieldFocused" to {arg: dynamic ->
                    val field = arg.field
                    focusedField = field
                },

                "fieldBlurred" to {arg: dynamic ->
                    val field = arg.field
                    focusedField = undefined
                },

                "render" to outta@{
                    figureOutActualVisibleFieldNames()

                    return@outta jshit.diva(json(),
                        jshit.forma.apply(null, js("[]").concat(
                            jsArrayOf(
                                json("className" to className),
                                error && jshit.errorBanner(json("content" to error, "style" to errorBannerStyle))),
                            fields
                                .filter({x: dynamic -> actualVisibleFieldNames.includes(x.getName())})
                                .map({x: dynamic -> x.render(json())}),

                            jshit.diva(json("style" to json("textAlign" to "left")),
                                jshit.button(json("tamy" to "primary", "shamy" to if (dontShameButtons) undefined else "primary",
                                    "level" to "primary", "title" to primaryButtonTitle, "disabled" to working,
                                    "onClick" to {"__async"
                                        jshit.beginTrain(json("name" to "Submit fucking${if (debugName) " " + debugName else ""} form")); try {
                                            for (field in jsArrayToList(fields)) {
                                                field.setError(undefined)
                                                field.setDisabled(true)
                                            }
                                            error = undefined
                                            working = true
                                            update()

                                            val reb = jshit.RequestBuilder(json())
                                            reb.set(json("key" to "fun", "value" to rpcFun))
                                            for (field in jsArrayToList(fields.filter({ x: dynamic -> actualVisibleFieldNames.includes(x.getName())}))) {
                                                field.contributeToRequest(json("reb" to reb))
                                            }
                                            val res = __await<dynamic>(ui.rpcSoft(reb.toMessage(json())))

                                            if (res.error) {
                                                error = res.error
                                                __await<dynamic>(jshit.utils.fova(onError, res))
                                            } else {
                                                error = undefined
                                                __await<dynamic>(onSuccess(res))
                                            }

                                            working = false
                                            for (field in jsArrayToList(fields)) {
                                                field.setError(res.fieldErrors && res.fieldErrors[field.getName()])
                                                field.setDisabled(false)
                                            }
                                            update()
                                        } finally { jshit.endTrain() }
                                    })),
                                cancelButtonTitle && jshit.button(json("tamy" to "cancel", "shamy" to if (dontShameButtons) undefined else "cancel",
                                    "title" to cancelButtonTitle, "disabled" to working, "style" to json("marginLeft" to 10), "onClick" to onCancel)),
                                working && formTicker())
                        )))
                },

                "componentDidMount" to {
                    if (focusedField) {
                        focusedField.focus()
                    }
                },

                "componentDidUpdate" to {
                    if (focusedField) {
                        focusedField.focus()
                    }
                }
            )

            for (field in jsArrayToList(fields)) {
                field.form = me
            }

            return@statefulElementCtor me
        }))
    }

    return ::jsFacing_Form
}

fun orig_jsFacing_makeFormCtor(ui: dynamic): dynamic {
    fun jsFacing_Form(spec: dynamic): dynamic {
        val primaryButtonTitle = spec.primaryButtonTitle; val cancelButtonTitle = spec.cancelButtonTitle; val autoFocus = spec.autoFocus;
        val fields = spec.fields; val rpcFun = spec.rpcFun; val onSuccess = spec.onSuccess; val onError = spec.onError;
        val className = spec.className; val dontShameButtons = spec.dontShameButtons; val errorBannerStyle = spec.errorBannerStyle;
        val debugName = spec.debugName; val getInvisibleFieldNames = spec.getInvisibleFieldNames; val onCancel = spec.onCancel;

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

        return jshit.statefulElement(json("ctor" to statefulElementCtor@{update: dynamic ->
            var working: dynamic = undefined
            var error: dynamic = undefined
            var focusedField: dynamic = undefined
            var actualVisibleFieldNames: dynamic = undefined

            fun figureOutActualVisibleFieldNames() {
                actualVisibleFieldNames = fields.map({x -> x.getName()})
                if (getInvisibleFieldNames) {
                    actualVisibleFieldNames = jshit.utils.without.apply(null, js("[]").concat(jsArrayOf(actualVisibleFieldNames), getInvisibleFieldNames()))
                }
            }

            val me = json(
                "getField" to outta@{name: dynamic ->
                    for (field in jsArrayToList(fields)) {
                        if (field.getName() == name) return@outta field
                    }
                    jshit.utils.raise("No fucking field [${name}] in the form")
                },

                "fieldChanged" to {
                    // @wip rejection
                    if (getInvisibleFieldNames) {
                        val oldVisible = jshit.utils.clone(actualVisibleFieldNames)
                        figureOutActualVisibleFieldNames()
                        if (!jshit.utils.isEqual(oldVisible, actualVisibleFieldNames)) {
                            update()
                        }
                    }
                },

                "fieldFocused" to {arg: dynamic ->
                    val field = arg.field
                    focusedField = field
                },

                "fieldBlurred" to {arg: dynamic ->
                    val field = arg.field
                    focusedField = undefined
                },

                "render" to outta@{
                    figureOutActualVisibleFieldNames()

                    return@outta jshit.diva(json(),
                        jshit.forma.apply(null, js("[]").concat(
                            jsArrayOf(
                                json("className" to className),
                                error && jshit.errorBanner(json("content" to error, "style" to errorBannerStyle))),
                            fields
                                .filter({x: dynamic -> actualVisibleFieldNames.includes(x.getName())})
                                .map({x: dynamic -> x.render(json())}),

                            jshit.diva(json("style" to json("textAlign" to "left")),
                                jshit.button(json("tamy" to "primary", "shamy" to if (dontShameButtons) undefined else "primary",
                                    "level" to "primary", "title" to primaryButtonTitle, "disabled" to working,
                                    "onClick" to {"__async"
                                        jshit.beginTrain(json("name" to "Submit fucking${if (debugName) " " + debugName else ""} form")); try {
                                            for (field in jsArrayToList(fields)) {
                                                field.setError(undefined)
                                                field.setDisabled(true)
                                            }
                                            error = undefined
                                            working = true
                                            update()

                                            val reb = jshit.RequestBuilder(json())
                                            reb.set(json("key" to "fun", "value" to rpcFun))
                                            for (field in jsArrayToList(fields.filter({ x: dynamic -> actualVisibleFieldNames.includes(x.getName())}))) {
                                                field.contributeToRequest(json("reb" to reb))
                                            }
                                            val res = __await<dynamic>(ui.rpcSoft(reb.toMessage(json())))

                                            if (res.error) {
                                                error = res.error
                                                __await<dynamic>(jshit.utils.fova(onError, res))
                                            } else {
                                                error = undefined
                                                __await<dynamic>(onSuccess(res))
                                            }

                                            working = false
                                            for (field in jsArrayToList(fields)) {
                                                field.setError(res.fieldErrors && res.fieldErrors[field.getName()])
                                                field.setDisabled(false)
                                            }
                                            update()
                                        } finally { jshit.endTrain() }
                                    })),
                                cancelButtonTitle && jshit.button(json("tamy" to "cancel", "shamy" to if (dontShameButtons) undefined else "cancel",
                                    "title" to cancelButtonTitle, "disabled" to working, "style" to json("marginLeft" to 10), "onClick" to onCancel)),
                                working && formTicker())
                        )))
                },

                "componentDidMount" to {
                    if (focusedField) {
                        focusedField.focus()
                    }
                },

                "componentDidUpdate" to {
                    if (focusedField) {
                        focusedField.focus()
                    }
                }
            )

            for (field in jsArrayToList(fields)) {
                field.form = me
            }

            return@statefulElementCtor me
        }))
    }

    return ::jsFacing_Form
}


