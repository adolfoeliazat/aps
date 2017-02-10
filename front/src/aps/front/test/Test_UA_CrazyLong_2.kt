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

            val startPoint = 2
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
                    // TODO:vgrechka Assert only modal's content, reuse assertion
                    sequence({tabClick(fconst.tab.order.files.testRef)},
                             steps = listOf(
                                 PauseAssertResumeStep(TestGlobal.switchTabHalfwayLock, "f727a9ea-c520-4613-97e0-c154f6506d3a"),
                                 PauseAssertResumeStep(TestGlobal.switchTabDoneLock, "f621673e-7f84-4a53-969f-8844614c4f30")))
                    addFile(fileName = "little pussy.rtf", title = "Задание на практику", details = "- Вам следовало остаться у себя в комнате! Разве Франц вам ничего не говорил?\n\n- Что вам, наконец, нужно? - спросил К., переводя взгляд с нового посетителя на того,  кого  назвали  Франц  (он  стоял  в дверях),  и  снова  на первого.  В  открытое окно видна была та старуха: в припадке старческого любопытства она уже  перебежала к другому окну - посмотреть, что дальше.", aid = "4c30a4f4-5bee-426a-ab7f-960367b2c198")
                    addFile(fileName = "crazy monster boobs.rtf", title = "Шаблон отчета", details = "Меньше всего К. боялся, что  его  потом упрекнут  в  непонимании  шуток,  зато он отлично помнил - хотя обычно с прошлым опытом и не считался - некоторые случаи,  сами по  себе  незначительные,  когда  он  в отличие от своих друзей сознательно пренебрегал возможными  последствиями  и  вел  себя крайне  необдуманно  и  неосторожно,  за  что  и  расплачивался полностью. Больше этого с ним повториться не  должно,  хотя  бы теперь,  а если это комедия, то он им подыграет. Но пока что он еще свободен.", aid = "167277d8-dcae-4de6-ae6c-a39f1981e6f5")
                    addFile(fileName = "pussy story.rtf", title = "Методичка какая-то левая", details = "В комнате он тотчас же стал выдвигать ящики стола; там был образцовый  порядок,  но  удостоверение  личности,  которое  он искал, он от волнения никак найти  не  мог.  Наконец  он  нашел удостоверение на велосипед и уже хотел идти с ним к стражам, но потом  эта  бумажка  показалась  ему неубедительной, и он снова стал искать, пока не нашел свою метрику.", aid = "54b2bb55-17fe-44e6-98b2-c631933c17ea")
                    addFile(fileName = "shitty document 1.rtf", title = "Let Over Lambda", details = "Let Over Lambda is one of the most hardcore computer programming books out there. Starting with the fundamentals, it describes the most advanced features of the most advanced language: Common Lisp. Only the top percentile of programmers use lisp and if you can understand this book you are in the top percentile of lisp programmers. If you are looking for a dry coding manual that re-hashes common-sense techniques in whatever langue du jour, this book is not for you. This book is about pushing the boundaries of what we know about programming. While this book teaches useful skills that can help solve your programming problems today and now, it has also been designed to be entertaining and inspiring. If you have ever wondered what lisp or even programming itself is really about, this is the book you have been looking for.", aid = "89209597-11ef-4d78-8575-bd39bf6fca26")
                    addFile(fileName = "shitty document 2.rtf", title = "Land of Lisp", details = " Lisp has been hailed as the world’s most powerful programming language, but its cryptic syntax and academic reputation can be enough to scare off even experienced programmers. Those dark days are finally over—Land of Lisp brings the power of functional programming to the people!\n\nWith his brilliantly quirky comics and out-of-this-world games, longtime Lisper Conrad Barski teaches you the mysteries of Common Lisp. You’ll start with the basics, like list manipulation, I/O, and recursion, then move on to more complex topics like macros, higher order programming, and domain-specific languages. Then, when your brain overheats, you can kick back with an action-packed comic book interlude! ", aid = "5ac8d7c0-bd39-4e12-bdfa-451074af8f4d")
                }
            }
        }
    }

    private suspend fun addFile(fileName: String, title: String, details: String, aid: String) {
        seq({buttonClick(fconst.key.button.plus.testRef)}, TestGlobal.modalShownLock, "$aid--3")
        fileFieldChoose(fileName, "$aid--4")
        inputSetValue(fieldSpecs.shebang.title.testRef, title)
        inputSetValue(fieldSpecs.shebang.details.testRef, details)
        submitFormSequence(testShit, aid = "$aid--5")
    }
}



//send(TestCodeFiddleRequest()-{it.what.value = "fuck1"}) //; waitTillEndOfTime()











