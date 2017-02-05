package aps.front

import aps.*
import into.kommon.*
import kotlin.coroutines.experimental.*
import kotlin.coroutines.experimental.intrinsics.*
import kotlin.properties.Delegates.notNull

// https://promisesaplus.com/#point-47
class ThenableShitHidingWrapper<out T>(val thenableShit: T)

fun asu(block: suspend () -> Unit): Promisoid<Unit> =
    async(block)

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
        COROUTINE_SUSPENDED
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
        COROUTINE_SUSPENDED
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
    realTimeoutSet(ms) {
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

fun tillEndOfTimePromise(): Promisoid<Unit> {
    dlog("--- Waiting till end of time ---")
    return delay(Int.MAX_VALUE)
}

suspend fun waitTillEndOfTime() {
    dlog("--- Waiting till end of time ---")
    await(delay(Int.MAX_VALUE))
}

suspend fun sleep(ms: Int) {
    await(delay(ms))
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

    suspend fun testPause1() {
        await(testPause1.promise.orTestTimeoutNamedAfter(testPause1Timeout, {testPause1}))
    }

    suspend fun testPause2() {
        await(testPause2.promise.orTestTimeoutNamedAfter(testPause2Timeout, {testPause2}))
    }

    fun testResume1() {
        sutPause1.resolve()
    }

    fun testResume2() {
        sutPause2.resolve()
    }

    suspend fun sutPause1() {
        testPause1.resolve()
        await(sutPause1.promise.orTestTimeoutNamedAfter(sutPause1Timeout, {sutPause1}))
    }

    suspend fun sutPause2() {
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

    suspend fun testPause() {
        await(testPause.promise.orTestTimeoutNamedAfter(testPauseTimeout, {testPause}))
    }

    fun testResume() {
        sutPause.resolve()
    }

    suspend fun sutPause() {
        testPause.resolve()
        await(sutPause.promise.orTestTimeoutNamedAfter(sutPauseTimeout, {sutPause}))
    }
}

fun timeoutSet(ms: Int, cb: () -> Unit) {
    Globus.currentBrowseroid.timer.setTimeout(cb, ms)
}

fun realTimeoutSet(ms: Int, cb: () -> Unit) {
    realBrowseroid.timer.setTimeout(cb, ms)
}

fun notAwait(block: suspend () -> Unit) {
    async {block()}
}




