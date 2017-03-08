/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.*
import kotlin.js.*

class SelectKey<T>(override val fqn: String) : Fucker(), FQNed
where T : Titled

class Select<T>(
    val key: SelectKey<T>? = null,
    attrs: Attrs = Attrs(),
    val values: Array<T>,
    val initialValue: T?,
    val getItemID: (T) -> String,
    val isAction: Boolean = false,
    val style: Json = json(),
    val volatileDisabled: (() -> Boolean)? = null,
    val onChange: () -> Unit = {},
    var onChanga: suspend () -> Unit = {},
    val onFocus: () -> Unit = {},
    val onFocusa: suspend () -> Unit = {},
    val onBlur: () -> Unit = {},
    val onBlura: suspend () -> Unit = {}
) : Control2(attrs), Blinkable where T : Titled {
    var blinker: BlinkerOperations? = null

    companion object {
        val instances = mutableMapOf<SelectKey<*>, Select<*>>()

        @Suppress("UNCHECKED_CAST")
        fun <E> instance(key: SelectKey<E>/*, values: Array<E>*/): Select<E>
            where E : Enum<E>, E : Titled
        {
            val select = instances[key] as Select<E>? ?: bitch("No Select keyed `${key.fqn}`")
//            val expectedEnumName = values[0]::class.js.name
//            val actualEnumName = select.values[0]::class.js.name
//            check(expectedEnumName == actualEnumName
//                      && arraysEquals(values, select.values)) {
//                "Select values mismatch. Expected $expectedEnumName, got $actualEnumName"}
            return select
        }
    }

    var persistentDisabled: Boolean = false
    var value: T = initialValue ?: values[0]

    override fun defaultControlTypeName() = "Select"

    fun wantPersistentDisablingAllowed() {
        wantNull(volatileDisabled) {"volatileDisabled conflicts with persistent disabling"}
    }

    fun stringToValue(s: String) = values.find {getItemID(it) == s}
        ?: wtf("53afaa3a-19bd-4c82-a9c2-1b67a919a8c0")

    private val stringValue get() = getItemID(value)

    override fun render(): ToReactElementable {
        return reactCreateElement("select", json(
            "id" to elementID,
            "className" to "form-control",
            "value" to stringValue,

            // React doesn't add `selected` attribute to `option`s, so we capture value here.
            // Needed for HTML assertions.
            "data-value" to stringValue,

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
                                      reactCreateElement("option", json("value" to getItemID(it)), listOf(it.title.asDynamicReactElement()))
                                  }
        ).toToReactElementable()
    }


    override fun testGetValue() = stringValue

    suspend fun setValue(value: T) {
        setValueExt(value)
    }

    suspend fun setValueExt(newValue: T, notify: Boolean = false) {
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
            blinker = await(effects).blinkOn(byid(elementID), BlinkOpts(widthCountMargin = false))
        } else {
            bang(blinker).unblink()
            blinker = null
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

suspend fun <E> selectSetValue(keyRef: TestRef<SelectKey<E>>, value: E)
where E : Enum<E>, E : Titled {
    _selectSetValue(keyRef.it, value)
}

//suspend fun <E> selectSetValue(spec: TestRef<SelectFieldSpec<E>>, value: E)
//where E : Enum<E>, E : Titled {
//    _selectSetValue(FieldSpecToCtrlKey[spec.it], value)
//}

private suspend fun <E> _selectSetValue(key: SelectKey<E>, value: E) where E : Enum<E>, E : Titled {
    notAwait {Select.instance(key).setValueExt(value, notify = true)}
}








