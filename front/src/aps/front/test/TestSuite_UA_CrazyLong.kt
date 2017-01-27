package aps.front

import aps.*
import aps.front.testutils.*
import into.kommon.*

class TestSuite_UA_CrazyLong : TestSuite {
    override val shortDescription = null

    override val scenarios by lazy {listOf<TestScenario>(
        Test_UA_CrazyLong_1()
    )}
}

class Test_UA_TryOutBrowseroids : StepBasedTestScenario() {
    override fun buildSteps() {
        // o.initialShit(this)
        o.act {console.log("aaaaaaaaa")}
    }
}


class Test_UA_CrazyLong_1 : StepBasedTestScenario() {
    // http://aps-ua-writer.local:3022/faq.html?test=Test_UA_CrazyLong_1&stopOnAssertions=true&dontStopOnCorrectAssertions=true&animateUserActions=false&handPauses=true

    override fun buildSteps() {
        val ivo1 = MordaBuilder(o)

        o.initialShit(this)
        ivo1.init(
            MordaCoitizeParams(
                browseroidName = "ivo1",
                url = fconst.test.url.customer,
                fillTypedStorageLocal = {},
                fillRawStorageLocal = {}
            ),
            buildStaticAssertion = {o.assertAnonymousCustomerStaticIndexScreen()},
            buildDynamicAssertion = {o.assertAnonymousCustomerDynamicIndexScreen()}
        )
        o.topNavItemSequence(
            descr = "Click 'Sign In'",
            key = fconst.key.topNavItem.signIn.testRef,
            aid = "5d81d6de-36f1-4a5c-9259-8975f36d84b4"
        )

        o.linkSequence(descr = "Go to sign-up page", key = fconst.key.link.createAccount.testRef, aid = "b1765832-d2a9-47a4-a2ef-4f1a3f63d203")
        o.debugMailboxClear()
        o.acta {formSubmissionAttempts2(
            testShit,
            descr = "Sign-up attempts",
            baseID = "7418ea51-7b3c-4bc6-9404-7e4d513a00cb",
            attempts = eachOrCombinationOfLasts(listOf(
                badTextFieldValuesThenValid2(fieldSpecs.email_testRef, TestData.bobul.email),
                badTextFieldValuesThenValid2(fieldSpecs.firstName_testRef, TestData.bobul.firstName),
                badTextFieldValuesThenValid2(fieldSpecs.lastName_testRef, TestData.bobul.lastName),
                listOf(TestAttempt2(
                    subID = "${fieldSpecs.agreeTerms_testRef.name}-check",
                    descr = "Mark checkbox",
                    prepare = {
                        checkboxSet2(fieldSpecs.agreeTerms_testRef.name, true)
                    },
                    beforeSubmit = {
                        imposeNextGeneratedPassword2(TestData.bobul.password)
                    }
                ))
            ))
        )}
        o.debugMailboxCheck("c937779f-0870-4dec-a5a9-4ccaa791b934")

        o.acta {formSubmissionAttempts2(
            testShit,
            descr = "Sign-in attempts",
            baseID = "6a87ffac-4adf-4348-9894-f697bcb23268",
            attempts = eachOrLast(mutableListOf<TestAttempt2>()-{l->
                var subID = 1
                class Value(val value: String, val descr: String)
                val emails = listOf(
                    Value("", "empty"),
                    Value("shit", "malformed"),
                    Value("pizda@wrong-email.me", "incorrect"),
                    Value(TestData.bobul.email, "correct")
                )
                val passwords = listOf(
                    Value("", "empty"),
                    Value("shitty password", "incorrect"),
                    Value(TestData.bobul.password, "correct")
                )
                for (email in emails) {
                    for (password in passwords) {
                        l += TestAttempt2(
                            subID = "" + subID++,
                            descr = "Email: ${email.descr}; Password: ${password.descr}",
                            prepare = {async{
                                await(inputSetValue2(fieldSpecs.emailInSignInForm_testRef.name, email.value))
                                await(inputSetValue2(fieldSpecs.passwordInSignInForm_testRef.name, password.value))
                            }}
                        )
                    }
                }
            })
        )}

        o.beginWorkRegion()
        o.topNavItemSequence(
            descr = "Click 'My Orders'",
            key = fconst.key.topNavItem.orders.testRef,
            aid = "5305390c-3f36-4c60-b43d-7f127ace28a1"
        )

        o.acta {sequence_openPlusForm("a46f92d5-e07d-41cf-bd9d-6559375ca7ad")}

        o.acta {tillEndOfTime()}
    }
}



