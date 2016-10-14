/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

// - Run test by full name
// - Run all tests in given package and subpackages
// - Cherry-pick tests from several packages into new package
// - Packages also serve for easy test naming
// - Name of test is comprised of all super-packages + leaf
// - Renaming package causes automatic renaming of all sub-tests
// - Easily define internal test helpers and state
// - Easily find in code test by its (possibly partial) name

interface TestHost {
    fun selectBrowser(name: String)
    fun navigate(url: String): Promise<Unit>
}

abstract class TestScenario {
    lateinit var host: TestHost

    val drpc = jshit.getDebugRPC()
    val testCommon by lazy { TestCommon(host) }

    abstract fun run(): Promise<Unit>
}

val testScenarios = mutableMapOf<String, TestScenario>()

fun runTestScenario(fullName: String) {
    val scenario = testScenarios[fullName] ?: die("No such test scenario: $fullName")
    scenario.run()
}

fun tetete() {
    val x = puid()
    println(x)
    println(jsTypeOf(x))
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

fun jsFacing_igniteTestShit(makeCleanPairAndBoot: dynamic) {"__async"
    val urlObject = jshit.utils.url.parse(global.location.href)
    val urlQuery = jshit.utils.querystring.parse(urlObject.query)

    for (name in jsArrayToList(jshit.utils.tokens("DEBUG_RPC_LAG_FOR_MANUAL_TESTS"))) {
        if (urlQuery[name] != undefined) {
            global[name] = urlQuery[name]
        }
    }

    val testScenarioToRun: String = urlQuery.testScenario ?: bitch("Gimme testScenario")

    jshit.preventScrollToBottomOnAssertionError = urlQuery.scrollToBottom == "no"
    jshit.preventExceptionRevelation = urlQuery.revealException == "no"
    jshit.preventUIAssertionThrowing = urlQuery.uiAssertionThrows == "no"
    jshit.testSpeed = if (urlQuery.testSpeed) urlQuery.testSpeed else "fast"
    jshit.alternativeTestSpeed = urlQuery.alternativeTestSpeed
    if (!jshit.alternativeTestSpeed) {
        if (jshit.testSpeed == "fast" || jshit.testSpeed == "medium") {
            jshit.alternativeTestSpeed = "slow"
        } else {
            jshit.alternativeTestSpeed = "medium"
        }
    }
    jshit.art.respectArtPauses = urlQuery.respectArtPauses == "yes"

    val sim = object : TestHost {
        override fun navigate(url: String): Promise<Unit> {"__async"
            jshit.utils.dlog("Navigating", jshit.browser.name, url)
            global.history.replaceState(null, "", url)
            __await<dynamic>(makeCleanPairAndBoot())
            return __asyncResult(Unit)
        }

        override fun selectBrowser(name: String) {
            jshit.utils.dlog("Selecting browser", name)
            jshit.browser = jshit.browsers[name]
            if (!jshit.browser) {
                var storageLocalItems = js("({})")

                jshit.browser = json(
                    "name" to name,
                    "storageLocal" to json(
                        "clear" to {
                            storageLocalItems = js("({})")
                        },
                        "getItem" to {key: dynamic ->
                            storageLocalItems[key]
                        },
                        "setItem" to {key: dynamic, value: dynamic ->
                            storageLocalItems[key] = value
                            global.localStorage.setItem(key, value) // TODO:vgrechka @kill
                        }
                    )
                )

                jshit.browsers[name] = jshit.browser
            }

            jshit.storageLocal = jshit.browser.storageLocal

            if (!jshit.browser.topNavbarElement) {
                // WTF is this?..
                // Doing this prevents proper unmounting of React components
                //                        byid("topNavbarContainer").html(`<div style="text-align: center;">New browser: ${name}</div>`)
                //                        byid("root").html("")
            } else {
                jshit.ReactDOM.render(jshit.browser.topNavbarElement, jshit.byid0("topNavbarContainer"))
                jshit.ReactDOM.render(jshit.browser.rootElement, jshit.byid0("root"))
            }
        }
    }

    val scenarioClass = eval("kot.aps.front.$testScenarioToRun") ?: bitch("No test scenario named [${testScenarioToRun}]")
    val scenario: TestScenario = eval("new scenarioClass()")
    scenario.host = sim

    jshit.DB = "aps-test"
    global.sessionStorage.setItem("DB", jshit.DB)




    val initialPath = global.location.pathname + global.location.search

    jshit.currentTestScenarioName = testScenarioToRun
    jshit.lastTestScenarioName = testScenarioToRun
    val oldHotCodeUpdateDisabled = jshit.hotCodeUpdateDisabled
    val oldLiveStatusPollingViaIntervalDisabled = jshit.liveStatusPollingViaIntervalDisabled

    // Prevent some unnecessary glithes
    jshit.hotCodeUpdateDisabled = false
    jshit.liveStatusPollingViaIntervalDisabled = false

    jshit.urlQueryBeforeRunningTest = jshit.getURLQuery()

    try {
        __await(scenario.run())
    } finally {
        jshit.hotCodeUpdateDisabled = oldHotCodeUpdateDisabled
        jshit.liveStatusPollingViaIntervalDisabled = oldLiveStatusPollingViaIntervalDisabled

        jshit.currentTestScenarioName = undefined
        if (!jshit.preventRestoringURLAfterTest) {
            global.setTimeout({ global.history.replaceState(null, "", initialPath) }, 1000)
        }
    }

    if (!jshit.art.halted) {
        jshit.openTestPassedPaneArgs = json("scenario" to scenario)
        openTestPassedPane(jshit.openTestPassedPaneArgs)
    }
}
























