/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*
import kotlin.js.json

class Checkbox(val valueSetter: (Boolean) -> Unit, val elementID: String) {
    companion object {
        val instances = mutableMapOf<CheckboxKey, Checkbox>()

        fun instance(key: CheckboxKey): Checkbox {
            return instances[key] ?: bitch("No Checkbox keyed `${key.fqn}`")
        }
    }

    fun testSetValue(b: Boolean) {
        valueSetter(b)
    }
}

class CheckboxKey(override val fqn: String) : Fucker(), FQNed

fun jsFacing_Checkbox(def: dynamic, key: CheckboxKey? = null): dynamic {
    val onChange: (() -> Promisoid<Unit>)? = def.onChange
    val initialValue: dynamic = if (def.initialValue != null) def.initialValue else false

    var disabled: Boolean = false

    return Shitus.statefulElement(ctor@{update ->
            var value: dynamic = initialValue

            var me: dynamic = null
        val setValue = {x: dynamic -> value = x; update()}
        me = json(
            "render" to {
                    elKillme("input", json(
                        "id" to me.elementID,
                        "type" to "checkbox",
                        "checked" to value,
                        "disabled" to disabled,
                        "onChange" to {async{
                            value = !value
                            update()
                            onChange?.let {await(it())}
                        }}
                    ))
                },

            "getValue" to { value },
            "setValue" to setValue,
            "setDisabled" to {x: dynamic -> disabled = x; update() },
            "isDisabled" to {x: dynamic -> disabled },

            "contributeTestState" to {state: TestStateContributions ->
                    if (me.tame) {
                        me.noisy = true
                        state.put(me, me.getTamePath(), me.getValue())
                    }
                },

            "testGetValue" to {
                    me.getValue()
                },

            "testSetValue" to {arg: dynamic -> async {
                    val value: dynamic = arg.value

                    if (art.testSpeed == "slow") {
                        console.warn("// TODO:vgrechka Implement slow testSetValue for Checkbox    1dcb9af1-30f4-42b1-8dce-c9e3f742d881")
                        me.setValue(value)
                    } else {
                        me.setValue(value)
                    }
                }}
            )

            me.componentDidMount = {
                if (key != null) {
                    Checkbox.instances[key] = Checkbox(setValue, me.elementID)
                }
            }

            me.componentWillUnmount = {
                if (key != null) {
                    Checkbox.instances.remove(key)
                }
            }

            me.controlTypeName = "Checkbox"
            legacy_implementControlShit(json("me" to me, "def" to def))
            return@ctor me
        })
}


suspend fun checkboxSetValue(field: TestRef<CheckboxFieldSpec>, value: Boolean) {
    checkboxSetValue(FieldSpecToCtrlKey[field.it], value)
}

suspend fun checkboxSetValue(key: CheckboxKey, value: Boolean) {
    Checkbox.instance(key).testSetValue(value)
}






