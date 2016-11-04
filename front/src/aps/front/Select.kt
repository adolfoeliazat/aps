/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.*

interface ShitWithRenderFunction {
    val render: () -> ReactElement
}

class statefulElement(ctor: () -> ShitWithRenderFunction): ToReactElementable {
    lateinit var legacyUpdate: () -> Unit

    val legacyStatefulElement = Shitus.statefulElement({update ->
        legacyUpdate = update
        ctor()
    })

    override fun toReactElement() = legacyStatefulElement.element

    fun update() {
        legacyUpdate()
    }
}

class Select<E>(
    attrs: Attrs,
    val values: Array<E>,
    val initialValue: E?,
    val isAction: Boolean = false,
    val style: Json = json(),
    val volatileDisabled: (() -> Boolean)? = null,
    val onChange: () -> Unit = {},
    val onChanga: () -> Promise<Unit> = {__asyncResult(Unit)},
    val onFocus: () -> Unit = {},
    val onFocusa: () -> Promise<Unit> = {__asyncResult(Unit)},
    val onBlur: () -> Unit = {},
    val onBlura: () -> Promise<Unit> = {__asyncResult(Unit)}

) : Control2(attrs), Blinkable where E : Enum<E>, E : Titled {

    var persistentDisabled: Boolean = false
    var value: E = initialValue ?: values[0]

    override fun defaultControlTypeName() = "Select"

    fun wantPersistentDisablingAllowed() {
        wantNull(volatileDisabled) {"volatileDisabled conflicts with persistent disabling"}
    }

    fun stringToValue(s: String) = values.find {it.name == s} ?: bitch("Select value: $s")

    override fun render(): ReactElement {
        return reactCreateElement("select", json(
            "id" to elementID,
            "className" to "form-control",
            "value" to value.name,
            "disabled" to (volatileDisabled?.let {it()} ?: persistentDisabled),

            "onChange" to {"__async"
                setValue(stringToValue(Shitus.byid0(elementID).value))
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
            }),

            values.map {
                reactCreateElement("option", json("value" to it.name), listOf(it.title.asDynamicReactElement()))
            }
        )
    }

    override fun contributeTestState(state: TestStateContributions) {
        if (tame != null) {
            val tp = tamePath()
            state.put(this, tp + ".selected.value", value.name)

            val selectedItem = values.find{x -> x.name == value.name}
            if (selectedItem == null) throw JSException("WTF is selectedItem (me=${debugDisplayName}; value=${value.name})")
            state.put(this, tp + ".selected.title", selectedItem.title)

            values.forEachIndexed {i, entry ->
                state.put(this, tp + ".item${Shitus.sufindex(i)}.value", entry.name)
                state.put(this, tp + ".item${Shitus.sufindex(i)}.title", entry.title)
            }
        }
    }

    override fun testGetValue() = value.name

    override fun testSetValue(arg: dynamic): Promise<Unit> {"__async"
        val stringValue = arg.value
        val testActionHandOpts = arg.testActionHandOpts

        if (art.testSpeed == "slow") {
            val el = Shitus.byid0(elementID)
            el.value = value
            el.dispatchEvent(js("new MouseEvent('mousedown')"))
            val testActionHand = art.showTestActionHand(global.Object.assign(json("target" to Shitus.byid(elementID)), testActionHandOpts))
            __await<dynamic>(Shitus.delay(global.DEBUG_ACTION_HAND_DELAY))
            testActionHand.delete()

            __await(setValueExt(stringToValue(stringValue), notify = true))
        } else {
            __await(setValueExt(stringToValue(stringValue), notify = true))
        }
        return __asyncResult(Unit)
    }

    fun setValue(value: E): Promise<Unit> {"__async"
        val x = value.name
        return __await(setValueExt(value)) /ignora
    }

    fun setValueExt(newValue: E, notify: Boolean = false): Promise<Unit> {"__async"
        value = newValue
        update()

        if (notify) {
            onChange()
            __await(onChanga())
        }
        return __asyncResult(Unit)
    }

    fun isDisabled(): Boolean {
        wantPersistentDisablingAllowed()
        return persistentDisabled
    }

    fun setDisabled(b: Boolean) {
        wantPersistentDisablingAllowed()
        persistentDisabled = b
        update()
    }

    fun focus() {
        Shitus.byid(elementID).focus()
    }

    override fun setBlinking(b: Boolean) {
        if (b) {
            effects.blinkOn(json("target" to Shitus.byid(elementID), "widthCountMargin" to false))
        } else {
            effects.blinkOff()
        }
    }

}






//class bak_Select<E>(
//    val values: Array<E>,
//    val initialValue: E?,
//
//    // TODO:vgrechka Kotlinize tame/y, shame/y, and all that shit
//    val tamyShamy: String? = null,
//    val tamy: Any? = null,
//
//    val isAction: Boolean = false,
//    val style: Json = json(),
//    val volatileDisabled: (() -> Boolean)? = null,
//
//    val onChange: () -> Unit = {},
//    val onChanga: () -> Promise<Unit> = {__asyncResult(Unit)},
//    val onFocus: () -> Unit = {},
//    val onFocusa: () -> Promise<Unit> = {__asyncResult(Unit)},
//    val onBlur: () -> Unit = {},
//    val onBlura: () -> Promise<Unit> = {__asyncResult(Unit)}
//) : ToReactElementable, Blinkable
//where E : Enum<E>, E : Titled {
//    var persistentDisabled: Boolean = false
//
//    fun wantPersistentDisablingAllowed() {
//        wantNull(volatileDisabled) {"volatileDisabled conflicts with persistent disabling"}
//    }
//
//    var value: E = initialValue ?: values[0]
//
//    fun stringToValue(s: String) = values.find {it.name == s} ?: bitch("Select value: $s")
//
//    val me = ControlShitMe()
//
//    val statefulShit = statefulElement({
//        me.contributeTestState = {state: dynamic ->
//            if (me.tame != null) {
//                state.put(json("control" to me, "key" to me.getTamePath() + ".selected.value", "value" to value.name))
//
//                val selectedItem = values.find{x -> x.name == value.name}
//                if (selectedItem == null) throw JSException("WTF is selectedItem (me=${me.debugDisplayName}; value=${value.name})")
//                state.put(json("control" to me, "key" to me.getTamePath() + ".selected.title",
//                    "value" to selectedItem.title))
//
//                values.forEachIndexed {i, entry ->
//                    state.put(json("control" to me, "key" to me.getTamePath() + ".item${Shitus.sufindex(i)}.value", "value" to entry.name))
//                    state.put(json("control" to me, "key" to me.getTamePath() + ".item${Shitus.sufindex(i)}.title", "value" to entry.title))
//                }
//            }
//        }
//
//        me.testGetValue = {
//            value.name
//        }
//
//        me.testSetValue = {arg: dynamic -> "__async"
//            val stringValue = arg.value
//            val testActionHandOpts = arg.testActionHandOpts
//
//            if (art.testSpeed == "slow") {
//                val el = Shitus.byid0(me.elementID)
//                el.value = value
//                el.dispatchEvent(js("new MouseEvent('mousedown')"))
//                val testActionHand = art.showTestActionHand(global.Object.assign(json("target" to Shitus.byid(me.elementID)), testActionHandOpts))
//                __await<dynamic>(Shitus.delay(global.DEBUG_ACTION_HAND_DELAY))
//                testActionHand.delete()
//
//                __await(setValueExt(stringToValue(stringValue), notify = true))
//            } else {
//                __await(setValueExt(stringToValue(stringValue), notify = true))
//            }
//        }
//
//        me.controlTypeName = "Select"
//        me.tamyPrefix = "Select"
//
//        me.render = {reactCreateElement("select", json(
//            "id" to me.elementID,
//            "className" to "form-control",
//            "value" to value.name,
//            "disabled" to (volatileDisabled?.let {it()} ?: persistentDisabled),
//
//            "onChange" to {"__async"
//                setValue(stringToValue(Shitus.byid0(me.elementID).value))
//                onChange()
//                __await(onChanga())
//            },
//
//            "onFocus" to {"__async"
//                onFocus()
//                __await(onFocusa())
//            },
//
//            "onBlur" to {"__async"
//                onBlur()
//                __await(onBlura())
//            }),
//
//            values.map {
//                reactCreateElement("option", json("value" to it.name), listOf(it.title.asReactElement()))
//            }
//        )}
//
//        implementControlShit2(me, json(
//            "tamy" to tamy,
//            "tamyShamy" to tamyShamy
//        ))
//
//        me
//    })
//
//    override fun toReactElement(): ReactElement = statefulShit.toReactElement()
//
//    fun setValue(value: E): Promise<Unit> {"__async"
//        val x = value.name
//        return __await(setValueExt(value)) /ignora
//    }
//
//    fun setValueExt(newValue: E, notify: Boolean = false): Promise<Unit> {"__async"
//        value = newValue
//        statefulShit.update()
//
//        if (notify) {
//            onChange()
//            __await(onChanga())
//        }
//        return __asyncResult(Unit)
//    }
//
//    fun isDisabled(): Boolean {
//        wantPersistentDisablingAllowed()
//        return persistentDisabled
//    }
//
//    fun setDisabled(b: Boolean) {
//        wantPersistentDisablingAllowed()
//        persistentDisabled = b
//        statefulShit.update()
//    }
//
//    fun focus() {
//        Shitus.byid(me.elementID).focus()
//    }
//
//    override fun setBlinking(b: Boolean) {
//        if (b) {
//            effects.blinkOn(json("target" to Shitus.byid(me.elementID), "widthCountMargin" to false))
//        } else {
//            effects.blinkOff()
//        }
//    }
//}



