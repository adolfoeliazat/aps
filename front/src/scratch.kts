test6()

fun test6() {
    abstract class A {
        abstract val something: String

        init {
            println(something.toUpperCase())
        }
    }

    class B : A() {
        override val something: String = "i am something"
    }

    B()
}

fun test5() {
    class Arg {
        val foo = "foooooo"
    }

    abstract class Foo(val arg: Arg) {
        init {
        }

        val foo = when {
            true -> arg.foo
            else -> "asd"
        }

    }

    val arg = Arg()

    val foo = object : Foo(arg) {
    }

    println(foo.foo)
}

fun testInit() {
    fun makeX(): String {
        println(2)
        return ""
    }

    class Foo {
        init {
            println(1)
        }

        val x = makeX()

        init {
            println(3)
        }
    }

    Foo()
}

fun tralala1() {
    val s: String? = "qwe"
    println(s + "wwwwwwwwwwwwwwwwwwwwwwwwww")
}

fun strings1() {
    //       012345678
    val s = "foobarbaz"
    println(s.substring(0, 3))
}

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


