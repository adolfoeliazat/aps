/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*


abstract class Form(val ui: dynamic, val legacySpec: dynamic) : Control(ControlInstanceSpec()) {
    abstract fun onSuccess(res: dynamic): Promise<Unit>

    override fun defaultControlTypeName() = "Form"

    val legacyInstance = LegacyCtor()

    fun LegacyCtor(): dynamic {
        val primaryButtonTitle = legacySpec.primaryButtonTitle; val cancelButtonTitle = legacySpec.cancelButtonTitle; val autoFocus = legacySpec.autoFocus;
        val fields = legacySpec.fields; val rpcFun = legacySpec.rpcFun; val onError = legacySpec.onError;
        val className = legacySpec.className; val dontShameButtons = legacySpec.dontShameButtons; val errorBannerStyle = legacySpec.errorBannerStyle;
        val debugName = legacySpec.debugName; val getInvisibleFieldNames = legacySpec.getInvisibleFieldNames; val onCancel = legacySpec.onCancel;

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

//                                            val reb = jshit.RequestBuilder(json())
//                                            reb.set(json("key" to "fun", "value" to rpcFun))
//                                            for (field in jsArrayToIterable(fields.filter({x: dynamic -> actualVisibleFieldNames.includes(x.getName())}))) {
//                                                field.contributeToRequest(json("reb" to reb))
//                                            }
//                                            val res = __await<dynamic>(ui.rpcSoft(reb.toMessage(json())))

                                            val res: SignInWithPasswordResponse = __await(rpc(SignInWithPasswordRequest(
                                                email = getField("email").getValue(),
                                                password = getField("password").getValue()
                                            )))

                                            if (res.error != null) {
                                                error = res.error
                                                __await<dynamic>(jshit.utils.fova(onError, res))
                                            } else {
                                                error = undefined
                                                __await<dynamic>(onSuccess(res))
                                            }

                                            working = false
                                            console.log("---------- res", res)
                                            for (field in jsArrayToList(fields)) {
                                                field.setError(res.fieldErrors[field.getName()])
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

    override fun render(): ReactElement {
        return legacyInstance.render()
    }

    fun getField(arg: dynamic): dynamic {
        return legacyInstance.getField(arg)
    }

}

fun jsFacing_loadSignInPageCtor(ui: dynamic): dynamic {
    fun loadSignInPage() {
        var form: Form = js("undefined")
        form = object : Form(ui, json(
            "debugName" to "Sign In",
            "primaryButtonTitle" to t("Sign In", "Войти"),
            "autoFocus" to "email",
            "fields" to jsArrayOf(
                ui.TextField(json(
                    "name" to "email",
                    "title" to t("Email", "Почта")
                )),
                ui.TextField(json(
                    "name" to "password",
                    "type" to "password",
                    "title" to t("Password", "Пароль")
                ))
            ),
            "rpcFun" to "signInWithPassword",

            "onError" to {res: dynamic ->
                form.getField("password").setValue("")
            }

        )) {
            override fun onSuccess(res: dynamic): Promise<Unit> {"__async"
                console.log("uuuuuuuuuuuuuuu", res.user)
                ui.user = res.user
                ui.startLiveStatusPolling()
                ui.token = res.token
                jshit.storageLocal.setItem("token", ui.token)
                if (ui.user.state == "cool") {
                    return __await<dynamic>(ui.pushNavigate("dashboard.html"))
                } else {
                    return __await<dynamic>(ui.pushNavigate("profile.html"))
                }
            }
        }

        ui.setPage(json(
            "header" to jshit.pageHeader(json("title" to t("Sign In", "Вход"))),
            "body" to jshit.diva(json(),
                ui.signedUpOK && jshit.preludeWithGreenCheck(json("title" to t(
                    "Cool. You have an account now. We sent you email with password.",
                    "Все круто. Теперь у тебя есть аккаунт. Пароль мы отправили письмом."))),

                form,

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

fun orig_jsFacing_loadSignInPageCtor(ui: dynamic): dynamic {
    fun loadSignInPage() {
        var form: dynamic = undefined
        form = ui.Form(json(
            "debugName" to "Sign In",
            "primaryButtonTitle" to t("Sign In", "Войти"),
            "autoFocus" to "email",
            "fields" to jsArrayOf(
                ui.TextField(json(
                    "name" to "email",
                    "title" to t("Email", "Почта")
                )),
                ui.TextField(json(
                    "name" to "password",
                    "type" to "password",
                    "title" to t("Password", "Пароль")
                ))
            ),
            "rpcFun" to "signInWithPassword",

            "onError" to {res: dynamic ->
                form.getField("password").setValue("")
            },

            "onSuccess" to {res: dynamic -> "__async"
                ui.user = res.user
                ui.startLiveStatusPolling()
                ui.token = res.token
                jshit.storageLocal.setItem("token", ui.token)
                if (ui.user.state == "cool") {
                    __await<dynamic>(ui.pushNavigate("dashboard.html"))
                } else {
                    __await<dynamic>(ui.pushNavigate("profile.html"))
                }
            }
        ))

        ui.setPage(json(
            "header" to jshit.pageHeader(json("title" to t("Sign In", "Вход"))),
            "body" to jshit.diva(json(),
                ui.signedUpOK && jshit.preludeWithGreenCheck(json("title" to t(
                    "Cool. You have an account now. We sent you email with password.",
                    "Все круто. Теперь у тебя есть аккаунт. Пароль мы отправили письмом."))),

                form,

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


