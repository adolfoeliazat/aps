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
    lateinit var host: TestHost

    val testCommon by lazy { TestCommon(host) }

    abstract fun run(): Promise<Unit>
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

    val testScenarioToRun = urlQuery["testScenario"] ?: bitch("Gimme testScenario")

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

    hrss.currentTestScenarioName = testScenarioToRun
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

        hrss.currentTestScenarioName = undefined
//        if (!hrss.preventRestoringURLAfterTest) {
//            global.setTimeout({ global.history.replaceState(null, "", initialHref) }, 1000)
//        }

        run { // XXX Refresh tethers
            jqbody.scrollTop(jqbody.scrollTop() + 1)
            jqbody.scrollTop(jqbody.scrollTop() - 1)
        }
    }

    if (!art.halted) {
        hrss.openTestPassedPaneArgs = json("scenario" to scenario)
        openTestPassedPane(hrss.openTestPassedPaneArgs)
    }

    return __asyncResult(Unit)
}
























