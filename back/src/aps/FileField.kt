package aps

import aps.back.*
import into.kommon.*
import java.io.File
import java.util.*
import kotlin.properties.Delegates.notNull

@Back class FileField(
    container: RequestMatumba,
    val spec: FileFieldSpec
): FormFieldBack(container, spec.name) {
    class Value(val fileName: String, val base64: String, val valueKind: FileFieldValueKind)

    private var _value by notNullOnce<Value>()

    val value: Value get() {
        check(include){"Attempt to read back FileField $name, which is not included"}
        RequestGlobus.retrievedFields += this
        return _value
    }

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        val rawValue = (input[name] ?: bitch("Gimme $name, motherfucker")) as Map<*, *>

        var fileName by notNullOnce<String>()
        var base64 by notNullOnce<String>()
        var valueKind by notNullOnce<FileFieldValueKind>()

        run error@{
            valueKind = FileFieldValueKind.valueOf(cast(rawValue["valueKind"]))
            if (valueKind !in spec.allowedValueKinds) {
                when {
                    spec.allowedValueKinds == setOf(FileFieldValueKind.PROVIDED) -> return@error t("TOTE", "Файл обязателен")
                    else -> return@error t("TOTE", "Я ебу, какой-то файл отстойный")
                }
            }

            if (valueKind == FileFieldValueKind.PROVIDED) {
                fileName = cast(rawValue["fileName"])
                base64 = cast(rawValue["base64"])
            }

            _value = Value(fileName, base64, valueKind)
            null
        }?.let {error ->
            fieldErrors.add(FieldError(name, error))
        }
    }
}


