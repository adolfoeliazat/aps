/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import org.w3c.dom.events.MouseEvent

@MixableType
data class Attrs(
    val tame: String? = null,
    val tamy: String? = null,
    val shame: String? = null,
    val shamy: String? = null,
    val tamyShamy: String? = null,
    val controlTypeName: String? = null,
    val id: String? = null,
    val tattrs: Json? = null,
    val noStateContributions: Boolean? = null,
    val className: String? = null,
    val onClick: ((MouseEvent) -> Unit)? = null,
    val onClicka: ((MouseEvent) -> Promise<Unit>)? = null,
    val onMouseEnter: ((MouseEvent) -> Unit)? = null,
    val onMouseEntera: ((MouseEvent) -> Promise<Unit>)? = null,
    val onMouseLeave: ((MouseEvent) -> Unit)? = null,
    val onMouseLeava: ((MouseEvent) -> Promise<Unit>)? = null
)

abstract class Control2(val attrs: Attrs) : ToReactElementable {
    abstract fun defaultControlTypeName(): String
    abstract fun render(): ReactElement

    open fun componentDidUpdate   (){}
    open fun componentWillUpdate  (){}
    open fun componentWillUnmount (){}
    open fun componentDidMount    (){}
    open fun componentWillMount   (){}
    open fun contributeTestState(state: dynamic) {}
    open fun ignoreDebugCtrlShiftClick() = false
    open fun defaultNoStateContributions() = false
    open fun effectiveShameDefaultsToTamePath() = true

    val controlTypeName = attrs.controlTypeName ?: defaultControlTypeName()
    val noStateContributions = attrs.noStateContributions?.let {it} ?: defaultNoStateContributions()

    open val tamyPrefix = controlTypeName
    open val shamyPrefix = controlTypeName

    val tamy: String?
    val shamy: String?
    init {
        if (attrs.tamyShamy != null) {
            check(attrs.tame == null) {"tame is not compatible with tamyShamy"}
            check(attrs.tamy == null) {"tamy is not compatible with tamyShamy"}
            check(attrs.shame == null) {"shame is not compatible with tamyShamy"}
            check(attrs.shamy == null) {"shamy is not compatible with tamyShamy"}
            tamy = attrs.tamyShamy
            shamy = attrs.tamyShamy
        } else {
            tamy = attrs.tamy
            shamy = attrs.shamy
        }

        check(tamy == null || attrs.tame == null) {"Either tame or tamy, please, fuck you"}
        check(shamy == null || attrs.shame == null) {"Either shame or shamy, please, fuck you"}
    }

    val tame: String? = when {
        tamy == "" -> tamyPrefix
        tamy != null -> tamyPrefix + "-" + tamy
        else -> attrs.tame
    }
    init {if (tame == null) check(attrs.tattrs == null) {"Control with tattrs should be tamed"}}

    val shame: String? = when {
        shamy == "" -> shamyPrefix
        shamy != null -> shamyPrefix + "-" + shamy
        else -> attrs.shame
    }

    val debugDisplayName: String = when {
        tame != null && tame != controlTypeName -> tame
        else -> controlTypeName
    }

    val id = puid()
    val elementID = attrs.id ?: puid()
    lateinit var reactClassInstance: ReactClassInstance
    var reactClassInstanceOperable = false
    var shouldUpdate = false
    var errorStickerID: String? = null
    var errorStickerTether: Tether? = null

    override fun toReactElement(): ReactElement = reactElement

    val reactElement = React.createElement(
        React.createClass(json(
            "componentWillMount" to {
                reactClassInstance = js("this")

                var elementControls = Shitus.elementIDToControls[elementID]
                if (elementControls == null) {
                    Shitus.elementIDToControls[elementID] = jsArrayOf()
                    elementControls = Shitus.elementIDToControls[elementID]
                }

                if (tame != null) {
                    for (another: dynamic in jsArrayToList(elementControls)) {
                        if (another.tame != null) Shitus.raise("Control ${debugDisplayName} conflicts with ${another.debugDisplayName}, because both are tamed on #${elementID}")
                    }
                }

                elementControls.unshift(this)

                componentWillMount()
            },

            "componentDidMount" to {
                reactClassInstanceOperable = true

                addEventListeners()

                art.uiStateContributions[id] = {state: dynamic ->
                    var shouldContribute = !noStateContributions

                    if (shouldContribute) {
                        Shitus.byid(elementID).parents().each {
                            val parentControls = Shitus.elementIDToControls[js("this").id] || jsArrayOf()
                            for (parentControl in jsArrayToList(parentControls)) {
                                if (parentControl.noStateContributions) {
                                    shouldContribute = false
                                    return@each false // break
                                }
                            }
                        }
                    }

                    if (shouldContribute) contributeTestState(state)

                    for (entry in jsArrayToList(lodash.toPairs(attrs.tattrs ?: js("({})")))) {
                        val key: dynamic = entry[0]
                        val value: dynamic = entry[1]
                        if (value != null) {
                            state.put(json("control" to this, "key" to tamePath() + "." + key, "value" to value))
                        }
                    }

                    effectiveShame?.let {es->
                        val tp = tamePath()
                        if (tp != es) {
                            state.put(json("control" to this, "key" to tp  + ".shame", "value" to es))
                        }
                    }
                }


                val effectiveShame: String? = effectiveShame
                effectiveShame?.let {
                    if (TestGlobal.shameToControl.containsKey(it)) {
                        stickException(json("exception" to Error("There is already a thing shamed ${it}")))
                    } else {
                        TestGlobal.shameToControl[it] = json(
                            "testSetValue" to {x: dynamic -> "__async"; __await(testSetValue(x))},
                            "testGetValue" to {testGetValue()},
                            "testClick" to {"__async"; __await(testClick())}
                        )
                    }
                }

                componentDidMount()
            },

            "componentWillUnmount" to {
                reactClassInstanceOperable = false
                componentWillUnmount()

                removeEventListeners()
                // @wip perf
//                dwarnStriking("before", Shitus.elementIDToControls[elementID].length, Shitus.elementIDToControls[elementID])
                jsFacing_arrayDeleteFirstThat(Shitus.elementIDToControls[elementID], {x: dynamic -> x.id == id})
//                dwarnStriking("after", Shitus.elementIDToControls[elementID].length, Shitus.elementIDToControls[elementID])
                jsFacing_deleteKey(art.uiStateContributions, id)

                effectiveShame?.let {TestGlobal.shameToControl.remove(it)}

                errorStickerID?.let {DebugPanes.remove(it)}
                errorStickerTether?.destroy()
            },

            "componentWillUpdate" to {
                removeEventListeners()
                componentWillUpdate()
            },

            "componentDidUpdate" to {
                addEventListeners()
                componentDidUpdate()
            },

            "shouldComponentUpdate" to {shouldUpdate},

            "render" to {
                try {
                    render()
                } catch (e: Throwable) {
                    // TODO:vgrechka Render clickable error banner, on click reveal control construction stack
                    throw e
                }
            }
        )),
        json() // Attributes
    )

    open fun testSetValue(x: dynamic): Promise<Unit> {die("Control $debugDisplayName doesn't support testSetValue")}
    open fun testGetValue(): Any? {die("Control $debugDisplayName doesn't support testGetValue")}
    open fun testClick(): Promise<Unit> {die("Control $debugDisplayName doesn't support testClick")}

    fun update() {
        if (isInTestScenario() && hrss.worldIsHalted) return
        if (!reactClassInstanceOperable) {
            // TODO:vgrechka Include control construction stack in warning
            console.warn("Attempt to update non-operable control")
            return
        }

        shouldUpdate = true
        reactClassInstance.forceUpdate()
        shouldUpdate = false
    }

    open fun onRootClick(e: dynamic): Promise<Unit> {"__async"; return __asyncResult(Unit)}

    fun captureAction(arg: dynamic) {imf("captureAction")}

    fun addEventListeners() {
        Shitus.byid(elementID).off() // Several controls can be on same element, and we don't want to handle click several times
        Shitus.byid(elementID).on("click", onClick@{e: dynamic -> "__async"
            if (MODE == "debug" && e.ctrlKey) {
                if (e.shiftKey) {
                    if (ignoreDebugCtrlShiftClick()) return@onClick Unit

                    preventAndStop(e)

                    if (effectiveShame == null) {
                        Shitus.raiseWithMeta(json("message" to "Put some shame on me", "meta" to this))
                    }

                    return@onClick captureAction(null) // TODO:vgrechka Prettier default argument
                }

                preventAndStop(e)
                return@onClick Shitus.revealControl(this)
            }

            __await(onRootClick(e))
        })
    }

    fun removeEventListeners() {
        Shitus.byid(elementID).off()
    }

    fun tamePath(): String {
        var res: String = tame ?: bitch("getTamePath can only be called on tamed control")
        val parents: dynamic = Shitus.byid(elementID).parents()
        parents.each {
            val parentControls: dynamic = Shitus.elementIDToControls[js("this").id] || jsArrayOf()
            for (parentControl in jsArrayToList(parentControls.slice().reverse())) {
                if (parentControl.tame) {
                    // TODO:vgrechka Check that [parentControl.tame] works with new controls
                    res = parentControl.tame + "." + res
                }
            }
        }

        return res
    }

    val effectiveShame: String? by lazy {when{
        shame != null -> shame
        tame != null && effectiveShameDefaultsToTamePath() -> tamePath()
        else -> null
    }}

    fun stickException(arg: dynamic) {
        val exception: dynamic = arg.exception

        val element = Shitus.byid0(elementID)
        check(element != null) {"stickException to unrendered element"}

        fun doReveal() {
            Shitus.revealStack(json("exception" to global.Object.assign(exception, json("\$render" to {
                Shitus.renderDefinitionStackStrip(json("stack" to "TODO: Control2 definition stack"))
            }))))
        }

        doReveal() // Does nothing if something is already revealed

        val errorStickerID = puid(); this.errorStickerID = errorStickerID

        DebugPanes.put(errorStickerID, oldShitAsReactElementable(React.createElement("div", json(
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
            errorStickerTether = Tether(json(
                "element" to byid(errorStickerID),
                "target" to element,
                "attachment" to "top left",
                "targetAttachment" to "top left"
            ))
        }
    }
}







