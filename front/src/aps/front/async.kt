package aps.front

import aps.*

fun <T> async(
    coroutine c: FutureController<T>.() -> Continuation<Unit>
): Promise<T> =
    Promise {resolve, reject ->
        c(FutureController(resolve, reject)).resume(Unit)
    }

@AllowSuspendExtensions
class FutureController<T>(
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

    operator fun handleResult(value: T, c: Continuation<Nothing>) {
        resolve(value)
    }

    operator fun handleException(t: Throwable, c: Continuation<Nothing>) {
        reject(t)
    }
}


fun ttt_async() {
    val p = async<Unit> {
        console.log("fuck")
    }
    p.then<Unit>({console.log("shit")})
}
























