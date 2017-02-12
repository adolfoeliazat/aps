package aps.front

import aps.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun myName() = object:ReadOnlyProperty<Any?, String> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): String =
        property.name
}

fun <T> named(make: (ident: String) -> T) = object:ReadOnlyProperty<Any?, T> {
    private var value: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (value == null)
            value = make(property.name)
        return bang(value)
    }

}

class eagerNamed<out T>(val make: (ident: String) -> T) {
    operator fun provideDelegate(thiz: Any?, property: KProperty<*>) = run {
        val value = make(property.name)
        object:ReadOnlyProperty<Any?, T> {
            override fun getValue(thisRef: Any?, property: KProperty<*>): T = value
        }
    }
}

external fun downloadjs(data: Any, fileName: String, mime: String)





