fun main(args: Array<String>) {
//    testOverrideWithNothing()
//    testInitOrder()
//    test1()
//    testWhen1()
    testNullableStringPlus()
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

