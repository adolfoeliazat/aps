@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import into.kommon.bitch

fun ctorName(x: Any): String = x.asDynamic().__proto__.constructor.name

inline fun <reified T> instantiate(className: String): T {
    val fullClassName = "F.$className"
    val klass = eval(fullClassName) ?: bitch("No class named $fullClassName")
    check(jsTypeOf(klass) == "function") {"I want $fullClassName to be a function (class ctor)"}
    val inst = eval("new klass()")
    check(inst is T) {"Shit is not of expected type"}
    return inst
}


