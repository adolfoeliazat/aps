package aps.front

import aps.*
import aps.front.testutils.*

class Test_UA_CrazyLong_1 : StepBasedTestScenario() {
    // http://aps-ua-writer.local:3022/faq.html?test=Test_UA_CrazyLong_1&stopOnAssertions=true&dontStopOnCorrectAssertions=true&animateUserActions=false&handPauses=true

    override fun buildSteps() {
        imf("Reimplement Test_UA_CrazyLong_1")
//        val ivo1 = Morda(
//            "ivo1",
//            url = fconst.test.url.customer,
//            fillTypedStorageLocal = {},
//            fillRawStorageLocal = {}
//        )
//
//        o.acts {
//            forceFast {
//                initialTestShit(this)
//                ivo1.coitizeAndBootAsserting(
//                    assertStatic = {assertAnonymousCustomerStaticIndexScreen()},
//                    assertDynamic = {assertAnonymousCustomerDynamicIndexScreen()}
//                )
//
//                topNavItemSequence(
//                    descr = "Click 'Sign In'",
//                    page = pageSpecs.uaCustomer.signIn_testRef,
//                    aid = "5d81d6de-36f1-4a5c-9259-8975f36d84b4"
//                )
//
//                linkSequence(descr = "Go to sign-up page", key = fconst.key.link.createAccount.testRef, aid = "b1765832-d2a9-47a4-a2ef-4f1a3f63d203")
//
//                debugMailboxClear()
//                imposeNextGeneratedPassword2(TestData.bobul.password)
//                formSubmissionAttempts(
//                    testShit,
//                    descr = "Sign-up attempts",
//                    baseID = "7418ea51-7b3c-4bc6-9404-7e4d513a00cb",
//                    attempts = eachOrCombinationOfLasts(listOf(
//                        badTextFieldValuesThenValid(fieldSpecs.shebang.email_testRef, TestData.bobul.email),
//                        badTextFieldValuesThenValid(fieldSpecs.shebang.firstName_testRef, TestData.bobul.firstName),
//                        badTextFieldValuesThenValid(fieldSpecs.shebang.lastName_testRef, TestData.bobul.lastName),
//                        listOf(TestAttempt(
//                            subID = "${fieldSpecs.shebang.agreeTerms_testRef.it.name}-check",
//                            descr = "Mark checkbox",
//                            prepare = {
//                                checkboxSet2(fieldSpecs.shebang.agreeTerms_testRef.it.name, true)
//                            }
//                        ))
//                    ))
//                )
//                debugMailboxCheck("c937779f-0870-4dec-a5a9-4ccaa791b934")
//
//                formSubmissionAttempts(
//                    testShit,
//                    descr = "Sign-in attempts",
//                    baseID = "6a87ffac-4adf-4348-9894-f697bcb23268",
//                    attempts = eachOrLast(mutableListOf<TestAttempt>() - {l ->
//                        var subID = 1
//
//                        class Value(val value: String, val descr: String)
//
//                        val emails = listOf(
//                            Value("", "empty"),
//                            Value("shit", "malformed"),
//                            Value("pizda@wrong-email.me", "incorrect"),
//                            Value(TestData.bobul.email, "correct")
//                        )
//                        val passwords = listOf(
//                            Value("", "empty"),
//                            Value("shitty password", "incorrect"),
//                            Value(TestData.bobul.password, "correct")
//                        )
//                        for (email in emails) {
//                            for (password in passwords) {
//                                l += TestAttempt(
//                                    subID = "" + subID++,
//                                    descr = "Email: ${email.descr}; Password: ${password.descr}",
//                                    prepare = {
//                                        inputSetValue(fieldSpecs.shebang.emailInSignInForm_testRef.it.name, email.value)
//                                        inputSetValue(fieldSpecs.shebang.passwordInSignInForm_testRef.it.name, password.value)
//                                    }
//                                )
//                            }
//                        }
//                    })
//                )
//
//                topNavItemSequence(
//                    descr = "Click 'My Orders'",
//                    page = pageSpecs.uaCustomer.orders_testRef,
//                    aid = "5305390c-3f36-4c60-b43d-7f127ace28a1"
//                )
//            }
//
//            sequence_openPlusForm("a46f92d5-e07d-41cf-bd9d-6559375ca7ad")
//            selectSetValue(fieldSpecs.ua.documentType_testRef, UADocumentType.GRADUATION)
//            val topRight = AssertScreenOpts(bannerVerticalPosition = VerticalPosition.TOP, bannerHorizontalPosition = HorizontalPosition.RIGHT)
//            formSubmissionAttempts(
//                testShit,
//                descr = "Order creation attempts",
//                baseID = "ee5fa2d1-f3be-4f98-a06b-8e327146f65c",
//                attempts = eachOrCombinationOfLasts(listOf(
//                    badTextFieldValuesThenValid(fieldSpecs.title_testRef, "Особенности перевода правовых документов на основе брачного контракта"),
//                    badIntFieldValuesThenValid(fieldSpecs.numPages_testRef, 250),
//                    badIntFieldValuesThenValid(fieldSpecs.numSources_testRef, 10),
//                    badTextFieldValuesThenValid(fieldSpecs.details_testRef, aopts = topRight, validValue = "На протяжении жизни человек сталкивается с различными ситуациями, требующими обращения к профессиональному юристу.\n\nЗачастую документацию приходится воспроизводить на иностранном языке, например, для получения рабочей вакансии за границей, поступления в зарубежный вуз, успешного проведения деловых операций, приобретения права проживания в том или ином государстве.\n\nВ этом случае не обойтись без услуг профессионального переводчика. Перевод документов физических и юридических лиц можно охарактеризовать, как \"перевод текстов, относящихся к области права и используемых для обмена юридической информацией между людьми, говорящими на разных языках\".\n\nПоскольку право является предметной областью, связанной с социально-политическими и культурными особенностями страны, юридический перевод правовых документов представляет собой непростую задачу, считается одним из самых сложных видов перевыражения текста в среде переводчиков, т.к. для адекватной передачи юридической информации язык юридического перевода должен быть особо точным, ясным и достоверным."),
//                    badTextFieldValuesThenValid(field = fieldSpecs.phone_testRef, aopts = topRight, validValue = "+3 8 (1) 23 45(6) 7-8 910-1112")
//                ))
//            )
//
//            waitTillEndOfTime()
//        }
    }
}