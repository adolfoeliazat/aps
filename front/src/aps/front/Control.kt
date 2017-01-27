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
            cis.iconColor?.let { aps.global.Object.assign(glyphAttrs, json("style" to json("color" to it))) }

            return React.createElement("button", json(
                "id" to elementID,
                "className" to "btn btn-${cis.level} ${cis.className}",
                "style" to cis.style.toJSObject(),
                "title" to cis.hint),

                if (cis.icon != null) Shitus.glyph(cis.icon, glyphAttrs) else null,
                if (cis.icon != null && cis.title != null) Shitus.spana(js("({})"), symbols.nbsp) else null,
                Shitus.spana(js("({})"), cis.title)
            )
        }

        override fun contributeTestState(state: TestStateContributions) {
            if (cis.title != null) state.put(this, "$effectiveTame.title", cis.title!!)
            if (cis.icon != null) state.put(this, "$effectiveTame.icon", cis.icon!!)
        }
    }
}

val NIL_AsyncReactEventHandler: (ReactEvent) -> Promisoid<Unit> = js("({})")

inline fun y(areh: (ReactEvent) -> Promisoid<Unit>) = areh !== NIL_AsyncReactEventHandler


inline fun ifs(cond: String, then: () -> String): String = if (cond != "") then() else ""

inline fun y(x: String) = x != ""
inline fun n(x: String) = x == ""

fun anyControlDefinitionStackString(control: dynamic, sep: String): Promisoid<String> = async {
    return@async if (control is StatefulElement)
            await(control.definitionStackString(sep))
        else if (control is Control)
            await(control.definitionStackString(sep))
        else
            "Some legacy shit: " + control.`$definitionStack`.map { x -> x.loc }.join(sep)

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

        override fun contributeTestState(state: TestStateContributions) {
            // println("controlID=$controlID; elementID=$elementID; effectiveTame=$effectiveTame; content=$content")
            state.put(this, effectiveTame, content)
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

    fun add(child: ReactElement?) {
        if (child != null && child !== NORE_killme) {
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
    fun toReactElement(): ReactElement?
    companion object
}

fun ToReactElementable.Companion.from(el: ReactElement?) =
    object:ToReactElementable {
        override fun toReactElement(): ReactElement? = el
    }

fun ToReactElementable.Companion.from(render: () -> ToReactElementable) =
    object:ToReactElementable {
        override fun toReactElement() = render().toReactElement()
    }

fun ToReactElementable.Companion.volatile(render: () -> ToReactElementable) =
    ToReactElementable.from(render)


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

    fun onClicka(handler: (ReactEvent) -> Promisoid<Unit>) { onClick = handler }

    fun onClick(handler: (ReactEvent) -> Unit) {
        onClick = {e -> async {
            handler(e)
        }}
    }

    fun onClickp(handler: () -> Unit) {
        onClick {e ->
            preventAndStop(e)
            handler()
        }
    }
}

abstract class Control(val cis: ControlInstanceSpec = ControlInstanceSpec()) : ToReactElementable, FuckingControl {
    abstract fun defaultControlTypeName(): String
    abstract fun render(): ReactElement
    open fun contributeTestState(state: TestStateContributions) {}
    open fun implicitEffectiveShameIsEffectiveTame_todoMakeMeLonger() = false
    open fun firstSignificantStackLine() = 2


    open fun componentWillMount() {}
    open fun componentDidMount() {}
    open fun componentWillUpdate() {}
    open fun componentDidUpdate() {}
    open fun componentWillUnmount() {}

    val controlID = puid()
    override val elementID = or(cis.elementID, puids())
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
                        for (parentControl in jsArrayToListOfDynamic(js("parentControls.slice().reverse()"))) {
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
                for (other in jsArrayToListOfDynamic(elementControls)) {
                    if (other.tame) {
                        runni {async{
                            val otherDescription = await(anyControlDefinitionStackString(other, "  "))
                            val thisDescription = await(this@Control.definitionStackString("  "))

                            val shortMessage = "Control ${debugDisplayName} conflicts with ${other.debugDisplayName}, because both are tamed"
                            val longMessage = buildString {
                                append(shortMessage)
                                append("\n\n")
                                append("Control 1: ${otherDescription}\n\n")
                                append("Control 2: ${thisDescription}\n")
                            }
                            console.error(longMessage)
                            Shitus.raise(shortMessage)
                        }}
                    }
                }
            }

            elementControls.unshift(this)

            componentWillMount()
        }

        reactClassSpec.componentDidMount = {
            addEventListeners()

            fun testStateContributor(state: TestStateContributions) {
                if (n(effectiveTame)) return
                if (cis.noStateContributions) return

                for (parentElement in jsArrayToListOfDynamic(Shitus.byid(elementID).parents())) {
                    val parentControls = Shitus.elementIDToControls[parentElement.id]
                    if (parentControls)
                        for (parentControl in jsArrayToListOfDynamic(parentControls))
                            if (parentControl.noStateContributions) return
                }

                contributeTestState(state)

                for ((key, value) in cis.tattrs)
                    if (y(value))
                    // TODO:vgrechka Capture source location of contributing control    7f25a85a-aed2-4aaf-907f-415b35a74721
                        state.put(this, "$effectiveTame.$key", value)

                if (y(effectiveShame) && effectiveTame != effectiveShame)
                    state.put(this, "$effectiveTame.shame", effectiveShame)
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
            art.uiStateContributions.remove(controlID)

            if (effectiveShame != "") {
                jsDeleteKey(TestGlobal.shameToControl, effectiveShame)
            }
        }

        reactClassSpec.render = { render() }

        val clazz = React.createClass(reactClassSpec)
        React.createElement(clazz, js("({})"))
    }


    open fun onRootClick(e: ReactEvent): Promisoid<Unit> = async {
        if (y(cis.onClick)) {
            preventAndStop(e)
            await(cis.onClick(e))
        }
    }

    fun testClick(spec: dynamic): Promisoid<Unit> = async {
        val testActionHandOpts: dynamic = if (spec) spec.testActionHandOpts else undefined

        if (art.testSpeed == "slow") {
            val testActionHand = art.showTestActionHand(aps.global.Object.assign(json("target" to Shitus.byid(elementID)), testActionHandOpts))
            await<dynamic>(Shitus.delay(aps.global.DEBUG_ACTION_HAND_DELAY))
            testActionHand.delete()
        }

        await(onRootClick(object : ReactEvent {
            override val keyCode = 0
            override val ctrlKey = false
            override val shiftKey = false
            override fun preventDefault() {}
            override fun stopPropagation() {}
        }))
    }

    fun addEventListeners() {
        fun onClick(e: ReactEvent) {
            if (Globus.mode == Mode.DEBUG && e.ctrlKey) {
                if (e.shiftKey) {
                    if (ignoreDebugCtrlShiftClick) return

                    preventAndStop(e)

                    if (effectiveShame == null) {
                        Shitus.raiseWithMeta(json("message" to "Put some shame on me", "meta" to this)) // TODO:vgrechka meta: me
                    }

                    return captureAction()
                }

                preventAndStop(e)
                die("This code path should be deleted")
//                return Shitus.revealControl(this)
            }

            onRootClick(e)
        }

        removeEventListeners() // Several controls can be on same element, and we don't want to handle click several times
        Shitus.byid(elementID).on("click", ::onClick)
    }

    fun removeEventListeners() {
        Shitus.byid(elementID).off()
    }

    val `$sourceLocation`: Promisoid<String?> by lazy {
        Promisoid<String?>({resolve, reject ->
            `$definitionStack`.then({jsArray ->
                resolve(if (jsArray[0]) jsArray[0].loc else null)
            })
        })
    }

    val `$definitionStack`: Promisoid<dynamic> by lazy {
        promiseDefinitionStack(constructionStackAsError, firstSignificantStackLine())
    }

    fun definitionStackString(sep: String): Promisoid<String> = async {
        val jsarray = await(this.`$definitionStack`)
        return@async jsArrayToListOfDynamic(jsarray).joinToString(sep) { it.loc }
    }

    fun stickException(exception: Exception) {
        fun doReveal() {
            revealStack(exception)
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










