/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import aps.front.testutils.*
import into.kommon.*
import kotlin.js.Json
import kotlin.js.json

data class FormSpec<Req: RequestMatumba, Res>(
    val req: Req,
    val populateFields: (Json) -> Unit = {},
    val ui: World,
//    val className: String = "",
    val containerClassName: String = "",
    val containerStyle: Style = Style(),
    val errorBannerStyle: dynamic = js("undefined"),
    val primaryButtonTitle: String = t("Post", "Запостить"),
    val cancelButtonTitle: String? = null,
//    val deleteButtonTitle: String? = null,
    val dontShameButtons: Boolean = false,
//    val renderButtons: Boolean = true,
    val buttonLocation: ButtonLocation = FormSpec.ButtonLocation.LEFT,

    val onCancel: FormMatumba<Req, Res>.() -> Unit = {},
    val onCancela: suspend FormMatumba<Req, Res>.() -> Unit = {},
    val onError: FormMatumba<Req, Res>.(res: FormResponse.Shitty) -> Unit = {},
    val onErrora: suspend FormMatumba<Req, Res>.(res: FormResponse.Shitty) -> Unit = {},
    val onSuccess: FormMatumba<Req, Res>.(res: Res) -> Unit = {},
    val onSuccessa: suspend FormMatumba<Req, Res>.(res: Res) -> Unit = {},
//    val onDeleta: FormMatumba<Req, Res>.() -> Promise<Unit> = {async{}},
    val getInvisibleFieldNames: FormMatumba<Req, Res>.() -> Iterable<String> = {listOf()}
) {
    enum class ButtonLocation {
        LEFT, RIGHT
    }
}

class FormMatumba<Req: RequestMatumba, Res>(val form: FormSpec<Req, Res>, val procedureName: String? = null) : ToReactElementable {
    init {
        req.fields.forEach {it.form = this}
    }

    val req: Req get() = form.req
    var error: String? = null
    var working: Boolean = false
    lateinit var actualVisibleFieldNames: Iterable<String>
    var focusedField: FormFieldFront? = null

    val elementID get() = control.elementID

    val fieldsAndBanner = object:Control2() {
        override fun render(): ElementBuilder {
            figureOutActualVisibleFieldNames()
            return kform{o->
                if (error != null) {
                    val shit: ReactElement = Shitus.errorBanner(json("content" to error, "style" to form.errorBannerStyle))
                    o- shit
                }
                for (f in form.req.fields) {
                    if (actualVisibleFieldNames.contains(f.name)) {
                        o- f.render()
                    }
                }
            }
        }
    }

    val buttonsAndTicker = object:Control2() {
        override fun render(): ToReactElementable {
            return kdiv(display = "flex"){o->
                val tickerBar = kdiv(Style(display = "flex", flexGrow = 1,
                                           justifyContent = when (form.buttonLocation) {
                                               FormSpec.ButtonLocation.LEFT -> "flex-end"
                                               FormSpec.ButtonLocation.RIGHT -> "flex-start"})){o->
                    if (working)
                        o- renderTicker()
                }

                if (form.buttonLocation == FormSpec.ButtonLocation.RIGHT)
                    o- tickerBar

                o- Button(
                    key = buttonKey(fconst.key.button.primary.ref),
                    level = Button.Level.PRIMARY,
                    title = form.primaryButtonTitle,
                    disabled = working,
                    onClicka = {submit()}
                )

                if (form.cancelButtonTitle != null) {
                    o- Button(
                        key = buttonKey(fconst.key.button.cancel.ref),
                        title = form.cancelButtonTitle,
                        disabled = working,
                        style = Style(marginLeft = 10),
                        onClicka = {async{
                            (form.onCancel)()
                            (form.onCancela)()
                        }}
                    ).toReactElement()
                }

                if (form.buttonLocation == FormSpec.ButtonLocation.LEFT)
                    o- tickerBar
            }
        }

        private fun buttonKey(key: ButtonKey): ButtonKey {
            val subscript = req.fieldInstanceKeySuffix
            return when (subscript) {
                null -> key
                else -> SubscriptButtonKey(key, subscript)
            }
        }

    }

    val control = object:Control() {
        override fun defaultControlTypeName() = "FormMatumba"

        override fun render(): ReactElement {
            return kdiv(attrs = Attrs(className = form.containerClassName, id = elementID), style = form.containerStyle){o->
                o- fieldsAndBanner
                o- buttonsAndTicker
            }.toReactElement()
        }


        override fun componentDidMount() {
            focusedField?.let {it.focus()}
        }

        override fun componentDidUpdate() {
            focusedField?.let {it.focus()}
        }
    }

    suspend fun submit() {
        for (field: FormFieldFront in form.req.fields) {
            field.error = null
            field.disabled = true
        }
        error = null
        working = true
        updateAll()

        TestGlobal.formTickingLock.sutPause()

        val theProcedureName = procedureName ?: remoteProcedureNameForRequest(req)
        val res: FormResponse = await(callMatumba(theProcedureName, form.req, form.ui.tokenMaybe, populateFields = form.populateFields))

        when (res) {
            is FormResponse.Shitty -> {
                error = res.error
                (form.onError)(res)
                (form.onErrora)(res)
            }
            is FormResponse.Hunky<*> -> {
                error = null
                val meat = res.meat as Res
                (form.onSuccess)(meat)
                (form.onSuccessa)(meat)
            }
        }

        for (field in form.req.fields) {
            field.error = if (res !is FormResponse.Shitty) null else
                res.fieldErrors.find{it.field == field.name}?.error
            field.disabled = false
        }

        working = false
        updateAll()

        TestGlobal.formDoneLock.sutPause()
    }

    fun updateAll() {
        fieldsAndBanner.update()
        buttonsAndTicker.update()
    }

    fun figureOutActualVisibleFieldNames() {
        actualVisibleFieldNames = form.req.fields.map{x -> x.name}.without((form.getInvisibleFieldNames)())
    }

    fun fieldChanged() {
        val oldVisible = actualVisibleFieldNames.toSet()
        figureOutActualVisibleFieldNames()
        if (oldVisible != actualVisibleFieldNames.toSet()) {
            updateAll()
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

//fun TestScenarioBuilder.formSequence_killme(
//    buildAction: () -> Unit,
//    assertionDescr: String,
//    halfwayAssertionID: String,
//    finalAssertionID: String,
//    halfwayTimeout: Int = 5000,
//    completedTimeout: Int = 1000
//) {
//    val o = this
//    o.act {
//        TestGlobal.formActionCompleted = ResolvableShit()
//        TestGlobal.formActionHalfway = ResolvableShit()
//        TestGlobal.formActionHalfwayConsidered = ResolvableShit()
//    }
//
//    buildAction()
//
//    o.acta {TestGlobal.formActionHalfway.promise.orTestTimeout(halfwayTimeout)}
//    o.assertScreenHTML("$assertionDescr (halfway)", halfwayAssertionID)
//    o.act {TestGlobal.formActionHalfwayConsidered.resolve()}
//
//    o.acta {TestGlobal.formActionCompleted.promise.orTestTimeout(completedTimeout)}
//    o.assertScreenHTML(assertionDescr, finalAssertionID)
//}

//fun TestScenarioBuilder.formWithAnimationOnCompletionSequence_killme(
//    shit: TestShit,
//    buildAction: () -> Unit,
//    assertionDescr: String,
//    halfwayAssertionID: String,
//    completionAnimationHalfwayAssertionID: String,
//    finalAssertionID: String,
//    halfwayTimeout: Int = 5000,
//    completedTimeout: Int = 5000
//) {
//    act {
//        TestGlobal.formActionCompleted = ResolvableShit()
//        TestGlobal.formActionHalfway = ResolvableShit()
//        TestGlobal.formActionHalfwayConsidered = ResolvableShit()
//        TestGlobal.animationHalfwaySignal = ResolvableShit()
//        TestGlobal.animationHalfwaySignalProcessedSignal = ResolvableShit()
//    }
//
//    acta {shit.imposeNextRequestTimestamp()}
//    buildAction()
//
//    acta {TestGlobal.formActionHalfway.promise.orTestTimeout(halfwayTimeout)}
//    assertScreenHTML("$assertionDescr (halfway)", halfwayAssertionID)
//    act {TestGlobal.formActionHalfwayConsidered.resolve()}
//
//    acta {TestGlobal.animationHalfwaySignal.promise.orTestTimeout(fconst.test.default.animationHalfwaySignalTimeout)}
//    assertScreenHTML(assertionDescr + " (completion animation halfway)", completionAnimationHalfwayAssertionID)
//    act {TestGlobal.animationHalfwaySignalProcessedSignal.resolve()}
//
//    acta {TestGlobal.formActionCompleted.promise.orTestTimeout(completedTimeout)}
//    assertScreenHTML(assertionDescr, finalAssertionID)
//}

suspend fun submitFormSequence(
    shit: TestShit,
    descr: String,
    action: (suspend () -> Unit)? = null,
    aid: String,
    buttonKey: TestRef<ButtonKey>? = null,
    imposeTimestamp: Boolean = true,
    aopts: AssertScreenOpts? = null
) {
    sequence(
        action = {async{
            if (imposeTimestamp) {
                shit.imposeNextRequestTimestamp()
            }
            val shit = action ?: {
                buttonClick(buttonKey ?: fconst.key.button.primary.testRef)
            }
            shit()
        }},
        descr = descr,
        steps = listOf(
            PauseAssertResumeStep(TestGlobal.formTickingLock, "$aid--1"),
            PauseAssertResumeStep(TestGlobal.formDoneLock, "$aid--2")
        ),
        aopts = aopts
    )
}

suspend fun formSubmissionAttempts(
    testShit: TestShit,
    descr: String = "Describe me",
    baseID: String,
    buttonKey: TestRef<ButtonKey>? = null,
    attempts: List<TestAttempt>
) {
    for (i in 0 until attempts.size)
        for (j in i+1 until attempts.size)
            if (attempts[i].subID == attempts[j].subID) bitch("Attempt subID duplication: ${attempts[i].subID}")

    for ((i, attempt) in attempts.withIndex()) {
        attempt.prepare()
        submitFormSequence(
            testShit,
            descr = /*"Attempt: " + */attempt.descr,
            aid = "$baseID--${attempt.subID}",
            imposeTimestamp = i == attempts.lastIndex,
            buttonKey = buttonKey,
            aopts = attempt.aopts)
    }
}

class TestAttempt(
    val subID: String,
    val descr: String,
    val prepare: suspend () -> Unit,
    val aopts: AssertScreenOpts? = null
)


fun badTextFieldValuesThenValid(
    field: TestRef<TextFieldSpec>,
    validValue: String,
    aopts: AssertScreenOpts? = null,
    subIDSuffix: String = ""
): List<TestAttempt> {
    val f = field.it
    return mutableListOf<TestAttempt>()-{res->
        val add = fuckingAdder(res, f.name, aopts, subIDSuffix)

        if (f.minLen >= 1) {
            add("empty", "")
            add("blank", " ".repeat(f.minLen)) // TODO:vgrechka Check for other whitespace or non-alphanumeric shit?
        }
        if (f.minLen > 1) {
            add("too short", TestData.generateShit(f.minLen - 1))
        }
        add("too long", TestData.generateShit(f.maxLen + 1))

        exhaustive/when (f.type) {
            TextFieldType.EMAIL -> {
                add("malformed 1", "shit")
                // TODO:vgrechka Other shitty emails?
            }
            TextFieldType.PHONE -> {
                add("malformed 1", "shit-123")
                add("malformed 2", "123 4835 a 43234")
                add("too few digits", "1   2    3")
                // TODO:vgrechka Other shitty phones?
            }
            TextFieldType.STRING, TextFieldType.TEXTAREA, TextFieldType.PASSWORD -> {}
        }

        add("valid", validValue)
    }
}

fun badIntFieldValuesThenValid(field: TestRef<IntFieldSpec>, validValue: Int): List<TestAttempt> {
    val f = field.it
    return mutableListOf<TestAttempt>()-{res->
        val add = fuckingAdder(res, f.name)
        add("empty", "")
        add("blank", "     ")
        add("fraction 1", "1.5")
        add("fraction 2", ".5")
        add("fraction 3", "5.")
        add("malformed 1", "5..")
        add("malformed 2", "4e5")
        add("malformed 3", "pizda")
        add("malformed 4", "1 2")
        add("too little", (f.min - 1).toString())
        add("too big", (f.max + 1).toString())

        add("valid", validValue.toString())
    }
}

private class fuckingAdder(
    val res: MutableList<TestAttempt>,
    val fieldName: String,
    val aopts: AssertScreenOpts? = null,
    val subIDSuffix: String = ""
) {
    operator fun invoke(descr: String, value: String) {
        val lastDot = fieldName.lastIndexOf(".")
        val fieldNameForDescr = when {
            lastDot == -1 -> fieldName
            else -> fieldName.substring(lastDot + 1)
        }
        res += TestAttempt(
            subID = "$fieldName--" + descr.replace(" ", "-") + subIDSuffix,
            descr = "$fieldNameForDescr: $descr",
            prepare = {
                inputSetValue(fieldName, value)
            },
            aopts = aopts
        )
    }
}

fun eachOrCombinationOfLasts(chunks: List<List<TestAttempt>>): List<TestAttempt> =
    if (!testOpts().skipRambling) {
        flatten(chunks)
    } else {
        val lastItem = chunks.last().last()
        listOf(
            TestAttempt(
                subID = "firsts-combined",
                descr = "firsts combined",
                prepare = {
                    for (chunk in chunks) {
                        chunk.first().prepare()
                    }
                }
            ),
            TestAttempt(
                subID = "lasts-combined",
                descr = "lasts combined",
                prepare = {
                    for (chunk in chunks) {
                        chunk.last().prepare()
                    }
                }
            ))
    }

fun eachOrLast(attempts: List<TestAttempt>): List<TestAttempt> =
    if (!testOpts().skipRambling) {
        attempts
    } else {
        listOf(attempts.last())
    }




















