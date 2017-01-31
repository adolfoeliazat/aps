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

abstract class Fucker {
    var belongsToFuckers by notNullOnce<Fuckers<*>>()
}

abstract class Fuckers<T> {
    val fuckers = mutableListOf<T>()
    val name get() = this::class.simpleName
}

class namedFucker<Base, out T>(val make: (ident: String) -> T) where T : Base, Base : Fucker {
    operator fun provideDelegate(thiz: Fuckers<Base>, property: KProperty<*>) = run {
        val fucker = make(property.name)
        thiz.fuckers += fucker
        fucker.belongsToFuckers = thiz

        object:ReadOnlyProperty<Fuckers<Base>, T> {
            override fun getValue(thisRef: Fuckers<Base>, property: KProperty<*>): T = fucker
        }
    }
}




