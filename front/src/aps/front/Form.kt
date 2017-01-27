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
    val onCancela: suspend FormMatumba<Req, Res>.() -> Unit = {},
    val onError: FormMatumba<Req, Res>.(res: FormResponse.Shitty) -> Unit = {},
    val onErrora: suspend FormMatumba<Req, Res>.(res: FormResponse.Shitty) -> Unit = {},
    val onSuccess: FormMatumba<Req, Res>.(res: Res) -> Unit = {},
    val onSuccessa: suspend FormMatumba<Req, Res>.(res: Res) -> Unit = {},
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

    val elementID get() = control.elementID

    val control = object:Control() {
        override fun defaultControlTypeName() = "FormMatumba"

        override fun render(): ReactElement {
            aps.gloshit.updateForm = {update()}
            figureOutActualVisibleFieldNames()

            return kdiv(attrs = Attrs(className = spec.containerClassName, id = elementID), style = spec.containerStyle){o->
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
                            key = fconst.key.primary.decl + req.fieldInstanceKeySuffix,
                            level = Button.Level.PRIMARY,
                            title = spec.primaryButtonTitle,
                            disabled = working,
                            onClicka = {
                                for (field: FormFieldFront in spec.req.fields) {
                                    field.error = null
                                    field.disabled = true
                                }
                                error = null
                                working = true
                                update()

                                TestGlobal.formTickingLock.sutPause()

                                val res: FormResponse = await(callMatumba(spec.req, spec.ui.tokenMaybe))
//                                    TestGlobal.formActionHalfway.resolve()
//                                    await(TestGlobal.formActionHalfwayConsidered.promise)

                                when (res) {
                                    is FormResponse.Shitty -> {
                                        error = res.error
                                        (spec.onError)(res)
                                        (spec.onErrora)(res)
                                    }
                                    is FormResponse.Hunky<*> -> {
                                        error = null
                                        val meat = res.meat as Res
                                        (spec.onSuccess)(meat)
                                        (spec.onSuccessa)(meat)
                                    }
                                }

                                for (field in spec.req.fields) {
                                    field.error = if (res !is FormResponse.Shitty) null else
                                        res.fieldErrors.find{it.field == field.name}?.error
                                    field.disabled = false
                                }

                                working = false
                                update()

                                TestGlobal.formDoneLock.sutPause()
//                                    TestGlobal.formActionCompleted.resolve()
                            }
                        ).toReactElement(),


                        if (spec.cancelButtonTitle != null) {
                            Button(
                                key = fconst.key.cancel.decl + req.fieldInstanceKeySuffix,
                                title = spec.cancelButtonTitle,
                                disabled = working,
                                style = Style(marginLeft = 10),
                                onClicka = {async{
                                    (spec.onCancel)()
                                    (spec.onCancela)()
//                                    TestGlobal.formActionCompleted.resolve()
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

//fun TestScenarioBuilder.submitFormSequence(
//    shit: TestShit,
//    descr: String,
//    buildAction: (() -> Unit)? = null,
//    aid: String,
//    buttonKey: String? = null,
//    imposeTimestamp: Boolean = true,
//    buildBeforeAction: () -> Unit = {}
//) {
//    sequence(
//        buildAction = {
//            if (imposeTimestamp) {
//                acta {shit.imposeNextRequestTimestamp()}
//            }
//            buildBeforeAction()
//            (buildAction ?: {
//                buttonClick(buttonKey ?: fconst.key.primary.testRef)
//            })()
//        },
//        assertionDescr = descr,
//        steps = listOf(
//            TestSequenceStep(TestGlobal.formTickingLock, "$aid--1"),
//            TestSequenceStep(TestGlobal.formDoneLock, "$aid--2")
//        )
//    )
//}

suspend fun submitFormSequence2(
    shit: TestShit,
    descr: String,
    action: (() -> Promisoid<Unit>)? = null,
    aid: String,
    buttonKey: String? = null,
    imposeTimestamp: Boolean = true,
    beforeAction: () -> Promisoid<Unit> = {async{}}
) {
    sequence2(
        action = {async{
            if (imposeTimestamp) {
                await(shit.imposeNextRequestTimestamp())
            }
            await(beforeAction())
            await((action ?: {
                buttonClick2(buttonKey ?: fconst.key.primary.testRef)
            })())
        }},
        assertionDescr = descr,
        steps = listOf(
            TestSequenceStep(TestGlobal.formTickingLock, "$aid--1"),
            TestSequenceStep(TestGlobal.formDoneLock, "$aid--2")
        )
    )
}

//fun TestScenarioBuilder.formSubmissionAttempts(
//    testShit: TestShit,
//    descr: String,
//    baseID: String,
//    attempts: List<TestAttempt>
//) {
//    for (i in 0 until attempts.size)
//        for (j in i+1 until attempts.size)
//            if (attempts[i].subID == attempts[j].subID) bitch("Attempt subID duplication: ${attempts[i].subID}")
//
//    section(descr) {
//        for ((i, attempt) in attempts.withIndex()) {
//            attempt.buildPrepare()
//            submitFormSequence(
//                testShit,
//                descr = "Attempt: ${attempt.descr}",
//                aid = "$baseID--${attempt.subID}",
//                imposeTimestamp = i == attempts.lastIndex,
//                buildBeforeAction = attempt.buildBeforeSubmit)
//        }
//    }
//}

suspend fun formSubmissionAttempts2(
    testShit: TestShit,
    descr: String,
    baseID: String,
    attempts: List<TestAttempt2>
) {
    for (i in 0 until attempts.size)
        for (j in i+1 until attempts.size)
            if (attempts[i].subID == attempts[j].subID) bitch("Attempt subID duplication: ${attempts[i].subID}")

//    section(descr) {
    for ((i, attempt) in attempts.withIndex()) {
        await(attempt.prepare())
        submitFormSequence2(
            testShit,
            descr = "Attempt: ${attempt.descr}",
            aid = "$baseID--${attempt.subID}",
            imposeTimestamp = i == attempts.lastIndex,
            beforeAction = attempt.beforeSubmit)
    }
//    }
}

class TestAttempt(
    val subID: String,
    val descr: String,
    val buildBeforeSubmit: () -> Unit = {},
    val buildPrepare: () -> Unit
)

class TestAttempt2(
    val subID: String,
    val descr: String,
    val beforeSubmit: () -> Promisoid<Unit> = {async{}},
    val prepare: () -> Promisoid<Unit>
)

class FuckingItem(
    val subID: String,
    val descr: String,
    val buildBeforeSubmit: () -> Unit = {},
    val buildPrepare: () -> Unit
)

//class TestAttemptBuilder(val o: TestScenarioBuilder) {
//    private val _attempts = mutableListOf<TestAttempt>()
//
//    val attempts get() = _attempts.toList()
//
//    fun add(attempt: TestAttempt) {
//        if (_attempts.any {it.subID == attempt.subID}) bitch("ID is already used: $${attempt.subID}")
//        _attempts += attempt
//    }
//}

fun badTextFieldValuesThenValid(o: TestScenarioBuilder, field: TextFieldSpec, validValue: String): List<TestAttempt> {
    val l = mutableListOf<TestAttempt>()
    exhaustive/when (field.type) {
        TextFieldType.STRING, TextFieldType.PASSWORD, TextFieldType.TEXTAREA -> {
            if (field.minLen >= 1) {
                l += TestAttempt(subID = "${field.name}--empty", descr = "${field.name}: empty") {o.inputSetValue(field.name, "")}
            }
            if (field.minLen > 1) {
                l += TestAttempt(subID = "${field.name}--tooShort", descr = "${field.name}: too short") {o.inputSetValue(field.name, TestData.generateShit(field.minLen - 1))}
            }
            l += TestAttempt(subID = "${field.name}--tooLong", descr = "${field.name}: too long") {o.inputSetValue(field.name, TestData.generateShit(field.maxLen + 1))}
            l += TestAttempt(subID = "${field.name}--valid", descr = "${field.name}: valid") {o.inputSetValue(field.name, validValue)}
        }

        TextFieldType.EMAIL -> {
            l += TestAttempt(subID = "${field.name}--empty", descr = "${field.name}: empty") {o.inputSetValue(field.name, "")}
            l += TestAttempt(subID = "${field.name}-shit", descr = "${field.name}: shit") {o.inputSetValue(field.name, "shit")}
            l += TestAttempt(subID = "${field.name}-valid", descr = "${field.name}: valid") {o.inputSetValue(field.name, validValue)}
        }

        TextFieldType.PHONE -> imf("badTextFieldValuesThenValid for PHONE")
    }
    return l
}

fun badTextFieldValuesThenValid2(field: TextFieldSpec, validValue: String): List<TestAttempt2> {
    val l = mutableListOf<TestAttempt2>()
    exhaustive/when (field.type) {
        TextFieldType.STRING, TextFieldType.PASSWORD, TextFieldType.TEXTAREA -> {
            if (field.minLen >= 1) {
                l += TestAttempt2(subID = "${field.name}--empty", descr = "${field.name}: empty") {inputSetValue2(field.name, "")}
            }
            if (field.minLen > 1) {
                l += TestAttempt2(subID = "${field.name}--tooShort", descr = "${field.name}: too short") {inputSetValue2(field.name, TestData.generateShit(field.minLen - 1))}
            }
            l += TestAttempt2(subID = "${field.name}--tooLong", descr = "${field.name}: too long") {inputSetValue2(field.name, TestData.generateShit(field.maxLen + 1))}
            l += TestAttempt2(subID = "${field.name}--valid", descr = "${field.name}: valid") {inputSetValue2(field.name, validValue)}
        }

        TextFieldType.EMAIL -> {
            l += TestAttempt2(subID = "${field.name}--empty", descr = "${field.name}: empty") {inputSetValue2(field.name, "")}
            l += TestAttempt2(subID = "${field.name}-shit", descr = "${field.name}: shit") {inputSetValue2(field.name, "shit")}
            l += TestAttempt2(subID = "${field.name}-valid", descr = "${field.name}: valid") {inputSetValue2(field.name, validValue)}
        }

        TextFieldType.PHONE -> imf("badTextFieldValuesThenValid for PHONE")
    }
    return l
}

//fun TestAttemptBuilder.badTextFieldValuesThenValid(field: TextFieldSpec, validValue: String) {
//
//    exhaustive/when (field.type) {
//        TextFieldType.STRING, TextFieldType.PASSWORD, TextFieldType.TEXTAREA -> {
//            if (field.minLen >= 1) {
//                add(TestAttempt(subID = "${field.name}--empty", descr = "${field.name}: empty") {o.inputSetValue(field.name, "")})
//            }
//            if (field.minLen > 1) {
//                add(TestAttempt(subID = "${field.name}--tooShort", descr = "${field.name}: too short") {o.inputSetValue(field.name, TestData.generateShit(field.minLen - 1))})
//            }
//            add(TestAttempt(subID = "${field.name}--tooLong", descr = "${field.name}: too long") {o.inputSetValue(field.name, TestData.generateShit(field.maxLen + 1))})
//            add(TestAttempt(subID = "${field.name}--valid", descr = "${field.name}: valid") {o.inputSetValue(field.name, validValue)})
//        }
//
//        TextFieldType.EMAIL -> {
//            add(TestAttempt(subID = "${field.name}--empty", descr = "${field.name}: empty") {o.inputSetValue(field.name, "")})
//            add(TestAttempt(subID = "${field.name}-shit", descr = "${field.name}: shit") {o.inputSetValue(field.name, "shit")})
//            add(TestAttempt(subID = "${field.name}-valid", descr = "${field.name}: valid") {o.inputSetValue(field.name, validValue)})
//        }
//
//        TextFieldType.PHONE -> imf("badTextFieldValuesThenValid for PHONE")
//
//    }
//}

fun eachOrCombinationOfLasts(chunks: List<List<TestAttempt2>>): List<TestAttempt2> =
    if (!testOpts().skipRambling) {
        flatten(chunks)
    } else {
        val lastItem = chunks.last().last()
        listOf(TestAttempt2(
            subID = lastItem.subID,
            descr = lastItem.descr,
            beforeSubmit = lastItem.beforeSubmit,
            prepare = {async{
                for (chunk in chunks) {
                    await(chunk.last().prepare())
                }
            }}
        ))
    }

fun eachOrLast(attempts: List<TestAttempt2>): List<TestAttempt2> =
    if (!testOpts().skipRambling) {
        attempts
    } else {
        listOf(attempts.last())
    }




















