package aps.front

import aps.*
import kotlin.browser.document
import kotlin.browser.window

@Suppress("Unused")
fun dumpShames() {
    for ((shame, ctrl) in TestGlobal.shameToControl) {
        clog("Shame: $shame")
        // clog(ctrl)
    }
}

@Suppress("Unused")
fun dumpControls() {
    for (key in Input.instances.keys) clog("Input: $key")
    for (key in Button.instances.keys) clog("Button: $key")
    for (key in Checkbox.instances.keys) clog("Checkbox: $key")
    for (key in DateTimePicker.instances.keys) clog("DateTimePicker: $key")
    for (key in FileField.instances.keys) clog("FileField: $key")
    for (key in Select.instances.keys) clog("Select: $key")
    for (key in kic.instances.keys) clog("kic: $key")
}

fun igniteDebugShit(): Promise<Unit> = DebugShitToIgnite.skipCorrectAssertions()
private object DebugShitToIgnite {
    fun nothing() = async {}

    fun skipNonFailedAssertions() = skipAssertionsUntil {currentAssertionBannerKind == AssertionBannerKind.INCORRECT}
    fun skipCorrectAssertions() = skipAssertionsUntil {currentAssertionBannerKind != AssertionBannerKind.CORRECT}

    private fun skipAssertionsUntil(condition: () -> Boolean): Promise<Unit> {
        return async<Unit> {
            while (true) {
                await(tillPausedOnAssertion())
                if (condition())
                    break
                resumePausedAssertion()
            }
        }
    }

    fun shit1() = async {
        await(tillBodyHTMLContains("Assertion: Customer breathe screen"))
        Button.instance("assertionBanner-play").click()
        await(tillBodyHTMLContains("Assertion: 1"))
        Button.instance("assertionBanner-vdiff").click()
    }

    fun shit2() = async {
        await(tillBodyHTMLContains("Assertion: Customer breathe screen"))
        Button.instance("assertionBanner-play").click()
        await(tillBodyHTMLContains("Assertion: 1"))
//        Button.instance("assertionBanner-accept").click()
    }
}

@Suppress("Unused")
fun runLastTest() {
    val url = Globus.realTypedStorageLocal.lastTestURL!!
    window.location.href = url
}

private fun tillBodyHTMLContains(needle: String): Promise<Unit> = async {
    while (true) {
        if (document.body!!.innerHTML.contains(needle))
            break
        else
            await(delay(500))
    }
}

fun blowUp() {
    throw Exception("fuck you")
}







