package aps.front

import aps.*


fun qwe_breaks_map() {
    val local = 10
    val x = mutableMapOf<Any?, Any?>()
    x["asd"] = lll@{
        console.log(local)
    }
}

fun qwe_breaks_dynamic() {
    val local = 10
    val x: dynamic = js("({})")
    x["asd"] = lll@{
        console.log(local)
    }
}

fun qwe_breaks_operatorSet() {
    val local = 10
    val x = object { operator fun set(i: String, value: Any) {} }
    x["asd"] = lll@{
        console.log(local)
    }
}

fun qwe_works_noLabel() {
    val local = 10
    val x = mutableMapOf<Any?, Any?>()
    x["asd"] = {
        console.log(local)
    }
}

fun qwe_works_noBrackets() {
    val local = 10
    val x = lll@{
        console.log(local)
    }
}

fun qwe_works_simpleProperty() {
    val local = 10
    val x = object { var y: Any? = null }
    x.y = lll@{
        console.log(local)
    }
}

