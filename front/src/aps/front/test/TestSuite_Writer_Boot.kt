package aps.front

import aps.front.test.TestWriter_Boot_Banned

class TestSuite_Writer_Boot : TestSuite {
    override val shortDescription = "Shitty writer boot sequences"

    override val scenarios by lazy {listOf(
        TestWriter_Boot_NoToken(),
        TestWriter_Boot_InvalidToken(),
        TestWriter_Boot_Cool(),
        TestWriter_Boot_ProfilePending(),
        TestWriter_Boot_ProfilePending_CanSignOut(),
        TestWriter_Boot_ProfileRejected(),
        TestWriter_Boot_ProfileRejected_CanSignOut(),
        TestWriter_Boot_Banned()
    )}
}



