/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import aps.Color.*
import into.kommon.*
import org.w3c.dom.events.KeyboardEvent
import kotlin.properties.Delegates
import kotlin.properties.Delegates.notNull

object TestGlobal {
    val shameToControl = mutableMapOf<String, dynamic>()
    var minimalGertrude = false
    lateinit var loadPageForURL_href: String
    val topNavbarLinks = mutableMapOf<String, dynamic>() // TODO:vgrechka Rename to nameToTopNavbarLink
    var testShitBeingAssertedID: String? = null
    var hasScenarioTODOs = false
    var hasScenarioRems = false
    var lastTestMaybe: TestScenario? = null
    var lastTestOptsMaybe: TestOptions? = null
    var requestPause: ResolvableShit<Unit>? = null
    var responseProcessedSignal by notNullNamed<ResolvableShit<Unit>>()
    var animationHalfwaySignal by notNullNamed<ResolvableShit<Unit>>()
    var animationHalfwaySignalProcessedSignal by notNullNamed<ResolvableShit<Unit>>()
    var shitVanished by notNullNamed<ResolvableShit<Unit>>()
    var forcedTestOpts: TestOptions? = null
    var killAwait = false
    var formActionCompleted by notNullNamed<ResolvableShit<Unit>>()
    var formActionHalfway by notNullNamed<ResolvableShit<Unit>>()
    var formActionHalfwayConsidered by notNullNamed<ResolvableShit<Unit>>()
    var testRunning = false
    val loadPageForURLLock by notNullNamed(TwoStepTestLock())
    val plusSequenceLock by notNullNamed(TwoStepTestLock())
    val fadeHalfwayLock by notNullNamed(TestLock())
    val fadeDoneLock by notNullNamed(TestLock())
    val fileFieldChangedLock by notNullNamed(TestLock())
    val formTickingLock by notNullNamed(TestLock())
    val formDoneLock by notNullNamed(TestLock())
    var browser by notNull<Browser>()
    val reloadPageTickingLock by notNullNamed(TestLock())
    val reloadPageDoneLock by notNullNamed(TestLock())
    var overriddenClientKind: ClientKind? = null
    val topNavItemTickingLock by notNullNamed(TestLock())
    val topNavItemDoneLock by notNullNamed(TestLock())
    val linkTickingLock by notNullNamed(TestLock())
    val linkDoneLock by notNullNamed(TestLock())

    val lastTest get() = lastTestMaybe!!
    val lastTestOpts get() = lastTestOptsMaybe!!

    val lastTestHrefMaybe
        get() = lastTestMaybe?.let {
            testNameToURL(it.name, lastTestOpts)
        }

    val lastTestHref get() = lastTestHrefMaybe!!
    var lastTestSuite: TestSuite? = null
}

fun requestAnimationFrame(block: () -> Unit) {
    global.requestAnimationFrame(block)
}

fun customerDynamicPageNames(): List<String> {
    return listOf("test", "sign-in", "sign-up", "dashboard", "orders", "order", "support")
}

fun writerDynamicPageNames(): List<String> {
    return listOf(
        "test", "sign-in", "sign-up", "dashboard", "orders", "support", "store", "users", "profile",
        "admin-my-tasks", "admin-heap", "admin-users",
        "debug-perf-render", "debug-kotlin-playground", "debug"
    )
}

fun jsFacing_isDynamicPage(name: String): Boolean {
    if (Globus.clientKind == ClientKind.UA_CUSTOMER) return customerDynamicPageNames().indexOf(name) != -1
//    if (global.CLIENT_KIND == UserKind.CUSTOMER.name) return customerDynamicPageNames().indexOf(name) != -1
    return writerDynamicPageNames().indexOf(name) != -1
}

fun isDynamicPage(clientKind: ClientKind, name: String): Boolean =
    when (clientKind) {
        ClientKind.UA_CUSTOMER -> customerDynamicPageNames().indexOf(name) != -1
        ClientKind.UA_WRITER -> writerDynamicPageNames().indexOf(name) != -1
    }



fun userKindTitle(kind: UserKind) = when (kind) {
    UserKind.CUSTOMER -> t("TOTE", "Заказчик")
    UserKind.WRITER -> t("TOTE", "Писатель")
    UserKind.ADMIN -> t("TOTE", "Админ")
}

fun requiredToken(ui: World): String = ui.tokenMaybe ?: bitch("I want a token")

fun oldShitAsToReactElementable(someShit: Any?): ToReactElementable =
    object:ToReactElementable {
        override fun toReactElement() = asReactElement(someShit)
    }


class limpopo(val tame: String, val label: String, val colsm: Int?, val formGroupStyle: Any? = null) {
    operator fun invoke(content: ReactElement): ReactElement {
        val res = Shitus.diva(json("controlTypeName" to "limpopo", "tame" to tame, "className" to "form-group", "style" to formGroupStyle),
            labe(json("content" to label)),
            Shitus.diva(json(), content))
        return if (colsm == null) res else Shitus.diva(json("className" to "col-sm-${colsm}"), res)
    }

    operator fun invoke(value: String, spancStyle: Any? = null): ReactElement {
        return this(asReactElement(Shitus.spanc(json(
            "tame" to "value",
            "style" to spancStyle,
            "content" to value))))
    }
}

fun renderStamp(stamp: String, includeTZ: Boolean = true): String {
    return Shitus.timestampString(stamp, json("includeTZ" to includeTZ))
}


interface Blinkable {
    fun setBlinking(b: Boolean)
}

fun darkLink(def: dynamic): ReactElement {
    return Shitus.link(Shitus.asnnoDollar(json("style" to json("color" to "#333")), def))
}

fun rawHTML(__html: String, tag: String = "div"): ReactElement {
    return React.createElement(tag, json("dangerouslySetInnerHTML" to json("__html" to __html)))
}

fun preludeWithOrangeTriangle(def: dynamic): dynamic {
    val title = def.title
    val center = def.center

    val style: dynamic = json()
    if (center) {
        global.Object.assign(style, json("width" to center))
    }
    global.Object.assign(style, json("marginBottom" to 15))

    return Shitus.diva(json("controlTypeName" to "preludeWithOrangeTriangle", "tame" to "preludeWithOrangeTriangle", "style" to style),
        Shitus.ia(json("className" to "fa fa-exclamation-triangle", "style" to json("color" to Color.AMBER_900.toString()))),
        symbols.nbsp, symbols.nbsp,
        Shitus.spancTitle(json("title" to title)))
}

fun preludeWithGreenCheck(def: dynamic): ReactElement {
    val title = def.title
    val center = def.center

    val style: dynamic = json()
    if (center) {
        global.Object.assign(style, json("width" to center, "margin" to "0 auto"))
    }
    global.Object.assign(style, json("marginBottom" to 15))

    return Shitus.diva(json("controlTypeName" to "preludeWithGreenCheck", "tame" to "preludeWithGreenCheck", "style" to style),
        Shitus.ia(json("className" to "fa fa-check", "style" to json("color" to Color.LIGHT_GREEN_700.toString()))),
        symbols.nbsp, symbols.nbsp,
        Shitus.spancTitle(json("title" to title)))
}

fun preludeWithBadNews(def: dynamic): dynamic {
    var content = def.content
    val title = def.title
    val quote = def.quote
    val center = def.center
    var style = def.style

    if (title && content) Shitus.raise("Either title or content, please, fuck you")

    if (title) {
        content = Shitus.spancTitle(json("title" to title))
    }

    style = global.Object.assign(json(), style)
    if (center) {
        global.Object.assign(style, json("width" to center, "margin" to "0 auto"))
    }
    global.Object.assign(style, json("marginBottom" to 20, "padding" to 5, "background" to Color.RED_50.toString()))

    return Shitus.diva(json("tame" to "preludeWithBadNews", "style" to style),
        Shitus.diva(json("style" to json("display" to "flex", "alignItems" to "center")),
            Shitus.glyph("minus-circle", json("style" to json("color" to Color.RED_900.toString(), "fontSize" to "200%", "zIndex" to 20))),
            Shitus.glyph("circle", json("style" to json("color" to Color.WHITE.toString(), "fontSize" to "150%", "zIndex" to 10, "marginLeft" to "-1em", "marginTop" to "0.2em"))),
            Shitus.spana(json("style" to json("marginLeft" to 8)), content)),
        quote && Shitus.diva(json(),
            Shitus.blockquotea(json("style" to json("fontSize" to "1em", "marginTop" to -5, "marginBottom" to 0, "borderLeftColor" to Color.RED_200.toString(), "marginLeft" to 10, "paddingLeft" to 13)),
                Shitus.spanc(json("tame" to "quote", "content" to quote))))
    )
}

fun preludeWithVeryBadNews(def: dynamic): dynamic {
    val content = def.content
    val center = def.center
    var style = def.style

    style = global.Object.assign(json(), style)
    if (center) {
        global.Object.assign(style, json("width" to center, "margin" to "0 auto"))
    }
    global.Object.assign(style, json("marginBottom" to 20, "padding" to 5, "background" to Color.RED_50.toString(), "display" to "flex", "alignItems" to "center"))

    return Shitus.diva(json("tame" to "preludeWithVeryBadNews", "style" to style),
        Shitus.glyph("minus-circle", json("style" to json("color" to Color.RED_900.toString(), "fontSize" to "200%", "zIndex" to 20))),
        Shitus.glyph("circle", json("style" to json("color" to Color.WHITE.toString(), "fontSize" to "150%", "zIndex" to 10, "marginLeft" to "-1em", "marginTop" to "0.2em"))),
        Shitus.spana(json("style" to json("marginLeft" to 8)), content))
}

fun preludeWithHourglass(def: dynamic): dynamic {
    val content = def.content
    val center = def.center
    var style = def.style

    style = global.Object.assign(json(), style)
    if (center) {
        global.Object.assign(style, json("width" to center, "margin" to "0 auto"))
    }
    global.Object.assign(style, json("marginBottom" to 20))

    val hourglassColor = Color.AMBER_900.toString()

        return Shitus.diva(json("tame" to "preludeWithHourglass", "style" to style),
            Shitus.glyph("hourglass-half", json("style" to json("color" to hourglassColor))),
            symbols.nbsp, symbols.nbsp, content)
}

fun hr(): ReactElement {
    return React.createElement("hr", json())
}

val elKillme: dynamic = {
    val all = js("Array.prototype.slice.call(arguments)")
    global.React.createElement.apply(global.React, /*...*/all)
}

fun getURLQuery(): Map<String, String> {
    return parseQueryString(Globus.location.href)
}

fun labe(def: dynamic): dynamic {
    val content = def.content
    return Shitus.labela(json(), Shitus.spanc(json("tame" to "label", "content" to content)))
}

fun renderExceptionTriangleAndRevealStack(def: dynamic): dynamic {
    val exception = def.exception
    global.requestAnimationFrame {revealStack(exception)}
    return renderExceptionTriangle(json("exception" to exception))
}

fun renderExceptionTriangle(def: dynamic): dynamic {
    val exception = def.exception
    return Shitus.spana(json("noStateContributions" to true, "controlTypeName" to "renderExceptionTriangle"),
        Shitus.spana(
            json("style" to json("cursor" to "pointer"),
                "onClick" to {
                    revealStack(exception)
                }),
            renderRedExclamationTriangleLabel(json("title" to "It fucking throwed: ${exception.message}"))),
        Shitus.spana(json("style" to json("marginLeft" to 10)), "(" /*)*/),
        Shitus.link(json("title" to "Reveal", "onClick" to {revealStack(exception)})),
        /*(*/ ")")
}

fun renderRedExclamationTriangleLabel(def: dynamic): dynamic {
    val title = def.title
    return Shitus.spana(json("controlTypeName" to "renderRedExclamationTriangleLabel", "style" to json("color" to Color.RED_700.toString())),
        Shitus.spana(json("className" to "fa fa-exclamation-triangle")),
        Shitus.spana(json("style" to json("marginLeft" to 10)), title))
}

fun Tabs(def: dynamic): dynamic {
    return Shitus.statefulElement(ctor@{update: dynamic ->
        var activeId: dynamic = def.activeTab ?: global.Object.keys(def.tabs)[0]
        val me: dynamic = json(
            "render" to {
                Shitus.diva(json(),
                    Shitus.diva(json("style" to json("position" to "relative")),
                        Shitus.ula.apply(null, js("[]").concat(
                            json("className" to "nav nav-tabs"),
                            /*...*/Shitus.omapa(def.tabs, {tab: dynamic, id: dynamic->
                                Shitus.lia(json("className" to if (id == activeId) "active" else ""),
                                    Shitus.aa(json("href" to "#",
                                        "onClick" to {e: dynamic ->
                                            e.preventDefault()
                                            activeId = id
                                            update()
                                        }
                                    ), tab.title))}))),
                        Shitus.diva(json("style" to json("position" to "absolute", "right" to 0, "top" to 3)),
                            def.tabs[activeId].actions)),
                        Shitus.diva(json("style" to json("marginTop" to 5)),
                            def.tabs[activeId].content))
            }
        )
        return@ctor me
    })
}

fun callDebugRPWithProgress(arg: dynamic): Promisoid<Unit> {
    imf("callDebugRPWithProgress")
}

fun debugRPC(arg: dynamic): Promisoid<dynamic> {
    bitch("It's time to use new RPC shit, OK?")
}

fun OpenSourceCodeLink(def: dynamic): ReactElement {
    var where = def.where
    val stackItem = def.stackItem
    val style = def.style
    val title = def.title
    val icon = def.icon

    var payload: dynamic = null
    if (stackItem) imf("stackItem in OpenSourceCodeLink")

//    if (stackItem) {
//        where = json("\$sourceLocation" to stackItem.loc)
//
//        if (stackItem.def || stackItem.arg) {
//            if (stackItem.def && !isObject(stackItem.def)) { console.warn("stackItem.def is not an object"); return null }
//            if (stackItem.arg && !isObject(stackItem.arg)) { console.warn("stackItem.arg is not an object"); return null }
//
//            payload = renderPayload@{
//                return@renderPayload Shitus.diva(json(),
//                    Shitus.diva({style: json()},
//                        stackItem.def && Shitus.diva(json(),
//                            Shitus.diva({style: {fontWeight: "bold", background: GRAY_300}}, t("def = ")),
//                            Shitus.diva({style: {fontFamily: "monospace", whiteSpace: "pre"}}, repr(stackItem.def))),
//                        stackItem.arg && Shitus.diva(json(),
//                            Shitus.diva({style: {fontWeight: "bold", background: GRAY_300}}, t("arg = ")),
//                            Shitus.diva({style: {fontFamily: "monospace", whiteSpace: "pre"}}, repr(stackItem.arg))),
//                        ))
//
//                // TODO:vgrechka @refactor Unduplicate    505666f6-52fc-41eb-bb21-4bd9b11325c2
//                function repr(it) {
//                    const visitedObjects = new Set()
//                    let itsClone
//
//                        if (!isObject(it)) {
//                            itsClone = it
//                        } else {
//                            itsClone = isArray(it) ? [] : json()
//                            run2(it, itsClone, function descend(src, dest) {
//                                for (const [key, value] of toPairs(src)) {
//                                if (["$$$captuta", "$sourceLocation", "$trace", "$definitionStack", "$callStack", "sqlWithARGPlaceholders"].includes(key)
//                                    || typeof value === "function") {
//                                // Skip
//                            }
//                                else if (key === "sql" && typeof value === "string") {
//                                dest[key] = value.replace(/\n/g, "<<<br>>>")
//                            }
//                                else if (!isObject(value)) {
//                                dest[key] = value
//                            }
//                            else if (typeof value.$meta !== "undefined" && typeof value.meat !== "undefined") {
//                                dest[key] = `t<${value.meat}>`
//                            }
//                                else if (value.$$typeof === Symbol.for("react.element")) {
//                                dest[key] = `<react.element>`
//                            }
//                                else if (visitedObjects.has(value)) {
//                                dest[key] = `<Circular>`
//                            }
//                            else {
//                                visitedObjects.add(value)
//                                const valueClone = isArray(value) ? [] : json()
//                                dest[key] = valueClone
//                                descend(value, valueClone)
//                            }
//                            }
//                            })
//                        }
//
//
//                    let s = deepInspect(itsClone)
//                    s = s.replace(/<<<br>>>/g, "\n")
//                    return s
//                }
//            }
//        }
//    }

    var cshit: dynamic = null
    if (payload) {
        cshit = CollapsibleShit(json("content" to payload))
    }

    return Shitus.updatableElement(json(), ctor@{update: dynamic ->
        var tag = where.`$tag`
        var sourceLocation = where.`$sourceLocation`
        var linkText = title || where.`$tag` || where.`$sourceLocation`

        if (js("linkText instanceof Promise")) imf("Promise linkText in OpenSourceCodeLink")
        val loading = false
//        var loading = linkText instanceof Promise
//        if (loading) {
//            linkText.then(res => {
//                sourceLocation = linkText = res
//                loading = false
//                update()
//            })
//        }

        val my: dynamic = json()
        return@ctor render@{
            if (loading) return@render Shitus.spana(json(), "Loading...")
            return@render Shitus.diva(json(),
                Shitus.diva(json(),
                    Shitus.link(json("title" to linkText, "style" to Shitus.asnn(json("color" to BLACK_BOOT.toString(), "textDecoration" to "underline"), style),
                        "onClick" to onClick@{async{
                            my.linkProgress = Shitus.glyph("refresh fa-spin")
                            update()

                            var error: String? = null
                            try {
                                val res = await(OpenSourceCodeRequest.send(sourceLocation))
                                error = res.error
                                if (error == null) {
                                    my.linkProgress = Shitus.glyph("check")
                                    update()
                                    return@async
                                }
                            } catch (e: Throwable) {
                                console.error(e)
                                error = "Big internal fuckup"
                            }

                            my.linkProgress = Shitus.spana(json("style" to json("color" to RED_700.toString())),
                                Shitus.glyph("exclamation-triangle"), Shitus.spana(json("style" to json("marginLeft" to 10)), error))
                            update()
                        }})),
                    if (my.linkProgress != null) Shitus.spana(json("style" to json("marginLeft" to 10)), my.linkProgress) else null,
                    if (cshit != null) cshit.renderCaret(json("style" to json("marginLeft" to 10))) else null
                ),
                if (cshit != null) cshit.renderContent() else null
            )
        }
    })
}

fun marginateLeft(size: dynamic, content: dynamic): dynamic {
    return Shitus.diva(json("style" to json("marginLeft" to size)), content)
}

fun img(src: dynamic, _attrs: dynamic = null): dynamic {
    val attrs: dynamic = _attrs ?: json()
    return React.createElement("img", global.Object.assign(attrs, json("src" to src)))
}

fun img2(id: String? = null, src: String, style: Style = Style()): ReactElement {
    return React.createElement("img", json("id" to id, "src" to src, "style" to style.toReactStyle()))
}

fun errorLabelOld(def: dynamic): dynamic {
    val name: dynamic = def.name
    val title: dynamic = def.title
    val style: dynamic = def.style ?: json()

    return Shitus.diva(json("controlTypeName" to "errorLabel", "style" to global.Object.assign(json("color" to Color.RED_300.toString()), style)),
        Shitus.spanc(json(/*"tame" to "errorLabel",*/ "content" to title)))
}

fun ObjectViewer(def: dynamic): dynamic {
    imf("ObjectViewer")
}

fun textMeat(it: dynamic): dynamic {
    if (!it) return it
    if (it.meat) return it.meat
    if (jsTypeOf(it) != "string") Shitus.raise("Weird thing to extract meat from")
    return it
}

fun getControlByShame(shame: dynamic): dynamic {
    imf("getControlByShame")
//    for (const controls of Shitus.values(elementIDToControls)) {
//        for (const control of controls) {
//        if (control.effectiveShame === shame) {
//            return control
//        }
//    }
//    }
}

object Preloader {
    fun srcDefault32(): dynamic {
        imf("Preloader.srcDefault32")
    }
}

fun decorate(def: dynamic) {
    val target = def.target
    jsDeleteKey(def, "target")

    for (name in jsArrayToListOfDynamic(global.Object.keys(def))) {
        val origName = name.slice(name.indexOf("_") + 1)
        val orig = target[origName]
        val decorator = def[name]
        if (name.startsWith("pre_")) {
            target[origName] = {
                val args = js("Array.prototype.slice.call(arguments)")
                decorator()
                Shitus.fov.apply(null, js("[]").concat(orig, /*...*/args))
            }
        } else if (name.startsWith("post_")) {
            target[origName] = {
                val args = js("Array.prototype.slice.call(arguments)")
                try {
                    Shitus.fov.apply(null, js("[]").concat(orig, /*...*/args))
                } finally {
                    decorator()
                }
            }
        } else if (name.startsWith("arounda_")) {
            target[origName] = {async{
                val args = js("Array.prototype.slice.call(arguments)")
                await<dynamic>(decorator.apply(null, js("[]").concat(orig, /*...*/args)))
            }}
        } else {
            Shitus.raise("WTF is decorator kind ${name}")
        }
    }
}

fun CollapsibleShit(def: dynamic): dynamic {
    val content = def.content
    val initialOpen = def.initialOpen

    var open: dynamic = !!initialOpen
    var updateCaret: dynamic = null
    var updateContent: dynamic = null

    fun updateAll() {
        updateCaret()
        updateContent()
    }

    var me: dynamic = null
    me = json(
        "renderCaret" to renderCaret@{_arg: dynamic ->
            val arg: dynamic = _arg ?: json()
            val style = arg.style

            return@renderCaret Shitus.updatableElement(json(), updatableElementCtor@{update: dynamic ->
                updateCaret = update
                return@updatableElementCtor {
                    Shitus.spana(json("controlTypeName" to "CollapsibleShit::caret", "className" to "fa fa-caret-${if (open) "up" else "down"}",
                        "style" to global.Object.assign(json("cursor" to "pointer"), style),
                        "onClick" to {e: KeyboardEvent ->
                            preventAndStop(e)
                            me.toggle()
                        }))}
            })
        },

        "renderContent" to renderContent@{_arg: dynamic ->
            val arg: dynamic = _arg ?: json()
            val style = arg.style

            return@renderContent Shitus.updatableElement(json(), updatableElementCtor@{update: dynamic ->
                updateContent = update
                return@updatableElementCtor {
                    if (open) Shitus.diva(json("controlTypeName" to "CollapsibleShit::content"), content) else null
                }
            })
        },

        "toggle" to {
            open = !open
            updateAll()
        }
    )

    return me
}

fun clogError(e: dynamic, contextMsg: dynamic) {
    die("@kill clogError")
}

fun pageHeader2(
    title: String,
    className: String = "",
    labels: Iterable<dynamic> = listOf()
): ToReactElementable {
    return kdiv(className = "page-header $className", marginTop = 0, marginBottom = 15){o->
        o- h3(marginBottom = 0){o->
            o- title
        }
    }
}

fun pageHeader3(
    content: ToReactElementable,
    className: String = ""
): ToReactElementable {
    return kdiv(className = "page-header $className", marginTop = 0, marginBottom = 15){o->
        o- h3(marginBottom = 0){o->
            o- content
        }
    }
}

fun errorToMappedClientStackString(shit: Throwable): Promisoid<String> {
    val mangledStack: String = shit.asDynamic().stack
    // dwarnStriking("mangledStack", mangledStack)
    return stackToMappedClientStackString(mangledStack)
}

fun stackToMappedClientStackString(mangledStack: String): Promisoid<String> = async {
    val res = await(MapStackRequest.send(mangledStack))
    res.originalStack
}

//interface Errorish {
//    @JsName("message") val message: String?
//    @JsName("stack") val stack: String?
//}

enum class DeliveryOption(override val title: String) : Titled {
    D8(t("8+ days", "8+ дней")),
    D7(t("6-7 days", "6-7 дней")),
    D5(t("4-5 days", "4-5 дней")),
    D3(t("2-3 days", "2-3 дня")),
    H24(t("24 hours", "24 часа")),
    H12(t("12 hours", "12 часов")),
}

interface PaperType : Titled {
    fun price(dopt: DeliveryOption): Int
}

enum class PaperTypeEN(override val title: String) : PaperType {
    ESSAY("Essay, Research") {
        override fun price(dopt: DeliveryOption) = when (dopt) {
            DeliveryOption.D8 -> 1499
            DeliveryOption.D7 -> 1699
            DeliveryOption.D5 -> 1899
            DeliveryOption.D3 -> 2099
            DeliveryOption.H24 -> 2599
            DeliveryOption.H12 -> 3099
        }
    },

    BIBLIO("Annotated Bibliography") {
        override fun price(dopt: DeliveryOption) = when (dopt) {
            DeliveryOption.D8 -> 1099
            DeliveryOption.D7 -> 1299
            DeliveryOption.D5 -> 1499
            DeliveryOption.D3 -> 1699
            DeliveryOption.H24 -> 1899
            DeliveryOption.H12 -> 2099
        }
    },

    EDITING("Proofreading, Editing") {
        override fun price(dopt: DeliveryOption) = when (dopt) {
            DeliveryOption.D8 -> 399
            DeliveryOption.D7 -> 599
            DeliveryOption.D5 -> 799
            DeliveryOption.D3 -> 999
            DeliveryOption.H24 -> 1199
            DeliveryOption.H12 -> 1299
        }
    }
}

enum class PaperTypeRU(override val title: String) : PaperType {
    ESSAY("Реферат") {
        override fun price(dopt: DeliveryOption) = when (dopt) {
            DeliveryOption.H12 -> 3599
            DeliveryOption.H24 -> 2599
            DeliveryOption.D3 ->  2099
            DeliveryOption.D5 ->  1599
            DeliveryOption.D7 ->  1299
            DeliveryOption.D8 ->  1099
        }
    },

    COURSE("Курсовая работа") {
        override fun price(dopt: DeliveryOption) = when (dopt) {
            DeliveryOption.D8 -> 1599
            DeliveryOption.D7 -> 1799
            DeliveryOption.D5 -> 2099
            DeliveryOption.D3 -> 2599
            DeliveryOption.H24 -> 3099
            DeliveryOption.H12 -> 4099
        }
    },

    GRADUATE("Дипломная работа") {
        override fun price(dopt: DeliveryOption) = when (dopt) {
            DeliveryOption.D8 -> 2099
            DeliveryOption.D7 -> 2299
            DeliveryOption.D5 -> 2599
            DeliveryOption.D3 -> 3099
            DeliveryOption.H24 -> 3599
            DeliveryOption.H12 -> 4599
        }
    },

    EDITING("Корректура") {
        override fun price(dopt: DeliveryOption) = when (dopt) {
            DeliveryOption.D8 -> 599
            DeliveryOption.D7 -> 799
            DeliveryOption.D5 -> 1099
            DeliveryOption.D3 -> 1599
            DeliveryOption.H24 -> 2099
            DeliveryOption.H12 -> 3099
        }
    }
}

enum class Currency {
    USD {
        override fun prefixSuffix(lang: Language) = when (lang) {
            Language.EN -> "\$" to ""
            Language.UA -> "\$" to ""
        }
    },

    UAH {
        override fun prefixSuffix(lang: Language) = when (lang) {
            Language.EN -> "" to " UAH"
            Language.UA -> "" to " грн."
        }
    };

    abstract fun prefixSuffix(lang: Language): Pair<String, String>
}

fun moneyTitleWithoutCurrency(amount: Int, lang: Language): String {
    val integral = "" + amount / 100
    var decimal = "" + amount % 100
    if (decimal.length == 1) decimal = "0$decimal"
    return integral + lang.decimalPoint + decimal
}

fun moneyTitleWithCurrency(amount: Int, curr: Currency, lang: Language): String {
    val raw = moneyTitleWithoutCurrency(amount, lang)
    val (prefix, suffix) = curr.prefixSuffix(lang)
    return prefix + raw + suffix
}

val markdownItInstance: dynamic by lazy {
    if (isNodeJS()) nodeRequire("markdown-it")()
    else global.markdownIt()
}

fun markdownToHTML(md: String): String {
    return markdownItInstance.render(md)
}

fun markdown(md: String, stripP: Boolean = false): ToReactElementable {
    var html = markdownToHTML(md).trim()

    if (stripP) html = html
        .replace(Regex("^<p>"), "")
        .replace(Regex("</p>$"), "")

    return rawHTML(html).toToReactElementable()
}

fun renderStackLink(e: Throwable, title: String = "Show stack"): ToReactElementable =
    link(title=title, onClick = {
//        console.error(e.stack)
        revealStack(e, muteConsole = true)
    })

fun renderRawStackLink(stack: String, title: String = "Show stack"): ToReactElementable =
    link(title=title, onClick = {
        revealRawStack(stack)
    })


fun <T : Any> typeSafeURLQuery(ui: World, make: () -> T): T {
    val inst = make()
    for (k in JSObject.keys(inst)) {
        inst.asDynamic()[k] = ui.urlQuery[k]
    }
    return inst
}

val ELID_UNDER_FOOTER = "underFooter"

fun markdownTitledList(title: String, items: List<String>): String {
    return title + "\n" + items.map{"* $it"}.joinToString("\n")
}

fun World.setErrorPage(msg: String) {
    setPage(Page(
        header = oldShitAsToReactElementable(Shitus.pageHeader(json("title" to t("TOTE", "Облом")))),
        body = oldShitAsToReactElementable(Shitus.diva(js("({})"), Shitus.errorBanner(json("content" to msg))))))
}

fun World.setShittyParamsPage() {
    setErrorPage(t("TOTE", "Не надо мне всякую херню передавать в параметрах, OK?"))
}

fun <T> World.setShittyResponsePage(res: ZimbabweResponse.Shitty<T>) {
    setErrorPage(res.error)
}

fun renderTicker(float: String?): ToReactElementable =
    kdiv(Attrs(className = "progressTicker"),
         Style(float = float,
               width = 14,
               height = 33.6,
               backgroundColor = Color.BLUE_GRAY_600))

fun renderErrorBanner(msg: String): ToReactElementable =
    kblockquote(className = css.errorBanner){o->
        o- msg
    }


















