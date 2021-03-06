/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import aps.Color.*
import kotlin.js.json

external val lodash: dynamic

object Shitus {
    var topZIndex = 100000

//    val getArtStateContributionsByControl = {
//        art.stateContributionsByControl
//    }

    val initDebugFunctionsShit = {art.initDebugFunctionsShit()}

    val isOrWasInTestScenario: dynamic = js("jsFacing_isOrWasInTestScenario")
    val renderDefinitionStackStrip: dynamic = js("jsFacing_renderDefinitionStackStrip")
    val raiseWithMeta: dynamic = js("jsFacing_raiseWithMeta")
    val glyph: dynamic = js("jsFacing_glyph")
    val errorBanner = js("jsFacing_errorBanner")
    val byid: dynamic = js("jsFacing_byid")
    val byid0: dynamic = js("jsFacing_byid0")
    val horizontala: dynamic = js("jsFacing_horizontala")
    val hor1: dynamic = js("jsFacing_hor1")
    val hor2: dynamic = js("jsFacing_hor2")
    val nostring: dynamic = js("jsFacing_nostring")
    val elcl: dynamic = js("jsFacing_elcl")
    val diva: dynamic = js("jsFacing_diva")
    val spana: dynamic = js("jsFacing_spana")
    val ula: dynamic = js("jsFacing_ula")
    val lia: dynamic = js("jsFacing_lia")
    val forma: dynamic = js("jsFacing_forma")
    val labela: dynamic = js("jsFacing_labela")
    val nava: dynamic = js("jsFacing_nava")
    val aa: dynamic = js("jsFacing_aa")
    val ia: dynamic = js("jsFacing_ia")
    val h3a: dynamic = js("jsFacing_h3a")
    val h4a: dynamic = js("jsFacing_h4a")
    val blockquotea: dynamic = js("jsFacing_blockquotea")
//    val dom.spana = ::jsFacing_dom_spana
    val horiza: dynamic = js("jsFacing_horiza")
    val link: dynamic = js("jsFacing_link")
//    val faIcon = ::jsFacing_faIcon
//    val liveBadge: dynamic = ::jsFacing_liveBadge
//    val liveBadge2: dynamic = ::jsFacing_liveBadge2
//    val Checkbox: dynamic = ::jsFacing_Checkbox
    val button: dynamic = js("jsFacing_button")
//    val TopNavItem: dynamic = ::jsFacing_TopNavItem
    val spanc: dynamic = js("jsFacing_spanc")
    val Input: dynamic = js("jsFacing_Input")

    val delay = js("jsFacing_delay")
    val run: dynamic = js("jsFacing_run")
    val runa = js("jsFacing_runa")
    val repeat = js("jsFacing_repeat")
    val fov: dynamic = js("jsFacing_fov")
    val fova = js("jsFacing_fova")
    val tokens = js("jsFacing_tokens")
    val values = js("jsFacing_values")
    val invariant = js("jsFacing_invariant")
    val raise = js("jsFacing_raise")
    val isEqual = js("jsFacing_isEqual")
    val clone = js("jsFacing_clone")
    val isObject = js("jsFacing_isObject")
    val noop: dynamic = {}
    val isBlank = js("jsFacing_isBlank")
    val sortKeys = js("jsFacing_sortKeys")
//    val trim = lodash.trim
//    val trimStart = lodash.trimStart
//    val sortBy = lodash.sortBy
//    val isEmpty = lodash.isEmpty
//    val errorToMappedClientStackString: dynamic = ::jsFacing_errorToMappedClientStackString
    val makeT = js("jsFacing_makeT")
    val dedent = js("jsFacing_dedent")
    val omapo = js("jsFacing_omapo")
    val uuid = js("jsFacing_uuid")
    val codeLinesToString = js("jsFacing_codeLinesToString")
    val fcomapo = js("jsFacing_fcomapo")
//    val invalidateKotlinStackSourceMapConsumer = ::jsFacing_invalidateKotlinStackSourceMapConsumer
    val captureStackAsException = js("jsFacing_captureStackAsException")
    val omapa = js("jsFacing_omapa")

    val asn1: dynamic = {
        val all = js("Array.prototype.slice.call(arguments)")
        val first = all[0]
        val rest = all.slice(1)
        global.Object.assign.apply(global.Object, js("[]").concat(json(), /*...*/rest, first))
    }

    val asnn: dynamic = {
        val all = js("Array.prototype.slice.call(arguments)")
        val first = all[0]
        val rest = all.slice(1)
        global.Object.assign.apply(global.Object, js("[]").concat(json(), first, /*...*/rest))
    }

    val asnnoDollar: dynamic = {
        val all = js("Array.prototype.slice.call(arguments)")
        val first = all[0]
        val rest = all.slice(1)

        val res = json()
        global.Object.assign(res, first)
        for (arg: dynamic in jsArrayToListOfDynamic(rest)) {
            if (arg) {
                for (key: dynamic in jsArrayToListOfDynamic(global.Object.keys(arg))) {
                    if (key[0] != "$") {
                        res[key] = arg[key]
                    }
                }
            }
        }
        res
    }

    val spancTitle = ::jsFacing_spancTitle

    fun statefulElement(ctor: (update: () -> Unit) -> ShitWithRenderFunction): dynamic /*ToReactElementable*/ {
//    val ctor: (update: (then: () -> Unit) -> Unit) -> ShitWithRenderFunction = def.ctor
//    val noisy: dynamic = def.noisy
//    val displayName: dynamic = def.displayName

        var reactElement: dynamic = null
        var shouldUpdate: dynamic = null

        var inst: dynamic = ctor(update@{ // then: dynamic ->
            if (isInTestScenario() && hrss.worldIsHalted) return@update Unit
            if (!reactElement) return@update Unit // Not yet mounted or unmounted

            shouldUpdate = true
            reactElement.forceUpdate()

            // Shitus.fov(then)
        })

        if (jsTypeOf(inst) == "function") inst = json("render" to inst)
        Shitus.invariant(jsTypeOf(inst.render) == "function", "Element constructor should return a function or something with render()")

        inst.element = React.createElement(React.createClass(json(
            "componentWillMount" to {
                reactElement = js("this")
                Shitus.fov(inst.componentWillMount)
            },

            "componentDidMount" to {
                reactElement = js("this")
                Shitus.fov(inst.componentDidMount)
            },

            "componentWillUnmount" to {
                reactElement = null
                Shitus.fov(inst.componentWillUnmount)
            },

            "componentWillUpdate" to {
                Shitus.fov(inst.componentWillUpdate)
            },

            "componentDidUpdate" to {
                Shitus.fov(inst.componentDidUpdate)
            },

            "shouldComponentUpdate" to shouldComponentUpdate@{
                if (!shouldUpdate) return@shouldComponentUpdate false

                shouldUpdate = false
                return@shouldComponentUpdate true
            },

            "render" to render@{
                // TODO:vgrechka Render exception triangle
                return@render inst.render()

//            try {
//                val res = inst.render()
//                return@render res
//            } catch (e: Throwable) {
//                return@render renderExceptionTriangleAndRevealStack(json("exception" to e))
//            }
            }
        )), json())

        return inst

//        return object : ToReactElementable {
//            override fun toReactElement(): ReactElement {
//                return element
//            }
//        }
    }

    val updatableElement: dynamic = ::jsFacing_updatableElement
    val pageHeader: dynamic = ::jsFacing_pageHeader
    val Placeholder: dynamic = ::jsFacing_Placeholder

    val elementIDToControls: dynamic = json()

    val hideStackRevelation: dynamic = {
        old_debugPanes.remove("revealStack")
        revealStackCalledTimes = 0
    }

    val timestampString: dynamic = ::jsFacing_timestampString

    val sufindex: dynamic = {i: dynamic ->
        val len = 3

        var s = "" + i
        if (s.length > len) Shitus.raise("Number ${i} is too big for sufindex")
        "-i" + Shitus.repeat("0", len - s.length) + s
    }

    var rootTrain: dynamic = null
    var trainStack = jsArrayOf()

//    fun beginTrain(def: dynamic) {
//        val name = def.name
//
//        val newTrain = Shitus.asnn(def, json("\$\$typeof" to "train", "name" to name, "parent" to Shitus.currentTrain(), "items" to jsArrayOf(), "rpcs" to jsArrayOf()))
//        Shitus.pushItemToCurrentTrain(newTrain)
//        Shitus.trainStack.push(newTrain)
//        return newTrain
//    }
//
//    val endTrain: dynamic = {def: dynamic ->
//        Shitus.currentTrain() // Blows up if no train
//        Shitus.trainStack.pop()
//    }

//    fun currentTrain(): dynamic {
//        val train = Shitus.maybeCurrentTrain()
//        Shitus.invariant(train, "I need a train")
//        return train
//    }
//
//    fun maybeCurrentTrain() {
//        return lodash.last(Shitus.trainStack)
//    }

//    fun pushItemToCurrentTrain(item: dynamic) {
//        val items = currentTrain().items
//        if (items.length > 20) {
//            console.warn("Train is too long---shifting")
//            items.shift()
//        }
//
//        items.push(item)
//    }

//    fun initTrains() {
//        rootTrain = json("\$\$typeof" to "train", "name" to "Root", "items" to js("[]"), "rpcs" to js("[]"))
//        trainStack = jsArrayOf(rootTrain)
//    }

//    fun entraina(def: dynamic): Promisoid<Unit> = async {
//        val name: dynamic = def.name
//        val act: dynamic = def.act
//        val `$sourceLocation`: dynamic = def.`$sourceLocation`
//        val `$definitionStack`: dynamic = def.`$definitionStack`
//
//        beginTrain(json("name" to name, "\$sourceLocation" to `$sourceLocation`, "\$definitionStack" to `$definitionStack`)); try {
//            return@async await<dynamic>(act())
//        } finally { endTrain() }
//    }

}

fun jsFacing_spancTitle(def: dynamic): ReactElement {
    // #extract {title} from def
    val title = def.title
    jsDeleteKey(def, "title")
    return Shitus.spanc(global.Object.assign(def, json("tame" to "title", "content" to title)))
}

fun jsFacing_arrayDeleteFirstThat(arr: dynamic, pred: dynamic): dynamic {
    for (i in 0 until arr.length) {
        if (pred(arr[i])) {
            arr.splice(i, 1)
            return arr
        }
    }
    return arr
}

fun jsDeleteKey(obj: Any, key: String) {
    js("delete obj[key]")
}

fun jsFacing_delay(ms: dynamic): dynamic {
    die("@killme jsFacing_delay")
//    return newNativePromise({resolve: dynamic ->
//        global.setTimeout(resolve, ms)
//    })
}

fun jsFacing_run(f: dynamic): dynamic {
    return f()
}

fun jsFacing_runa(f: dynamic): Promisoid<dynamic> = async {
    await<dynamic>(f())
}

fun jsFacing_repeat(s: String, count: Int): String {
    return s.repeat(count)
}

fun jsFacing_fov(): dynamic {
    val all = js("Array.prototype.slice.call(arguments)")
    val x = all[0]
    return if (jsTypeOf(x) == "function") x.apply(null, all.slice(1)) else x
}

fun shittyFov(x: dynamic): dynamic {
    return if (jsTypeOf(x) == "function") x() else x
}

fun shittyFov(x: dynamic, y: dynamic): dynamic {
    return if (jsTypeOf(x) == "function") x(y) else x
}

fun jsFacing_fova(): Promisoid<dynamic> = async {
    val all = js("Array.prototype.slice.call(arguments)")
    val x = all[0]
    if (jsTypeOf(x) == "function") await<dynamic>(x.apply(null, x.slice(1))) else x
}

fun jsFacing_tokens(s: dynamic): dynamic {
    return s.trim().split(global.RegExp("\\s+"))
}

fun jsFacing_values(x: dynamic): dynamic {
    val res = js("[]")
    for (k in jsArrayToListOfDynamic(global.Object.keys(x)))
        res.push(x[k])
    return res
}

fun jsFacing_invariant(cond: dynamic, msg: dynamic) {
    if (!cond) {
        val message = "[INVARIANT VIOLATION] " + msg
        console.error(message)
        Shitus.raise(message)
    }
}

fun jsFacing_raise(msg: dynamic) {
    throw FatException(msg)
}

fun jsFacing_isEqual(a: Any?, b: Any?): Boolean {
    imf("jsFacing_isEqual")
}

fun jsFacing_clone(x: Any?): Any? {
    imf("jsFacing_clone")
}

fun jsFacing_isObject(value: dynamic): Boolean {
    val type = jsTypeOf(value)
    return value != null && (type == "object" || type == "function");
}

fun jsFacing_isBlank(s: dynamic): dynamic {
    return s == null || s.trim() == ""
}

fun jsFacing_sortKeys(o: dynamic) {
    if (global.Array.isArray(o)) {
        o.filter(::jsFacing_isObject).forEach(::jsFacing_sortKeys)
    } else if (jsFacing_isObject(o)) {
        var pairs = global.lodash.toPairs(o)
        pairs = global.lodash.sortBy(pairs, {x: dynamic -> x[0]})
        pairs.forEach({arg: dynamic ->
            val k = arg[0]
            jsDeleteKey(o, k)
        })
        pairs.forEach({arg: dynamic ->
            val k = arg[0]
            val v = arg[1]
            o[k] = v
            if (jsFacing_isObject(v)) {
                jsFacing_sortKeys(v)
            }
        })
    }
}


fun jsFacing_beginLogTime(what: dynamic): dynamic {
    val t0 = global.Date.now()
    dlog("--- BEGIN ${what}")

    return json(
        "end" to {
            val elapsed = global.Date.now() - t0
            dlog("--- END ${what} in ${elapsed}ms")
        }
    )
}

fun jsFacing_makeT(lang: dynamic): dynamic {
    fun t() {
        val all = js("Array.prototype.slice.call(arguments)")
        if (!all[0]) throw FatException("I don’t want falsy first argument in t()")
        var ss: dynamic = null
        if (jsTypeOf(all[0]) == "object") {
            ss = all[0]
            ss.EN = ss.EN || ss.en
            ss.UA = ss.UA || ss.ua
        } else {
            ss = json("EN" to all[0], "UA" to (all[1] || all[0]))
        }

        val res = ss[lang.toUpperCase()]
        if (!res) throw FatException("Localize me: " + global.nodeUtil.inspect(json("lang" to lang, "arguments" to all), json("depth" to null)))
        return res
    }

    return ::t
}

fun jsFacing_dedent(it: dynamic): dynamic {
    val lines = it.split("\\r?\\n")
    if (lines.length && !lines[0].trim()) {
        lines.shift()
    }
    if (lines.length && !lines[lines.length - 1].trim()) {
        lines.pop()
    }

    var minIndent = global.Number.MAX_SAFE_INTEGER
    for (line in jsArrayToListOfDynamic(lines)) {
        if (line.trim()) {
            val lineIndent = line.length - line.trimLeft().length
            if (lineIndent < minIndent) {
                minIndent = lineIndent
            }
        }
    }

    return lines.map({line: dynamic ->
        if (!line.trim()) "" else line.slice(minIndent)
    }).join("\n")
}

fun jsFacing_omapo(o: dynamic, f: dynamic): dynamic {
    return global.Object.keys(o).reduce({res: dynamic, k: dynamic, i: dynamic ->
        res[k] = f(o[k], k, i)
        res
    }, json())
}

fun jsFacing_uuid(): String {
    imf("jsFacing_uuid (do it on server, cause node-uuid is +1.5M)")
}

fun jsFacing_codeLinesToString(arg: dynamic): dynamic {
    val codeLines: dynamic = arg.codeLines
    val indent: dynamic = arg.indent

    var res = ""
    for (line in jsArrayToListOfDynamic(codeLines)) {
        res += " ".repeat(indent) + (line || "".asDynamic()) + "\n"
    }
    return res
}

fun jsFacing_fcomapo(o: dynamic, f: dynamic): dynamic {
    return jsFacing_omapo(jsFacing_focompact(o), f)
}

fun jsFacing_focompact(o: dynamic) {
    return jsFacing_ocompact(jsFacing_ofov(o))
}

fun jsFacing_ofov(o: dynamic) {
    return jsFacing_omapo(o, {v: dynamic -> Shitus.fov(v)})
}

fun jsFacing_ocompact(o: dynamic) {
    return jsFacing_ofilter(o, {v: dynamic -> !jsFacing_nilf(v)})
}

fun jsFacing_ofilter(o: dynamic, f: dynamic): dynamic {
    return global.Object.keys(o).reduce({res: dynamic, k: dynamic, i: dynamic ->
        if(f(o[k], k, i)) res[k] = o[k]
        res
    }, json())
}

fun jsFacing_nilf(x: dynamic): Boolean {
    return x == null || x == false
}

//fun jsFacing_invalidateKotlinStackSourceMapConsumer() {
//    kotlinStackSourceMapConsumerPromise = null
//}

fun jsFacing_captureStackAsException(msg: String?): dynamic {
    try {
        throw FatException(msg ?: "Gimme fucking stack")
    } catch (e: Throwable) {
        return e
    }
}

fun jsFacing_omapa(o: dynamic, f: dynamic): dynamic {
    return global.Object.keys(o).map {k: dynamic, i: dynamic -> f(o[k], k, i)}
}


fun jsFacing_updatableElement(def: dynamic, ctor_killme: dynamic): dynamic {
    var renderCtor: dynamic = def.renderCtor
    if (renderCtor != null && ctor_killme != null) Shitus.raise("Gimme either renderCtor or ctor_killme")

    if (!renderCtor) renderCtor = ctor_killme

    var inst: dynamic = null
    var renderElement: dynamic = null

    fun makeRenderElement() {
        renderElement = renderCtor(updater@{then: dynamic ->
            if (isInTestScenario() && hrss.worldIsHalted) return@updater Unit
            if (!inst) return@updater Unit

//            try {
                inst.forceUpdate()
//            } catch (e: Throwable) {
//                Shitus.raiseWithMeta(json("message" to e.message, "meta" to def, "cause" to e))
//            }

            Shitus.fov(then)
        })
    }

    makeRenderElement()


    return React.createElement(React.createClass(json(
        "componentDidMount" to {
            inst = js("this")
        },

        "componentWillUnmount" to {
            inst = null
        },

        "render" to render@{
            return@render renderElement()
//            try {
//                const res = renderElement()
//                return res
//            } catch (e) {
//                return renderExceptionTriangleAndRevealStack({exception: e})
//            }
        }
    )), json())
}

fun pageHeader(title: String): ReactElement {
//    val className = ""
//    return reactCreateElement(
//        "div",
//        json("className" to "page-header $className", "style" to json("marginTop" to 0, "marginBottom" to 15)),
//        listOf(
//            reactCreateElement(
//                "h3",
//                json("tame" to "pageHeader", "style" to json("marginBottom" to 0)),
//                listOf(
//                    title.asReactElement()
//                )
//            )
//        )
//        )

    return jsFacing_pageHeader(json("title" to title))
}

fun jsFacing_pageHeader(def: dynamic): dynamic {
    val title: dynamic = def.title
    val labels: dynamic = def.labels ?: jsArrayOf()
    val className: dynamic = def.className ?: ""

    val id = puid()

    "rrrrrrrrrrrrrrrrrrrrrrrrrrrr"
    val me = json(
        "render" to render@{
            // TODO:vgrechka Externalize pageHeader colors    bec22c87-4fa1-4118-aa88-a37a6baeca56
            return@render Shitus.diva(json("className" to "page-header ${className}", "style" to json("marginTop" to 0, "marginBottom" to 15)),
                Shitus.h3a(
                    json("tame" to "pageHeader", "style" to json("marginBottom" to 0)),
                    Shitus.spancTitle(json("title" to title)) // ,
//                    /*...*/labels.map({label: dynamic, i: dynamic ->
//                        val style = json(
//                            "fontSize" to "12px",
//                            "fontWeight" to "normal",
//                            "position" to "relative",
//                            "top" to "-4px",
//                            "left" to "8px",
//                            "display" to "inline",
//                            "padding" to ".2em .6em .3em",
//                            "lineHeight" to "1",
//                            "color" to "#fff",
//                            "textAlign" to "center",
//                            "whiteSpace" to "nowrap",
//                            "verticalAlign" to "baseline",
//                            "borderRadius" to ".25em"
//                        )
//                        if (label.level == "success") {
//                            global.Object.assign(style, json("background" to LIGHT_GREEN_700.toString()))
//                        } else {
//                            Shitus.raise("Weird pageHeader label level: ${label.level}")
//                        }
//                        return@map Shitus.spana(json("tame" to "label${Shitus.sufindex(i)}", "tattrs" to json("level" to label.level), "style" to style),
//                            Shitus.spancTitle(json("title" to label.title))
//                        )
//                    })
)
            )
        }
    )

    return Shitus.elcl(me)
}

fun jsFacing_Placeholder(): dynamic {
    var content: dynamic = null
    var prevContent: dynamic = null

    return Shitus.statefulElement(ctor@{update: dynamic ->
            var me: dynamic = null
            me = json(
                "render" to render@{
                    return@render content
                },

                "setContent" to {newContent: dynamic ->
                    prevContent = content
                    content = newContent
                    update()
                },

                "setPrevContent" to {
                    me.setContent(prevContent)
                }
            )

            return@ctor me
        })
}

fun jsFacing_nostring(arg: dynamic): dynamic {
    val no: dynamic = arg.no
    var lang: dynamic = arg.lang

    if (!lang) {
        if (jsTypeOf(global.LANG) == "string") lang = global.LANG
        else Shitus.raise("If not lang, I want global LANG")
    }
    lang = lang.toUpperCase()

    if (lang == "EN") return "#" + no
    if (lang == "UA" || lang == "RU") return "№" + no
    wtf("Weird lang for nostring: ${lang}")
}

fun jsFacing_horizontala() {
    val all = js("Array.prototype.slice.call(arguments)")
    val def = all[0]
    val items = all.slice(1)

    val spacing: dynamic = def.spacing
    val style: dynamic = def.style ?: json()
    val className: dynamic = def.className ?: ""

    Shitus.invariant(spacing != null, "Gimme some spacing")

    return Shitus.diva.apply(null, js("[]").concat(
        Shitus.asn1(json("controlTypeName" to (def.controlTypeName ?: "horizontala"), "className" to className, "style" to Shitus.asnn(json("display" to "flex"), style)), def),
        /*...*/items.map({item: dynamic, i: dynamic -> if (item != null) Shitus.diva(json("style" to json("marginLeft" to if (i > 0) spacing else 0)), item) else null}))
    )
}

fun jsFacing_hor1() {
    val all = js("Array.prototype.slice.call(arguments)")
    val def = all[0]
    val items = all.slice(1)

    return Shitus.horizontala.apply(null, js("[]").concat(Shitus.asn1(json("controlTypeName" to "hor1", "spacing" to 4), def), /*...*/items))
}

fun jsFacing_hor2() {
    val all = js("Array.prototype.slice.call(arguments)")
    val def = all[0]
    val items = all.slice(1)

    return Shitus.horizontala.apply(null, js("[]").concat(Shitus.asn1(json("controlTypeName" to "hor2", "spacing" to 8), def), /*...*/items))
}

fun jsFacing_byid(id: dynamic): dynamic {
    return js("$")("#" + ("" + id).asDynamic().replace(global.RegExp("\\.", "g"), "\\."))
}

fun jsFacing_byid0(id: dynamic): dynamic {
    return jsFacing_byid(id)[0]
}

fun jsFacing_errorBanner(def: dynamic): ReactElement {
    val content: dynamic = def.content
    val style: dynamic = def.style

    return Shitus.blockquotea(json("controlTypeName" to "errorBanner", "style" to Shitus.asnn(json(
        "backgroundColor" to RED_50,
        "borderLeft" to "3px solid " + Color.RED_300,
        "fontSize" to "14px",
        "marginBottom" to 15
        ), style)),
        Shitus.spanc(json("tame" to "errorBanner", "content" to content)))
}

fun jsFacing_glyph(_name: dynamic, _def: dynamic): ReactElement {
    var name: dynamic = _name
    val def: dynamic = _def ?: json()

    val hint: dynamic = def.hint
    val className: dynamic = def.className ?: ""

    val attrs: dynamic = global.Object.assign(js("({})"), def)
    jsDeleteKey(attrs, "hint")
    jsDeleteKey(attrs, "className")
//    val attrs: dynamic = lodash.omit(def, "hint", "className")

    val names: dynamic = Shitus.tokens(name)
    name = names[0]
    var className0: dynamic = null
    val className1: dynamic = names[1] ?: ""
    if (name.startsWith("gi-"))
        className0 = "glyphicon glyphicon-" + name.slice(3)
    else
        className0 = "fa fa-" + name
    return React.createElement("i", global.Object.assign(json("className" to "${className0} ${className1} ${className}", "title" to hint), attrs))
}

fun jsFacing_raiseWithMeta(def: dynamic) {
    val message: dynamic = def.message
//    val meta: dynamic = def.meta
//    var metaItems: dynamic = def.metaItems
//    val cause: dynamic = def.cause
//
//    if (!(meta || metaItems)) Shitus.raise("Gimme meta or metaItems, please, fuck you")
//    if (meta && metaItems) Shitus.raise("Either meta or metaItems, please, fuck you")
//
//    if (meta) {
//        metaItems = jsArrayOf(json("meta" to meta))
//    }

    Shitus.raise(message)

//    Shitus.raise(message, json("cause" to cause, "\$render" to render@{
//        return Shitus.diva({}, ...metaItems.map((item, index) => {
//        if (!item.meta.$definitionStack) return renderRedExclamationTriangleLabel({title: t("I want meta.$definitionStack")})
//
//        let title
//            if (item.title) title = item.title
//            else if (item.titlePrefix) title = t("${item.titlePrefix} $definitionStack:")
//            else if (metaItems.length == 1) title = t("$definitionStack:")
//            else title = t("$definitionStack ${index}:")
//
//        return updatableElement(s{}, update => {
//            let loading
//                if (typeof item.meta.$definitionStack == "function") {
//            loading = true
//            Shitus.run(async function() {
//                stack = await item.meta.$definitionStack()
//                loading = false
//                update()
//            })
//        } else {
//            stack = item.meta.$definitionStack
//        }
//
//            return _=> {
//            if (loading) {
//                return Shitus.spana({style: {fontStyle: "italic"}}, "Loading...")
//            } else {
//                return Shitus.diva({style: {marginTop: index == 0 ? 0 : 10}},
//                    renderDefinitionStackStrip(s{title, stack}))
//            }
//        }
//        })
//    }))
//        return renderDefinitionStackStrip(s{stack: meta.$definitionStack})
//    }))
}

//fun jsFacing_revealControl(arg: dynamic) {
//    imf("jsFacing_revealControl")
//
////    export function revealControl(target, {backTarget, scrollToTarget}={}) {
////        controlBeingRevealed = target
////        dumpContributionsByControlAndChildren(target, {})
////
////        debugPanes.set({name: 'revealControl', element: updatableElement({}, update => {
////            const paneHeight = 210
////            const paneBodyHeight = 118
////
////            const my = {}
////            const jqel = Shitus.byid(target.elementID)
////            if (!jqel.length) {
////                console.error('Failed to find element', deepInspect(target))
////                console.error(target.$definitionStack)
////                Shitus.raise(`Failed to find element with ID ${target.elementID}`)
////            }
////
////            if (scrollToTarget) {
////                const ofs = jqel.offset()
////                $(document).scrollTop(ofs.top - 50 - 20)
////            }
////
////            let attrsInput
////                if (target.$attrsTinkerable) {
////                    let attrSnap
////                        if (target.$takeAttrSnap) {
////                            attrSnap = target.$takeAttrSnap()
////                        } else {
////                            attrSnap = {className: target.className || '', style: target.style || {}}
////                        }
////                    const attrSnapString = deepInspect(attrSnap).replace(/ *\n\ */g, ' ').replace(/\{ /g, '{').replace(/ \}/g, '}')
////                    attrsInput = Input({kind: 'textarea', initialValue: attrSnapString,
////                        onKeyDown(e) {
////                            if (e.ctrlKey && e.keyCode == 13) {
////                                e.preventDefault()
////                                applyAttrs()
////                            }
////                        }, untested: true})
////                }
////            const borderCheck = Checkbox({initialValue: true, onChange: update, untested: true})
////
////            return _=> Shitus.diva({noStateContributions: true},
////            borderCheck.getValue() && elementFrame({jqel, border: `3px dashed ${BLACK}`}),
////
////            updatableElement({}, update => {
////                let frame = null
////                my.setPotentialRevelationFrame = function(newFrame) {
////                    frame = newFrame; update()
////                }
////                return _=> frame
////            }),
////
////            Shitus.diva({style: {
////                position: 'fixed', left: 0, bottom: 0, width: '100%', overflow: 'auto',
////                zIndex: topZIndex++, minHeight: paneHeight, maxHeight: paneHeight + 200, padding: 10,
////                borderTop: `3px solid ${BLACK}`, background: WHITE}},
////
////                Shitus.run(_=> {
////                    // @wip sourceLocation promise
////                    if (target.$tag || target.$sourceLocation) return OpenSourceCodeLink({where: pick(target, '$tag', '$sourceLocation')})
////                    return Shitus.diva({}, t('No client-side source location attached'))
////                }),
////
////            Shitus.diva({style: {position: 'relative'}},
////                Shitus.diva({className: 'row', style: {marginTop: 10}},
////
////                    Shitus.diva({className: 'col-sm-3', style: {}},
////                        Shitus.diva({className: 'form-group'},
////                            labela({}, t(`Here`)),
////                            Shitus.run(function() {
////                                const items = []
////
////                                const controls = elementIDToControls[target.elementID]
////                                for (const control of controls) {
////                                if (control.id == target.id) {
////                                    items.push(Shitus.diva({style: {whiteSpace: 'nowrap', fontStyle: 'italic'}}, '> ' + control.getLongRevelationTitle()))
////                                } else {
////                                    items.push(revealAnotherLink({control}))
////                                }
////                            }
////
////                                return Shitus.diva({style: {height: paneBodyHeight, overflow: 'auto'}}, ...items)
////                            }))),
////
////                    Shitus.diva({className: 'col-sm-3', style: {paddingLeft: 0}},
////                        Shitus.diva({className: 'form-group'},
////                            labela({}, t(`Parents`)),
////                            Shitus.run(function() {
////                                const items = []
////
////                                jqel.parents().each(function() {
////                                    if (!this.id) return
////                                    const controls = elementIDToControls[this.id] || []
////                                    for (const control of controls.slice().reverse()) {
////                                    items.push(revealAnotherLink({control}))
////                                }
////                                })
////
////                                if (!items.length) return Shitus.diva({}, 'No parents')
////                                return Shitus.diva({style: {height: paneBodyHeight, overflow: 'auto'}}, ...items)
////                            }))),
////
////                    Shitus.diva({className: 'col-sm-3', style: {}},
////                        Shitus.diva({className: 'form-group'},
////                            labela({}, t(`Children`)),
////                            Shitus.run(function renderChildren() {
////                                const items = []
////
////                                run2(jqel, 0, function findItems(el, indent) {
////                                    const children = el.children()
////                                    if (children.length = 0) return
////
////                                    children.each(function() {
////                                        let indentIncrease = 0
////                                        if (this.id) {
////                                            const controls = elementIDToControls[this.id] || []
////                                            for (const control of controls) {
////                                                items.push(revealAnotherLink({control}))
////                                            }
////                                        }
////
////                                        findItems($(this), indent + indentIncrease)
////                                    })
////                                })
////
////                                if (!items.length) return Shitus.diva({}, 'No children')
////                                return Shitus.diva({style: {height: paneBodyHeight, overflow: 'auto'}}, ...items)
////                            })),
////                        ),
////
////                    Shitus.diva({className: 'col-sm-3', style: {}},
////                        Shitus.diva({className: 'form-group'},
////                            labela({}, t(`Siblings`)),
////                            Shitus.run(function() {
////                                const items = []
////
////                                const jqchildren = jqel.parent().children()
////                                jqchildren.each(function() {
////                                    const controls = elementIDToControls[this.id]
////                                    if (controls && controls.length) {
////                                        const control = controls[0]
////                                        if (control.elementID == target.elementID) {
////                                            items.push(Shitus.diva({style: {whiteSpace: 'nowrap', fontStyle: 'italic'}}, '> ' + control.getLongRevelationTitle()))
////                                        } else {
////                                            items.push(revealAnotherLink({control}))
////                                        }
////                                    }
////                                })
////
////                                return Shitus.diva({style: {height: paneBodyHeight, overflow: 'auto'}}, ...items)
////                            }))),
////                    ),
////
////                Shitus.diva({},
////                    target.testName && Shitus.link({title: t('Hand pause'), untested: true, onClick() {
////                        let movingHand, startScreenX, startScreenY, startDleft, startDtop, dleft = 0, dtop = 0, handShown
////                        const pausePointTag = uuid()
////                        const progressPlaceholder = Placeholder()
////                        const codeArea = Input({kind: 'textarea', untested: true, style: {fontFamily: 'monospace'}})
////
////                        generateCodeAndShowHand()
////
////                        function generateCodeAndShowHand() {
////                            if (handShown) {
////                                testGlobal.controls[target.testName].hideHand()
////                                handShown = false
////                            }
////
////                            const showHandArg = {testActionHandOpts: {pointingFrom: 'right', dleft, dtop}}
////                            const showHandArgForTinkering = cloneDeep(showHandArg)
////                            showHandArgForTinkering.testActionHandOpts.noBlinking = true
////                            testGlobal.controls[target.testName].showHand(showHandArgForTinkering)
////                            handShown = true
////
////                            codeArea.setValue(dedent(`
////                                // /*killme*/ setTestSpeed('slow'); art.respectArtPauses = true
////                                testGlobal.controls['${target.testName}'].showHand(${JSON.stringify(showHandArg)})
////                            ${'#'}hawait art.pausePoint({title: 'TODO: Describe situation', $tag: '${pausePointTag}'})
////                            testGlobal.controls['${target.testName}'].hideHand()
////                            `))
////                        }
////
////                        document.addEventListener('mousemove', mouseMoveListener)
////                        function mouseMoveListener(e) {
////                            if (movingHand) {
////                                if (e.ctrlKey) {
////                                    dleft = startDleft + e.screenX - startScreenX
////                                    dtop = startDtop + e.screenY - startScreenY
////                                    generateCodeAndShowHand()
////                                } else {
////                                    movingHand = false
////                                }
////                            } else {
////                                if (e.ctrlKey) {
////                                    startScreenX = e.screenX
////                                    startScreenY = e.screenY
////                                    startDleft = dleft
////                                    startDtop = dtop
////                                    movingHand = true
////                                }
////                            }
////                        }
////
////                        revealer.reveal({
////                            pane: Shitus.diva({style: {position: 'relative'}},
////                            Shitus.diva({style: {height: '1.5em'}}),
////                            Shitus.diva({},
////                                Shitus.diva({className: 'form-group'}, labela({}, t('Code')), Shitus.diva({}, codeArea)),
////                                ),
////                            Shitus.diva({style: {position: 'absolute', right: 0, top: 0, display: 'flex'}},
////                                Shitus.diva({style: {marginTop: 8, marginRight: 10}}, progressPlaceholder),
////                                art.actionPlaceholderTag && button({level: 'primary', icon: 'pencil', title: t('Insert Test Action Code'), style: {}, async onClick() {
////                                    callDebugRPWithProgress({msg: {fun: 'danger_insertTestActionCode', placeholderTag: art.actionPlaceholderTag, code: codeArea.getValue()}, progressPlaceholder, progressTitle: 'Inserting test action code'})
////                                }, untested: true}),
////                                ),
////                            ),
////
////                            onClose() {
////                                document.removeEventListener('mousemove', mouseMoveListener)
////                                if (handShown) {
////                                    testGlobal.controls[target.testName].hideHand()
////                                    handShown = false
////                                }
////                            },
////                        })
////                    }}),
////
////                    target.renderInRevelationPane,
////
////                    target.$definitionStack && renderDefinitionStackStrip({stack: target.$definitionStack}),
////
////                    //                                target.$ctorArgument && Shitus.diva({style: {display: 'flex', flexWrap: 'wrap'}},
//////                                    Shitus.diva({style: {fontWeight: 'bold', marginRight: 10}}, t('$ctorArgument:')),
//////                                    Shitus.diva({}, deepInspect(target.$ctorArgument))),
////
////                    Shitus.run(_=> {
////                        my.stacksCShit = my.stacksCShit || CollapsibleShit({content: Shitus.diva({}, _=> renderStacks(pickStacks(target)))})
////                        return Shitus.diva({},
////                            Shitus.diva({style: {display: 'flex', marginRight: 10}},
////                                Shitus.diva({style: {fontWeight: 'bold'}}, t('Stacks')),
////                                my.stacksCShit.renderCaret({style: {marginLeft: 10}})),
////                            my.stacksCShit.renderContent())
////                    }),
////
////                makeBackendMetaPanel({metaID: target.$metaID}),
////
////                Shitus.fov(target.contributeRevelationSection),
////                )
////            ),
////
////            Shitus.diva({style: {position: 'absolute', right: 5, top: 5, display: 'flex', alignItems: 'center'}},
////                target.tame && target.controlTypeName && Shitus.diva({style: {backgroundColor: LIME_200, fontWeight: 'bold', padding: 5}}, target.getTamePath()),
////                target.controlTypeName && Shitus.diva({style: {marginLeft: 5, backgroundColor: LIME_200, fontWeight: 'bold', padding: 5}}, target.controlTypeName),
////                Shitus.diva({style: {marginRight: 15, backgroundColor: WHITE, fontWeight: 'normal', padding: 5}}, `#${target.elementID}`),
////                Shitus.diva({style: {display: 'flex', marginLeft: 5}}, borderCheck, t('Border')),
////                backTarget && button({icon: 'reply', style: {marginLeft: 5}, onClick() {
////                    revealControl(backTarget)
////                }, untested: true}),
////                target.$attrsTinkerable && button({level: 'primary', icon: 'bolt', style: {marginLeft: 5}, onClick: applyAttrs, untested: true}),
////                button({level: 'danger', icon: 'close', style: {marginLeft: 5},
////                    onClick: closeControlRevealer = function() {
////                        closeControlRevealer = undefined
////                        controlBeingRevealed = undefined
////                        debugPanes.delete({name: 'revealControl'})
////                    },
////                    untested: true}),
////                ),
////            ))
////
////            function elementFrame({jqel, border}) {
////                const ofs = jqel.offset()
////                let width = jqel.outerWidth(true) + 10
////                let height = jqel.outerHeight(true) + 10
////                let left = ofs.left - 5
////                let top = ofs.top - 5
////                return Shitus.diva({style: {
////                    position: 'absolute',
////                    left, top, width, height,
////                    border, zIndex: topZIndex++}})
////            }
////
////            function applyAttrs() {
////                const newAttrSnap = eval(`(${attrsInput.getValue()})`)
////                if (target.$tinkerWithAttrs) {
////                    target.$tinkerWithAttrs(newAttrSnap)
////                } else {
////                    target.className = newAttrSnap.className
////                    target.style = newAttrSnap.style
////                    target.$update()
////                }
////            }
////
////            function revealAnotherLink({control}) {
////                return Shitus.diva({style: {whiteSpace: 'nowrap'}}, Shitus.link({content: control.getLongRevelationTitle(),
////                    onClick() {
////                        revealControl(control, {backTarget: target})
////                    },
////                    onMouseEnter() {
////                        my.setPotentialRevelationFrame(elementFrame({jqel: Shitus.byid(control.elementID), border: `2px dashed ${GRAY_500}`}))
////                    },
////                    onMouseLeave() {
////                        my.setPotentialRevelationFrame(null)
////                    }
////                }))
////            }
////        })})
////
////        controlBeingRevealed = target
////    }
//}

fun jsFacing_timestampString(_ts: dynamic, _opts: dynamic = null) {
    var ts: dynamic = _ts
    val opts: dynamic = _opts ?: json()
    val includeTZ: dynamic = opts.includeTZ

    if (hrss.lang == "ua" || hrss.lang == "UA") {
        val dotIndex = ts.indexOf(".")
        if (dotIndex != -1) {
            ts = ts.slice(0, dotIndex)
        }
        val m = global.moment.tz(ts, "YYYY-MM-DD HH:mm:ss", "UTC")
        if (!m.isValid()) Shitus.raise("Bad format of timestamp: [${ts}]")
        ts = m.tz("Europe/Kiev").format("DD/MM/YYYY HH:mm:ss")
        if (includeTZ) {
            ts += " (Киев)"
        }
        return ts
    }

    Shitus.raise("Implement timestampString for language ${hrss.lang}")
}



