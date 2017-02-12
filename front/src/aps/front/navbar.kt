/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import aps.ClientKind.*
import aps.UserKind.*
import into.kommon.*
import kotlin.js.json

fun renderTopNavbar(clientKind: ClientKind,
                    t: (String, String) -> String,
                    highlight: PageSpec? = null,
                    ui: World? = null,
                    rightLinkStyle: Style = Style()
): ReactElement {
    val user = ui?.getUser()

    fun renderItem(p: PageSpec, title: String? = null, linkStyle: Style = Style()) = bang(
        TopNavItem(ui, p, active = p == highlight, title = title, linkStyle = linkStyle).toReactElement())

    val rightEl = run {
        class Shit(val page: PageSpec, val title: String? = null)
        val shit = when (clientKind) {
            UA_CUSTOMER -> {
                val q = pages.uaCustomer
                when (user) {
                    null -> Shit(q.signIn)
                    else -> Shit(q.dashboard, user.firstName)
                }
            }
            UA_WRITER -> {
                val q = pages.uaWriter
                when (user) {
                    null -> Shit(q.signIn)
                    else -> Shit(q.dashboard, user.firstName)
                }
            }
        }
        renderItem(shit.page, title = shit.title, linkStyle = rightLinkStyle)
    }

    val staticPages = when (clientKind) {
        UA_CUSTOMER -> {
            val q = pages.uaCustomer
            listOf(q.why, q.prices, q.samples, q.faq, q.contact, q.blog, q.makeOrder)
        }
        UA_WRITER -> {
            val q = pages.uaWriter
            listOf(q.why, q.prices, q.samples, q.faq)
        }
    }

    val leftEls = mutableListOf<ReactElement>()
    when (user) {
        null -> {
            leftEls += staticPages.map {renderItem(it)}
        }
        else -> {
            leftEls += reactCreateElement(
                "li",
                json("className" to "dropdown"),
                listOf(
                    reactCreateElement(
                        "a",
                        json("href" to "#",
                             "className" to "dropdown-toggle skipClearMenus",
                             "style" to when {
                                 staticPages.any {it == highlight} -> Style(backgroundColor = "#e7e7e7")
                                 else -> Style()
                             }.toReactStyle(),
                             "data-toggle" to "dropdown",
                             "role" to "button"),
                        listOf(
                            t("Stuff", "Стафф").asReactElement(),
                            reactCreateElement(
                                "span",
                                json("className" to "caret",
                                     "style" to json("marginLeft" to 5))))),
                    reactCreateElement(
                        "ul",
                        json("className" to "dropdown-menu"),
                        staticPages.map {renderItem(it)})))

            val privatePages = when (clientKind) {
                UA_CUSTOMER -> {
                    val q = pages.uaCustomer
                    listOf(q.orders)
                }
                UA_WRITER -> {
                    val q = pages.uaWriter
                    listOf(q.orders, q.store)
                }
            }
            leftEls += privatePages.map {renderItem(it)}
        }
    }

    val brandTitle = when (clientKind) {
        UA_CUSTOMER -> "APS"
        UA_WRITER -> {
            when (user?.kind) {
                ADMIN -> t("Admin", "Админ")
                else -> t("Writer", "Писец")
            }
        }
    }

    return reactCreateElement(
        "nav",
        json("className" to "navbar navbar-default navbar-fixed-top"),
        listOf(
            reactCreateElement(
                "div",
                json("className" to "container"),
                listOf(
                    reactCreateElement(
                        "div",
                        json("className" to "navbar-header"),
                        listOf(makeBrandLink(ui, "home", brandTitle, "navbar-brand"))),
                    reactCreateElement(
                        "div",
                        json("style" to json("textAlign" to "left")),
                        listOf(
                            reactCreateElement(
                                "ul",
                                json("id" to "leftNavbar",
                                     "className" to "nav navbar-nav",
                                     "style" to json(
                                         "float" to "none",
                                         "display" to "inline-block",
                                         "verticalAlign" to "top")),
                                leftEls),
                            reactCreateElement(
                                "ul",
                                json("id" to "rightNavbar",
                                     "className" to "nav navbar-nav navbar-right"),
                                listOf(rightEl))))))))
}

class TopNavItem(
    val ui: World?,
    val page: PageSpec,
    val title: String? = null,
    val linkStyle: Style = Style(),
    val active: Boolean
) : Control2() {
//    private val href = if (page.path == "index") "/" else "${page.path}.html"
    private val href = page.path + ".html"
    private val aid = puid()

    companion object {
        val instances = mutableMapOf<PageSpec, TopNavItem>()

        fun instance(page: PageSpec): TopNavItem {
            return instances[page] ?: bitch("No TopNavItem keyed `$page`")
        }
    }

    override fun render(): ToReactElementable {
        return reactCreateElement(
            "li",
            json(
                "id" to elementID,
                "className" to (if (active) "active" else ""),
                "onClick" to {e: ReactEvent ->
                    preventAndStop(e)
                    asu {click()}
                }
            ),
            listOf(
                reactCreateElement(
                    "a",
                    json(
                        "id" to aid,
                        "href" to href,
                        "style" to linkStyle.toReactStyle()
                    ),
                    listOf(
                        (title ?: page.navTitle ?: wtf("TopNavItem title")).asReactElement()
                    )
                )
            )
        ).toToReactElementable()
    }

    suspend fun click() {
        var dleft = 0
        var dwidth = 0
        // TODO:vgrechka Is this still needed?
        if (page.path == "index") { // XXX For some reason jQuery cannot find width/offset of navbar-header element precisely
            dleft = -15
            dwidth = 15
        }

        val blinker = await(effects).blinkOn(byid(aid).parent(), BlinkOpts(fixed = true, dleft = dleft, dwidth = dwidth, overHeader = true))
        TestGlobal.topNavItemTickingLock.sutPause()

        ui!!.pushNavigate(href)

        await(delay(250))
        blinker.unblink()
        ExternalGlobus.bsClearMenus()
        TestGlobal.topNavItemDoneLock.sutPause()
    }

    override fun componentDidMount() {
        instances[page] = this
    }

    override fun componentWillUnmount() {
        instances.remove(page)
    }
}

suspend fun topNavItemClick(page: TestRef<PageSpec>, handOpts: HandOpts = HandOpts()) {
    val target = TopNavItem.instance(page.it)
    await(TestUserActionAnimation.hand(target, handOpts))
    notAwait {target.click()}
}

suspend fun topNavItemSequence(
    descr: String? = null,
    page: TestRef<PageSpec>,
    aid: String
) {
    sequence(
        action = {
            topNavItemClick(page)
        },
        descr = descr ?: "Click nav item ${page.it.navTitle}",
        steps = listOf(
            PauseAssertResumeStep(TestGlobal.topNavItemTickingLock, "$aid--1"),
//            DumbStep {TestGlobal.defaultAssertScreenOpts = AssertScreenOpts(
//                bannerVerticalPosition = VerticalPosition.BOTTOM,
//                bannerHorizontalPosition = HorizontalPosition.RIGHT)},
            PauseAssertResumeStep(TestGlobal.topNavItemDoneLock, "$aid--2")
        )
    )
}



// TODO:vgrechka @kill
private fun makeBrandLink(ui: World?, name: String, title: String, className: String): ReactElement {
    val id = puid()
    val href = if (name == "home") "/" else "$name.html"

    var dleft = 0; var dwidth = 0
    if (name == "home") { // XXX For some reason jQuery cannot find width/offset of navbar-header element precisely
        dleft = -15
        dwidth = 15
    }

    return jsFacing_elcl(json(
        "render" to {
            Shitus.aa(json("id" to id, "className" to className, "href" to href), title)
        },

        "componentDidMount" to {
            // XXX Have to add event handler in weird way in order to prevent Bootstrap from hiding dropdown.
            //     It turned out, React doesn't actually add event handlers on elements, that's why e.stopPropagation()
            //     in onClick(e) doesn't cancel non-React handlers on upper-level elements.
            //
            //     https://facebook.github.io/react/docs/interactivity-and-dynamic-uis.html#under-the-hood-autobinding-and-event-delegation

            var me: dynamic = undefined; var testActionHand: dynamic = undefined

            fun onClick(e: dynamic): Promisoid<Unit> = async {
                if (e) { // Not simulated in test
                    if (e.ctrlKey && !e.shiftKey) return@async Unit // Allow debug revelations
                    e.preventDefault()
                    e.stopPropagation()
                }

                if (Globus.mode == Mode.DEBUG && e && e.ctrlKey && e.shiftKey) {
                    console.warn("TODO:vgrechka Implement top navbar action capturing in Kotlin    de9fd7c7-2bf1-4ce1-ad97-229635093e58")
//                        val cp = getCapturePane()
//                        cp.show()
//                        cp.addCode("                // Action\n"
//                            + "                ${"#"}hawait testGlobal.topNavbarLinks["${name}"].click()\n"
//                        + "                ${"#"}hawait art.uiStateAfterLiveStatusPolling(json($tag: "${uuid()}", expected: {\n"
//                        + "\n"
//                        + "                }))\n"
//                        )
//                        cp.focusAndSelect()
//                        return
                }

                await(effects).blinkOn(byid(id).parent(), BlinkOpts(fixed = true, dleft = dleft, dwidth = dwidth, overHeader = true))
//                    TestGlobal["topNavbarLink_" + name + "_blinks"] = true

                fuckOff("Don't use makeBrandLink")
//                if ((!isDynamicPage(name) || jsArrayOf("sign-in", "sign-up").indexOf(name) != -1) && !(isInTestScenario() && art.testSpeed == "fast")) {
//                    await<dynamic>(Shitus.delay(global.ACTION_DELAY_FOR_FANCINESS))
//                }
                ui!!.pushNavigate(href)

//                global.setTimeout({async{
//                                      await(effects).blinkOff()
////                        TestGlobal["topNavbarLink_" + name + "_blinks"] = false
//                                      global.bsClearMenus()
//                                  }}, 250)

                return@async Unit
            }

            me = {
                "click" to {async{
                    if (art.testSpeed == "slow") {
                        me.showHand()
                        await<dynamic>(Shitus.delay(global.DEBUG_ACTION_HAND_DELAY))
                        me.hideHand()
                        await<dynamic>(onClick(js("undefined")))
                    } else {
                        await<dynamic>(onClick(js("undefined")))
                    }
                }}
            }
            TestGlobal.topNavbarLinks[name] = me

            Shitus.byid(id).on("click", ::onClick)


        },

        "componentWillUnmount" to {
            TestGlobal.topNavbarLinks.remove(name)
//                jsFacing_deleteKey(TestGlobal.topNavbarLinks, name)
            Shitus.byid(id).off()
        }
    ))
}



