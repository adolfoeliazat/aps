package aps.front

import aps.front.testutils.*

class TestSuite_UA_CrazyLong : TestSuite {
    override val shortDescription = null

    override val scenarios by lazy {listOf<TestScenario>(
        Test_UA_CrazyLong_1()
    )}
}

data class TestUserData(
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String
)

object TestData {
    val bobul = TestUserData(email = "bobul@test.shit.ua", firstName = "Иво", lastName = "Бобул", password = "bobulsecret")
}

class Test_UA_CrazyLong_1 : StepBasedTestScenario() {
    override fun buildSteps() {
        o.initialShit(this)

        o.section("Sign up Ivo Bobul") {
            o.kindaNavigateToStaticContent(fconst.test.url.customer)
            o.assertCustomerStaticIndexScreen()
            o.boot(worldName = "ivo1", aid = "63b74778-5e05-4dd9-ba73-c5657a42f3f1")

            o.topNavItemSequence(descr = "Navigate sign-in page", key = fconst.key.topNavItem.signIn.testRef, aid = "c951e45d-3174-4d0f-8ec6-f4c4293754f1")
            o.submitSignInForm(shit, userData = TestData.bobul, descr = "Incorrect sign-in", aid = "1573955e-d896-4f9f-ad44-6068c9a25828")
            o.linkSequence(descr = "Go to sign-up page", key = fconst.key.link.createAccount.testRef, aid = "7951049a-94a8-42c5-8d03-3111ccb396b1")
        }

        o.beginWorkRegion()
    }
}


