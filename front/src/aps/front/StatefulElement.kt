/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import aps.Color.*
import into.kommon.*
import kotlin.js.json

object js {
    val Object = js("Object")
    val Error = js("Error")
}

abstract class StatefulElement(val tame: String? = null, override val elementID: String = "" + puid()) : ToReactElementable, WithElementID {
    abstract val defaultControlTypeName: String

    var controlTypeName: String = defaultControlTypeName
    abstract fun render(): ReactElement

    open fun componentWillMount() {}
    open fun componentDidMount() {}
    open fun componentWillUpdate() {}
    open fun componentDidUpdate() {}
    open fun componentWillUnmount() {}

    protected open fun onRootClick(e: ReactEvent) {}
    protected open fun contributeTestState(state: TestStateContributions) {}

    protected open fun getLongRevelationTitle(): String = "// TODO:vgrechka Implement getLongRevelationTitle"

    val controlID = puid()

    var ignoreDebugCtrlShiftClick: Boolean = false

    val constructionStackAsError: dynamic = js("Error()")
    open val firstSignificantStackLine: Int = 2

    val `$sourceLocation`: Promisoid<String?> by lazy {
        Promisoid<String?>({resolve, reject ->
            `$definitionStack`.then({jsArray ->
                resolve(if (jsArray[0]) jsArray[0].loc else null)
            })
        })
    }

    val `$definitionStack`: Promisoid<dynamic> by lazy {
        promiseDefinitionStack(constructionStackAsError, firstSignificantStackLine)
    }

    val definitionStackString: Promisoid<String> get() = async {
        val jsarray = await(this.`$definitionStack`)
        buildString {
            for (item in jsArrayToListOfDynamic(jsarray)) {
                append("${item.loc}  ")
            }
        }
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

            var elementControls = Shitus.elementIDToControls[elementID]
            if (!elementControls) {
                elementControls = js("[]")
                Shitus.elementIDToControls[elementID] = elementControls
            }

            if (tame != null) {
                for (other in jsArrayToListOfDynamic(elementControls)) {
                    runni {async{
                        val otherDescription = if (other is StatefulElement)
                            await(other.definitionStackString)
                        else
                            "Some legacy shit"
                        val thisDescription = await(this@StatefulElement.definitionStackString)

                        if (other.tame) {
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
                    }}
                }
            }

            elementControls.unshift(this)

            componentWillMount()
        }

        def.componentDidMount = {
            addEventListeners()

            art.uiStateContributions[controlID] = {state: TestStateContributions ->
                var shouldContribute = !noStateContributions

                if (shouldContribute) {
                    Shitus.byid(elementID).parents().each {
                        val parentControls = Shitus.elementIDToControls[js("this").id]
                        if (!parentControls) undefined
                        else {
                            for (parentControl in jsArrayToListOfDynamic(parentControls)) {
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
                            state.put(this, tp + "." + key, value) // TODO Capture source location
                        }
                    }

                    if (effectiveShame != null) {
                        if (tp != effectiveShame) {
                            state.put(this, tp + ".shame", effectiveShame!!)
                        }
                    }
                }
            }

            effectiveShame?.let {
                if (TestGlobal.shameToControl.containsKey(it)) {
                    stickException(js.Error("There is already a thing shamed ${it}"))
                }
                TestGlobal.shameToControl[it] = this
            }

            componentDidMount()
        }

        def.componentWillUnmount = {
            componentWillUnmount()

            removeEventListeners()
            jsFacing_arrayDeleteFirstThat(Shitus.elementIDToControls[elementID], {x: dynamic -> x.id == controlID })
            art.uiStateContributions.remove(controlID)

            if (effectiveShame != null) {
                jsDeleteKey(TestGlobal.shameToControl, effectiveShame!!)
            }
        }

        def.render = { render() }

        val clazz = React.createClass(def)
        element = React.createElement(clazz, js("({})"))
    }

    private fun stickException(exception: dynamic) {
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


    private fun captureAction() {
        console.warn("Implement captureAction, fuck you")
    }

    private fun addEventListeners() {
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

    private fun removeEventListeners() {
        Shitus.byid(elementID).off()
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
        val parents = Shitus.byid(elementID).parents()
        parents.each {
            val parentControls = Shitus.elementIDToControls[js("this").id]
            if (!parentControls) undefined
            else {
                for (parentControl in jsArrayToListOfDynamic(js("parentControls.slice().reverse()"))) {
                    if (parentControl.tame) {
                        res = parentControl.tame + "." + res
                    }
                }
            }
        }

        return res
    }

}



fun or(vararg xs: String): String {
    return xs.find { it != "" } ?: ""
}

fun puids(): String {
    return "" + puid()
}


class ImplementControlShitSpec {

}

interface IReactClassSpec {
    val render: () -> ReactElement
    val componentWillMount:(() -> Unit)?
    val componentDidMount: (() -> Unit)?
    val componentWillUpdate: (() -> Unit)?
    val componentDidUpdate: (() -> Unit)?
    val componentWillUnmount: (() -> Unit)?
}


// typealias ReactEventHandler = (ReactEvent) -> Unit
// typealias AsyncReactEventHandler = (ReactEvent) -> Promise<Unit>

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
    return prefix + Shitus.sufindex(index)
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

    fun onClick(handler: (ReactEvent) -> Unit) {
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

//    @Suppress("USELESS_CAST")
//    operator fun dynamic.unaryMinus() {
//        when {
//            this == null -> {}
//            this is String -> - (this as String)
//            this.`$meta` != null -> - this.meat
//            this.`$$typeof` == js("Symbol['for']('react.element')") -> -asReactElement(this)
//            this.element && this.element.`$$typeof` == js("Symbol['for']('react.element')") -> -asReactElement(this.element)
//            else -> raise("Weird shit in FlowElementBuilder child")
//        }
//    }

    fun toElement(): ReactElement {
        val theStyle = if (styleKludge != null) styleKludge else style.toJSObject()
//        if (theStyle.background == null && theStyle.backgroundColor == null) theStyle.background = "rosybrown"
        val allAttrs = (attrs + ("style" to theStyle)).toJSObject()
        // console.log("allAttrs", allAttrs)
        return React.createElement(tag, allAttrs, *children.toTypedArray())
    }

    open fun transformChildBeforeAddition(child: ReactElement) = child
}

