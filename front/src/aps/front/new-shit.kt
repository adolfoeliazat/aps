/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.*
import org.w3c.dom.events.*

val kdiv = ElementBuilderFactory("div")
val kspan = ElementBuilderFactory("span")
val kspan_nolint = ElementBuilderFactory("span")
val h3 = ElementBuilderFactory("h3")
val kul = ElementBuilderFactory("ul")
val kol = ElementBuilderFactory("ol")
val kli = ElementBuilderFactory("li")
val ka = ElementBuilderFactory("a")

fun span(s: String? = null) = kspan {it-s}

open class ElementBuilder(val tag: String, val attrs: Attrs, var style: Style, block: ((ElementBuilder) -> Unit)? = null) : ToReactElementable {
    val children = mutableListOf<ToReactElementable>()

    init {
        block?.let {it(this)}
    }

    fun add(eb: ToReactElementable?) {
        if (eb != null) children.add(eb)
    }

    fun add(f: () -> ReactElement?) {
        add(object:ToReactElementable {
            override fun toReactElement(): ReactElement? = f()
        })
    }

    fun add(re: ReactElement?) {
        if (re != null) add {re}
    }

    operator fun minus(eb: ToReactElementable?) {
        add(eb)
    }

    operator fun minus(s: String?) {
        if (s != null) minus(s.asDynamicReactElement())
    }

    operator fun minus(re: ReactElement?) {
        add(re)
    }

    operator fun minus(newStyle: Style) {
        style = newStyle
    }

    fun addAll(ebs: Iterable<ToReactElementable?>) {
        ebs.forEach{add(it)}
    }

    fun addAll(ebs: Iterable<ReactElement?>) {
        ebs.forEach{add(it)}
    }

    operator fun plus(ebs: Iterable<ToReactElementable?>) {
        addAll(ebs)
    }

    operator fun plus(ebs: Iterable<ReactElement?>) {
        addAll(ebs)
    }

    override fun toReactElement(): ReactElement = control.toReactElement()

    open fun wrapChild(index: Int, child: ToReactElementable): ToReactElementable = child

    val control:Control2 by lazy {
        object : Control2(attrs) {
            override fun defaultControlTypeName() = tag

            override fun render(): ToReactElementable {
                val jsAttrs = json(
                    "id" to elementID,
                    "style" to style.toReactStyle()
                )
                attrs.className?.let {jsAttrs["className"] = it}
                attrs.href?.let {jsAttrs["href"] = it}
                attrs.onClick?.let {jsAttrs["onClick"] = it}

                return React.createElement(
                    tag,
                    jsAttrs,
                    // TODO:vgrechka This looks shitty
                    *children
                        .map{it.toReactElement()}
                        .filterNotNull()
                        .mapIndexed {index, child ->
                            wrapChild(index, ToReactElementable.from(child))
                        }
                        .map{it.toReactElement()}
                        .filterNotNull()
                        .toTypedArray()
                ).toToReactElementable()
            }
        }
    }

}

@MixableType
data class Style(
    var top: Any? = null,
    var right: Any? = null,
    var bottom: Any? = null,
    var left: Any? = null,
    var width: Any? = null,
    var height: Any? = null,
    var position: String? = null,
    var margin: Any? = null,
    var marginTop: Any? = null,
    var marginRight: Any? = null,
    var marginBottom: Any? = null,
    var marginLeft: Any? = null,
    var paddingTop: Any? = null,
    var paddingRight: Any? = null,
    var paddingBottom: Any? = null,
    var paddingLeft: Any? = null,
    var padding: Any? = null,
    var color: Any? = null,
    var backgroundColor: Any? = null,
    var border: String? = null,
    var borderTop: String? = null,
    var borderRight: String? = null,
    var borderBottom: String? = null,
    var borderLeft: String? = null,
    var textAlign: String? = null,
    var fontFamily: String? = null,
    var fontSize: String? = null,
    var fontWeight: String? = null,
    var fontStyle: String? = null,
    var display: String? = null,
    var justifyContent: String? = null,
    var whiteSpace: String? = null,
    var cursor: String? = null,
    var opacity: Any? = null,
    var float: String? = null
) {
    fun toReactStyle(): dynamic {
        return dyna{o->
            checkNSI(marginTop, "marginTop")
            checkNSI(marginRight, "marginRight")
            checkNSI(marginLeft, "marginLeft")
            checkNSI(marginBottom, "marginBottom")
            checkNSI(paddingBottom, "paddingBottom")
            checkColor(color, "backgroundColor")
            checkColor(backgroundColor, "backgroundColor")

            top?.let {o.top = it}
            right?.let {o.right = it}
            bottom?.let {o.bottom = it}
            left?.let {o.left = it}
            width?.let {o.width = it}
            height?.let {o.height = it}
            position?.let {o.position = it}
            margin?.let {o.margin = it}
            marginTop?.let {o.marginTop = it}
            marginRight?.let {o.marginRight = it}
            marginBottom?.let {o.marginBottom = it}
            marginLeft?.let {o.marginLeft = it}
            padding?.let {o.padding = it}
            paddingTop?.let {o.paddingTop = it}
            paddingRight?.let {o.paddingRight = it}
            paddingBottom?.let {o.paddingBottom = it}
            paddingLeft?.let {o.paddingLeft = it}
            color?.let {o.color = it.toString()}
            backgroundColor?.let {o.backgroundColor = it.toString()}
            border?.let {o.border = it}
            borderTop?.let {o.borderTop = it}
            borderRight?.let {o.borderRight = it}
            borderBottom?.let {o.borderBottom = it}
            borderLeft?.let {o.borderLeft = it}
            textAlign?.let {o.textAlign = it}
            fontFamily?.let {o.fontFamily = it}
            fontSize?.let {o.fontSize = it}
            fontWeight?.let {o.fontWeight = it}
            fontStyle?.let {o.fontStyle = it}
            display?.let {o.display = it}
            justifyContent?.let {o.justifyContent = it}
            whiteSpace?.let {o.whiteSpace = it}
            cursor?.let {o.cursor = it}
            opacity?.let {o.opacity = it}
            float?.let {o.float = it}
        }
    }

    fun checkNSI(value: Any?, name: String) {
        when {
            value == null -> return
            jsTypeOf(value).oneOf("string", "number") -> return
            else -> bitch("$name should be null, String, or Int, but got [$value]")
        }
    }

    fun checkColor(value: Any?, name: String) {
        when {
            value == null -> return
            jsTypeOf(value) == "string" -> return
            ctorName(value) == "Color" -> return
            else -> bitch("$name should be null, String, or Color, but got [$value]")
        }
    }
}

class ControlShitMe : ShitWithRenderFunction, FuckingControl {
    override lateinit var render: () -> ReactElement

    lateinit var controlTypeName: String
    lateinit var id: String
    override var elementID: String = ""
    var tame: String? = null
    var tamy: String? = null
    var shame: String? = null
    var shamyPrefix: String? = null
    var `$definitionStack`: Any? = null
    var `$sourceLocation`: Any? = null
    var `$tag`: Any? = null
    var `$callStack`: Any? = null
    var tamyPrefix: String? = null
    var tattrs: Json? = null
    lateinit var debugDisplayName: String
    var noStateContributions: Boolean = false
    lateinit var getLongRevelationTitle: () -> String
    var ignoreDebugCtrlShiftClick: Boolean = false
    var effectiveShame: String? = null
    lateinit var captureAction: (dynamic) -> Unit
    var onRootClick: ((Any?) -> Promise<Any?>)? = null
    var testClick: ((dynamic) -> Unit)? = null
    var testKeyDown: ((dynamic) -> Unit)? = null
    var testSetValue: ((dynamic) -> Unit)? = null
    var contributeTestState: ((dynamic) -> Unit)? = null
    lateinit var getTamePath: () -> String
    lateinit var stickException: (e: dynamic) -> Unit
    lateinit var testShowHand: (dynamic) -> Unit
    lateinit var testHideHand: () -> Unit
    var setValueIsAction: Boolean = false
    lateinit var testGetValue: () -> Any?
}

fun implementControlShit2(me: ControlShitMe, def: dynamic, implementTestClick: dynamic = null, implementTestKeyDown: dynamic = null) {
    class Shit {
        var errorStickerID: String? = null
        var errorStickerTether: Tether? = null
    }
    val shit = Shit()

    if (def.controlTypeName != null || def.ctn != null) me.controlTypeName = def.controlTypeName ?: def.ctn
    Shitus.invariant(me.controlTypeName, "I want controlTypeName")
    Shitus.invariant(!(me.tame != null && me.tamy != null), "I want either tame or tamy")

    me.`$definitionStack` = def.`$definitionStack`
    val `$definitionStack` = me.`$definitionStack`

    me.id = puid()
    if (me.elementID == null) me.elementID = def.id ?: def.elementID ?: puid().asDynamic()
    if (me.`$tag` == null) me.`$tag` = def.`$tag`
    if (me.`$sourceLocation` == null) me.`$sourceLocation` = def.`$sourceLocation`
    if (me.`$callStack` == null) me.`$callStack` = def.`$callStack`

//        if (def.shameIsTamePath != undefined) me.shameIsTamePath = true

    if (def.tamyShamy) {
        Shitus.invariant(!(def.tamy != null || def.shamy != null), "tamyShamy is incompatible with tamy or shamy")
        def.shamy = def.tamyShamy
        def.tamy = def.shamy
    }

    if (me.shame == null) {
        if (def.shamy) {
            if (me.shamyPrefix == null) me.shamyPrefix = me.tamyPrefix ?: me.controlTypeName
            me.shame = me.shamyPrefix!! + "-" + def.shamy
        } else {
            me.shame = def.shame
        }
    }

    if (me.tame == null) {
        if (def.tamy == true) me.tame = me.tamyPrefix ?: me.controlTypeName
        else if (def.tamy) me.tame = (me.tamyPrefix ?: me.controlTypeName).asDynamic() + "-" + def.tamy
        else me.tame = def.tame
    }
    if (me.tattrs == null) me.tattrs = def.tattrs
    if (me.tattrs != null) Shitus.invariant(me.tame, "Control with tattrs should be tamed")

    if (me.tame != null && me.controlTypeName != null && me.tame != me.controlTypeName) me.debugDisplayName = "${me.tame}"
    else if (def.tame != null) me.debugDisplayName = def.tame
    else if (me.controlTypeName != null) me.debugDisplayName = me.controlTypeName
    else me.debugDisplayName = "dunno"

    if (def.noStateContributions != null) me.noStateContributions = def.noStateContributions

    try {me.getLongRevelationTitle} catch(e: Throwable) {
        me.getLongRevelationTitle = {
            me.debugDisplayName
        }
    }

    fun addEventListeners() {
        Shitus.byid(me.elementID).off() // Several controls can be on same element, and we don't want to handle click several times
        Shitus.byid(me.elementID).on("click", onClick@{e: KeyboardEvent -> "__async"
            if (MODE == "debug" && e.ctrlKey) {
                if (e.shiftKey) {
                    if (me.ignoreDebugCtrlShiftClick) return@onClick Unit

                    preventAndStop(e)

                    if (me.effectiveShame == null) {
                        Shitus.raiseWithMeta(json("message" to "Put some shame on me", "meta" to me))
                    }

                    return@onClick me.captureAction(null) // TODO:vgrechka Prettier default argument
                }

                preventAndStop(e)
                die("This code path should be deleted")
//                return@onClick Shitus.revealControl(me)
            }

//            val shit: ((Any?) -> Promise<Any?>)? = me.onRootClick
            me.onRootClick?.let {__await(it(e))}
//            __await<dynamic>(jshit.utils.fova(me.onRootClick, e))
        })
    }

    fun removeEventListeners() {
        Shitus.byid(me.elementID).off()
    }

    var came: dynamic = null
    val mutatorFunctionNames = jsArrayOf("setValue", "click")

    fun hasTestManipulationFunctions(): dynamic {
        return (me.testClick ?: me.testSetValue) != null
    }

    decorate(json("target" to me,
        "pre_componentWillUpdate" to outta@{
            if (js("typeof window != 'object'")) return@outta
            removeEventListeners()
        },

        "pre_componentDidUpdate" to outta@{
            if (js("typeof window != 'object'")) return@outta
            addEventListeners()
        },

        "pre_componentWillMount" to outta@{
            if (js("typeof window != 'object'")) return@outta

            var elementControls = Shitus.elementIDToControls[me.elementID]
            if (elementControls == null) {
                Shitus.elementIDToControls[me.elementID] = jsArrayOf()
                elementControls = Shitus.elementIDToControls[me.elementID]
            }

            if (me.tame != null) {
                for (another: dynamic in jsArrayToListOfDynamic(elementControls)) {
                    if (another.tame != null) Shitus.raise("Control ${me.debugDisplayName} conflicts with ${another.debugDisplayName}, because both are tamed on #${me.elementID}")

//                        raise("Control ${me.debugDisplayName} conflicts with ${another.debugDisplayName}, because both are tamed", json(
//                        "\$render" to {
//                            fun renderControl(co: dynamic): dynamic {
//                                val cshit = jshit.CollapsibleShit(json("content" to Shitus.diva(json(), jshit.renderStacks(jshit.pickStacks(co)))))
//                                return Shitus.diva(json(),
//                                    Shitus.diva(json("style" to json("display" to "flex", "marginRight" to 10)),
//                                        Shitus.diva(json("style" to json("fontWeight" to "bold")), co.debugDisplayName),
//                                        cshit.renderCaret(json("style" to json("marginLeft" to 10)))),
//                                    cshit.renderContent())
//                            }
//
//                            Shitus.diva(json("style" to json("display" to "flex")), renderControl(me), renderControl(another))
//                        }
//                    ))
                }
            }

            elementControls.unshift(me)
        },

        "pre_componentDidMount" to outta@{
            if (js("typeof window != 'object'")) return@outta

            addEventListeners()

            art.uiStateContributions[me.id] = {state: TestStateContributions ->
                me.contributeTestState?.let {cts->
                    var shouldContribute = !me.noStateContributions

                    if (shouldContribute) {
                        Shitus.byid(me.elementID).parents().each {
                            val parentControls = Shitus.elementIDToControls[js("this").id] || jsArrayOf()
                            for (parentControl in jsArrayToListOfDynamic(parentControls)) {
                                if (parentControl.noStateContributions) {
                                    shouldContribute = false
                                    return@each false // break
                                }
                            }
                        }
                    }

                    if (shouldContribute) cts(state)
                }

                for (entry in jsArrayToListOfDynamic(lodash.toPairs(me.tattrs ?: js("({})")))) {
                    val key: dynamic = entry[0]
                    val value: dynamic = entry[1]
                    if (value != null) {
                        state.put(me, me.getTamePath() + "." + key, value)
                    }
                }

                if (me.effectiveShame != null) {
                    val tp = me.getTamePath()
                    if (tp != me.effectiveShame) {
                        state.put(me, tp + ".shame", me.effectiveShame!!)
                    }
                }
            }

            // Determine effective shame
            if (me.shame != null) {
                me.effectiveShame = me.shame
            }
            else if (me.tame != null && hasTestManipulationFunctions()) {
                me.effectiveShame = me.getTamePath()
            }

            val effectiveShame: String? = me.effectiveShame
            effectiveShame?.let {
                if (TestGlobal.shameToControl.containsKey(it)) {
                    me.stickException(json("exception" to Error("There is already a thing shamed ${it}")))
                } else {
                    TestGlobal.shameToControl[it] = me
                }
            }
        },

        "post_componentWillUnmount" to outta@{
            if (js("typeof window != 'object'")) return@outta

            removeEventListeners()
            // @wip perf
            jsFacing_arrayDeleteFirstThat(Shitus.elementIDToControls[me.elementID], {x: dynamic -> x.id == me.id})
            art.uiStateContributions.remove(me.id)

            me.effectiveShame?.let {TestGlobal.shameToControl.remove(it)}

            shit.errorStickerID?.let {DebugPanes.remove(it)}
            shit.errorStickerTether?.destroy()
        }
    ))


    me.stickException = {arg: dynamic ->
        // {exception}
        val exception: dynamic = arg.exception

        val element = Shitus.byid0(me.elementID)
        checkNotNull(element) {"stickException to unrendered element"}

        fun doReveal() {
            revealStack(exception)
        }

        doReveal() // Does nothing if something is already revealed

        val errorStickerID = puid()
        shit.errorStickerID = errorStickerID

        DebugPanes.put(errorStickerID, oldShitAsToReactElementable(React.createElement("div", json(
            "id" to errorStickerID,
            "style" to json(
                "width" to 10,
                "height" to 10,
                "background" to Color.RED_500.toString(),
                "cursor" to "pointer",
                "zIndex" to REALLY_BIG_Z_INDEX),
            "onClick" to {
                Shitus.hideStackRevelation()
                revealStackCalledTimes = 0
                doReveal()
            }
        ))))

        requestAnimationFrame {
            shit.errorStickerTether = Tether(json(
                "element" to byid(errorStickerID),
                "target" to element,
                "attachment" to "top left",
                "targetAttachment" to "top left"
            ))
        }

        art.uiStateContributions["stickedError"] = {state: TestStateContributions ->
            state.put(me, "stickedError", exception.message ?: "Some shit happened")
        }
    }

    me.getTamePath = getTamePath@{
        Shitus.invariant(me.tame, "getTamePath can only be called on tamed control")
//        invariant(me.tame, "getTamePath can only be called on tamed control", json("\$definitionStack" to def.`$definitionStack`))

        var res: dynamic = me.tame
        val parents = Shitus.byid(me.elementID).parents()
        parents.each {
            val parentControls: dynamic = Shitus.elementIDToControls[js("this").id] || jsArrayOf()
            for (parentControl in jsArrayToListOfDynamic(parentControls.slice().reverse())) {
                if (parentControl.tame) {
                    res = parentControl.tame + "." + res
                }
            }
        }

//            res = res.replace(/-\$the$/, '')

        return@getTamePath res
    }

    var testActionHand: dynamic = null

    me.testShowHand = {_arg: dynamic ->
        val arg: dynamic = if (_arg) _arg else js("({})")
        val testActionHandOpts = arg.testActionHandOpts

        testActionHand = art.showTestActionHand(global.Object.assign(json("target" to Shitus.byid(me.elementID)), testActionHandOpts))
    }

    me.testHideHand = {
        testActionHand.delete()
    }

    if (implementTestClick) {
        me.testClick = {_arg: dynamic -> "__async"
            val arg: dynamic = if (_arg) _arg else json()
            val testActionHandOpts = arg.testActionHandOpts

            val stubEvent = json("preventDefault" to Shitus.noop, "stopPropagation" to Shitus.noop)

            if (art.testSpeed == "slow") {
                val testActionHand = art.showTestActionHand(global.Object.assign(json("target" to Shitus.byid(me.elementID)), testActionHandOpts))
                __await<dynamic>(Shitus.delay(global.DEBUG_ACTION_HAND_DELAY))
                testActionHand.delete()
                val shit: ((Any?) -> Promise<Any?>)? = implementTestClick.onClick
                shit?.let {__await(it(stubEvent))}
//                __await<dynamic>(jshit.utils.fova(implementTestClick.onClick, stubEvent))
            } else {
                val shit: ((Any?) -> Promise<Any?>)? = implementTestClick.onClick
                shit?.let {__await(it(stubEvent))}
//                __await<dynamic>(jshit.utils.fova(implementTestClick.onClick, stubEvent))
            }
        }
    }

    if (implementTestKeyDown) {
        me.testKeyDown = {_arg: dynamic -> "__async"
            val arg: dynamic = if (_arg) _arg else json()
            val testActionHandOpts = arg.testActionHandOpts
            val keyCode = arg.keyCode

            val stubEvent = json("preventDefault" to Shitus.noop, "stopPropagation" to Shitus.noop, "keyCode" to keyCode)

            if (art.testSpeed == "slow") {
                val testActionHand = art.showTestActionHand(global.Object.assign(json("target" to Shitus.byid(me.elementID)), testActionHandOpts))
                __await<dynamic>(js("$")(global.DEBUG_ACTION_HAND_DELAY))
                testActionHand.delete()
                val shit: ((Any?) -> Promise<Any?>)? = implementTestKeyDown.onKeyDown
                shit?.let {__await(it(stubEvent))}
//                __await<dynamic>(jshit.utils.fova(implementTestKeyDown.onKeyDown, stubEvent))
            } else {
                val shit: ((Any?) -> Promise<Any?>)? = implementTestKeyDown.onKeyDown
                shit?.let {__await(it(stubEvent))}
//                __await<dynamic>(jshit.utils.fova(implementTestKeyDown.onKeyDown, stubEvent))
            }
        }
    }

    me.captureAction = captureAction@{_def: dynamic ->
        val def = if (_def) _def else js("({})")
        val includeShames: dynamic = def.includeShames
        val excludeShames: dynamic = def.excludeShames
        val todoActionDescription: dynamic = def.todoActionDescription

        hrss.thingsToDoAfterHotUpdate.control_captureAction = control_captureAction@{
            val control = getControlByShame(me.effectiveShame)
            if (!control) return@control_captureAction console.warn("No control shamed [${me.effectiveShame}] to capture action on")
            control.captureAction(def)
        }

        if ((me.testClick ?: me.testSetValue) == null) {
            return@captureAction console.warn("Control ${me.debugDisplayName} doesn’t support any of test manipulation functions")
        }

        val codeLines = jsArrayOf()

        val inputLines = jsArrayOf()
        for (co in TestGlobal.shameToControl.values) {
            if (co.testSetValue) {
                if (!co.testGetValue) {
                    Shitus.raiseWithMeta(json("message" to "co.testSetValue requires co.testGetValue", "meta" to co))
                }

                var shouldCapture = true
                if (includeShames && !includeShames.includes(co.effectiveShame)) shouldCapture = false
                if (excludeShames && excludeShames.includes(co.effectiveShame)) shouldCapture = false
                if (shouldCapture) {

                    inputLines.push("${"s"}{setValue: {shame: '${co.effectiveShame}', value: ${global.nodeUtil.inspect(co.testGetValue(), json("depth" to null))}}},")
                }
            }
        }

        codeLines.push("${"s"}{step: {kind: 'action', long: ${"t"}('${todoActionDescription || "TODO Action description".asDynamic()}')}},")
        codeLines.push.apply(codeLines.push, inputLines)
        val stampUTC = global.moment.tz("UTC").format("YYYY/MM/DD HH:mm:ss")
        val timestampPropCode = ", timestamp: '${stampUTC}'"
        if (me.testSetValue != null && me.setValueIsAction) {
            codeLines.push("${"s"}{setValue: {shame: '${me.effectiveShame}', value: ${global.nodeUtil.inspect(me.testGetValue(), json("depth" to null))}${if (me.setValueIsAction) timestampPropCode else ""}}},")
        }
        if (me.testClick != null) {
            codeLines.push("${"s"}{click: {shame: '${me.effectiveShame}'${timestampPropCode}}},")
        }

        codeLines.push("${"s"}{step: {kind: 'state', long: ${"t"}('TODO State description')}},")
        val expectationWillBeGeneratedShit = true

        if (expectationWillBeGeneratedShit) {
            codeLines.push("${"s"}{assert: {\$tag: '${Shitus.uuid()}', expected: '---generated-shit---'}},")
        } else {
            Shitus.raise("implement this case properly")
            codeLines.push("art.uiState({\$tag: '${Shitus.uuid()}', expected: {")
            codeLines.push()
            codeLines.push("}})")
        }

        codeLines.push()

        run { // Action capture pane
            var thePane: dynamic = null
            thePane = openDebugPane(json("name" to "openActionCapturePane", "height" to 250,
                "content" to Shitus.updatableElement(json(), updatableElementCtor@{update: dynamic ->
                    val code = Shitus.codeLinesToString(json(codeLines, "indent" to 0))

                    val codeArea = Shitus.Input(json(
                        "kind" to "textarea", "rows" to 8, "style" to json("fontFamily" to "monospace"),
                        "initialValue" to code,
                        "onKeyDown" to { e: dynamic ->
                            if (e.keyCode == 27) {
                                thePane.close()
                            }
                        }
                    ))

                    var insertedCodeLink: dynamic = null
                    val progressPlaceholder = Shitus.Placeholder()

//                                function focusAndSelect() {
//                                    requestAnimationFrame(_=> {
//                                        codeArea.focus()
//                                        codeArea.select()
//                                    })
//                                }

                    return@updatableElementCtor render@{
                        Shitus.diva(json("controlTypeName" to "openActionCapturePane", "style" to json("position" to "relative")),
                            Shitus.diva(json("style" to json("height" to "1.5em"))),
                            Shitus.diva(json("className" to "form-group"),
                                Shitus.labela(json(), "Code"),
                                Shitus.diva(json(), codeArea)
                            ),
                            Shitus.diva(json("style" to json("position" to "absolute", "right" to 0, "top" to 10, "display" to "flex")),
                                Shitus.diva(json(), progressPlaceholder),

                                {
                                    if (art.actionPlaceholderTag != null) "No actionPlaceholderTag"
                                    else if (insertedCodeLink) insertedCodeLink
                                    else Shitus.button(json("level" to "primary", "icon" to "pencil", "title" to "Insert Test Action Code", "style" to json(),
                                        "onClick" to {
                                            "__async"
                                            __await<dynamic>(callDebugRPWithProgress(json(
                                                "msg" to json(
                                                    "fun" to "danger_insertTestActionCode",
                                                    "placeholderTag" to art.actionPlaceholderTag,
                                                    "code" to codeArea.getValue()
                                                ),
                                                "progressPlaceholder" to progressPlaceholder,
                                                "progressTitle" to "Inserting test action code"
                                            )))

                                            val m = codeArea.getValue().match(global.RegExp("\\\$tag: \"(.*?)\""))
                                            Shitus.invariant(m && m[1], "Where the fuck is tag in generated code?")
                                            insertedCodeLink = Shitus.diva(json(
                                                "style" to json("marginLeft" to 8)),
                                                OpenSourceCodeLink(json("where" to json("\$tag" to m[1]))))
                                            update()
                                        }
                                    ))
                                }
                            )
                        )
                    }
                },

                    "onClose" to {
                        jsFacing_deleteKey(hrss.thingsToDoAfterHotUpdate, "control_captureAction")
                    }
                )))
        }
    }

}

fun ReactElement.toToReactElementable(): ToReactElementable {
    return object:ToReactElementable {
        override fun toReactElement() = this@toToReactElementable
    }
}

fun spancTitle(title: String): ReactElement =
    Shitus.spancTitle(json("title" to title))

@GenerateSignatureMixes
fun hor(spacing: Int, @Mix attrs: Attrs, @Mix style: Style, block: ((ElementBuilder) -> Unit)? = null) =
    object:ElementBuilder("div", attrs, style.copy(display="flex"), block) {
        override fun wrapChild(index: Int, child: ToReactElementable) =
            kdiv(marginLeft = if (index > 0) spacing else 0){o->
                o- child
            }
    }

@GenerateSignatureMixes
fun hor1(@Mix attrs: Attrs, @Mix style: Style, block: ((ElementBuilder) -> Unit)? = null) =
    hor(4, attrs, style.copy(display="flex"), block)

@GenerateSignatureMixes
fun hor2(@Mix attrs: Attrs, @Mix style: Style, block: ((ElementBuilder) -> Unit)? = null) =
    hor(8, attrs, style.copy(display="flex"), block)

fun pageHeader0(title: String, className: String = "") =
    kdiv(className="page-header $className", marginTop=0, marginBottom=15){o->
        o- h3(tame="pageHeader", marginBottom=0){o->
            o- spancTitle(title)
        }
    }

open class Placeholder(private var content: ToReactElementable = kspan_nolint()) : Control2(Attrs()) {
    lateinit var prevContent: ToReactElementable

    fun setContent(newContent: ToReactElementable) {
        prevContent = content
        content = newContent
        update()
    }

    fun setPrevContent() {
        setContent(prevContent)
    }

    override fun defaultControlTypeName() = "Placeholder"
    override fun render() = content
}























