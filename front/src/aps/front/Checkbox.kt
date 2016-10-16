/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

fun jsFacing_Checkbox(def: dynamic): dynamic {
    val onChange: (() -> Promise<Unit>)? = def.onChange
    val initialValue: dynamic = if (def.initialValue != null) def.initialValue else false

    var disabled: Boolean = false

    return jshit.statefulElement(json(
        "ctor" to ctor@{update: () -> Unit ->
            var value: dynamic = initialValue

            var me: dynamic = null
            me = json(
                "render" to {
                    jshit.el("input", json(
                        "id" to me.elementID,
                        "type" to "checkbox",
                        "checked" to value,
                        "disabled" to disabled,
                        "onChange" to {"__async"
                            value = !value
                            update()
                            onChange?.let {__await(it())}
//                            __await<dynamic>(fova(onChange))
                        }
                    ))
                },

                "getValue" to { value },
                "setValue" to {x: dynamic -> value = x; update() },
                "setDisabled" to {x: dynamic -> disabled = x; update() },
                "isDisabled" to {x: dynamic -> disabled },

                "contributeTestState" to {state: dynamic ->
                    if (me.tame) {
                        me.noisy = true
                        state.put(json("control" to me, "key" to me.getTamePath(), "value" to me.getValue()))
                    }
                },

                "testGetValue" to {
                    me.getValue()
                },

                "testSetValue" to {arg: dynamic -> "__async"
                    val value: dynamic = arg.value

                    if (jshit.testSpeed == "slow") {
                        console.warn("// TODO:vgrechka Implement slow testSetValue for Checkbox    1dcb9af1-30f4-42b1-8dce-c9e3f742d881")
                        me.setValue(value)
                    } else {
                        me.setValue(value)
                    }
                }
            )

            me.controlTypeName = "Checkbox"
            legacy_implementControlShit(json("me" to me, "def" to def))
            return@ctor me
        }
    ))
}


