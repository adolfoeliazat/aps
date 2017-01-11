package aps.front

import aps.front.test.*

class TestSuite_UACustomer_Shebang : TestSuite {
    override val shortDescription = "Customer tests shebang"

    override val scenarios by lazy {listOf<TestScenario>(
        Test_UACustomer_SignUp_HappyPath(),
        Test_UACustomer_NewOrder_HappyPath()
    )}
}

