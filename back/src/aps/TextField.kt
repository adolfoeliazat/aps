package aps

import into.kommon.*
import org.apache.commons.validator.routines.EmailValidator

@Back class TextField(
    container: RequestMatumba,
    val spec: TextFieldSpec
): FormFieldBack(container, spec.name) {

    // TODO:vgrechka Maybe use PASSWORD here as a reason to hide value from log?
//    @Dummy enum class Type { TEXT, PASSWORD }
//    @Dummy enum class Kind { INPUT, TEXTAREA }

    lateinit var value: String

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        value = (input[name] ?: bitch("Gimme $name, motherfucker")) as String
        value = value.trim()

        run error@{
            if (value.length < spec.minLen)
                return@error if (value.length == 0) t("TOTE", "Поле обязательно")
                else t("TOTE", "Не менее ${spec.minLen} символов")
            if (value.length > spec.maxLen) return@error t("TOTE", "Не более ${spec.maxLen} символов")

            when (spec.type) {
                TextFieldType.PHONE -> {
                    var digitCount = 0
                    for (c in value.toCharArray()) {
                        if (!Regex("(\\d| |-|\\+|\\(|\\))+").matches("$c")) return@error t("TOTE", "Странный телефон какой-то")
                        if (Regex("\\d").matches("$c")) ++digitCount
                    }

                    if (digitCount < spec.minDigits) return@error t("TOTE", "Не менее ${spec.minDigits} цифр")
                }
                TextFieldType.EMAIL -> {
                    if (!EmailValidator.getInstance().isValid(value)) return@error t("TOTE", "Странная почта какая-то")
                }
            }

            null
        }?.let {error ->
            return fieldErrors.add(FieldError(name, error)) /ignore
        }
    }

}


