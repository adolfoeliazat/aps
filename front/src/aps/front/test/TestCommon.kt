/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import aps.front.testutils.*
import into.kommon.*
import jquery.jq
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.document
import kotlin.browser.window

interface TestHost {
    fun selectNewBrowserAndNavigate(name: String, url: String): Promisoid<Unit>
}

abstract class TestScenario {
    val SLOWISH = false

    abstract fun run0(showTestPassedPane: Boolean): Promisoid<Throwable?>
    open val shortDescription: String = ctorName(this)
    open fun prepareShit(): Promisoid<Unit> = async {}
    open val name: String get() = ctorName(this)
    open val longDescription: String? = null
    val testShit = TestShit()
//    open val useSnapshot = false

    lateinit var host: TestHost
    val testCommon by lazy { TestCommon(host) }

    fun run(showTestPassedPane: Boolean): Promisoid<Throwable?> = async {
        executed += this

//        byid0("topNavbarContainer")?.let {ReactDOM.unmountComponentAtNode(it)}
//        byid0("root")?.let {ReactDOM.unmountComponentAtNode(it)}
//        DOMReact.containers.toList().forEach {DOMReact.unmountComponentAtNode(it)}
//
//        docInnerHTML = "<h3>Running Test: ${ctorName(this)}</h3><hr>"
//        measureAndReportToDocumentElement("Resetting database") {
//            __await(send(RecreateTestDatabaseSchemaRequest()-{o->
////                if (useSnapshot) {
////                    o.templateDB.value = "apsTestSnapshotOnTestServer"
////                }
//            }))
////            __await(send(ResetTestDatabaseRequest()))
////            __await(ResetTestDatabaseAlongWithTemplateRequest.send(templateDB = "test-template-ua-1", recreateTemplate = true))
//        }
//        measureAndReportToDocumentElement("Preparing shit") {
//            __await(prepareShit())
//        }
        await(run0(showTestPassedPane))
    }


    companion object {
        val executed = mutableListOf<TestScenario>()
    }
}

interface TestSuite {
    val scenarios: List<TestScenario>
    val shortDescription: String?
}

val testScenarios = mutableMapOf<String, TestScenario>()

fun tetete() {
    val strings = listOf("foo", "bar")
    val inst: List<Int> = strings as List<Int>
    val x = inst.first()
    println(x)
    println("ok")
}

class TestCommon(val sim: dynamic) {
    val LONG_SHIT_301 = TestData.generateShit(301)

}

fun jsFacing_igniteTestShit() = async<Unit> {
    ExternalGlobus.DB = "apsTestOnTestServer"
    Globus.isTest = true
    val urlQuery = parseQueryString(Globus.realLocation.href)

    for (name in jsArrayToListOfDynamic(Shitus.tokens("DEBUG_RPC_LAG_FOR_MANUAL_TESTS"))) {
        if (urlQuery[name] != undefined) {
            global[name] = urlQuery[name]
        }
    }

    window.addEventListener("keydown", {e ->
        e as KeyboardEvent
        if (e.code == "KeyX" && e.altKey) {
            preventAndStop(e)
            TestGlobal.killAwait = true
        }
    })

    val testName = urlQuery["test"]
    val testSuiteName = urlQuery["testSuite"]

    try {
        try {
            await(when {
                testName != null -> runTestNamed(testName, urlQuery)
                testSuiteName != null -> runTestSuiteFailingFast(testSuiteName, urlQuery)
                else -> bitch("Gimme test or testSuite in URL")
            })
        } catch(e: Throwable) {
            console.error(e.stack)
        }
    } finally {
        openTestListPane()
    }
}

private fun runTestSuiteFailingFast(testSuiteName: String, urlQuery: Map<String, String>): Promisoid<Unit> = async {
    val suite: TestSuite = instantiate(testSuiteName)
    TestGlobal.lastTestSuite = suite
    Globus.realTypedStorageLocal.lastTestSuiteURL = Globus.realLocation.href
    for (scenario in suite.scenarios) {
        clog("=====", "Running scenario", ctorName(scenario), "=====")
        val res = await(runTest(scenario, urlQuery, showTestPassedPane = false))
        if (res != null) {
            val bar = "*************************************"
            console.error("\n$bar\nWe are fucked, man\n$bar")
        }
    }

    openShitPassedPane(
        title = testSuiteName + suite.shortDescription.letOrEmpty {". $it"},
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
}

private fun runTestNamed(testName: String, urlQuery: Map<String, String>): Promisoid<Throwable?> {
    val scenario: TestScenario = instantiate(testName)
    return runTest(scenario, urlQuery, showTestPassedPane = true)
}

private fun runTest(scenario: TestScenario, urlQuery: Map<String, String>, showTestPassedPane: Boolean): Promisoid<Throwable?> = async {
    val opts = TestOptions.load(urlQuery)

    Globus.realTypedStorageLocal.lastTestURL = Globus.realLocation.href

    TestGlobal.lastTestMaybe = scenario
    TestGlobal.lastTestOptsMaybe = opts

    Globus.rootRedisLogMessageID = await(fedis.beginLogGroup("Test: ${scenario.name}"))

    TestGlobal.testRunning = true
    try {
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
            override fun selectNewBrowserAndNavigate(name: String, url: String): Promisoid<Unit> = async {
                die("don't use me")
//                dlog("Selecting browser", name)
//                hrss.browserOld = hrss.browsers.getOrPut(name) {BrowserOld(name)}
//                die("Attempt to use hrss.storageLocal")
//
//                dlog("Navigating", hrss.browserOld.name, url)
//                global.history.replaceState(null, "", url)
//                __await(World("killme").boot())
//                return __asyncResult(Unit)
            }
        }

        scenario.host = sim

//        global.DB = "aps-test"
//        global.sessionStorage.setItem("DB", global.DB)

        val initialHref = Globus.realLocation.pathname + Globus.realLocation.search
        global.addEventListener("keydown", {e: KeyboardEvent ->
            if (e.altKey && e.code == "KeyR") {
                preventAndStop(e)
                Globus.realLocation.href = initialHref
            }
        })

        hrss.currentTestScenario = scenario
        lastTestScenarioName = ctorName(scenario)
        val oldHotCodeUpdateDisabled = hrss.hotCodeUpdateDisabled
        val oldLiveStatusPollingViaIntervalDisabled = hrss.liveStatusPollingViaIntervalDisabled

        // Prevent some unnecessary glithes
        hrss.hotCodeUpdateDisabled = false
        hrss.liveStatusPollingViaIntervalDisabled = false

        hrss.urlQueryBeforeRunningTest = parseQueryString(Globus.realLocation.href)

        measure("Load generated shit") {
            // TODO:vgrechka Load generated shit once for whole suite
            eval(await(GetGeneratedShitRequest.send()).code)
        }

        val res = await(scenario.run(showTestPassedPane))
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

        return@async res
    }
    finally {
        TestGlobal.testRunning = false
        await(fedis.endLogGroup(Globus.rootRedisLogMessageID!!))
        Globus.rootRedisLogMessageID = null
    }
}

fun testNameToURL(testName: String, opts: TestOptions): String {
    return testSomethingToURL(testName, const.urlq.test.test, opts)
}

fun testSuiteNameToURL(testSuiteName: String, opts: TestOptions): String {
    return testSomethingToURL(testSuiteName, const.urlq.test.testSuite, opts)
}

private fun testSomethingToURL(name: String, nameParam: String, opts: TestOptions): String {
    val hostPort = when {
        name.contains("Writer") -> "aps-ua-writer.local:3022"
        name.contains("Customer") -> "aps-ua-customer.local:3012"
        else -> "aps-ua-writer.local:3022"
//        else -> bitch("Cannot figure out test URL hostPort for [$name]")
    }

    return buildString {
        append("http://$hostPort/faq.html")
        append("?$nameParam=$name")
        append("&" + opts.toURLQuery())
    }
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

















