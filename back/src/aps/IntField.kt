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
                _value = toInternal(stringValue.toInt())
            } catch(e: NumberFormatException) {
                return@error t("TOTE", "Я такие числа не понимаю")
            }

            if (_value < spec.min) return@error t("TOTE", "Не менее ${fromInternal(spec.min)}")
            if (_value > spec.max) return@error t("TOTE", "Не более ${fromInternal(spec.max)}")

            null
        }?.let {error ->
            fieldErrors.add(FieldError(name, error))
        }
    }

    private fun toInternal(x: Int): Int = when (spec.type) {
        is IntFieldType.Money -> {
            check(!spec.type.fractions){"758d27c0-b19d-4816-9bc6-165e3c17a081"}
            x * 100
        }
        else -> x
    }

    private fun fromInternal(x: Int): Int = when (spec.type) {
        is IntFieldType.Money -> {
            check(!spec.type.fractions){"f1ef17e2-5b1e-4ea3-868f-3160588d23e2"}
            x / 100
        }
        else -> x
    }

}


