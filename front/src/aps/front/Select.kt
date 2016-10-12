/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

class Select<E>(val values: Array<E>, val initialValue: E?, val legacySpec: Json) : ToReactElementable, Blinkable
where E : Enum<E>, E : Titled {

    fun LegacyCtor(): dynamic {
//        val values: dynamic = legacySpec.get("values")
//        val initialValue: dynamic = legacySpec.get("initialValue")
        val onChange: dynamic = legacySpec.get("onChange")
        var disabled: dynamic = legacySpec.get("disabled")
        val isAction: dynamic = legacySpec.get("isAction")
        val onFocus: dynamic = legacySpec.get("onFocus")
        val onBlur: dynamic = legacySpec.get("onBlur")

        val def = global.Object.assign(js("({})"), legacySpec)
        js("delete def.values")
        js("delete def.initialValue")
        js("delete def.onChange")
        js("delete def.disabled")
        js("delete def.isAction")
        js("delete def.onFocus")
        js("delete def.onBlur")

        var stringValue: String = if (initialValue == null) values[0].name else initialValue.name

        return jshit.statefulElement(json("ctor" to {update: dynamic ->
            var me: dynamic = null
            me = json(
                "render" to {
                    jshit.el.apply(null, js("[]").concat("select", global.Object.assign(json(
                        "id" to me.elementID,
                        "className" to "form-control",
                        "value" to stringValue,
                        "disabled" to jshit.utils.fov(disabled),
                        "onFocus" to onFocus,
                        "onBlur" to onBlur,
                        "onChange" to {
                            me.setValue(jshit.byid0(me.elementID).value)
                        }), def),

                        values.map {
//                            val value = x.value; val title = x.title
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
                    if (notify && onChange != null) {
                        __await<dynamic>(onChange())
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
                        __await<dynamic>(jshit.delay(global.DEBUG_ACTION_HAND_DELAY))
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

                "setDisabled" to {x: dynamic ->
                    disabled = x
                    update()
                },

                "isDisabled" to {
                    disabled
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


            jshit.implementControlShit(json("me" to me, "def" to def))

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














