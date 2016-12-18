package aps.front

import aps.*
import aps.front.testutils.*

class TestUACustomer_LongFileList : StepBasedTestScenario() {
    val shit = TestShit()

    override fun buildSteps() {
        o.setUpOrderFilesTestTemplate(
            shit,
            setUpOrders = {o.setUpBobulOrder(shit, {oid-> setUpFiles1(shit, oid)})},
            assertScreen = {o.todo("setUpOrderFiles1 assertScreen")})
    }
}


