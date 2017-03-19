package aps

import kotlin.reflect.KProperty

annotation class Dummy

val KOMMON_HOME: String get()= getenv("INTO_KOMMON_HOME") ?: die("I want INTO_KOMMON_HOME environment variable")

fun bitch(msg: String = "Just bitching..."): Nothing = throw Exception(msg)
fun imf(what: String = "me"): Nothing = throw Exception("Implement $what, please, fuck you")
fun wtf(msg: String = "...WTF didn't you describe this WTF?"): Nothing = throw Exception("WTF: $msg")
fun die(msg: String = "You've just killed me, motherfucker!"): Nothing = throw Exception("Aarrgghh... $msg")
fun fuckOff(msg: String = "Don't call me"): Nothing = throw Exception("Fuck off... $msg")
fun unsupported(what: String = "Didn't bother to describe"): Nothing = throw Exception("Unsupported: $what")

fun <R> measuringAndPrinting(block: () -> R): R {
    val start = currentTimeMillis()
    val res = block()
    val ms = currentTimeMillis() - start
    println("COOL [${ms}ms]")
    return res
}

inline fun ifOrEmpty(test: Boolean, block: () -> String): String =
    if (test) block()
    else ""

inline fun <T> T.letu(block: (T) -> Unit): Unit = block(this)

class FieldError(val field: String, val error: String)

interface CommonResponseFields {
    var backendVersion: String
}



//------------------------- KOTLIN -------------------------


interface ReadOnlyProperty<in R, out T> {
    operator fun getValue(thisRef: R, property: KProperty<*>): T
}

interface ReadWriteProperty<in R, T> {
    operator fun getValue(thisRef: R, property: KProperty<*>): T
    operator fun setValue(thisRef: R, property: KProperty<*>, value: T)
}

fun <T: Any> notNull(): ReadWriteProperty<Any?, T> = NotNullVar()

private class NotNullVar<T: Any>() : ReadWriteProperty<Any?, T> {
    private var value: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: throw IllegalStateException("Property ${property.name} should be initialized before get.")
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}















