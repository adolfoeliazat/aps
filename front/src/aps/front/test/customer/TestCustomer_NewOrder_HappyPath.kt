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

        o.clickDescribingStep("button-plus")
        o.setValueDescribingStep("TextField-numPages.Input", "qwe")
        o.clickDescribingStep("button-primary")
    }
}





