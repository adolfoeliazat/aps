package aps.front

import aps.*
import kotlin.properties.Delegates.notNull
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class URLQueryBase_killme(val world: World)

interface URLParam<T> {
    var name: String
}

class URLParamValue<T>(val param: URLParam<T>, val value: T)

class MaybeStringURLParam : URLParam<String>, ReadOnlyProperty<Any?, MaybeStringURLParam> {
    override var name by notNull<String>()

    fun get(world: World = Globus.world): String? {
        return world.urlQuery[name]
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): MaybeStringURLParam {
        name = property.name
        return this
    }
}

class StringURLParam(val default: String) : URLParam<String>, ReadOnlyProperty<Any?, StringURLParam> {
    override var name by notNull<String>()

    fun get(): String {
        return Globus.world.urlQuery[name] ?: default
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): StringURLParam {
        name = property.name
        return this
    }
}

class LongURLParam : URLParam<Long>, ReadOnlyProperty<Any?, LongURLParam> {
    override var name by notNull<String>()

    fun get(): Long {
        return bang(Globus.world.urlQuery[name]).toLong()
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): LongURLParam {
        name = property.name
        return this
    }
}

class EnumURLParam<E: Enum<E>>(val values: Array<E>, val default: E) : URLParam<E>, ReadOnlyProperty<Any?, EnumURLParam<E>> {
    override var name by notNull<String>()

    fun get(world: World = Globus.world): E {
        return world.urlQuery[name].relaxedToEnum(values, default)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): EnumURLParam<E> {
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



fun makeURL(page: PageSpec, paramValues: List<URLParamValue<*>>) = stringBuild{s->
    s += page.path + ".html"
    if (paramValues.isNotEmpty())
        s += "?"
    s += paramValues.joinToString("&") {it.param.name + "=" + it.value}
}

fun makeURL(page: TestRef<PageSpec>, paramValues: List<URLParamValue<*>>) = makeURL(page.it, paramValues)















