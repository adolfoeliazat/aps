package aps.front

import aps.*
import kotlin.coroutines.*

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




// ------------------------------ PRE-1.1-M04 ------------------------------

//fun <T> async(
//    coroutine c: PromiseController<T>.() -> Continuation<Unit>
//): Promise<T> =
//    Promise {resolve, reject ->
//        c(PromiseController(resolve, reject)).resume(Unit)
//    }
//
//class PromiseController<T>(
//    private val resolve: (T) -> Unit,
//    private val reject: (Throwable) -> Unit
//) {
//    suspend fun <V> await(p: Promise<V>, machine: Continuation<V>) {
//        p.then<Any?>(
//            onFulfilled = {value->
//                machine.resume(value)
//            },
//            onRejected = {throwable->
//                machine.resumeWithException(throwable)
//            }
//        )
//    }
//
//    @Suppress("UnsafeCastFromDynamic")
//    suspend fun <V> awaitJSShit(p: dynamic, machine: Continuation<V>) {
//        if (p is Promise<*>) {
//            await<V>(p, machine)
//        } else {
//            machine.resume(p)
//        }
//    }
//
//    operator fun handleResult(value: T, c: Continuation<Nothing>) {
//        resolve(value)
//    }
//
//    operator fun handleException(t: Throwable, c: Continuation<Nothing>) {
//        reject(t)
//    }
//}




















