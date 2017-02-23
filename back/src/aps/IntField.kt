package aps

import aps.back.*
import into.kommon.*
import kotlin.properties.Delegates
import kotlin.properties.Delegates.notNull

@Back class IntField(container: RequestMatumba, val spec: IntFieldSpec)
    : FormFieldBack(container, spec.name)
{
    private var _value by notNull<Int>()

    val value: Int get() {
        check(include){"Attempt to read back IntField $name, which is not included"}
        RequestGlobus.retrievedFields += this
        return _value
    }

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        var stringValue = (input[name] ?: bitch("Gimme $name, motherfucker")) as String
        stringValue = stringValue.trim()

        run error@{
            if (stringValue == "") return@error t("TOTE", "Поле обязательно")

            try {
                _value = stringValue.toInt()
            } catch(e: NumberFormatException) {
                return@error t("TOTE", "Я такие числа не понимаю")
            }

            if (_value < spec.min) return@error t("TOTE", "Не менее ${spec.min}")
            if (_value > spec.max) return@error t("TOTE", "Не более ${spec.max}")

            null
        }?.let {error ->
            fieldErrors.add(FieldError(name, error))
        }
    }

}


