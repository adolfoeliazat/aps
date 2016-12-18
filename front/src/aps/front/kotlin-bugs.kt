@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*


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

