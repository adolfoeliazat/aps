/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.*

fun button(build: ButtonBuilder.() -> Unit): Control {
    val cis = ButtonBuilder()
    cis.build()

    return object : Control(cis) {
        override fun defaultControlTypeName() = "button"

        override fun render(): ReactElement {
            val glyphAttrs = js("({})")
            cis.iconColor?.let { global.Object.assign(glyphAttrs, json("style" to json("color" to it))) }

            return React.createElement("button", json(
                "id" to elementID,
                "className" to "btn btn-${cis.level} ${cis.className}",
                "style" to cis.style.toJSObject(),
                "title" to cis.hint),

                if (cis.icon != null) Shitus.glyph(cis.icon, glyphAttrs) else null,
                if (cis.icon != null && cis.title != null) Shitus.spana(js("({})"), nbsp) else null,
                Shitus.spana(js("({})"), cis.title)
            )
        }

        override fun contributeTestState(state: dynamic) {
            if (cis.title != null) state.put(json("control" to this, "key" to "$effectiveTame.title", "value" to cis.title))
            if (cis.icon != null) state.put(json("control" to this, "key" to "$effectiveTame.icon", "value" to cis.icon))
        }
    }
}

val NIL_AsyncReactEventHandler: (ReactEvent) -> Promise<Unit> = js("({})")

inline fun y(areh: (ReactEvent) -> Promise<Unit>) = areh !== NIL_AsyncReactEventHandler


inline fun ifs(cond: String, then: () -> String): String = if (cond != "") then() else ""

inline fun y(x: String) = x != ""
inline fun n(x: String) = x == ""

fun anyControlDefinitionStackString(control: dynamic, sep: String): Promise<String> {"__async"
    return __asyncResult(
        if (control is StatefulElement)
            __await(control.definitionStackString(sep))
        else if (control is Control)
            __await(control.definitionStackString(sep))
        else
            "Some legacy shit: " + control.`$definitionStack`.map { x -> x.loc }.join(sep)
    )
}


open class CommonControlInstanceShit: ControlInstanceSpec() {
    var className: String = ""
    val style = StyleBuilder()
    var styleKludge: dynamic = undefined

    val allAttrs: dynamic get() {
        val res = js("({})")
        res.style = if (styleKludge != undefined) styleKludge else style.toJSObject()
        res.className = className
        if (y(onClick)) res.onClick = onClick

        // console.log("allAttrs", res)
        return res
    }

}

class ButtonBuilder : CommonControlInstanceShit() {
    var title: String? = null
    var hint: String? = null
    var level: String = "default"
    var icon: String? = null
    var iconColor: Color? = null
}

fun spanc(tame: String, content: String, build: CommonControlInstanceShit.() -> Unit = {}): Control {
    val cis = CommonControlInstanceShit()
    cis.tame = tame
    cis.build()

    return object : Control(cis) {
        override fun defaultControlTypeName() = "spanc"

        override fun render(): ReactElement {
            return Shitus.spana(json("id" to elementID, "className" to cis.className, "style" to cis.style.toJSObject()), content)
        }

        override fun contributeTestState(state: dynamic) {
            state.put(json("control" to this, "key" to effectiveTame, "value" to content))
        }
    }
}

val diva = makeBasicContainerControlCtor("div")
val spana = makeBasicContainerControlCtor("span")
val forma = makeBasicContainerControlCtor("form")
val h3a = makeBasicContainerControlCtor("h3")

val NORE_killme: ReactElement = js("({})")

open class BasicContainerControlBuilder: CommonControlInstanceShit() {
    open fun transformChildBeforeAddition(child: ReactElement) = child

    val children = mutableListOf<ReactElement>()

    fun add(child: ReactElement) {
        if (child !== NORE_killme) {
            val child2 = transformChildBeforeAddition(child)
            if (child2 !== NORE_killme)
                children.add(child2)
        }
    }

    operator fun ReactElement.unaryMinus() {
        add(this)
    }

    fun add(child: ToReactElementable) {
        add(child.toReactElement())
    }

    operator fun ToReactElementable.unaryMinus() {
        add(this)
    }

    fun add(s: String) {
        add(asReactElement(s))
    }

    operator fun String.unaryMinus() {
        add(this)
    }

    operator fun Iterable<ReactElement>.unaryPlus() {
        for (child in this) add(child)
    }

    operator fun Iterable<ToReactElementable>.unaryPlus() {
        for (child in this) add(child)
    }
}

fun makeBasicContainerControlCtor(tag: String): (BasicContainerControlBuilder.() -> Unit) -> Control {
    fun ctor(build: BasicContainerControlBuilder.() -> Unit): Control {
        val cis = BasicContainerControlBuilder()
        cis.build()

        return object : Control(cis) {
            override fun defaultControlTypeName() = tag + "a"

            override fun render(): ReactElement {
                val attrs = cis.allAttrs
                attrs.id = elementID
                return React.createElement(tag, attrs, *cis.children.toTypedArray())
            }
        }
    }

    return ::ctor
}

interface ToReactElementable {
    fun toReactElement(): ReactElement
}

open class ControlInstanceSpec {
    var shame = ""
    var tame = ""
    var tamy = ""
    var shamy = ""
    var elementID = ""
    var controlTypeName = ""
    var noStateContributions = false
    val tattrs = mutableMapOf<String, String>()
    var onClick = NIL_AsyncReactEventHandler

    fun tamyShamy(value: String) {
        tamy = value
        shamy = value
    }

    fun onClicka(handler: (ReactEvent) -> Promise<Unit>) { onClick = handler }

    fun onClick(handler: (ReactEvent) -> Unit) {
        onClick = {e -> "__async"
            handler(e)
            __asyncResult(Unit)
        }
    }

    fun onClickp(handler: () -> Unit) {
        onClick {e ->
            preventAndStop(e)
            handler()
        }
    }
}

abstract class Control(val cis: ControlInstanceSpec = ControlInstanceSpec()) : ToReactElementable {
    abstract fun defaultControlTypeName(): String
    abstract fun render(): ReactElement
    open fun contributeTestState(state: dynamic) {}
    open fun implicitEffectiveShameIsEffectiveTame_todoMakeMeLonger() = false
    open fun firstSignificantStackLine() = 2


    open fun componentWillMount() {}
    open fun componentDidMount() {}
    open fun componentWillUpdate() {}
    open fun componentDidUpdate() {}
    open fun componentWillUnmount() {}

    val controlID = puid()
    val elementID = or(cis.elementID, puids())
    val controlTypeName = or(cis.controlTypeName, defaultControlTypeName())
    var elementThis: dynamic = null
    var ignoreDebugCtrlShiftClick: Boolean = false
    val constructionStackAsError: dynamic = js("Error()")

    val tame: String; val shame: String

    init {
        tame = when {
            cis.tamy == "it" -> controlTypeName
            y(cis.tamy) -> "$controlTypeName-${cis.tamy}"
            else -> cis.tame
        }

        shame = when {
            y(cis.shamy) -> "$controlTypeName-${cis.shamy}"
            else -> cis.shame
        }
    }

    val effectiveTame by lazy {
        when {
            n(tame) -> ""
            else -> {
                var res = tame
                val parents = Shitus.byid(elementID).parents()
                parents.each {
                    val parentControls = Shitus.elementIDToControls[js("this").id]
                    if (!parentControls) undefined
                    else {
                        // TODO:vgrechka @unjs    85c906ea-435e-439e-a8bf-3c9f4a3c9798
                        for (parentControl in jsArrayToList(js("parentControls.slice().reverse()"))) {
                            if (parentControl.tame) {
                                res = parentControl.tame + "." + res
                            }
                        }
                    }
                }

                res
            }
        }
    }

    fun getTamePath() = effectiveTame // @legacy

    val effectiveShame by lazy {
        when {
            y(shame) -> shame
            y(tame) && implicitEffectiveShameIsEffectiveTame_todoMakeMeLonger() -> effectiveTame
            else -> ""
        }
    }

    val debugDisplayName = controlTypeName + ifs(tame){"-$tame"}

    val element: ReactElement = run {
        val reactClassSpec = js("({})")

        reactClassSpec.componentWillUpdate = {
            removeEventListeners()
            componentWillUpdate()
        }

        reactClassSpec.componentDidUpdate = {
            addEventListeners()
            componentDidUpdate()
        }

        reactClassSpec.componentWillMount = {
            elementThis = js("this")

            var elementControls = Shitus.elementIDToControls[elementID]
            if (!elementControls) {
                elementControls = js("[]")
                Shitus.elementIDToControls[elementID] = elementControls
            }

            if (tame != null) {
                for (other in jsArrayToList(elementControls)) {
                    if (other.tame) {
                        runni {"__async"
                            val otherDescription = __await(anyControlDefinitionStackString(other, "  "))
                            val thisDescription = __await(this@Control.definitionStackString("  "))

                            val shortMessage = "Control ${debugDisplayName} conflicts with ${other.debugDisplayName}, because both are tamed"
                            val longMessage = buildString {
                                append(shortMessage)
                                append("\n\n")
                                append("Control 1: ${otherDescription}\n\n")
                                append("Control 2: ${thisDescription}\n")
                            }
                            console.error(longMessage)
                            Shitus.raise(shortMessage)
                        }
                    }
                }
            }

            elementControls.unshift(this)

            componentWillMount()
        }

        reactClassSpec.componentDidMount = {
            addEventListeners()

            fun testStateContributor(state: dynamic) {
                if (n(effectiveTame)) return
                if (cis.noStateContributions) return

                for (parentElement in jsArrayToList(Shitus.byid(elementID).parents())) {
                    val parentControls = Shitus.elementIDToControls[parentElement.id]
                    if (parentControls)
                        for (parentControl in jsArrayToList(parentControls))
                            if (parentControl.noStateContributions) return
                }

                contributeTestState(state)

                for ((key, value) in cis.tattrs)
                    if (y(value))
                    // TODO:vgrechka Capture source location of contributing control    7f25a85a-aed2-4aaf-907f-415b35a74721
                        state.put(json("control" to this, "key" to "$effectiveTame.$key", "value" to value))

                if (y(effectiveShame) && effectiveTame != effectiveShame)
                    state.put(json("control" to this, "key" to "$effectiveTame.shame", "value" to effectiveShame))
            }

            art.uiStateContributions[controlID] = ::testStateContributor

            effectiveShame.let {if (it != "") {
                if (TestGlobal.shameToControl.containsKey(it)) {
                    stickException(js.Error("There is already a thing shamed ${it}"))
                }
                TestGlobal.shameToControl[it] = this
            }}

            componentDidMount()
        }

        reactClassSpec.componentWillUnmount = {
            componentWillUnmount()

            removeEventListeners()
            jsFacing_arrayDeleteFirstThat(Shitus.elementIDToControls[elementID], {x: dynamic -> x.controlID == controlID })
            jsFacing_deleteKey(art.uiStateContributions, controlID)

            if (effectiveShame != "") {
                jsFacing_deleteKey(TestGlobal.shameToControl, effectiveShame)
            }
        }

        reactClassSpec.render = { render() }

        val clazz = React.createClass(reactClassSpec)
        React.createElement(clazz, js("({})"))
    }


    open fun onRootClick(e: ReactEvent): Promise<Unit> {"__async"
        if (y(cis.onClick)) {
            preventAndStop(e)
            __await(cis.onClick(e))
        }

        return __asyncResult(Unit)
    }

    fun testClick(spec: dynamic): Promise<Unit> {"__async"
        val testActionHandOpts: dynamic = if (spec) spec.testActionHandOpts else undefined

        if (art.testSpeed == "slow") {
            val testActionHand = art.showTestActionHand(global.Object.assign(json("target" to Shitus.byid(elementID)), testActionHandOpts))
            __await<dynamic>(Shitus.delay(global.DEBUG_ACTION_HAND_DELAY))
            testActionHand.delete()
        }

        __await(onRootClick(object : ReactEvent {
            override val keyCode = 0
            override val ctrlKey = false
            override val shiftKey = false
            override fun preventDefault() {}
            override fun stopPropagation() {}
        }))

        return __asyncResult(Unit)
    }

    fun addEventListeners() {
        fun onClick(e: ReactEvent) {
            if (MODE == "debug" && e.ctrlKey) {
                if (e.shiftKey) {
                    if (ignoreDebugCtrlShiftClick) return

                    preventAndStop(e)

                    if (effectiveShame == null) {
                        Shitus.raiseWithMeta(json("message" to "Put some shame on me", "meta" to this)) // TODO:vgrechka meta: me
                    }

                    return captureAction()
                }

                preventAndStop(e)
                return Shitus.revealControl(this)
            }

            onRootClick(e)
        }

        removeEventListeners() // Several controls can be on same element, and we don't want to handle click several times
        Shitus.byid(elementID).on("click", ::onClick)
    }

    fun removeEventListeners() {
        Shitus.byid(elementID).off()
    }

    val `$sourceLocation`: Promise<String?> by lazy {
        Promise<String?>({resolve, reject ->
            `$definitionStack`.then<Nothing> {jsArray ->
                resolve(if (jsArray[0]) jsArray[0].loc else null)
            }
        })
    }

    val `$definitionStack`: Promise<dynamic> by lazy {
        promiseDefinitionStack(constructionStackAsError, firstSignificantStackLine())
    }

    fun definitionStackString(sep: String): Promise<String> {"__async"
        val jsarray = __await(this.`$definitionStack`)
        return __asyncResult(jsArrayToList(jsarray).joinToString(sep) { it.loc })

//        return __asyncResult(buildString {
//            jsArrayToIterable(jsarray).joinToString(sep) { it.loc }
//
//        })
//
//        jsArrayToIterable(jsarray).forEachIndexed { i, item ->
//            if (i > 0) append(sep)
//            append("" + item.loc)
//        }
    }

    fun stickException(exception: dynamic) {
        fun doReveal() {
            Shitus.revealStack(json("exception" to global.Object.assign(exception, json("\$render" to {
                Shitus.renderDefinitionStackStrip(json("stack" to `$definitionStack`))
            }))))
        }

        doReveal() // Does nothing if something is already revealed

//        jshit.debugControlStickers.add(json("control" to this, "shit" to json(
//            "onClick" to {
//                jshit.hideStackRevelation()
//                doReveal()
//            }
//        )))
    }

    fun captureAction() {
        console.warn("Implement captureAction, please, fuck you")
    }

    fun update() {
        elementThis.forceUpdate()
    }

    override fun toReactElement() = element

    fun getLongRevelationTitle(): String = debugDisplayName
}










