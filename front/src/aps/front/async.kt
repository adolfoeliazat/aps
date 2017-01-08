package aps.front

import aps.*
import into.kommon.*
import kotlin.browser.window
import kotlin.coroutines.*
import kotlin.properties.Delegates.notNull

fun <T> async(block: suspend () -> T): Promise<T> =
    Promise {resolve, reject ->
        block.startCoroutine(object:Continuation<T> {
            override fun resume(value: T) {
                resolve(value)
            }

            override fun resumeWithException(exception: Throwable) {
                reject(exception)
            }
        })
    }

suspend fun <T> await(p: Promise<T>): T =
    CoroutineIntrinsics.suspendCoroutineOrReturn {c: Continuation<T> ->
        p.then<Any?>(onFulfilled = {c.resume(it)},
                     onRejected = {c.resumeWithException(it)})
        CoroutineIntrinsics.SUSPENDED
    }

suspend fun <T> awaitJSShit(p: Any?): T =
    if (p is Promise<*>) await(p as Promise<T>)
    else p as T

fun <T> Promise<T>.finally(onFulfilled: (T) -> Unit) =
    this.then<Nothing>(onFulfilled, {})


fun <T> Promise<T>.orTimeout(ms: Int, descr: String = "shit"): Promise<T> {
    val shit = ResolvableShit<T>()
    window.setTimeout({shit.reject(Exception("Sick of waiting for $descr"))}, ms)
    this.finally {shit.resolve(it)}
    return shit.promise
}

class ResolvableShit<T> {
    var resolve by notNull<(T) -> Unit>()
    var reject by notNull<(Throwable) -> Unit>()
    var promise by notNull<Promise<T>>()

    init {
        reset()
    }

    fun reset() {
        promise = Promise<T> {resolve, reject ->
            this.resolve = resolve
            this.reject = reject
        }
    }
}

fun ResolvableShit<Unit>.resolve() = this.resolve(Unit)

//class Signal<T> {
//
//}















