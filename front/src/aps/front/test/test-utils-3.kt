package aps.front

fun isTest(): Boolean =
    TestGlobal.lastTestMaybe != null

fun testOpts(): TestRunnerOptions =
    TestGlobal.forcedTestOpts?.let {it} ?: TestGlobal.lastTestOpts


