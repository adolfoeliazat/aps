/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

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
    val SLOWISH = false

    abstract fun run0(showTestPassedPane: Boolean): Promise<Throwable?>
    open val shortDescription: String = ctorName(this)
    open fun prepareShit(): Promise<Unit> = __asyncResult(Unit)
    open val name: String get() = ctorName(this)
    open val longDescription: String? = null

    lateinit var host: TestHost
    val testCommon by lazy { TestCommon(host) }

    fun run(showTestPassedPane: Boolean): Promise<Throwable?> {"__async"
        executed += this

//        byid0("topNavbarContainer")?.let {ReactDOM.unmountComponentAtNode(it)}
//        byid0("root")?.let {ReactDOM.unmountComponentAtNode(it)}
        DOMReact.containers.toList().forEach {DOMReact.unmountComponentAtNode(it)}

        docInnerHTML = "<h3>Running Test: ${ctorName(this)}</h3><hr>"
        measureAndReportToDocumentElement("Resetting database") {
            __await(ResetTestDatabaseRequest.send(templateDB = "test-template-ua-1", recreateTemplate = true))
        }
        measureAndReportToDocumentElement("Preparing shit") {
            __await(prepareShit())
        }
        return __reawait(run0(showTestPassedPane))
    }

    inline fun <T> measureAndReportToDocumentElement(name: String, block: () -> T): T {
        docInnerHTML += "$name...."
        val res = measure(name, block)
        docInnerHTML += " DONE<br>"
        return res
    }

    var docInnerHTML: String
        get() = document.documentElement!!.innerHTML
        set(value) {document.documentElement!!.innerHTML = value}


    companion object {
        val executed = mutableListOf<TestScenario>()
    }
}

interface TestSuite {
    val scenarios: List<TestScenario>
    val shortDescription: String
}

val testScenarios = mutableMapOf<String, TestScenario>()


fun tetete() {
    val list = listOf("foo", "bar", "baz")
    global.shit = list.toTypedArray()
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

    for (name in jsArrayToListOfDynamic(Shitus.tokens("DEBUG_RPC_LAG_FOR_MANUAL_TESTS"))) {
        if (urlQuery[name] != undefined) {
            global[name] = urlQuery[name]
        }
    }

    val testName = urlQuery["test"]
    val testSuiteName = urlQuery["testSuite"]

    __await(when {
        testName != null -> runTestNamed(testName, urlQuery)
        testSuiteName != null -> runTestSuiteFailingFast(testSuiteName, urlQuery)
        else -> bitch("Gimme test or testSuite in URL")
    })
    return __asyncResult(Unit)
}

private fun runTestSuiteFailingFast(testSuiteName: String, urlQuery: Map<String, String>): Promise<Unit> {"__async"
    val suite: TestSuite = instantiate(testSuiteName)
    for (scenario in suite.scenarios) {
        clog("=====", "Running scenario", ctorName(scenario), "=====")
        val res = __await(runTest(scenario, urlQuery, showTestPassedPane = false))
        if (res != null) {
            val bar = "*************************************"
            console.error("\n$bar\nWe are fucked, man\n$bar")
            return __asyncResult(Unit)
        }
    }

    openShitPassedPane(
        title = "$testSuiteName. ${suite.shortDescription}",
        details = kdiv{o->
            o- "Following motherfuckers have executed and kind of passed:"
            o- kol{o->
                for (test in TestScenario.executed) {
                    o- kli{o->
                        o- "${test.name}"
                        if (test.shortDescription != test.name) {
                            o- ". ${test.shortDescription}"
                        }
                    }
                }
            }
        })

    return __asyncResult(Unit)
}

private fun runTestNamed(testName: String, urlQuery: Map<String, String>): Promise<Throwable?> {"__async"
    val scenario: TestScenario = instantiate(testName)
    return __reawait(runTest(scenario, urlQuery, showTestPassedPane = true))
}

private fun runTest(scenario: TestScenario, urlQuery: Map<String, String>, showTestPassedPane: Boolean): Promise<Throwable?> {"__async"
    val testName = ctorName(scenario)
    TestShit.lastTestHref = when {
        testName.contains("Writer") -> "http://aps-ua-writer.local:3022/faq.html?test=$testName"
        else -> bitch("Cannot figure out URL for test [$testName]")
    }

    send(SendRedisLogMessageRequest()-{o->
        o.type.value = RedisLogMessage.Separator.Type.THICK_DASHED_SEPARATOR
        o.text.value = "Running test: $testName"
    })

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

    val sim = object : TestHost {
        override fun selectNewBrowserAndNavigate(name: String, url: String): Promise<Unit> {
            "__async"
            dlog("Selecting browser", name)
            hrss.browserOld = hrss.browsers.getOrPut(name) {BrowserOld(name)}
            die("Attempt to use hrss.storageLocal")
//            hrss.storageLocal = hrss.browserOld.storageLocal

            dlog("Navigating", hrss.browserOld.name, url)
            global.history.replaceState(null, "", url)
//            __await<dynamic>(makeCleanPairAndBoot())
            __await(World().boot())
            return __asyncResult(Unit)
        }
    }

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
    lastTestScenarioName = ctorName(scenario)
    val oldHotCodeUpdateDisabled = hrss.hotCodeUpdateDisabled
    val oldLiveStatusPollingViaIntervalDisabled = hrss.liveStatusPollingViaIntervalDisabled

    // Prevent some unnecessary glithes
    hrss.hotCodeUpdateDisabled = false
    hrss.liveStatusPollingViaIntervalDisabled = false

    hrss.urlQueryBeforeRunningTest = getURLQuery()

    measure("Load generated shit") {
        // TODO:vgrechka Load generated shit once for whole suite
        eval(__await(GetGeneratedShitRequest.send()).code)
    }

    val res = __await(scenario.run(showTestPassedPane))
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

//    if (!art.halted) {
//        hrss.openTestPassedPaneArgs = json("scenario" to scenario)
//        openTestPassedPane(hrss.openTestPassedPaneArgs)
//    }

    return __asyncResult(res)
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

















