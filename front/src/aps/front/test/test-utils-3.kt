package aps.front

import kotlin.browser.window

fun isTest(): Boolean =
    TestGlobal.lastTestMaybe != null

fun responseProcessed() {
    TestGlobal.responseProcessedSignal.resolve()
}
