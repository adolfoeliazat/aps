package aps

import into.kommon.*
import kotlin.properties.Delegates

@Back class IntField(
    container: RequestMatumba,
    name: String,
    @Dummy val title: String,
    val min: Int,
    val max: Int
) : FormFieldBack(container, name) {
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

            if (value < min) return@error t("TOTE", "Не менее $min")
            if (value > max) return@error t("TOTE", "Не более $max")

            null
        }?.let {error ->
            fieldErrors.add(FieldError(name, error))
        }
    }

}


