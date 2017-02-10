package aps.front

import aps.*
import aps.front.*

data class TestOptions(
    val stopOnAssertions: Boolean = false,
    val dontStopOnCorrectAssertions: Boolean = false,
    val animateUserActions: Boolean = false,
    val slowdown: Int = 1,
    val handPauses: Boolean = false,
    val ignoreNotHardened: Boolean = false,
    val ignoreIncorrect: Boolean = false,
    val skipRambling: Boolean = false,
    val addTestOffClassSuffixes: Boolean = false,
    val fastFileUpload: Boolean = false
) {
    fun toURLQuery(): String {
        return buildString {
            append(const.urlq.test.stopOnAssertions + "=" + stopOnAssertions)
            append("&" + const.urlq.test.dontStopOnCorrectAssertions + "=" + dontStopOnCorrectAssertions)
            append("&" + const.urlq.test.animateUserActions + "=" + animateUserActions)
            append("&" + const.urlq.test.handPauses + "=" + handPauses)
        }
    }

    companion object {
        fun load(urlQuery: Map<String, String>): TestOptions {
            var res = TestOptions(
                stopOnAssertions = urlQuery[const.urlq.test.stopOnAssertions].relaxedToBoolean(default = false),
                dontStopOnCorrectAssertions = urlQuery[const.urlq.test.dontStopOnCorrectAssertions].relaxedToBoolean(default = false),
                animateUserActions = urlQuery[const.urlq.test.animateUserActions].relaxedToBoolean(default = false),
                handPauses = urlQuery[const.urlq.test.handPauses].relaxedToBoolean(default = false)
            )

            return res
        }
    }
}

class TestOptionsTemplate(val title: String, val opts: TestOptions)

object TestOptionsTemplates {
    val slower = TestOptionsTemplate("Slower", TestOptions(stopOnAssertions = true, dontStopOnCorrectAssertions = false, animateUserActions = true, slowdown = 2, handPauses = true))
    val fastest = TestOptionsTemplate("Fastest", TestOptions(stopOnAssertions = false, dontStopOnCorrectAssertions = false, animateUserActions = false, slowdown = 1, handPauses = false))
    val fastestIgnoreNotHardened = TestOptionsTemplate("Fastest, ignore not-hardened assertions", TestOptions(stopOnAssertions = false, dontStopOnCorrectAssertions = false, animateUserActions = false, slowdown = 1, handPauses = false, ignoreNotHardened = true))
    val fastestExceptShowBannerOnNonCorrectAssertions = TestOptionsTemplate("Fastest except show banner on non-correct assertions", TestOptions(stopOnAssertions = true, dontStopOnCorrectAssertions = true, animateUserActions = false, slowdown = 1, handPauses = false, skipRambling = true, addTestOffClassSuffixes = true, fastFileUpload = true))

    val all = setOf(
        TestOptionsTemplate("Default", TestOptions()),
        fastest,
        fastestExceptShowBannerOnNonCorrectAssertions,
        TestOptionsTemplate("Slow", TestOptions(stopOnAssertions = true, dontStopOnCorrectAssertions = false, animateUserActions = true, slowdown = 1, handPauses = true)),
        TestOptionsTemplate("Stop on assertions", TestOptions(stopOnAssertions = true)),
        TestOptionsTemplate("Stop on assertions, animate", TestOptions(stopOnAssertions = true, animateUserActions = true)),
        TestOptionsTemplate("Stop on assertions except correct", TestOptions(stopOnAssertions = true, dontStopOnCorrectAssertions = true)),
        TestOptionsTemplate("Stop on assertions except correct, animate", TestOptions(stopOnAssertions = true, dontStopOnCorrectAssertions = true, animateUserActions = true)),
        TestOptionsTemplate("Stop on assertions except correct, animate, hand pauses", TestOptions(stopOnAssertions = true, dontStopOnCorrectAssertions = true, animateUserActions = true, handPauses = true)),
        slower
    )
}

val oneOffTestOptionsTemplateTitle: String? by lazy {
    Globus.realTypedStorageLocal.oneOffTestOptionsTemplateTitle?.let {
        Globus.realTypedStorageLocal.oneOffTestOptionsTemplateTitle = null
        it
    }
}

@Suppress("UnsafeCastFromDynamic")
fun testOpts(): TestOptions {
    TestGlobal.forcedTestOpts?.let {return it}

    oneOffTestOptionsTemplateTitle?.let {requested->
        return TestOptionsTemplates.all.find {it.title == requested}!!.opts}

    return TestGlobal.lastTestOpts
}




