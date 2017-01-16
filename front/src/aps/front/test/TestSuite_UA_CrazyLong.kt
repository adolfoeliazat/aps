package aps.front

class TestSuite_UA_CrazyLong : TestSuite {
    override val shortDescription = null

    override val scenarios by lazy {listOf<TestScenario>(
        Test_UA_CrazyLong_1()
    )}
}

class Test_UA_CrazyLong_1 : TestUACustomer_Order_Files_Base() {
    override fun buildSteps0() {
        o.halt()
    }
}

