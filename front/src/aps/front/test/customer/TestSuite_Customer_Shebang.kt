package aps.front

import aps.front.test.*

class TestSuite_Customer_Shebang : TestSuite {
    override val shortDescription = "Customer tests shebang"

    override val scenarios by lazy {listOf<TestScenario>(
        TestCustomer_SignUp_HappyPath(),
        TestUACustomer_NewOrder_HappyPath(),
        TestUACustomer_Order_Files_1(),
        TestUACustomer_DownloadForbiddenFile(),
        TestUACustomer_LongFileList(),
        TestUACustomer_Order_Files_Search(),
        TestUACustomer_Order_Files_EditFile(),
        TestUACustomer_Order_Files_EditMeta(),
        TestUACustomer_Order_Files_Misc()
    )}
}


