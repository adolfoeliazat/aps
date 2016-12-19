package aps.front

import aps.*
import aps.front.testutils.*
import into.kommon.*
import into.mochka.assertEquals

class TestUACustomer_Order_Files_Search : StepBasedTestScenario() {
    val shit = TestShit()

    override fun buildSteps() {
        o.setUpOrderFilesTestTemplate_1(
            shit,
            setUpUsers = {
                o.setUpBobul(shit)
                o.setUpFedor(shit)
            },
            setUpOrders = {
                o.setUpBobulOrder(
                    shit,
                    setUpFiles = {oid-> async {
                        await(setUpFilesByBobul_1(shit, oid))
                        await(setUpFilesByFedor_1(shit, oid))
                    }})
            },
            assertScreen = {o.todo("TestUACustomer_Order_Files_Search assertScreen")}
        )
    }
}






