import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.TimeZone



fun main(args: Array<String>) {
//    testOverrideWithNothing()
//    testInitOrder()
//    test1()
//    testWhen1()
//    testNullableStringPlus()
//    shit1()
//    shit2()
//    testConcurrentModification()
//    testDownTo()
//    testFinallyResult()
//    testNonExhaustiveWhen()
//    println(System.currentTimeMillis())
//    shit3()
    shit4()
}

fun shit4() {
    val pathname = "/apsua/sign-in.html"
//    val pathname = "/sign-in.html"
    val highlightedItem = Regex("/([^/]*?)\\.html").find(pathname)?.let {
        it.groupValues[1]
    }
    println(highlightedItem)
}


private fun shit3() {
    val unix = 1481389170511L
//    val unix = System.currentTimeMillis()

    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z")
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val formattedDate = sdf.format(Timestamp(unix))
    println(formattedDate)
}

fun testNonExhaustiveWhen() {
    fun qwe(name: String): () -> String =
        when (name) {
            "foo" -> ({"oof"})
            "bar" -> ({
                when (123) {
                }
            })
            else -> ({"wtf"})
        }

    println("foo: " + qwe("foo")())
    println("bar: " + qwe("bar")())
    println("baz: " + qwe("baz")())
}

fun testFinallyResult() {
    val res = try {
        "fuck"
    } finally {
        "shit"
    }
    println(res)
}

fun testDownTo() {
    println("(5 downTo 3)")
    (5 downTo 3).forEach(::println)
    println("(5 downTo 5)")
    (5 downTo 5).forEach(::println)
    println("(5 downTo 6)")
    (5 downTo 6).forEach(::println)
}

fun testConcurrentModification() {
    val list = mutableListOf("foo", "bar", "baz")
    list.forEach {if (it == "foo") list.remove(it)}
}

fun shit2() {
    val a: String? = null
    val b: String? = null
    val c = a + b
    println(c)
}

fun shit1() {
    val html = """<ul id="leftNavbar" class="nav navbar-nav" style="float:none;display:inline-block;vertical-align:top;">"""
    val res = html.replace(Regex("style=\"[^\"]*\"")) {mr->
        mr.value.replace(":", ": ").replace(";", "; ").replace(Regex("; \"$"), ";\"")
    }
    println(res)
}

fun testNullableStringPlus() {
    val s: String? = "tralala"
    println(s + "!")
}

fun testWhen1() {
    run {
        var flag = true
        when {
            flag -> {
                println("case 1")
                flag = false
            }
            !flag -> {
                println("case 2")
            }
        }
    }

}

class test1 {
    init {
        add(10)
        add(20)
        addAll(listOf(30, 40, 50))
    }

    fun add(x: Int) {
        println("Adding $x")
    }

    fun addAll(xs: Iterable<Int>) {
        xs.forEach(this::add)
    }
}

fun testInitOrder() {
    run {
        open class A {
            open val x = "x of a"
            init {println(x)}
        }

        class B : A() {
            override val x: String get()= "x of b"
        }

        B()
    }

    run {
        open class A {
            open fun x() = "x of a"
            init {println(x())}
        }

        class B : A() {
            override fun x() = "x of b"
        }

        B()
    }
}


//fun testOverrideWithNothing() {
//    class A {
//        override fun toString(): String = throw Exception("Don't call toString() on A")
//    }
//
//    class B {
//        override fun toString()/*: Nothing*/ = throw Exception("Don't call toString() on B")
//    }
//
//    println("1) " + try {"" + A()} catch (e: Throwable) {"Died saying: ${e.message}"})
//    println("2) " + try {"" + B()} catch (e: Throwable) {"Died saying: ${e.message}"})
//}
//
//fun foo1() {
//    fun test(f: () -> ZimbabweResponse<String>) {
//        val r = f()
//        when (r) {
//            is ZimbabweResponse.Hunky<String> -> {
//                val s: String = r.meat
//                println("Hunky: $s")
//            }
//            is ZimbabweResponse.Shitty -> {
//                val s: String = r.error
//                println("Shitty: $s")
//            }
//        }
//    }
//
//    test(::bar1)
//    test(::bar2)
//}
//
//fun bar1(): ZimbabweResponse<String> {
//    return ZimbabweResponse.Hunky("yeah")
//}
//
//fun bar2(): ZimbabweResponse<String> {
//    return ZimbabweResponse.Shitty("nope")
//}
//
//sealed class ZimbabweResponse<T> {
//    class Hunky<T>(val meat: T): ZimbabweResponse<T>()
//    class Shitty<T>(val error: String): ZimbabweResponse<T>()
//}
//

