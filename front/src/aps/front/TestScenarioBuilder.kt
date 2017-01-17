/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import aps.Color.*
import aps.front.testutils.*
import into.kommon.*
import jquery.JQuery
import jquery.jq
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.window
import kotlin.properties.Delegates.notNull

fun buildAndRunTestScenario(scenario: StepBasedTestScenario, showTestPassedPane: Boolean, block: (TestScenarioBuilder) -> Unit): Promise<Throwable?> = async {
    val builder = TestScenarioBuilder(scenario)
    block(builder)
    await(builder.runScenario(showTestPassedPane))
}

val transformNavbarLineTidy = {it: String ->
    it.replace(Regex(" id=\"MakeStaticSites-\\d+\""), "")
}

val transformRootLineTidy = {it: String ->
    it
}

class TestScenarioBuilder(val scenario: StepBasedTestScenario) {
    val instructions = mutableListOf<TestInstruction>()
    val shit get() = scenario.shit
    var lastAssertScreenHTMLParams by notNull<AssertScreenHTMLParams>()

    fun runScenario(showTestPassedPane: Boolean): Promise<Throwable?> = async {
        await(art.run(shit, instructions, showTestPassedPane))
    }

    fun state(descr: String) {
        instructions.add(TestInstruction.Step.StateStep(descr))
    }

    fun assert(test: Boolean, descr: String) {
        val step = TestInstruction.Step.AssertionStep(descr)
        instructions.add(step)
        act {
            step.passed = test
            if (!step.passed) art.fail(descr)
        }
    }

    fun act(descr: String? = null, block: () -> Unit) {
        acta(descr, {async{
            block()
        }})
    }

    fun <T> acta(descr: String? = null, block: () -> Promise<T>): TestInstruction.Do {
        var step: TestInstruction.Step? = null
        if (descr != null) {
            step = TestInstruction.Step.ActionStep(descr)
            instructions.add(step)
        }

        val instr = TestInstruction.Do {"__async"
            __await(block())
            step?.passed = true
            __asyncResult(Unit)
        }
        instructions.add(instr)
        return instr
    }

    fun <T> await(block: () -> Promise<T>) {
        acta(null, block)
    }

    fun assertVisibleText(expected: String, under: CSSSelector = "body") {
        assertOnAnimationFrame("Page should contain in $under: _${expected}_", {
            val actual = visibleText(under)
            actual.contains(expected)
        })
    }

    fun assertNoVisibleText(expected: String, under: CSSSelector = "body") {
        assertOnAnimationFrame("Page should not contain in $under: ~~_${expected}_~~", {
            val actual = visibleText(under)
            // dwarnStriking(actual)
            !actual.contains(expected)
        })
    }

    private fun visibleText(under: CSSSelector) = jq("$under *:not(:has(*)):visible").text()

    fun assertOnAnimationFrame(stepTitle: String, test: () -> Boolean) {
        checkOnAnimationFrame(stepTitle) {async{
            if (!test()) throw ArtAssertionError(stepTitle)
        }}
    }

    fun checkOnAnimationFrame(stepTitle: String, block: () -> Promise<Unit>) {
        val step = TestInstruction.Step.AssertionStep(stepTitle)
        instructions.add(step)
        acta {async{
            await(tillAnimationFrame())
            await(block())
            step.passed = true
        }}-{m->
            m.isAssertion = true
        }
    }

    fun halt() {
        val step = TestInstruction.Step.HaltStep("Fucking halt")
        instructions.add(step)
        act {
            throw FatException("Fucking halted", visualPayload = kdiv{o->
                fun makeTab(title: String, paste: String) = TabSpec(
                    id = title,
                    title = title,
                    content = kdiv{o->
                        o- Input(json("initialValue" to paste,
                                      "kind" to "textarea",
                                      "rows" to 10,
                                      "style" to json("width" to "100%",
                                                      "height" to "100%"),
                                      "untested" to true))
                    })

                o- Tabs2(initialActiveID = "Navbar Paste", tabs = listOf(
                    makeTab("Navbar Paste", "o.assertNavbarHTML(\"\"\"${takeHTMLForAssertion(SELECTOR_NAVBAR)}\"\"\")"),
                    makeTab("Root Paste", "o.assertRootHTML(\"\"\"${takeHTMLForAssertion(SELECTOR_ROOT)}\"\"\")")
                ))
            })
        }
    }

    fun assertHTML(under: CSSSelector, expected: String, transformLine: ((String) -> String)? = null) {
        assertHTML(inside=under, transformLine=transformLine, expected = {async{expected}})
    }

    fun assertHTML(inside: CSSSelector,
                   expected: () -> Promise<String>,
                   transformLine: ((String) -> String)? = null,
                   descr: String? = null
    ) {
        var stepTitle = "HTML inside `$inside`"
        descr?.let {stepTitle += ": $it"}
        checkOnAnimationFrame(stepTitle) {async{
            val rawActual = takeHTMLForAssertion(inside)
            val tidyActual = tidyHTML(rawActual, transformLine=transformLine)
            val tidyExpected = tidyHTML(await(expected()), transformLine=transformLine)
            if (tidyActual != tidyExpected) {
                throw ArtAssertionError(stepTitle, visualPayload = renderDiff(
                    expected = tidyExpected,
                    actual = tidyActual,
                    actualTestShit = rawActual.trim(),
                    actualPaste = symbols.threeQuotes + rawActual.trim() + symbols.threeQuotes))
            }
        }}
    }

    fun assertNavbarHTML(expected: String) {
        assertHTML(under = SELECTOR_NAVBAR,
                   expected = expected,
                   transformLine = transformNavbarLineTidy)
    }

    fun assertNavbarHTMLExt(descr: String?, id: String) {
        act {TestGlobal.testShitBeingAssertedID = id}
        assertHTML(inside = SELECTOR_NAVBAR,
                   expected = {async{await(fuckingRemoteCall.loadTestShit(id)) ?: "--- kill me ---"}},
                   transformLine = transformNavbarLineTidy,
                   descr=descr)
        act {TestGlobal.testShitBeingAssertedID = null}
    }

    fun assertRootHTML(expected: String) {
        assertHTML(under = SELECTOR_ROOT, expected = expected, transformLine = {it})
    }

    fun assertRootHTMLExt(descr: String?, id: String) {
        act {TestGlobal.testShitBeingAssertedID = id}
        assertHTML(inside = SELECTOR_ROOT, expected = {async{await(fuckingRemoteCall.loadTestShit(id)) ?: "--- kill me ---"}}, transformLine = {it}, descr=descr)
        act {TestGlobal.testShitBeingAssertedID = null}
    }

    fun assertRootHTMLExt(id: String) {
        assertRootHTMLExt(null, id)
    }

    fun assertUnderFooterHTML(descr: String, expected: String) {
        assertHTML(inside = "#$ELID_UNDER_FOOTER", expected = {Promise.resolve(expected)}, transformLine = {it}, descr=descr)
    }

    fun click(shame: String) {
        instructions.add(TestInstruction.Click(shame))
    }

    fun clickDescribingStep(shame: String) {
        val descr = "Clicking on `$shame`"
        val step = TestInstruction.Step.ActionStep(descr)
        instructions.add(step)
        click(shame)
        act {step.passed = true}
    }

    fun actionStep(descr: String, block: () -> Unit) {
        val step = TestInstruction.Step.ActionStep(descr)
        instructions.add(step)
        block()
        act {step.passed = true}
    }

    fun setValue(shame: String, value: String) {
        acta {async{
            val control = getShamedControl(shame)
            await<dynamic>(control.testSetValue(json("value" to value)))
        }}
    }

    fun getShamedControl(shame: String): dynamic {
        val control = TestGlobal.shameToControl[shame]
        if (control == null) Shitus.raiseWithMeta(json("message" to "Control shamed $shame is not found"))
        return control
    }

    fun setValueDescribingStep(shame: String, value: String) {
        val descr = "Typing into `$shame`: ${markdownItalicVerbatim(value)}"
        val step = TestInstruction.Step.ActionStep(descr)
        instructions.add(step)
        setValue(shame, value)
        act {step.passed = true}
    }

    fun <E : Enum<E>> setValueDescribingStep(shame: String, value: E) {
        setValueDescribingStep(shame, value.name)
    }

    fun setValueDescribingStep(shame: String, value: Int) {
        setValueDescribingStep(shame, value.toString())
    }

    fun setValueDescribingStep(shame: String, value: Boolean) {
        val action = if (value) "Checking" else "Unchecking"
        val descr = "$action `$shame`"
        val step = TestInstruction.Step.ActionStep(descr)
        instructions.add(step)
        setValue(shame, value)
        act {step.passed = true}
    }

    fun setValue(shame: String, value: Boolean) {
        instructions.add(TestInstruction.SetCheckbox(shame, value))
    }

    fun assertMailInFooter(descr: String, expectedTo: String, expectedSubject: String, expectedBody: String) {
        assertUnderFooterHTML("Email: $descr",
            """<div id="debugPanes-initDebugFunctions-mailbox"><div data-reactroot="" id="544"><div id="542" style="margin-top: 10px;"><div id="514" style="font-weight: bold; background: rgb(178, 223, 219);">Mailbox</div><div id="540" style="margin-top: 5px; padding-bottom: 5px; border-bottom: 2px dotted rgb(158, 158, 158);"><div id="536" style="background: rgb(255, 255, 255); margin-bottom: 5px;"><div spacing="4" class="" id="524" style="display: flex;"><div id="520" style="margin-left: 0px;"><span id="516" class="" style="font-weight: bold;">To:</span></div><div id="522" style="margin-left: 4px;"><span id="518" class="">""" +
            expectedTo.replace("<", "&lt;").replace(">", "&gt;") +
            """</span></div></div><div spacing="4" class="" id="534" style="display: flex;"><div id="530" style="margin-left: 0px;"><span id="526" class="" style="font-weight: bold;">Subject:</span></div><div id="532" style="margin-left: 4px;"><span id="528" class="">""" +
            expectedSubject +
            """</span></div></div></div><div id="538"><div>""" +
            dedent(expectedBody) +
            """</div></div></div></div></div></div>""")
    }

    fun beginSection(descr: String) {
        instructions.add(TestInstruction.BeginSection(descr))
    }

    fun endSection() {
        instructions.add(TestInstruction.EndSection())
    }

    fun section(descr: String, block: () -> Unit) {
        beginSection(descr)
        block()
        endSection()
    }

    fun section_rem(descr: String, block: () -> Unit) {
        TestGlobal.hasScenarioRems = true
        beginSection(descr)
        todo("This section is commented out")
        endSection()
    }

}

fun TestScenarioBuilder.forceOptsTillHere(opts: TestOptions) {
    instructions.add(0, TestInstruction.Do {async{
        TestGlobal.forcedTestOpts = opts
    }})
    instructions.add(TestInstruction.Do {async{
        TestGlobal.forcedTestOpts = null
    }})
}

fun TestScenarioBuilder.beginWorkRegion() {
    instructions.add(0, TestInstruction.Do {async{
        TestGlobal.forcedTestOpts = TestOptionsTemplates.fastestExceptShowBannerOnNonCorrectAssertions .opts
    }})
    instructions.add(TestInstruction.Do {async{
        TestGlobal.forcedTestOpts = null
    }})
}

fun TestScenarioBuilder.endWorkRegion() {
    instructions.add(TestInstruction.Do {async{
        val signal = ResolvableShit<Unit>()

        var pane by notNull<String>()
        pane = debugPanes.put(kdiv(className = css.test.popup.pause){o->
            o- hor1(marginBottom = "0.5rem"){o->
                o- Button(icon = fa.bomb, onClick = {
                    debugPanes.remove(pane)
                    signal.reject(Exception("Fucking killed"))
                })
                o- rerunTestButton()
                o- rerunTestSlowlyButton()
            }
            o- "End of work region"
        })

        await(signal.promise)
    }})
}

fun rerunTestButton() = Button(
    icon = fa.refresh,
    onClick = {
        Globus.realLocation.href = Globus.realTypedStorageLocal.lastTestURL!!
    })

fun rerunTestSlowlyButton(): Button {
    fun go(templateTitle: String) {
        Globus.realTypedStorageLocal.oneOffTestOptionsTemplateTitle = templateTitle
        Globus.realLocation.href = Globus.realTypedStorageLocal.lastTestURL!!
    }

    return Button(
        icon = fa.spinner,
        dropDownMenu = Menu(TestOptionsTemplates.all.map {
            MenuItem(it.title) {async {go(it.title)}}
        }),
        separateDropDownMenuButton = true,
        dropDownMenuDirection = Button.MenuDirection.UP,
        narrowCaret = true,
        onClick = {
            go(TestOptionsTemplates.slower.title)
        })
}

fun TestScenarioBuilder.twoStepLockSequence(
    buildAction: () -> Unit,
    lock: TwoStepTestLock,
    assertionDescr: String,
    assertionID1: String,
    assertionID2: String
) {
    val o = this
    o.act {lock.reset()}

    buildAction()

    o.acta {lock.testPause1()}
    o.assertScreenHTML("$assertionDescr (1)", assertionID1)
    o.act {lock.testResume1()}

    o.acta {lock.testPause2()}
    o.assertScreenHTML("$assertionDescr (2)", assertionID2)
    o.act {lock.testResume2()}
}

class TestSequenceStep(
    val lock: TestLock,
    val assertionID: String
)

fun TestScenarioBuilder.sequence(
    buildAction: () -> Unit,
    assertionDescr: String,
    steps: List<TestSequenceStep>
) {
    val o = this
    o.act {steps.forEach {it.lock.reset()}}

    buildAction()

    for ((i, step) in steps.withIndex()) {
        o.acta {step.lock.testPause()}
        o.assertScreenHTML("$assertionDescr (${i + 1})", step.assertionID)
        o.act {step.lock.testResume()}
    }
}

fun TestScenarioBuilder.fileFieldChoose(assertionDescr: String, assertionID: String, keySuffix: String, fileName: String) {
    sequence(
        assertionDescr = assertionDescr,
        buildAction = {
            buttonUserInitiatedClick("${fconst.key.upload.testRef}$keySuffix")
            typeIntoOpenFileDialog(fconst.test.filesRoot + fileName)
        },
        steps = listOf(
            TestSequenceStep(TestGlobal.fileFieldChangedLock, assertionID)
        )
    )
}

fun TestScenarioBuilder.snapshot(snapshot: Snapshot) {
    check(snapshot.name.length == 1 && snapshot.name[0] >= '1' && snapshot.name[0] <= '9') {"Snapshot name corresponds to a numeric key"}

    shit.snapshots.add(snapshot)

    instructions.add(0, TestInstruction.Do {async<Unit>{
        val snapshotChoice = ResolvableShit<Snapshot?>()
        val bannerPane = debugPanes.put(kdiv(className = css.test.popup.chooseSnapshot){o->
            o- "Choose snapshot:"
            o- Button(title = "0", style = Style(marginLeft = "0.5em")) {
                snapshotChoice.resolve(null)
            }
            for (snap in shit.snapshots) {
                o- Button(title = snap.name, style = Style(marginLeft = "0.5em")) {
                    snapshotChoice.resolve(snap)
                }
            }
        })
        fun keyListener(e: Event) {
            e as KeyboardEvent
            if (e.key == "0") {
                snapshotChoice.resolve(null)
            }
            for (snap in shit.snapshots) {
                if (e.key == snap.name) {
                    snapshotChoice.resolve(snap)
                }
            }
        }
        window.addEventListener("keydown", ::keyListener)

        try {
            await(snapshotChoice.promise)?.let {useSnapshot->
                dlog("Using snapshot ${useSnapshot.id}")
                val useSnapshotIndex = instructions.indexOfFirst {
                    it.snapshot?.id == useSnapshot.id
                }
                if (useSnapshotIndex == -1) bitch("Snapshot instruction not found: ${useSnapshot.id}")
                val assertion = instructions[useSnapshotIndex - 1]
                gloshit.instructions = instructions
                gloshit.useSnapshotIndex = useSnapshotIndex
                gloshit.assertion = assertion
                check(assertion.isAssertion) {"Instruction before snapshot should be assertion"}
                instructions.subList(0, useSnapshotIndex + 1).clear()
                instructions.add(0, TestInstruction.Do {async<Unit>{
                    dlog("Restoring shit from snapshot ${useSnapshot.id}")
                    DOMReact.containers.toList().forEach {DOMReact.unmountComponentAtNode(it)}

                    docInnerHTML = "<h3>Restoring test: ${ctorName(scenario)} (snapshot: ${useSnapshot.name})</h3><hr>"
                    measureAndReportToDocumentElement("Restoring snapshot database") {
                        await(send(RecreateTestDatabaseSchemaRequest()-{o->
                            o.snapshotID.value = useSnapshot.id
                        }))
                    }

                    val clientState = JSON.parse<ClientStateSnapshot>(
                        Globus.realStorageLocal.getItem(
                            fconst.storage.clientStateSnapshotPrefix + useSnapshot.id)!!)
                    dlog("Restored clientState", clientState)

                    _initFuckingBrowser(fillRawStorageLocal = {store->
                        for (item in clientState.storageItems) {
                            item.value?.let {
                                store.setItem(item.key, it)
                            }
                        }
                    })
                    shit.nextRequestTimestampIndex = clientState.nextRequestTimestampIndex
                    await(_kindaNavigateToStaticContent(clientState.url))

                    val world = World("boobs")
                    await(world.boot())
                }}-{m->
                    m.debugDescription = "Restore shit from snapshot"
                })
                instructions.add(1, assertion)
                shit.nextInstructionIndex = 0

            }
        } finally {
            window.removeEventListener("keydown", ::keyListener)
            debugPanes.remove(bannerPane)
        }
    }})

    lastAssertScreenHTMLParams.let {
        assertScreenHTML(it.copy(descr = "Before snapshot: " + it.descr))}

    instructions.add(TestInstruction.Do {async<Unit> {
        dlog("Taking snapshot ${snapshot.id}")
        await(send(TestTakeSnapshotRequest()-{o->
            o.name.value = snapshot.id
            o.url.value = Globus.realLocation.href // TODO:vgrechka @kill
        }))

        val store = TestGlobal.browser.typedStorageLocal.store
        gloshit.store = store
        val clientState = ClientStateSnapshot(
            url = Globus.realLocation.href,
            nextRequestTimestampIndex = shit.nextRequestTimestampIndex,
            storageItems = (mutableListOf<ClientStateSnapshot.StorageItem>()-{o->
                for (i in 0 until store.length) {
                    val key = store.key(i)!!
                    o += ClientStateSnapshot.StorageItem(
                        key = key,
                        value = store.getItem(key)
                    )
                }
            }).toTypedArray()
        )
        gloshit.clientState = clientState

        val clientStateJSON = JSON.stringify(clientState)
        dlog("clientStateJSON", clientStateJSON)
        Globus.realStorageLocal.setItem(
            fconst.storage.clientStateSnapshotPrefix + snapshot.id,
            clientStateJSON)
    }}-{m->
        m.snapshot = snapshot
    })
}

class ClientStateSnapshot (
    val url: String,
    val nextRequestTimestampIndex: Int,
    val storageItems: Array<StorageItem>
) {
    class StorageItem(
        val key: String,
        val value: String?
    )
}



fun TestScenarioBuilder.initialShit(test: TestScenario) {
    acta {async{
        killEverythingVisual()
        Globus.location = FakeLocationProxy(Globus.realLocation.href)

        docInnerHTML = "<h3>Running Test: ${ctorName(test)}</h3><hr>"
        measureAndReportToDocumentElement("Resetting database") {
            await(send(RecreateTestDatabaseSchemaRequest()-{o->
//                if (useSnapshot) {
//                    o.templateDB.value = "apsTestSnapshotOnTestServer"
//                }
            }))
//            await(send(ResetTestDatabaseRequest()))
//            await(ResetTestDatabaseAlongWithTemplateRequest.send(templateDB = "test-template-ua-1", recreateTemplate = true))
        }
//        measureAndReportToDocumentElement("Preparing shit") {
//            await(prepareShit())
//        }
    }}
}


fun TestScenarioBuilder.addFile(shit: TestShit, fileName: String, title: String, details: String, aid: String) {
    sequence(
        buildAction = {
            buttonClick(fconst.key.plus.testRef)
        },
        assertionDescr = "Opened plus form",
        steps = listOf(
            TestSequenceStep(TestGlobal.fadeHalfwayLock, "$aid--1"),
            TestSequenceStep(TestGlobal.fadeDoneLock, "$aid--2")
        )
    )

    fileFieldChoose(
        assertionDescr = "Chose file",
        assertionID = "$aid--3",
        keySuffix = "",
        fileName = fileName)

    inputAppendValue("title", title)
    inputAppendValue("details", details)
    submitForm(
        shit,
        descr = "Shit is added",
        aid = "$aid--submit"
    )
}

fun TestScenarioBuilder.refreshPage(aid: String) {
    sequence(
        assertionDescr = "Refresh page",
        buildAction = {
            buttonClick(fconst.key.refreshPage.testRef)
        },
        steps = listOf(
            TestSequenceStep(TestGlobal.reloadPageTickingLock, "$aid--1"),
            TestSequenceStep(TestGlobal.reloadPageDoneLock, "$aid--2")
        )
    )
}

fun TestScenarioBuilder.search(text: String, aid: String) {
    sequence(
        assertionDescr = "Search",
        buildAction = {
            inputSetValue(fconst.key.search.testRef, text)
            inputPressEnter(fconst.key.search.testRef)
        },
        steps = listOf(
            TestSequenceStep(TestGlobal.reloadPageTickingLock, "$aid--1"),
            TestSequenceStep(TestGlobal.reloadPageDoneLock, "$aid--2")
        )
    )
}










