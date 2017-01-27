package aps

import into.kommon.*
import kotlin.properties.Delegates

@Back class IntField(
    container: RequestMatumba,
    val spec: IntFieldSpec
) : FormFieldBack(container, spec.name) {
    var value by Delegates.notNull<Int>()

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        var stringValue = (input[name] ?: bitch("Gimme $name, motherfucker")) as String
        stringValue = stringValue.trim()

        run error@{
            if (stringValue == "") return@error t("TOTE", "Поле обязательно")

            try {
                value = stringValue.toInt()
            } catch(e: NumberFormatException) {
                return@error t("TOTE", "Я такие числа не понимаю")
            }

            if (value < spec.min) return@error t("TOTE", "Не менее $spec.min")
            if (value > spec.max) return@error t("TOTE", "Не более $spec.max")

            null
        }?.let {error ->
            fieldErrors.add(FieldError(name, error))
        }
    }

}


