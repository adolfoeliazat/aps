///*
// * APS
// *
// * (C) Copyright 2015-2016 Vladimir Grechka
// */
//
//package aps.front
//
//import aps.*
//
//val elementIDToControls = mutableMapOf<String, MutableList<ControlShit>>()
//
//class ControlShit(
//    val controlTypeName: String,
//    val ignoreDebugCtrlShiftClick: Boolean = true,
//    val attrs: ControlShitAttrs,
//    val render: (ControlShit) -> ToReactElementable,
//    val componentWillUpdate: () -> Unit = {},
//    val componentDidUpdate: () -> Unit = {},
//    val componentWillMount: () -> Unit = {},
//    val componentDidMount: () -> Unit = {},
//    val componentWillUnmount: () -> Unit = {}
//    ) {
//
//    val elementID: String = attrs.id.orDefault(::puid)
//
//    val reactElement = React.createElement(jshit.React.createClass(json(
//        "render" to {render(this).toReactElement()},
//
//        "componentWillUpdate" to {
//            removeEventListeners()
//            componentWillUpdate()
//        },
//
//        "componentDidUpdate" to {
//            addEventListeners()
//            componentDidUpdate()
//        },
//
//        "componentWillMount" to {
//            val elementControls = elementIDToControls.getOrPut(elementID) {mutableListOf()}
//
//            if (attrs.tame != null) {
//                elementControls.find {it.attrs.tame != null}?.let {other ->
//                    // TODO:vgrechka Display source locations of culprits    881fb005-e290-448e-805c-301d76bd3b25
//                    throw JSException("Control ${debugDisplayName} conflicts with ${other.debugDisplayName} because both are tamed on same element")
//                }
//            }
//
//            elementControls.add(0, this) // TODO:vgrechka Why to the head?
//            componentWillMount()
//        },
//
//        "componentDidMount" to {
//            addEventListeners()
//
//            art.uiStateContributions[me.id] = function(state) {
//                if (me.contributeTestState) {
//                    let shouldContribute = !me.noStateContributions
//
//                    if (shouldContribute) {
//                        byid(me.elementID).parents().each(function() {
//                            const parentControls = elementIDToControls[this.id] || []
//                            for (const parentControl of parentControls) {
//                            if (parentControl.noStateContributions) {
//                                shouldContribute = false
//                                return false // break
//                            }
//                        }
//                        })
//                    }
//
//                    if (shouldContribute) {
//                        me.contributeTestState(state)
//                    }
//                }
//
//                for (const [key, value] of toPairs(me.tattrs || {})) {
//                if (value !== undefined) {
//                    state.put(s{control: me, key: me.getTamePath() + '.' + key, value})
//                }
//            }
//
//                if (me.effectiveShame) {
//                    const tp = me.getTamePath()
//                    if (tp !== me.effectiveShame) {
//                        state.put(s{control: me, key: tp + '.shame', value: me.effectiveShame})
//                    }
//                }
//            }
//
//            // Determine effective shame
//            if (me.shame) {
//                me.effectiveShame = me.shame
//            }
//            else if (me.tame && hasTestManipulationFunctions()) {
//                me.effectiveShame = me.getTamePath()
//            }
//
//            if (me.effectiveShame) {
//                if (keys(testGlobal.controls).includes(me.effectiveShame)) {
//                    me.stickException({exception: Error(`testGlobal.controls already contains thing shamed ${me.effectiveShame}`)})
//                }
//                testGlobal.controls[me.effectiveShame] = me
//            }
//
//            componentDidMount()
//        },
//
////            "componentWillReceiveProps" to spec.componentWillReceiveProps,
////            "shouldComponentUpdate" to spec.shouldComponentUpdate,
////            "componentWillUpdate" to spec.componentWillUpdate
//    )), json())
//
//
//}





//val kdiv = BasicElementBuilder("div")
//val kspan = BasicElementBuilder("span")
//val kform = BasicElementBuilder("form")
//
//open class ControlShitAttrs {
//    var tame: String? = null
//    val id: String? = null
//}
//
//class BasicElementBuilder(val tag: String): ControlShitAttrs(), ToReactElementable {
//    val children = mutableListOf<ToReactElementable>()
//    var className: String = ""
//
//    lateinit var control: ControlShit
//
//    val o = O()
//    inner class O {
//        operator fun plus(moreChildren: Iterable<ToReactElementable>) {
//            children.addAll(moreChildren)
//        }
//    }
//
//    operator fun invoke(build: BasicElementBuilder.() -> Unit): ToReactElementable {
//        build()
//        control = ControlShit(
//            controlTypeName = tag,
//            ignoreDebugCtrlShiftClick = true,
//            attrs = this,
//            render = {ctx: ControlShit -> object:ToReactElementable {
//                override fun toReactElement() = React.createElement(
//                    tag,
//                    json(
//                        "id" to ctx.elementID,
//                        "className" to className
//                    ),
//                    *children.map{it.toReactElement()}.toTypedArray()
//                )
//            }}
//        )
//        return this
//    }
//
//    override fun toReactElement() = control.reactElement
//}
//
//fun kelcl(spec: ReactClassSpec): ReactElement {
//    return jshit.React.createElement(jshit.React.createClass(json(
//        "render" to spec.render,
//        "componentWillMount" to spec.componentWillMount,
//        "componentDidMount" to spec.componentDidMount,
//        "componentWillReceiveProps" to spec.componentWillReceiveProps,
//        "shouldComponentUpdate" to spec.shouldComponentUpdate,
//        "componentWillUpdate" to spec.componentWillUpdate,
//        "componentDidUpdate" to spec.componentDidUpdate,
//        "componentWillUpdate" to spec.componentWillUpdate
//    )))
//}
//
//data class ReactClassSpec(
//    val render: () -> ReactElement,
//    val componentWillMount: (() -> Unit)? = null,
//    val componentDidMount: (() -> Unit)? = null,
//    val componentWillReceiveProps: (() -> Unit)? = null,
//    val shouldComponentUpdate: (() -> Unit)? = null,
//    val componentWillUpdate: (() -> Unit)? = null,
//    val componentDidUpdate: (() -> Unit)? = null,
//    val componentWillUnmount: (() -> Unit)? = null
//)
//
////class ControlShitContext {
////    lateinit var elementID: String
////}

