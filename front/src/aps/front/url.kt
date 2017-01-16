package aps.front

import aps.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class URLQueryBase(val world: World)

fun booleanURLParam(default: Boolean = false) =
    object: ReadOnlyProperty<URLQueryBase, Boolean> {
        override fun getValue(thisRef: URLQueryBase, property: KProperty<*>): Boolean {
            return thisRef.world.urlQuery[property.name].relaxedToBoolean(default)
        }
    }

fun stringURLParam(default: String = "") =
    object: ReadOnlyProperty<URLQueryBase, String> {
        override fun getValue(thisRef: URLQueryBase, property: KProperty<*>): String {
            return (thisRef.world.urlQuery[property.name] ?: default).trim()
        }
    }

fun <E : Enum<E>> enumURLParam(values: Array<E>, default: E) =
    object:ReadOnlyProperty<URLQueryBase, E> {
        override fun getValue(thisRef: URLQueryBase, property: KProperty<*>): E {
            return thisRef.world.urlQuery[property.name].relaxedToEnum(values, default)
        }
    }


