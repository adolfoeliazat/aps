/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

class Select<E>(
    val values: Array<E>,
    val initialValue: E?,

    // TODO:vgrechka Kotlinize tame/y, shame/y, and all that shit
    val tamyShamy: String? = null,
    val tamy: Any? = null,

    val isAction: Boolean = false,
    val style: Json = json(),
    val volatileDisabled: (() -> Boolean)? = null,

    val onChange: () -> Unit = {},
    val onChanga: () -> Promise<Unit> = {__asyncResult(Unit)},
    val onFocus: () -> Unit = {},
    val onFocusa: () -> Promise<Unit> = {__asyncResult(Unit)},
    val onBlur: () -> Unit = {},
    val onBlura: () -> Promise<Unit> = {__asyncResult(Unit)}
    ) : ToReactElementable, Blinkable
where E : Enum<E>, E : Titled {
    var persistentDisabled: Boolean = false

    fun wantPersistentDisablingAllowed() {
        wantNull(volatileDisabled) {"volatileDisabled conflicts with persistent disabling"}
    }

    fun LegacyCtor(): dynamic {
        var stringValue: String = if (initialValue == null) values[0].name else initialValue.name

        return jshit.statefulElement(json("ctor" to {update: dynamic ->
            var me: dynamic = null
            me = json(
                "render" to {
                    jshit.el.apply(null, js("[]").concat(
                        "select",

                        json(
                            "id" to me.elementID,
                            "className" to "form-control",
                            "value" to stringValue,
                            "disabled" to (volatileDisabled?.let {it()} ?: persistentDisabled),

                            "onChange" to {"__async"
                                me.setValue(jshit.byid0(me.elementID).value)
                                onChange()
                                __await(onChanga())
                            },

                            "onFocus" to {"__async"
                                onFocus()
                                __await(onFocusa())
                            },

                            "onBlur" to {"__async"
                                onBlur()
                                __await(onBlura())
                            }
                        ),

                        values.map {
                            jshit.el("option", json("value" to it.name), it.title)
                        }.toJSArray()
                    ))
                },

//                isAction() {
//                    return isAction
//                },

                "getValue" to {stringValue},
                "focus" to {jshit.byid(me.elementID).focus()},

                "contributeTestState" to {state: dynamic ->
                    if (me.tame) {
                        state.put(json("control" to me, "key" to me.getTamePath() + ".selected.value", "value" to stringValue))

                        val selectedItem = values.find{x -> x.name == stringValue}
                        if (selectedItem == null) throw JSException("WTF is selectedItem (me=${me.debugDisplayName}; value=${stringValue})")
                        state.put(json("control" to me, "key" to me.getTamePath() + ".selected.title",
                            "value" to selectedItem.title))

                        values.forEachIndexed {i, entry ->
                            state.put(json("control" to me, "key" to me.getTamePath() + ".item${jshit.sufindex(i)}.value", "value" to entry.name))
                            state.put(json("control" to me, "key" to me.getTamePath() + ".item${jshit.sufindex(i)}.title", "value" to entry.title))
                        }
                    }
                },

//                getTestValue() {
//                    return {title: textMeat(values.find(x => x.value == value).title),
//                            titles: values.map(x => textMeat(x.title))}
//                },

                "setValue" to {x: dynamic -> "__async"
                    __await<dynamic>(me.setValueExt(json("value" to x)))
                },

                "setValueExt" to {def: dynamic -> "__async"
//                    #extract {value: newValue, notify} from def
                    val newValue = def.value
                    val notify = def.notify

                    stringValue = newValue
                    update()

                    if (notify) {
                        onChange()
                        __await(onChanga())
                    }
                },

                "testGetValue" to {
                    me.getValue()
                },

                "testSetValue" to {arg: dynamic -> "__async"
                    val value = arg.value
                    val testActionHandOpts = arg.testActionHandOpts

                    if (jshit.testSpeed == "slow") {
                        val el = jshit.byid0(me.elementID)
                        el.value = value
                        el.dispatchEvent(js("new MouseEvent('mousedown')"))
                        val testActionHand = jshit.showTestActionHand(global.Object.assign(json("target" to jshit.byid(me.elementID)), testActionHandOpts))
                        __await<dynamic>(Shitus.delay(global.DEBUG_ACTION_HAND_DELAY))
                        testActionHand.delete()

                        __await<dynamic>(me.setValueExt(json("value" to value, "notify" to true)))
//                        __await<dynamic>(me.setValue(value))
                    } else {
                        __await<dynamic>(me.setValueExt(json("value" to value, "notify" to true)))
//                        __await<dynamic>(me.setValue(value))
                    }
                },

                "setBlinking" to {b: dynamic ->
                    if (b) {
                        jshit.effects.blinkOn(json("target" to jshit.byid(me.elementID), "widthCountMargin" to false))
                    } else {
                        jshit.effects.blinkOff()
                    }
                },

                "setDisabled" to {b: Boolean ->
                    wantPersistentDisablingAllowed()
                    persistentDisabled = b
                    update()
                },

                "isDisabled" to {
                    wantPersistentDisablingAllowed()
                    persistentDisabled
                },

                "renderInRevelationPane" to {
                    val els = js("[]")
                    jshit.diva(json("style" to json()),
                        jshit.Betsy(json(
                            "title" to "Values",
                            "details" to jshit.ObjectViewer(("object" to values))
                            ))
                    )
                }
            )

            me.controlTypeName = "Select"
            me.tamyPrefix = "Select"


            jshit.implementControlShit(json("me" to me, "def" to json(
                "tamy" to tamy,
                "tamyShamy" to tamyShamy
            )))

            me
        }))
    }

    val legacyShit = LegacyCtor()

    override fun toReactElement(): ReactElement {
        return asReactElement(legacyShit.element)
    }

    fun getValue(): E {
        val legacyValue: String = legacyShit.getValue()
        return values.find {it.name == legacyValue} ?: wtf("Shitty legacyValue: ${legacyValue}")
    }

    fun setValue(value: E) {
        legacyShit.setValue(value.name)
    }

    fun isDisabled(): Boolean {
        return legacyShit.isDisabled()
    }

    fun setDisabled(b: Boolean) {
        legacyShit.setDisabled(b)
    }

    fun focus() {
        legacyShit.focus()
    }

    val value: E get() = getValue()

    override fun setBlinking(b: Boolean) {
        legacyShit.setBlinking(b)
    }

}














