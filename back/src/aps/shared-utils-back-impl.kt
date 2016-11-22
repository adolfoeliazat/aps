/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

import aps.back.*
import into.kommon.*

//fun bitch(msg: String = "Just bitching..."): Nothing = throw Exception(msg)
//fun imf(what: String = "me"): Nothing = throw Exception("Implement $what, please, fuck you")
//fun wtf(msg: String = "...WTF didn't you describe this WTF?"): Nothing = throw Exception("WTF: $msg")
//fun die(msg: String = "You've just killed me, motherfucker!"): Nothing = throw Exception("Aarrgghh... $msg")
//val dontCallMe: Nothing get() = wtf("Don't call me, motherfucker")

fun dlog(vararg xs: Any?) = debugLog.info(xs.joinToString(" "))
fun dwarn(vararg xs: Any?) = debugLog.info(xs.joinToString(" "))

fun t(en: String, ru: String) = ru

annotation class Dummy
annotation class Back

@Dummy interface Promise<T>
@Dummy fun <Res> callRemoteProcedure(req: Any): Promise<Res> = dontCallMe
@Dummy fun <Res> callRemoteProcedure(req: Any, ui: LegacyUIShit): Promise<Res> = dontCallMe
@Dummy fun <T> __await(x: Promise<T>): T = dontCallMe
@Dummy fun <T> __asyncResult(x: T): Promise<T> = dontCallMe
@Dummy interface LegacyUIShit
@Dummy fun <Res> callMatumba(req: RequestMatumba, token: String?): Promise<Res> = dontCallMe
@Dummy fun <Res> callDangerousMatumba(req: RequestMatumba): Promise<Res> = dontCallMe
@Dummy fun <Res> callZimbabwe(req: RequestMatumba, token: String?): Promise<ZimbabweResponse<Res>> = dontCallMe
@Dummy fun <Res> callZimbabwe(procedureName: String, req: RequestMatumba, token: String?): Promise<ZimbabweResponse<Res>> = dontCallMe

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
        value = input[name] as String
    }
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

@Back class TextField(
    container: RequestMatumba,
    name: String,
    @Dummy val title: String,
    val type: TextFieldType,
    val minLen: Int,
    val maxLen: Int,
    val minDigits: Int = -1
): FormFieldBack(container, name) {

    // TODO:vgrechka Maybe use PASSWORD here as a reason to hide value from log?
//    @Dummy enum class Type { TEXT, PASSWORD }
//    @Dummy enum class Kind { INPUT, TEXTAREA }

    lateinit var value: String

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        value = (input[name] ?: bitch("Gimme $name, motherfucker")) as String
        value = value.trim()

        run error@{
            if (value.length < minLen)
                return@error if (value.length == 0) t("TOTE", "Поле обязательно")
                else t("TOTE", "Не менее $minLen символов")
            if (value.length > maxLen) return@error t("TOTE", "Не более $maxLen символов")

            if (type == TextFieldType.PHONE) {
                var digitCount = 0
                for (c in value.toCharArray()) {
                    if (!Regex("(\\d| |-|\\+|\\(|\\))+").matches("$c")) return@error t("TOTE", "Странный телефон какой-то")
                    if (Regex("\\d").matches("$c")) ++digitCount
                }

                if (digitCount < minDigits) return@error t("TOTE", "Не менее $minDigits цифр")
            }

            null
        }?.let {error ->
            return fieldErrors.add(FieldError(name, error)) /ignore
        }
    }

}

@Back class CheckboxField(
    container: RequestMatumba,
    name: String
) : FormFieldBack(container, name) {
    lateinit var _yes: java.lang.Boolean

    val yes: Boolean get() = _yes.booleanValue()
    val no: Boolean get() = !yes

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        _yes = input[name] as java.lang.Boolean
    }
}

@Back class SelectField<T>(
    container: RequestMatumba,
    name: String,
    val title: String,
    val values: Array<T>
)
: FormFieldBack(container, name) where T : Enum<T> {
    lateinit var value: T

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        value = values.find{it.name == input[name] as String}!!
    }
}

fun printStack() {
    try {throw Exception("Gimme the stack")}
    catch (e: Throwable) {e.printStackTrace()}
}









