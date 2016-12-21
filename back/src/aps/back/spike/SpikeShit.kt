package aps.back.spike

import java.util.*

fun main(args: Array<String>) {
//    shit1()
//    casts_1()
//    qwe_casts_2()
//    qwe_casts_1()
    qwe_enumValuesEquality()
}

private enum class E1 {FOO, BAR}
private enum class E2 {BAZ, QUX}

private fun qwe_enumValuesEquality() {
    println(Arrays.equals(E1.values(), E1.values()))
    println("ok")
}

private fun qwe_casts_1() {
    val xs = listOf(E1.FOO)
    val ys = xs as List<E2>
    val y: Any = xs.first()
    println(y)
    println("ok")
}


private fun qwe_casts_2() {
    val x = E1.FOO
    val y = x as E2
}

private fun shit1() {
    val text = "foo bar baz"
    val docs = text.split(Regex("----*"))
}
