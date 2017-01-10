package aps.front

import kotlin.browser.window

fun isTest(): Boolean = TestGlobal.testRunning

fun responseProcessed() {
    TestGlobal.responseProcessedSignal.resolve()
}


