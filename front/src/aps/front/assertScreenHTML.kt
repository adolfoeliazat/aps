package aps.front

import aps.*
import aps.Color.*
import into.kommon.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.document
import kotlin.browser.window
import kotlin.properties.Delegates.notNull

private var testPausedOnAssertion = false
private var pausedOnAssertion = ResolvableShit<Unit>()
private var assertionBannerPause by notNull<ResolvableShit<Unit>>()
private var _currentAssertionBannerKind by notNull<TestBannerKind>()

val currentAssertionBannerKind get() = _currentAssertionBannerKind
private var prevDiffResult: RenderDiffResult? = null

enum class TestBannerKind(val className: String) {
    NOT_HARDENED(css.test.popup.assertion.notHardened),
    CORRECT(css.test.popup.assertion.correct),
    INCORRECT(css.test.popup.assertion.incorrect),
    PAUSE(css.test.popup.pause)
}

enum class VerticalPosition {
    TOP, BOTTOM
}

enum class HorizontalPosition {
    LEFT, RIGHT
}

fun isTestPausedOnAssertion() = testPausedOnAssertion

fun tillPausedOnAssertion() = pausedOnAssertion.promise

fun resumeTestBannerPause() = assertionBannerPause.resolve()

fun killTestButton(): Button {
    return Button(icon = fa.bomb, onClick = {
        assertionBannerPause.reject(Exception("Fucking killed"))
    })
}

class AssertScreenOpts(
    val bannerVerticalPosition: VerticalPosition = VerticalPosition.BOTTOM,
    val bannerHorizontalPosition: HorizontalPosition = HorizontalPosition.LEFT
)

data class AssertScreenHTMLParams(
    val descr: String?,
    val assertionID: String,
    val opts: AssertScreenOpts? = null
)

suspend fun assertScreenHTML(descr: String? = null, aid: String, opts: AssertScreenOpts? = null) {
    assertScreenHTML(AssertScreenHTMLParams(descr, aid, opts))
}

suspend fun assertScreenHTML(p: AssertScreenHTMLParams) {
    if (TestGlobal.skipAllFreakingAssertions) {
        testOpts().sleepAfterEachAssertionMs?.let {
            sleep(it)
        }
        return
    }

    val opts = p.opts ?: TestGlobal.defaultAssertScreenOpts
//    lastAssertScreenHTMLParams = p
//    act {TestGlobal.testShitBeingAssertedID = p.assertionID}

    sleep(50)             // XXX Fucking React...
    waitAnimationFrame()  // It seems, both of these is needed
                          // Also, when Chrome's Elements tab is open, updates still lag

    TestGlobal.testShitBeingAssertedID = p.assertionID
    val expected = await(fuckingRemoteCall.loadTestShit(p.assertionID))

    val ctx = ShowTestBannerContext()
    ctx.assertionDescr = p.descr ?: "Describe me"

    val actual = buildString {
        append("-------------------- URL --------------------\n\n")
        var url = loc.href
        val doubleSlash = url.indexOfOrDie("//")
        var nextSlash = url.indexOf("/", doubleSlash + 2)
        if (nextSlash == -1) {
            url += "/"
            nextSlash = url.indexOf("/", doubleSlash + 2)
        }
        val base = when (Globus.clientKind) {
            ClientKind.UA_CUSTOMER -> "[uac]"
            ClientKind.UA_WRITER -> "[uaw]"
        }
        append(base + url.substring(nextSlash) + "\n\n")

        val modalPaneSelector = ".${css.shebang.modalPane}"
        val jqModalPane = jq(modalPaneSelector)
        if (jqModalPane.length > 0) {
            check(jqModalPane.length == 1) {"Too many modal panes"}
            append("\n-------------------- MODAL --------------------\n\n")
            append(tidyHTML(takeHTMLForAssertion(modalPaneSelector)))
        } else {
            gloshit.actualAssertionURL = toString()
            append("-------------------- NAVBAR --------------------\n\n")
            append(tidyHTML(takeHTMLForAssertion("#" + fconst.elementID.topNavbarContainer), transformNavbarLineTidy))
            if (!endsWith("\n")) append("\n")
            append("\n-------------------- ROOT --------------------\n\n")
            append(tidyHTML(takeHTMLForAssertion("#" + fconst.elementID.root), transformRootLineTidy))
        }

        if (!endsWith("\n")) append("\n")
        append("\n-------------------- PANES --------------------\n\n")
        append(tidyHTML(takeHTMLForAssertion("#" + fconst.elementID.testablePanes)))
    }

    if (testOpts().stopOnAssertions) {
        await(object {
            init {
                ctx.verticalPosition = opts.bannerVerticalPosition
                ctx.horizontalPosition = opts.bannerHorizontalPosition
            }

            var capturedVisualShit = false

            suspend fun acceptCurrentShit() {
                ctx.descriptionPanel.setContent(kdiv{o->
                    o- span("Accepting...", Style(fontStyle = "italic"))
                })

                await(captureVisualShitIfNeeded())
                await(sendp(SaveCapturedVisualShitRequest()))
                await(send(HardenScreenHTMLRequest()-{o->
                    o.assertionID = p.assertionID
                    o.html = actual
                }))
                assertionBannerPause.resolve()
            }
            init {ctx.acceptCurrentShit = {acceptCurrentShit()}}

            val shit = async {
                when {
                    expected == null || TestGlobal.pretendAllAssertionsNotHardened -> {
                        showAssertionTestBanner(
                            ctx, p,
                            TestBannerKind.NOT_HARDENED,
                            renderSpecificButtons = {o ->
                                o- acceptButton()
                            })
                    }

                    actual == expected -> {
                        if (!testOpts().dontStopOnCorrectAssertions) {
                            showAssertionTestBanner(ctx, p, TestBannerKind.CORRECT)
                        }
                    }

                    actual != expected -> {
                        val pane = old_debugPanes.put(byid(fconst.elementID.underFooter), kdiv(
                            id = "fuckingDiff",
                            backgroundColor = RED_700, color = WHITE, marginTop = 10, padding = "10px 10px",
                            textAlign = "center", fontWeight = "bold"
                        ){o->
                            o- kdiv(paddingBottom = 10){o->
                                o- "Diff"
                            }

                            o- kdiv(
                                backgroundColor = WHITE, color = BLACK_BOOT,
                                fontWeight = "normal", textAlign = "left", padding = 5
                            ){o->
                                val diffResult = renderDiff(expected = expected, actual = actual, actualTestShit = actual)
                                val thePrevDiffResult = prevDiffResult
                                prevDiffResult = diffResult
                                if (thePrevDiffResult != null && thePrevDiffResult.diffSummary == diffResult.diffSummary) {
                                    clog("************************************")
                                    clog("* Diff is the same as previous one *")
                                    clog("************************************")
                                }
                                o- diffResult.tre
                            }
                        })

                        try {
                            val captureExists = await(send(CapturedVisualShitExistsRequest()-{o->
                                o.id = p.assertionID
                            })).exists

                            showAssertionTestBanner(
                                ctx, p,
                                TestBannerKind.INCORRECT,
                                renderSpecificButtons = {o ->
                                    o- Button(title = "Diff", style = ctx.bannerButtonStyle, onClick = {
                                        ctx.verticalPosition = opts.bannerVerticalPosition
                                        ctx.horizontalPosition = opts.bannerHorizontalPosition
                                        ctx.banner.update()
//                                                byid("fuckingDiff").scrollBodyToShit()
                                        scroll.body.toShit(nextDiff(), dy = -70)
                                    })
                                    if (captureExists) {
                                        o- Button(title = "VDiff", style = ctx.bannerButtonStyle, key = buttons.assertionBanner.vdiff, onClicka = {
                                            async<Unit> {
                                                openVisualDiff()
                                            }
                                        })
                                    }
                                    o- acceptButton()
                                })
                        } finally {
                            old_debugPanes.remove(pane)
                        }
                    }

                    else -> wtf()
                }
            }

            var diffIndex = -1
            val diffElements by lazy {
                val shit = jq(".${css.diff.expected.title}, .${css.diff.actual.title}")
                check(shit.length > 0)
                shit
            }

            fun nextDiff(): JQuery {
                if (++diffIndex > diffElements.length - 1) {
                    diffIndex = 0
                }
                return jq(diffElements[diffIndex]!!)
            }

            inner class openVisualDiff {
                val mycss = css.test.popup.imageViewer
                var visualDiffPane by notNull<String>()

                inner abstract class Mode {
                    abstract val buttonTitle: String

                    val scrollerID: String = puid()
                    var scrollLeft: Double = 0.0
                    var scrollTop: Double = 0.0

                    val view by lazy {
                        val place = Placeholder(kdiv{o->
                            o- hor2 {o ->
                                o- kdiv(marginTop = "0.7rem"){o->
                                    o- "Loading shit..."
                                }
                                o- renderTicker(float = null)
                            }
                        })

                        async {
                            try {
                                val base64 = await(promiseBase64())
                                place.setContent(kdiv(id = scrollerID, style = Style(position = "absolute", width = "100%", height = "100%", overflow = "auto")){o->
                                    val imgURL = "data:image/png;base64,$base64"
                                    o- img2(src = imgURL, style = Style(width = "100%"))
                                })
                            } catch (e: dynamic) {
                                place.setContent(kdiv{o->
                                    o- hor2 {o ->
                                        o- ki(iconClass = fa.frownO)
                                        o- "It didn't work. See your fucking server log..."
                                    }
                                })
                            }
                        }

                        return@lazy place
                    }

                    abstract fun promiseBase64(): Promisoid<String>

                    fun renderButton() = Button(
                        title = buttonTitle,
                        style = ctx.bannerButtonStyle.copy(
                            borderLeft = if (mode != this) null else
                                "1rem solid $ORANGE_300"
                        ),
                        onClick = {
                            byid0(mode.scrollerID)?.let {
                                mode.scrollLeft = it.scrollLeft
                                mode.scrollTop = it.scrollTop
                            }

                            mode = this
                            ctrl.update()

                            timeoutSet(0) {
                                byid0(mode.scrollerID)?.let {scroller ->
                                    // May be loading, so no scroller yet
                                    scroller.scrollLeft = mode.scrollLeft
                                    scroller.scrollTop = mode.scrollTop
                                }
                            }
                        }
                    )
                }

                val diffMode = object : Mode() {
                    override val buttonTitle = "Diff"

                    override fun promiseBase64() = async {
                        val res = await(send(DiffCapturedVisualShitWithSavedRequest()-{o->
                            o.id = p.assertionID
                        }))
                        res.base64
                    }
                }

                val hardenedMode = object : Mode() {
                    override val buttonTitle = "Hardened"

                    override fun promiseBase64() = async {
                        val res = await(send(GetCapturedVisualShitRequest()-{o->
                            o.id = p.assertionID
                        }))
                        res.base64
                    }
                }

                val currentMode = object : Mode() {
                    override val buttonTitle = "Current"

                    override fun promiseBase64() = async {
                        val res = await(send(GetCurrentCapturedVisualShitRequest()))
                        res.base64
                    }
                }

                var mode: Mode = diffMode

                val ctrl: Control2 by lazy {
                    Control2.from {
                        kdiv(className = mycss.pane){o->
                            o- kdiv(className = mycss.titleBar){o->
                                o- kdiv(className = mycss.title){o->
                                    o- "Visual Diff"
                                }
                                o- hor1(style = Style(justifyContent = "flex-end")){o->
                                    for (m in listOf(diffMode, hardenedMode, currentMode)) {
                                        o- m.renderButton()
                                    }
                                    o- kdiv(width = "1rem")
                                    o- Button(icon = fa.check, title = "Accept", style = ctx.bannerButtonStyle, key = buttons.assertionBanner.accept, onClicka = {
                                        old_debugPanes.remove(visualDiffPane)
                                        acceptCurrentShit()
                                    })
                                    o- Button(icon = fa.close, style = ctx.bannerButtonStyle, onClick = {
                                        old_debugPanes.remove(visualDiffPane)
                                    })
                                }
                            }
                            o- kdiv(className = mycss.content){o->
                                o- mode.view
                            }
                        }
                    }
                }

                init {
                    async {
                        await(captureVisualShitIfNeeded())
                        visualDiffPane = old_debugPanes.put(ctrl)
                    }
                }
            }

            fun captureVisualShitIfNeeded() = async {
                if (!capturedVisualShit) {
                    await(captureVisualShitPromise(p.assertionID))
                    capturedVisualShit = true
                }
            }

            fun acceptButton() = Button(
                key = buttons.assertionBanner.accept,
                icon = fa.check,
                style = ctx.bannerButtonStyle,
                onClicka = {acceptCurrentShit()})


        }.shit)
    } else {
        when {
            expected == null && testOpts().ignoreNotHardened -> {
            }
            actual != expected -> {
                if (!testOpts().ignoreIncorrect) {
                    throw ArtAssertionError(
                        bang(ctx.assertionDescr),
                        visualPayload = renderDiff(
                            expected = expected ?: "--- Not yet hardened ---",
                            actual = actual,
                            actualTestShit = actual
                        ).tre
                    )
                }
            }
        }
    }

    TestGlobal.testShitBeingAssertedID = null
}

class ShowTestBannerContext {
    val stackCapture = CaptureStackException()
    val initialScrollTop = document.body!!.scrollTop
    val bannerButtonStyle = Style()

    var banner by notNullOnce<Control2>()
    var verticalPosition by notNull<VerticalPosition>()
    var horizontalPosition by notNull<HorizontalPosition>()
    var descriptionPanel by notNullOnce<Placeholder>()
    var acceptCurrentShit by notNullOnce<suspend () -> Unit>()
    var assertionDescr by notNullOnce<String>()
}

suspend fun showAssertionTestBanner(ctx: ShowTestBannerContext, p: AssertScreenHTMLParams, kind: TestBannerKind, renderSpecificButtons: (ElementBuilder) -> Unit = {}) {
    showTestBanner(ctx, ctx.assertionDescr, p.assertionID, kind, renderSpecificButtons)
}

suspend fun showTestBanner(ctx: ShowTestBannerContext, title: String, subtitle: String, kind: TestBannerKind, renderSpecificButtons: (ElementBuilder) -> Unit = {}) {
    _currentAssertionBannerKind = kind
    val className = kind.className
    assertionBannerPause = ResolvableShit<Unit>()
    ctx.banner = Control2.from {
        val style = Style()
        exhaustive=when (ctx.verticalPosition) {
            VerticalPosition.TOP -> style.top = 0
            VerticalPosition.BOTTOM -> style.bottom = 0
        }
        exhaustive=when (ctx.horizontalPosition) {
            HorizontalPosition.LEFT -> style.left = 0
            HorizontalPosition.RIGHT -> style.right = 0
        }
        kdiv(className = className, baseStyle = style){o->
            o- hor1(style = Style(marginBottom = "0.5rem")){o->
                o- Button(key = buttons.assertionBanner.play, icon = fa.play, style = ctx.bannerButtonStyle, onClick = {
                    assertionBannerPause.resolve()
                })
                o- killTestButton()
                o- Button(
                    icon = when (ctx.verticalPosition) {
                        VerticalPosition.TOP -> fa.arrowDown
                        VerticalPosition.BOTTOM -> fa.arrowUp
                    },
                    style = ctx.bannerButtonStyle,
                    onClick = {
                        ctx.verticalPosition = when (ctx.verticalPosition) {
                            VerticalPosition.TOP -> VerticalPosition.BOTTOM
                            VerticalPosition.BOTTOM -> VerticalPosition.TOP
                        }
                        ctx.banner.update()
                    })
                o- Button(
                    icon = when (ctx.horizontalPosition) {
                        HorizontalPosition.LEFT -> fa.arrowRight
                        HorizontalPosition.RIGHT -> fa.arrowLeft
                    },
                    style = ctx.bannerButtonStyle,
                    onClick = {
                        ctx.horizontalPosition = when (ctx.horizontalPosition) {
                            HorizontalPosition.LEFT -> HorizontalPosition.RIGHT
                            HorizontalPosition.RIGHT -> HorizontalPosition.LEFT
                        }
                        ctx.banner.update()
                    })
                o- rerunTestButton()
                o- rerunTestSlowlyButton()
                renderSpecificButtons(o)
            }

            ctx.descriptionPanel = Placeholder(kdiv{o->
                o- kdiv{o->
                    o- link(title = title, color = BLACK, onClick = {
                        revealStack(ctx.stackCapture, muteConsole = true)
                    })
                }
                o- kdiv(fontSize = "75%", fontWeight = "normal"){o->
                    o- subtitle
                }
            })
            o- ctx.descriptionPanel
        }
    }
    val bannerPane = old_debugPanes.put(ctx.banner)

    val keyListener = fun(e: Event) {
        e as KeyboardEvent
        when (e.key) {
            "n" -> assertionBannerPause.resolve()
            "N" -> {
                TestGlobal.forcedTestOpts = testOpts().copy(
                    stopOnAssertions = false,
                    ignoreIncorrect = true,
                    ignoreNotHardened = true)
                assertionBannerPause.resolve()
            }
            "y" -> {
                asu {ctx.acceptCurrentShit()}
            }
        }
    }
    window.addEventListener("keydown", keyListener)

    try {
        testPausedOnAssertion = true
        pausedOnAssertion.resolve()
        await(assertionBannerPause.promise)
    } finally {
        testPausedOnAssertion = false
        pausedOnAssertion = ResolvableShit()
        window.removeEventListener("keydown", keyListener)
        old_debugPanes.remove(bannerPane)
        document.body!!.scrollTop = ctx.initialScrollTop
    }
}


