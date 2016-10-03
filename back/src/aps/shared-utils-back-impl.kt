/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

import aps.back.debugLog
import aps.back.striking

fun bitch(msg: String = "Just bitching..."): Nothing = throw Exception(msg)
fun imf(what: String = "me"): Nothing = throw Exception("Implement $what, please, fuck you")
fun wtf(msg: String = "...WTF didn't you describe this WTF?"): Nothing = throw Exception("WTF: $msg")
fun die(msg: String = "You've just killed me, motherfucker!"): Nothing = throw Exception("Aarrgghh... $msg")
val dontCallMe: Nothing get() = wtf("Don't call me, motherfucker")

fun dlog(vararg xs: Any?) {
    debugLog.info(xs.joinToString(" "))
}

fun t(en: String, ru: String) = ru

annotation class Dummy
annotation class Back

@Dummy interface Promise<T>
@Dummy fun <Res> callRemoteProcedure(req: Any): Promise<Res> = dontCallMe
@Dummy fun <Res> callRemoteProcedure(req: Any, ui: LegacyUIShit): Promise<Res> = dontCallMe
@Dummy fun <T> __await(x: Promise<T>): T = dontCallMe
@Dummy fun <T> __asyncResult(x: T): Promise<T> = dontCallMe
@Dummy interface LegacyUIShit


@Back open class RequestMatumba {
    val fields = mutableListOf<FormFieldBack>()
}

abstract class FormFieldBack(container: RequestMatumba, val name: String) {
    abstract fun load(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>)

    init {
        container.fields.add(this)
    }
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

    override fun load(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
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

@Back class CheckboxField(container: RequestMatumba, name: String) : FormFieldBack(container, name) {
    override fun load(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        _yes = input[name] as java.lang.Boolean
    }

    lateinit var _yes: java.lang.Boolean

    val yes: Boolean get() = _yes.booleanValue()
    val no: Boolean get() = !yes
}










