package aps

import aps.back.*
import into.kommon.*
import java.sql.Timestamp

@Back class DateTimeField(
    container: RequestMatumba,
    name: String,
    val title: String
) : FormFieldBack(container, name) {
    private lateinit var _value: Timestamp

    val value: Timestamp get() {
        check(include){"Attempt to read back DateTimeField $name, which is not included"}
        RequestGlobus.retrievedFields += this
        return _value
    }

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        val rawValue = input[name]

        run error@{
            rawValue ?: return@error t("TOTE", "Поле обязательно")
            _value = Timestamp(rawValue as Long)
            null
        }?.let {error ->
            fieldErrors.add(FieldError(name, error))
        }
    }
}
