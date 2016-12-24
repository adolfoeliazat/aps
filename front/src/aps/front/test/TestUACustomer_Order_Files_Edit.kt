package aps.front

import aps.front.testutils.*

class TestUACustomer_Order_Files_Edit : StepBasedTestScenario() {
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
                    setUpFiles = {oid->
                        async {
                            await(setUpFilesByBobul_1(shit, oid))
                            await(setUpFilesByFedor_1(shit, oid))
                            await(setUpFilesByBobul_2(shit, oid))
                            await(setUpFilesByFedor_2(shit, oid))
                        }
                    })
            },
            assertScreen = {o.assertScreenHTML("1", "ecec02be-35ab-4c44-afc4-a50b73d45b1c")}
        )

        o.act {jqbody.scrollTop(700)}
        o.kicClick("edit-0-3")

        o.act {jqbody.scrollTop(900)}
        o.kicClick("edit-0-5")
    }

}
