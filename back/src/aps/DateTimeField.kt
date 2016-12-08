package aps

import into.kommon.*
import java.sql.Timestamp

@Back class DateTimeField(
    container: RequestMatumba,
    name: String,
    val title: String
) : FormFieldBack(container, name) {
    lateinit var value: Timestamp

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        val rawValue = input[name]

        run error@{
            rawValue ?: return@error t("TOTE", "Поле обязательно")
            value = Timestamp(rawValue as Long)
            null
        }?.let {error ->
            fieldErrors.add(FieldError(name, error))
        }
    }
}
