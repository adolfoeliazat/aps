package aps.front

import aps.*
import aps.front.testutils.*

// TODO:vgrechka Use paths from pageSpecs in URLs

class Test_UA_CrazyLong_2 : FuckingScenario() {
    // http://aps-ua-writer.local:3022/faq.html?test=Test_UA_CrazyLong_2&stopOnAssertions=true&dontStopOnCorrectAssertions=true&animateUserActions=false&handPauses=true

    override suspend fun run1() {
        val testdata = object {
            val details = "Кто-то, по-видимому, оклеветал Йозефа К., потому  что,  не сделав   ничего  дурного,  он  попал  под  арест.\n\nКухарка  его квартирной хозяйки,  фрау  Грубах,  ежедневно  приносившая  ему завтрак около восьми, на этот раз не явилась. Такого случая еще не  бывало. К. немного подождал, поглядел с кровати на старуху, живущую напротив, - она смотрела из окна с  каким-то  необычным для  нее  любопытством - и потом, чувствуя и голод, и некоторое недоумение, позвонил. Тотчас же  раздался  стук,  и  в  комнату вошел  какой-то  человек. К. никогда раньше в этой квартире его не видел."
        }

        TestGlobal.defaultAssertScreenOpts = AssertScreenOpts(
            bannerVerticalPosition = VerticalPosition.TOP,
            bannerHorizontalPosition = HorizontalPosition.RIGHT)

        forceFast {
            initialTestShit(this)

            val startPoint = 1
            var point = 0

            if (++point >= startPoint) {
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

                // TODO:vgrechka Try to access order page before it's confirmed (should say "fuck you" to user)

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

                run { // Edit params -- cancel
                    step({buttonClick(fconst.key.button.edit.testRef)}, TestGlobal.modalShownLock, "1_9b32c20b-bcdb-4024-b068-5c6a36231944")
                    inputSetValue(fieldSpecs.shebang.documentTitle.testRef, "Хуй")
                    step({buttonClick(fconst.key.button.cancel.testRef)}, TestGlobal.modalHiddenLock, "1_65da1c1a-7b2d-487e-a9cb-e99035eaa04b")
                }

                run { // Edit params -- save
                    step({buttonClick(fconst.key.button.edit.testRef)}, TestGlobal.modalShownLock, "f0386438-99f7-417a-83a6-b29d804a1b1c")
                    selectSetValue(fieldSpecs.shebang.ua.documentType.testRef, UADocumentType.LAB)
                    formSubmissionAttempts(
                        testShit, baseID = "3_beaa5793-9590-415e-8bc9-ca6fec7ead52",
                        attempts = eachOrCombinationOfLasts(listOf(
                            badTextFieldValuesThenValid(fieldSpecs.shebang.documentTitle.testRef, "Как я пинал большие хуи на практике"),
                            badIntFieldValuesThenValid(fieldSpecs.shebang.numPages.testRef, 23),
                            badIntFieldValuesThenValid(fieldSpecs.shebang.numSources.testRef, 7),
                            badTextFieldValuesThenValid(fieldSpecs.shebang.documentDetails.testRef, "Это чисто на почитать... " + testdata.details),
                            badTextFieldValuesThenValid(fieldSpecs.shebang.phone.testRef, "+38 (068) 321-45-67")
                        ))
                    )
                }

                run { // Edit params -- save 2
                    step({buttonClick(fconst.key.button.edit.testRef)}, TestGlobal.modalShownLock, "b556cf5e-0184-4ce0-8560-f083861116e7")
                    selectSetValue(fieldSpecs.shebang.ua.documentType.testRef, UADocumentType.PRACTICE)
                    submitFormSequence(testShit, aid = "6ea13411-892b-4e96-a1b8-c77b23e29567")
                }

                send(TestTakeDBSnapshotRequest()-{o->
                    o.snapshotName.value = "pizda-$point"
                    o.browseroidName.value = TestGlobal.currentMorda.browseroidName
                    o.href.value = Globus.currentBrowseroid.location.href
                    o.token.value = Globus.currentBrowseroid.typedStorageLocal.token
                })
            }

            if (++point >= startPoint) {
                if (point == startPoint) {
                    val state = send(TestRestoreDBSnapshotRequest()-{o->
                        o.snapshotName.value = "pizda-${point - 1}"
                    })
                    dlog("Snapshot response", state)
                    val ivo3 = Morda(state.browseroidName,
                                     url = state.href,
                                     fillTypedStorageLocal = {it.token = state.token},
                                     fillRawStorageLocal = {})
                    ivo3.coitizeAndBoot()
                }
                run { // Add some files
                    sequence({tabClick(fconst.tab.order.files.testRef)},
                             steps = listOf(
                                 PauseAssertResumeStep(TestGlobal.switchTabHalfwayLock, "f727a9ea-c520-4613-97e0-c154f6506d3a"),
                                 PauseAssertResumeStep(TestGlobal.switchTabDoneLock, "f621673e-7f84-4a53-969f-8844614c4f30")))
                    seq({buttonClick(fconst.key.button.plus.testRef)}, TestGlobal.modalShownLock, "314f08fe-960b-4e92-8454-28d3745a5c52")
                }
            }
        }
    }
}



//send(TestCodeFiddleRequest()-{it.what.value = "fuck1"}) //; waitTillEndOfTime()












