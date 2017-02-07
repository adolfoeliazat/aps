package aps

import aps.front.*
import into.kommon.*

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

fun Any.qualifyMe(path: String?) = qualify(bang(this::class.simpleName), path)
fun <Item : NamedItem> Any.qualifyMe(group: NamedGroup<Item>?) = qualifyMe(group?.qualifiedName)

interface NamedItem {
    val name: String
}

abstract class NamedGroup<Item : NamedItem>(val parent: NamedGroup<Item>?) {
    val name get()= bang(this::class.simpleName)
    val qualifiedName: String get()= qualify(name, parent?.qualifiedName)
    val items = mutableListOf<Item>()

    fun itemSimplyNamed(simpleName: String?) = items.find {simpleName(it.name) == simpleName}
}

abstract class Refs<Item : NamedItem>(val group: NamedGroup<Item>?) {
    val name = qualifyMe(group)
}

fun simpleName(qualified: String): String {
    return qualified.substring(qualified.lastIndexOf(".") + 1)
}








