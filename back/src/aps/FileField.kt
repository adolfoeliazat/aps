package aps

import aps.back.*
import java.io.File
import java.util.*
import kotlin.properties.Delegates.notNull

@Back class FileField(
    container: RequestMatumba,
    val spec: FileFieldSpec
): FormFieldBack(container, spec.name) {
    sealed class Value {
        class None: Value()
        class Unchanged: Value()
        class Provided(val fileName: String, val base64: String): Value()
    }

    private var _value by notNullOnce<Value>()

    val value: Value get() {
        check(include){"Attempt to read back FileField $name, which is not included"}
        RequestGlobus.retrievedFields += this
        return _value
    }

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        val rawValue = (input[name] ?: bitch("Gimme $name, motherfucker")) as Map<*, *>

        run error@{
            val valueKind = FileFieldValueKind.valueOf(cast(rawValue["valueKind"]))
            if (valueKind !in spec.allowedValueKinds) {
                when {
                    spec.allowedValueKinds == setOf(FileFieldValueKind.PROVIDED) -> return@error t("TOTE", "Файл обязателен")
                    else -> return@error t("TOTE", "Я ебу, какой-то файл отстойный")
                }
            }

            _value = when (valueKind) {
                FileFieldValueKind.NONE -> Value.None()
                FileFieldValueKind.UNCHANGED -> Value.Unchanged()
                FileFieldValueKind.PROVIDED -> Value.Provided(
                    fileName = cast(rawValue["fileName"]),
                    base64 = cast(rawValue["base64"])
                )
            }

            null
        }?.let {error ->
            fieldErrors.add(FieldError(name, error))
        }
    }
}


