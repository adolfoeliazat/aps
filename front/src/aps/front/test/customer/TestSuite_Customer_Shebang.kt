package aps.front

class TestSuite_Customer_Shebang : TestSuite {
    override val shortDescription = "Customer tests shebang"

    override val scenarios by lazy {listOf<TestScenario>(
        TestCustomer_SignUp_HappyPath(),
        TestUACustomer_NewOrder_HappyPath()
    )}
}


