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

        initialTestShit(this)
        ivo1.coitizeAndBootAsserting(assertStatic = {assertAnonymousCustomerStaticIndexScreen()},
                                     assertDynamic = {assertAnonymousCustomerDynamicIndexScreen()})

    }
}



