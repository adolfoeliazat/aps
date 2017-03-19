package aps

import aps.TextFieldType.*
import aps.back.*
import org.apache.commons.validator.routines.EmailValidator
import kotlin.properties.Delegates.notNull

@Back class TextField(
    container: RequestMatumba,
    val spec: TextFieldSpec,
    include : Boolean = true
)
    : FormFieldBack(container, spec.name, include = include)
{
    private var _value by notNull<String>()

    val value: String get() {
        check(include){"Attempt to read back TextField $name, which is not included"}
        backPlatform.requestGlobus.retrievedFields += this
        return _value
    }

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        _value = (input[name] ?: bitch("Gimme $name, motherfucker")) as String
        _value = _value.trim()

        error()?.let {
            fieldErrors.add(FieldError(name, it))
        }
    }

    private fun error(): String? {
        if (_value.length < spec.minLen) return when {
            _value.isEmpty() -> t("TOTE", "Поле обязательно")
            else -> t("TOTE", "Не менее ${spec.minLen} символов")
        }

        if (_value.length > spec.maxLen) return t("TOTE", "Не более ${spec.maxLen} символов")

        when (spec.type) {
            PHONE -> {
                var digitCount = 0
                for (c in _value.toCharArray()) {
                    if (!Regex("(\\d| |-|\\+|\\(|\\))+").matches("$c")) return shittyMessageFor(PHONE)
                    if (Regex("\\d").matches("$c")) ++digitCount
                }

                if (digitCount < spec.minDigits) return t("TOTE", "Не менее ${spec.minDigits} цифр")
            }
            EMAIL -> {
                if (!EmailValidator.getInstance().isValid(_value)) return shittyMessageFor(EMAIL)
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




