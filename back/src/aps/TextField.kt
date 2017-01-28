package aps

import aps.TextFieldType.*
import into.kommon.*
import org.apache.commons.validator.routines.EmailValidator
import kotlin.properties.Delegates.notNull

@Back class TextField(container: RequestMatumba, val spec: TextFieldSpec)
    : FormFieldBack(container, spec.name)
{
    var value by notNull<String>()

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        value = (input[name] ?: bitch("Gimme $name, motherfucker")) as String
        value = value.trim()

        error()?.let {
            fieldErrors.add(FieldError(name, it))
        }
    }

    private fun error(): String? {
        if (value.length < spec.minLen) return when {
            value.isEmpty() -> t("TOTE", "Поле обязательно")
            isFreeForm() -> t("TOTE", "Не менее ${spec.minLen} символов")
            else -> shittyMessageFor(spec.type)
        }

        if (value.length > spec.maxLen) return when {
            isFreeForm() -> t("TOTE", "Не более ${spec.maxLen} символов")
            else -> shittyMessageFor(spec.type)
        }

        when (spec.type) {
            PHONE -> {
                var digitCount = 0
                for (c in value.toCharArray()) {
                    if (!Regex("(\\d| |-|\\+|\\(|\\))+").matches("$c")) return shittyMessageFor(PHONE)
                    if (Regex("\\d").matches("$c")) ++digitCount
                }

                if (digitCount < spec.minDigits) return t("TOTE", "Не менее ${spec.minDigits} цифр")
            }
            EMAIL -> {
                if (!EmailValidator.getInstance().isValid(value)) return shittyMessageFor(EMAIL)
            }
            else -> {}
        }

        return null
    }


    private fun isFreeForm() = spec.type.oneOf(STRING, TEXTAREA, PASSWORD)

    private fun shittyMessageFor(type: TextFieldType): String = when (type) {
        PHONE -> t("TOTE", "Странный телефон какой-то")
        EMAIL -> t("TOTE", "Странная почта какая-то")
        else -> wtf("shittyMessageFor $type")
    }

}


