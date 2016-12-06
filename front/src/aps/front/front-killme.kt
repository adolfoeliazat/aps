/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.*
import org.w3c.dom.events.KeyboardEvent

@native class Tether(opts: Json) {
    fun destroy(): Unit = noImpl
}

fun legacy_implementControlShit(arg: dynamic) {
    class Shit {
        var errorStickerID: String? = null
        var errorStickerTether: Tether? = null
    }
    val shit = Shit()

    val me: dynamic = arg.me
    val def: dynamic = arg.def
    val implementTestClick: dynamic = arg.implementTestClick
    val implementTestKeyDown: dynamic = arg.implementTestKeyDown

    if (def.controlTypeName || def.ctn) me.controlTypeName = def.controlTypeName || def.ctn
    Shitus.invariant(me.controlTypeName, "I want controlTypeName")
    Shitus.invariant(!(me.tame && me.tamy), "I want either tame or tamy")

    me.`$definitionStack` = def.`$definitionStack`
    val `$definitionStack` = me.`$definitionStack`

    me.id = puid()
    if (!me.elementID) me.elementID = def.id || def.elementID || puid().asDynamic()
    if (!me.`$tag`) me.`$tag` = def.`$tag`
    if (!me.`$sourceLocation`) me.`$sourceLocation` = def.`$sourceLocation`
    if (!me.`$callStack`) me.`$callStack` = def.`$callStack`

//        if (def.shameIsTamePath != undefined) me.shameIsTamePath = true

    if (def.tamyShamy) {
        Shitus.invariant(!(def.tamy || def.shamy), "tamyShamy is incompatible with tamy or shamy")
        def.shamy = def.tamyShamy
        def.tamy = def.shamy
    }

    if (!me.shame) {
        if (def.shamy) {
            if (!me.shamyPrefix) me.shamyPrefix = me.tamyPrefix || me.controlTypeName
            me.shame = me.shamyPrefix + "-" + def.shamy
        } else {
            me.shame = def.shame
        }
    }

    if (!me.tame) {
        if (def.tamy == true) me.tame = me.tamyPrefix || me.controlTypeName
        else if (def.tamy) me.tame = (me.tamyPrefix || me.controlTypeName).asDynamic() + "-" + def.tamy
        else me.tame = def.tame
    }
    if (!me.tattrs) me.tattrs = def.tattrs
    if (me.tattrs) Shitus.invariant(me.tame, "Control with tattrs should be tamed")

    if (me.tame && me.controlTypeName && me.tame != me.controlTypeName) me.debugDisplayName = "${me.tame}"
    else if (def.tame) me.debugDisplayName = def.tame
    else if (me.controlTypeName) me.debugDisplayName = me.controlTypeName
    else me.debugDisplayName = "dunno"

    if (me.noStateContributions == undefined) me.noStateContributions = def.noStateContributions

    if (!me.getLongRevelationTitle) {
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

                    if (!me.effectiveShame) {
                        Shitus.raiseWithMeta(json("message" to "Put some shame on me", "meta" to me))
                    }

                    return@onClick me.captureAction()
                }

                preventAndStop(e)
                die("This code path should be deleted")
//                return@onClick Shitus.revealControl(me)
            }

            val shit: ((Any?) -> Promise<Any?>)? = me.onRootClick
//            shit?.let {__await(it(e))}
            shit?.let {it(e)}

//            __await<dynamic>(jshit.utils.fova(me.onRootClick, e))
        })
    }

    fun removeEventListeners() {
        Shitus.byid(me.elementID).off()
    }

    var came: dynamic = null
        val mutatorFunctionNames = jsArrayOf("setValue", "click")

    fun hasTestManipulationFunctions(): dynamic {
        return me.testClick || me.testSetValue
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

            if (me.tame) {
                for (another: dynamic in jsArrayToListOfDynamic(elementControls)) {
                    if (another.tame) Shitus.raise("Control ${me.debugDisplayName} conflicts with ${another.debugDisplayName}, because both are tamed")

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
                if (me.contributeTestState) {
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

                    if (shouldContribute) {
                        me.contributeTestState(state)
                    }
                }

                for (entry in jsArrayToListOfDynamic(lodash.toPairs(me.tattrs || js("({})")))) {
                    val key: dynamic = entry[0]
                    val value: dynamic = entry[1]
                    if (value != null) {
                        state.put(me, me.getTamePath() + "." + key, value)
                    }
                }

                if (me.effectiveShame) {
                    val tp = me.getTamePath()
                    if (tp != me.effectiveShame) {
                        state.put(me, tp + ".shame", me.effectiveShame)
                    }
                }
            }

            // Determine effective shame
            if (me.shame) {
                me.effectiveShame = me.shame
            }
            else if (me.tame && hasTestManipulationFunctions()) {
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

            if (me.effectiveShame) {
                TestGlobal.shameToControl.remove(me.effectiveShame)
            }

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

        art.uiStateContributions["_stickedError"] = {state: TestStateContributions ->
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
        me.testClick = fun(_arg: dynamic): Promise<Unit> = async {
            val arg: dynamic = if (_arg) _arg else json()
            val testActionHandOpts = arg.testActionHandOpts

            val stubEvent = json("preventDefault" to Shitus.noop, "stopPropagation" to Shitus.noop)

            if (art.testSpeed == "slow") {
                val testActionHand = art.showTestActionHand(global.Object.assign(json("target" to Shitus.byid(me.elementID)), testActionHandOpts))
                await<dynamic>(Shitus.delay(global.DEBUG_ACTION_HAND_DELAY))
                testActionHand.delete()
                val shit: ((Any?) -> Promise<Any?>)? = implementTestClick.onClick
                shit?.let {await(it(stubEvent))}
//                await<dynamic>(jshit.utils.fova(implementTestClick.onClick, stubEvent))
            } else {
                val shit: ((Any?) -> Promise<Any?>)? = implementTestClick.onClick
                shit?.let {awaitJSShit<Unit>(it(stubEvent))}
//                await<dynamic>(jshit.utils.fova(implementTestClick.onClick, stubEvent))
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

        if (!(me.testClick || me.testSetValue)) {
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
        if (me.testSetValue && me.setValueIsAction) {
            codeLines.push("${"s"}{setValue: {shame: '${me.effectiveShame}', value: ${global.nodeUtil.inspect(me.testGetValue(), json("depth" to null))}${if (me.setValueIsAction) timestampPropCode else ""}}},")
        }
        if (me.testClick) {
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


fun jsFacing_horiza(vararg ignored: dynamic): dynamic {
    val arg = js("arguments[0]")
    val items = js("Array.prototype.slice.call(arguments, 1)")

    val itemStyle = arg.itemStyle
    jsFacing_deleteKey(arg, "itemStyle")
    val spacing = if (arg.spacing == null) 10 else arg.spacing
    jsFacing_deleteKey(arg, "spacing")


    return Shitus.diva(global.Object.assign(arg, json(
        "items" to Shitus.fcomapo(items,
            {v: dynamic, k: dynamic, i: dynamic ->
                Shitus.diva(json("style" to global.Object.assign(
                    json("verticalAlign" to "middle"),
                    itemStyle,
                    json("display" to "inline-block", "marginLeft" to if (i > 0) spacing else 0))),
                v)
    }))))
}



















