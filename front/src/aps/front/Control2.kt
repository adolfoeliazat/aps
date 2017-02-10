/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.*
import org.w3c.dom.events.MouseEvent
import kotlin.js.Json
import kotlin.js.json

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
    val href: String? = null,
    val tabIndex: Int? = null,
    val onClick: ((MouseEvent) -> Unit)? = null,
    val onClicka: ((MouseEvent) -> Promisoid<Unit>)? = null,
    val onMouseEnter: ((MouseEvent) -> Unit)? = null,
    val onMouseEntera: ((MouseEvent) -> Promisoid<Unit>)? = null,
    val onMouseLeave: ((MouseEvent) -> Unit)? = null,
    val onMouseLeava: ((MouseEvent) -> Promisoid<Unit>)? = null
)

abstract class Control2(val attrs: Attrs = Attrs()) : ToReactElementable, FuckingControl {
    abstract protected fun render(): ToReactElementable

    open fun componentWillMount        (){}
    open fun componentDidMount         (){}
    open fun componentWillReceiveProps (){}
    open fun componentDidUpdate        (){}
    open fun componentWillUpdate       (){}
    open fun componentWillUnmount      (){}
    open fun defaultControlTypeName() = "SomeShit"
    open fun contributeTestState(state: TestStateContributions) {}
    open fun contributeTestStateIfTamed(state: TestStateContributions) {}
    open fun ignoreDebugCtrlShiftClick() = false
    open fun defaultNoStateContributions() = false
    open fun effectiveShameDefaultsToTamePath() = true
    open fun simpleOnRootClickImpl() = false
    open fun simpleTestClickImpl() = false

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
    override val elementID = attrs.id ?: puid()
    lateinit var reactClassInstance: ReactClassInstance
    var reactClassInstanceOperable = false
    var shouldUpdate = false
//    var errorStickerID: String? = null
    var errorStickerTether: Tether? = null
    val definitionStackCapture = CaptureStackException()

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
                    for (another: dynamic in jsArrayToListOfDynamic(elementControls)) {
                        if (another.tame != null) Shitus.raise("Control ${debugDisplayName} conflicts with ${another.debugDisplayName}, because both are tamed on #${elementID}")
                    }
                }

                elementControls.unshift(this)

                componentWillMount()
            },

            "componentDidMount" to {
                reactClassInstanceOperable = true

                addEventListeners()

                art.uiStateContributions[id] = {state: TestStateContributions ->
                    var shouldContribute = !noStateContributions

                    if (shouldContribute) {
                        Shitus.byid(elementID).parents().each {
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
                        contributeTestState(state)
                        if (tame != null) contributeTestStateIfTamed(state)
                    }

                    for (entry in jsArrayToListOfDynamic(lodash.toPairs(attrs.tattrs ?: js("({})")))) {
                        val key: dynamic = entry[0]
                        val value: dynamic = entry[1]
                        if (value != null) {
                            state.put(this, tamePath() + "." + key, value)
                        }
                    }

                    effectiveShame?.let {es->
                        val tp = tamePath()
                        if (tp != es) {
                            state.put(this, tp  + ".shame", es)
                        }
                    }
                }


                val effectiveShame: String? = effectiveShame
                effectiveShame?.let {
                    if (TestGlobal.shameToControl.containsKey(it)) {
                        stickException(json("exception" to Error("There is already a thing shamed ${it}")))
                    } else {
                        TestGlobal.shameToControl[it] = json(
                            "testSetValue" to {x: dynamic -> asu {
                                testSetValue(x)
                            }},
                            "testGetValue" to {
                                testGetValue()
                            },
                            "testClick" to {async<Unit>{
                                await(testClick())
                            }}
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
                art.uiStateContributions.remove(id)

                effectiveShame?.let {TestGlobal.shameToControl.remove(it)}

//                errorStickerID?.let {debugPanes.remove(it)}
                errorStickerTether?.destroy()
            },

            "componentWillReceiveProps" to {
                componentWillReceiveProps()
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
                    render().toReactElement()
                } catch (e: Throwable) {
                    throw e
                }
            }
        )),
        json() // Attributes
    )

    open suspend fun testSetValue(x: dynamic) {die("Control $debugDisplayName doesn't support testSetValue")}
    open fun testGetValue(): Any? {die("Control $debugDisplayName doesn't support testGetValue")}

    open fun testClick(): Promisoid<Unit> {
        if (simpleTestClickImpl()) {
            return onRootClick(DummyMouseEvent())
        } else {
            die("Control $debugDisplayName doesn't support testClick")
        }
    }

    fun update() {
        if (isInTestScenario() && hrss.worldIsHalted) return
        if (!reactClassInstanceOperable) {
            // TODO:vgrechka Include control construction stack in warning
            // console.warn("Attempt to update non-operable control")
            return
        }

        shouldUpdate = true
        reactClassInstance.forceUpdate()
        shouldUpdate = false
    }

    open fun onRootClick(e: MouseEvent): Promisoid<Unit> = async {
        if (simpleOnRootClickImpl()) {
            e.preventDefault()
            e.stopPropagation()
            attrs.onClick?.let {it(e)}
            attrs.onClicka?.let {await(it(e))}
        }
    }

    fun captureAction(arg: dynamic) {imf("captureAction")}

    fun addEventListeners() {
        Shitus.byid(elementID).off() // Several controls can be on same element, and we don't want to handle click several times
        Shitus.byid(elementID).on("click", {e: MouseEvent -> async<Unit> {
            if (Globus.mode == Mode.DEBUG && e.ctrlKey) {
                if (e.shiftKey) {
                    if (ignoreDebugCtrlShiftClick()) return@async Unit

                    preventAndStop(e)

                    if (effectiveShame == null) {
                        Shitus.raiseWithMeta(json("message" to "Put some shame on me", "meta" to this))
                    }

                    return@async captureAction(null) // TODO:vgrechka Prettier default argument
                }

                preventAndStop(e)
                return@async await(revealStack(definitionStackCapture, muteConsole=true, skipAllForeignLines=true))
            }

            await(onRootClick(e))
        }})
    }

    fun removeEventListeners() {
        Shitus.byid(elementID).off()
    }

    fun tamePath(): String {
        var res: String = tame ?: bitch("getTamePath can only be called on tamed control")
        val parents: dynamic = Shitus.byid(elementID).parents()
        parents.each {
            val parentControls: dynamic = Shitus.elementIDToControls[js("this").id] || jsArrayOf()
            for (parentControl in jsArrayToListOfDynamic(parentControls.slice().reverse())) {
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
        die("stickException")
//        val exception: dynamic = arg.exception
//
//        val element = Shitus.byid0(elementID)
//        check(element != null) {"stickException to unrendered element"}
//
//        fun doReveal() {
//            revealStack(exception)
//        }
//
//        doReveal() // Does nothing if something is already revealed
//
//        val errorStickerID = puid(); this.errorStickerID = errorStickerID
//
//        debugPanes.put(errorStickerID, oldShitAsToReactElementable(React.createElement("div", json(
//            "id" to errorStickerID,
//            "style" to json(
//                "width" to 10,
//                "height" to 10,
//                "background" to Color.RED_500.toString(),
//                "cursor" to "pointer",
//                "zIndex" to REALLY_BIG_Z_INDEX),
//            "onClick" to {
//                Shitus.hideStackRevelation()
//                revealStackCalledTimes = 0
//                doReveal()
//            }
//        ))))
//
//        requestAnimationFrame {
//            errorStickerTether = Tether(json(
//                "element" to byid(errorStickerID),
//                "target" to element,
//                "attachment" to "top left",
//                "targetAttachment" to "top left"
//            ))
//        }
    }

    companion object
}

fun Control2.Companion.from(render: () -> ToReactElementable): Control2 =
    object:Control2(Attrs()) {
        override fun render(): ToReactElementable = render()
    }





















