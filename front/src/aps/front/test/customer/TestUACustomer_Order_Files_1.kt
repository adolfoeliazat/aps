package aps.front

import aps.front.testutils.*
import into.kommon.*
import into.mochka.assertEquals

class TestUACustomer_Order_Files_1 : StepBasedTestScenario() {
    val shit = TestShit()

    override fun buildSteps() {
        o.orderFiles1(shit)
        o.expectDownloadedFile("100001") {
            o.kicClick("download-1")
        }
    }
}






