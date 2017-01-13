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
                                    TestGlobal.formActionHalfway.resolve()
                                    await(TestGlobal.formActionHalfwayConsidered.promise)

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

                                    for (field in spec.req.fields) {
                                        field.error = if (res !is FormResponse.Shitty) null else
                                            res.fieldErrors.find{it.field == field.name}?.error
                                        field.disabled = false
                                    }

                                    working = false
                                    update()
                                    TestGlobal.formActionCompleted.resolve()
                                } finally {
                                    Shitus.endTrain()
                                }
                            }}
                        ).toReactElement(),


                        if (spec.cancelButtonTitle != null) {
                            Button(
                                key = "cancel" + req.fieldInstanceKeySuffix,
                                title = spec.cancelButtonTitle,
                                disabled = working,
                                style = Style(marginLeft = 10),
                                onClicka = {async{
                                    (spec.onCancel)()
                                    await((spec.onCancela)())
                                    TestGlobal.formActionCompleted.resolve()
                                }}
                            ).toReactElement()

                        } else undefined,

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

fun TestScenarioBuilder.formSequence(
    buildAction: () -> Unit,
    assertionDescr: String,
    halfwayAssertionID: String,
    finalAssertionID: String,
    halfwayTimeout: Int = 5000,
    completedTimeout: Int = 1000
) {
    val o = this
    o.act {
        TestGlobal.formActionCompleted = ResolvableShit()
        TestGlobal.formActionHalfway = ResolvableShit()
        TestGlobal.formActionHalfwayConsidered = ResolvableShit()
    }

    buildAction()

    o.acta {TestGlobal.formActionHalfway.promise.orTestTimeout(halfwayTimeout)}
    o.assertScreenHTML("$assertionDescr (halfway)", halfwayAssertionID)
    o.act {TestGlobal.formActionHalfwayConsidered.resolve()}

    o.acta {TestGlobal.formActionCompleted.promise.orTestTimeout(completedTimeout)}
    o.assertScreenHTML(assertionDescr, finalAssertionID)
}

fun TestScenarioBuilder.formWithAnimationOnCompletionSequence(
    buildAction: () -> Unit,
    assertionDescr: String,
    halfwayAssertionID: String,
    completionAnimationHalfwayAssertionID: String,
    finalAssertionID: String,
    halfwayTimeout: Int = 5000,
    completedTimeout: Int = 5000
) {
    val o = this
    o.act {
        TestGlobal.formActionCompleted = ResolvableShit()
        TestGlobal.formActionHalfway = ResolvableShit()
        TestGlobal.formActionHalfwayConsidered = ResolvableShit()
        TestGlobal.animationHalfwaySignal = ResolvableShit()
        TestGlobal.animationHalfwaySignalProcessedSignal = ResolvableShit()
    }

    buildAction()

    o.acta {TestGlobal.formActionHalfway.promise.orTestTimeout(halfwayTimeout)}
    o.assertScreenHTML("$assertionDescr (halfway)", halfwayAssertionID)
    o.act {TestGlobal.formActionHalfwayConsidered.resolve()}

    o.acta {TestGlobal.animationHalfwaySignal.promise.orTestTimeout(fconst.test.default.animationHalfwaySignalTimeout)}
    o.assertScreenHTML(assertionDescr + " (completion animation halfway)", completionAnimationHalfwayAssertionID)
    o.act {TestGlobal.animationHalfwaySignalProcessedSignal.resolve()}

    o.acta {TestGlobal.formActionCompleted.promise.orTestTimeout(completedTimeout)}
    o.assertScreenHTML(assertionDescr, finalAssertionID)
}























