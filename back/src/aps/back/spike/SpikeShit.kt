package aps.back.spike

import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.Continuation
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

fun main(args: Array<String>) {
//    shit1()
//    casts_1()
//    qwe_casts_2()
//    qwe_casts_1()
//    qwe_enumValuesEquality()
//    qwe_compiles_explicitReturnsRemoved()
//    qwe_doesntCompile()
//    qwe_coroutines_unit()
//    qwe_1()
//    qwe_jvm_lateinit()
}

//fun qwe_jvm_lateinit() {
//    val x = object {
//        lateinit var y: String
//    }
//
//    println(x.y)
//    println("ok")
//}


//private fun qwe_1() {
//    fun <T> g(): T = 123 as T // Implementation doesn't matter here...
//
//    fun <T> f(): CompletableFuture<T> = async {
//        g() // Error: Not enough information to infer parameter T in fun <T> g()
//        // g<T>() // This works. But above line worked in 1.1-M03
//    }
//}


//fun foo() {}
//fun bar(): Int = 123
//
//fun longRunning(): CompletableFuture<Unit> = async {
//    foo()
//    bar()
//    Unit // <-- I don't want this line
//}

//fun qwe_coroutines_unit() {
//    fun foo() {}
//    fun bar(): Int = 123
//
//    val x: Unit = run { // Compiles OK, as it figures out that result of `bar` should be just ignored
//        foo()
//        bar()
//    }
//
//    val p: CompletableFuture<Unit> = async { // Error: ...inferred type is CompletableFuture<Int>, but CompletableFuture<Unit> was expected
//        foo()
//        bar()
//    }
//}

//private sealed class A {
//    class A1 : A()
//    class A2 : A()
//}
//
//private fun qwe_doesntCompile() {
//    val a: CompletableFuture<A.A2?> = async { // Error: ...inferred type is CompletableFuture<A?> but CompletableFuture<A.A2?> was expected
//        val b: A = A.A2()
//        when (b) {
//            is A.A2 -> return@async b
//            else -> return@async null
//        }
//    }
//}
//
//private fun qwe_compiles_explicitReturnsRemoved() {
//    val a: CompletableFuture<A.A2?> = async {
//        val b: A = A.A2()
//        when (b) {
//            is A.A2 -> b
//            else -> null
//        }
//    }
//}

// ---------- Below is taken from https://blog.jetbrains.com/kotlin/2016/12/kotlin-1-1-m04-is-here/#more-4405 ----------

//private suspend fun <T> await(f: CompletableFuture<T>): T =
//    suspendCoroutine<T> { c: Continuation<T> ->
//        f.whenComplete { result, exception ->
//            if (exception == null) // the future has been completed successfully
//                c.resume(result)
//            else // the future has been completed with an exception
//                c.resumeWithException(exception)
//        }
//    }
//
//private fun <T> async(block: suspend () -> T): CompletableFuture<T> {
//    val future = CompletableFuture<T>()
//    block.startCoroutine(completion = object : Continuation<T> {
//        override fun resume(value: T) {
//            future.complete(value)
//        }
//        override fun resumeWithException(exception: Throwable) {
//            future.completeExceptionally(exception)
//        }
//    })
//    return future
//}
//
//
//private enum class E1 {FOO, BAR}
//private enum class E2 {BAZ, QUX}
//
//private fun qwe_enumValuesEquality() {
//    println(Arrays.equals(E1.values(), E1.values()))
//    println("ok")
//}
//
//private fun qwe_casts_1() {
//    val xs = listOf(E1.FOO)
//    val ys = xs as List<E2>
//    val y: Any = xs.first()
//    println(y)
//    println("ok")
//}
//
//
//private fun qwe_casts_2() {
//    val x = E1.FOO
//    val y = x as E2
//}
//
//private fun shit1() {
//    val text = "foo bar baz"
//    val docs = text.split(Regex("----*"))
//}



