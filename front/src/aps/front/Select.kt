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
    val key: String? = null,
    attrs: Attrs = Attrs(),
    val values: Array<E>,
    val initialValue: E?,
    val isAction: Boolean = false,
    val style: Json = json(),
    val volatileDisabled: (() -> Boolean)? = null,
    val onChange: () -> Unit = {},
    var onChanga: suspend () -> Unit = {},
    val onFocus: () -> Unit = {},
    val onFocusa: suspend () -> Unit = {},
    val onBlur: () -> Unit = {},
    val onBlura: suspend () -> Unit = {}

) : Control2(attrs), Blinkable where E : Enum<E>, E : Titled {

    companion object {
        val instances = mutableMapOf<String, Select<*>>()

        @Suppress("UNCHECKED_CAST")
        fun <E> instance(key: String, values: Array<E>): Select<E>
            where E : Enum<E>, E : Titled {
            val select = instances[key] as Select<E>? ?: bitch("No Select keyed `$key`")
            val expectedEnumName = values[0]::class.js.name
            val actualEnumName = select.values[0]::class.js.name
            check(expectedEnumName == actualEnumName
                      && arraysEquals(values, select.values)) {
                "Select values mismatch. Expected $expectedEnumName, got $actualEnumName"}
            return select
        }
    }

    var persistentDisabled: Boolean = false
    var value: E = initialValue ?: values[0]

    override fun defaultControlTypeName() = "Select"

    fun wantPersistentDisablingAllowed() {
        wantNull(volatileDisabled) {"volatileDisabled conflicts with persistent disabling"}
    }

    fun stringToValue(s: String) = values.find {it.name == s}
        ?: throw FatException("Select value: $s",
                              markdownPayload = markdownTitledList("Available values:", values.map {it.name}))

    override fun render(): ToReactElementable {
        return reactCreateElement("select", json(
            "id" to elementID,
            "className" to "form-control",
            "value" to value.name,
            "disabled" to (volatileDisabled?.let {it()} ?: persistentDisabled),

            "onChange" to {asu{
                setValue(stringToValue(Shitus.byid0(elementID).value))
                onChange()
                asu {onChanga()}
            }},

            "onFocus" to {asu{
                onFocus()
                onFocusa()
            }},

            "onBlur" to {asu{
                onBlur()
                onBlura()
            }}),

            values.map {
                reactCreateElement("option", json("value" to it.name), listOf(it.title.asDynamicReactElement()))
            }
        ).toToReactElementable()
    }

    override fun contributeTestState(state: TestStateContributions) {
        if (tame != null) {
            val tp = tamePath()
            state.put(this, tp + ".selected.value", value.name)

            val selectedItem = values.find{x -> x.name == value.name}
            if (selectedItem == null) throw FatException("WTF is selectedItem (me=${debugDisplayName}; value=${value.name})")
            state.put(this, tp + ".selected.title", selectedItem.title)

            values.forEachIndexed {i, entry ->
                state.put(this, tp + ".item${Shitus.sufindex(i)}.value", entry.name)
                state.put(this, tp + ".item${Shitus.sufindex(i)}.title", entry.title)
            }
        }
    }

    override fun testGetValue() = value.name

    override suspend fun testSetValue(arg: dynamic) {
        val stringValue = arg.value
        val testActionHandOpts = arg.testActionHandOpts

        if (art.testSpeed == "slow") {
            val el = Shitus.byid0(elementID)
            el.value = value
            el.dispatchEvent(js("new MouseEvent('mousedown')"))
            val testActionHand = art.showTestActionHand(global.Object.assign(json("target" to Shitus.byid(elementID)), testActionHandOpts))
            await<dynamic>(Shitus.delay(global.DEBUG_ACTION_HAND_DELAY))
            testActionHand.delete()

            setValueExt(stringToValue(stringValue), notify = true)
        } else {
            setValueExt(stringToValue(stringValue), notify = true)
        }
    }

    suspend fun setValue(value: E) {
        val x = value.name
        setValueExt(value)
    }

    suspend fun setValueExt(newValue: E, notify: Boolean = false) {
        value = newValue
        update()

        if (notify) {
            onChange()
            onChanga()
        }
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

    override fun setBlinking(b: Boolean) {async{
        if (b) {
            await(effects).blinkOn(byid(elementID), BlinkOpts(widthCountMargin = false))
        } else {
            await(effects).blinkOff()
        }
    }}

    override fun componentDidMount() {
        if (key != null) {
            instances[key] = this
        }
    }

    override fun componentWillUnmount() {
        if (key != null) {
            instances.remove(key)
        }
    }
}

//fun <E> TestScenarioBuilder.selectSetValueDescribingStep(key: String, values: Array<E>, value: E)
//where E : Enum<E>, E : Titled {
//    acta("Selecting in `$key`: ${markdownItalicVerbatim(value.title)}") {
//        val select = Select.instance(key, values)
//        select.setValueExt(value, notify = true)
//    }
//}

suspend fun <E> selectSetValue(spec: TestRef<SelectFieldSpec<E>>, value: E)
where E : Enum<E>, E : Titled {
    val select = Select.instance(spec.it.name, spec.it.values)
    select.setValueExt(value, notify = true)
}





