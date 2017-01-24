package aps.front

import aps.*
import aps.front.testutils.*
import into.kommon.*

class TestSuite_UA_CrazyLong : TestSuite {
    override val shortDescription = null

    override val scenarios by lazy {listOf<TestScenario>(
        Test_UA_CrazyLong_1()
    )}
}

class Test_UA_TryOutBrowseroids : StepBasedTestScenario() {
    override fun buildSteps() {
        // o.initialShit(this)
        o.act {console.log("aaaaaaaaa")}
    }
}


class Test_UA_CrazyLong_1 : StepBasedTestScenario() {
    override fun buildSteps() {
        val ivo1 = MordaBuilder(o)

        o.initialShit(this)
        ivo1.init(
            MordaCoitizeParams(
                browseroidName = "ivo1",
                url = fconst.test.url.customer,
                fillTypedStorageLocal = {},
                fillRawStorageLocal = {}
            ),
            buildStaticAssertion = {o.assertAnonymousCustomerStaticIndexScreen()},
            buildDynamicAssertion = {o.assertAnonymousCustomerDynamicIndexScreen()}
        )

        o.acta {tillEndOfTime()}


//        o.initialShit(this)
//
////        val ivo1 = TestBrowseroid(name = "ivo1", initialURL = fconst.test.url.customer)
//
//        o.section("Sign up Ivo Bobul") {
//            o.kindaNavigateToStaticContent(fconst.test.url.customer)
////            o.acta {ivo1.switchTo()}
//            o.assertCustomerStaticIndexScreen()
////            o.acta {ivo1.boot()}
////            o.boot(worldName = "ivo1", aid = "63b74778-5e05-4dd9-ba73-c5657a42f3f1")
//            o.assertScreenHTML("Booted ivo1", "63b74778-5e05-4dd9-ba73-c5657a42f3f1")
//
//            o.topNavItemSequence(descr = "Navigate sign-in page", key = fconst.key.topNavItem.signIn.testRef, aid = "c951e45d-3174-4d0f-8ec6-f4c4293754f1")
//            o.submitSignInForm(testShit, userData = TestData.bobul, descr = "Incorrect sign-in", aid = "1573955e-d896-4f9f-ad44-6068c9a25828")
//            o.linkSequence(descr = "Go to sign-up page", key = fconst.key.link.createAccount.testRef, aid = "7951049a-94a8-42c5-8d03-3111ccb396b1")
//            o.debugMailboxClear()
//            o.formSubmissionAttempts(
//                testShit,
//                descr = "Sign-up attempts",
//                baseID = "2ddc169e-8383-47d7-9b2b-bb0b669acc00",
//                buildAttempts = {a->
//                    a.prepareNothing()
//                    a.badTextFieldValuesThenValid(fieldSpecs.email_testRef, TestData.bobul.email)
//                    a.badTextFieldValuesThenValid(fieldSpecs.firstName_testRef, TestData.bobul.firstName)
//                    a.badTextFieldValuesThenValid(fieldSpecs.lastName_testRef, TestData.bobul.lastName)
//                    a.add(TestAttempt("${fieldSpecs.agreeTerms_testRef.name}-check") {o.checkboxSet(fieldSpecs.agreeTerms_testRef.name, true)})
//                })
////            o.debugMailboxCheck(aid = "4a553cfb-a7fd-45aa-9c9c-9ebe497a49f2")
//
////            o.assertEmailThenClear(
////                descr = "Got email with password",
////                expectedTo = "Иво Бобул <bobul@test.shit.ua>",
////                expectedSubject = "Пароль для APS",
////                expectedBody = """
////                    Привет, Иво!<br><br>
////                    Вот твой пароль: secret-big-as-fuck
////                    <br><br>
////                    <a href="http://aps-ua-customer.local:3012/sign-in.html">http://aps-ua-customer.local:3012/sign-in.html</a>
////                """)
//        }
//
//        o.beginWorkRegion()
    }

}

fun TestScenarioBuilder.formSubmissionAttempts(
    testShit: TestShit,
    descr: String,
    baseID: String,
    buildAttempts: (TestAttemptBuilder) -> Unit
) {
    val testAttemptBuilder = TestAttemptBuilder(this)
    buildAttempts(testAttemptBuilder)

    section(descr) {
        for ((i, attempt) in testAttemptBuilder.attempts.withIndex()) {
            attempt.buildPrepare()
            submitForm(
                testShit,
                descr = "Attempt ${attempt.id}",
                aid = "$baseID--${attempt.id}",
                imposeTimestamp = i == testAttemptBuilder.attempts.lastIndex)
        }
    }
}

class TestAttempt(
    val id: String,
    val buildPrepare: () -> Unit
)

class TestAttemptBuilder(val o: TestScenarioBuilder) {
    private val _attempts = mutableListOf<TestAttempt>()

    val attempts get() = _attempts.toList()

    fun add(attempt: TestAttempt) {
        if (_attempts.any {it.id == attempt.id}) bitch("ID is already used: $${attempt.id}")
        _attempts += attempt
    }
}

fun TestAttemptBuilder.prepareNothing() {
    add(TestAttempt("prepareNothing") {})
}

fun TestAttemptBuilder.badTextFieldValuesThenValid(field: TextFieldSpec, validValue: String) {
    exhaustive/when (field.type) {
        TextFieldType.STRING, TextFieldType.PASSWORD, TextFieldType.TEXTAREA -> {
            if (field.minLen > 1) {
                add(TestAttempt("${field.name}--tooShort") {o.inputSetValue(field.name, TestData.generateShit(field.minLen - 1))})
            }
            add(TestAttempt("${field.name}--tooLong") {o.inputSetValue(field.name, TestData.generateShit(field.maxLen + 1))})
            add(TestAttempt("${field.name}--valid") {o.inputSetValue(field.name, validValue)})
        }

        TextFieldType.EMAIL -> {
            add(TestAttempt("${field.name}-shit") {o.inputSetValue(field.name, "shit")})
            add(TestAttempt("${field.name}-valid") {o.inputSetValue(field.name, validValue)})
        }

        TextFieldType.PHONE -> imf("badTextFieldValuesThenValid for PHONE")

    }
}



