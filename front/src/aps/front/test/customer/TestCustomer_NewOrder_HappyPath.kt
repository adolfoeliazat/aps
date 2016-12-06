package aps.front

import aps.front.testutils.*

class TestCustomer_NewOrder_HappyPath : StepBasedTestScenario() {
    val shit = TestShit()

    override fun buildSteps() {
        o.prepareBobul(shit)
        o.initFuckingBrowser(fillStorageLocal = {
            it.token = shit.bobulToken
        })
        o.kindaNavigateToStaticContent("$TEST_URL_CUSTOMER/orders.html")
        o.assertCustomerBreatheScreen()

        o.acta {async{
            val world = World("boobs")
            await(world.boot())
        }}

        o.assertScreenHTML("1", "9d4f20ac-0d7f-494c-bd94-807a4afdd2c5")
        o.clickDescribingStep("button-plus")
        o.assertScreenHTML("2", "44a46713-621c-4a69-b6e6-b09db0965f25")
//        o.setValueDescribingStep("IntField-numPages.Input", "qwe")
//        o.clickDescribingStep("button-primary")
    }
}





