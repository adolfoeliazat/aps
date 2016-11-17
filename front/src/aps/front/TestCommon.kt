/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.*
import jquery.jq
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.document
import kotlin.browser.window

interface TestHost {
    fun selectNewBrowserAndNavigate(name: String, url: String): Promise<Unit>
}

abstract class TestScenario {
    abstract fun run0(): Promise<Unit>

    open val name: String get() = ctorName(this)
    open val shortDescription: String? = null
    open val longDescription: String? = null

    lateinit var host: TestHost
    val testCommon by lazy { TestCommon(host) }

    fun run(): Promise<Unit> {"__async"
        measure("Resetting database") {
            __await(ResetTestDatabaseRequest.send(templateDB="test-template-ua-1", recreateTemplate=true))
        }
        return __reawait(run0())
    }
}

val testScenarios = mutableMapOf<String, TestScenario>()

fun tetete() {
    die()
}

class TestCommon(val sim: dynamic) {
    val LONG_SHIT_301 = makeLongShit(301)

    fun makeLongShit(len: Int): String {
        val bunchOfShit = buildString {
            while (length < len) {
                append("iamlongshit")
            }
        }

        return bunchOfShit.substring(0, len)
    }
}

fun jsFacing_igniteTestShit(): Promise<Unit> {"__async"
//    val urlObject = jshit.utils.url.parse(global.location.href)
//    val urlQuery = jshit.utils.querystring.parse(urlObject.query)
    val urlQuery = parseQueryString(global.location.href)

    for (name in jsArrayToList(Shitus.tokens("DEBUG_RPC_LAG_FOR_MANUAL_TESTS"))) {
        if (urlQuery[name] != undefined) {
            global[name] = urlQuery[name]
        }
    }

    val testScenarioToRun = urlQuery["test"] ?: bitch("Gimme test name")
//    val testScenarioToRun = urlQuery["testScenario"] ?: bitch("Gimme testScenario")

    hrss.preventScrollToBottomOnAssertionError = urlQuery["scrollToBottom"] == "no"
    hrss.preventExceptionRevelation = urlQuery["revealException"] == "no"
    hrss.preventUIAssertionThrowing = urlQuery["uiAssertionThrows"] == "no"
    art.testSpeed = urlQuery["testSpeed"] ?: "fast"
    hrss.alternativeTestSpeed = urlQuery["alternativeTestSpeed"]
    if (!hrss.alternativeTestSpeed) {
        if (art.testSpeed == "fast" || art.testSpeed == "medium") {
            hrss.alternativeTestSpeed = "slow"
        } else {
            hrss.alternativeTestSpeed = "medium"
        }
    }
    art.respectArtPauses = urlQuery["respectArtPauses"] == "yes"

    val sim = object:TestHost {
        override fun selectNewBrowserAndNavigate(name: String, url: String): Promise<Unit> {"__async"
            dlog("Selecting browser", name)
            hrss.browser = hrss.browsers.getOrPut(name) {Browser(name)}
            hrss.storageLocal = hrss.browser.storageLocal

            dlog("Navigating", hrss.browser.name, url)
            global.history.replaceState(null, "", url)
//            __await<dynamic>(makeCleanPairAndBoot())
            __await(World().boot())
            return __asyncResult(Unit)
        }
    }

    dwarnStriking(testScenarioToRun)
    val scenarioClass = eval("kot.aps.front.$testScenarioToRun") ?: bitch("No test scenario named [${testScenarioToRun}]")
    val scenario: TestScenario = eval("new scenarioClass()")
    scenario.host = sim

    global.DB = "aps-test"
    global.sessionStorage.setItem("DB", global.DB)

    val initialHref = window.location.pathname + window.location.search
    global.addEventListener("keydown", {e: KeyboardEvent ->
        if (e.altKey && e.code == "KeyR") {
            preventAndStop(e)
            window.location.href = initialHref
        }
    })

    hrss.currentTestScenario = scenario
    lastTestScenarioName = testScenarioToRun
    val oldHotCodeUpdateDisabled = hrss.hotCodeUpdateDisabled
    val oldLiveStatusPollingViaIntervalDisabled = hrss.liveStatusPollingViaIntervalDisabled

    // Prevent some unnecessary glithes
    hrss.hotCodeUpdateDisabled = false
    hrss.liveStatusPollingViaIntervalDisabled = false

    hrss.urlQueryBeforeRunningTest = getURLQuery()

    measure("Load generated shit") {
        eval(__await(GetGeneratedShitRequest.send()).code)
    }

    try {
        __await(scenario.run())
    } finally {
        hrss.hotCodeUpdateDisabled = oldHotCodeUpdateDisabled
        hrss.liveStatusPollingViaIntervalDisabled = oldLiveStatusPollingViaIntervalDisabled

        hrss.currentTestScenario = null
//        if (!hrss.preventRestoringURLAfterTest) {
//            global.setTimeout({ global.history.replaceState(null, "", initialHref) }, 1000)
//        }

        run { // XXX Refresh tethers
            jqbody.scrollTop(jqbody.scrollTop() + 1)
            jqbody.scrollTop(jqbody.scrollTop() - 1)
        }
    }

//    if (!art.halted) {
//        hrss.openTestPassedPaneArgs = json("scenario" to scenario)
//        openTestPassedPane(hrss.openTestPassedPaneArgs)
//    }

    return __asyncResult(Unit)
}

fun buildPieceOfTest(build: (PieceOfTestBuilder) -> Unit): Iterable<TestInstruction> {
    val items = mutableListOf<TestInstruction>()
    val builder = PieceOfTestBuilder(items)
    build(builder)

    return items
}

class PieceOfTestBuilder(val items: MutableList<TestInstruction>) {
    fun section(long: String, build: (TestSectionBuilder) -> Unit) {
        items.add(TestInstruction.BeginSection(long))

        val builder = TestSectionBuilder(items)
        build(builder)

        items.add(TestInstruction.EndSection())
    }
}

class TestSectionBuilder(val items: MutableList<TestInstruction>) {

    fun action(long: String, build: (TestActionBuilder) -> Unit) {
        items.add(TestInstruction.Step.ActionStep(long))

        val builder = TestActionBuilder(items)
        build(builder)
    }

    fun state(long: String, build: (TestStateBuilder) -> Unit) {
        items.add(TestInstruction.Step.StateStep(long))

        val builder = TestStateBuilder(items)
        build(builder)
    }

    operator fun Iterable<TestInstruction>.unaryPlus() {
        for (item in this) add(item)
    }

    operator fun TestInstruction.unaryMinus() {
        add(this)
    }

    fun add(item: TestInstruction) {
        items.add(item)
    }

}

class TestActionBuilder(val items: MutableList<TestInstruction>) {
    fun setValue(shame: String, value: String) {
        items.add(TestInstruction.SetValue(shame, value))
    }

    fun setValue(shame: String, value: Boolean) {
        items.add(TestInstruction.SetCheckbox(shame, value))
    }

    fun click(shame: String, timestamp: String) {
        items.add(TestInstruction.Click(shame, timestamp))
    }

    fun keyDown(shame: String, keyCode: Int) {
        items.add(TestInstruction.KeyDown("Input-search", 13))
    }
}

class TestStateBuilder(val items: MutableList<TestInstruction>) {
    fun assertGen(tag: String, expectedExtender: ((dynamic) -> Unit)? = null) {
        items.add(TestInstruction.AssertGenerated(tag, "---generated-shit---", expectedExtender))
    }
}

















