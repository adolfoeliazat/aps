package aps.front

import aps.*
import aps.Color.*
import into.kommon.*
import jquery.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.window
import kotlin.properties.Delegates.notNull

private var testPausedOnAssertion = false
private var pausedOnAssertion = ResolvableShit<Unit>()
private var assertionBannerPause by notNull<ResolvableShit<Unit>>()
private var _currentAssertionBannerKind by notNull<AssertionBannerKind>()

val currentAssertionBannerKind get() = _currentAssertionBannerKind

enum class AssertionBannerKind(val className: String) {
    NOT_HARDENED(css.test.popup.assertion.notHardened),
    CORRECT(css.test.popup.assertion.correct),
    INCORRECT(css.test.popup.assertion.incorrect)
}

enum class VerticalPosition {
    TOP, BOTTOM
}

enum class HorizontalPosition {
    LEFT, RIGHT
}

fun isTestPausedOnAssertion() = testPausedOnAssertion

fun tillPausedOnAssertion() = pausedOnAssertion.promise

fun resumePausedAssertion() = assertionBannerPause.resolve()

fun killTestButton(): Button {
    return Button(icon = fa.bomb, onClick = {
        assertionBannerPause.reject(Exception("Fucking killed"))
    })
}

class AssertScreenOpts(
    val bannerVerticalPosition: VerticalPosition = VerticalPosition.BOTTOM,
    val bannerHorizontalPosition: HorizontalPosition = HorizontalPosition.LEFT,
    val spoilActual: Boolean = false
)

data class AssertScreenHTMLParams(
    val descr: String?,
    val assertionID: String,
    val opts: AssertScreenOpts? = null
)

suspend fun assertScreenHTML(descr: String?, assertionID: String, opts: AssertScreenOpts? = null) {
    assertScreenHTML(AssertScreenHTMLParams(descr, assertionID, opts))
}

suspend fun assertScreenHTML(p: AssertScreenHTMLParams) {
    val stackCapture: CaptureStackException? = CaptureStackException()
    val opts = p.opts ?: TestGlobal.defaultAssertScreenOpts
//    lastAssertScreenHTMLParams = p
//    act {TestGlobal.testShitBeingAssertedID = p.assertionID}

    sleep(0)             // XXX Fucking React...
    waitAnimationFrame() // It seems, both of these is needed
                         // Also, when Chrome's Elements tab is open, updates still lag

    TestGlobal.testShitBeingAssertedID = p.assertionID
    val expected = await(fuckingRemoteCall.loadTestShit(p.assertionID))

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
        gloshit.actualAssertionURL = toString()
        append("-------------------- NAVBAR --------------------\n\n")
        append(tidyHTML(takeHTMLForAssertion(SELECTOR_NAVBAR), transformNavbarLineTidy))
        if (!endsWith("\n")) append("\n")
        append("\n-------------------- ${if (!opts.spoilActual) "ROOT" else "FUCKROOT"} --------------------\n\n")
        append(tidyHTML(takeHTMLForAssertion(SELECTOR_ROOT), transformRootLineTidy))
    }

    if (testOpts().stopOnAssertions) {
        await(object {
            val bannerButtonStyle = Style()

            var banner by notNull<Control2>()
            var verticalPosition = opts.bannerVerticalPosition
            var horizontalPosition = opts.bannerHorizontalPosition
            var capturedVisualShit = false

            suspend fun acceptCurrentShit() {
                await(captureVisualShitIfNeeded())
                await(send(SaveCapturedVisualShitRequest()))
                await(send(HardenScreenHTMLRequest()-{o->
                    o.assertionID = p.assertionID
                    o.html = actual
                }))
                assertionBannerPause.resolve()
            }

            val shit = async {
                when {
                    expected == null || TestGlobal.pretendAllAssertionsNotHardened -> {
                        await(showTestBanner(
                            AssertionBannerKind.NOT_HARDENED,
                            renderSpecificButtons = {o ->
                                o- acceptButton()
                            }))
                    }

                    actual == expected -> {
                        if (!testOpts().dontStopOnCorrectAssertions) {
                            await(showTestBanner(AssertionBannerKind.CORRECT))
                        }
                    }

                    actual != expected -> {
                        val pane = old_debugPanes.put(byid(ELID_UNDER_FOOTER), kdiv(
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
                                o- renderDiff(expected = expected, actual = actual, actualTestShit = actual)
                            }
                        })

                        try {
                            val captureExists = await(send(CapturedVisualShitExistsRequest()-{o->
                                o.id = p.assertionID
                            })).exists

                            await(showTestBanner(
                                AssertionBannerKind.INCORRECT,
                                renderSpecificButtons = {o ->
                                    o- Button(title = "Diff", style = bannerButtonStyle, onClick = {
                                        verticalPosition = opts.bannerVerticalPosition
                                        horizontalPosition = opts.bannerHorizontalPosition
                                        banner.update()
//                                                byid("fuckingDiff").scrollBodyToShit()
                                        nextDiff().scrollBodyToShit(dy = -70)
                                    })
                                    if (captureExists) {
                                        o- Button(title = "VDiff", style = bannerButtonStyle, key = fconst.key.button.assertionBanner.vdiff.ref, onClicka = {
                                            async<Unit> {
                                                openVisualDiff()
                                            }
                                        })
                                    }
                                    o- acceptButton()
                                }))
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
                        style = bannerButtonStyle.copy(
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
                                o- hor1(baseStyle = Style(justifyContent = "flex-end")){o->
                                    for (m in listOf(diffMode, hardenedMode, currentMode)) {
                                        o- m.renderButton()
                                    }
                                    o- kdiv(width = "1rem")
                                    o- Button(icon = fa.check, title = "Accept", style = bannerButtonStyle, key = fconst.key.button.assertionBanner.accept.ref, onClicka = {
                                        old_debugPanes.remove(visualDiffPane)
                                        acceptCurrentShit()
                                    })
                                    o- Button(icon = fa.close, style = bannerButtonStyle, onClick = {
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
                    await(captureVisualShit(p.assertionID))
                    capturedVisualShit = true
                }
            }

            fun acceptButton() = Button(
                key = fconst.key.button.assertionBanner.accept.ref,
                icon = fa.check,
                style = bannerButtonStyle,
                onClicka = {acceptCurrentShit()})


            fun showTestBanner(kind: AssertionBannerKind, renderSpecificButtons: (ElementBuilder) -> Unit = {}) = async {
                _currentAssertionBannerKind = kind
                val className = kind.className
                assertionBannerPause = ResolvableShit<Unit>()
                banner = Control2.from {
                    val style = Style()
                    exhaustive / when (verticalPosition) {
                        VerticalPosition.TOP -> style.top = 0
                        VerticalPosition.BOTTOM -> style.bottom = 0
                    }
                    exhaustive / when (horizontalPosition) {
                        HorizontalPosition.LEFT -> style.left = 0
                        HorizontalPosition.RIGHT -> style.right = 0
                    }
                    kdiv(className = className, baseStyle = style){o->
                        o- hor1(marginBottom = "0.5rem"){o->
                            o- Button(key = fconst.key.button.assertionBanner.play.ref, icon = fa.play, style = bannerButtonStyle, onClick = {
                                assertionBannerPause.resolve()
                            })
                            o- killTestButton()
                            o- Button(
                                icon = when (verticalPosition) {
                                    VerticalPosition.TOP -> fa.arrowDown
                                    VerticalPosition.BOTTOM -> fa.arrowUp
                                },
                                style = bannerButtonStyle,
                                onClick = {
                                    verticalPosition = when (verticalPosition) {
                                        VerticalPosition.TOP -> VerticalPosition.BOTTOM
                                        VerticalPosition.BOTTOM -> VerticalPosition.TOP
                                    }
                                    banner.update()
                                })
                            o- Button(
                                icon = when (horizontalPosition) {
                                    HorizontalPosition.LEFT -> fa.arrowRight
                                    HorizontalPosition.RIGHT -> fa.arrowLeft
                                },
                                style = bannerButtonStyle,
                                onClick = {
                                    horizontalPosition = when (horizontalPosition) {
                                        HorizontalPosition.LEFT -> HorizontalPosition.RIGHT
                                        HorizontalPosition.RIGHT -> HorizontalPosition.LEFT
                                    }
                                    banner.update()
                                })
                            o- rerunTestButton()
                            o- rerunTestSlowlyButton()
                            renderSpecificButtons(o)
                        }
                        o- kdiv{o->
                            o- link(title = "Assertion: ${p.descr}", color = BLACK, onClick = {
                                if (stackCapture != null) {
                                    revealStack(stackCapture, muteConsole = true)
                                } else {
                                    bitch("No fucking stack capture")
                                }
                            })
                        }
                        o- kdiv(fontSize = "75%", fontWeight = "normal"){o->
                            o- p.assertionID
                        }
                    }
                }
                val bannerPane = old_debugPanes.put(banner)

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
                            asu {acceptCurrentShit()}
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
                }
            }
        }.shit)
    } else {
        when {
            expected == null && testOpts().ignoreNotHardened -> {
            }
            actual != expected -> {
                if (!testOpts().ignoreIncorrect) {
                    throw ArtAssertionError(
                        bang(p.descr),
                        visualPayload = renderDiff(
                            expected = expected ?: "--- Not yet hardened ---",
                            actual = actual,
                            actualTestShit = actual
                        )
                    )
                }
            }
        }
    }

    TestGlobal.testShitBeingAssertedID = null
}


