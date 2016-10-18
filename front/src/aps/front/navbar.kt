/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

fun jsFacing_renderTopNavbar_calledByFuckingUI(ui: dynamic, arg: dynamic): dynamic {
    // {highlightedItem}
    val highlightedItem = arg.highlightedItem

    fun _t(en: String, ru: String) = ru
    return renderTopNavbar(theClientKind, json("highlightedItem" to highlightedItem, "t" to ::_t, "ui" to ui))
}

fun renderTopNavbar(clientKind: UserKind, arg: dynamic): dynamic {
    // {highlightedItem, t, ui}
    val highlightedItem = arg.highlightedItem; val ui = arg.ui

    val t: (String, String) -> String = arg.t

    val user: UserRTO? = if (ui) ui.getUser() else null

    fun TopNavItem(def: dynamic): dynamic {
        // #extract {counter} from def
        val counter = def.counter

        val active = highlightedItem == def.name
        val res = Shitus.TopNavItem(global.Object.assign(def, json(
            "shame" to "TopNavItem-${def.name}",
            "tame" to "${sufindex("TopNavItem", counter[0]++)}",
            "tattrs" to json("active" to (active || js("undefined"))),
            "ui" to ui,
            "active" to active)))
        // res.name = def.name
        return res
    }

    var proseItems: dynamic = undefined
    val proseCounter = jsArrayOf(0)
    if (clientKind == UserKind.CUSTOMER) {
        proseItems = jsArrayOf(
        TopNavItem(json("name" to "why", "title" to t("Why Us?", "Почему мы?"), "counter" to proseCounter)),
        TopNavItem(json("name" to "prices", "title" to t("Prices", "Цены"), "counter" to proseCounter)),
        TopNavItem(json("name" to "samples", "title" to t("Samples", "Примеры"), "counter" to proseCounter)),
        TopNavItem(json("name" to "faq", "title" to t("FAQ", "ЧаВо"), "counter" to proseCounter)),
        TopNavItem(json("name" to "contact", "title" to t("Contact Us", "Связь"), "counter" to proseCounter)),
        TopNavItem(json("name" to "blog", "title" to t("Blog", "Блог"), "counter" to proseCounter))
        )
    } else {
        if (user == null || user.kind != UserKind.ADMIN) {
            proseItems = jsArrayOf(
            TopNavItem(json("name" to "why", "title" to t("Why Us?", "Почему мы?"), "counter" to proseCounter)),
            TopNavItem(json("name" to "prices", "title" to t("Prices", "Цены"), "counter" to proseCounter)),
            TopNavItem(json("name" to "faq", "title" to t("FAQ", "ЧаВо"), "counter" to proseCounter))
            )
        }
    }

    var privateItems: dynamic = undefined
    val privateCounter = jsArrayOf(0)
    if (user != null) {
        if (clientKind == UserKind.CUSTOMER) {
            privateItems = jsArrayOf(
            TopNavItem(json("name" to "orders", "title" to t("My Orders", "Мои заказы"), "counter" to privateCounter)),
            TopNavItem(json("name" to "support", "title" to t("Support", "Поддержка"), "liveStatusFieldName" to "supportMenuBadge", "counter" to privateCounter))
            )
        } else {
            if (user.kind == UserKind.WRITER) {
                privateItems = lodash.compact(jsArrayOf(
                user.state == UserState.COOL && TopNavItem(json("name" to "orders", "title" to t("My Orders", "Мои заказы"), "counter" to privateCounter)),
                user.state == UserState.COOL && TopNavItem(json("name" to "store", "title" to t("Store", "Аукцион"), "counter" to privateCounter)),
                TopNavItem(json("name" to "profile", "title" to t("Profile", "Профиль"), "counter" to privateCounter))

                // TODO:vgrechka Reenable Support navitem...    11a150ac-97fd-48ce-8ba6-67d0559a2768
                // TopNavItem(json("name" to "support", "title" to t("Support", "Поддержка"), "liveStatusFieldName" to "supportMenuBadge", "counter" to privateCounter))
                ))
            } else if (user.kind == UserKind.ADMIN) {
                privateItems = jsArrayOf()
                // privateItems.push(TopNavItem(json("name" to "admin-heap", "title" to t("TOTE", "Куча"), "liveStatusFieldName" to "heapSize", "counter" to privateCounter)))
                privateItems.push(TopNavItem(json("name" to "admin-users", "title" to t("Users", "Юзеры"), "counter" to privateCounter)))
                if (user.roles.contains(UserRole.SUPPORT)) {
                    // TODO:vgrechka Reenable Support navitem...    9c49cfeb-86c1-4d86-85ed-6430e14946d8
                    // privateItems.push(TopNavItem(json("name" to "support", "title" to t("Support", "Поддержка"), "liveStatusFieldName" to "supportMenuBadge", "counter" to privateCounter)))
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
        if (ui.getUser().state == "COOL") {
            rightNavbarItem = TopNavItem(json("name" to "dashboard", "title" to t(user.firstName), "counter" to jsArrayOf(0)))
        }
    } else {
        leftNavbarItems = proseItems
        rightNavbarItem = TopNavItem(json("name" to "sign-in", "title" to t("Sign In", "Вход"), "counter" to jsArrayOf(0)))
    }

    var brand: dynamic = undefined
    if (clientKind == UserKind.CUSTOMER) {
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

                    if (MODE == "debug" && e && e.ctrlKey && e.shiftKey) {
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

                    jshit.effects.blinkOn(json("target" to jshit.byid(id).parent(), "fixed" to true, "dleft" to dleft, "dwidth" to dwidth))
                    global.testGlobal["topNavbarLink_" + name + "_blinks"] = true

                    if ((!jsFacing_isDynamicPage(name) || jsArrayOf("sign-in", "sign-up").indexOf(name) != -1) && !(jshit.isInTestScenario() && jshit.getTestSpeed() == "fast")) {
                        __await<dynamic>(Shitus.delay(global.ACTION_DELAY_FOR_FANCINESS))
                    }
                    __await<dynamic>(ui.pushNavigate(href))

                    global.setTimeout({
                        jshit.effects.blinkOff()
                        global.testGlobal["topNavbarLink_" + name + "_blinks"] = false
                        global.bsClearMenus()
                    }, 250)

                    return __asyncResult(Unit)
                }

                me = {
                    "click" to {"__async"
                        if (jshit.getTestSpeed() == "slow") {
                            me.showHand()
                            __await<dynamic>(jshit.utils.delay(global.DEBUG_ACTION_HAND_DELAY))
                            me.hideHand()
                            __await<dynamic>(onClick(js("undefined")))
                        } else {
                            __await<dynamic>(onClick(js("undefined")))
                        }
                    }
                    "showHand" to {_arg: dynamic ->
                        // {testActionHandOpts}={}
                        val arg = if (_arg) arg else js("({})")
                        val testActionHandOpts = arg.testActionHandOpts

                        testActionHand = jshit.showTestActionHand(global.Object.assign(json("target" to jshit.byid(id)), testActionHandOpts))
                    }
                    "hideHand" to {
                        testActionHand.delete()
                    }
                }
                global.testGlobal.topNavbarLinks[name] = me

                jshit.byid(id).on("click", ::onClick)


            },

            "componentWillUnmount" to {
                jsFacing_deleteKey(global.testGlobal.topNavbarLinks, name)
                jshit.byid(id).off()
            }
        ))
    }

    return Shitus.nava(json("className" to "navbar navbar-default navbar-fixed-top"),
        Shitus.diva(json("className" to "container-fluid"),
            Shitus.diva(json("className" to "navbar-header"),
                makeLink("home", brand, "navbar-brand")),

            Shitus.diva(json("style" to json("textAlign" to "left")),
                Shitus.ula.apply(null, js("[]").concat(
                    json("tame" to "topNavLeft", "id" to "leftNavbar", "className" to "nav navbar-nav", "style" to json("float" to "none", "display" to "inline-block", "verticalAlign" to "top")),
                    leftNavbarItems)),
            rightNavbarItem && Shitus.ula(json("tame" to "topNavRight", "id" to "rightNavbar", "className" to "nav navbar-nav navbar-right"),
                rightNavbarItem))))
}

