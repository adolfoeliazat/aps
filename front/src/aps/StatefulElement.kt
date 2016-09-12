/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

object js {
    val Object = js("Object")
    val Error = js("Error")
}

abstract class StatefulElement(val tame: String? = null) : BasicElementBuilder, ToReactElementable {
    abstract val controlTypeName: String
    abstract fun render(): ReactElement

    open fun componentWillMount() {}
    open fun componentDidMount() {}
    open fun componentWillUpdate() {}
    open fun componentDidUpdate() {}
    open fun componentWillUnmount() {}

    protected open fun onRootClick(e: ReactEvent) = UnitPromise {resolve, reject ->}
    protected open fun contributeTestState(state: dynamic) {}

    protected open fun getLongRevelationTitle(): String = "// TODO:vgrechka Implement getLongRevelationTitle"

    val id = puid()
    val elementID: String = "" + puid()
    var ignoreDebugCtrlShiftClick: Boolean = false

    val constructionStackAsError: dynamic = js("Error()")
    open val firstSignificantStackLine: Int = 2

    var onClick: ReactEventHandler? = null
    fun onClick(handler: ReactEventHandler) { onClick = handler }

    var onMouseEnter: ReactEventHandler? = null
    fun onMouseEnter(handler: ReactEventHandler) { onMouseEnter = handler }

    var onMouseLeave: ReactEventHandler? = null
    fun onMouseLeave(handler: ReactEventHandler) { onMouseLeave = handler }

    override var className = ""
    override val style = StyleBuilder()


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
            for (item in jsArrayToIterable(jsarray)) {
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
                for (other in jsArrayToIterable(elementControls)) {
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

            jshit.art.uiStateContributions[id] = {state: dynamic ->
                var shouldContribute = !noStateContributions

                if (shouldContribute) {
                    jshit.byid(elementID).parents().each {
                        val parentControls = jshit.elementIDToControls[js("this").id]
                        if (!parentControls) undefined
                        else {
                            for (parentControl in jsArrayToIterable(parentControls)) {
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
            jshit.arrayDeleteFirstThat(jshit.elementIDToControls[elementID], {x -> x.id == id })
            jshit.deleteKey(jshit.art.uiStateContributions, id)

            if (effectiveShame != null) {
                jshit.deleteKey(global.testGlobal.controls, effectiveShame)
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
                for (parentControl in jsArrayToIterable(js("parentControls.slice().reverse()"))) {
                    if (parentControl.tame) {
                        res = parentControl.tame + "." + res
                    }
                }
            }
        }

        return res
    }

}

class spanc(tame: String, val content: String, build:  BasicElementBuilder.() -> Unit) : StatefulElement(tame = tame) {
    override val controlTypeName: String get() = "spanc"

    init {
        build()
    }

    override fun render(): ReactElement {
        return jshit.spana(json("id" to elementID, "className" to className, "style" to style.toJSObject()), content)
    }

    override fun contributeTestState(state: dynamic) {
        getTamePath()?.let {
            state.put(json("control" to this, "key" to it, "value" to content))
        }
    }
}



