@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*

fun qweeeeee() {

}

fun qwe_1() = async {
    fun f() = Promisoid<Unit> {_, _ -> console.log("I will never end")}

    val promise = async<Unit> {
        f()
    }

    console.log(1)
    console.log("res", await(promise))
    console.log(2)
}

//fun qwe_2() = async {
//    val promise: Promisoid<Promisoid<Unit>> = async {
//        Promisoid<Unit> {_, _ -> console.log("I will never end")}
//    }
//
//    console.log(1)
//    promise.then<Nothing>(
//        onFulfilled = {res: Promisoid<Unit> ->
//            console.log("Fulfilled")    // <-- This never executes, but it should
//        }
//    )
//    await(promise)
//    console.log(2)
//}
//
//fun qwe_3() {
//    val promise: Promisoid<Promisoid<Unit>> = Promisoid<Promisoid<Unit>> {resolve, _ ->
//        resolve(Promisoid<Unit> {_, _ -> console.log("I will never end")})
//    }
//
//    console.log(1)
//    promise.then<Nothing>(
//        onFulfilled = {res: Promisoid<Unit> ->
//            console.log("Fulfilled")    // <-- This never executes, but it should
//        }
//    )
//    console.log(2)
//}


//fun qwe_works() {
//    var x by notNull<Int>()
//    x = 10
//    console.log(x)
//}
//
//fun qwe_breaks(): Promise<Unit> = async {
//    var x by notNull<Int>()
//    "aaaaaaaaaaa"    // <-- Marker for generated code
//    x = 10
//    "bbbbbbbbbbb"    // <-- Marker for generated code
//    console.log(x)
//}
//
//fun qwe_fix(): Promise<Unit> = async {
//    val shit = object {
//        var x by notNull<Int>()
//    }
//    shit.x = 10
//    console.log(shit.x)
//}



//fun qwe_breaks() {
//    123.let {
//        val x = run {
//            return@let
//        }
//    }
//}
//
//fun qwe_zzzz() {
//    fun showProgress() {}
//    fun hideProgress() {}
//
//    async {
//        showProgress()
//        // try {                                     // <-- Try uncommenting this
//            val stuff = try {
//                val promise = async {"Maybe I'll throw, maybe not"}
//                await(promise)
//            } catch(e: Exception) {
//                console.log("No stuff")
//                return@async
//            }
//
//            console.log("Got stuff: $stuff")
//        // } finally { hideProgress() }              // <-- Try uncommenting this
//    }
//}
//
//
//
//fun qwe_works_sync() {
//    try {
//        js("undefined").lalala
//    } catch (e: dynamic) {
//        console.log("Aaarrgghh", e.message)
//    } finally {
//        console.log("Finally")
//    }
//}
//
//fun qwe_works_async_simple() {
//    val promise = async {
//        js("undefined").lalala
//    }
//
//    async {
//        try {
//            await(promise)
//        } catch (e: dynamic) {
//            console.log("Aaarrgghh", e.message)
//        } finally {
//            console.log("Finally")
//        }
//    }
//}
//
//fun qwe_breaks_async_addedDelay() {
//    val promise = async {
//        await(Promise<Unit> {resolve, _ -> window.setTimeout(resolve, 100)}) // <-- Added this line
//        console.log("After delay")
//        js("undefined").lalala
//    }
//
//    async {
//        try {
//            await(promise)
//        } catch (e: dynamic) {
//            console.log("Aaarrgghh", e.message)
//        } finally {
//            console.log("Finally")
//        }
//    }
//}
//
//fun qwe_works_async_wrappedNativeError() {
//    val promise = async {
//        await(Promise<Unit> {resolve, _ -> window.setTimeout(resolve, 100)}) // <-- Added this line
//        console.log("After delay")
//        try {
//            js("undefined").lalala
//        } catch (e: dynamic) {
//            throw Exception("Wrapped: ${e.message}")
//        }
//    }
//
//    async {
//        try {
//            await(promise)
//        } catch (e: dynamic) {
//            console.log("Aaarrgghh", e.message)
//        } finally {
//            console.log("Finally")
//        }
//    }
//}

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

