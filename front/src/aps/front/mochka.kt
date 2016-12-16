@file:Suppress("UnsafeCastFromDynamic")

package into.mochka

import aps.*

class runMochka(build: runMochka.() -> Unit) {
    private var currentMochaDone: ((error: Throwable?) -> Unit)? = null
    private val mochaShit = js("({})")
    private val process = js("process")

    init {
        process.on("unhandledRejection") {error: Throwable, where: Promise<*> ->
            currentMochaDone.let {
                if (it != null) {
                    it(error)
                } else {
                    console.error("\n---------- Unexpected async fuckup ----------\n")
                    console.error(error.asDynamic().stack)
                    process.exit(1)
                }
            }
        }

        val Mocha = js("require('mocha')")

        // XXX Hack to make Mocha populate our `mochaShit` with `describe`, etc.
        Mocha.prototype.loadFiles = {fn: dynamic ->
            val self: dynamic = js("this")
            val suite: dynamic = js("this").suite
            js("this").files.forEach {_file: dynamic ->
                val file: dynamic = js("require('path')").resolve(_file)
                suite.emit("pre-require", mochaShit, file, self)

                // Original: suite.emit('require', require(file), file, self)
                build()

                suite.emit("post-require", mochaShit, file, self)
            }
            if (fn) fn()
        }

        val mocha = js("new Mocha")
        mocha.addFile("dummy")

        mocha.run {failures: dynamic ->
            process.on("exit") {
                process.exit(failures)
            }
        }
    }

    fun describe(name: String, block: runMochka.() -> Unit) {
        mochaShit.describe(name) {
            afterEach {
                currentMochaDone = null
            }

            block()
        }
    }

    fun suite(name: String, block: runMochka.() -> Unit) = describe(name, block)

    fun afterEach(block: () -> Unit) {
        mochaShit.afterEach(block)
    }

    fun it(name: String, block: () -> Unit) {
        mochaShit.it(name) {block()}
    }

    fun test(name: String, block: () -> Unit) = it(name, block)

    fun ita(name: String, block: (done: () -> Unit) -> Unit) {
        mochaShit.it(name) {mochaDone->
            currentMochaDone = mochaDone
            block {mochaDone()}
        }
    }

    fun testa(name: String, block: (done: () -> Unit) -> Unit) = ita(name, block)
}

fun <T> assertEquals(expected: List<T>, actual: List<T>) {
    assert(expected.size == actual.size, "Expected size ${expected.size}, got ${actual.size}")
    expected.forEachIndexed {i, exp ->
        assert(exp == actual[i], "Item $i: expected <$exp>, got <${actual[i]}>")
    }
}

fun assertEquals(expected: Any?, actual: Any?, msg: String? = null) {
    assert(expected == actual, buildString {
        msg?.let {append(it + ". ")}
        append("Expected <$expected>, got <$actual>")
    })
}

fun fail(msg: String? = null): Nothing {
    throw AssertionError(msg ?: "Shit failed")
}

fun assert(test: Boolean, msg: String? = null) {
    if (!test) fail(msg ?: "Expected true, got false")
}

fun assertTrue(test: Boolean, msg: String? = null) {
    assert(test, msg)
}

fun assertFalse(test: Boolean, msg: String? = null) {
    assert(!test, msg)
}

fun assertFails(block: () -> Unit): Throwable = assertFails(null, block)

fun assertFails(message: String?, block: () -> Unit): Throwable {
    try {
        block()
    } catch (e: Throwable) {
        return e
    }
    val msg = message?.let {"$it. "} ?: ""
    fail(msg + "Expected an exception to be thrown, but was completed successfully.")
}

















