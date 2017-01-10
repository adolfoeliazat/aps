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

suspend fun <T> await(p: Promise<T>): T {
    if (TestGlobal.killAwait) die()
    return CoroutineIntrinsics.suspendCoroutineOrReturn {c: Continuation<T> ->
        p.then<Any?>(onFulfilled = {c.resume(it)},
                     onRejected = {c.resumeWithException(it)})
        CoroutineIntrinsics.SUSPENDED
    }
}

suspend fun <T> awaitJSShit(p: Any?): T =
    if (p is Promise<*>) await(p as Promise<T>)
    else p as T

fun <T> Promise<T>.finally(onFulfilled: (T) -> Unit) =
    this.then<Nothing>(onFulfilled, {})


fun <T> Promise<T>.orTimeout(ms: Int, promiseName: String? = null): Promise<T> {
    val shit = ResolvableShit<T>()
    val _promiseName = promiseName ?: NamesOfThings[this] ?: "shit"
    window.setTimeout({shit.reject(Exception("Sick of waiting for $_promiseName"))}, ms)
    this.finally {shit.resolve(it)}
    return shit.promise
}

class ResolvableShit<T> {
    private var _resolve by notNull<(T) -> Unit>()
    private var _reject by notNull<(Throwable) -> Unit>()
    private var _promise by notNull<Promise<T>>()
    private var hasPromise = false

    init {
        reset()
    }

    val promise: Promise<T> get() = _promise
    fun resolve(value: T) = _resolve(value)
    fun reject(e: Throwable) = _reject(e)

    fun reset() {
        if (hasPromise) {
            NamesOfThings.unflow(this, promise)
        }

        _promise = Promise<T> {resolve, reject ->
            this._resolve = resolve
            this._reject = reject
        }
        hasPromise = true
        NamesOfThings.flow(this, promise)
    }
}

fun ResolvableShit<Unit>.resolve() = this.resolve(Unit)

fun tillEndOfTime(): Promise<Unit> = delay(Int.MAX_VALUE)













