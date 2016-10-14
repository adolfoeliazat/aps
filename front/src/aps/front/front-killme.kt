/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

fun jsFacing_makeFormCtor(ui: dynamic): dynamic {
    fun jsFacing_Form(spec: dynamic): dynamic {
        val primaryButtonTitle = spec.primaryButtonTitle; val cancelButtonTitle = spec.cancelButtonTitle; val autoFocus = spec.autoFocus;
        val fields = spec.fields; val rpcFun = spec.rpcFun; val onSuccess = spec.onSuccess; val onError = spec.onError;
        val className = spec.className; val dontShameButtons = spec.dontShameButtons; val errorBannerStyle = spec.errorBannerStyle;
        val debugName = spec.debugName; val getInvisibleFieldNames = spec.getInvisibleFieldNames; val onCancel = spec.onCancel;

        fun formTicker(): dynamic {
            return jshit.elcl(json(
                "render" to {
                    jshit.diva(json("className" to "progressTicker", "style" to json("float" to "right", "width" to 14, "height" to 28, "backgroundColor" to Color.BLUE_GRAY_600)))
                },
                "componentDidMount" to {
                    global.testGlobal["shitSpins"] = true
                },
                "componentWillUnmount" to {
                    global.testGlobal["shitSpins"] = false
                }
            ))
        }

        return jshit.statefulElement(json("ctor" to statefulElementCtor@{update: dynamic ->
            var working: dynamic = undefined
            var error: dynamic = undefined
            var focusedField: dynamic = undefined
            var actualVisibleFieldNames: dynamic = undefined

            fun figureOutActualVisibleFieldNames() {
                actualVisibleFieldNames = fields.map({x -> x.getName()})
                if (getInvisibleFieldNames) {
                    actualVisibleFieldNames = jshit.utils.without.apply(null, js("[]").concat(jsArrayOf(actualVisibleFieldNames), getInvisibleFieldNames()))
                }
            }

            val me = json(
                "getField" to outta@{name: dynamic ->
                    for (field in jsArrayToList(fields)) {
                        if (field.getName() == name) return@outta field
                    }
                    jshit.utils.raise("No fucking field [${name}] in the form")
                },

                "fieldChanged" to {
                    // @wip rejection
                    if (getInvisibleFieldNames) {
                        val oldVisible = jshit.utils.clone(actualVisibleFieldNames)
                        figureOutActualVisibleFieldNames()
                        if (!jshit.utils.isEqual(oldVisible, actualVisibleFieldNames)) {
                            update()
                        }
                    }
                },

                "fieldFocused" to {arg: dynamic ->
                    val field = arg.field
                    focusedField = field
                },

                "fieldBlurred" to {arg: dynamic ->
                    val field = arg.field
                    focusedField = undefined
                },

                "render" to outta@{
                    figureOutActualVisibleFieldNames()

                    return@outta jshit.diva(json(),
                        jshit.forma.apply(null, js("[]").concat(
                            jsArrayOf(
                                json("className" to className),
                                error && jshit.errorBanner(json("content" to error, "style" to errorBannerStyle))),
                            fields
                                .filter({x: dynamic -> actualVisibleFieldNames.includes(x.getName())})
                                .map({x: dynamic -> x.render(json())}),

                            jshit.diva(json("style" to json("textAlign" to "left")),
                                jshit.button(json("tamy" to "primary", "shamy" to if (dontShameButtons) undefined else "primary",
                                    "level" to "primary", "title" to primaryButtonTitle, "disabled" to working,
                                    "onClick" to {"__async"
                                        jshit.beginTrain(json("name" to "Submit fucking${if (debugName) " " + debugName else ""} form")); try {
                                            for (field in jsArrayToList(fields)) {
                                                field.setError(undefined)
                                                field.setDisabled(true)
                                            }
                                            error = undefined
                                            working = true
                                            update()

                                            val reb = jshit.RequestBuilder(json())
                                            reb.set(json("key" to "fun", "value" to rpcFun))
                                            for (field in jsArrayToList(fields.filter({ x: dynamic -> actualVisibleFieldNames.includes(x.getName())}))) {
                                                field.contributeToRequest(json("reb" to reb))
                                            }
                                            val res = __await<dynamic>(ui.rpcSoft(reb.toMessage(json())))

                                            if (res.error) {
                                                error = res.error
                                                __await<dynamic>(jshit.utils.fova(onError, res))
                                            } else {
                                                error = undefined
                                                __await<dynamic>(onSuccess(res))
                                            }

                                            working = false
                                            for (field in jsArrayToList(fields)) {
                                                field.setError(res.fieldErrors && res.fieldErrors[field.getName()])
                                                field.setDisabled(false)
                                            }
                                            update()
                                        } finally { jshit.endTrain() }
                                    })),
                                cancelButtonTitle && jshit.button(json("tamy" to "cancel", "shamy" to if (dontShameButtons) undefined else "cancel",
                                    "title" to cancelButtonTitle, "disabled" to working, "style" to json("marginLeft" to 10), "onClick" to onCancel)),
                                working && formTicker())
                        )))
                },

                "componentDidMount" to {
                    if (focusedField) {
                        focusedField.focus()
                    }
                },

                "componentDidUpdate" to {
                    if (focusedField) {
                        focusedField.focus()
                    }
                }
            )

            for (field in jsArrayToList(fields)) {
                field.form = me
            }

            return@statefulElementCtor me
        }))
    }

    return ::jsFacing_Form
}

fun orig_jsFacing_makeFormCtor(ui: dynamic): dynamic {
    fun jsFacing_Form(spec: dynamic): dynamic {
        val primaryButtonTitle = spec.primaryButtonTitle; val cancelButtonTitle = spec.cancelButtonTitle; val autoFocus = spec.autoFocus;
        val fields = spec.fields; val rpcFun = spec.rpcFun; val onSuccess = spec.onSuccess; val onError = spec.onError;
        val className = spec.className; val dontShameButtons = spec.dontShameButtons; val errorBannerStyle = spec.errorBannerStyle;
        val debugName = spec.debugName; val getInvisibleFieldNames = spec.getInvisibleFieldNames; val onCancel = spec.onCancel;

        fun formTicker(): dynamic {
            return jshit.elcl(json(
                "render" to {
                    jshit.diva(json("className" to "progressTicker", "style" to json("float" to "right", "width" to 14, "height" to 28, "backgroundColor" to Color.BLUE_GRAY_600)))
                },
                "componentDidMount" to {
                    global.testGlobal["shitSpins"] = true
                },
                "componentWillUnmount" to {
                    global.testGlobal["shitSpins"] = false
                }
            ))
        }

        return jshit.statefulElement(json("ctor" to statefulElementCtor@{update: dynamic ->
            var working: dynamic = undefined
            var error: dynamic = undefined
            var focusedField: dynamic = undefined
            var actualVisibleFieldNames: dynamic = undefined

            fun figureOutActualVisibleFieldNames() {
                actualVisibleFieldNames = fields.map({x -> x.getName()})
                if (getInvisibleFieldNames) {
                    actualVisibleFieldNames = jshit.utils.without.apply(null, js("[]").concat(jsArrayOf(actualVisibleFieldNames), getInvisibleFieldNames()))
                }
            }

            val me = json(
                "getField" to outta@{name: dynamic ->
                    for (field in jsArrayToList(fields)) {
                        if (field.getName() == name) return@outta field
                    }
                    jshit.utils.raise("No fucking field [${name}] in the form")
                },

                "fieldChanged" to {
                    // @wip rejection
                    if (getInvisibleFieldNames) {
                        val oldVisible = jshit.utils.clone(actualVisibleFieldNames)
                        figureOutActualVisibleFieldNames()
                        if (!jshit.utils.isEqual(oldVisible, actualVisibleFieldNames)) {
                            update()
                        }
                    }
                },

                "fieldFocused" to {arg: dynamic ->
                    val field = arg.field
                    focusedField = field
                },

                "fieldBlurred" to {arg: dynamic ->
                    val field = arg.field
                    focusedField = undefined
                },

                "render" to outta@{
                    figureOutActualVisibleFieldNames()

                    return@outta jshit.diva(json(),
                        jshit.forma.apply(null, js("[]").concat(
                            jsArrayOf(
                                json("className" to className),
                                error && jshit.errorBanner(json("content" to error, "style" to errorBannerStyle))),
                            fields
                                .filter({x: dynamic -> actualVisibleFieldNames.includes(x.getName())})
                                .map({x: dynamic -> x.render(json())}),

                            jshit.diva(json("style" to json("textAlign" to "left")),
                                jshit.button(json("tamy" to "primary", "shamy" to if (dontShameButtons) undefined else "primary",
                                    "level" to "primary", "title" to primaryButtonTitle, "disabled" to working,
                                    "onClick" to {"__async"
                                        jshit.beginTrain(json("name" to "Submit fucking${if (debugName) " " + debugName else ""} form")); try {
                                            for (field in jsArrayToList(fields)) {
                                                field.setError(undefined)
                                                field.setDisabled(true)
                                            }
                                            error = undefined
                                            working = true
                                            update()

                                            val reb = jshit.RequestBuilder(json())
                                            reb.set(json("key" to "fun", "value" to rpcFun))
                                            for (field in jsArrayToList(fields.filter({ x: dynamic -> actualVisibleFieldNames.includes(x.getName())}))) {
                                                field.contributeToRequest(json("reb" to reb))
                                            }
                                            val res = __await<dynamic>(ui.rpcSoft(reb.toMessage(json())))

                                            if (res.error) {
                                                error = res.error
                                                __await<dynamic>(jshit.utils.fova(onError, res))
                                            } else {
                                                error = undefined
                                                __await<dynamic>(onSuccess(res))
                                            }

                                            working = false
                                            for (field in jsArrayToList(fields)) {
                                                field.setError(res.fieldErrors && res.fieldErrors[field.getName()])
                                                field.setDisabled(false)
                                            }
                                            update()
                                        } finally { jshit.endTrain() }
                                    })),
                                cancelButtonTitle && jshit.button(json("tamy" to "cancel", "shamy" to if (dontShameButtons) undefined else "cancel",
                                    "title" to cancelButtonTitle, "disabled" to working, "style" to json("marginLeft" to 10), "onClick" to onCancel)),
                                working && formTicker())
                        )))
                },

                "componentDidMount" to {
                    if (focusedField) {
                        focusedField.focus()
                    }
                },

                "componentDidUpdate" to {
                    if (focusedField) {
                        focusedField.focus()
                    }
                }
            )

            for (field in jsArrayToList(fields)) {
                field.form = me
            }

            return@statefulElementCtor me
        }))
    }

    return ::jsFacing_Form
}

fun legacy_implementControlShit(arg: dynamic) {
    val me: dynamic = arg.me
    val def: dynamic = arg.def
    val implementTestClick: dynamic = arg.implementTestClick
    val implementTestKeyDown: dynamic = arg.implementTestKeyDown

    if (def.controlTypeName || def.ctn) me.controlTypeName = def.controlTypeName || def.ctn
    invariant(me.controlTypeName, "I want controlTypeName")
    invariant(!(me.tame && me.tamy), "I want either tame or tamy")

    me.`$definitionStack` = def.`$definitionStack`
    val `$definitionStack` = me.`$definitionStack`

    me.id = puid()
    if (!me.elementID) me.elementID = def.id || def.elementID || puid().asDynamic()
    if (!me.`$tag`) me.`$tag` = def.`$tag`
    if (!me.`$sourceLocation`) me.`$sourceLocation` = def.`$sourceLocation`
    if (!me.`$callStack`) me.`$callStack` = def.`$callStack`

//        if (def.shameIsTamePath != undefined) me.shameIsTamePath = true

    if (def.tamyShamy) {
        invariant(!(def.tamy || def.shamy), "tamyShamy is incompatible with tamy or shamy")
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
    if (me.tattrs) invariant(me.tame, "Control with tattrs should be tamed")

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
        jshit.byid(me.elementID).off() // Several controls can be on same element, and we don't want to handle click several times
        jshit.byid(me.elementID).on("click", onClick@{e: dynamic -> "__async"
            if (MODE == "debug" && e.ctrlKey) {
                if (e.shiftKey) {
                    if (me.ignoreDebugCtrlShiftClick) return@onClick Unit

                    preventAndStop(e)

                    if (!me.effectiveShame) {
                        jshit.raiseWithMeta(json("message" to "Put some shame on me", "meta" to me))
                    }

                    return@onClick me.captureAction()
                }

                preventAndStop(e)
                return@onClick jshit.revealControl(me)
            }

            __await<dynamic>(jshit.utils.fova(me.onRootClick, e))
        })
    }

    fun removeEventListeners() {
        jshit.byid(me.elementID).off()
    }

    var came: dynamic = null
        val mutatorFunctionNames = jsArrayOf("setValue", "click")

    fun hasTestManipulationFunctions(): dynamic {
        return me.testClick || me.testSetValue
    }

    jshit.decorate(json("target" to me,
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

            var elementControls = jshit.elementIDToControls[me.elementID]
            if (elementControls == null) {
                jshit.elementIDToControls[me.elementID] = jsArrayOf()
                elementControls = jshit.elementIDToControls[me.elementID]
            }

            if (me.tame) {
                for (another: dynamic in jsArrayToList(elementControls)) {
                    if (another.tame) raise("Control ${me.debugDisplayName} conflicts with ${another.debugDisplayName}, because both are tamed", json(
                        "\$render" to {
                            fun renderControl(co: dynamic): dynamic {
                                val cshit = jshit.CollapsibleShit(json("content" to jshit.diva(json(), jshit.renderStacks(jshit.pickStacks(co)))))
                                return jshit.diva(json(),
                                    jshit.diva(json("style" to json("display" to "flex", "marginRight" to 10)),
                                        jshit.diva(json("style" to json("fontWeight" to "bold")), co.debugDisplayName),
                                        cshit.renderCaret(json("style" to json("marginLeft" to 10)))),
                                    cshit.renderContent())
                            }

                            jshit.diva(json("style" to json("display" to "flex")), renderControl(me), renderControl(another))
                        }
                    ))
                }
            }

            elementControls.unshift(me)
        },

        "pre_componentDidMount" to outta@{
            if (js("typeof window != 'object'")) return@outta

            addEventListeners()

            jshit.art.uiStateContributions[me.id] = {state: dynamic ->
                if (me.contributeTestState) {
                    var shouldContribute = !me.noStateContributions

                    if (shouldContribute) {
                        jshit.byid(me.elementID).parents().each {
                            val parentControls = jshit.elementIDToControls[js("this").id] || jsArrayOf()
                            for (parentControl in jsArrayToList(parentControls)) {
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

                for (entry in jsArrayToList(jshit.utils.toPairs(me.tattrs || js("({})")))) {
                    val key: dynamic = entry[0]
                    val value: dynamic = entry[1]
                    if (value != null) {
                        state.put(json("control" to me, "key" to me.getTamePath() + "." + key, "value" to value))
                    }
                }

                if (me.effectiveShame) {
                    val tp = me.getTamePath()
                    if (tp != me.effectiveShame) {
                        state.put(json("control" to me, "key" to tp + ".shame", "value" to me.effectiveShame))
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

            if (me.effectiveShame) {
                if (jshit.utils.keys(global.testGlobal.controls).includes(me.effectiveShame)) {
                    me.stickException(json("exception" to Error("testGlobal.controls already contains thing shamed ${me.effectiveShame}")))
                }
                global.testGlobal.controls[me.effectiveShame] = me
            }
        },

        "post_componentWillUnmount" to outta@{
            if (js("typeof window != 'object'")) return@outta

            removeEventListeners()
            // @wip perf
            jshit.utils.arrayDeleteFirstThat(jshit.elementIDToControls[me.elementID], {x: dynamic -> x.id == me.id})
            jshit.utils.deleteKey(jshit.art.uiStateContributions, me.id)

            if (me.effectiveShame) {
                jshit.utils.deleteKey(global.testGlobal.controls, me.effectiveShame)
            }
        }
    ))


    me.stickException = {arg: dynamic ->
        // {exception}
        val exception: dynamic = arg.exception

        fun doReveal() {
            jshit.revealStack(json("exception" to global.Object.assign(exception, json("\$render" to {
                jshit.renderDefinitionStackStrip(json("stack" to me.`$definitionStack`))
            }))))
        }

        doReveal() // Does nothing if something is already revealed

        jshit.debugControlStickers.add(json("control" to me, "shit" to json(
            "onClick" to {
                jshit.hideStackRevelation()
                doReveal()
            }
        )))
    }

    me.getTamePath = getTamePath@{
        invariant(me.tame, "getTamePath can only be called on tamed control", json("\$definitionStack" to def.`$definitionStack`))

        var res: dynamic = me.tame
        val parents = jshit.byid(me.elementID).parents()
        parents.each {
            val parentControls: dynamic = jshit.elementIDToControls[js("this").id] || jsArrayOf()
            for (parentControl in jsArrayToList(parentControls.slice().reverse())) {
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

        testActionHand = jshit.showTestActionHand(global.Object.assign(json("target" to jshit.byid(me.elementID)), testActionHandOpts))
    }

    me.testHideHand = {
        testActionHand.delete()
    }

    if (implementTestClick) {
        me.testClick = {_arg: dynamic -> "__async"
            val arg: dynamic = if (_arg) _arg else json()
            val testActionHandOpts = arg.testActionHandOpts

            val stubEvent = json("preventDefault" to jshit.utils.noop, "stopPropagation" to jshit.utils.noop)

            if (jshit.testSpeed == "slow") {
                val testActionHand = jshit.showTestActionHand(global.Object.assign(json("target" to jshit.byid(me.elementID)), testActionHandOpts))
                __await<dynamic>(jshit.delay(global.DEBUG_ACTION_HAND_DELAY))
                testActionHand.delete()
                __await<dynamic>(jshit.utils.fova(implementTestClick.onClick, stubEvent))
            } else {
                __await<dynamic>(jshit.utils.fova(implementTestClick.onClick, stubEvent))
            }
        }
    }

    if (implementTestKeyDown) {
        me.testKeyDown = {_arg: dynamic -> "__async"
            val arg: dynamic = if (_arg) _arg else json()
            val testActionHandOpts = arg.testActionHandOpts
            val keyCode = arg.keyCode

            val stubEvent = json("preventDefault" to jshit.utils.noop, "stopPropagation" to jshit.utils.noop, "keyCode" to keyCode)

            if (jshit.testSpeed == "slow") {
                val testActionHand = jshit.showTestActionHand(global.Object.assign(json("target" to jshit.byid(me.elementID)), testActionHandOpts))
                __await<dynamic>(jshit.utils.delay(global.DEBUG_ACTION_HAND_DELAY))
                testActionHand.delete()
                __await<dynamic>(jshit.utils.fova(implementTestKeyDown.onKeyDown, stubEvent))
            } else {
                __await<dynamic>(jshit.utils.fova(implementTestKeyDown.onKeyDown, stubEvent))
            }
        }
    }

    me.captureAction = captureAction@{_def: dynamic ->
        val def = if (_def) _def else js("({})")
        val includeShames: dynamic = def.includeShames
        val excludeShames: dynamic = def.excludeShames
        val todoActionDescription: dynamic = def.todoActionDescription

        jshit.thingsToDoAfterHotUpdate.control_captureAction = control_captureAction@{
            val control = jshit.getControlByShame(me.effectiveShame)
            if (!control) return@control_captureAction console.warn("No control shamed [${me.effectiveShame}] to capture action on")
            control.captureAction(def)
        }

        if (!(me.testClick || me.testSetValue)) {
            return@captureAction console.warn("Control ${me.debugDisplayName} doesn’t support any of test manipulation functions")
        }

        val codeLines = jsArrayOf()

        val inputLines = jsArrayOf()
        for (co: dynamic in jsArrayToList(jshit.utils.values(global.testGlobal.controls))) {
            if (co.testSetValue) {
                if (!co.testGetValue) {
                    jshit.raiseWithMeta(json("message" to "co.testSetValue requires co.testGetValue", "meta" to co))
                }

                var shouldCapture = true
                if (includeShames && !includeShames.includes(co.effectiveShame)) shouldCapture = false
                if (excludeShames && excludeShames.includes(co.effectiveShame)) shouldCapture = false
                if (shouldCapture) {
                    inputLines.push("${"s"}{setValue: {shame: '${co.effectiveShame}', value: ${jshit.utils.toLiteralCode(co.testGetValue())}}},")
                }
            }
        }

        codeLines.push("${"s"}{step: {kind: 'action', long: ${"t"}('${todoActionDescription || "TODO Action description".asDynamic()}')}},")
        codeLines.push.apply(codeLines.push, inputLines)
        val stampUTC = jshit.utils.moment.tz("UTC").format("YYYY/MM/DD HH:mm:ss")
        val timestampPropCode = ", timestamp: '${stampUTC}'"
        if (me.testSetValue && me.setValueIsAction) {
            codeLines.push("${"s"}{setValue: {shame: '${me.effectiveShame}', value: ${jshit.utils.toLiteralCode(me.testGetValue())}${if (me.setValueIsAction) timestampPropCode else ""}}},")
        }
        if (me.testClick) {
            codeLines.push("${"s"}{click: {shame: '${me.effectiveShame}'${timestampPropCode}}},")
        }

        codeLines.push("${"s"}{step: {kind: 'state', long: ${"t"}('TODO State description')}},")
        val expectationWillBeGeneratedShit = true

        if (expectationWillBeGeneratedShit) {
            codeLines.push("${"s"}{assert: {\$tag: '${jshit.utils.uuid()}', expected: '---generated-shit---'}},")
        } else {
            raise("implement this case properly")
            codeLines.push("art.uiState({\$tag: '${jshit.utils.uuid()}', expected: {")
            codeLines.push()
            codeLines.push("}})")
        }

        codeLines.push()

        run { // Action capture pane
            var thePane: dynamic = null
            thePane = jshit.openDebugPane(json("name" to "openActionCapturePane", "height" to 250,
                "content" to jshit.updatableElement(json(), updatableElementCtor@{update: dynamic ->
                    val code = jshit.utils.codeLinesToString(json(codeLines, "indent" to 0))

                    val codeArea = jshit.Input(json(
                        "kind" to "textarea", "rows" to 8, "style" to json("fontFamily" to "monospace"),
                        "initialValue" to code,
                        "onKeyDown" to { e: dynamic ->
                            if (e.keyCode == 27) {
                                thePane.close()
                            }
                        }
                    ))

                    var insertedCodeLink: dynamic = null
                    val progressPlaceholder = jshit.Placeholder()

//                                function focusAndSelect() {
//                                    requestAnimationFrame(_=> {
//                                        codeArea.focus()
//                                        codeArea.select()
//                                    })
//                                }

                    return@updatableElementCtor render@{
                        jshit.diva(json("controlTypeName" to "openActionCapturePane", "style" to json("position" to "relative")),
                            jshit.diva(json("style" to json("height" to "1.5em"))),
                            jshit.diva(json("className" to "form-group"),
                                jshit.labela(json(), "Code"),
                                jshit.diva(json(), codeArea)
                            ),
                            jshit.diva(json("style" to json("position" to "absolute", "right" to 0, "top" to 10, "display" to "flex")),
                                jshit.diva(json(), progressPlaceholder),

                                {
                                    if (art.actionPlaceholderTag != null) t("No actionPlaceholderTag")
                                    else if (insertedCodeLink) insertedCodeLink
                                    else jshit.button(json("level" to "primary", "icon" to "pencil", "title" to "Insert Test Action Code", "style" to json(),
                                        "onClick" to {
                                            "__async"
                                            __await<dynamic>(jshit.callDebugRPWithProgress(json(
                                                "msg" to json(
                                                    "fun" to "danger_insertTestActionCode",
                                                    "placeholderTag" to art.actionPlaceholderTag,
                                                    "code" to codeArea.getValue()
                                                ),
                                                "progressPlaceholder" to progressPlaceholder,
                                                "progressTitle" to "Inserting test action code"
                                            )))

                                            val m = codeArea.getValue().match(global.RegExp("\\\$tag: \"(.*?)\""))
                                            invariant(m && m[1], "Where the fuck is tag in generated code?")
                                            insertedCodeLink = jshit.diva(json(
                                                "style" to json("marginLeft" to 8)),
                                                jshit.OpenSourceCodeLink(json("where" to json("\$tag" to m[1]))))
                                            update()
                                        }
                                    ))
                                }
                            )
                        )
                    }
                },

                "onClose" to {
                    jshit.utils.deleteKey(jshit.thingsToDoAfterHotUpdate, "control_captureAction")
                }
            )))
        }
    }

}


fun jsFacing_horiza(vararg ignored: dynamic): dynamic {
    val arg = js("arguments[0]")
    val items = js("Array.prototype.slice.call(arguments, 1)")

    val itemStyle = arg.itemStyle
    jshit.utils.deleteKey(arg, "itemStyle")
    val spacing = if (arg.spacing == null) 10 else arg.spacing
    jshit.utils.deleteKey(arg, "spacing")


    return jshit.diva(global.Object.assign(arg, json(
        "items" to jshit.utils.fcomapo(items,
            {v: dynamic, k: dynamic, i: dynamic ->
                jshit.diva(json("style" to global.Object.assign(
                    json("verticalAlign" to "middle"),
                    itemStyle,
                    json("display" to "inline-block", "marginLeft" to if (i > 0) spacing else 0))),
                v)
    }))))
}




















