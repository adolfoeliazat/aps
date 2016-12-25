@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*

fun qwe_qwe() {
    class qwe {
        val x get() = 123
        init {
            val p = this::x
            val i: Int = p()
            clog("iiiii", i)
        }
    }
    qwe()
}


//fun qwe_compiles() {
//    val a: A.A2? = run {
//        val b: A = A.A2()
//        when (b) {
//            is A.A2 -> return@run b
//            else -> return@run null
//        }
//    }
//}
//
//private sealed class A {
//    class A1 : A()
//    class A2 : A()
//}

//fun qwe_doesntCompile() {
//    val a: Promise<A.A2?> = async { // Error: ...inferred type is Promise<A?> but Promise<A.A2?> was expected
//        val b: A = A.A2()
//        when (b) {
//            is A.A2 -> return@async b
//            else -> return@async null
//        }
//    }
//}

//fun qwe_compilesOK() {
//    val a: Promise<A.A2?> = async {
//        val b: A = A.A2()
//        when (b) {
//            is A.A2 -> b
//            else -> null
//        }
//    }
//}
//
//fun qwe_coroutineUnit() {
//    val f: (() -> Unit)? = null
//
//    val x: Unit = run { // OK. Figures out to ignore last evaluated value in block
//        f?.invoke()
//    }
//
////    val p: Promise<Unit> = async { // Error: ...inferred type is Promise<Unit?> but Promise<Unit> was expected
////        f?.invoke()
////    }
//}


//private enum class E1 {FOO, BAR}
//private enum class E2 {BAZ, QUX}

//fun qwe_casts_1() {
//    val xs = listOf(E1.FOO)
//    val ys = xs as List<E2>
//    println("ok")
//}
//
//fun qwe_casts_2() {
//    val x = E1.FOO
//    val y = x as E2
//    console.log("ok")
//}
//
//fun qwe_casts_3() {
//    val x = "qwe"
//    val y = x as Int
//    console.log(y)
//    console.log("ok")
//}
//
//fun qwe_casts_4() {
//    val xs = listOf("qwe")
//    val ys = xs as List<Int>
//    val y = ys.first()
//    console.log(jsTypeOf(y))
//    console.log(y)
//    console.log("ok")
//}
//
//fun qwe_casts_4_1() {
//    val xs = listOf("qwe")
//    val ys = xs as List<Int>
//    val y = ys.first() as Int
//    console.log(jsTypeOf(y))
//    console.log(y)
//    console.log("ok")
//}
//
//fun qwe_casts_5() {
//    val xs = listOf(E1.FOO)
//    val ys = xs as List<E2>
//    val y = ys.first()
//    console.log(y)
//    console.log("ok")
//}

//fun qwe_split_works_sync() {
//    val tokens = "foo:bar:baz".split(Regex(":"))
//    console.log(tokens.joinToString("-"))
//}
//
//fun qwe_split_breaks() {
//    async<Unit> {
//        val tokens = "foo:bar:baz".split(Regex(":"))
//        console.log(tokens.joinToString("-"))
//    }
//}
//
//fun qwe_split_breaks_catch() {
//    async<Unit> {
//        try {
//            val tokens = "foo:bar:baz".split(Regex(":"))
//            console.log(tokens.joinToString("-"))
//        } catch(e: Throwable) {
//            console.log(e.asDynamic().stack)
//        }
//    }
//}
//
//fun qwe_split_works_async_noRegex() {
//    async<Unit> {
//        val tokens = "foo:bar:baz".split(":")
//        console.log(tokens.joinToString("-"))
//    }
//}



//class A(val s: String) {
//    constructor(i: Int) : this("zzzzzzzzz " + when {
//        else -> {
//            console.log("Hi, I'm at top level :)")
//            i
//        }
//    })
//}



//private enum class E {
//    FIRST, SECOND
//}
//
//fun qwe_enum_js() {
//    console.log(E.valueOf("THIRD"))
//}

//fun qwe_breaks() {
//    val obj = object {
//        val s: String? = "qqqqqq"
//    }
//
//    obj.s?.let {
//        console.log("In this block I don't reference argument")
//    }
//}
//
//fun qwe_works() {
//    val obj = object {
//        val s: String? = "qqqqqq"
//    }
//
//    obj.s?.let {
//        console.log("In this block I do reference argument: " + it)
//    }
//}


//fun qwe_withoutCast_1() {
//    val d: dynamic = null // Something coming from outside
//    // .....
//    val s: String = d
//    clog("sssss", s)
//}
//
//fun qwe_withCast_1() {
//    val d: dynamic = null // Something coming from outside
//    // .....
//    val s: String = d as String
//    //                ^~~~~~~~~ Incorrect warning: No cast needed
//    clog("sssss", s)
//}
//
//fun qwe_withoutCast_2() {
//    val d: dynamic = 123 // Something coming from outside
//    // .....
//    val s: String = d
//    clog("sssss", s)
//}
//
//fun qwe_withCast_2() {
//    val d: dynamic = 123 // Something coming from outside
//    // .....
//    val s: String = d as String
//    //                ^~~~~~~~~ Incorrect warning: No cast needed
//    clog("sssss", s)
//}



//fun qwe_works() {
//    fun f(): Promise<Unit> = async {
//        await(delay(1000))
//        throw Exception("Aarrgghh...")
//        await(delay(1000))
//        console.log("OK")
//    }
//
//    async<Unit> {
//        try {
//            await(f())
//        } catch (e: Throwable) {
//            console.log(e.asDynamic().stack)
//        }
//    }
//}
//
//fun qwe_breaks() {
//    fun f(): Promise<Unit> = async {
//        await(delay(1000))
//        null!!
//        await(delay(1000))
//        console.log("OK")
//    }
//
//    async<Unit> {
//        try {
//            await(f())
//        } catch (e: Throwable) {
//            console.log(e.asDynamic().stack)
//        }
//    }
//}


//interface A
//class B : Throwable()
//open class Something
//
//fun qwe_breaks() {
//    val e = Throwable()
//
//    console.log(1)
//    if (e is A) { // <-- Dies here
//        console.log("is A")
//    }
//    console.log(2)
//}
//
//fun qwe_works_lhsIsNotThrowable() {
//    val e = Something()
//
//    console.log(1)
//    if (e is A) {
//        console.log("is A")
//    }
//    console.log(2)
//}
//
//fun qwe_works_rhsIsNotInterface() {
//    val e = Throwable()
//
//    console.log(1)
//    if (e is B) {
//        console.log("is B")
//    }
//    console.log(2)
//}




//fun qwe_breaks_map() {
//    val local = 10
//    val x = mutableMapOf<Any?, Any?>()
//    x["asd"] = lll@{
//        console.log(local)
//    }
//}
//
//fun qwe_breaks_dynamic() {
//    val local = 10
//    val x: dynamic = js("({})")
//    x["asd"] = lll@{
//        console.log(local)
//    }
//}
//
//fun qwe_breaks_operatorSet() {
//    val local = 10
//    val x = object { operator fun set(i: String, value: Any) {} }
//    x["asd"] = lll@{
//        console.log(local)
//    }
//}
//
//fun qwe_works_noLabel() {
//    val local = 10
//    val x = mutableMapOf<Any?, Any?>()
//    x["asd"] = {
//        console.log(local)
//    }
//}
//
//fun qwe_works_noBrackets() {
//    val local = 10
//    val x = lll@{
//        console.log(local)
//    }
//}
//
//fun qwe_works_simpleProperty() {
//    val local = 10
//    val x = object { var y: Any? = null }
//    x.y = lll@{
//        console.log(local)
//    }
//}

