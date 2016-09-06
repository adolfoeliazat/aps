generateStyleBuilderShit()

fun generateStyleBuilderShit() {
    for (propName in arrayOf(
        "marginTop", "marginRight", "marginBottom", "marginLeft",
        "paddingTop", "paddingRight", "paddingBottom", "paddingLeft",
        "borderTop", "borderRight", "borderBottom", "borderLeft",
        "width", "height", "fontSize", "borderWidth", "margin", "padding"
    )) {
        println("var $propName: String? = null; set(value) { if (value == null) attrs.remove(\"$propName\") else attrs[\"$propName\"] = value }")
        println("fun $propName(value: Int) { $propName = \"\${value}px\" }")
    }
}

fun test1() {
    for (i in 0 until 3)
        println(i)
}

fun test3() {
    val map1 = mapOf("a" to 10, "c" to 30)
    val map2 = mapOf("b" to 20)
    val all = map1 + map2
    println(all)

    var all2 = all + ("q" to 123)
    println(all2)
}


