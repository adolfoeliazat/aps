/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import kotlin.js.*
import kotlin.reflect.KClass

class SelectKey(val testerEnums: List<KClass<*>> = listOf()) {
    val fqn: String get() = imf("aeb11f89-f53b-4201-bcbb-944b66b3d27c")
}

//class SelectKey(override val fqn: String) : Fucker(), FQNed

class Select(
    val key: SelectKey? = null,
    attrs: Attrs = Attrs(),
    val values: List<StringIDTimesTitle>,
    val initialValue: String?,
    val isAction: Boolean = false,
    val style: Json = json(),
    val volatileDisabled: (() -> Boolean)? = null,
    val onChange: () -> Unit = {},
    var onChanga: suspend () -> Unit = {},
    val onFocus: () -> Unit = {},
    val onFocusa: suspend () -> Unit = {},
    val onBlur: () -> Unit = {},
    val onBlura: suspend () -> Unit = {}
) : Control2(attrs), Blinkable {
    var blinker: BlinkerOperations? = null

    companion object {
        val instances = mutableMapOf<SelectKey, Select>()

        @Suppress("UNCHECKED_CAST")
        fun instance(key: SelectKey): Select {
            return instances[key] ?: bitch("No Select keyed `${key.fqn}`")
        }
    }

    var persistentDisabled: Boolean = false
    var value: String = initialValue ?: values[0].id

    override fun defaultControlTypeName() = "Select"

    fun wantPersistentDisablingAllowed() {
        wantNull(volatileDisabled) {"volatileDisabled conflicts with persistent disabling"}
    }

//    fun stringToValue(s: String) = values.find {it.id == s}
//        ?: wtf("53afaa3a-19bd-4c82-a9c2-1b67a919a8c0")

    override fun render(): ToReactElementable {
        return reactCreateElement("select", json(
            "id" to elementID,
            "className" to "form-control",
            "value" to value,

            // React doesn't add `selected` attribute to `option`s, so we capture value here.
            // Needed for HTML assertions.
            "data-value" to value,

            "disabled" to (volatileDisabled?.let {it()} ?: persistentDisabled),

            "onChange" to {asu{
                setValue(Shitus.byid0(elementID).value)
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
              reactCreateElement("option", json("value" to it.id), listOf(it.title.asDynamicReactElement()))
          }
        ).toToReactElementable()
    }


    override fun testGetValue() = value

    suspend fun setValue(value: String) {
        setValueExt(value)
    }

    suspend fun setValueExt(newValue: String, notify: Boolean = false) {
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

object tselect {
    suspend fun setValue(keyRef: SelectKey, value: String) {
        notAwait {Select.instance(keyRef).setValueExt(value, notify = true)}
    }
}









