package aps.front

import aps.*

fun <T> async(
    coroutine c: PromiseController<T>.() -> Continuation<Unit>
): Promise<T> =
    Promise {resolve, reject ->
        c(PromiseController(resolve, reject)).resume(Unit)
    }

class PromiseController<T>(
    private val resolve: (T) -> Unit,
    private val reject: (Throwable) -> Unit
) {
    suspend fun <V> await(p: Promise<V>, machine: Continuation<V>) {
        p.then<Any?>(
            onFulfilled = {value->
                machine.resume(value)
            },
            onRejected = {throwable->
                machine.resumeWithException(throwable)
            }
        )
    }

    @Suppress("UnsafeCastFromDynamic")
    suspend fun <V> awaitJSShit(p: dynamic, machine: Continuation<V>) {
        if (p is Promise<*>) {
            await<V>(p, machine)
        } else {
            machine.resume(p)
        }
    }

    operator fun handleResult(value: T, c: Continuation<Nothing>) {
        resolve(value)
    }

    operator fun handleException(t: Throwable, c: Continuation<Nothing>) {
        reject(t)
    }
}




















