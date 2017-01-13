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


fun <T> Promise<T>.orTestTimeout(ms: Int, getPromiseName: (() -> String?)? = null): Promise<T> {
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
    this.finally {shit.resolve(it)}
    return shit.promise
}

fun <T> Promise<T>.orTestTimeoutNamedAfter(ms: Int, getPromiseNameBearer: () -> Any): Promise<T> {
    return this.orTestTimeout(ms, getPromiseName = {NamesOfThings[getPromiseNameBearer()]})
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

    fun testPause1(): Promise<Unit> = async {
        await(testPause1.promise.orTestTimeoutNamedAfter(testPause1Timeout, {testPause1}))
    }

    fun testPause2(): Promise<Unit> = async {
        await(testPause2.promise.orTestTimeoutNamedAfter(testPause2Timeout, {testPause2}))
    }

    fun testResume1() {
        sutPause1.resolve()
    }

    fun testResume2() {
        sutPause2.resolve()
    }

    fun sutPause1(): Promise<Unit> = async {
        testPause1.resolve()
        await(sutPause1.promise.orTestTimeoutNamedAfter(sutPause1Timeout, {sutPause1}))
    }

    fun sutPause2(): Promise<Unit> = async {
        testPause2.resolve()
        await(sutPause2.promise.orTestTimeoutNamedAfter(sutPause2Timeout, {sutPause2}))
    }
}

class TestLock(
    val testPauseTimeout: Int = 5000,
    val sutPauseTimeout: Int = 5000
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

    fun testPause(): Promise<Unit> = async {
        await(testPause.promise.orTestTimeoutNamedAfter(testPauseTimeout, {testPause}))
    }

    fun testResume() {
        sutPause.resolve()
    }

    fun sutPause(): Promise<Unit> = async {
        testPause.resolve()
        await(sutPause.promise.orTestTimeoutNamedAfter(sutPauseTimeout, {sutPause}))
    }
}








