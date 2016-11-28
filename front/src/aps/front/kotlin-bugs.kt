package aps.front

import aps.*


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

