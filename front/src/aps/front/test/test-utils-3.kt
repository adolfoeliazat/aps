package aps.front

import aps.*
import aps.front.testutils.*
import into.kommon.*
import kotlin.browser.window

fun isTest(): Boolean = TestGlobal.testRunning

fun responseProcessed() {
    TestGlobal.responseProcessedSignal.resolve()
}

fun TestScenarioBuilder.submitSignInForm(shit: TestShit, userData: TestUserData, descr: String, aid: String) {
    imf("reimplement submitSignInForm")
//    inputSetValue(fieldSpecs.email_testRef.name, userData.email)
//    inputSetValue(fieldSpecs.password_testRef.name, userData.password)
//    submitFormSequence(shit, descr = descr, aid = aid)
}
