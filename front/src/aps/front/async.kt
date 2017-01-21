package aps.front

import aps.*
import into.kommon.*
import kotlin.coroutines.*
import kotlin.coroutines.intrinsics.SUSPENDED_MARKER
import kotlin.coroutines.intrinsics.suspendCoroutineOrReturn
import kotlin.properties.Delegates.notNull

// https://promisesaplus.com/#point-47
class ThenableShitHidingWrapper<out T>(val thenableShit: T)


fun <T> async(block: suspend () -> T): Promisoid<T> =
    Promisoid {resolve, reject ->
        block.startCoroutine(object:Continuation<T> {
            override val context = EmptyCoroutineContext

            override fun resume(value: T) {
                resolve(value)
            }

            override fun resumeWithException(exception: Throwable) {
                reject(exception)
            }
        })
    }

suspend fun <T> await(p: Promisoid<T>): T {
    if (TestGlobal.killAwait) die()
    return suspendCoroutineOrReturn {c: Continuation<T> ->
        p.then(
            onFulfilled = {
                c.resume(it)
            },
            onRejected = {
                c.resumeWithException(it)
            }
        )
        SUSPENDED_MARKER
    }
}

fun <T> asyncNative(block: suspend () -> T): Promise<T> =
    Promise {resolve, reject ->
        block.startCoroutine(object:Continuation<T> {
            override val context = EmptyCoroutineContext

            override fun resume(value: T) {
                resolve(value)
            }

            override fun resumeWithException(exception: Throwable) {
                reject(exception)
            }
        })
    }

suspend fun <T> awaitNative(p: Promise<T>): T {
    if (TestGlobal.killAwait) die()
    return suspendCoroutineOrReturn {c: Continuation<T> ->
        p.then<Any?>(
            onFulfilled = {
                c.resume(it)
            },
            onRejected = {
                c.resumeWithException(it)
            }
        )
        SUSPENDED_MARKER
    }
}

suspend fun <T> awaitJSShit(p: Any?): T =
    if (p is Promise<*>) awaitNative(p) as T
    else p as T

//fun <T> Promisoid<T>.finally(onFulfilled: (T) -> Unit) =
//    this.then<Nothing>(onFulfilled, {})


fun <T> Promisoid<T>.orTestTimeout(ms: Int, getPromiseName: (() -> String?)? = null): Promisoid<T> {
    val shit = ResolvableShit<T>()
    val thePromiseName = getPromiseName?.invoke() ?: "shit"
    timeoutSet(ms) {
        val msg = "Sick of waiting for $thePromiseName"
        if (isTestPausedOnAssertion()) {
            console.warn("--- $msg, but not dying because test is paused on assertion ---")
        } else {
            shit.reject(Exception(msg))
        }
    }
    this.then {shit.resolve(it)}
    return shit.promise
}

fun <T> Promisoid<T>.orTestTimeoutNamedAfter(ms: Int, getPromiseNameBearer: () -> Any): Promisoid<T> {
    return this.orTestTimeout(ms, getPromiseName = {NamesOfThings[getPromiseNameBearer()]})
}


class ResolvableShit<T> {
    private var _resolve by notNull<(T) -> Unit>()
    private var _reject by notNull<(Throwable) -> Unit>()
    private var _promise by notNull<Promisoid<T>>()
    private var hasPromise = false

    init {
        reset()
    }

    val promise: Promisoid<T> get() = _promise
    fun resolve(value: T) = _resolve(value)
    fun reject(e: Throwable) = _reject(e)

    fun reset() {
        if (hasPromise) {
            NamesOfThings.unflow(this, promise)
        }

        _promise = Promisoid<T> {resolve, reject ->
            this._resolve = resolve
            this._reject = reject
        }
        hasPromise = true
        NamesOfThings.flow(this, promise)
    }
}

fun ResolvableShit<Unit>.resolve() = this.resolve(Unit)

fun tillEndOfTime(): Promisoid<Unit> {
    dlog("--- Waiting till end of time ---")
    return delay(Int.MAX_VALUE)
}

class TwoStepTestLock(
    val testPause1Timeout: Int = 5000,
    val testPause2Timeout: Int = 5000,
    val sutPause1Timeout: Int = 5000,
    val sutPause2Timeout: Int = 5000
) {
    private val testPause1 by notNullNamed(ResolvableShit<Unit>(), parentNamed = this)
    private val testPause2 by notNullNamed(ResolvableShit<Unit>(), parentNamed = this)
    private val sutPause1 by notNullNamed(ResolvableShit<Unit>(), parentNamed = this)
    private val sutPause2 by notNullNamed(ResolvableShit<Unit>(), parentNamed = this)

    init {
        // Initially everything is resolved, so if not in test, shit just works
        testPause1.resolve()
        testPause2.resolve()
        sutPause1.resolve()
        sutPause2.resolve()
    }

    fun reset() {
        testPause1.reset()
        testPause2.reset()
        sutPause1.reset()
        sutPause2.reset()
    }

    fun testPause1(): Promisoid<Unit> = async {
        await(testPause1.promise.orTestTimeoutNamedAfter(testPause1Timeout, {testPause1}))
    }

    fun testPause2(): Promisoid<Unit> = async {
        await(testPause2.promise.orTestTimeoutNamedAfter(testPause2Timeout, {testPause2}))
    }

    fun testResume1() {
        sutPause1.resolve()
    }

    fun testResume2() {
        sutPause2.resolve()
    }

    fun sutPause1(): Promisoid<Unit> = async {
        testPause1.resolve()
        await(sutPause1.promise.orTestTimeoutNamedAfter(sutPause1Timeout, {sutPause1}))
    }

    fun sutPause2(): Promisoid<Unit> = async {
        testPause2.resolve()
        await(sutPause2.promise.orTestTimeoutNamedAfter(sutPause2Timeout, {sutPause2}))
    }
}

class TestLock(
    val testPauseTimeout: Int = 10000,
    val sutPauseTimeout: Int = 10000
) {
    private val testPause by notNullNamed(ResolvableShit<Unit>(), parentNamed = this)
    private val sutPause by notNullNamed(ResolvableShit<Unit>(), parentNamed = this)

    init {
        // Initially everything is resolved, so if not in test, shit just works
        testPause.resolve()
        sutPause.resolve()
    }

    fun reset() {
        testPause.reset()
        sutPause.reset()
    }

    fun testPause(): Promisoid<Unit> = async {
        await(testPause.promise.orTestTimeoutNamedAfter(testPauseTimeout, {testPause}))
    }

    fun testResume() {
        sutPause.resolve()
    }

    fun sutPause(): Promisoid<Unit> = async {
        testPause.resolve()
        await(sutPause.promise.orTestTimeoutNamedAfter(sutPauseTimeout, {sutPause}))
    }
}








