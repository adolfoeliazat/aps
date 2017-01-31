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
            selectSetValue(fieldSpecs.ua.documentType_testRef, UADocumentType.PRACTICE)
            formSubmissionAttempts(
                testShit,
                descr = "Make order (no sign-in)",
                baseID = "c31b6b5e-aac1-4136-8bef-906cf5be8cdc",
                attempts = eachOrCombinationOfLasts(listOf(
                    badTextFieldValuesThenValid(fieldSpecs.documentTitle_testRef, "Как я пинал хуи на практике"),
                    badIntFieldValuesThenValid(fieldSpecs.numPages_testRef, 13),
                    badIntFieldValuesThenValid(fieldSpecs.numSources_testRef, 5),
                    badTextFieldValuesThenValid(fieldSpecs.documentDetails_testRef, "Кто-то, по-видимому, оклеветал Йозефа К., потому  что,  не сделав   ничего  дурного,  он  попал  под  арест.  Кухарка  его квартирной хозяйки,  фрау  Грубах,  ежедневно  приносившая  ему завтрак около восьми, на этот раз не явилась. Такого случая еще не  бывало. К. немного подождал, поглядел с кровати на старуху, живущую напротив, - она смотрела из окна с  каким-то  необычным для  нее  любопытством - и потом, чувствуя и голод, и некоторое недоумение, позвонил. Тотчас же  раздался  стук,  и  в  комнату вошел  какой-то  человек. К. никогда раньше в этой квартире его не видел."),
                    badTextFieldValuesThenValid(fieldSpecs.name_testRef, "Пися Камушкин"),
                    badTextFieldValuesThenValid(fieldSpecs.phone_testRef, "+38 (068) 123-45-67"),
                    badTextFieldValuesThenValid(fieldSpecs.email_testRef, "pisia@test.shit.ua")
                ))
            )
            debugMailboxCheck("b9196719-9e01-45f3-987c-cb8259c7f9e6")
        }

    }
}



