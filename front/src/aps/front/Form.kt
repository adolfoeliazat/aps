/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

val defaultCancelButtonTitle = t("Never Mind", "Передумал")

data class FormSpec<Req: RequestMatumba, Res>(
    val req: Req,
    val ui: LegacyUIShit,
    val className: String = "",
    val errorBannerStyle: dynamic = js("undefined"),
    val primaryButtonTitle: String = t("Post", "Запостить"),
    val cancelButtonTitle: String? = null,
    val dontShameButtons: Boolean = false,

    val onCancel: FormMatumba<Req, Res>.() -> Unit = {},
    val onCancela: FormMatumba<Req, Res>.() -> Promise<Unit> = {"__async"; __asyncResult(Unit)},
    val onError: FormMatumba<Req, Res>.(res: FormResponse.Shitty) -> Unit = {},
    val onErrora: FormMatumba<Req, Res>.(res: FormResponse.Shitty) -> Promise<Unit> = {"__async"; __asyncResult(Unit)},
    val onSuccess: FormMatumba<Req, Res>.(res: Res) -> Unit = {},
    val onSuccessa: FormMatumba<Req, Res>.(res: Res) -> Promise<Unit> = {"__async"; __asyncResult(Unit)},
    val getInvisibleFieldNames: FormMatumba<Req, Res>.() -> Iterable<String> = {listOf()}
)


class FormMatumba<Req: RequestMatumba, Res>(val spec: FormSpec<Req, Res>) {
    init {
        req.fields.forEach {it.form = this}
    }

    val req: Req get() = spec.req
    var error: String? = null
    var working: Boolean = false
    lateinit var actualVisibleFieldNames: Iterable<String>
    var focusedField: FormFieldFront<*>? = null

    val control = object:Control() {
        override fun defaultControlTypeName() = "FormMatumba"

        override fun render(): ReactElement {
            figureOutActualVisibleFieldNames()

            fun formTicker(): dynamic {
                return jsFacing_elcl(json(
                    "render" to {
                        Shitus.diva(json("className" to "progressTicker", "style" to json("float" to "right", "width" to 14, "height" to 28, "backgroundColor" to Color.BLUE_GRAY_600)))
                    },
                    "componentDidMount" to {
//                        TestGlobal.shitSpins = true
                    },
                    "componentWillUnmount" to {
//                        TestGlobal.shitSpins = false
                    }
                ))
            }

            return Shitus.diva(json(),
                Shitus.forma.apply(null, js("[]").concat(
                    jsArrayOf(
                        json("className" to spec.className),
                        if (error != null) Shitus.errorBanner(json("content" to error, "style" to spec.errorBannerStyle)) else undefined),

                    spec.req.fields
                        .filter{x -> actualVisibleFieldNames.contains(x.name)}
                        .map{x -> x.render()}
                        .toJSArray(),

                    Shitus.diva(json("style" to json("textAlign" to "left")),
                        Shitus.button(json("tamy" to "primary", "shamy" to if (spec.dontShameButtons) undefined else "primary",
                            "level" to "primary", "title" to spec.primaryButtonTitle, "disabled" to working,
                            "onClick" to {"__async"
                                Shitus.beginTrain(json("name" to "Submit fucking form")); try {
                                    for (field: FormFieldFront<*> in spec.req.fields) {
                                        field.error = null
                                        field.disabled = true
                                    }
                                    error = null
                                    working = true
                                    update()

                                    val res: FormResponse = __await(callMatumba(spec.req, spec.ui.token))

                                    when (res) {
                                        is FormResponse.Shitty -> {
                                            error = res.error
                                            (spec.onError)(res)
                                            __await((spec.onErrora)(res))
                                        }
                                        is FormResponse.Hunky<*> -> {
                                            error = null
                                            val meat = res.meat as Res
                                            (spec.onSuccess)(meat)
                                            __await((spec.onSuccessa)(meat))
                                        }
                                    }

//                                    if (res is FormResponse.Shitty) {
//                                        for (fe in res.fieldErrors) {
//                                            console.warn(fe.field + " --> " + fe.error)
//                                        }
//                                    }

                                    for (field in spec.req.fields) {
                                        field.error = if (res !is FormResponse.Shitty) null else
                                            res.fieldErrors.find{it.field == field.name}?.error
                                        field.disabled = false
                                    }

                                    working = false
                                    update()
                                } finally { Shitus.endTrain() }
                            }
                        )),

                        if (spec.cancelButtonTitle != null)
                            Shitus.button(json("tamy" to "cancel", "shamy" to if (spec.dontShameButtons) undefined else "cancel",
                                "title" to spec.cancelButtonTitle, "disabled" to working, "style" to json("marginLeft" to 10),
                                "onClick" to {"__async"
                                    (spec.onCancel)()
                                    __await((spec.onCancela)())
                                })) else undefined,

                        working && formTicker()
                    )
                )))
        }

        override fun componentDidMount() {
            focusedField?.let {it.focus()}
        }

        override fun componentDidUpdate() {
            focusedField?.let {it.focus()}
        }
    }

    fun figureOutActualVisibleFieldNames() {
        actualVisibleFieldNames = spec.req.fields.map{x -> x.name}.without((spec.getInvisibleFieldNames)())
    }

    fun fieldChanged() {
        val oldVisible = actualVisibleFieldNames.toSet()
        figureOutActualVisibleFieldNames()
        if (oldVisible != actualVisibleFieldNames.toSet()) {
            control.update()
        }
    }

    fun fieldFocused(field: FormFieldFront<*>) {
        focusedField = field
    }

    fun fieldBlurred(field: FormFieldFront<*>) {
        focusedField = null
    }


    fun toReactElement(): ReactElement {
        return control.toReactElement()
    }
}



