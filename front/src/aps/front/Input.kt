/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

fun jsFacing_Input(legacySpec: Json) {
    val input = Input(legacySpec)
    return input.legacyShit
}

val inputReactClass by lazy {React.createClass(json(
    "render" to render@{
        val self = js("this")

        val kind: String? =  self.props.kind
        val defaultValue: dynamic =  self.props.value
        val onChange: dynamic =  self.props.onChange
        val onMount: dynamic =  self.props.onMount
        val style: dynamic =  self.props.style

        val rest = lodash.omit( self.props, "kind", "value", "onChange", "onMount", "style")

        return@render React.createElement(kind ?: "input", global.Object.assign(json(
            "ref" to "input",
            "defaultValue" to defaultValue,
            "style" to global.Object.assign(json("resize" to "none"), style),
            "onChange" to {e: dynamic ->
                self.manualValue = e.target.value
                onChange && onChange(e)
            }
        ), rest))
    },

    "componentDidUpdate" to {
        if (js("this").props.value !== js("this").manualValue) {
            global.ReactDOM.findDOMNode(js("this").refs.input).value = js("this").props.value
        }
    },

    "componentDidMount" to {
        js("this").props.onMount && js("this").props.onMount()
    }
))}


class Input(val legacySpec: Json) : ToReactElementable, Blinkable {

    fun LegacyCtor(): dynamic {
        val onChange: dynamic = legacySpec.get("onChange")
        val style: dynamic = legacySpec.get("style")
        val volatileStyle: dynamic = legacySpec.get("volatileStyle")
        val placeholder: dynamic = legacySpec.get("placeholder")
        val onKeyDown: dynamic = legacySpec.get("onKeyDown")
        val disabled: dynamic = legacySpec.get("disabled")
        val rows: dynamic = legacySpec.get("rows") ?: 5
        val kind: dynamic = legacySpec.get("kind") ?: "input"
        val type: dynamic = legacySpec.get("type") ?: "text"
        val initialValue: dynamic = legacySpec.get("initialValue") ?: ""

        val def = global.Object.assign(js("({})"), legacySpec)
        js("delete def.onChange")
        js("delete def.style")
        js("delete def.volatileStyle")
        js("delete def.placeholder")
        js("delete def.onKeyDown")
        js("delete def.disabled")
        js("delete def.rows")
        js("delete def.kind")
        js("delete def.type")
        js("delete def.initialValue")

        return Shitus.statefulElement(json("ctor" to statefulElementCtor@{update: dynamic ->
            var value = initialValue
            var loading = false
            var _isDisabled = false

            var me: dynamic = null
            me = json(
                "render" to render@{
                    var isRenderingDisabled = _isDisabled
                        if (disabled != null) {
                            isRenderingDisabled = shittyFov(disabled)
                        }

                    return@render React.createElement(inputReactClass, json(
                        "id" to me.elementID,
                        "rows" to rows,
                        "placeholder" to textMeat(placeholder),
                        "kind" to kind,
                        "type" to type,
                        "value" to value,
                        "className" to "form-control",
                        "disabled" to isRenderingDisabled,
                        "onClick" to me.onPhysicalClick,
                        "style" to global.Object.assign(json(),
                            shittyFov(volatileStyle),
                            style,
                            // TODO:vgrechka Implement new-style spinner for Input    d39c80df-fc4e-4b83-8318-c79963b6a010
                            if (loading) json(
                                "background" to "url('${Preloader.srcDefault32()}') no-repeat right center",
                                "paddingRight" to 36
                            ) else null
                        ),

                        "onChange" to {e: dynamic ->
                            value = e.target.value
                            update()
                            onChange && onChange(e.target.value)
                        },

                        "onKeyDown" to {e: dynamic ->
                            shittyFov(onKeyDown, e)
                        }
                    ))
                },

                "contributeTestState" to {state: dynamic ->
                    if (me.tame) {
                        val key = me.getTamePath()
                        state.put(json("control" to me, "key" to key, "value" to me.getValue()))
                        if (type != "text") {
                            state.put(json("control" to me, "key" to key + ".type", "value" to type))
                        }
                        if (kind != "input") {
                            state.put(json("control" to me, "key" to key + ".kind", "value" to kind))
                        }
                    }
                },

                "getValue" to { value },
                "setLoading" to {x: dynamic -> loading = x; update() },
                "focus" to { Shitus.byid(me.elementID).focus() },
                "select" to { Shitus.byid(legacySpec.get("id")).select() },
                "setDisabled" to {x: dynamic -> _isDisabled = x; update() },
                "isDisabled" to { _isDisabled },

                "setValue" to {x: dynamic -> "__async"
                    me.setValueExt(json("value" to x))
                },

                "setValueExt" to {def: dynamic -> "__async"
                    val newValue: dynamic = def.value
                    val notify: dynamic = def.notify

                    value = newValue
                    update()
                    if (notify && onChange != null) {
                        __await<dynamic>(onChange())
                    }
                },

                "testSetValue" to {arg: dynamic -> "__async"
                    val value: dynamic = arg.value
                    if (art.testSpeed == "slow") {
                        me.setValue("")
                        for (len in 1 until value.length) {
                            __await<dynamic>(Shitus.delay(50))
                            me.setValue(value.slice(0, len))
                        }
                    } else {
                        me.setValue(value)
                    }
                },


                "testGetValue" to { me.getValue() },

                "setBlinking" to {b: dynamic ->
                    if (b) {
                        effects.blinkOn(json("target" to Shitus.byid(me.elementID), "widthCountMargin" to false))
                    } else {
                        effects.blinkOff()
                    }
                }
            )

            me.renderInRevelationPane = {
                val els = js("[]")
                Shitus.diva(json("style" to json()),
                    Shitus.hor2(json(),
                        Shitus.link(json(
                            "title" to t("Capture primary click with only this entered"),
                            "onClick" to onClick@{
                                hrss.closeControlRevealer()
                                val primaryButton = getControlByShame("button-primary")
                                if (!primaryButton) return@onClick console.warn("No primary control to click on")

                                var descriptiveName = me.effectiveShame
                                descriptiveName = descriptiveName.replace(global.RegExp("^TextField-"), "")
                                descriptiveName = descriptiveName.replace(global.RegExp("\\.Input\$"), "")
                                primaryButton.captureAction(json(
                                    "includeShames" to jsArrayOf(me.effectiveShame),
                                    "todoActionDescription" to "Entering ${descriptiveName}: TODO"))
                            }
                        ))
                    )
                )
            }

            me.controlTypeName = "Input"
            legacy_implementControlShit(json("me" to me, "def" to def, "implementTestKeyDown" to json("onKeyDown" to onKeyDown)))
            return@statefulElementCtor me
        }))
    }

    val legacyShit = LegacyCtor()

    override fun toReactElement(): ReactElement {
        return asReactElement(legacyShit.element)
    }

    fun getValue(): String {
        return legacyShit.getValue()
    }

    fun setValue(value: String) {
        legacyShit.setValue(value)
    }

    fun isDisabled(): Boolean {
        return legacyShit.isDisabled()
    }

    fun setDisabled(b: Boolean) {
        legacyShit.setDisabled(b)
    }

//    fun focus() {
//        legacyShit.focus()
//    }

    val value: String get() = getValue()

    override fun setBlinking(b: Boolean) {
        legacyShit.setBlinking(b)
    }

}














