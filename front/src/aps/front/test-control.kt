package aps.front

suspend fun ___stopHereAndEverywhereAfter(verticalPosition: VerticalPosition? = null, horizontalPosition: HorizontalPosition? = null) {
    ___stopEverywhere()
    describeState("Doing stopHereAndEverywhereLater()...", verticalPosition = verticalPosition, horizontalPosition = horizontalPosition)
}

fun ___stopEverywhere() {
    TestGlobal.skipAllFreakingAssertions = false
    TestGlobal.forcedTestOpts = testOpts().copy(
        stopOnAssertions = true,
        dontStopOnCorrectAssertions = false
    )
    TestGlobal.describeStateConfig = DescribeStateConfig(showBanners = true, autoResumeAfterMs = null)
}

fun ___slowAssertionsEverywhereAfter() {
    TestGlobal.forcedTestOpts = testOpts().copy(
        sleepAfterEachAssertionMs = 2000
    )
}

fun ___showStateDescriptions() {
    TestGlobal.describeStateConfig = DescribeStateConfig(showBanners = true, autoResumeAfterMs = null)
}

fun ___showStateDescriptionsButAutoResume() {
    TestGlobal.describeStateConfig = DescribeStateConfig(showBanners = true, autoResumeAfterMs = 5000)
}
