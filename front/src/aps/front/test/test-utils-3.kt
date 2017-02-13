package aps.front

import aps.*
import aps.front.testutils.*
import into.kommon.*
import kotlin.browser.window

fun isTest(): Boolean = TestGlobal.testRunning

fun TestScenarioBuilder.submitSignInForm(shit: TestShit, userData: TestUserData, descr: String, aid: String) {
    imf("reimplement submitSignInForm")
//    inputSetValue(fieldSpecs.email_testRef.name, userData.email)
//    inputSetValue(fieldSpecs.password_testRef.name, userData.password)
//    submitFormSequence(shit, descr = descr, aid = aid)
}

suspend fun navigate(url: String) {
    Globus.currentBrowseroid.location.pushState(null, "", url)
}

suspend fun vanishSequence(action: suspend () -> Unit, aid: String) {
    sequence(action,
             steps = listOf(
                 PauseAssertResumeStep(TestGlobal.fadeHalfwayLock, "$aid--1"),
                 PauseAssertResumeStep(TestGlobal.shitVanished, "$aid--2")
             ))
}

suspend fun condition(action: suspend () -> Unit, lock: TestLock) {
    lock.reset()
    action()
    lock.pauseTestFromTest()
}
