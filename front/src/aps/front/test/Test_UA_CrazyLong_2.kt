package aps.front

import aps.*
import aps.front.testutils.*

class Test_UA_CrazyLong_2 : FuckingScenario() {
    // http://aps-ua-writer.local:3022/faq.html?test=Test_UA_CrazyLong_2&stopOnAssertions=true&dontStopOnCorrectAssertions=true&animateUserActions=false&handPauses=true

    override suspend fun run1() {
        val ivo1 = Morda("ivo1",
                         url = fconst.test.url.customer,
                         fillTypedStorageLocal = {},
                         fillRawStorageLocal = {})

        forceFast {
            initialTestShit(this)
            ivo1.coitizeAndBootAsserting(assertStatic = {assertAnonymousCustomerStaticIndexScreen()},
                                         assertDynamic = {assertAnonymousCustomerDynamicIndexScreen()})
            topNavItemSequence(page = pages.uaCustomer.makeOrder_testRef,
                               aid = "00c34b38-a47d-4ae5-a8f3-6cceadb0d481")
            debugMailboxClear()
            formSubmissionAttempts(
                testShit,
                descr = "Make order (no sign-in)",
                baseID = "c31b6b5e-aac1-4136-8bef-906cf5be8cdc",
                attempts = eachOrCombinationOfLasts(listOf(
                    badTextFieldValuesThenValid(fieldSpecs.documentTitle_testRef, "Как я пинал хуи на практике")
    //                badTextFieldValuesThenValid(fieldSpecs.firstName_testRef, TestData.bobul.firstName),
    //                badTextFieldValuesThenValid(fieldSpecs.lastName_testRef, TestData.bobul.lastName),
    //                listOf(TestAttempt(
    //                    subID = "${fieldSpecs.agreeTerms_testRef.name}-check",
    //                    descr = "Mark checkbox",
    //                    prepare = {
    //                        checkboxSet2(fieldSpecs.agreeTerms_testRef.name, true)
    //                    }
    //                ))
                ))
            )
            debugMailboxCheck("b9196719-9e01-45f3-987c-cb8259c7f9e6")
        }

    }
}



