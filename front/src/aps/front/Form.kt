/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*

data class FormSpec<Req: RequestMatumba, Res>(
    val req: Req,
    val ui: World,
    val className: String = "",
    val containerClassName: String = "",
    val containerStyle: Style = Style(),
    val errorBannerStyle: dynamic = js("undefined"),
    val primaryButtonTitle: String = t("Post", "Запостить"),
    val cancelButtonTitle: String? = null,
//    val deleteButtonTitle: String? = null,
    val dontShameButtons: Boolean = false,

    val onCancel: FormMatumba<Req, Res>.() -> Unit = {},
    val onCancela: FormMatumba<Req, Res>.() -> Promise<Unit> = {"__async"; __asyncResult(Unit)},
    val onError: FormMatumba<Req, Res>.(res: FormResponse.Shitty) -> Unit = {},
    val onErrora: FormMatumba<Req, Res>.(res: FormResponse.Shitty) -> Promise<Unit> = {"__async"; __asyncResult(Unit)},
    val onSuccess: FormMatumba<Req, Res>.(res: Res) -> Unit = {},
    val onSuccessa: FormMatumba<Req, Res>.(res: Res) -> Promise<Unit> = {"__async"; __asyncResult(Unit)},
//    val onDeleta: FormMatumba<Req, Res>.() -> Promise<Unit> = {async{}},
    val getInvisibleFieldNames: FormMatumba<Req, Res>.() -> Iterable<String> = {listOf()}
)


class FormMatumba<Req: RequestMatumba, Res>(val spec: FormSpec<Req, Res>) : ToReactElementable {
    init {
        req.fields.forEach {it.form = this}
    }

    val req: Req get() = spec.req
    var error: String? = null
    var working: Boolean = false
    lateinit var actualVisibleFieldNames: Iterable<String>
    var focusedField: FormFieldFront? = null

    val control = object:Control() {
        override fun defaultControlTypeName() = "FormMatumba"

        override fun render(): ReactElement {
            gloshit.updateForm = {update()}
            figureOutActualVisibleFieldNames()

            return kdiv(attrs = Attrs(className = spec.containerClassName), style = spec.containerStyle){o->
                val form: ReactElement = Shitus.forma.apply(null, js("[]").concat(
                    jsArrayOf(
                        json("className" to spec.className),
                        if (error != null) Shitus.errorBanner(json("content" to error, "style" to spec.errorBannerStyle)) else undefined),

                    spec.req.fields
                        .filter{x -> actualVisibleFieldNames.contains(x.name)}
                        .map{x -> x.render()}
                        .toJSArray(),

                    Shitus.diva(
                        json("style" to json("textAlign" to "left")),

                        Button(
                            key = "primary" + req.fieldInstanceKeySuffix,
                            level = Button.Level.PRIMARY,
                            title = spec.primaryButtonTitle,
                            disabled = working,
                            onClicka = {async{
                                Shitus.beginTrain(json("name" to "Submit fucking form"))
                                try {
                                    for (field: FormFieldFront in spec.req.fields) {
                                        field.error = null
                                        field.disabled = true
                                    }
                                    error = null
                                    working = true
                                    update()

                                    val res: FormResponse = await(callMatumba(spec.req, spec.ui.tokenMaybe))

                                    when (res) {
                                        is FormResponse.Shitty -> {
                                            error = res.error
                                            (spec.onError)(res)
                                            await((spec.onErrora)(res))
                                        }
                                        is FormResponse.Hunky<*> -> {
                                            error = null
                                            val meat = res.meat as Res
                                            (spec.onSuccess)(meat)
                                            await((spec.onSuccessa)(meat))
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
                                    TestGlobal.actionSignal.resolve()
                                } finally {
                                    Shitus.endTrain()
                                }
                            }}
                        ).toReactElement(),

//                        jsFacing_button(json(
//                            /*"tamy" to "primary", "shamy" to if (spec.dontShameButtons) undefined else "primary",*/
//                            "level" to "primary", "title" to spec.primaryButtonTitle, "disabled" to working,
//                            "onClick" to {"__async"
//                                Shitus.beginTrain(json("name" to "Submit fucking form")); try {
//                                    for (field: FormFieldFront in spec.req.fields) {
//                                        field.error = null
//                                        field.disabled = true
//                                    }
//                                    error = null
//                                    working = true
//                                    update()
//
//                                    val res: FormResponse = __await(callMatumba(spec.req, spec.ui.tokenMaybe))
//
//                                    when (res) {
//                                        is FormResponse.Shitty -> {
//                                            error = res.error
//                                            (spec.onError)(res)
//                                            __await((spec.onErrora)(res))
//                                        }
//                                        is FormResponse.Hunky<*> -> {
//                                            error = null
//                                            val meat = res.meat as Res
//                                            (spec.onSuccess)(meat)
//                                            __await((spec.onSuccessa)(meat))
//                                        }
//                                    }
//
////                                    if (res is FormResponse.Shitty) {
////                                        for (fe in res.fieldErrors) {
////                                            console.warn(fe.field + " --> " + fe.error)
////                                        }
////                                    }
//
//                                    for (field in spec.req.fields) {
//                                        field.error = if (res !is FormResponse.Shitty) null else
//                                            res.fieldErrors.find{it.field == field.name}?.error
//                                        field.disabled = false
//                                    }
//
//                                    working = false
//                                    update()
//                                } finally { Shitus.endTrain() }
//                            }
//                        ), key = "primary" + req.fieldInstanceKeySuffix),

                        if (spec.cancelButtonTitle != null) {
                            Button(
                                key = "cancel" + req.fieldInstanceKeySuffix,
                                title = spec.cancelButtonTitle,
                                disabled = working,
                                style = Style(marginLeft = 10),
                                onClicka = {async{
                                    (spec.onCancel)()
                                    await((spec.onCancela)())
                                    TestGlobal.actionSignal.resolve()
                                }}
                            ).toReactElement()

//                                    jsFacing_button(json(/*"tamy" to "cancel", "shamy" to if (spec.dontShameButtons) undefined else "cancel",*/
//                                        "title" to spec.cancelButtonTitle, "disabled" to working, "style" to json("marginLeft" to 10),
//                                        "onClick" to {"__async"
//                                            (spec.onCancel)()
//                                            __await((spec.onCancela)())
//                                        }), key = "cancel" + req.fieldInstanceKeySuffix)
                        } else undefined,

                        //                                spec.deleteButtonTitle?.let {
//                                    Button(key = "delete" + req.fieldInstanceKeySuffix, title = it, onClicka = {
//                                        (spec.onDeleta)()
//                                    })
//                                },

                        if (working) renderTicker("right").toReactElement() else null
                    )
                ))
                o- form
            }.toReactElement()
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

    fun fieldFocused(field: FormFieldFront) {
        focusedField = field
    }

    fun fieldBlurred(field: FormFieldFront) {
        focusedField = null
    }

    override fun toReactElement(): ReactElement {
        return control.toReactElement()
    }
}



