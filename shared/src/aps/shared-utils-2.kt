package aps

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
fun Any.qualifyMe(group: NamedGroup?) = qualifyMe(group?.qualifiedName)

abstract class NamedGroup(val parent: NamedGroup?) {
    val name get()= bang(this::class.simpleName)
    val qualifiedName: String get()= qualify(name, parent?.qualifiedName)
}

abstract class Refs(val group: NamedGroup?) {
    val name = qualifyMe(group)
}









