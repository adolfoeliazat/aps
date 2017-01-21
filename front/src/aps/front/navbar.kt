/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.*
import kotlin.properties.Delegates.notNull

fun renderTopNavbar(clientKind: ClientKind,
                    t: (String, String) -> String,
                    highlightedItem: String? = null,
                    ui: World? = null,
                    rightNavbarItemAStyle: Style = Style()
): ReactElement {
    val user: UserRTO? = ui?.getUser()

    class Item(val key: String, val pageName: String, val title: String, val aStyle: Style = Style()) {
        fun render(): ReactElement {
            return TopNavItem(
                key = key, ui = ui, pageName = pageName, title = title, linkStyle = aStyle,
                active = highlightedItem == pageName
            ).toReactElement()
        }
    }

    var proseItems by notNull<List<Item>>()
    if (clientKind == ClientKind.UA_CUSTOMER) {
        proseItems = listOf(
            Item(pageName = "why", title = t("Why Us?", "Почему мы?"), key = fconst.key.topNavItem.why.decl),
            Item(pageName = "prices", title = t("Prices", "Цены"), key = fconst.key.topNavItem.prices.decl),
            Item(pageName = "samples", title = t("Samples", "Примеры"), key = fconst.key.topNavItem.samples.decl),
            Item(pageName = "faq", title = t("FAQ", "ЧаВо"), key = fconst.key.topNavItem.faq.decl),
            Item(pageName = "contact", title = t("Contact Us", "Связь"), key = fconst.key.topNavItem.contact.decl),
            Item(pageName = "blog", title = t("Blog", "Блог"), key = fconst.key.topNavItem.blog.decl)
        )
    } else {
        if (user == null || user.kind != UserKind.ADMIN) {
            proseItems = listOf(
                Item(pageName = "why", title = t("Why Us?", "Почему мы?"), key = fconst.key.topNavItem.why.decl),
                Item(pageName = "prices", title = t("Prices", "Цены"), key = fconst.key.topNavItem.prices.decl),
                Item(pageName = "faq", title = t("FAQ", "ЧаВо"), key = fconst.key.topNavItem.faq.decl)
            )
        }
    }

    val privateItems = mutableListOf<Item>()
    if (user != null) {
        if (clientKind == ClientKind.UA_CUSTOMER) {
            privateItems.addAll(listOf(
                Item(pageName = "orders", title = t("My Orders", "Мои заказы"), key = fconst.key.topNavItem.orders.decl),
                Item(pageName = "support", title = t("Support", "Поддержка"), key = fconst.key.topNavItem.support.decl)
            ))
        } else {
            if (user.kind == UserKind.WRITER) {
                if (user.state == UserState.COOL) {
                    privateItems.addAll(listOf(
                        Item(pageName = "orders", title = t("My Orders", "Мои заказы"), key = fconst.key.topNavItem.orders.decl),
                        Item(pageName = "store", title = t("Store", "Аукцион"), key = fconst.key.topNavItem.store.decl)
                    ))
                }
                privateItems.addAll(listOf(
                    Item(pageName = "profile", title = t("Profile", "Профиль"), key = fconst.key.topNavItem.profile.decl)
                ))
            } else if (user.kind == UserKind.ADMIN) {
                privateItems.addAll(listOf(
                    Item(pageName = "admin-users", title = t("Users", "Юзеры"), key = fconst.key.topNavItem.adminUsers.decl)
                ))
            }
        }
    }

    val leftNavbarElements = mutableListOf<ReactElement>()
    var rightNavbarItem: ReactElement? = null
    if (user != null) {
        if (user.kind != UserKind.ADMIN) {
            val dropdownLinkStyle = when {
                proseItems.any {x -> x.pageName == highlightedItem} -> Style(backgroundColor = "#e7e7e7")
                else -> Style()
            }
            leftNavbarElements.add(
                reactCreateElement("li", json("className" to "dropdown"), listOf(
                    reactCreateElement(
                        "a",
                        json(
                            "href" to "#",
                            "className" to "dropdown-toggle skipClearMenus",
                            "style" to dropdownLinkStyle.toReactStyle(),
                            "data-toggle" to "dropdown",
                            "role" to "button"
                        ),
                        listOf(
                            t("Prose", "Проза").asReactElement(),
                            reactCreateElement(
                                "span",
                                json(
                                    "className" to "caret",
                                    "style" to json("marginLeft" to 5)
                                )
                            )
                        )
                    ),
                    reactCreateElement("ul", json("className" to "dropdown-menu"), proseItems.map(Item::render))
                )))
//            leftNavbarElements.add(
//                Shitus.lia(json("tame" to "prose", "className" to "dropdown"),
//                    Shitus.aa(json("href" to "#", "className" to "dropdown-toggle skipClearMenus", "style" to dropdownAStyle, "data-toggle" to "dropdown", "role" to "button"), t("Prose", "Проза"), Shitus.spana(json("className" to "caret", "style" to json("marginLeft" to 5)))),
//                    Shitus.ula.apply(null, js("[]").concat(
//                        json("className" to "dropdown-menu"),
//                        proseItems))))
        }
        leftNavbarElements.addAll(privateItems.map(Item::render))
        rightNavbarItem = Item(pageName = "dashboard", title = user.firstName, aStyle = rightNavbarItemAStyle, key = fconst.key.topNavItem.dashboard.decl).render()
    } else {
        leftNavbarElements.addAll(proseItems.map(Item::render))
        rightNavbarItem = Item(pageName = "sign-in", title = t("Sign In", "Вход"), aStyle = rightNavbarItemAStyle, key = fconst.key.topNavItem.signIn.decl).render()
    }

    val brandTitle = when (clientKind) {
        ClientKind.UA_CUSTOMER -> "APS"
        ClientKind.UA_WRITER -> when {
            user != null && user.kind == UserKind.ADMIN -> t("Admin", "Админ")
            else -> t("Writer", "Писец")
        }
    }

    return reactCreateElement("nav", json("className" to "navbar navbar-default navbar-fixed-top"), listOf(
        reactCreateElement("div", json("className" to "container"), listOf(
            reactCreateElement("div", json("className" to "navbar-header"), listOf(
                makeBrandLink(ui, "home", brandTitle, "navbar-brand"))),
            reactCreateElement("div", json("style" to json("textAlign" to "left")), listOf(
                reactCreateElement("ul", json("id" to "leftNavbar", "className" to "nav navbar-nav", "style" to json("float" to "none", "display" to "inline-block", "verticalAlign" to "top")), leftNavbarElements),
                ifornull(rightNavbarItem != null) {reactCreateElement("ul", json("id" to "rightNavbar", "className" to "nav navbar-nav navbar-right"), listOf(rightNavbarItem))}
            ))
        ))
    ))
}

private class OldSpec(
    val name: String,
    val title: String,
    val active: Boolean,
    val ui: World?,
    val aStyle: Style
)

class TopNavItem(
    val key: String,
    val ui: World?,
    val pageName: String,
    val title: String,
    val linkStyle: Style,
    val active: Boolean
) : Control2() {
//    private val href = if (pageName == "home") "/" else "/$pageName.html"
    private val href = if (pageName == "home") "/" else "$pageName.html"
    private val aid = puid()

    companion object {
        val instances = mutableMapOf<String, TopNavItem>()

        fun instance(key: String): TopNavItem {
            return instances[key] ?: bitch("No TopNavItem keyed `$key`")
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
                    click()
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
                        title.asReactElement()
                    )
                )
            )
        ).toToReactElementable()
    }

    fun click(): Promisoid<Unit> = async {
        var dleft = 0
        var dwidth = 0
        if (pageName == "home") { // XXX For some reason jQuery cannot find width/offset of navbar-header element precisely
            dleft = -15
            dwidth = 15
        }

        effects.blinkOn(json("target" to Shitus.byid(aid).parent(), "fixed" to true, "dleft" to dleft, "dwidth" to dwidth))
        await(TestGlobal.topNavItemTickingLock.sutPause())

        await(ui!!.pushNavigate(href))

        await(delay(250))
        effects.blinkOff()
        ExternalGlobus.bsClearMenus()
        await(TestGlobal.topNavItemDoneLock.sutPause())
    }

    override fun componentDidMount() {
        instances[key] = this
    }

    override fun componentWillUnmount() {
        instances.remove(key)
    }
}

fun TestScenarioBuilder.topNavItemClick(key: String, handOpts: HandOpts = HandOpts()) {
    acta("Clicking top nav item `$key`") {async<Unit>{
        val target = TopNavItem.instance(key)
        await(TestUserActionAnimation.hand(target, handOpts))
        target.click() // Not await
        Unit
    }}
}

fun TestScenarioBuilder.topNavItemSequence(
    descr: String,
    key: String,
    aid: String
) {
    sequence(
        buildAction = {
            topNavItemClick(key)
        },
        assertionDescr = descr,
        steps = listOf(
            TestSequenceStep(TestGlobal.topNavItemTickingLock, "$aid--1"),
            TestSequenceStep(TestGlobal.topNavItemDoneLock, "$aid--2")
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

                effects.blinkOn(json("target" to Shitus.byid(id).parent(), "fixed" to true, "dleft" to dleft, "dwidth" to dwidth))
//                    TestGlobal["topNavbarLink_" + name + "_blinks"] = true

                if ((!jsFacing_isDynamicPage(name) || jsArrayOf("sign-in", "sign-up").indexOf(name) != -1) && !(isInTestScenario() && art.testSpeed == "fast")) {
                    await<dynamic>(Shitus.delay(global.ACTION_DELAY_FOR_FANCINESS))
                }
                await<dynamic>(ui!!.pushNavigate(href))

                global.setTimeout({
                                      effects.blinkOff()
//                        TestGlobal["topNavbarLink_" + name + "_blinks"] = false
                                      global.bsClearMenus()
                                  }, 250)

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



