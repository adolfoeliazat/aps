package aps.front

import aps.*

suspend fun ___stopHereAndEverywhereAfter(verticalPosition: VerticalPosition? = null, horizontalPosition: HorizontalPosition? = null) {
    ___stopEverywhere()
    describeState("Doing stopHereAndEverywhereLater()...", verticalPosition = verticalPosition, horizontalPosition = horizontalPosition)
}

suspend fun ___stopHereAndEverywhereAfter_rightTop() {
    ___stopHereAndEverywhereAfter(verticalPosition = VerticalPosition.TOP, horizontalPosition = HorizontalPosition.RIGHT)
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

fun ___animateShit() {
    TestGlobal.forcedTestOpts = testOpts().copy(animateUserActions = true)
}

suspend fun describeState(descr: String, verticalPosition: VerticalPosition? = null, horizontalPosition: HorizontalPosition? = null) {
    val cfg = TestGlobal.describeStateConfig
    if (!cfg.showBanners) return

    val ctx = ShowTestBannerContext()-{o->
        o.verticalPosition = verticalPosition ?: VerticalPosition.BOTTOM
        o.horizontalPosition = horizontalPosition ?: HorizontalPosition.RIGHT
    }

    val autoResumeAfterMs = cfg.autoResumeAfterMs
    if (autoResumeAfterMs != null) {
        async {
            sleep(autoResumeAfterMs)
            resumeTestBannerPause()
        }
    }

    showTestBanner(ctx, title = descr, subtitle = "", kind = TestBannerKind.PAUSE)
}














