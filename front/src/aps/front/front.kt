/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import aps.front.Color.*
import into.kommon.*
import org.w3c.dom.events.Event

enum class Color(val string: String) {
    // https://www.google.com/design/spec/style/color.html#color-color-palette
    BLACK("#000000"), BLACK_BOOT("#333333"), WHITE("#ffffff"),
    RED_50("#ffebee"), RED_100("#ffcdd2"), RED_200("#ef9a9a"), RED_300("#e57373"), RED_400("#ef5350"), RED_500("#f44336"), RED_600("#e53935"), RED_700("#d32f2f"), RED_800("#c62828"), RED_900("#b71c1c"), RED_A100("#ff8a80"), RED_A200("#ff5252"), RED_A400("#ff1744"), RED_A700("#d50000"),
    PINK_50("#fce4ec"), PINK_100("#f8bbd0"), PINK_200("#f48fb1"), PINK_300("#f06292"), PINK_400("#ec407a"), PINK_500("#e91e63"), PINK_600("#d81b60"), PINK_700("#c2185b"), PINK_800("#ad1457"), PINK_900("#880e4f"), PINK_A100("#ff80ab"), PINK_A200("#ff4081"), PINK_A400("#f50057"), PINK_A700("#c51162"),
    PURPLE_50("#f3e5f5"), PURPLE_100("#e1bee7"), PURPLE_200("#ce93d8"), PURPLE_300("#ba68c8"), PURPLE_400("#ab47bc"), PURPLE_500("#9c27b0"), PURPLE_600("#8e24aa"), PURPLE_700("#7b1fa2"), PURPLE_800("#6a1b9a"), PURPLE_900("#4a148c"), PURPLE_A100("#ea80fc"), PURPLE_A200("#e040fb"), PURPLE_A400("#d500f9"), PURPLE_A700("#aa00ff"),
    DEEP_PURPLE_50("#ede7f6"), DEEP_PURPLE_100("#d1c4e9"), DEEP_PURPLE_200("#b39ddb"), DEEP_PURPLE_300("#9575cd"), DEEP_PURPLE_400("#7e57c2"), DEEP_PURPLE_500("#673ab7"), DEEP_PURPLE_600("#5e35b1"), DEEP_PURPLE_700("#512da8"), DEEP_PURPLE_800("#4527a0"), DEEP_PURPLE_900("#311b92"), DEEP_PURPLE_A100("#b388ff"), DEEP_PURPLE_A200("#7c4dff"), DEEP_PURPLE_A400("#651fff"), DEEP_PURPLE_A700("#6200ea"),
    INDIGO_50("#e8eaf6"), INDIGO_100("#c5cae9"), INDIGO_200("#9fa8da"), INDIGO_300("#7986cb"), INDIGO_400("#5c6bc0"), INDIGO_500("#3f51b5"), INDIGO_600("#3949ab"), INDIGO_700("#303f9f"), INDIGO_800("#283593"), INDIGO_900("#1a237e"), INDIGO_A100("#8c9eff"), INDIGO_A200("#536dfe"), INDIGO_A400("#3d5afe"), INDIGO_A700("#304ffe"),
    BLUE_50("#e3f2fd"), BLUE_100("#bbdefb"), BLUE_200("#90caf9"), BLUE_300("#64b5f6"), BLUE_400("#42a5f5"), BLUE_500("#2196f3"), BLUE_600("#1e88e5"), BLUE_700("#1976d2"), BLUE_800("#1565c0"), BLUE_900("#0d47a1"), BLUE_A100("#82b1ff"), BLUE_A200("#448aff"), BLUE_A400("#2979ff"), BLUE_A700("#2962ff"),
    LIGHT_BLUE_50("#e1f5fe"), LIGHT_BLUE_100("#b3e5fc"), LIGHT_BLUE_200("#81d4fa"), LIGHT_BLUE_300("#4fc3f7"), LIGHT_BLUE_400("#29b6f6"), LIGHT_BLUE_500("#03a9f4"), LIGHT_BLUE_600("#039be5"), LIGHT_BLUE_700("#0288d1"), LIGHT_BLUE_800("#0277bd"), LIGHT_BLUE_900("#01579b"), LIGHT_BLUE_A100("#80d8ff"), LIGHT_BLUE_A200("#40c4ff"), LIGHT_BLUE_A400("#00b0ff"), LIGHT_BLUE_A700("#0091ea"),
    CYAN_50("#e0f7fa"), CYAN_100("#b2ebf2"), CYAN_200("#80deea"), CYAN_300("#4dd0e1"), CYAN_400("#26c6da"), CYAN_500("#00bcd4"), CYAN_600("#00acc1"), CYAN_700("#0097a7"), CYAN_800("#00838f"), CYAN_900("#006064"), CYAN_A100("#84ffff"), CYAN_A200("#18ffff"), CYAN_A400("#00e5ff"), CYAN_A700("#00b8d4"),
    TEAL_50("#e0f2f1"), TEAL_100("#b2dfdb"), TEAL_200("#80cbc4"), TEAL_300("#4db6ac"), TEAL_400("#26a69a"), TEAL_500("#009688"), TEAL_600("#00897b"), TEAL_700("#00796b"), TEAL_800("#00695c"), TEAL_900("#004d40"), TEAL_A100("#a7ffeb"), TEAL_A200("#64ffda"), TEAL_A400("#1de9b6"), TEAL_A700("#00bfa5"),
    GREEN_50("#e8f5e9"), GREEN_100("#c8e6c9"), GREEN_200("#a5d6a7"), GREEN_300("#81c784"), GREEN_400("#66bb6a"), GREEN_500("#4caf50"), GREEN_600("#43a047"), GREEN_700("#388e3c"), GREEN_800("#2e7d32"), GREEN_900("#1b5e20"), GREEN_A100("#b9f6ca"), GREEN_A200("#69f0ae"), GREEN_A400("#00e676"), GREEN_A700("#00c853"),
    LIGHT_GREEN_50("#f1f8e9"), LIGHT_GREEN_100("#dcedc8"), LIGHT_GREEN_200("#c5e1a5"), LIGHT_GREEN_300("#aed581"), LIGHT_GREEN_400("#9ccc65"), LIGHT_GREEN_500("#8bc34a"), LIGHT_GREEN_600("#7cb342"), LIGHT_GREEN_700("#689f38"), LIGHT_GREEN_800("#558b2f"), LIGHT_GREEN_900("#33691e"), LIGHT_GREEN_A100("#ccff90"), LIGHT_GREEN_A200("#b2ff59"), LIGHT_GREEN_A400("#76ff03"), LIGHT_GREEN_A700("#64dd17"),
    LIME_50("#f9fbe7"), LIME_100("#f0f4c3"), LIME_200("#e6ee9c"), LIME_300("#dce775"), LIME_400("#d4e157"), LIME_500("#cddc39"), LIME_600("#c0ca33"), LIME_700("#afb42b"), LIME_800("#9e9d24"), LIME_900("#827717"), LIME_A100("#f4ff81"), LIME_A200("#eeff41"), LIME_A400("#c6ff00"), LIME_A700("#aeea00"),
    YELLOW_50("#fffde7"), YELLOW_100("#fff9c4"), YELLOW_200("#fff59d"), YELLOW_300("#fff176"), YELLOW_400("#ffee58"), YELLOW_500("#ffeb3b"), YELLOW_600("#fdd835"), YELLOW_700("#fbc02d"), YELLOW_800("#f9a825"), YELLOW_900("#f57f17"), YELLOW_A100("#ffff8d"), YELLOW_A200("#ffff00"), YELLOW_A400("#ffea00"), YELLOW_A700("#ffd600"),
    AMBER_50("#fff8e1"), AMBER_100("#ffecb3"), AMBER_200("#ffe082"), AMBER_300("#ffd54f"), AMBER_400("#ffca28"), AMBER_500("#ffc107"), AMBER_600("#ffb300"), AMBER_700("#ffa000"), AMBER_800("#ff8f00"), AMBER_900("#ff6f00"), AMBER_A100("#ffe57f"), AMBER_A200("#ffd740"), AMBER_A400("#ffc400"), AMBER_A700("#ffab00"),
    ORANGE_50("#fff3e0"), ORANGE_100("#ffe0b2"), ORANGE_200("#ffcc80"), ORANGE_300("#ffb74d"), ORANGE_400("#ffa726"), ORANGE_500("#ff9800"), ORANGE_600("#fb8c00"), ORANGE_700("#f57c00"), ORANGE_800("#ef6c00"), ORANGE_900("#e65100"), ORANGE_A100("#ffd180"), ORANGE_A200("#ffab40"), ORANGE_A400("#ff9100"), ORANGE_A700("#ff6d00"),
    DEEP_ORANGE_50("#fbe9e7"), DEEP_ORANGE_100("#ffccbc"), DEEP_ORANGE_200("#ffab91"), DEEP_ORANGE_300("#ff8a65"), DEEP_ORANGE_400("#ff7043"), DEEP_ORANGE_500("#ff5722"), DEEP_ORANGE_600("#f4511e"), DEEP_ORANGE_700("#e64a19"), DEEP_ORANGE_800("#d84315"), DEEP_ORANGE_900("#bf360c"), DEEP_ORANGE_A100("#ff9e80"), DEEP_ORANGE_A200("#ff6e40"), DEEP_ORANGE_A400("#ff3d00"), DEEP_ORANGE_A700("#dd2c00"),
    BROWN_50("#efebe9"), BROWN_100("#d7ccc8"), BROWN_200("#bcaaa4"), BROWN_300("#a1887f"), BROWN_400("#8d6e63"), BROWN_500("#795548"), BROWN_600("#6d4c41"), BROWN_700("#5d4037"), BROWN_800("#4e342e"), BROWN_900("#3e2723"),
    GRAY_50("#fafafa"), GRAY_100("#f5f5f5"), GRAY_200("#eeeeee"), GRAY_300("#e0e0e0"), GRAY_400("#bdbdbd"), GRAY_500("#9e9e9e"), GRAY_600("#757575"), GRAY_700("#616161"), GRAY_800("#424242"), GRAY_900("#212121"),
    BLUE_GRAY_50("#eceff1"), BLUE_GRAY_100("#cfd8dc"), BLUE_GRAY_200("#b0bec5"), BLUE_GRAY_300("#90a4ae"), BLUE_GRAY_400("#78909c"), BLUE_GRAY_500("#607d8b"), BLUE_GRAY_600("#546e7a"), BLUE_GRAY_700("#455a64"), BLUE_GRAY_800("#37474f"), BLUE_GRAY_900("#263238"),
    RED("red"), GREEN("green"), BLUE("blue"), ROSYBROWN("rosybrown"),;

    override fun toString() = string
}

@native interface IKotlinShit {
//    fun ignite(hotIgnition: Boolean)
    fun loadDebugKotlinPlaygroundPage()
    fun loadAdminUsersPage(): Promise<Unit>
    fun loadDashboardPage(): Promise<Unit>
    fun loadProfilePage(): Promise<Unit>
    fun loadSignUpPage(): Promise<Unit>
}


object KotlinShit : IKotlinShit {
    lateinit var ui: World
    lateinit var clientImpl: World

    val igniteTestShit = ::jsFacing_igniteTestShit

//    val loadSignInPageCtor = ::jsFacing_loadSignInPageCtor
    val renderTopNavbar_calledByFuckingUI = ::jsFacing_renderTopNavbar_calledByFuckingUI
    val isDynamicPage = ::jsFacing_isDynamicPage

    val jsFacing_pushNavigate = ::pushNavigate
    val jsFacing_puid = ::puid

//    val jsFacing_art_invokeStateContributions = ::invokeStateContributions
    val apsCSS = ::jsFacing_apsCSS
    val pollLiveStatus = ::jsFacing_pollLiveStatus
//    val startLiveStatusPolling = ::jsFacing_startLiveStatusPolling
//    val stopLiveStatusPolling = ::jsFacing_stopLiveStatusPolling
    val urlLink = ::jsFacing_urlLink
    val pageLink = ::jsFacing_pageLink
//    val initHotCodeShit: dynamic = ::jsFacing_initHotCodeShit
//    val parseQueryString = ::jsFacing_parseQueryString

//    val shittyShit = json(
//        "tests_UA_Writer" to ::jsFacing_tests_UA_Writer
//    )

//    override fun loadSignUpPage(): Promise<Unit> {"__async"
//        return __asyncResult(__await(SignUpPage(ui).load()))
//    }

    override fun loadAdminUsersPage(): Promise<Unit> {"__async"
        return __asyncResult(__await(AdminUsersPage(ui).load()))
    }

    override fun loadProfilePage(): Promise<Unit> {"__async"
        return __asyncResult(__await(ProfilePage(ui).load()))
    }

    override fun loadSignUpPage(): Promise<Unit> {"__async"
        return __asyncResult(__await(SignUpPage(ui).load()))
    }

    override fun loadDashboardPage(): Promise<Unit> {"__async"
        return __asyncResult(__await(DashboardPage(ui).load()))
    }

//    val kot_melinda = ::jsFacing_melinda

//    override fun makeProfileFields(def: dynamic): dynamic {
//        return jsArrayOf(
//            ui.TextField(json(
//                "name" to "phone",
//                "title" to t("TOTE", "Телефон")
//            )),
//            ui.TextField(json(
//                "name" to "aboutMe",
//                "kind" to "textarea",
//                "title" to t("TOTE", "Пара ласковых о себе")
//            ))
//        )
//    }

//    override fun userKindIcon(def: dynamic): dynamic {
//        // #extract {user} from def
//        val user: UserRTO = def.user
//
//        return Shitus.faIcon(json("tame" to "icon", "style" to json("marginLeft" to 5, "marginRight" to 5),
//            "icon" to when (user.kind) {
//                UserKind.CUSTOMER -> "user"
//                UserKind.WRITER -> "pencil"
//                UserKind.ADMIN -> "cog"
//            }
//        ))
//    }


    override fun loadDebugKotlinPlaygroundPage() {
        KotlinShit.ui.setPage(Page(
            header = oldShitAsToReactElementable(Shitus.pageHeader(json("title" to "debug-kotlin-playground"))),
            body = oldShitAsToReactElementable(Shitus.diva(json("tame" to "qweqwe", "style" to json("color" to "blue")), "La-la-la5"))
        ))
    }
}

class ReactClassShit(
    val render: () -> ReactElement,
    val componentWillMount: (() -> Unit)? = null,
    val componentDidMount: (() -> Unit)? = null,
    val componentWillUpdate: (() -> Unit)? = null,
    val componentDidUpdate: (() -> Unit)? = null,
    val componentWillUnmount: (() -> Unit)? = null
)

fun elcl(spec: ReactClassShit): ReactElement =
    React.createElement(
        React.createClass(json(
            "render" to spec.render,
            "componentWillMount" to spec.componentWillMount,
            "componentDidMount" to spec.componentDidMount,
            "componentWillUpdate" to spec.componentWillUpdate,
            "componentDidUpdate" to spec.componentDidUpdate,
            "componentWillUnmount" to spec.componentWillUnmount
        )),
        json()
    )

fun jsFacing_elcl(def: dynamic): ReactElement {
    val origRender = def.render
    return React.createElement(React.createClass(global.Object.assign(def, json(
        "render" to render@{
            fun hrendus(e: dynamic) {
                if (js("typeof window") == "object") {
                    return renderExceptionTriangleAndRevealStack(json("exception" to e))
                } else {
                    throw e
                }
            }

            if (def.exception) {
                return@render hrendus(def.exception)
            }

            try {
                return@render origRender()
            } catch (e: Throwable) {
                return@render hrendus(e)
            }
        }
    ))), js("({})"))
}


fun jsFacing_diva(vararg ignored: dynamic): dynamic {
    val attrs = js("arguments[0]")
    val children = js("Array.prototype.slice.call(arguments, 1)")
    return killme_basicTag("div", attrs, children)
}

fun jsFacing_spana(vararg ignored: dynamic): dynamic {
    val attrs = js("arguments[0]")
    val children = js("Array.prototype.slice.call(arguments, 1)")
    return killme_basicTag("span", attrs, children)
}

fun jsFacing_ula(vararg ignored: dynamic): dynamic {
    val attrs = js("arguments[0]")
    val children = js("Array.prototype.slice.call(arguments, 1)")
    return killme_basicTag("ul", attrs, children)
}

fun jsFacing_lia(vararg ignored: dynamic): dynamic {
    val attrs = js("arguments[0]")
    val children = js("Array.prototype.slice.call(arguments, 1)")
    return killme_basicTag("li", attrs, children)
}

fun jsFacing_forma(vararg ignored: dynamic): dynamic {
    val attrs = js("arguments[0]")
    val children = js("Array.prototype.slice.call(arguments, 1)")
    return killme_basicTag("form", attrs, children)
}

fun jsFacing_labela(vararg ignored: dynamic): dynamic {
    val attrs = js("arguments[0]")
    val children = js("Array.prototype.slice.call(arguments, 1)")
    return killme_basicTag("label", attrs, children)
}

fun jsFacing_nava(vararg ignored: dynamic): dynamic {
    val attrs = js("arguments[0]")
    val children = js("Array.prototype.slice.call(arguments, 1)")
    return killme_basicTag("nav", attrs, children)
}

fun jsFacing_aa(vararg ignored: dynamic): dynamic {
    val attrs = js("arguments[0]")
    val children = js("Array.prototype.slice.call(arguments, 1)")
    return killme_basicTag("a", attrs, children)
}

fun jsFacing_ia(vararg ignored: dynamic): dynamic {
    val attrs = js("arguments[0]")
    val children = js("Array.prototype.slice.call(arguments, 1)")
    return killme_basicTag("i", attrs, children)
}

fun jsFacing_h3a(vararg ignored: dynamic): dynamic {
    val attrs = js("arguments[0]")
    val children = js("Array.prototype.slice.call(arguments, 1)")
    return killme_basicTag("h3", attrs, children)
}

fun jsFacing_h4a(vararg ignored: dynamic): dynamic {
    val attrs = js("arguments[0]")
    val children = js("Array.prototype.slice.call(arguments, 1)")
    return killme_basicTag("h4", attrs, children)
}

fun jsFacing_blockquotea(vararg ignored: dynamic): dynamic {
    val attrs = js("arguments[0]")
    val children = js("Array.prototype.slice.call(arguments, 1)")
    return killme_basicTag("blockquote", attrs, children)
}

fun jsFacing_dom_spana(vararg ignored: dynamic): dynamic {
    val attrs = js("arguments[0]")
    val children = js("Array.prototype.slice.call(arguments, 1)")
    return killme_veryBasicTag("span", attrs, *children)
}

fun killme_basicTag(tag: String, attrs: dynamic, childrenAsJSArray: dynamic): dynamic {
    var me: dynamic = undefined // @workaround
    me = json(
        "render" to render@{
            try {
                childrenAsJSArray.forEach({ child: dynamic, idx: dynamic ->
                    if (!Shitus.isObject(child)) return@forEach Unit
                    else {
                        if (js("typeof child") == "function") return@forEach Unit
                        if (child.`$$typeof` == js("Symbol['for']('react.element')")) return@forEach Unit
                        if (child.element) return@forEach Unit

                        if (child.`$meta` || child.`$metaID`) {
                            val meat = child.meat
                            if (js("typeof meat") != "string") {
                                // TODO:vgrechka Why not throw here?
                                console.log("Only string meat, please, fuck you")
                                return@forEach Unit
                            }
                            return@forEach Unit
                        }

                        console.log("Bad-boy child at index ${idx}", child)
                        Shitus.raiseWithMeta(json("message" to "Bad-boy child at index ${idx}", "meta" to attrs))
                    }
                })

                attrs.id = me.elementID
                return@render killme_veryBasicTag(tag, attrs, *childrenAsJSArray)
//                return@render killme_veryBasicTag(tag, global.Object.assign(attrs, json("id" to me.elementID)), *childrenAsJSArray)
            } catch (e: Throwable) {
                throw global.Object.assign(e, json("\$definitionStack" to attrs.`$definitionStack`))
            }
        }
    )
    me.controlTypeName = tag + "a"
    me.ignoreDebugCtrlShiftClick = true
    legacy_implementControlShit(json("me" to me, "def" to attrs))
    return jsFacing_elcl(me)
}

fun jsArrayOrArrayLikeObjectToArray(obj: dynamic): Array<Any?> {
    val list = mutableListOf<dynamic>()
    global.Object.keys(obj).forEach { k -> list.add(obj[k]) }
    return list.toTypedArray()
}

fun killme_veryBasicTag(tag: String, attrs: dynamic, vararg _items: dynamic): dynamic {
    val items = if (attrs.items) jsArrayOrArrayLikeObjectToArray(attrs.items) else _items

    fun enhanceChild(element: dynamic, key: dynamic): dynamic {
        if (global.Array.isArray(element)) Shitus.raise("I don't want arrays as ${tag}’s children (key=${key})")
        val typeofElement = js("typeof element")

        if (typeofElement != "object" && typeofElement != "string") Shitus.raise("I don't want to deal with ${typeofElement} element children (key=${key})")

        val element2 =
            if (element.element)
                element.element
            else
                element

        val element3 =
            if (js("typeof element2") == "string") {
                element2
            } else if (element2.`$meta` || element2.`$metaID`) {
                val meat = element2.meat
                if (js("typeof meat") != "string") Shitus.raise("Only string meat, please, fuck you")

                var me: dynamic = undefined
                me = json(
                    "render" to {
                        React.createElement("span", json("id" to me.elementID), meat)
                    }
                )
                if (element2.`$meta`) {
                    me.controlTypeName = "t()"
                    global.Object.assign(me, json(
                        "\$sourceLocation" to element2.`$meta`.`$sourceLocation`,
                        "\$definitionStack" to element2.`$meta`.`$definitionStack`
                    ))
//                    global.Object.assign(me, Shitus.pick(element2.`$meta`, "\$sourceLocation", "\$definitionStack"))
                } else {
                    me.controlTypeName = "backend-t()"
                    me.`$metaID` = element2.`$metaID`
                }
                me.ignoreDebugCtrlShiftClick = true
                legacy_implementControlShit(json("me" to me, "def" to js("({})")))

                jsFacing_elcl(me)
            } else {
                element2
            }

        return element3
    }


    var children: dynamic = undefined
    if (!attrs.dangerouslySetInnerHTML) {
        children = js("[]")
        for (i in 0 until items.size) {
            val captureItemInJS = items[i]
            var item = captureItemInJS
            if (js("typeof captureItemInJS") == "function") {
                item = item()
            }
            if (item) {
                val enhanced = enhanceChild(item, i)
                children.push(enhanced)
            }
        }
    }

    return global.React.createElement.apply(global.React, js("[]").concat(tag, attrs, children))
}

class JSError(message: String) : Throwable(message) {
    init {
        // @hack
        val msg = message
        val jsError = js("Error(msg)")
        throw jsError
    }
}

fun jsFacing_spanc(def: dynamic): dynamic {
    // #extract {content, className='', style={}} from def
    val content = def.content

    if (!def.tame) Shitus.raiseWithMeta(json("message" to "I want all spancs to be tamed, why use them otherwise?", "meta" to def))

    val isString = js("typeof content") == "string"
    val isMeaty = js("typeof content") == "object" && js("typeof content.\$meta") && js("typeof content.meat") == "string"
    Shitus.invariant(content == null || isString || isMeaty, "Bad content for spanc (keys: ${if (content == null) "null-like" else global.Object.keys(content)})")

    return spanc(def.tame, if (isString || content == null) content else content.meat) {
        className = if (def.className != undefined) def.className else ""
        style { addFromJSObject(if (def.style != undefined) def.style else js("({})")) }
    }.toReactElement()
}

//fun bak_jsFacing_spanc(def: dynamic): dynamic {
//    // #extract {content, className='', style={}} from def
//    val content = def.content
//    val className = if (def.className != undefined) def.className else ""
//    val style = if (def.style != undefined) def.style else js("({})")
//
//    if (!def.tame) Shitus.raiseWithMeta(json("message" to "I want all spancs to be tamed, why use them otherwise?", "meta" to def))
//
//    val isString = js("typeof content") == "string"
//    val isMeaty = js("typeof content") == "object" && js("typeof content.\$meta") && js("typeof content.meat") == "string"
//    jshit.utils.invariant(jshit.utils.nil(content) || isString || isMeaty, "Bad content for spanc (keys: ${global.Object.keys(content)})")
//
//    var me: dynamic = undefined // Workaround for Kotlin
//    me = json(
//        "render" to {
//            var renderThing = content
//            Shitus.spana(json("id" to me.elementID, "className" to className, "style" to style), renderThing)
//        },
//
//        "contributeTestState" to {state: dynamic ->
//            var stateThing = content
//            state.put(json("control" to me, "key" to me.getTamePath(), "value" to jshit.textMeat(stateThing)))
//        },
//
//        "getLongRevelationTitle" to {
//            var revelationTitleThing = content
//            me.debugDisplayName + ": " + jshit.textMeat(revelationTitleThing)
//        }
//    )
//
//    me.controlTypeName = "spanc"
//    me.ignoreDebugCtrlShiftClick = true
//    jshit.implementControlShit(json("me" to me, "def" to def))
//    return jshit.elcl(me)
//}


//fun jsFacing_renderExceptionTriangle(def: dynamic): dynamic {
//    var exception = def.exception
//
//    return span {
//        -span {style {cursor = "pointer"}
//            onClick { println("zzzzzzz") }
//            -renderRedExclamationTriangleLabel("It fucking throwed: ${exception.message}")
//        }
//
//        -span {style {marginLeft(10) }; -"("}
//        -"Reveal"
////        -link("Reveal") {
////            Shitus.revealStack(json("exception" to exception))
////        }
//        -")"
//    }
//
////    try {
////        return Shitus.spana(json("noStateContributions" to true, "controlTypeName" to "renderExceptionTriangle"),
////            Shitus.spana(json("style" to json("cursor" to "pointer"),
////                "onClick" to { Shitus.revealStack(json("exception" to exception)) }),
////                jshit.renderRedExclamationTriangleLabel(json("title" to t("It fucking throwed: ${exception.message}")))
////            ),
////
////            Shitus.spana(json("style" to json("marginLeft" to 10)), "("),
////            Shitus.link(json("title" to t("Reveal"), "onClick" to { Shitus.revealStack(json("exception" to exception)) })),
////            ")"
////        )
////    } catch(e: Throwable) {
////        val msg = "We are in deep shit, man. Cannot renderExceptionTriangle()"
////        global.console.error(msg)
////        return null
////    }
//}


@Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
fun jsLink(vararg args: dynamic): ReactElement {
    val shit = Shitus
    return js("shit.link.apply(null, args)")
}

@Suppress("UNUSED_PARAMETER")
fun dynamicKeys(obj: dynamic): dynamic {
    return js("Object.keys(obj)")
}

@Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
fun jdiva(vararg args: dynamic): ReactElement {
    val shit = Shitus
    return js("shit.diva.apply(null, args)")
}

fun fuckingDiviarius(): ReactElement {
    return Shitus.diva(js("({})"), "Fucking Diviarius")
}

fun fuckingSpantus(): ReactElement {
    return Shitus.spana(js("({})"), "Fucking Spantus")
}




fun <T> ifornull(cond: Boolean, f: () -> T): T? {
    return if (cond) f() else null
}

class UnitPromise(f: (resolve: () -> Unit, reject: (Throwable) -> Unit) -> Unit) {
    val promise: Promise<Unit>

    init {
        promise = Promise<Unit> { resolve, reject ->
            f({ resolve(Unit) }, reject)
        }
    }

    fun then(cb: () -> Unit) {
        promise.then<Nothing> { cb() }
    }
}

fun promiseUnit(f: (resolve: () -> Unit, reject: (Throwable) -> Unit) -> Unit): Promise<Unit> {
    return Promise { resolve, reject ->
        f({ resolve(Unit) }, reject)
    }
}

// typealias Voidy = () -> Unit

class SimpleEventHandlerBuilder {
    var handler: (() -> Unit)? = null

    operator fun invoke(_handler: () -> Unit) {
        handler = _handler
    }

    fun notify() {
        handler?.invoke()
    }
}


fun timeoutSet(ms: Int, cb: () -> Unit) {
    kotlin.browser.window.setTimeout(cb, ms)
}

fun intervalSet(ms: Int, cb: () -> Unit) {
    kotlin.browser.window.setInterval(cb, ms)
}

@Suppress("UNUSED_PARAMETER")
fun simpleButton(title: String?, onClick: (e: ReactEvent) -> Unit): ReactElement {
    val attrs = js("({onClick: onClick})")
    return React.createElement("button", attrs, title)
}


//class link(build: link.() -> Unit) : StatefulElement() {
//    var content: ReactElement? = null
//
//    init {
//        build()
//    }
//
//    fun title(title: ManagedString) {
//        content = spancTitle(title)
//    }
//
//    override fun render(): ReactElement = element
//
//    private val element: ReactElement by lazy {
//        React.createElement("a", json(
//            "id" to elementID,
//            "href" to "#",
//            "className" to className,
//            "style" to style.toJSObject(),
//            "onClick" to onClick,
//            "onMouseEnter" to onMouseEnter,
//            "onMouseLeave" to onMouseLeave
//        ), content)
//    }
//}


//class oldButton(build: oldButton.() -> Unit) : StatefulElement(null) {
//    override val controlTypeName: String get() = "button"
//
//    var icon: String? = null
//    var title: ManagedString? = null
//    var level = "default"
//    val iconStyle = StyleBuilder()
//    var hint: ManagedString? = null
//
//    init {
//        build()
//    }
//
//    override fun render(): ReactElement = element
//
//    private val element: ReactElement by lazy {
//        val attrs = js("({})")
//        attrs.id = elementID
//        attrs.className = "btn btn-${level} ${className}"
//        attrs.style = style.toJSObject()
//        attrs.title = hint?.meat
//        attrs.onClick = onClick
//
//        React.createElement("button", attrs,
//            icon?.let { jshit.glyph(it, json("style" to iconStyle.toJSObject())) },
//            if (icon != null && title != null) nbsp else null,
//            title?.meat
//        )
//    }
//}

@native interface ReactEvent {
    val keyCode: Int
    val ctrlKey: Boolean
    val shiftKey: Boolean

    fun preventDefault()
    fun stopPropagation()
}

//fun puid(): String {
//    return jshit.utils.puid()
//}

@native val MODE: String = noImpl

fun preventAndStop(e: ReactEvent) {
    e.preventDefault()
    e.stopPropagation()
}

fun preventAndStop(e: Event) {
    e.preventDefault()
    e.stopPropagation()
}

fun <R> runni(f: () -> R): R {
    return f()
}


fun promiseDefinitionStack(constructionStackAsError: Any?, firstSignificantStackLine: Int): Promise<dynamic> {
    return Promise {resolve, reject ->
        console.warn("Implement promiseDefinitionStack")
        resolve(jsArrayOf())
    }


//    return Promise<dynamic>({ resolve, reject ->
//        Shitus.errorToMappedClientStackString(constructionStackAsError, json("skipMessage" to true)).then { stackString: String ->
//            // @wip sourceLocation
//            var lines = stackString.lines()
//            lines = lines.slice(firstSignificantStackLine..lines.lastIndex)
//            /// println("Liiines"); lines.take(5).forEachIndexed { i, s -> println("$i) $s")}
//
//            val jsArray = js("[]")
//            lines.forEach { line ->
//                Regex("\\((.*?\\.(ts|kt):\\d+:\\d+)\\)").find(line)?.let {
//                    jsArray.push(json(
//                        "loc" to it.groupValues[1]
//                    ))
//                }
//            }
//            resolve(jsArray)
//        }
//    })
}


fun div(doInsideBuilder: FlowElementBuilder.() -> Unit): ReactElement {
    val builder = FlowElementBuilder("div")
    builder.doInsideBuilder()
    return builder.toElement()
}

fun horizontala(doInsideBuilder: HorizontalaBuilder.() -> Unit): ReactElement {
    val builder = HorizontalaBuilder()
    builder.doInsideBuilder()
    return builder.toElement()
}

//fun hor2(doInsideBuilder: HorizontalaBuilder.() -> Unit): ReactElement {
//    return horizontala {
//        spacing = 8
//        doInsideBuilder()
//    }
//}

fun hor1_killme(doInsideBuilder: HorizontalaBuilder.() -> Unit): ReactElement {
    return horizontala {
        spacing = 4
        doInsideBuilder()
    }
}

class HorizontalaBuilder() : FlowElementBuilder("div") {
    var spacing: Int = 0

    init {
        style { display = "flex" }
    }

    override fun transformChildBeforeAddition(child: ReactElement) =
        div {
            style { marginLeft(if (children.isEmpty()) 0 else spacing) }
            -child
        }
}

// TODO:vgrechka Make PhraseElementBuilder    cb5b0102-4159-4080-8d06-c324d1cf2e08
fun span(doInsideBuilder: FlowElementBuilder.() -> Unit): ReactElement {
    val builder = FlowElementBuilder("span")
    builder.doInsideBuilder()
    return builder.toElement()
}


@native val console: NativeConsole
@native interface NativeConsole {
    fun log(vararg args: Any?)
    fun warn(vararg args: Any?)
    fun error(vararg args: Any?)
}

fun Map<String, Any?>.toJSObject(): dynamic {
    val obj = js("({})")
    for ((k, v) in this) obj[k] = v
    return obj
}



fun jsArrayOf(vararg xs: dynamic): dynamic {
    val res = js("[]")
    xs.forEach { res.push(it) }
    return res
}

class StyleBuilder {
    private val attrs = mutableMapOf<String, String>()

    fun toJSObject(): dynamic {
        return attrs.toJSObject()
    }

    operator fun invoke(insideMe: StyleBuilder.() -> Unit) {
        insideMe()
    }

    fun add(other: StyleBuilder?) {
        if (other != null) attrs.putAll(other.attrs)
    }

    fun addFromJSObject(jso: dynamic) {
        for (key: String in jsArrayToListOfDynamic(global.Object.keys(jso))) {
            val value = jso[key]
            if (!value) continue

            val jsType = js("typeof value")
            val stringValue = when (jsType) {
                "number" -> when {
                    setOf("width", "height",
                          "padding", "paddingTop", "paddingRight", "paddingBottom", "paddingLeft",
                          "margin", "marginTop", "marginRight", "marginBottom", "marginLeft").contains(key)
                        -> "${value}px"
                    setOf("opacity").contains(key)
                        -> "${value}"
                    else
                        -> throw JSError("Style property $key cannot be number, fuck you")
                }
                "string" -> "" + value
                else -> "Style property $key cannot be $jsType, fuck you"
            }

            attrs[key] = value
        }
    }

    var display: String? = null; set(value) { if (value == null) attrs.remove("display") else attrs["display"] = value }
    var cursor: String? = null; set(value) { if (value == null) attrs.remove("cursor") else attrs["cursor"] = value }
    var fontWeight: String? = null; set(value) { if (value == null) attrs.remove("fontWeight") else attrs["fontWeight"] = value }
    var fontStyle: String? = null; set(value) { if (value == null) attrs.remove("fontStyle") else attrs["fontStyle"] = value }
    var color: Color? = null; set(value) { if (value == null) attrs.remove("color") else attrs["color"] = value.string }
    var backgroundColor: Color? = null; set(value) { if (value == null) attrs.remove("backgroundColor") else attrs["backgroundColor"] = value.string }
    var opacity: Double? = null; set(value) { if (value == null) attrs.remove("opacity") else attrs["opacity"] = "" + value }
    var textAlign: String? = null; set(value) { if (value == null) attrs.remove("textAlign") else attrs["textAlign"] = value }
    var justifyContent: String? = null; set(value) { if (value == null) attrs.remove("justifyContent") else attrs["justifyContent"] = value }

    var marginTop: String? = null; set(value) { if (value == null) attrs.remove("marginTop") else attrs["marginTop"] = value }
    fun marginTop(value: Int) { marginTop = "${value}px" }
    var marginRight: String? = null; set(value) { if (value == null) attrs.remove("marginRight") else attrs["marginRight"] = value }
    fun marginRight(value: Int) { marginRight = "${value}px" }
    var marginBottom: String? = null; set(value) { if (value == null) attrs.remove("marginBottom") else attrs["marginBottom"] = value }
    fun marginBottom(value: Int) { marginBottom = "${value}px" }
    var marginLeft: String? = null; set(value) { if (value == null) attrs.remove("marginLeft") else attrs["marginLeft"] = value }
    fun marginLeft(value: Int) { marginLeft = "${value}px" }
    var paddingTop: String? = null; set(value) { if (value == null) attrs.remove("paddingTop") else attrs["paddingTop"] = value }
    fun paddingTop(value: Int) { paddingTop = "${value}px" }
    var paddingRight: String? = null; set(value) { if (value == null) attrs.remove("paddingRight") else attrs["paddingRight"] = value }
    fun paddingRight(value: Int) { paddingRight = "${value}px" }
    var paddingBottom: String? = null; set(value) { if (value == null) attrs.remove("paddingBottom") else attrs["paddingBottom"] = value }
    fun paddingBottom(value: Int) { paddingBottom = "${value}px" }
    var paddingLeft: String? = null; set(value) { if (value == null) attrs.remove("paddingLeft") else attrs["paddingLeft"] = value }
    fun paddingLeft(value: Int) { paddingLeft = "${value}px" }
    var borderTop: String? = null; set(value) { if (value == null) attrs.remove("borderTop") else attrs["borderTop"] = value }
    fun borderTop(value: Int) { borderTop = "${value}px" }
    var borderRight: String? = null; set(value) { if (value == null) attrs.remove("borderRight") else attrs["borderRight"] = value }
    fun borderRight(value: Int) { borderRight = "${value}px" }
    var borderBottom: String? = null; set(value) { if (value == null) attrs.remove("borderBottom") else attrs["borderBottom"] = value }
    fun borderBottom(value: Int) { borderBottom = "${value}px" }
    var borderLeft: String? = null; set(value) { if (value == null) attrs.remove("borderLeft") else attrs["borderLeft"] = value }
    fun borderLeft(value: Int) { borderLeft = "${value}px" }
    var width: String? = null; set(value) { if (value == null) attrs.remove("width") else attrs["width"] = value }
    fun width(value: Int) { width = "${value}px" }
    var height: String? = null; set(value) { if (value == null) attrs.remove("height") else attrs["height"] = value }
    fun height(value: Int) { height = "${value}px" }
    var fontSize: String? = null; set(value) { if (value == null) attrs.remove("fontSize") else attrs["fontSize"] = value }
    fun fontSize(value: Int) { fontSize = "${value}px" }
    var borderWidth: String? = null; set(value) { if (value == null) attrs.remove("borderWidth") else attrs["borderWidth"] = value }
    fun borderWidth(value: Int) { borderWidth = "${value}px" }
    var margin: String? = null; set(value) { if (value == null) attrs.remove("margin") else attrs["margin"] = value }
    fun margin(value: Int) { margin = "${value}px" }
    var padding: String? = null; set(value) { if (value == null) attrs.remove("padding") else attrs["padding"] = value }
    fun padding(value: Int) { padding = "${value}px" }
}







// ------------------------- Async Playground -------------------------


fun testAsyncShit() {"__async"
    println("Begin testAsyncShit")

    val methodRes: Int = __await(SomeClass().someAsyncShit_method())
    println("Async method result: $methodRes")

    val topLevelRes: Int = __await(someAsyncShit_topLevel())
    println("Top-level function result: $topLevelRes")

    val closureRes: Int = __await(someAsyncShit_closure())
    println("Closure result: $closureRes")

    someAsyncShit_localContainer()

    println("End testAsyncShit")
}

fun promise10(): Promise<Int> {
    return Promise { resolve, reject ->
        timeoutSet(500) {
            resolve(10)
        }
    }
}

fun promise20(): Promise<Int> {
    return Promise { resolve, reject ->
        timeoutSet(1000) {
            resolve(20)
        }
    }
}

val someAsyncShit_closure = {"__async"
    println("Begin someAsyncShit_closure")
    val a = __await(promise10())
    println("Got someAsyncShit_closure a: $a")
    val b = __await(promise20())
    println("Got someAsyncShit_closure b: $b")
    __asyncResult(a + b)
}

class SomeClass {
    fun someAsyncShit_method(): Promise<Int> {"__async"
        println("Begin someAsyncShit_method")
        val a = __await(promise10())
        println("Got someAsyncShit_method a: $a")
        val b = __await(promise20())
        println("Got someAsyncShit_method b: $b")
        return __asyncResult(a + b)
    }
}

fun someAsyncShit_topLevel(): Promise<Int> {"__async"
    println("Begin someAsyncShit_topLevel")
    val a = __await(promise10())
    println("Got someAsyncShit_topLevel a: $a")
    val b = __await(promise20())
    println("Got someAsyncShit_topLevel b: $b")
    return __asyncResult(a + b)
}

fun someAsyncShit_localContainer() {
    fun someAsyncShit_local(): Promise<Int> {"__async"
        println("Begin someAsyncShit_local")
        val a = __await(promise10())
        println("Got someAsyncShit_local a: $a")
        val b = __await(promise20())
        println("Got someAsyncShit_local b: $b")
        return __asyncResult(a + b)
    }

    someAsyncShit_local().then<Nothing> {res: Int ->
        println("Got result in `then`: $res")
    }
}

//fun initTestShit() {
//    fun testDynamicVarargs() {
//    }
//
//    global.testAsyncShit = ::testAsyncShit
//    global.testDynamicVarargs = ::testDynamicVarargs
//}

//fun t(en: String) = t(en, en)
//fun t(en: String, ru: String) = ru
















