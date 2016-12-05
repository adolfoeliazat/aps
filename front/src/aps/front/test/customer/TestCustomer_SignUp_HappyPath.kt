package aps.front

import aps.front.testutils.*

class TestCustomer_SignUp_HappyPath : StepBasedTestScenario() {
    override fun buildSteps() {
        o.initFuckingBrowser()
        o.kindaNavigateToStaticContent(TEST_URL_CUSTOMER)
        o.acta {async{
            val world = World(name)
            await(world.boot())
        }}
        o.assertCustomerIndexHTML()
    }
}



