package aps.front

class TestSuite_Writer_Boot : TestSuite {
    override val shortDescription = "Shitty writer boot sequences"

    override val scenarios by lazy {listOf(
        TestWriter_Boot1(),
        TestWriter_Boot2(),
        TestWriter_Boot3(),
        TestWriter_Boot4()
    )}
}



