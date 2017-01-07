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
            __await(send(RecreateTestDatabaseSchemaRequest()))
//            __await(send(ResetTestDatabaseRequest()))
//            __await(ResetTestDatabaseAlongWithTemplateRequest.send(templateDB = "test-template-ua-1", recreateTemplate = true))
        }
        measureAndReportToDocumentElement("Preparing shit") {
            __await(prepareShit())
        }
        return __reawait(run0(showTestPassedPane))
    }


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
    val strings = listOf("foo", "bar")
    val inst: List<Int> = strings as List<Int>
    val x = inst.first()
    println(x)
    println("ok")
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

fun jsFacing_igniteTestShit() = async<Unit> {
//    val urlObject = jshit.utils.url.parse(global.location.href)
//    val urlQuery = jshit.utils.querystring.parse(urlObject.query)
    ExternalGlobus.DB = "apsTestOnTestServer"
    Globus.isTest = true
    val urlQuery = parseQueryString(global.location.href)

    for (name in jsArrayToListOfDynamic(Shitus.tokens("DEBUG_RPC_LAG_FOR_MANUAL_TESTS"))) {
        if (urlQuery[name] != undefined) {
            global[name] = urlQuery[name]
        }
    }

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

private fun runTestNamed(testName: String, urlQuery: Map<String, String>): Promise<Throwable?> {
    val scenario: TestScenario = instantiate(testName)
    return runTest(scenario, urlQuery, showTestPassedPane = true)
}

private fun runTest(scenario: TestScenario, urlQuery: Map<String, String>, showTestPassedPane: Boolean): Promise<Throwable?> = async {
    val opts = TestRunnerOptions.load(urlQuery)

    Globus.realTypedStorageLocal.lastTestURL = window.location.href

    TestGlobal.lastTestMaybe = scenario
    TestGlobal.lastTestOptsMaybe = opts

    Globus.rootRedisLogMessageID = await(fedis.beginLogGroup("Test: ${scenario.name}"))

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
            override fun selectNewBrowserAndNavigate(name: String, url: String): Promise<Unit> = async {
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
        await(fedis.endLogGroup(Globus.rootRedisLogMessageID!!))
        Globus.rootRedisLogMessageID = null
    }
}

data class TestRunnerOptions(
    val _stopOnAssertions: Boolean = false,
    val dontStopOnCorrectAssertions: Boolean = false,
    val _animateUserActions: Boolean = false,
    val _slowdown: Int = 1
) {
    fun toURLQuery(): String {
        return buildString {
            append(const.urlq.test.stopOnAssertions + "=" + _stopOnAssertions)
            append("&" + const.urlq.test.dontStopOnCorrectAssertions + "=" + dontStopOnCorrectAssertions)
            append("&" + const.urlq.test.animateUserActions + "=" + _animateUserActions)
        }
    }

    val stopOnAssertions get() =
        if (TestGlobal.forcedFastest) false
        else _stopOnAssertions

    val slowdown get() =
        if (TestGlobal.forcedFastest) 1
        else _slowdown

    val animateUserActions get() =
        if (TestGlobal.forcedFastest) false
        else _animateUserActions

    companion object {
        class OptionSet(val title: String, val opts: TestRunnerOptions)

        val optionSets = setOf(
            OptionSet("Usual", TestRunnerOptions()),
            OptionSet("Stop on assertions", TestRunnerOptions(_stopOnAssertions = true)),
            OptionSet("Stop on assertions, animate", TestRunnerOptions(_stopOnAssertions = true, _animateUserActions = true)),
            OptionSet("Stop on assertions except correct", TestRunnerOptions(_stopOnAssertions = true, dontStopOnCorrectAssertions = true)),
            OptionSet("Stop on assertions except correct, animate", TestRunnerOptions(_stopOnAssertions = true, dontStopOnCorrectAssertions = true, _animateUserActions = true))
        )

        fun load(urlQuery: Map<String, String>): TestRunnerOptions {
            var res = TestRunnerOptions(
                _stopOnAssertions = urlQuery[const.urlq.test.stopOnAssertions].relaxedToBoolean(default = false),
                dontStopOnCorrectAssertions = urlQuery[const.urlq.test.dontStopOnCorrectAssertions].relaxedToBoolean(default = false),
                _animateUserActions = urlQuery[const.urlq.test.animateUserActions].relaxedToBoolean(default = false)
            )

            Globus.realTypedStorageLocal.subsequentTestSlowdown?.let {
                res = res.copy(_slowdown = it)
                Globus.realTypedStorageLocal.subsequentTestSlowdown = null
            }

            return res
        }
    }
}

fun testNameToURL(testName: String, opts: TestRunnerOptions): String {
    val hostPort = when {
        testName.contains("Writer") -> "aps-ua-writer.local:3022"
        testName.contains("Customer") -> "aps-ua-customer.local:3012"
        else -> bitch("Cannot figure out test URL hostPort for [$testName]")
    }

    return buildString {
        append("http://$hostPort/faq.html")
        append("?" + const.urlq.test.test + "=" + testName)
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

















