package aps

import into.kommon.*
import java.io.File
import java.util.*
import kotlin.properties.Delegates.notNull

@Back class FileField(
    container: RequestMatumba,
    val spec: FileFieldSpec
): FormFieldBack(container, spec.name) {
    var fileName by notNullOnce<String>()
    var base64 by notNullOnce<String>()
    var valueKind by notNullOnce<FileFieldValueKind>()

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        val value = (input[name] ?: bitch("Gimme $name, motherfucker")) as Map<*, *>

        run error@{
            valueKind = FileFieldValueKind.valueOf(cast(value["valueKind"]))
            if (valueKind !in spec.allowedValueKinds) {
                when {
                    spec.allowedValueKinds == setOf(FileFieldValueKind.PROVIDED) -> return@error t("TOTE", "Файл обязателен")
                    else -> return@error t("TOTE", "Я ебу, какой-то файл отстойный")
                }
            }

            if (valueKind == FileFieldValueKind.PROVIDED) {
                fileName = cast(value["fileName"])
                base64 = cast(value["base64"])
            }
            null
        }?.let {error ->
            fieldErrors.add(FieldError(name, error))
        }
    }
}


