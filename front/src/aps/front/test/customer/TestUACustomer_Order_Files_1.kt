package aps.front

import aps.*
import aps.front.testutils.*
import into.kommon.*
import into.mochka.assertEquals

class TestUACustomer_Order_Files_1 : StepBasedTestScenario() {
    val shit = TestShit()

    override fun buildSteps() {
        o.orderFiles1(shit)
        o.expectPieceOfShitDownload(PieceOfShitDownload(100001, "crazy monster boobs.rtf", forbidden = false)) {
            o.kicClick("download-1")
        }
    }
}






