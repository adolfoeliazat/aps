package aps.front

import aps.*
import aps.front.testutils.*

class Test_UA_CrazyLong_2 : FuckingScenario() {
    // http://aps-ua-writer.local:3022/faq.html?test=Test_UA_CrazyLong_2&stopOnAssertions=true&dontStopOnCorrectAssertions=true&animateUserActions=false&handPauses=true

    override suspend fun run1() {
        val testdata = object {
            val details = "Кто-то, по-видимому, оклеветал Йозефа К., потому  что,  не сделав   ничего  дурного,  он  попал  под  арест.\n\nКухарка  его квартирной хозяйки,  фрау  Грубах,  ежедневно  приносившая  ему завтрак около восьми, на этот раз не явилась. Такого случая еще не  бывало. К. немного подождал, поглядел с кровати на старуху, живущую напротив, - она смотрела из окна с  каким-то  необычным для  нее  любопытством - и потом, чувствуя и голод, и некоторое недоумение, позвонил. Тотчас же  раздался  стук,  и  в  комнату вошел  какой-то  человек. К. никогда раньше в этой квартире его не видел."
        }

        forceFast {
            initialTestShit(this)

            run { // Make order
                val ivo1 = Morda("ivo1",
                                 url = fconst.test.url.customer,
                                 fillTypedStorageLocal = {},
                                 fillRawStorageLocal = {})
                ivo1.coitizeAndBootAsserting(assertStatic = {assertAnonymousCustomerStaticIndexScreen()},
                                             assertDynamic = {assertAnonymousCustomerDynamicIndexScreen()})
                topNavItemSequence(page = pageSpecs.uaCustomer.makeOrder_testRef,
                                   aid = "00c34b38-a47d-4ae5-a8f3-6cceadb0d481")
                debugMailboxClear()
                selectSetValue(fieldSpecs.shebang.ua.documentType.testRef, UADocumentType.PRACTICE)
                imposeNextGeneratedConfirmationSecret("top-fucking-secret")
                formSubmissionAttempts(
                    testShit, baseID = "c31b6b5e-aac1-4136-8bef-906cf5be8cdc-1",
                    attempts = eachOrCombinationOfLasts(listOf(
                        badTextFieldValuesThenValid(fieldSpecs.shebang.documentTitle.testRef, "Как я пинал хуи на практике"),
                        badIntFieldValuesThenValid(fieldSpecs.shebang.numPages.testRef, 13),
                        badIntFieldValuesThenValid(fieldSpecs.shebang.numSources.testRef, 5),
                        badTextFieldValuesThenValid(fieldSpecs.shebang.documentDetails.testRef, testdata.details),
                        badTextFieldValuesThenValid(fieldSpecs.shebang.anonymousCustomerName.testRef, "Пися Камушкин"),
                        badTextFieldValuesThenValid(fieldSpecs.shebang.phone.testRef, "+38 (068) 123-45-67"),
                        badTextFieldValuesThenValid(fieldSpecs.shebang.email.testRef, "pisia@test.shit.ua")
                    ))
                )
                debugMailboxCheck("b9196719-9e01-45f3-987c-cb8259c7f9e6")
            }

            // TODO:vgrechka Try to access order page before it's confirmed

            run { // Wrong confirmation secret
                val ivo2 = Morda("ivo2",
                                 url = fconst.test.url.customer + "/confirmOrder.html?secret=wrong-secret",
                                 fillTypedStorageLocal = {},
                                 fillRawStorageLocal = {})
                ivo2.coitizeAndBootAsserting(assertStatic = {assertScreenHTML("Static confirmOrder", "7d46b2b1-e303-4146-9a3f-a89e02a1fe23")},
                                             assertDynamic = {assertScreenHTML("Dynamic confirmOrder", "45a5842e-ffe9-4e78-9261-9a5056a3c11f")})
            }

            run { // Correct confirmation secret
                val ivo3 = Morda("ivo3",
                                 url = fconst.test.url.customer + "/confirmOrder.html?secret=top-fucking-secret",
                                 fillTypedStorageLocal = {},
                                 fillRawStorageLocal = {})
                ivo3.coitizeAndBootAsserting(assertStatic = {assertScreenHTML("Static confirmOrder", "2acbad6a-e169-4c0d-9938-99fac621fef5")},
                                             assertDynamic = {assertScreenHTML("Dynamic confirmOrder", "a6a44d05-7c1d-4dbf-82a2-3b42e0ca98f3")})
            }

            while (true) {
                run { // Edit params -- cancel
                    step({buttonClick(fconst.key.button.edit.testRef)}, TestGlobal.modalShownLock, "9b32c20b-bcdb-4024-b068-5c6a36231944")
                    inputSetValue(fieldSpecs.shebang.documentTitle.testRef, "Хуй")
                    step({buttonClick(fconst.key.button.modal.cancel.testRef)}, TestGlobal.modalHiddenLock, "65da1c1a-7b2d-487e-a9cb-e99035eaa04b")
                }
                sleep(1000)
            }

            run { // Edit params -- save
                unforceFast()
                step({buttonClick(fconst.key.button.edit.testRef)}, TestGlobal.modalShownLock, "f0386438-99f7-417a-83a6-b29d804a1b1c")
                selectSetValue(fieldSpecs.shebang.ua.documentType.testRef, UADocumentType.LAB)
                formSubmissionAttempts(
                    testShit, baseID = "beaa5793-9590-415e-8bc9-ca6fec7ead52",
                    buttonKey = fconst.key.button.modal.ok.testRef,
                    attempts = eachOrCombinationOfLasts(listOf(
                        badTextFieldValuesThenValid(fieldSpecs.shebang.documentTitle.testRef, "Как я пинал большие хуи на практике"),
                        badIntFieldValuesThenValid(fieldSpecs.shebang.numPages.testRef, 23),
                        badIntFieldValuesThenValid(fieldSpecs.shebang.numSources.testRef, 7),
                        badTextFieldValuesThenValid(fieldSpecs.shebang.documentDetails.testRef, "Это чисто на почитать... " + testdata.details),
                        badTextFieldValuesThenValid(fieldSpecs.shebang.phone.testRef, "+38 (068) 321-45-67")
                    ))
                )
            }
        }
    }
}















//val key = fconst.key.button.edit_testRef
//val descr: String? = null
//val aid = "aa754a01-a99e-49d9-b982-db9ff87abf41"
//sequence(
//action = {
//    buttonClick(key)
//},
//descr = descr ?: "Click button $key",
//steps = listOf(
//PauseAssertResume(TestGlobal.openEditFormHalfwayLock, "$aid--1"),
//PauseAssertResume(TestGlobal.openEditFormDoneLock, "$aid--2")
//)
//)






