package aps.front

class TestSuite_Writer_Boot : TestSuite {
    override val shortDescription = "Shitty writer boot sequences"

    override val scenarios by lazy {listOf(
        TestWriter_Boot_NoToken(),
        TestWriter_Boot_InvalidToken(),
        TestWriter_Boot_Cool(),
        TestWriter_Boot_ProfilePending(),
        TestWriter_Boot_ProfileRejected()
    )}
}



