package aps.front

import aps.*
import kotlin.properties.Delegates.notNull
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class URLQueryBase_killme(val world: World)

class MaybeStringURLParam : ReadOnlyProperty<Any?, MaybeStringURLParam> {
    var name by notNull<String>()

    fun get(world: World): String? {
        return world.urlQuery[name]
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): MaybeStringURLParam {
        name = property.name
        return this
    }
}

fun booleanURLParam_killme(default: Boolean = false) =
    object:ReadOnlyProperty<URLQueryBase_killme, Boolean> {
        override fun getValue(thisRef: URLQueryBase_killme, property: KProperty<*>): Boolean {
            return thisRef.world.urlQuery[property.name].relaxedToBoolean(default)
        }
    }

fun stringURLParam_killme(default: String = "") =
    object:ReadOnlyProperty<URLQueryBase_killme, String> {
        override fun getValue(thisRef: URLQueryBase_killme, property: KProperty<*>): String {
            return (thisRef.world.urlQuery[property.name] ?: default).trim()
        }
    }

fun <E : Enum<E>> enumURLParam_killme(values: Array<E>, default: E) =
    object:ReadOnlyProperty<URLQueryBase_killme, E> {
        override fun getValue(thisRef: URLQueryBase_killme, property: KProperty<*>): E {
            return thisRef.world.urlQuery[property.name].relaxedToEnum(values, default)
        }
    }


