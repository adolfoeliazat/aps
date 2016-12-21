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
                        await(setUpFilesByBobul_2(shit, oid))
                    }})
            },
            assertScreen = {o.assertScreenHTML_todo("1", "0f77828b-fe22-4606-8f97-0efecc6fefde")}
        )

        o.halt()
        o.inputSetValue("search", "мышкин")
        o.inputPressEnter("search")
        o.assertScreenHTML_todo("2", "561577ab-ad4b-4bb5-bf1d-f50e9eac2c68")
    }
}






