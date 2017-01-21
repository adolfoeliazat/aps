/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

import aps.back.*
import into.kommon.*
import kotlin.properties.Delegates
import kotlin.properties.Delegates.notNull
import kotlin.system.exitProcess

//fun bitch(msg: String = "Just bitching..."): Nothing = throw Exception(msg)
//fun imf(what: String = "me"): Nothing = throw Exception("Implement $what, please, fuck you")
//fun wtf(msg: String = "...WTF didn't you describe this WTF?"): Nothing = throw Exception("WTF: $msg")
//fun die(msg: String = "You've just killed me, motherfucker!"): Nothing = throw Exception("Aarrgghh... $msg")
//val dontCallMe: Nothing get() = wtf("Don't call me, motherfucker")

fun clog(vararg xs: Any?): Unit = println(xs.joinToString(" "))
fun dlog(vararg xs: Any?) = debugLog.info(xs.joinToString(" "))
fun dwarn(vararg xs: Any?) = debugLog.info(xs.joinToString(" "))

fun t(en: String, ru: String) = ru

annotation class Back

@Dummy interface Promisoid<T>
@Dummy fun <Res> callRemoteProcedure(req: Any): Promisoid<Res> = dontCallMe
@Dummy fun <Res> callRemoteProcedure(req: Any, ui: LegacyUIShit): Promisoid<Res> = dontCallMe
//@Dummy fun <T> __await(x: Promisoid<T>): T = dontCallMe
//@Dummy fun <T> __asyncResult(x: T): Promisoid<T> = dontCallMe
@Dummy interface LegacyUIShit
@Dummy fun <Res> callMatumba(req: RequestMatumba, token: String?): Promisoid<Res> = dontCallMe
@Dummy fun <Res> callDangerousMatumba(req: RequestMatumba): Promisoid<Res> = dontCallMe
@Dummy fun <Res> callZimbabwe(req: RequestMatumba, token: String?): Promisoid<ZimbabweResponse<Res>> = dontCallMe
@Dummy fun <Res> callZimbabwe(procedureName: String, req: RequestMatumba, token: String?): Promisoid<ZimbabweResponse<Res>> = dontCallMe

@Back open class RequestMatumba {
    val fields = mutableListOf<FormFieldBack>()
}

//abstract class HiddenFormFieldBack<T>(val container: RequestMatumba, val name: String) {
//    init {
//        container.hiddenFields.add(this)
//    }
//
//    abstract fun toRemote(): Any?
//}

//@Back inline fun <reified T : Any> HiddenField(container: RequestMatumba, name: String): HiddenField<T> {
//    return HiddenField(container, name, T::class)
//}



@Back class EnumHiddenField<E : Enum<E>>(
    container: RequestMatumba,
    name: String,
    val values: Array<E>,
    possiblyUnspecified: Boolean = false
) : FormFieldBack(container, name, possiblyUnspecified=possiblyUnspecified) {
    lateinit var value: E

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        val string = input[name] as String
        value = values.find{it.name == string} ?: bitch("Bad enum value: [$string]")
    }
}

fun <T> culprit(culprit: Culprit, f: () -> T): T {
    return try {
        f()
    } catch (e: Exception) {
        throw ExceptionWithCulprit(e, culprit)
    }
}

@Back class StringHiddenField(
    container: RequestMatumba,
    name: String,
    possiblyUnspecified: Boolean = false
) : FormFieldBack(container, name, possiblyUnspecified=possiblyUnspecified) {
    lateinit var value: String

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        if (!possiblyUnspecified || specified) {
            value = input[name] as String
        }
    }
}

@Back fun stringHiddenField() = eagerEx<RequestMatumba, StringHiddenField> {thisRef, property ->
    StringHiddenField(thisRef, property.name)
}

@Back class MaybeStringHiddenField(
    container: RequestMatumba,
    name: String,
    possiblyUnspecified: Boolean = false
) : FormFieldBack(container, name, possiblyUnspecified=possiblyUnspecified) {
    var loaded = false
    var _value: String? = null
    val value: String? get() = if (!loaded) bitch("I am not loaded") else _value

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        _value = input[name] as String?
        loaded = true
    }
}

@Back fun maybeStringHiddenField() = eagerEx<RequestMatumba, MaybeStringHiddenField> {thisRef, property ->
    MaybeStringHiddenField(thisRef, property.name)
}

@Back class BooleanHiddenField(
    container: RequestMatumba,
    name: String,
    possiblyUnspecified: Boolean = false
) : FormFieldBack(container, name, possiblyUnspecified=possiblyUnspecified) {
    lateinit var _value: java.lang.Boolean
    var value: Boolean
        get() = _value.booleanValue()
        @Dummy set(x) = wtf("@Back BooleanHiddenField.value.set should not be called")

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        _value = input[name] as java.lang.Boolean
    }
}

abstract class FormFieldBack(
    container: RequestMatumba,
    val name: String,
    val possiblyUnspecified: Boolean = false
) : Culprit {
    abstract fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>)

    lateinit var _specified: java.lang.Boolean
    val specified: Boolean get() = _specified.booleanValue()
    override val constructionStack = Exception().stackTrace

    init {
        container.fields.add(this)
    }

    fun load(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) =
        culprit(this, {
            if (possiblyUnspecified) {
                _specified = input["$name-specified"] as java.lang.Boolean
            }
            loadOrBitch(input, fieldErrors)
        })

    override fun toString(): String = bitch("Use field.value to get value of field [$name]")
}



fun printStack() {
    try {throw Exception("Gimme the stack")}
    catch (e: Throwable) {e.printStackTrace()}
}

fun dwarnStrikingAndExit(vararg xs: Any?) {
    dwarnStriking(*xs)
    exitProcess(0)
}
















