/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import aps.front.Color.*

object js {
    val Object = js("Object")
    val Error = js("Error")
}

abstract class StatefulElement(val tame: String? = null, val elementID: String = "" + puid()) : ToReactElementable {
    abstract val defaultControlTypeName: String

    var controlTypeName: String = defaultControlTypeName
    abstract fun render(): ReactElement

    open fun componentWillMount() {}
    open fun componentDidMount() {}
    open fun componentWillUpdate() {}
    open fun componentDidUpdate() {}
    open fun componentWillUnmount() {}

    protected open fun onRootClick(e: ReactEvent) = UnitPromise {resolve, reject ->}
    protected open fun contributeTestState(state: dynamic) {}

    protected open fun getLongRevelationTitle(): String = "// TODO:vgrechka Implement getLongRevelationTitle"

    val controlID = puid()

    var ignoreDebugCtrlShiftClick: Boolean = false

    val constructionStackAsError: dynamic = js("Error()")
    open val firstSignificantStackLine: Int = 2

    val `$sourceLocation`: Promise<String?> by lazy {
        Promise<String?>({resolve, reject ->
            `$definitionStack`.then<Nothing> {jsArray ->
                resolve(if (jsArray[0]) jsArray[0].loc else null)
            }
        })
    }

    val `$definitionStack`: Promise<dynamic> by lazy {
        promiseDefinitionStack(constructionStackAsError, firstSignificantStackLine)
    }

    val definitionStackString: Promise<String> get() {"__async"
        val jsarray = __await(this.`$definitionStack`)
        return __asyncResult(buildString {
            for (item in jsArrayToList(jsarray)) {
                append("${item.loc}  ")
            }
        })
    }


    val tattrs = mutableMapOf<String, String>() // TODO
    val noStateContributions: Boolean = false

    private val element: ReactElement
    private var elementThis: dynamic = null

//    fun accessTame(): String {
//        return if (tame != null) tame
//        else "nope"
//    }

//    val debugDisplayName: String by lazy {when{
//        tame != null -> tame
//        else -> "dunno"
////        tame != null -> tame
////        controlTypeName != null -> controlTypeName
////        else -> "dunno"
//    }}

//    val tame: String? by lazy {when{
//        simpleTamy -> tamyPrefix ?: controlTypeName
//        tamy != null -> (tamyPrefix ?: controlTypeName) + "-" + tamy
//        else -> explicitTame
//    }}

//    fun tame(value: String) {
//        explicitTame = value
//    }

    var explicitTame: String? = null
    var tamy: String? = null
    var tamyPrefix: String? = null
    var simpleTamy: Boolean = false
    var shame: String? = null
    var hasTestManipulationFunctions: Boolean = false

    val effectiveShame: String? by lazy {
        if (shame != null) {
            shame
        } else if (tame != null && hasTestManipulationFunctions) {
            getTamePath()
        } else {
            null
        }
    }

    val debugDisplayName: String = controlTypeName

    init {
        val def = js("({})")

        def.componentWillUpdate = {
            removeEventListeners()
            componentWillUpdate()
        }

        def.componentDidUpdate = {
            addEventListeners()
            componentDidUpdate()
        }

        def.componentWillMount = {
            elementThis = js("this")

            var elementControls = jshit.elementIDToControls[elementID]
            if (!elementControls) {
                elementControls = js("[]")
                jshit.elementIDToControls[elementID] = elementControls
            }

            if (tame != null) {
                for (other in jsArrayToList(elementControls)) {
                    runni {"__async"
                        val otherDescription = if (other is StatefulElement)
                            __await(other.definitionStackString)
                        else
                            "Some legacy shit"
                        val thisDescription = __await(this@StatefulElement.definitionStackString)

                        if (other.tame) {
                            val shortMessage = "Control ${debugDisplayName} conflicts with ${other.debugDisplayName}, because both are tamed"
                            val longMessage = buildString {
                                append(shortMessage)
                                append("\n\n")
                                append("Control 1: ${otherDescription}\n\n")
                                append("Control 2: ${thisDescription}\n")
                            }
                            console.error(longMessage)
                            raise(shortMessage, json(
                                "\$render" to {
                                    "TODO: Implement ce3ace61-41ee-4c31-ac9c-209748b5cc99"
                                }
                            ))
                        }
                    }
                }
            }

            elementControls.unshift(this)

            componentWillMount()
        }

        def.componentDidMount = {
            addEventListeners()

            jshit.art.uiStateContributions[controlID] = {state: dynamic ->
                var shouldContribute = !noStateContributions

                if (shouldContribute) {
                    jshit.byid(elementID).parents().each {
                        val parentControls = jshit.elementIDToControls[js("this").id]
                        if (!parentControls) undefined
                        else {
                            for (parentControl in jsArrayToList(parentControls)) {
                                if (parentControl.noStateContributions) {
                                    shouldContribute = false
                                    return@each false // break
                                }
                            }
                        }
                    }
                }

                if (shouldContribute) {
                    contributeTestState(state)
                }

                getTamePath()?.let {tp ->
                    for ((key, value) in tattrs) {
                        if (value != null) {
                            state.put(json("control" to this, "key" to tp + "." + key, "value" to value)) // TODO Capture source location
                        }
                    }

                    if (effectiveShame != null) {
                        if (tp != effectiveShame) {
                            state.put(json("control" to this, "key" to tp + ".shame", "value" to effectiveShame))
                        }
                    }
                }
            }

            if (effectiveShame != null) {
                val myEffectiveShame = effectiveShame
                if (js("Object.keys(testGlobal.controls).includes(myEffectiveShame)")) {
                    stickException(js.Error("testGlobal.controls already contains thing shamed ${effectiveShame}"))
                }
                global.testGlobal.controls[effectiveShame] = this
            }

            componentDidMount()
        }

        def.componentWillUnmount = {
            componentWillUnmount()

            removeEventListeners()
            jshit.utils.arrayDeleteFirstThat(jshit.elementIDToControls[elementID], {x -> x.id == controlID })
            jshit.utils.deleteKey(jshit.art.uiStateContributions, controlID)

            if (effectiveShame != null) {
                jshit.utils.deleteKey(global.testGlobal.controls, effectiveShame)
            }
        }

        def.render = { render() }

        val clazz = React.createClass(def)
        element = React.createElement(clazz, js("({})"))
    }

    private fun stickException(exception: dynamic) {
        fun doReveal() {
            jshit.revealStack(json("exception" to global.Object.assign(exception, json("\$render" to {
                jshit.renderDefinitionStackStrip(json("stack" to `$definitionStack`))
            }))))
        }

        doReveal() // Does nothing if something is already revealed

        jshit.debugControlStickers.add(json("control" to this, "shit" to json(
            "onClick" to {
                jshit.hideStackRevelation()
                doReveal()
            }
        )))
    }


    private fun captureAction() {
        console.warn("Implement captureAction, fuck you")
    }

    private fun addEventListeners() {
        fun onClick(e: ReactEvent) {
            if (MODE == "debug" && e.ctrlKey) {
                if (e.shiftKey) {
                    if (ignoreDebugCtrlShiftClick) return

                    preventAndStop(e)

                    if (effectiveShame == null) {
                        jshit.raiseWithMeta(json("message" to "Put some shame on me", "meta" to this)) // TODO:vgrechka meta: me
                    }

                    return captureAction()
                }

                preventAndStop(e)
                return jshit.revealControl(this)
            }

            onRootClick(e)
        }

        removeEventListeners() // Several controls can be on same element, and we don't want to handle click several times
        jshit.byid(elementID).on("click", ::onClick)
    }

    private fun removeEventListeners() {
        jshit.byid(elementID).off()
    }

    protected fun update() {
        elementThis.forceUpdate()
    }

    override fun toReactElement() = element

    fun getTamePath(): String? {
        if (tame == null) {
            throw js.Object.assign(js.Error("getTamePath can only be called on tamed control"),
                json("\$definitionStack" to `$definitionStack`))
        }

        var res = tame
        val parents = jshit.byid(elementID).parents()
        parents.each {
            val parentControls = jshit.elementIDToControls[js("this").id]
            if (!parentControls) undefined
            else {
                for (parentControl in jsArrayToList(js("parentControls.slice().reverse()"))) {
                    if (parentControl.tame) {
                        res = parentControl.tame + "." + res
                    }
                }
            }
        }

        return res
    }

}


//fun sampleUsage_diva() {
//    val el: ReactElement =  diva {
//        elementID = "something-explicit"
//        tame = "something"
//
//        style {color = ROSYBROWN; fontWeight = "bold"}
//
//        onClick {e ->
//            preventAndStop(e)
//            println("I'm responding")
//        }
//
//        onClicka {e -> "__async"
//            fun fetchSomeAsyncShit(): Promise<String> {
//                throw js.Error("No way")
//            }
//
//            preventAndStop(e)
//            val shit: String = __await(fetchSomeAsyncShit())
//            println("Here... I've got some async shit for you: $shit")
//            __asyncResult(Unit)
//        }
//
//        - "Some text content"
//        - diva {
//            - "Some nested shit"
//            onClick {
//                println("Nested shit reacts too")
//            }
//        }
//        - "More text"
//    }.toReactElement()
//}

//fun makeBasicHTMLControlCtor(tag: String): ((BasicHTMLControlBuilder.() -> Unit) -> Control) {
//    return {build ->
//        val builder = BasicHTMLControlBuilder(tag)
//        builder.build()
//        Control(builder)
//    }
//}

val noopAsyncReactEventHandler = {e: ReactEvent -> "__async"; __asyncResult(Unit)}

fun or(vararg xs: String): String {
    return xs.find { it != "" } ?: ""
}

fun puids(): String {
    return "" + puid()
}


class ImplementControlShitSpec {

}

class ControlShit {

}

fun elcl(spec: IReactClassSpec): ReactElement {
    val clazz = React.createClass(json(

    ))
    return React.createElement(clazz, js("({})"))
}

typealias MaybeFun = (() -> Unit)?

interface IReactClassSpec {
    val render: () -> ReactElement
    val componentWillMount: MaybeFun
    val componentDidMount: MaybeFun
    val componentWillUpdate: MaybeFun
    val componentDidUpdate: MaybeFun
    val componentWillUnmount: MaybeFun
}


typealias ReactEventHandler = (ReactEvent) -> Unit
typealias AsyncReactEventHandler = (ReactEvent) -> Promise<Unit>

//open class ControlSpec {
//    var elementID: String? = null
//    var tame: String? = null
//    var firstSignificantStackLine: Int = 2
//
//    var onRootClick: AsyncReactEventHandler = { __asyncResult(Unit) }
//    var contributeTestState: (state: dynamic) -> Unit = {}
////    var getLongRevelationTitle(): String = "// TODO:vgrechka Implement getLongRevelationTitle"
//
//    var componentWillMount: () -> Unit = {}
//    var componentDidMount: () -> Unit = {}
//    var componentWillUpdate: () -> Unit = {}
//    var componentDidUpdate: () -> Unit = {}
//    var componentWillUnmount: () -> Unit = {}
//}

//class BasicHTMLControlBuilder(val tag: String): ControlSpec() {
//    private val attrs = mutableMapOf<String, Any>()
//    protected val children = mutableListOf<ReactElement>()
//    val style = StyleBuilder()
//    var styleKludge: dynamic = undefined
//
//    var noStateContributions: Boolean = false
//
//    var className: String? = null; set(value) { if (value == null) attrs.remove("className") else attrs["className"] = value }
//    var id: String? = null; set(value) { if (value == null) attrs.remove("id") else attrs["id"] = value }
//
//    operator fun invoke(insideMe: FlowElementBuilder.() -> Unit) {
//        insideMe()
//    }
//
//    fun onClick(handler: ReactEventHandler) {
//        attrs["onClick"] = handler
//    }
//
//    operator fun Iterable<ReactElement>.unaryPlus() {
//        for (child in this) add(child)
//    }
//
//    fun add(child: ReactElement?) {
//        if (child != null) children.add(transformChildBeforeAddition(child))
//    }
//
//    fun add(child: ToReactElementable?) {
//        add(child?.toReactElement())
//    }
//
//    operator fun ReactElement?.unaryMinus() {
//        add(this)
//    }
//
//    operator fun ToReactElementable?.unaryMinus() {
//        add(this)
//    }
//
//    operator fun String?.unaryMinus() {
//        if (this != null) add(asReactElement(this))
//    }
//
////    @Suppress("USELESS_CAST")
////    operator fun dynamic.unaryMinus() {
////        when {
////            this == null -> {}
////            this is String -> - (this as String)
////            this.`$meta` != null -> - this.meat
////            this.`$$typeof` == js("Symbol['for']('react.element')") -> -asReactElement(this)
////            this.element && this.element.`$$typeof` == js("Symbol['for']('react.element')") -> -asReactElement(this.element)
////            else -> raise("Weird shit in FlowElementBuilder child")
////        }
////    }
//
//    fun toElement(): ReactElement {
//        val theStyle = if (styleKludge != undefined) styleKludge else style.toJSObject()
//        val allAttrs = (attrs + ("style" to theStyle)).toJSObject()
//        // console.log("allAttrs", allAttrs)
//        return React.createElement(tag, allAttrs, *children.toTypedArray())
//    }
//
//    open fun transformChildBeforeAddition(child: ReactElement) = child
//}




//class diva(build: diva.() -> Unit) : StatefulElement() {
//    private val element: ReactElement
//
//    override val defaultControlTypeName = "diva"
//
//    init {
////        val builder = FlowElementBuilder("div")
////        builder {
////            id = elementID
////        }
////        builder.build()
////        element = builder.toElement()
//    }
//
//    override fun render(): ReactElement = element
//}

fun sufindex(prefix: String, index: Int): String {
    return prefix + jshit.sufindex(index)
}

open class FlowElementBuilder(val tag: String) {
    private val attrs = mutableMapOf<String, Any>()
    protected val children = mutableListOf<ReactElement>()
    val style = StyleBuilder()
    var styleKludge: dynamic = undefined

    var noStateContributions: Boolean = false

    var className: String? = null; set(value) { if (value == null) attrs.remove("className") else attrs["className"] = value }
    var id: String? = null; set(value) { if (value == null) attrs.remove("id") else attrs["id"] = value }

//    operator fun invoke(insideMe: FlowElementBuilder.() -> Unit) {
//        insideMe()
//    }

    fun onClick(handler: ReactEventHandler) {
        attrs["onClick"] = handler
    }

    operator fun Iterable<ReactElement>.unaryPlus() {
        for (child in this) add(child)
    }

    fun add(child: ReactElement?) {
        if (child != null) children.add(transformChildBeforeAddition(child))
    }

    fun add(child: ToReactElementable?) {
        add(child?.toReactElement())
    }

    operator fun ReactElement?.unaryMinus() {
        add(this)
    }

    operator fun ToReactElementable?.unaryMinus() {
        add(this)
    }

    operator fun String?.unaryMinus() {
        if (this != null) add(asReactElement(this))
    }

    @Suppress("USELESS_CAST")
    operator fun dynamic.unaryMinus() {
        when {
            this == null -> {}
            this is String -> - (this as String)
            this.`$meta` != null -> - this.meat
            this.`$$typeof` == js("Symbol['for']('react.element')") -> -asReactElement(this)
            this.element && this.element.`$$typeof` == js("Symbol['for']('react.element')") -> -asReactElement(this.element)
            else -> raise("Weird shit in FlowElementBuilder child")
        }
    }

    fun toElement(): ReactElement {
        val theStyle = if (styleKludge != undefined) styleKludge else style.toJSObject()
        val allAttrs = (attrs + ("style" to theStyle)).toJSObject()
        // console.log("allAttrs", allAttrs)
        return React.createElement(tag, allAttrs, *children.toTypedArray())
    }

    open fun transformChildBeforeAddition(child: ReactElement) = child
}

