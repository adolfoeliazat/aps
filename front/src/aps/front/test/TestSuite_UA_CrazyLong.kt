package aps.front

import aps.*
import aps.front.testutils.*

class TestSuite_UA_CrazyLong : TestSuite {
    override val shortDescription = null

    override val scenarios by lazy {listOf<TestScenario>(
        Test_UA_CrazyLong_1(),
        Test_UA_CrazyLong_2()
    )}
}



