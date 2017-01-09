package aps.front

import aps.*
import aps.front.*

data class TestOptions(
    val stopOnAssertions: Boolean = false,
    val dontStopOnCorrectAssertions: Boolean = false,
    val animateUserActions: Boolean = false,
    val slowdown: Int = 1,
    val handPauses: Boolean = false
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
    val slowish = TestOptionsTemplate("Slowish", TestOptions(stopOnAssertions = true, dontStopOnCorrectAssertions = false, animateUserActions = true, slowdown = 2, handPauses = true))

    val all = setOf(
        TestOptionsTemplate("Usual", TestOptions()),
        TestOptionsTemplate("Stop on assertions", TestOptions(stopOnAssertions = true)),
        TestOptionsTemplate("Stop on assertions, animate", TestOptions(stopOnAssertions = true, animateUserActions = true)),
        TestOptionsTemplate("Stop on assertions except correct", TestOptions(stopOnAssertions = true, dontStopOnCorrectAssertions = true)),
        TestOptionsTemplate("Stop on assertions except correct, animate", TestOptions(stopOnAssertions = true, dontStopOnCorrectAssertions = true, animateUserActions = true)),
        TestOptionsTemplate("Stop on assertions except correct, animate, hand pauses", TestOptions(stopOnAssertions = true, dontStopOnCorrectAssertions = true, animateUserActions = true, handPauses = true)),
        slowish
    )
}

val oneOffTestOptionsTemplateTitle: String? by lazy {
    Globus.realTypedStorageLocal.oneOffTestOptionsTemplateTitle?.let {
        Globus.realTypedStorageLocal.oneOffTestOptionsTemplateTitle = null
        it
    }
}

@Suppress("UnsafeCastFromDynamic")
fun testOpts(): TestOptions =
    oneOffTestOptionsTemplateTitle?.let {requested -> TestOptionsTemplates.all.find {it.title == requested}!!.opts}
    ?: TestGlobal.forcedTestOpts?.let {it}
    ?: TestGlobal.lastTestOpts




