package aps.front

class TestSuite_Writer_Shebang : TestSuite {
    override val shortDescription = "Writer tests shebang"

    override val scenarios by lazy {listOf<TestScenario>(
        TestWriter_SignUp_HappyPath(),
        TestWriter_SignUp_SignUpFormErrors()
    )}
}


