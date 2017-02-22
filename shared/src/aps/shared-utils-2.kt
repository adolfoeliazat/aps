package aps

import aps.front.*
import into.kommon.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty

fun escapeHTML(s: String) =
    s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")

/**
 * Fuck thisy builders
 */
fun stringBuild(block: (StringBuilder) -> Unit) =
    StringBuilder().also(block).toString()

operator fun StringBuilder.plusAssign(x: Any?) {
    this.append(x)
}

/**
 * Until KJS source mapping for !! is fixed
 */
fun <T> bang(x: T?): T {
    if (x == null) bitch("Banging on null")
    return x
}

fun qualify(name: String, path: String?) = stringBuild{s->
    if (path != null)
        s += path + "."
    s += name
}

//fun Any.qualifyMe(path: String?) = qualify(bang(this::class.simpleName), path)
//fun <Item : NamedItem> Any.qualifyMe(group: NamedGroup<Item>?) = qualifyMe(group?.qualifiedName)

//interface NamedItem {
//    val name: String
//}
//
//abstract class NamedGroup<Item : NamedItem>(val parent: NamedGroup<Item>?) {
//    val name get()= bang(this::class.simpleName)
//    val qualifiedName: String get()= qualify(name, parent?.qualifiedName)
//    val items = mutableListOf<Item>()
//
//    fun itemSimplyNamed(simpleName: String?) = items.find {simpleName(it.name) == simpleName}
//}
//
//abstract class Refs<Item : NamedItem>(val group: NamedGroup<Item>?) {
//    val name = qualifyMe(group)
//}

fun simpleName(qualified: String): String {
    return qualified.substring(qualified.lastIndexOf(".") + 1)
}

abstract class Fucker {
    var belongsToFuckers by notNullOnce<Fuckers<*>>()
}

abstract class Fuckers<T : Fucker>(val parent: Fuckers<T>?) {
    val name get() = bang(this::class.simpleName)
    val qualifiedName: String get()= qualify(name, parent?.qualifiedName)
    val items = mutableListOf<T>()
}

class namedFucker<Base, out T>(val make: (fqn: String) -> T) where T : Base, Base : Fucker {
    operator fun provideDelegate(thiz: Fuckers<Base>, property: KProperty<*>) = run {
        val fqn = qualify(property.name, thiz.qualifiedName)
        val fucker = make(fqn)
        thiz.items += fucker
        fucker.belongsToFuckers = thiz

        object:ReadOnlyProperty<Fuckers<Base>, T> {
            override fun getValue(thisRef: Fuckers<Base>, property: KProperty<*>): T = fucker
        }
    }
}

fun threeTimes(block: () -> Unit) {
    for (i in 1..3) block()
}

inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long {
    var sum: Long = 0
    for (element in this) {
        sum += selector(element)
    }
    return sum
}






