package aps.front

import aps.*
import org.w3c.dom.events.KeyboardEvent
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
    for (key in Input.instances.keys) clog("Input: ${key.fqn}")
    for (key in Button.instances.keys) clog("Button: ${key.fqn}")
    for (key in Checkbox.instances.keys) clog("Checkbox: ${key.fqn}")
    for (key in DateTimePicker.instances.keys) clog("DateTimePicker: $key")
    for (key in FileField.instances.keys) clog("FileField: ${key.name}")
    for (key in EnumSelect.instances.keys) clog("EnumSelect: ${key.fqn}")
    for (key in Select.instances.keys) clog("Select: ${key.fqn}")
    for (key in kic.instances.keys) clog("kic: ${key.fqn}")
    for (key in TopNavItem.instances.keys) clog("TopNavItem: " + key.belongsToFuckers.name + " :: " + key.path)
    for (key in Link.instances.keys) clog("Link: ${key.fqn}")
    for (key in Tabs2.instances.keys) clog("Tab: ${key.fqn}")
}

fun igniteDebugShit() {
    val captureVisualShitShortcut = true

    if (captureVisualShitShortcut) addWindowAltSomethingListener("KeyS") {
        val id = "sample"
        captureVisualShit(id)
        send(SaveCapturedVisualShitRequest())
        dwarnStriking("Saved shit: $id")
    }
}

fun addWindowAltSomethingListener(code: String, block: suspend () -> Unit) {
    window.addEventListener("keydown", {
        it as KeyboardEvent
        if (it.altKey && it.code == code) {
            preventAndStop(it)
            async {
                block()
            }
        }
    })
}

private object DebugShitToIgnite {
    fun nothing() = async {}

    fun skipNonFailedAssertions() = skipAssertionsUntil {currentAssertionBannerKind == TestBannerKind.INCORRECT}
    fun skipCorrectAssertions() = skipAssertionsUntil {currentAssertionBannerKind != TestBannerKind.CORRECT}

    private fun skipAssertionsUntil(condition: () -> Boolean): Promisoid<Unit> {
        return async<Unit> {
            while (true) {
                await(tillPausedOnAssertion())
                if (condition())
                    break
                resumeTestBannerPause()
            }
        }
    }

    fun shit1() = async {
        await(tillBodyHTMLContains("Assertion: Customer breathe screen"))
        buttonClick(buttons.assertionBanner.play_testRef)
        await(tillBodyHTMLContains("Assertion: 1"))
        buttonClick(buttons.assertionBanner.vdiff_testRef)
    }

    fun shit2() = async {
        await(tillBodyHTMLContains("Assertion: Customer breathe screen"))
        buttonClick(buttons.assertionBanner.play_testRef)
        await(tillBodyHTMLContains("Assertion: 1"))
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

fun blowUpAndShowMappedStack() {
    fun fuck() {
        fun shit() {
            throw Exception("bitch")
        }
        shit()
    }
    try {
        fuck()
    } catch(e: Throwable) {
        async {
            val stack = await(errorToMappedClientStackString(e))
            console.log("------------- Mapped stack -------------")
            console.log(stack)
        }
    }
}

fun haltShit() {

}






