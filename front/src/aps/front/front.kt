/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import aps.Color.*
import into.kommon.*
import org.w3c.dom.events.Event


@native interface IKotlinShit {
    fun loadAdminUsersPage(ui: World): Promise<Unit>
    fun loadDashboardPage(ui: World): Promise<Unit>
    fun loadProfilePage(ui: World): Promise<Unit>
    fun loadSignUpPage(ui: World): Promise<Unit>
}


object KotlinShit : IKotlinShit {
//    lateinit var ui: World
    lateinit var clientImpl: World

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

    override fun loadAdminUsersPage(ui: World): Promise<Unit> {"__async"
        return __asyncResult(__await(AdminUsersPage(ui).load()))
    }

    override fun loadProfilePage(ui: World): Promise<Unit> {"__async"
        return __asyncResult(__await(ProfilePage(ui).load()))
    }

    override fun loadSignUpPage(ui: World): Promise<Unit> {"__async"
        return __asyncResult(__await(SignUpPage(ui).load()))
    }

    override fun loadDashboardPage(ui: World): Promise<Unit> {"__async"
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
        promise.then<Nothing>({cb()})
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

    someAsyncShit_local().then<Nothing>({res: Int ->
        println("Got result in `then`: $res")
    })
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
















