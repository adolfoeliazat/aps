/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.*
import org.w3c.dom.events.KeyboardEvent

fun renderTopNavbar(clientKind: ClientKind,
                    t: (String, String) -> String,
                    highlightedItem: String? = null,
                    ui: World? = null,
                    rightNavbarItemAStyle: Style = Style()
): ReactElement {
    val user: UserRTO? = ui?.getUser()

    fun item(name: String, title: String, aStyle: Style = Style()): ReactElement {
        return TopNavItem(ui = ui, name = name, title = title, aStyle = aStyle,
                          active = highlightedItem == name
        ).toReactElement()
    }

    var proseItems: dynamic = undefined
    if (clientKind == ClientKind.UA_CUSTOMER) {
        proseItems = jsArrayOf(
        item(name = "why", title = t("Why Us?", "Почему мы?")),
        item(name = "prices", title = t("Prices", "Цены")),
        item(name = "samples", title = t("Samples", "Примеры")),
        item(name = "faq", title = t("FAQ", "ЧаВо")),
        item(name = "contact", title = t("Contact Us", "Связь")),
        item(name = "blog", title = t("Blog", "Блог"))
        )
    } else {
        if (user == null || user.kind != UserKind.ADMIN) {
            proseItems = jsArrayOf(
            item(name = "why", title = t("Why Us?", "Почему мы?")),
            item(name = "prices", title = t("Prices", "Цены")),
            item(name = "faq", title = t("FAQ", "ЧаВо"))
            )
        }
    }

    var privateItems: dynamic = undefined
    if (user != null) {
        if (clientKind == ClientKind.UA_CUSTOMER) {
            privateItems = jsArrayOf(
            item(name = "orders", title = t("My Orders", "Мои заказы")),
            item(name = "support", title = t("Support", "Поддержка")))
        } else {
            if (user.kind == UserKind.WRITER) {
                privateItems = lodash.compact(jsArrayOf(
                if (user.state == UserState.COOL) item(name = "orders", title = t("My Orders", "Мои заказы")) else null,
                if (user.state == UserState.COOL) item(name = "store", title = t("Store", "Аукцион")) else null,
                item(name = "profile", title = t("Profile", "Профиль"))
                ))
            } else if (user.kind == UserKind.ADMIN) {
                privateItems = jsArrayOf()
                privateItems.push(item(name = "admin-users", title = t("Users", "Юзеры")))
                if (user.roles.contains(UserRole.SUPPORT)) {
                    // TODO:vgrechka Reenable Support navitem...    9c49cfeb-86c1-4d86-85ed-6430e14946d8
                }
            }
        }
    }

    var leftNavbarItems: dynamic = undefined; var rightNavbarItem: dynamic = undefined
    if (user != null) {
        val liaid = puid()
        leftNavbarItems = jsArrayOf()
        if (user.kind != UserKind.ADMIN) {
            var dropdownAStyle: dynamic = undefined
            if (proseItems.some{x -> x.name == highlightedItem}) {
                dropdownAStyle = json("backgroundColor" to "#e7e7e7")
            }
            leftNavbarItems.push(
                Shitus.lia(json("tame" to "prose", "className" to "dropdown"),
                    Shitus.aa(json("href" to "#", "className" to "dropdown-toggle skipClearMenus", "style" to dropdownAStyle, "data-toggle" to "dropdown", "role" to "button"), t("Prose", "Проза"), Shitus.spana(json("className" to "caret", "style" to json("marginLeft" to 5)))),
                    Shitus.ula.apply(null, js("[]").concat(
                        json("className" to "dropdown-menu"),
                        proseItems))))
        }
        leftNavbarItems.push.apply(leftNavbarItems, privateItems)
        // @wip rejection
        rightNavbarItem = item(name = "dashboard", title = user.firstName, aStyle = rightNavbarItemAStyle)
    } else {
        leftNavbarItems = proseItems
        rightNavbarItem = item(name = "sign-in", title = t("Sign In", "Вход"), aStyle = rightNavbarItemAStyle)
    }

    var brand: dynamic = undefined
    if (clientKind == ClientKind.UA_CUSTOMER) {
        brand = "APS"
    } else {
        brand = t("Writer", "Писец")
        if (user != null && user.kind == UserKind.ADMIN) {
            brand = t("Admin", "Админ")
        }
    }


    // TODO:vgrechka @refactor Kill renderTopNavbar::makeLink    47924ff3-db76-463f-9a3e-1099586d6219
    fun makeLink(name: dynamic, title: dynamic, className: dynamic): dynamic {
        val id = puid()
        val href = if (name == "home") "/" else "${name}.html"

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

                fun onClick(e: dynamic): Promise<Unit> {"__async"
                    if (e) { // Not simulated in test
                        if (e.ctrlKey && !e.shiftKey) return __asyncResult(Unit) // Allow debug revelations
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

                    effects.blinkOn(json("target" to Shitus.byid(id).parent(), "fixed" to true, "dleft" to dleft, "dwidth" to dwidth))
//                    TestGlobal["topNavbarLink_" + name + "_blinks"] = true

                    if ((!jsFacing_isDynamicPage(name) || jsArrayOf("sign-in", "sign-up").indexOf(name) != -1) && !(isInTestScenario() && art.testSpeed == "fast")) {
                        __await<dynamic>(Shitus.delay(global.ACTION_DELAY_FOR_FANCINESS))
                    }
                    __await<dynamic>(ui!!.pushNavigate(href))

                    global.setTimeout({
                        effects.blinkOff()
//                        TestGlobal["topNavbarLink_" + name + "_blinks"] = false
                        global.bsClearMenus()
                    }, 250)

                    return __asyncResult(Unit)
                }

                me = {
                    "click" to {"__async"
                        if (art.testSpeed == "slow") {
                            me.showHand()
                            __await<dynamic>(Shitus.delay(global.DEBUG_ACTION_HAND_DELAY))
                            me.hideHand()
                            __await<dynamic>(onClick(js("undefined")))
                        } else {
                            __await<dynamic>(onClick(js("undefined")))
                        }
                    }
//                    "showHand" to {_arg: dynamic ->
//                        // {testActionHandOpts}={}
//                        val arg = if (_arg) arg else js("({})")
//                        val testActionHandOpts = arg.testActionHandOpts
//
//                        testActionHand = art.showTestActionHand(global.Object.assign(json("target" to Shitus.byid(id)), testActionHandOpts))
//                    }
//                    "hideHand" to {
//                        testActionHand.delete()
//                    }
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

    return Shitus.nava(json("className" to "navbar navbar-default navbar-fixed-top"),
        Shitus.diva(json("className" to "container"),
            Shitus.diva(json("className" to "navbar-header"),
                makeLink("home", brand, "navbar-brand")),

            Shitus.diva(json("style" to json("textAlign" to "left")),
                Shitus.ula.apply(null, js("[]").concat(
                    json("tame" to "topNavLeft", "id" to "leftNavbar", "className" to "nav navbar-nav", "style" to json("float" to "none", "display" to "inline-block", "verticalAlign" to "top")),
                    leftNavbarItems)),
            rightNavbarItem && Shitus.ula(json("tame" to "topNavRight", "id" to "rightNavbar", "className" to "nav navbar-nav navbar-right"),
                rightNavbarItem))))
}

private class OldSpec(
    val name: String,
    val title: String,
    val active: Boolean,
    val ui: World?,
    val aStyle: Style
)

class TopNavItem(
    val ui: World?,
    val name: String,
    val title: String,
    val aStyle: Style,
    val active: Boolean
) : Control2() {
    val oldElement: ReactElement = jsFacing_TopNavItem(OldSpec(
        name = name,
        title = title,
        active = active,
        ui = ui,
        aStyle = aStyle
    ))

    override fun render(): ToReactElementable {
        return oldElement.toToReactElementable()
    }
}

private fun jsFacing_TopNavItem(def: OldSpec): dynamic {
    val title: String = def.title
    val active: Boolean = def.active
    val ui: World? = def.ui
    val aStyle: Style = def.aStyle

    val href = if (def.name == "home") "/" else "/${def.name}.html"
    val aid = puid()

    var me: dynamic = null
    me = json(
        "render" to {
            Shitus.lia(json("id" to me.elementID, "className" to (if (active) "active" else "")),
                       Shitus.aa(json("id" to aid, "href" to href, "style" to aStyle.toReactStyle()),
                                 Shitus.spancTitle(json("title" to title))
//                    if (liveStatusFieldName != null) ui.liveBadge2(json("liveStatusFieldName" to liveStatusFieldName)) else null
                       ))
        },

        "onRootClick" to fun(e: KeyboardEvent): Promise<Unit> = async {
            preventAndStop(e)

            var dleft = 0; var dwidth = 0
            if (def.name == "home") { // XXX For some reason jQuery cannot find width/offset of navbar-header element precisely
                // @revisit
                dleft = -15
                dwidth = 15
            }

            effects.blinkOn(json("target" to Shitus.byid(aid).parent(), "fixed" to true, "dleft" to dleft, "dwidth" to dwidth))

            await<dynamic>(ui!!.pushNavigate(href))

            global.setTimeout({
                                  effects.blinkOff()
                                  global.bsClearMenus()
                              }, 250)
        }
    )

    me.controlTypeName = "TopNavItem"
    legacy_implementControlShit(json("me" to me, "def" to def, "implementTestClick" to json("onClick" to me.onRootClick)))

    return elcl(me)
}

fun TestScenarioBuilder.topNavItemClick(key: String, handOpts: HandOpts = HandOpts()) {
    imf("topNavItemClick")
//    acta("Clicking top nav item `$key`") {async<Unit>{
//        val target = TopNavItem.instance(key)
//        await(TestUserActionAnimation.hand(target, handOpts))
//        target.click() // Not await
//    }}
}


