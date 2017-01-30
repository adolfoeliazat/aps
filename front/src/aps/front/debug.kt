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
    for (key in TopNavItem.instances.keys) clog("TopNavItem: " + key.belongsToFuckers.name + " :: " + key.path)
    for (key in Link.instances.keys) clog("Link: $key")
}

fun igniteDebugShit(): Promisoid<Unit> = DebugShitToIgnite.nothing()
private object DebugShitToIgnite {
    fun nothing() = async {}

    fun skipNonFailedAssertions() = skipAssertionsUntil {currentAssertionBannerKind == AssertionBannerKind.INCORRECT}
    fun skipCorrectAssertions() = skipAssertionsUntil {currentAssertionBannerKind != AssertionBannerKind.CORRECT}

    private fun skipAssertionsUntil(condition: () -> Boolean): Promisoid<Unit> {
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
    Globus.realLocation.href = Globus.realTypedStorageLocal.lastTestURL!!
}

@Suppress("Unused")
fun runLastTestSuite() {
    Globus.realLocation.href = Globus.realTypedStorageLocal.lastTestSuiteURL!!
}

@Suppress("Unused")
fun showLastTestURL() {
    console.dir(Globus.realTypedStorageLocal.lastTestURL!!)
}

@Suppress("Unused")
fun showLastTestSuiteURL() {
    console.dir(Globus.realTypedStorageLocal.lastTestSuiteURL!!)
}

private fun tillBodyHTMLContains(needle: String): Promisoid<Unit> = async {
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

fun haltShit() {

}






