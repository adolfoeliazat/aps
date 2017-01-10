package aps.front

import aps.*
import aps.Color.*
import into.kommon.*
import jquery.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.window
import kotlin.properties.Delegates.notNull

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

fun tillPausedOnAssertion() = pausedOnAssertion.promise

fun resumePausedAssertion() = assertionBannerPause.resolve()

fun killTestButton(): Button {
    return Button(icon = fa.bomb, onClick = {
        assertionBannerPause.reject(Exception("Fucking killed"))
    })
}

class AssertScreenOpts(
    val bannerVerticalPosition: VerticalPosition = VerticalPosition.BOTTOM,
    val bannerHorizontalPosition: HorizontalPosition = HorizontalPosition.LEFT
)

fun TestScenarioBuilder.assertScreenHTML(descr: String?, assertionID: String, opts: AssertScreenOpts = AssertScreenOpts()) {
    val stackCapture = CaptureStackException()
    act {TestGlobal.testShitBeingAssertedID = assertionID}

    val stepTitle = "HTML: $descr"
    checkOnAnimationFrame(stepTitle) {async<Unit>{
        val expected = await(fuckingRemoteCall.loadTestShit(assertionID))

        val actual = buildString {
            append("-------------------- NAVBAR --------------------\n\n")
            append(tidyHTML(takeHTMLForAssertion(SELECTOR_NAVBAR), transformNavbarLineTidy))
            if (!endsWith("\n")) append("\n")
            append("\n-------------------- ROOT --------------------\n\n")
            append(tidyHTML(takeHTMLForAssertion(SELECTOR_ROOT), transformRootLineTidy))
        }

        if (testOpts().stopOnAssertions) {
            await(object {
                val bannerButtonStyle = Style()

                var banner by notNull<Control2>()
                var verticalPosition = opts.bannerVerticalPosition
                var horizontalPosition = opts.bannerHorizontalPosition
                var capturedVisualShit = false

                fun acceptCurrentShit(): Promise<Unit> = async {
                    await(captureVisualShitIfNeeded())
                    await(send(SaveCapturedVisualShitRequest()))
                    await(send(HardenScreenHTMLRequest()-{o->
                        o.assertionID = assertionID
                        o.html = actual
                    }))
                    assertionBannerPause.resolve()
                }

                val shit = async {
                    when {
                        expected == null -> {
                            await(showTestBanner(
                                AssertionBannerKind.NOT_HARDENED,
                                renderSpecificButtons = {o->
                                    o- acceptButton()
                                }))
                        }

                        actual == expected -> {
                            if (!testOpts().dontStopOnCorrectAssertions) {
                                await(showTestBanner(AssertionBannerKind.CORRECT))
                            }
                        }

                        actual != expected -> {
                            val pane = debugPanes.put(byid(ELID_UNDER_FOOTER), kdiv(
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
                                await(showTestBanner(
                                    AssertionBannerKind.INCORRECT,
                                    renderSpecificButtons = {o->
                                        o- Button(title = "Diff", style = bannerButtonStyle, onClick = {
                                            verticalPosition = opts.bannerVerticalPosition
                                            horizontalPosition = opts.bannerHorizontalPosition
                                            banner.update()
//                                                byid("fuckingDiff").scrollBodyToShit()
                                            nextDiff().scrollBodyToShit(dy = -70)
                                        })
                                        o- Button(key = "assertionBanner-vdiff", title = "VDiff", style = bannerButtonStyle, onClicka = {async<Unit>{
                                            openVisualDiff()
                                        }})
                                        o- acceptButton()
                                    }))
                            } finally {
                                debugPanes.remove(pane)
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
                                o- hor2{o->
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

                        abstract fun promiseBase64(): Promise<String>

                        fun renderButton() = Button(
                            title = buttonTitle,
                            style = bannerButtonStyle.copy(
                                borderLeft = if (mode != this) null else
                                    "1rem solid $ORANGE_300"
                            ),
                            onClick = {
                                val scroller = byid0(mode.scrollerID)!!
                                mode.scrollLeft = scroller.scrollLeft
                                mode.scrollTop = scroller.scrollTop

                                mode = this
                                ctrl.update()

                                timeoutSet(0) {
                                    byid0(mode.scrollerID)?.let {scroller -> // May be loading, so no scroller yet
                                        scroller.scrollLeft = mode.scrollLeft
                                        scroller.scrollTop = mode.scrollTop
                                    }
                                }
                            }
                        )
                    }

                    val diffMode = object:Mode() {
                        override val buttonTitle = "Diff"

                        override fun promiseBase64() = async {
                            val res = await(send(DiffCapturedVisualShitWithSavedRequest()-{o->
                                o.id = assertionID
                            }))
                            res.base64
                        }
                    }

                    val hardenedMode = object:Mode() {
                        override val buttonTitle = "Hardened"

                        override fun promiseBase64() = async {
                            val res = await(send(GetCapturedVisualShitRequest()-{o->
                                o.id = assertionID
                            }))
                            res.base64
                        }
                    }

                    val currentMode = object:Mode() {
                        override val buttonTitle = "Current"

                        override fun promiseBase64() = async {
                            val res = await(send(GetCurrentCapturedVisualShitRequest()))
                            res.base64
                        }
                    }

                    var mode: Mode = diffMode

                    val ctrl: Control2 by lazy {Control2.from {
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
                                    o- Button(key = "visualDiffPane-accept", icon = fa.check, title = "Accept", style = bannerButtonStyle, onClick = {
                                        debugPanes.remove(visualDiffPane)
                                        acceptCurrentShit()
                                    })
                                    o- Button(icon = fa.close, style = bannerButtonStyle, onClick = {
                                        debugPanes.remove(visualDiffPane)
                                    })
                                }
                            }
                            o- kdiv(className = mycss.content){o->
                                o- mode.view
                            }
                        }
                    }}

                    init {
                        async {
                            await(captureVisualShitIfNeeded())
                            visualDiffPane = debugPanes.put(ctrl)
                        }
                    }
                }

                fun captureVisualShitIfNeeded() = async {
                    if (!capturedVisualShit) {
                        await(captureVisualShit(assertionID))
                        capturedVisualShit = true
                    }
                }

                fun acceptButton() = Button(
                    key = "assertionBanner-accept",
                    icon = fa.check,
                    style = bannerButtonStyle,
                    onClicka = this::acceptCurrentShit)


                fun showTestBanner(kind: AssertionBannerKind, renderSpecificButtons: (ElementBuilder) -> Unit = {}) = async {
                    _currentAssertionBannerKind = kind
                    val className = kind.className
                    assertionBannerPause = ResolvableShit<Unit>()
                    banner = Control2.from {
                        val style = Style()
                        exhaustive/when (verticalPosition) {
                            VerticalPosition.TOP -> style.top = 0
                            VerticalPosition.BOTTOM -> style.bottom = 0
                        }
                        exhaustive/when (horizontalPosition) {
                            HorizontalPosition.LEFT -> style.left = 0
                            HorizontalPosition.RIGHT -> style.right = 0
                        }
                        kdiv(className = className, baseStyle = style){o->
                            o- hor1(marginBottom = "0.5rem"){o->
                                o- Button(key = "assertionBanner-play", icon = fa.play, style = bannerButtonStyle, onClick = {
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
                            o- link(title = "Assertion: $descr", color = BLACK, onClick = {
                                revealStack(stackCapture, muteConsole = true)
                            })
//                                o- "Assertion: $descr"
                        }
                    }
                    val pane = debugPanes.put(banner)

                    fun keyListener(e: Event) {
                        e as KeyboardEvent
                        if (e.key == "n") {
                            assertionBannerPause.resolve()
                        }
                    }
                    window.addEventListener("keydown", ::keyListener)

                    try {
                        pausedOnAssertion.resolve()
                        await(assertionBannerPause.promise)
                    } finally {
                        pausedOnAssertion = ResolvableShit()
                        window.removeEventListener("keydown", ::keyListener)
                        debugPanes.remove(pane)
                    }
                }
            }.shit)
        } else {
            when {
                expected == null && testOpts().ignoreNotHardened -> {}
                actual != expected -> throw ArtAssertionError(
                    stepTitle,
                    visualPayload = renderDiff(
                        expected = expected ?: "--- Not yet hardened ---",
                        actual = actual,
                        actualTestShit = actual
                    )
                )
            }
        }
    }}

    act {TestGlobal.testShitBeingAssertedID = null}
}

