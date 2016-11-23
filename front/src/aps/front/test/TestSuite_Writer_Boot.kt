package aps.front

class TestSuite_Writer_Boot : TestSuite {
    override val shortDescription = "Shitty writer boot sequences"

    override val scenarios by lazy {listOf(
        TestWriter_Boot_NoToken(),
        TestWriter_Boot_InvalidToken(),
        TestWriter_Boot_Cool(),
        TestWriter_Boot_ProfilePending(),
        TestWriter_Boot_ProfilePending_CanUseDashboardButOnlyForAccountManagement(),
        TestWriter_Boot_ProfileApprovalPending(),
        TestWriter_Boot_ProfileApprovalPending_CanUseDashboardButOnlyForAccountManagement(),
        TestWriter_Boot_ProfileRejected(),
        TestWriter_Boot_ProfileRejected_CanUseDashboardButOnlyForAccountManagement(),
        TestWriter_Boot_Banned(),
        TestWriter_Boot_Banned_CanUseDashboardButOnlyToSignOut()
    )}
}



