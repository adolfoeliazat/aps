package aps.front

import kotlin.browser.window

fun isTest(): Boolean =
    TestGlobal.lastTestMaybe != null

