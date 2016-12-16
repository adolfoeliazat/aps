package aps.front

import aps.front.testutils.*

class TestUACustomer_Order_Files_1 : StepBasedTestScenario() {
    val shit = TestShit()

    override fun buildSteps() {
        o.orderFiles1(shit)
        o.kicClick("download-1")
    }
}






