package aps.front

import aps.front.testutils.*
import kotlin.browser.window

fun isTest(): Boolean = TestGlobal.testRunning

fun responseProcessed() {
    TestGlobal.responseProcessedSignal.resolve()
}

fun TestScenarioBuilder.submitSignInForm(shit: TestShit, userData: TestUserData, descr: String, aid: String) {
    inputSetValue(fconst.key.formInput.email.testRef, userData.email)
    inputSetValue(fconst.key.formInput.password.testRef, userData.password)
    submitForm(
        shit,
        descr = descr,
        aid = aid)
}
