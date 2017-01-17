package aps.front

import aps.front.testutils.*

class TestSuite_UA_CrazyLong : TestSuite {
    override val shortDescription = null

    override val scenarios by lazy {listOf<TestScenario>(
        Test_UA_CrazyLong_1()
    )}
}

class Test_UA_CrazyLong_1 : StepBasedTestScenario() {
    override fun buildSteps() {
        o.initialShit(this)
        o.kindaNavigateToStaticContent(fconst.test.url.customer)
        o.assertCustomerStaticIndexScreen()
        o.boot(worldName = "customer1", aid = "63b74778-5e05-4dd9-ba73-c5657a42f3f1")
    }
}


